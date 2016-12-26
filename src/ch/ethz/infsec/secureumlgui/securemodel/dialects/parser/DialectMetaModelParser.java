package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofPackage;
import javax.jmi.reflect.RefClass;
import javax.jmi.reflect.RefPackage;

import org.apache.log4j.Logger;
import org.argouml.model.Model;
import org.netbeans.api.mdr.MDRManager;
import org.netbeans.api.mdr.MDRepository;
import org.netbeans.api.xmi.XMIReader;
import org.netbeans.api.xmi.XMIReaderFactory;
import org.netbeans.lib.jmi.mapping.FileStreamFactory;
import org.netbeans.lib.jmi.mapping.JMIMapperImpl;

import ch.ethz.infsec.secureumlgui.DialectMetamodelSelectedListener;
import ch.ethz.infsec.secureumlgui.SecureUmlModule;
import ch.ethz.infsec.secureumlgui.TabSecureUml;

//import ch.ethz.infsec.secureumlgui.gui.SecureUmlComponentManager;
import ch.ethz.infsec.secureumlgui.gui.SecureUmlPermissionComponent;
import ch.ethz.infsec.secureumlgui.gui.SecureUmlRoleComponent;
import ch.ethz.infsec.secureumlgui.gui.SecureUmlComponent;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants;

import ch.ethz.infsec.secureumlgui.modelmanagement.ModelConst;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectHelper;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectModelMapper;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.DialectMetaModelInfo;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;
import ch.ethz.infsec.secureumlgui.securemodelimpl.SecureModelFactory;

/**
 * loads metamodel from .xmi file and calls {@link
 * DialectMetaModelAnalyzer}.  The metamodel is assumed to have the
 * following structure:
 *
 * <pre>
 * ...
 * (MOF) Model SecureModel
 *                |
 *                `--Package SecureUML                     (containing the SecureUML elements, like Role, Permission, Action)
 *                |
 *                `--Package &lt;DesignLanguage&gt;        (containing the design modeling elements)
 *                |
 *                `--Package &lt;DesignLanguage&gt;Dialect (containing the action types and their associations to resources)
 * ...
 * </pre>
 *
 * Note that there can be arbitrary many other models in the .xmi
 * file, as long as one is called "SecureModel" and containts the
 * SecureUML dialect.  The content of the .xmi file will be read into
 * an extent also called (confusingly) "SecureModel". Note that this
 * is not directly the metamodel that will be instantiated. The
 * metamodel that will be instantiated is the package called
 * "SecureModel" inside this extent. The extent for this instance will
 * be called "mySecureModel".
 *
 *
 */
public class DialectMetaModelParser
    implements DialectMetamodelSelectedListener
{



    MultiContextLogger logger = MultiContextLogger.getDefault();

    private static Logger aLog = Logger.getLogger(DialectMetaModelParser.class);

    // /** the file name of the .xmi file containing the dialect metamodel */
    // private String dialectMetamodelFilename;

    // /** The dialect instance */
    // private RefPackage mySecureModelPackage = null;

    // /** The SecureModel extent */
    // private ModelPackage secureModelExtent = null;

    // /** The dialect metamodel package (SecureModel) inside the SecureModel extent */
    // private MofPackage secureModelPackage = null;

    public void dialectMetamodelSelected(File xmiFile)
    {
        //String dialectMetamodelFilename = xmiFile.getPath();


        ModelPackage secureModelExtent    = createMetamodelExtent();
        MofPackage secureModelPackage   = readMetamodel(secureModelExtent, xmiFile);
        RefPackage mySecureModelPackage = createDialectInstanceExtent(secureModelPackage);

        DialectMetaModelUtil.generateSrcFiles(mySecureModelPackage);

        DialectMetaModelAnalyzer analyzer =
            new DialectMetaModelAnalyzer(secureModelPackage);

        DialectMetaModelInfo mmInfo = analyzer.analyzeDialect( secureModelPackage);
        mmInfo.setDialectExtent(mySecureModelPackage);
        mmInfo.setDialectMetaExtent(secureModelPackage);



        DialectMetaModelUtil.printMetamodel(mmInfo);

        GenericDialectHelper.getInstance().setDialectMetaModelInfo(mmInfo);

        registerSuComponents(mmInfo);

        GenericDialectModelMapper modelMapper =
            new GenericDialectModelMapper(mmInfo);



//      System.out.println("try to load Policy class");
//      try {
//		Class.forName("ch.ethz.infsec.secureumlgui.securemodel.secureuml.Policy");
//	} catch (ClassNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
    }

    /**
     * @param mmInfo
     */
    private void registerSuComponents(DialectMetaModelInfo mmInfo)
    {
        TabSecureUml tab = SecureUmlModule.getTab();

        SecureUmlRoleComponent suRoleComponent =
            new SecureUmlRoleComponent();

        tab.registerSecureUmlComponent(
            SecureUmlConstants.getRoleResourceTypeDummy(),
            suRoleComponent);

        SecureUmlPermissionComponent suPermissionComponent =
            new SecureUmlPermissionComponent();

        tab.registerSecureUmlComponent(
            SecureUmlConstants.getPermissionResourceTypeDummy(),
            suPermissionComponent);

        SecureUmlComponent suResourceComponent = new SecureUmlComponent();

        for (Iterator iter = mmInfo.getResourceTypes().iterator(); iter.hasNext();)
        {
            ResourceType rt = (ResourceType) iter.next();

            tab.registerSecureUmlComponent(rt,suResourceComponent);
        }
    }

    /** creates dialect metamodel extent. Deletes any existing old
     * dialect metamodel extent. Initializes {@link
     * #secureModelExtent}.
     */
    private ModelPackage createMetamodelExtent() {
        aLog.debug("createMetamodelExtent");
        MDRepository repository =  MDRManager.getDefault().getDefaultRepository();
        try {
            Object oldExtent = repository.getExtent(ModelConst.SECUREMODEL_EXTENT_NAME);
            if(oldExtent != null) {
                repository.getExtent(ModelConst.SECUREMODEL_EXTENT_NAME).refDelete();
            }
            return (ModelPackage) repository.createExtent(ModelConst.SECUREMODEL_EXTENT_NAME);
        } catch (Exception e) {
            logger.error("error while creating dialect metamodel extent: "+e.getMessage());
            return null;
        }
    }

    /** reads the dialect metamodel .xmi file into {@link
     * #secureModelExtent}, and returns the contained SecureModel
     * package.
     */
    private MofPackage readMetamodel(ModelPackage extent, File file) {
        aLog.debug("readMetamodel " + extent + " from file " + file.getAbsolutePath());
        MDRepository repository =  MDRManager.getDefault().getDefaultRepository();
        aLog.debug("here?");
        XMIReader reader = XMIReaderFactory.getDefault().createXMIReader();
        aLog.debug("here? b");

        try {
            FileInputStream inputstream = new FileInputStream(file);

            Collection c = reader.read(inputstream, file.getPath(), extent);

            aLog.debug("here? c");


            for (Object item : c )  {
                if(item instanceof javax.jmi.model.MofPackage) {
                    javax.jmi.model.MofPackage mofPackage = (javax.jmi.model.MofPackage) item;
                    aLog.debug("found: " + mofPackage.getName());
                    if (mofPackage.getName().equals(ModelConst.SECUREMODEL_PACKAGE_NAME)) {
                        return (javax.jmi.model.MofPackage) item;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("error while reading metamodel file: "+e.getMessage());
        }
        return null;
    }

    /** creates the dialect instance extent. Initializes {@link #mySecureModelPackage}*/
    private RefPackage createDialectInstanceExtent(MofPackage secureModelPackage) {
        aLog.debug("createDialectInstanceExtent for MofPackage " + secureModelPackage.getName() + " (" + secureModelPackage + ")");
        MDRepository repository =  MDRManager.getDefault().getDefaultRepository();

        RefPackage pack = repository.getExtent(ModelConst.SECUREMODEL_INSTANCE_NAME);
        if(pack == null) {
            try {
                pack = repository.createExtent(ModelConst.SECUREMODEL_INSTANCE_NAME,
                                               secureModelPackage);
            } catch(Exception e) {
                logger.error("error while creating dialect instance extent");
            }
        }
        return pack;
    }

}

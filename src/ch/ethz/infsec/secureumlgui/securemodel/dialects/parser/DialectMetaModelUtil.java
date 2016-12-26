/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jmi.reflect.RefAssociation;
import javax.jmi.reflect.RefClass;
import javax.jmi.reflect.RefPackage;

import org.apache.log4j.Logger;
import org.netbeans.lib.jmi.mapping.FileStreamFactory;
import org.netbeans.lib.jmi.mapping.JMIMapperImpl;

import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ActionResourceAssociation;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ActionType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.CompositeActionType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.DialectMetaModelInfo;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.InterResourceAssociation;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelClass;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

/**
 *
 */
public class DialectMetaModelUtil
{
    static MultiContextLogger logger = MultiContextLogger.getDefault();

    private static Logger aLog = Logger.getLogger(DialectMetaModelUtil.class);


    public static String getUri(String filename)
    {
        File xmiFile = new File(filename);
        if(xmiFile.exists())
        {
            logger.info("XMI Metamodel found: " + filename);
        }
        else
        {
            logger.error("XMI Metamodel NOT found: " + filename);
            return null;
        }

        return xmiFile.toURI().toString();
    }

    public static void generateSrcFiles(RefPackage mySecureModelPackage) {
        File destDir = new File("generated");
        destDir.mkdirs();
        aLog.debug("generate mySecureModelPackage sources to: " + destDir.getAbsolutePath());


        JMIMapperImpl mapperImpl = new JMIMapperImpl();
        FileStreamFactory fileStreamFactory = new FileStreamFactory(destDir);

        Collection<RefPackage> packages = (Collection<RefPackage>) mySecureModelPackage.refAllPackages();
        try {
            for (RefPackage packa : packages ) {
                aLog.debug("Generate package: " + packa);
                mapperImpl.generate(fileStreamFactory, packa.refMetaObject());
                for ( RefClass cl : (Collection<RefClass>) packa.refAllClasses() ) {
                    aLog.debug("Generate class: " + cl);
                    mapperImpl.generate(fileStreamFactory, cl.refMetaObject());
                }
                for ( RefAssociation ass : (Collection<RefAssociation>) packa.refAllAssociations()) {
                    aLog.debug("Generate association: " + ass);
                    mapperImpl.generate(fileStreamFactory, ass.refMetaObject());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            aLog.error(e);
        }
    }



    public static DialectMetaModelInfo printMetamodel(DialectMetaModelInfo metaModelInfo)
    {
//    	for ( Object cont : metaModelInfo.getDialectMetaExtent().getContents() ) {
//    		if ( cont instanceof javax.jmi.model.Tag ) {
//    			javax.jmi.model.Tag cont2 = (javax.jmi.model.Tag ) cont;
//    			if ( cont2.getName().equals("umlClassName")) {
//    				List cont3 = cont2.getQualifiedName();
//    				for ( Object cont4 : cont3) {
//    					System.out.println(cont4.getClass() + "." + cont4.toString());
//    				}
//    			}
//    		}
//    	}
//
//    	for ( MetaModelClass mmc : metaModelInfo.getMetaModelClasses() ) {
//
//    		System.out.println("MetaModelClass: " + mmc.getName() + ", " + mmc.getUmlClassName());
//
//    	}




//        DialectMetaModelParser parser =
//            new DialectMetaModelParser(metamodelFilename);
//
//        ModelPackage modelPackage;
//
//
//        metamodelTopPackage = parser.loadMetamodel();
//
//
//
//        //logger.info("Package type: " + topPkg.toString());
//
//        //mmTester.traverseMofMetamodel();
//
//        //mmTester.printDialectStuff();
//
//        metaModelInfo = parser.analyzeDialect(metamodelTopPackage);
//         logger.info("## Protected Resource Types ##");
//         printResourcesTypes(metaModelInfo.getResourceTypes(), metaModelInfo);
//         logger.info("## Atomic Action Types ##");
//         printActionsTypes(metaModelInfo.getAtomicActionTypes());
//         logger.info("## Composite Action Types ##");
//         printActionsTypes(metaModelInfo.getCompositeActionTypes());
//         logger.info("## other Metamodel Classes ##");
//         printMappableMetaModelClasses(metaModelInfo.getMetaModelClasses());
//         logger.info("## Resource-Action Associations##");
//         printResourceActionAssociations(metaModelInfo);
//         logger.info("## Inter Resource Associations ##");
//         printInterResourceAssociations(metaModelInfo);



        return metaModelInfo;
        // produces ClassCastException within org.netbeans.mdr.NBMDRepositoryImpl.createExtent
//        try
//        {
//            RepositoryManager.getRepository().createModel("test", metamodelTopPackage);
//        }
//        catch (Exception e)
//        {
//            logger.logException(e);
//        }
//

    }


    public static void printResourcesTypes(Collection<ResourceType> resourceTypes, DialectMetaModelInfo mmInfo)
    {

        for (Iterator iter = resourceTypes.iterator(); iter.hasNext();)
        {
            ResourceType resourceType = (ResourceType) iter.next();

            String resourceString = resourceType.toString();

            logger.info("Resource: " + resourceString);

            printActionsTypes(mmInfo.getActionTypesOfResourceType(resourceType), "  ");
        }
    }

    public static void printMetaModelClasses(Collection<MetaModelClass> metaModelClasses)
    {

        for (Iterator iter = metaModelClasses.iterator(); iter.hasNext();)
        {
            MetaModelClass metaModelClass = (MetaModelClass) iter.next();

            String classString = metaModelClass.toString();

            logger.info("MetaModelClass: " + classString);

            //printActionsTypes(mmInfo.getActionTypesOfResourceType(metaModelClass), "  ");
        }
    }

    public static void printMappableMetaModelClasses(Collection<MetaModelClass> metaModelClasses)
    {

        for (Iterator iter = metaModelClasses.iterator(); iter.hasNext();)
        {
            MetaModelClass metaModelClass = (MetaModelClass) iter.next();

            String classString = metaModelClass.toString();

            if(metaModelClass.getUmlClassName() != null)
                logger.info("MetaModelClass: " + classString);

            //printActionsTypes(mmInfo.getActionTypesOfResourceType(metaModelClass), "  ");
        }
    }

    public static void printActionsTypes(Collection actionTypes)
    {
        printActionsTypes(actionTypes, "");
    }
    public static void printActionsTypes(Collection actionTypes, String prefix)
    {
        if(actionTypes != null)
            for (Iterator iter = actionTypes.iterator(); iter.hasNext();)
            {
                try
                {
                    ActionType actionType = (ActionType) iter.next();

                    String loggerString = prefix + "Action: " + actionType.getName();

                    if (actionType instanceof CompositeActionType)
                    {
                        CompositeActionType compositeActionType = (CompositeActionType) actionType;

                        loggerString +=
                            " - SubactionsDefinition: "
                            + compositeActionType.getSubactionsDefinition();
                    }

                    logger.info(loggerString);
                }
                catch (Exception e)
                {
                    logger.logException(e);
                }

            }
    }

    public static void printResourceActionAssociations(DialectMetaModelInfo mmInfo)
    {
        for (Iterator iter = mmInfo.getResourceTypes().iterator(); iter.hasNext();)
        {
            ResourceType resourceType = (ResourceType) iter.next();

            for (Iterator iterator = mmInfo.getResourceActionAssociations(resourceType).iterator(); iterator.hasNext();)
            {
                ActionResourceAssociation raDependency = (ActionResourceAssociation) iterator.next();

                logger.info(raDependency.getResourceType().getName()
                            + " ----"
                            + raDependency.getShortname()
                            + "----> "
                            + raDependency.getActionType().getName());

            }

        }
    }

    public static void printInterResourceAssociations(DialectMetaModelInfo mmInfo)
    {
        for (Iterator iter = mmInfo.getInterResourceAssociations().iterator(); iter.hasNext();)
        {
            InterResourceAssociation interResourceAssociation = (InterResourceAssociation) iter.next();

            logger.info(interResourceAssociation.toString());

        }

    }
}

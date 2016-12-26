package ch.ethz.infsec.secureumlgui.modelmanagement;

import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants;
import ch.ethz.infsec.secureumlgui.modelmanagement.OclModelInfo.MetaModelName;
import ch.ethz.infsec.secureumlgui.securemodel.SecureModelPackage;

public class ModelConst {

    // note: xmi files must be given as absolute path vs. ocl files must be
    // given as URI
    private static final String BASE = System.getProperty("user.dir");

    private static final String URI_BASE = "file:" + BASE;

    public static final OclModelInfo seccompuml;

    public static final ExtentInfo secureuml_componentuml_extent;

    static {
        // metamodel extents
        secureuml_componentuml_extent = new ExtentInfo(
            "securityModel"/* name */, "SecureModel"/* packagename */,
            "su2holocl_metamodel"/* metamodel */, SecureModelPackage.class/* type */);

        // secureuml/componentuml metamodel
        seccompuml = new OclModelInfo(URI_BASE
                                      + "/metamodels/securecomponentuml/securecomponentuml_mof.xmi",
                                      BASE + "/metamodels/securecomponentuml/securecomponentuml.ocl",
                                      "securecomponentuml", MetaModelName.MOF14);
    }

    public static final String SECUREUML_TYPES_PACKAGE = "UML_OCL";

    public static final String ENVIRONMENTPACKAGE_NAME = "AuthorizationEnvironment";

    // additional
    public static final String SECUREUML_PACKAGE_NAME = "SecureUML";

    public static final String SECUREMODEL_PACKAGE_NAME = "SecureModel";
    public static final String SECUREMODEL_EXTENT_NAME   = "SecureModel";
    public static final String SECUREMODEL_INSTANCE_NAME = "mySecureModel";
    public static final String DIALECT_PACKAGE_NAME_PART = "dialect";
    public static final String DIALECT_PACKAGE_SUFFIX = "Dialect";

    public static final String SECUREUML_RESOURCE_NAME = "Resource";
    public static final String SECUREUML_ACTION_NAME = "ActionClass";
    public static final String SECUREUML_ATOMIC_ACTION_NAME = "AtomicAction";
    public static final String SECUREUML_COMPOSITE_ACTION_NAME = "CompositeAction";

    @Deprecated
    public static final String STEREOTYPE_SECUML_ACTIONTYPE = SecureUmlConstants.STEREOTYPE_SECUML_ACTIONTYPE; //"secuml.actiontype";

    @Deprecated
    public static final String STEREOTYPE_SECUML_ROLE = SecureUmlConstants.STEREOTYPE_SECUML_ROLE;

    @Deprecated
    public static final String STEREOTYPE_SECUML_PERMISSION = SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION;




    public static String getDialectPackageName(String dialectName)
    {
        return dialectName + DIALECT_PACKAGE_SUFFIX;
    }

    public static final String ACTION_RESOURCE_ASSOCIATION_NAME = "actionResource";
}

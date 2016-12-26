/**
 *
 */
package ch.ethz.infsec.secureumlgui.main;

import org.omg.uml.foundation.core.Classifier;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;

/**
 *
 *  SecureUml-related Constants used by several Classes
 */
public class SecureUmlConstants
{
//    public static final String STEREOTYPE_COMPUML_ENTITY = "compuml.entity";

    public static final String BASE_CLASS = "Class";
    public static final String BASE_ASSOCCLASS = "AssociationClass";


    public static enum SECUML_STEREOTYPES {
        STEREOTYPE_SECUML_USER 			( "secuml.user", BASE_CLASS ),
        STEREOTYPE_SECUML_ROLE			( "secuml.role", BASE_CLASS),
        STEREOTYPE_SECUML_RESOURCE 		( "secuml.resource", null ),
        STEREOTYPE_SECUML_ACTIONTYPE 	( "secuml.actiontype", null ),
        STEREOTYPE_SECUML_PERMISSION 	( "secuml.permission", BASE_ASSOCCLASS ),
        STEREOTYPE_SECUML_CONSTRAINT 	( "secuml.constraint", null ),
        STEREOTYPE_SECUML_POLICY 		( "secuml.policy", BASE_CLASS );

        private String value;
        private String base;

        SECUML_STEREOTYPES ( String value, String baseClass) {
            this.value = value;
            this.base = baseClass;
        }

        public String toString() {
            return value;
        }

        public String getBase() {
            return base;
        }
    }


    public static enum UML_OCL {
        OCL_ANY 			("OclAny", 			new UML_OCL[] {}),
//    	OCL_TYPE 			("OclType", 		new UML_OCL[] {OCL_ANY}),
//    	OCL_STATE 			("OclState", 		new UML_OCL[] {OCL_ANY}),
//    	OCL_MODELELEMENT	("OclModelElement", new UML_OCL[] {OCL_ANY}),
        OCL_BOOLEAN			("Boolean", 		new UML_OCL[] {OCL_ANY}),
        OCL_REAL			("Real", 			new UML_OCL[] {OCL_ANY}),
        OCL_STRING 			("String", 			new UML_OCL[] {OCL_ANY}),
        OCL_INTEGER			("Integer", 		new UML_OCL[] {OCL_REAL}),
//    	OCL_VOID			("OclVoid", 		new UML_OCL[] {OCL_TYPE, OCL_STATE, OCL_MODELELEMENT,
//				OCL_BOOLEAN, OCL_INTEGER, OCL_STRING});
        OCL_VOID			("OclVoid", 		new UML_OCL[] {OCL_BOOLEAN, OCL_INTEGER, OCL_STRING});
        private String value;
        private UML_OCL superTypes[];

        UML_OCL (String value, UML_OCL superTypes[]) {
            this.value = value;
            this.superTypes = superTypes;
        }

        public String toString() {
            return this.value;
        }

        public UML_OCL[] getSuperTypes() {
            return this.superTypes;
        }
    }


    public static final String STEREOTYPE_SECUML_PERMISSION = SECUML_STEREOTYPES.STEREOTYPE_SECUML_PERMISSION.toString();

    //public static final String STEREOTYPE_SECUML_EXCP_PERMISSION = "secuml.excppermission";

    public static final String STEREOTYPE_SECUML_POLICY = SECUML_STEREOTYPES.STEREOTYPE_SECUML_POLICY.toString();

    public static final String STEREOTYPE_SECUML_ROLE = SECUML_STEREOTYPES.STEREOTYPE_SECUML_ROLE.toString();

    public static final String STEREOTYPE_SECUML_ACTIONTYPE = SECUML_STEREOTYPES.STEREOTYPE_SECUML_ACTIONTYPE.toString();

//    public static final String STEREOTYPE_ENTITY_ATTRIBUTE_ACTION = "dialect.entityattributeaction";
//
//    public static final String STEREOTYPE_ENTITY_ACTION = "dialect.entityaction";
//
//    public static final String STEREOTYPE_ENTITY_OPERATION_ACTION = "dialect.entityoperationaction";

    public static final String STEREOTYPE_SECUML_CONSTRAINT = SECUML_STEREOTYPES.STEREOTYPE_SECUML_CONSTRAINT.toString();

    public static final String STEREOTYPE_SECUML_RESOURCE = SECUML_STEREOTYPES.STEREOTYPE_SECUML_RESOURCE.toString();

    public static final String STEREOTYPE_SECUML_USER = SECUML_STEREOTYPES.STEREOTYPE_SECUML_USER.toString();

    public static final String STEREOTYPE_OCL_TYPE = "ocltype";



    public static final String TAG_DEFINITION_AUTHORIZATION_CONSTRAINT = "authorizationConstraint";

    private static ResourceType permissionResourceTypeDummy;
    //private static ResourceType excpPermissionResourceTypeDummy;
    private static ResourceType roleResourceTypeDummy;
    //private static ResourceType excpLevelResourceTypeDummy;
    private static ResourceType policyResourceTypeDummy;

    public static final String NEW_PERMISSION_SUFFIX = "Perm";

    //public static final String AuthorizationConstraintTagName = "AuthorizationConstraint";



    public static final String UML_CLASS = "UmlClass";
    public static final String UML_ASSOCIATION = "AssociationClass";

    public static final String ROLE_NAME = "Role";
    public static final String ROLE_CLASSNAME = UML_CLASS;

    public static final String POLICY_NAME = "Policy";
    public static final String POLICY_CLASSNAME = UML_CLASS;
    public static final String DEFAULT_POLICY_NAME = "DefaultPolicy";

    public static final String POLICY_INHERITANCE_REFINEDBY = "refinedBy";
    public static final String POLICY_INHERITANCE_REFINES = "refines";

    public static final String PERMISSION_NAME = "Permission";
    public static final String PERMISSION_CLASSNAME = UML_ASSOCIATION;


    public static final String PACKAGE_SECUML = "secUML";
    public static final String PACKAGE_PERMISSIONS = "permissions";
    public static final String PACKAGE_OCL ="UML_OCL";





    /**
     * @return the permissionResourceTypeDummy
     */
    public static ResourceType getPermissionResourceTypeDummy()
    {
        if(permissionResourceTypeDummy == null)
        {
            permissionResourceTypeDummy = new ResourceType();

            permissionResourceTypeDummy.setName(PERMISSION_NAME);
            permissionResourceTypeDummy.setUmlClassName(PERMISSION_CLASSNAME);
            permissionResourceTypeDummy.setModelElementStereotype(
                SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION);
        }
        return permissionResourceTypeDummy;
    }



    public static ResourceType getPolicyResourceTypeDummy() {
        if ( policyResourceTypeDummy == null ) {

            policyResourceTypeDummy = new ResourceType();

            policyResourceTypeDummy.setName(POLICY_NAME);
            policyResourceTypeDummy.setUmlClassName(POLICY_CLASSNAME);
            policyResourceTypeDummy.setModelElementStereotype(STEREOTYPE_SECUML_POLICY);
        }
        return policyResourceTypeDummy;
    }




    public static ResourceType getRoleResourceTypeDummy()
    {
        if(roleResourceTypeDummy == null)
        {
            roleResourceTypeDummy = new ResourceType();

            roleResourceTypeDummy.setName(ROLE_NAME);
            roleResourceTypeDummy.setUmlClassName(ROLE_CLASSNAME);
            roleResourceTypeDummy.setModelElementStereotype(
                SecureUmlConstants.STEREOTYPE_SECUML_ROLE);
            //roleResourceTypeDummy.setAnchorPath("self");
        }
        return roleResourceTypeDummy;
    }
}


package ch.ethz.infsec.secureumlgui.securemodelimpl;

import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.modelmanagement.ModelConst;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectHelper;

import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ActionType;

import javax.jmi.reflect.RefObject;
import javax.jmi.reflect.RefPackage;

import org.apache.log4j.Logger;

import ch.ethz.infsec.secureumlgui.securemodel.secureuml.*;
import ch.ethz.infsec.secureumlgui.securemodel.*;

/**
 * Factory class to create Instances of the SecureUML and its dialects metamodel's elements.
 *
 * works untyped to workaround the MDR Classloader issue
 * (Interfaces cannot be found by MDR when
 *  running from inside ArgoUML Module)
 *
 *
 */
public class SecureModelFactory
{
    private SecureModelFactory()
    {
        // TODO NEW take from DA
        //secModel = new SecureModelPackageImpl();

        //secureUmlPackage = secModel.getSecureUml();
        //componentUmlPackage = secModel.getComponentUml();
    }

    MultiContextLogger logger = MultiContextLogger.getDefault();

    private static Logger aLog = Logger.getLogger(SecureModelFactory.class);

    protected Object secureModelPackage = null;
    public Object getSecureModel()
    {
        if(secureModelPackage == null)
        {
            secureModelPackage =
                GenericDialectHelper.getInstance().
                getDialectMetaModelInfo().getDialectExtent();

            if(secureModelPackage == null)
                logger.error("secureModelPackage = null");

            secureUmlPackage = Util.getProperty(
                                   secureModelPackage, "SecureUml");
            //secureUmlPackage = ((SecureModelPackage) secureModelPackage).getSecureUml();
        }
        return secureModelPackage;
    }

    public void setSecureModel(Object secModel)
    {
        this.secureModelPackage = secModel;
    }

    protected Object secureUmlPackage = null;
    public Object getSecureUmlPackage()
    {
        if(secureUmlPackage == null)
        {
            secureUmlPackage =
                Util.getProperty(getSecureModel(), "secureUml");
        }
        return secureUmlPackage;
    }
//    public void setSecureUmlPackage(SecureUmlPackage secureUmlPackage)
//    {
//        this.secureUmlPackage = secureUmlPackage;
//    }


    protected Object dialectPackage = null;
    public Object getDialectPackage()
    {
        if(dialectPackage == null)
        {
            String dialectPackageName =
                GenericDialectHelper.getInstance().
                getDialectMetaModelInfo().getDialectName();

            dialectPackage =
                Util.getProperty(getSecureModel(), dialectPackageName);
        }
        return dialectPackage;
    }

    protected Object dialectDialectPackage = null;
    public Object getDialectDialectPackage()
    {
        if(dialectDialectPackage == null)
        {
            String dialectDialectPackageName =
                GenericDialectHelper.getInstance().
                getDialectMetaModelInfo().getDialectName()
                + ModelConst.DIALECT_PACKAGE_SUFFIX;

            dialectDialectPackage =
                Util.getProperty(getSecureModel(),
                                 dialectDialectPackageName);
        }
        return dialectDialectPackage;

    }


////  public void setComponentUmlPackage(ComponentUmlPackage componentUmlPackage)
////  {
////      this.componentUmlPackage = componentUmlPackage;
////  }


//    protected Object componentUmlPackage = null;
//    public Object getComponentUmlPackage()
//    {
//        return componentUmlPackage;
//    }
////    public void setComponentUmlPackage(ComponentUmlPackage componentUmlPackage)
////    {
////        this.componentUmlPackage = componentUmlPackage;
////    }


    public static SecureModelFactory getInstance()
    {
        return new SecureModelFactory();
    }


    public Object createResource(String resourceTypeName, String name)
    {
        Object resourceTypeClass =
            Util.getProperty(getDialectPackage(), resourceTypeName);

        Object resource =
            Util.invokeParameterlessMethod(resourceTypeClass,
                                           "create"+Util.capitalize(resourceTypeName));

        return resource;
    }

    // ComponentUML Dialect specific
//    public Entity createEntity(String name)
//    {
//
//        return new EntityImpl(name);
//        //return componentUmlPackage.getEntity().createEntity(name);
//    }
//
//    public Attribute createAttribute(String name)
//    {
//        return new AttributeImpl(name);
//        //return componentUmlPackage.getAttribute().createAttribute(name);
//    }
//
//    public Method createMethod(String name)
//    {
//        return new MethodImpl(name);
//        //return componentUmlPackage.getMethod().createMethod(name);
//    }


    /** create an unnamed permission */
    public /*Permission*/ RefObject createPermission()
    {

        Object permissionClass = Util.getProperty(getSecureUmlPackage(), "permission");
        RefObject permission = (RefObject) Util.invokeParameterlessMethod(permissionClass, "createPermission");
        return permission;

//         return ((SecureUmlPackage) getSecureUmlPackage())
//             .getPermission()
//             .createPermission();
    }

    /** create a permission with the given name */
    public /*Permission*/ RefObject createPermission(String name)
    {


        //Object permissionClass = Util.getProperty(getSecureUmlPackage(), "permission");
        RefObject permission = createPermission();//(RefObject) Util.invokeParameterlessMethod(permissionClass, "createPermission");

        Util.setProperty(permission, "name", name);

        return permission;

        //  return new PermissionImpl(name);
        //return secureUmlPackage.getPermission().createPermission(name);
    }

    /** create a role with the given name */
    public RefObject /* Role */ createRole(String name)
    {

        //return new RoleImpl(name);

        Object roleClass = Util.getProperty(getSecureUmlPackage(), "role");
        RefObject role = (RefObject) Util.invokeParameterlessMethod(roleClass, "createRole");

        Util.setProperty(role, "name", name);

        return role;

        //return secureUmlPackage.getRole().createRole(name);
    }

    public RefObject /* Policy */ createPolicy(String name) {
        aLog.debug("createPolicy__" + name);
        Object policyClass = Util.getProperty(getSecureUmlPackage(), "policy");
        RefObject policy = (RefObject) Util.invokeParameterlessMethod(policyClass, "createPolicy");

        Util.setProperty(policy, "name", name);

        return policy;
    }


    /** create an action of the type given as string */
    public /*Action*/ RefObject createAction(String name)
    {
        //return new ActionI
        //return new ActionImpl(name);

        Object actionClass = Util.getProperty(
                                 getDialectDialectPackage(),
                                 //getSecureUmlPackage(),
                                 name);//"action");
        RefObject action = (RefObject) Util.invokeParameterlessMethod(actionClass,
                           "create" + Util.capitalize(name));//Action");

        Util.setProperty(action, "name", name);

        return action;
    }

    /** create an action of the given type */
    public RefObject createAction(ActionType actionType) {
        RefObject newActionObject = createAction(actionType.getName());
        Util.setProperty(newActionObject, "name", actionType.getShortName());
        return  newActionObject;
    }


    /** create an authorization constraint */
    public RefObject createAuthorizationConstraint(String constraint)
    {
        Object authCClass = Util.getProperty(getSecureUmlPackage(), "authorizationConstraint");
        RefObject newAuthCObject = (RefObject)  Util.invokeParameterlessMethod(authCClass, "createAuthorizationConstraint");
        Util.setProperty(newAuthCObject, "constraint", constraint);
        return newAuthCObject;
    }

//    /* currently not used */
//    public /*Action*/ Object createAction(Class cl)
//    {
//
//        try
//        {
//            Action action = null;
//            //org.argouml.ui.secureuml.securemodelimpl.componentumldialect.EntityFullAccessImpl
//
//            String classname = cl.getName();
//
//            // change 57:
//            // Implementation is in Package securemodelimpl! -> replace
//            classname = classname.replaceFirst(".securemodel", ".securemodelimpl");
//
//            logger.log(logger.INFORMATIONAL, logger.MODELMAPPER, "creating actionImpl: " + classname + "Impl");
//
//            Class actionImplClass = Class.forName(classname + "Impl");
//
//            Constructor ctor = actionImplClass.getConstructor(new Class[0]);
//
//            Object instance = ctor.newInstance(new Object[0]);
//
//            action = (Action) instance;
//
//            return action;
//        }
//        catch (Exception e)
//        {
//            logger.logException(e);
//        }
//
//        return null;
//    }


//     public /*Association*/ Object createAssociation(String name)
//     {
//         AssociationImpl assoc = new AssociationImpl(name);
//         return assoc;
//     }


}

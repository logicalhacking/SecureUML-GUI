package ch.ethz.infsec.secureumlgui;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


import org.apache.log4j.Logger;
import org.argouml.ui.targetmanager.TargetManager;
import org.omg.uml.foundation.core.AssociationClass;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.UmlClass;

//import ch.ethz.infsec.secureumlgui.gui.SecureUmlComponentManager;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectHelper;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectModelMapper;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.DialectMetaModelInfo;
import ch.ethz.infsec.secureumlgui.securemodel.secureuml.Role;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PolicyWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.ResourceWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;
import ch.ethz.infsec.secureumlgui.transformation.ModelMap;
import ch.ethz.infsec.secureumlgui.util.PermissionDummy;

/**
 * Central controller instance between the GUI (SecureUML property tab)
 * and the modelmapper (ArgoUml model -> SecureUML entities).
 *
 *
 */
public class ModuleController
/* TODO: refactor: introduce abstract class for this class
 * & use the abstract class in PropPanels
 */
{
    private static Logger aLog = Logger.getLogger(ModuleController.class);

    private ModuleController()
    {
        //TargetManager.getInstance().

        modelMapper = GenericDialectModelMapper.getInstance();
        if(modelMapper == null)
            logger.error("modelMapper = null");
    }

    private GenericDialectHelper helper = GenericDialectHelper.getInstance();

    private static ModuleController moduleControllerInstance = null;

    public static ModuleController getInstance()
    {
        if(moduleControllerInstance == null)
            moduleControllerInstance = new ModuleController();
        if(modelMapper==null) {
            moduleControllerInstance = null;
            return null;
        }
        return moduleControllerInstance;

    }

//    public static ModuleController getInstance()
//    {
//        if(moduleControllerInstance == null)
//            moduleControllerInstance = new ModuleController();
//        if(modelMapper==null)
//            return null;
//        return moduleControllerInstance;
//
//    }

    MultiContextLogger logger = MultiContextLogger.getDefault();

    static GenericDialectModelMapper modelMapper = null;

    /** initialize ModelMapper: clear cached mapped elements
     *
     */
    public void initModelMapper()
    {
        //logger.info("initModelMapper");
        if(modelMapper == null) {
            //logger.info("modelMapper = null");
            modelMapper = GenericDialectModelMapper.getInstance();
        }
        if(modelMapper != null)
            modelMapper.init();
    }

    public Object transform(ModelElement modelElement)
    {
        if(modelMapper == null) {
            logger.error("transform without modelmapper");
            return null;
        }
        else
        {

            modelMapper.transform(modelElement);

            return modelMapper.getModelMap().getElement(modelElement);
        }
    }


    /** returns a PermissionDummy containing only the necessary
     * information.
     *
     * associated ModelElements are mapped, but not used.  In
     * Addition, the Mapping permissionAssociationClass <->
     * PermissionDummy is put to the ModelMap.
     *
     */
    public PermissionDummy getSecureUmlPermission(
        AssociationClass permissionAssociationClass)
    {

        PermissionDummy permissionDummy = (PermissionDummy)
                                          ModelMap.getDefault().getElement(
                                              permissionAssociationClass);

        return permissionDummy;
    }


    /* DONE: rethink design (maybe move part of this to the modelmapper bzw.
     *       to a separate 'modelWriter' class
     */
    public void addPermission(PermissionWrapper permission)
    {
        modelMapper.getModelWriter().addPermission(permission);

        // moved to proppanels (they update theirselves)
        // reloadMappings(permission);

        refreshPropPanel(permission);

    }

    //XXX
//    public void addPermission(
//        ActionWrapper actionWrapper,
//        RoleWrapper roleWrapper)
//    {
//        modelMapper.getModelWriter().addPermission(
//            actionWrapper, roleWrapper);
//
//        // moved to proppanels (they update theirselves)
//        // reloadMappings(permission);
//
//        refreshPropPanel();
//
//    }

    public void addPermission(ActionWrapper actionWrapper,
                              RoleWrapper roleWrapper, Set<PolicyWrapper> policies)
    {
        aLog.debug("add permission: action " + actionWrapper.getName() + " role " + roleWrapper.getName() + " policies " + policies.size());

        modelMapper.getModelWriter().addPermission(
            actionWrapper, roleWrapper, policies);


        // moved to proppanels (they update theirselves)
        // reloadMappings(permission);

        refreshPropPanel();

    }


//    public UmlClass addPolicy(String policyName, Namespace namespace) {
//    	return modelMapper.getModelWriter().createPolicy(policyName, namespace);
//    	//return new PolicyWrapper(modelMapper.getModelMap().getElement(policyClass));
//    }

    public UmlClass createPolicy(String policyName, Set<PolicyWrapper> refined_by, Namespace namespace) {
        aLog.debug("write to modelwriter");
        UmlClass policyClass = modelMapper.getModelWriter().createPolicy(policyName, refined_by, namespace);
        aLog.debug("received new UmlClass: " + policyClass + " .. " + policyClass.getClass().toString());

        TargetManager.getInstance().setTarget(policyClass);

        //modelMapper.init();
        modelMapper.examineUmlClass(policyClass);

        Object policyObject = modelMapper.getModelMap().getElement(policyClass);
//    	aLog.debug("new policyObject: " + policyObject);
//    	logger.info("added Policy: class" + policyClass +" \nObject " + policyObject);
//    	return new PolicyWrapper(policyObject);
        return policyClass;
    }

    //hack
    public ModelMap getModelMap() {
        return modelMapper.getModelMap();
    }

    public void addRole(String roleName, ResourceWrapper resourceWrapper)
    {
        try
        {
            ModelElement modelElement = (ModelElement)
                                        modelMapper.getModelMap().getUmlElement(resourceWrapper.getModelElement());


            Namespace namespace = getNamespace(modelElement);

            //Role role = new RoleImpl(roleName);


            UmlClass newRole = modelMapper.getModelWriter().
                               createRole(roleName, namespace);

            if(newRole != null)
                TargetManager.getInstance().setTarget(newRole);

            logger.info("Added role: " + roleName);
        }
        catch (Exception e)
        {
            logger.logException(e);
        }

        // PropPanelClassSecureUml.getInstance().onTargetSet();
    }
    public Namespace getNamespace(ModelElement modelElement)
    {
        if(modelElement instanceof UmlClass)
        {
            return modelElement.getNamespace();
        }
        else if(modelElement instanceof Attribute)
        {
            return ((Attribute)modelElement).getOwner().getNamespace();
        }
        else if(modelElement instanceof Operation)
        {
            return ((Operation)modelElement).getOwner().getNamespace();
        }
        return null;
    }

    protected void refreshPropPanel()
    {
        logger.info(//logger.TARGET_EVENTS,
            "Model changed - triggering update " );
        //   + resource);

        SecureUmlModule.getTab().onTargetSet();

    }
    /**
     * @param permission
     */
    protected void refreshPropPanel(PermissionWrapper permission)
    {
        try
        {
            logger.info(//logger.TARGET_EVENTS,
                "Model changed - triggering update " +
                "of PropPanel with Resource: " );
            //   + resource);


            // TODO: refresh proppanels
            SecureUmlModule.getTab().onTargetSet();

        }
        catch (Exception e)
        {
            logger.logException(e);
        }
    }

    public Role getSecureUmlRole(UmlClass roleClass)
    {
        if(roleClass == null)
            return null;
        return (Role) ModelMap.getDefault().getElement(roleClass);
    }


    public List/*<Role>*/ getAllRoles(Object resource)
    {
        try
        {
            List roles = new LinkedList();
            for (Iterator iter = modelMapper.getRoleClasses().iterator(); iter.hasNext();)
            {
                UmlClass roleClass = (UmlClass) iter.next();

                roles.add(modelMapper.getModelMap().getElement(roleClass));
            }

            return roles;

        }
        catch (Exception ex)
        {
            logger.logException(ex);
            return new LinkedList<Role>();
        }
    }

    public List<Object> getAllPolicies() {
        List<Object> policies = new LinkedList<Object>();

        for ( UmlClass umlClass : modelMapper.getPolicyClasses() ) {
            policies.add( modelMapper.getModelMap().getElement(umlClass));
        }

        return policies;
    }

//    public List<Role> getAllRoles(Entity entity)
//    {
//        try
//        {
//            ModelElement modelElement = (ModelElement) ModelMap.getDefault().getUmlElement(entity);
//
//            return getAllRoles((Namespace) modelElement.getNamespace());
//        }
//        catch (Exception ex)
//        {
//            logger.logException(ex);
//            return null;
//        }
//    }

    public List/*<Role>*/ getAllRoles(Namespace namespace)
    {
        return null;
        //return modelMapper.transformAllRoles(namespace);
    }


    public void deletePermission(PermissionWrapper permissionWrapper)
    {
        // TODO: take from da;
        modelMapper.getModelWriter().deletePermission(permissionWrapper);

        refreshPropPanel(permissionWrapper);
    }


//    // unused
//    public void setPermissionName(PermissionWrapper permission, String name)
//    {
//        modelMapper.getModelWriter().
//            setPermissionName(permission, name);
//    }
//
    public void setAuthorizationConstraint(PermissionDummy permissionDummy, String constraint)
    {
        //      TODO: take from da;
        modelMapper.getModelWriter().
        setAuthorizationConstraint(permissionDummy, constraint);
    }


    DialectMetaModelInfo dialectMetaModelInfo;

    public DialectMetaModelInfo getDialectMetaModelInfo()
    {
        return dialectMetaModelInfo;
    }

    public void setDialectMetaModelInfo(
        DialectMetaModelInfo dialectMetaModelInfo)
    {
        this.dialectMetaModelInfo = dialectMetaModelInfo;
    }



    /* Event Handlers */
}

package ch.ethz.infsec.secureumlgui.modelmapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jmi.reflect.RefObject;
import javax.jmi.reflect.RefPackage;

//import org.argouml.ui.secureuml.modelmanagement.ModelConst;
import org.apache.log4j.Logger;
import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Feature;
import org.omg.uml.foundation.core.Generalization;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.AssociationClass;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.UmlClass;

//import tudresden.ocl20.core.MetaModelConst;
import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants;
import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.modelmapping.counters.SecureUmlMappingCounter;
//import ch.ethz.infsec.secureumlgui.oclconstraintloader.ConstraintLoader;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.DialectMetaModelInfo;
import ch.ethz.infsec.secureumlgui.util.PermissionDummy;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PolicyWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.ResourceWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;

import ch.ethz.infsec.secureumlgui.securemodelimpl.SecureModelFactory;

/**
 * Abstract class for mapping the SecureUML (non-dialect) elements.
 *
 * Subclasses will do the mapping for the dialect specific elements.
 *
 * @version 1.0
 */
public abstract class SecureUmlModelMapper extends ModelMapper {

    private static final boolean verbose = true;

    @Deprecated
    protected static final String STEREOTYPE_SECUML_ROLE = SecureUmlConstants.STEREOTYPE_SECUML_ROLE;

    @Deprecated
    protected static final String STEREOTYPE_SECUML_PERMISSION = SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION;

    private Collection<Classifier> permissionAnchors = new ArrayList<Classifier>();

    // private ConstraintLoader constraintLoader;

    public DialectMetaModelInfo dialectMetaModelInfo;

    GenericDialectHelper helper = GenericDialectHelper.getInstance();

    private SecureUmlMappingCounter counter = new SecureUmlMappingCounter();


    private static Logger aLog = Logger.getLogger(SecureUmlModelMapper.class);

    public SecureUmlModelMapper() {
        logger.info("SecureUmlModelMapper");
    }

    public void init() {
        super.init();

        counter = new SecureUmlMappingCounter();

        permissionAnchors.clear();
    }

    // public static <T> String join(final Collection<T> objs, final String
    // delimiter) {
    //
    // return null;
    // }
    //
    // public static void test() {
    // Collection<Integer> asdf = null;
    //
    // <Integer> String result = join(asdf, ";");
    // }

    /* mapping of general uml elements (identification of stereotyped elements) */

    @SuppressWarnings("unchecked")
    public void transform() {
        super.transform();
        logger.info(counter.toString());
    }

    public void examineUmlClass(UmlClass umlClass) {
        if (isOfType(umlClass, SecureUmlConstants.STEREOTYPE_SECUML_ROLE)) {
            transformRole(umlClass);
        } else if (isOfType(umlClass,
                            SecureUmlConstants.STEREOTYPE_SECUML_POLICY)) {
            transformPolicy(umlClass);
        }
    }

    protected void examineUmlGeneralization(Generalization generalization) {
        if (isOfType(generalization.getChild(),
                     SecureUmlConstants.STEREOTYPE_SECUML_ROLE)
                && isOfType(generalization.getParent(),
                            SecureUmlConstants.STEREOTYPE_SECUML_ROLE)) {
            transformRoleInheritance(generalization);
        }
    }

    @SuppressWarnings("unchecked")
    protected void examineAssociationClass(AssociationClass associationClass) {
        if (isOfType(associationClass,
                     SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION)) {
            transformPermissionClass(associationClass);
        }
    }

    /* mapping of stereotyped elements */

    @SuppressWarnings("unchecked")
    protected void transformPermissionClass(AssociationClass associationClass) {
        Classifier roleClassifier = null;
        Classifier policyClassifier = null;
        Classifier anchorClassifier = null;

        Object role = null;
        Object policy = null;
        Object anchor = null;

        // if (verbose)
        // logger.info("-- permission: "
        // + associationClass.getName());

        // association ends => role and anchor
        for (AssociationEnd end : associationClass.getConnection()) {
            Classifier participant = end.getParticipant();
            if (isOfType(participant, SecureUmlConstants.STEREOTYPE_SECUML_ROLE)) {
                roleClassifier = participant;
                aLog.debug("found role " + roleClassifier);
//			} else if (isOfType(participant,
//					SecureUmlConstants.STEREOTYPE_SECUML_POLICY)) {
//				policyClassifier = participant;
//				aLog.debug("found policy " + policyClassifier);
            } else if (!isOfType(participant,
                                 SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION)) {
                anchorClassifier = participant;
                aLog.debug("found anchor " + anchorClassifier);
            } else {
                aLog.debug("what did i find?" + participant);
            }
        }

        policyClassifier = GenericDialectModelMapper.getInstance().getPolicy(associationClass);




        // fail if either role or anchor are missing
        if (roleClassifier == null) {
            logger.error("no role associated with permission "
                         + associationClass.getName());
            return;
        } else {
            role = map.getElement(roleClassifier);
            aLog.debug("permission "+associationClass.getName()+ " associatied with Role "+role);
        }

        if (policyClassifier == null) {
            aLog.debug("no policyClassifier found for permission "
                       + associationClass.getName() );
//			policy = GenericDialectModelMapper.getInstance().getDefaultPolicy();
        } else {

            policy = map.getElement(policyClassifier);
            aLog.debug("found policy for permission: " + policy);
        }

        if (anchorClassifier == null) {
            logger.error("no anchor associated with permission "
                         + associationClass.getName());
            return;
        } else {
            anchor = map.getElement(anchorClassifier);
            // logger.info("permission "+associationClass.getName()+"
            // associatied with Anchor "+anchor);
        }
        // logger.info("role: " + role
        // + "anchor:" + anchor);
        // if(role==null){logger.error("role not found while transforming
        // permission "+associationClass.getName());}
        // if(anchor==null){logger.error("anchor not found while transforming
        // permission "+associationClass.getName());}
        counter.incPermissionClassCount();
        // may happen if the anchor wasn't in the mapping scope:
        if (anchor == null)
            return;
        // constraint => constraint for all permissions
        String constraint = getAuthorizationConstraint(associationClass);
        Object authorizationConstraint = null;
        if (constraint != null && constraint.length() > 0) {
            authorizationConstraint = SecureModelFactory.getInstance()
                                      .createAuthorizationConstraint(constraint);
        }

        PermissionDummy permissionDummy = new PermissionDummy(associationClass
                .getName());

        // RoleWrapper roleWrapper = new RoleWrapper(role);
        // ResourceWrapper anchorWrapper = new ResourceWrapper(anchor);

        permissionDummy.setRole(role);
        if (policy != null ) {
            permissionDummy.setPolicy(policy);
        }
        permissionDummy.setAnchor(anchor);
        permissionDummy.setAuthorizationConstraint(authorizationConstraint);

        map.put(associationClass, permissionDummy);

        // attributes => the permissions
        //for (Object o : (List) associationClass.getFeature())
        for (Feature feature : associationClass.getFeature())
            // .allAttributes())
        {
            if (feature instanceof Attribute) {
                Attribute a = (Attribute) feature;

                transformPermission(associationClass.getName(), a,
                                    anchorClassifier, roleClassifier, policyClassifier,
                                    authorizationConstraint);

                Object permission = map.getElement(a);

                if (permission != null) {
                    PermissionWrapper pw = new PermissionWrapper(permission);
                    pw.setAuthorizationConstraint(authorizationConstraint);

                    //PermissionWrapper permissionWrapper = new PermissionWrapper(permission);
                    //aLog.debug("permissionWrapper " + pw.getName() + " with policy: " + (pw.getPolicyWrapper() == null ? "NULL" : pw.getPolicyWrapper().getName()));

                    permissionDummy.addPermissionWrapper(pw);
                }
            }
        }
        // map.addForDeletion(associationClass);
        // map.printMap();
    }

    /**
     * looks for the proper tagged value of the assocation class.
     */
    private String getAuthorizationConstraint(AssociationClass associationClass) {
        String constraint = "";
        Collection taggedValues = associationClass.getTaggedValue();
        if (taggedValues != null && taggedValues.size() > 0) {
            for (Iterator iter = taggedValues.iterator(); iter.hasNext();) {
                TaggedValue taggedValue = (TaggedValue) iter.next();

                if (taggedValue != null && taggedValue.getDataValue() != null
                        && taggedValue.getDataValue().size() > 0) {
                    logger.info("### TaggedValue found( type: "
                                + taggedValue.getType().getName() + ", dataValue: "
                                + taggedValue.getDataValue().iterator().next());

                    if (taggedValue != null
                            && taggedValue.getType().getName() != null
                            && taggedValue
                            .getType()
                            .getName()
                            .equals(
                                SecureUmlConstants.TAG_DEFINITION_AUTHORIZATION_CONSTRAINT)) {

                        constraint = taggedValue.getDataValue().iterator()
                                     .next().toString();

                    }
                }
            }
        }
        return fixSpacing(constraint);
    }

    /**
     * replace " . " by "."
     *
     * @param constraint
     * @return the transformed string
     */
    private String fixSpacing(String constraint) {
        if (constraint == null) {
            return null;
        }
        return constraint.replaceAll("\\s\\.\\s", "\\.");
    }

    @SuppressWarnings("unchecked")
    protected void transformPermission(String permissionName, Attribute attr,
                                       Classifier anchorClassifier, Classifier roleClassifier, Classifier policyClassifier,
                                       Object constraint) {
        // TODO:

        // find the resource by resolving the attribute name, which is the
        // path name of the resource inside the anchor
        ModelElement targetResource =
            // PathNameResolver.resolve(
            helper.resolvePath(attr.getName(), anchorClassifier);

        // String attributeName = a.getName();

        // logger.info("transforming Permission on Resource '"
        // + targetResourceName
        // + "' for action'"
        // + attributeType);

        if (targetResource != null  &&  attr != null  &&  attr.getType() != null) {
            String attributeType = attr.getType().getName();
            String targetResourceName = targetResource.getName();
            // if (verbose)
            // logger.info(" resource: " + targetResourceName
            // + ", action: " + a.getType().getName());

            // create the permission
            // TODO:
            // Permission suPermission = null;
            // = target.getSecureUml().getPermission()
            // .createPermission(permissionName);
            // connect with role

            RefObject permission = SecureModelFactory
                                   .getInstance()
                                   .createPermission(attr.getName() + "_" + attr.getType().getName());

            // logger.info("Permission "+ a.getName() + "_" +
            // a.getType().getName() + " created");



            // permission.setRole((Role) map.getElement(role));
            Util
            .setProperty(permission, "role", map
                         .getElement(roleClassifier));

            Util.setProperty(permission, "name", permissionName);

            Object suPolicy = map.getElement(policyClassifier);

            if ( suPolicy != null ) {

//				Util.setProperty(permission, "policy", suPolicy);
//				aLog.debug("SET POLICY " + suPolicy + " to PERMISSION " + permission);
                PermissionWrapper perm = new PermissionWrapper(permission);

                perm.setPolicy(suPolicy);


                Set<PolicyWrapper> pol = perm.getPolicyWrappers();
                aLog.debug("check: " + (pol == null ? "NULL" : pol.size()));
            }

            map.put(attr, permission);

            // instantiate the corresponding action
            RefObject suTargetResource = (RefObject) map.getElement(targetResource);

            // ResourceWrapper rw = new ResourceWrapper(suTargetResource);
            // rw.get
            if (suTargetResource == null)
                logger.error("suTargetResource = null, targetResource = "
                             + targetResource + " " + targetResourceName
                             + ", permission = " + permissionName);

            RefObject suAction = getOrCreateAction(suTargetResource, attr
                                                   .getType().getName());

            // RefObject suAction =
            // ActionInstantiator.initializeAction(a.getType().getName(),
            // suAction,
            // suTargetResource);

            if (suAction == null) {
                logger.error("instantiation of action " + attr.getType().getName()
                             + " failed");
                return;
            }

            ActionWrapper aw = ActionWrapper.createActionWrapper(suAction);
            aw.addPermission(permission);

            Object suRole = map.getElement(roleClassifier);
            RoleWrapper roleWrapper = new RoleWrapper(suRole);

            roleWrapper.addPermission(permission);

            PermissionWrapper permissionwrapper = new PermissionWrapper(
                permission);
            permissionwrapper.setRole(suRole);
            permissionwrapper.setAction(suAction);
            if ( suPolicy != null ) {
                aLog.debug("set policy for permission " + permissionwrapper.getName() + " policy " + suPolicy + " " + new PermissionWrapper(suPolicy).getName());
                permissionwrapper.setPolicy(suPolicy);
            }

            ResourceWrapper resourceWrapper = new ResourceWrapper(
                suTargetResource);
            resourceWrapper.getAction().add(suAction);

            //PolicyWrapper policy = new PolicyWrapper(suPolicy);
            //policy.








            // logger.info("Transformed Permission for Role: "
            // + roleWrapper.getName()
            // + ", permission: "
            // + permissionwrapper.getName()
            // + " on Action: "
            // + permissionwrapper.getActionWrapper().getName());

            // TODO: connect the permission with the action
            // permission.getAction().add(suAction);

            // Util.setProperty(permission, "action", suAction);

            // connect the Action with the Resource

            if (constraint != null) {
                Util.setProperty(permission, "authorizationConstraint",
                                 constraint);

                Util.setProperty(constraint, "permission", permission);

            }

            permissionAnchors.add(anchorClassifier);
            counter.incPermissionCount();
        } else {
            logger.error("target resource not found");
        }
    }

    /** creates a SecureUML role with the name of the UML class */
    protected void transformRole(Classifier roleClass) {
        if (map.mapContainsKey(roleClass))
            return;

        // if (verbose)
        // logger.info("-- role: " + roleClass.getName());
        // RefObject role = createRole(roleClass.getName());

        RefObject role = SecureModelFactory.getInstance().createRole(
                             roleClass.getName());

        // TODO
        // target.getSecureUml().getRole().createRole();

        // role.setName(roleClass.getName());

        String propertyName = "name";

        Util.setProperty(role, propertyName, roleClass.getName());

        map.put(roleClass, role);
        // map.addForDeletion(roleClass);
        counter.incRoleCount();
    }

    protected void transformPolicy(Classifier policyClass) {
        if (map.mapContainsKey(policyClass)) {
            return;
        }

        RefObject policy = SecureModelFactory.getInstance().createPolicy(policyClass.getName());

        //redundant?
        Util.setProperty(policy, "name", policyClass.getName());

        map.put(policyClass, policy);

        counter.incPolicyCount();
    }

    @SuppressWarnings("unchecked")
    protected void transformRoleInheritance(Generalization generalization) {
        if (map.mapContainsKey(generalization))
            return;

        Object child = /* (Role) */map.getElement(generalization.getChild());
        Object parent = /* (Role) */map.getElement(generalization.getParent());

        if (child != null && parent != null) {
            // if (verbose)
            // logger.info("-- role inheritance: " + Util.getProperty(parent,
            // "name") + "-"
            // + Util.getProperty(child, "name"));

            // child.getSuperroles().add(parent);
            Util.setProperty(child, "superroles", parent);

            // parent.getSubroles().add(child);
            Util.setProperty(parent, "subroles", child);

            counter.incRoleInheritance();
        }
    }

    protected void  transformPolicyInheritance(Generalization generalization) {
        if (map.mapContainsKey(generalization)) {
            return;
        }

        Object child = /* (Policy) */ map.getElement(generalization.getChild());
        Object parent = /* (Policy) */map.getElement(generalization.getParent());

        if ( child != null && parent != null ) {

//			Method[] methods = child.getClass().getMethods();
//			for (Method method : methods) {
//				System.out.print(method.getReturnType() + " " + method.getName() + "(");
//				for ( Class paramType : method.getParameterTypes() ) {
//					System.out.print(paramType + ",");
//				}
//				System.out.println(")");
//			}

            Util.setProperty(child, SecureUmlConstants.POLICY_INHERITANCE_REFINEDBY, parent);
            Util.setProperty(parent, SecureUmlConstants.POLICY_INHERITANCE_REFINES, child);

            counter.incPolicyInheritanceCount();
        }
    }

    /* utility methods */

    protected boolean isOfType(ModelElement element, String stereotype) {
        if (element == null || stereotype == null)
            return false;

        Collection stereotypes = element.getStereotype();
        int nofStereotypes = stereotypes.size();

        if (stereotypes == null || stereotypes.size() == 0)
            return false;

        for (Iterator it = stereotypes.iterator(); it.hasNext();) {
            Stereotype s = (Stereotype) it.next();
            if (s.getName().equals(stereotype)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isOfType(Collection<ModelElement> elements,
                               String stereotype) {
        for (ModelElement elem : elements) {
            if (!isOfType(elem, stereotype)) {
                return false;
            }
        }
        return true;
    }

    protected boolean endsAreOfTypes(List<AssociationEnd> e, String s1,
                                     String s2) {
        if (e.size() != 2) {
            logger.warning("expecting 2 collection elements");
            return false;
        } else {
            return ((isOfType(e.get(0).getParticipant(), s1) && isOfType(e.get(
                         1).getParticipant(), s2)) || (isOfType(e.get(0)
                                 .getParticipant(), s2) && isOfType(e.get(1)
                                         .getParticipant(), s1)));
        }
    }

    // abstract public RefObject createRole(String name);

    // abstract public RefObject createPermission(String name);

    // abstract public RefObject createAuthorizationConstraint(String
    // constraint);

    abstract public RefObject getOrCreateAction(RefObject resource,
            String shortActionName);

    public Collection<Classifier> getPermissionAnchors() {
        return permissionAnchors;
    }

}

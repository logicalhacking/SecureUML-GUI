package ch.ethz.infsec.secureumlgui.modelmapping;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jmi.model.Association;
import javax.jmi.reflect.RefObject;

import org.apache.log4j.Logger;
import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.CoreFactory;
import org.argouml.model.CoreHelper;
import org.argouml.model.Facade;
import org.argouml.model.Model;
import org.omg.uml.foundation.core.AssociationClass;
import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.modelmanagement.UmlPackage;

//import java.lang.String;

import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants;
import ch.ethz.infsec.secureumlgui.modelmanagement.ModelConst;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ActionType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelConst;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;
import ch.ethz.infsec.secureumlgui.transformation.ModelMap;
import ch.ethz.infsec.secureumlgui.util.PermissionDummy;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.NamedModelElementWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PolicyWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.ResourceWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;

/**
 * Writes SecureUml Model Elements to UML Model.
 *
 * Only elements for which a mapping is contained in modelmap can be written
 *
 *
 */
public class ModelWriter {

    public ModelWriter(ModelMap modelmap) {
        this.modelmap = modelmap;
    }

    ModelMap modelmap;

    GenericDialectHelper helper = GenericDialectHelper.getInstance();

    private Logger aLog = Logger.getLogger(ModelWriter.class);


    private int hack_count = 0;



    /**
     * ModelMap containing (both-sided) Mapping between UML Model Elements and
     * SecureUml Entities.
     *
     */
    public ModelMap getModelmap() {
        return modelmap;
    }

    MultiContextLogger logger = MultiContextLogger.getDefault();

    public void createPermission(Object resourceUml, Object roleUml,
                                 ActionWrapper actionWrapper, Set<PolicyWrapper> policies) {
        String name = helper.getResourcePath((ModelElement) resourceUml);

        createPermission(resourceUml, roleUml, name, actionWrapper, policies);
    }

    public void createPermission(ResourceWrapper resourceWrapper,
                                 RoleWrapper roleWrapper, ActionWrapper actionWrapper, Set<PolicyWrapper> policies) {
        Object secUmlModelElement = resourceWrapper.getModelElement();

        ModelElement resourceUml = (ModelElement) getModelmap().getUmlElement(
                                       secUmlModelElement);

        String permissionAttributeName = getPermissionAttributeName(resourceUml);

        createPermission(resourceWrapper, roleWrapper, permissionAttributeName,
                         actionWrapper, policies);
    }

    protected void createPermission(Object resourceUml, Object roleUml,
                                    String permissionAttributeName, ActionWrapper actionWrapper, Set<PolicyWrapper> policies) {

        Classifier anchor = helper.findAnchor((ModelElement) resourceUml);

        createPermissionViaModelCore(permissionAttributeName, resourceUml,
                                     (Classifier) anchor, (Classifier) roleUml, actionWrapper, policies);
    }

    //XXX
//	protected void createPermission(ResourceWrapper resourceWrapper,
//			RoleWrapper roleWrapper, String permissionAttributeName,
//			ActionWrapper actionWrapper) {
//		try {
//			logger.info(logger.MODELWRITER, "creating Permission: \n"
//					+ " resourceWrapper: " + resourceWrapper.getName() + "\n"
//					+ " roleWrapper: " + roleWrapper.getName() + "\n"
//					+ " actionWrapper: " + actionWrapper.getName());
//
//			RefObject resourceUml = getModelmap().getUmlElement(
//					resourceWrapper.getModelElement());
//
//			Classifier anchor = helper.findAnchor((ModelElement) resourceUml);
//
//			Classifier roleClassifier = (Classifier) getModelmap()
//					.getUmlElement(roleWrapper.getModelElement());
//
//			createPermissionViaModelCore(permissionAttributeName, resourceUml,
//					anchor, roleClassifier, actionWrapper);
//		} catch (Exception e) {
//			logger.logException(e);
//		}
//	}

    protected void createPermission(ResourceWrapper resourceWrapper,
                                    RoleWrapper roleWrapper, String permissionAttributeName,
                                    ActionWrapper actionWrapper,  Set<PolicyWrapper> policies) {
        try {
            logger.info(logger.MODELWRITER, "creating Permission: \n"
                        + " resourceWrapper: " + resourceWrapper.getName() + "\n"
                        + " roleWrapper: " + roleWrapper.getName() + "\n"
                        + " actionWrapper: " + actionWrapper.getName());

            RefObject resourceUml = getModelmap().getUmlElement(
                                        resourceWrapper.getModelElement());

            Classifier anchor = helper.findAnchor((ModelElement) resourceUml);

            Classifier roleClassifier = (Classifier) getModelmap()
                                        .getUmlElement(roleWrapper.getModelElement());

            //Classifier policyClassifier = (Classifier) getModelmap().getUmlElement(policyWrapper.getModelElement());



            createPermissionViaModelCore(permissionAttributeName, resourceUml,
                                         anchor, roleClassifier, actionWrapper, policies);
        } catch (Exception e) {
            logger.logException(e);
        }
    }

    /**
     * @param anchor
     * @param roleClass
     * @param actionWrapper
     */
    protected void createPermissionViaModelCore(String permissionName,
            Object resourceUml, Classifier anchor, Classifier roleClass,
            ActionWrapper actionWrapper,  Set<PolicyWrapper> policies) {
        try {
            // check if Association class
            // with same Name already exists!
            String newPermissionName = permissionName
                                       + Util.getNewPermissionNumber();

            Collection classifiers = null;

            boolean isNameAlreadyUsed = false;

            do {
                isNameAlreadyUsed = false;
                if (classifiers != null) {
                    for (Iterator iter = classifiers.iterator(); iter.hasNext();) {
                        Classifier classifier = (Classifier) iter.next();

                        if (classifier.getName().equalsIgnoreCase(
                                    newPermissionName)) {
                            isNameAlreadyUsed = true;

                            logger.info(logger.MODELWRITER,
                                        "AssociationClass with Name "
                                        + newPermissionName
                                        + " already exists -> "
                                        + "incrementing sequence Number");

                            newPermissionName = permissionName
                                                + Util.getNewPermissionNumber();
                            break;
                        }
                    }
                }
            } while (isNameAlreadyUsed);

            AssociationClass newUmlPermission = (AssociationClass) Model
                                                .getCoreFactory().buildAssociationClass(anchor, roleClass);

            logger.info("Permission AssociationClass created: "
                        + newUmlPermission.getName());

            UmlPackage permissionPackage = GenericDialectModelMapper.getInstance().getPermissionPackage();

            newUmlPermission.setName(newPermissionName);
            newUmlPermission.setNamespace(permissionPackage); // TODO




            // hel put permission in sub namespace
            newUmlPermission.setActive(true);

            Stereotype stereotype = null;// Util.findStereotypeByName(newUmlPermission,
            // SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION);

            // TODO;
            // Util.findStereotypeByName(newUmlPermission,
            // SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION);
            stereotype = getOrCreateStereotype(newUmlPermission,
                                               SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION,
                                               newUmlPermission.getNamespace());
            // modelmap.getStereotype(SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION);

            newUmlPermission.getStereotype().add(stereotype);

            String permissionAttributeName = getPermissionAttributeName((ModelElement) resourceUml);

            String actionShortname = actionWrapper.getName();

            Classifier actionType = getOrCreateActionType(actionShortname,
                                    newUmlPermission.getNamespace());

            // TODO: examine the Type

            // String attributeName = helper.getResourcePath(
            // (ModelElement)resourceUml);

            Attribute permissionAttribute = (Attribute) addAttribute(
                                                newUmlPermission, "foobar", actionType);

            permissionAttribute.setName(permissionAttributeName);

            permissionAttribute.setType(actionType);

            // TODO: examine Stereotype

            String stereotypeName = getActionStereotype(resourceUml);

            stereotype = getOrCreateStereotype(permissionAttribute,
                                               stereotypeName, newUmlPermission.getNamespace());

            if (stereotype != null)
                permissionAttribute.getStereotype().add(stereotype);



            if (policies != null || policies.size() == 0 ) {


                if (policies.size() != 1) {
                    aLog.error("All policies than the first are ignored.... TODO!");
                }


                PolicyWrapper policyWrapper = policies.iterator().next();


                Classifier policy = (Classifier) getModelmap().getUmlElement(policyWrapper.getModelElement());

//				UmlAssociation perm_pol = (UmlAssociation) Model.getCoreFactory().buildAssociation(
//						newUmlPermission, true, policy, true, "permission_policy_" + ++hack_count);
                UmlAssociation perm_pol = (UmlAssociation) Model.getCoreFactory().buildAssociation(
                                              newUmlPermission, true, policy, true, policyWrapper.getName() + "_permission_" + ++hack_count);

                perm_pol.setNamespace(permissionPackage);

//				System.out.println("assoc connection count: " +perm_pol.getConnection().size());
//
//				Object assE1 = Model.getCoreFactory().buildAssociationEnd(newUmlPermission, perm_pol);
//				System.out.println("XX1: " + assE1.getClass().toString());
//
//
//				Object assE2 = Model.getCoreFactory().buildAssociationEnd(policy, perm_pol);
//				System.out.println("XX2: " + assE2.getClass().toString());
//
//				System.out.println("assoc connection count: " +perm_pol.getConnection().size());

                logger.info("added association to policy " + policyWrapper.getName() + " result: " + perm_pol.getClass().toString());
                aLog.debug("added association to policy " + policyWrapper.getName() + " result: " + perm_pol.getClass().toString());

//				System.out.println(perm_pol + " ## " + newUmlPermission);
//				AssociationEnd newAss = (AssociationEnd) Model.getCoreFactory().buildAssociationEnd(perm_pol, newUmlPermission );


                CoreFactory coreFact =  Model.getCoreFactory();


                List<AssociationEnd> newEnds = perm_pol.getConnection();

                try {
                    for (AssociationEnd end : newEnds ) {
                        if (end.getParticipant().equals(newUmlPermission)) {
                            aLog.debug("found association end: " + end + "\nnavigateable: " + end.isNavigable() + "  " + end.getAggregation());

                            //newUmlPermission.getConnection().add(end);
                        }
                        else {
                            aLog.debug("not the right end... " + end.getParticipant());
                        }
                    }
                } catch(Exception e) {
                    aLog.error("Error at adding association end: ", e);
                    e.printStackTrace();
                }



//				try {
//
//					AssociationEnd newAss = (AssociationEnd) coreFact.buildAssociationEnd(newUmlPermission.getConnection().get(0).getParticipant(), perm_pol);
//					//AssociationEnd newAss = (AssociationEnd) coreFact.buildAssociationEnd(newUmlPermission, perm_pol);
//					//AssociationEnd newAss = (AssociationEnd) coreFact.buildAssociationEnd(policy, perm_pol);
//					//newUmlPermission.get
//					aLog.debug("Created AssociationEnd: " + newAss);
//					aLog.debug("getParticipant: " + newAss.getParticipant());
//
//					newUmlPermission.getConnection().add(newAss);
//					aLog.debug("Added AssociationEnd to new permission");
//
//					//newUmlPermission.getConnection().
//					//perm_pol.getConnection().add(newAss);
//
//				} catch (Exception e) {
//					aLog.error("Could not create/add AssociationEnd: " + e.getClass() + ": " + e.getMessage());
//					System.out.println("####################################################");
//					e.printStackTrace();
//
//					aLog.error(e);
//				}



//				AssociationEnd newAss = (AssociationEnd) Model.getCoreFactory().buildAssociationEnd(
//						perm_pol,
//						"assEnd_" + ++hack_count2,
//						newUmlPermission,
//
//
//
//
//						assoc - The associaton this end will be part of
//						name - The name of the association end
//						type - The type (classifier) the end will connect. The end is a connection piece between an association and a classifier
//						multi - The multiplicity
//						stereo - The stereotype
//						navigable - The navigability. True if this association end can be 'passed' from the other classifier.
//						order - Ordering of the association
//						aggregation - the aggregationkind
//						scope - the scope kind
//						changeable - the changeablekind
//						visibility - the visibilitykind
//				newUmlPermission.getConnection().add(newAss);





//				for ( AssociationEnd assEnd : perm_pol.getConnection() ) {
//					Classifier classf = assEnd.getParticipant();
//
//
//					if ( classf != policy ) {
//						newUmlPermission.getConnection().add(assEnd);
//					}
//					//classf.
//
//					//AssociationClass (perm_pol) bzw.
//					//UmlClass (policy)
//
//					//policy.get
//
//
//					//aLog.debug(classf + " == " + ( classf == policy )  + " equals " + (classf.equals(policy))+ " ### == " + ( classf == perm_pol )  + " equals " + (classf.equals(perm_pol)));
//				}
                aLog.debug("PERMISSION:");
                for ( AssociationEnd assEnd : newUmlPermission.getConnection() ) {
                    aLog.debug(" with AssociationEnd " + assEnd);
                }

                aLog.debug("POLICY: " + policy);

//				ModelElement asdf = null;
//				asdf.
                //UmlClass policyUml = (UmlClass) policy;



//				for ( AssociationEnd assEnd : ( (UmlClass) policy)..getOwnedElement()..getConnection() ) {
//					aLog.debug(" with AssociationEnd " + assEnd);
//				}


            }

        } catch (Exception e) {
            logger.logException(e);
        }
    }

    /**
     * @param actionShortname
     * @return the action classifier
     */
    private Classifier getOrCreateActionType(String actionShortname,
            Namespace ns) {
        try {
            Classifier actionType = modelmap.getActionClass(actionShortname);

            if (actionType == null) {
//				Namespace secureUmlNs = helper.findNamespaceByName(ns,
//						"SecureUML");
                Namespace secureUmlNs = GenericDialectModelMapper.getInstance().getSecUMLPackage();

                if (secureUmlNs != null)
                    ns = secureUmlNs;

                actionType = (UmlClass) Model.getCoreFactory().createClass();

                Stereotype actionTypeStereotype = getOrCreateStereotype(
                                                      actionType,
                                                      SecureUmlConstants.STEREOTYPE_SECUML_ACTIONTYPE, ns);

                actionType.setName(actionShortname);
                actionType.setNamespace(ns);
                actionType.getStereotype().add(actionTypeStereotype);
            }
            return actionType;
        } catch (Exception e) {
            logger.logException(e);
            return null;
        }
    }

    /**
     * If Stereotype with the passed name exists (-> is stored in the Modelmap),
     * return it. Otherwise, create new Stereotype in the Namespace passed as
     * Argument
     *
     * @param name
     * @param ns
     * @return the stereotype
     */
    public Stereotype getOrCreateStereotype(ModelElement modelElementObject,
                                            String name, Namespace ns) {
        Stereotype stereotype = modelmap.getStereotype(name);

        if (stereotype == null) {
            stereotype = (Stereotype) Model.getExtensionMechanismsFactory()
                         .buildStereotype(modelElementObject, name, ns);
        }



        return stereotype;
    }


    public Stereotype getOrCreateStereotype(String name, UmlPackage secUMLPackage, String base) {
        Stereotype stereotype = modelmap.getStereotype(name);

        if (stereotype == null) {
            stereotype = (Stereotype) Model.getExtensionMechanismsFactory().buildStereotype(name, secUMLPackage);
            if ( base != null ) {
                stereotype.getBaseClass().add(base);
            }
            //Model.getCoreHelper().setOwner(stereotype, secUMLPackage);
            modelmap.putStereotype(stereotype);
        }

        return stereotype;

    }



    /**
     * @param resourceUml
     */
    private String getPermissionAttributeName(ModelElement resourceUml) {
        // String resourceName = resourceUml.getName();
        try {
            String resourcePath = helper.getResourcePath(resourceUml);
            return resourcePath;
        } catch (Exception e) {
            logger.logException(e);
        }
        return "";
    }

    private String getActionStereotype(Object resourceUml) {
        try {
            ModelElement umlModelElement = (ModelElement) resourceUml;
            ResourceType rt = helper.getResourceType(umlModelElement);
            return rt.getActionStereotype();
        } catch (Exception e) {
            logger.logException(e);
            return null;
        }
    }

    /**
     * To add a permission-Attribute to a Permission AssociationClass
     *
     * copied from org.argouml.ui.targetmanager.ActionWrapperAddAttribute
     *
     */
    public Object addAttribute(Classifier classifier, String name,
                               Classifier type) {
        Attribute attribute = (Attribute) Model.getCoreFactory()
                              .createAttribute();
        attribute.setName(name);
        attribute.setType(type);
        classifier.getFeature().add(attribute);

        return attribute;
    }

    /**
     * Adds a new Permission with the first ActionWrapper of the permission
     * Argument to the underlying Uml Model
     */
    public void addPermission(PermissionWrapper permissionWrapper) {
        try {
            ActionWrapper actionWrapper = permissionWrapper.getActionWrapper();
            if (actionWrapper == null) {
                logger.error(logger.MODELMAPPER,
                             "added Permission contains no action "
                             + "-> abort adding");
            } else {
                RoleWrapper roleWrapper = permissionWrapper.getRoleWrapper();
                addPermission(actionWrapper, roleWrapper, permissionWrapper.getPolicyWrappers());
            }
        } catch (Exception e) {
            logger.logException(e);
        }
    }

    //XXX
//	/**
//	 * @param actionWrapper
//	 * @param roleWrapper
//	 */
//	public void addPermission(ActionWrapper actionWrapper,
//			RoleWrapper roleWrapper) {
//		ResourceWrapper resourceWrapper = actionWrapper.getResourceWrapper();
//
//		ModelElement resourceUml = (ModelElement) modelmap
//				.getUmlElement(actionWrapper.getResource());
//		ModelElement anchorUml = helper.findAnchor(resourceUml);
//
//		logger.info("addPermission(anchor: " + anchorUml.getName()
//				+ ", resource: " + resourceUml.getName() + ", action: "
//				+ actionWrapper.getName());
//
//		Object suAnchor = modelmap.getElement(anchorUml);
//		NamedModelElementWrapper anchorWrapper = new NamedModelElementWrapper(
//				suAnchor);
//
//		String permissionName = "" + roleWrapper.getName()
//		// + resourceWrapper.getName()
//				// +anchorWrapper.getName()
//				+ anchorUml.getName()
//				// + getNewPermissionSuffix();
//				+ SecureUmlConstants.NEW_PERMISSION_SUFFIX;
//
//		createPermission(resourceWrapper, roleWrapper, permissionName,
//				actionWrapper);
//	}



    /**
     * @param actionWrapper
     * @param roleWrapper
     */
    public void addPermission(ActionWrapper actionWrapper,
                              RoleWrapper roleWrapper, Set<PolicyWrapper> policies) {
        ResourceWrapper resourceWrapper = actionWrapper.getResourceWrapper();

        ModelElement resourceUml = (ModelElement) modelmap
                                   .getUmlElement(actionWrapper.getResource());
        ModelElement anchorUml = helper.findAnchor(resourceUml);

        logger.info("addPermission(anchor: " + anchorUml.getName()
                    + ", resource: " + resourceUml.getName() + ", action: "
                    + actionWrapper.getName());

        Object suAnchor = modelmap.getElement(anchorUml);
        NamedModelElementWrapper anchorWrapper = new NamedModelElementWrapper(
            suAnchor);

        String permissionName = "" + roleWrapper.getName()
                                // + resourceWrapper.getName()
                                // +anchorWrapper.getName()
                                + anchorUml.getName()
                                // + getNewPermissionSuffix();
                                + SecureUmlConstants.NEW_PERMISSION_SUFFIX;

        createPermission(resourceWrapper, roleWrapper, permissionName,
                         actionWrapper, policies);
    }

    public void deletePermission(PermissionWrapper permissionWrapper) {

        deletePermissionViaModelUml(permissionWrapper);

    }

    public UmlClass createRole(String roleName, Namespace namespace) {
        try {
            // check if Class
            // with same Name already exists!
            String newRoleName = roleName + Util.getNewPermissionNumber();

            Collection classifiers = Model.getCoreHelper().getAllClassifiers(
                                         namespace);
            boolean isNameAlreadyUsed = false;

            do {
                isNameAlreadyUsed = false;
                for (Iterator iter = classifiers.iterator(); iter.hasNext();) {
                    Classifier classifier = (Classifier) iter.next();

                    if (classifier.getName().equalsIgnoreCase(newRoleName)) {
                        isNameAlreadyUsed = true;

                        logger.info(logger.MODELWRITER, "Class with Name "
                                    + newRoleName + " already exists -> "
                                    + "incrementing sequence Number");

                        newRoleName = roleName + Util.getNewPermissionNumber();
                        break;
                    }
                }
            } while (isNameAlreadyUsed);

            // END - found unique Name for the new role

            UmlClass newRole = (UmlClass) Model.getCoreFactory().buildClass(
                                   newRoleName);

            Stereotype secumlRole = getOrCreateStereotype(newRole,
                                    SecureUmlConstants.STEREOTYPE_SECUML_ROLE, newRole
                                    .getNamespace());
            newRole.getStereotype().add(secumlRole);
            newRole.setNamespace(namespace);
            newRole.setActive(true);

            return newRole;
        } catch (Exception e) {
            logger.logException(e);
            return null;
        }
    }

    public UmlClass createPolicy(String policyName, Namespace namespace) {
        // TODO assure that the policy does not exist

        aLog.debug("createPolicy: Model.getCoreFactory().buildClass(policyName)");
        UmlClass newPolicy = (UmlClass) Model.getCoreFactory().buildClass(
                                 policyName);

        aLog.debug("get stereotype");
        Stereotype secumlPolicy = getOrCreateStereotype(newPolicy,
                                  SecureUmlConstants.STEREOTYPE_SECUML_POLICY, newPolicy.getNamespace());
        newPolicy.getStereotype().add(secumlPolicy);
        newPolicy.setNamespace(namespace);
        newPolicy.setActive(true);

        return newPolicy;
    }

    public UmlClass createOclType(String name, Set<UmlClass> superTypes) {
        UmlClass newOcl = (UmlClass) Model.getCoreFactory().buildClass(name);


        Namespace namespace = GenericDialectModelMapper.getInstance().getOclPackage();

        newOcl.setNamespace(namespace);
//		Stereotype oclType = getOrCreateStereotype(newOcl, SecureUmlConstants.STEREOTYPE_OCL_TYPE, namespace);
//		newOcl.getStereotype().add(oclType);


        if ( superTypes != null && superTypes.size() > 0 ) {
            for ( UmlClass superType : superTypes ) {
                Object asdf = Model.getCoreFactory().buildGeneralization(newOcl, superType, superType.getName() + "__" + name);
                aLog.debug("build gen. " + asdf.getClass());
            }
        }


        return newOcl;
    }

    public UmlClass createPolicy(String policyName, Set<PolicyWrapper> refined_by, Namespace namespace) {

//		if (refined_by == null || refined_by.size() == 0) {
//			throw new InvalidParameterException(
//					"refined_by may not be null or empty");
//		}
//		aLog.debug("start ModelWriter.createPolicy");
//		PolicyWrapper asdf = refined_by.iterator().next();
//		aLog.debug("XXXX1: " + asdf );
//		Namespace namespace = ((UmlClass) modelmap.getUmlElement(asdf.getModelElement())).getNamespace();
//		aLog.debug("XXXX2");

        UmlClass newPolicy = createPolicy(policyName, namespace);

        aLog.debug("buildGeneralizations");
        for (PolicyWrapper ref : refined_by) {
            aLog.debug(ref.getName());
            UmlClass refinedBy_class = (UmlClass) modelmap.getUmlElement(ref
                                       .getModelElement());
            Model.getCoreFactory().buildGeneralization(refinedBy_class, newPolicy);
        }

        return newPolicy;
    }

    /**
     * @param permissionWrapper
     */
    private void deletePermissionViaModelUml(PermissionWrapper permissionWrapper) {
        try {
            /*
             * A SecureUmlPermission corresponds to an Attribute of an
             * AssociationClass in the Uml Model
             */
            RefObject permission = (RefObject) permissionWrapper
                                   .getModelElement();

            // logger.info("Deleting Permission " + permission);
            Attribute permissionAttribute = (Attribute) modelmap
                                            .getUmlElement(permission);

            AssociationClass permissionClass = (AssociationClass) permissionAttribute
                                               .getOwner();
            // logger.log(logger.INFORMATIONAL, logger.MODELWRITER,
            // "Deleting PermissionAttribute " + permissionAttribute);
            // logger.log(logger.INFORMATIONAL, logger.MODELWRITER,
            // corresponds to Permission: " + permissionClass + " )");
            /*
             * delete the attribute (stated like this in the cookbook
             */
            // String permissionName =
            // permissionAttribute.getName().substring(0);
            permissionAttribute.refDelete();

            if (permissionClass.getFeature() == null
                    || permissionClass.getFeature().size() == 0) {
                // logger.log(logger.INFORMATIONAL, logger.MODELWRITER,
                // "would delete permission AssociationClass " +
                // permissionClass);
                permissionClass.refDelete();
            }
            // logger.log(logger.INFORMATIONAL, logger.MODELWRITER,
            // "Permission deleted: " + permissionName);
        } catch (Exception e) {
            logger.error(logger.MODELWRITER, "deleting Permission failed");
            logger.logException(e);
        }
    }

    public void deleteModelElement(Object modelElement) {
        Project p = ProjectManager.getManager().getCurrentProject();
        p.moveToTrash(modelElement);
    }

    public void setPermissionName(Object permission, String name) {
        if (permission == null) {
            logger.error(logger.MODELWRITER, "setPermissionName(): "
                         + "permission Argument must not be 'null'!");
            return;
        }
        try {
            UmlAssociation permissionAssociation = (UmlAssociation) modelmap
                                                   .getUmlElement(permission);

            if (name == null || name.length() == 0)
                logger.error(logger.MODELWRITER, "setPermissionName(): "
                             + " new Permission Name is null or empty "
                             + "->  leaving unchanged");
            else
                permissionAssociation.setName(name);

        } catch (Exception e) {
            logger.logException(e);
        }

    }

    public void setAuthorizationConstraint(PermissionDummy permissionDummy,
                                           String constraint) {
        if (permissionDummy == null) {
            logger.error(logger.MODELWRITER, "setAuthorizationConstraint(): "
                         + "permission Argument must not be 'null'!");
            return;
        }
        try {
            AssociationClass permissionAssociation = (AssociationClass) modelmap
                    .getUmlElement(permissionDummy);

            if (constraint == null || constraint.length() == 0) {
                // remove TaggedValue
                Model
                .getExtensionMechanismsHelper()
                .removeTaggedValue(
                    permissionAssociation,
                    SecureUmlConstants.TAG_DEFINITION_AUTHORIZATION_CONSTRAINT);
            } else {
                // set Tagged Value
                Model
                .getCoreHelper()
                .setTaggedValue(
                    permissionAssociation,
                    SecureUmlConstants.TAG_DEFINITION_AUTHORIZATION_CONSTRAINT,
                    constraint);
            }
        } catch (Exception e) {
            logger.logException(e);
        }

    }

    /* Util methods */
    public String getActionType(Object action) {
        if (action != null) {
            ActionType actionType = GenericDialectHelper.getInstance()
                                    .getActionType(action);

            return actionType.getShortName();

        } else {
            return null;
        }
    }

}

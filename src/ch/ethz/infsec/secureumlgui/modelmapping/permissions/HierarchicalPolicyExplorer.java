package ch.ethz.infsec.secureumlgui.modelmapping.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.foundation.core.UmlClass;

import ch.ethz.infsec.secureumlgui.ModuleController;
import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectModelMapper;
import ch.ethz.infsec.secureumlgui.transformation.ModelMap;
import ch.ethz.infsec.secureumlgui.wrapper.PolicyWrapper;

public class HierarchicalPolicyExplorer {

    private static Logger aLog = Logger.getLogger(HierarchicalPolicyExplorer.class);


    private static HierarchicalPolicyExplorer INSTANCE;

    static {
        INSTANCE = new HierarchicalPolicyExplorer();
    }

    public static HierarchicalPolicyExplorer getInstance() {
        return INSTANCE;
    }

    private HierarchicalPolicyExplorer() {

    }

    //private PolicyWrapper defaultPolicy;
    private UmlClass defaultPolicy;

    public UmlClass getDefaultPolicy() {
        aLog.debug("getDefaultPolicy");
        if ( defaultPolicy == null ) {
            ModuleController moduleController = ModuleController.getInstance();
            if ( moduleController != null ) {

                Set<PolicyWrapper> unrefined = getUnrefinedPolicies();
                aLog.debug("got " + unrefined.size() + " unrefined policies");

                if ( unrefined != null && unrefined.size() > 0) {
                    PolicyWrapper pol = unrefined.iterator().next();
                    aLog.debug("First name: " + pol.getName());
                    if ( unrefined.size() == 1 && pol.getName().equals(SecureUmlConstants.DEFAULT_POLICY_NAME)) {
                        aLog.debug("default policy already defined");
                        //pol.get
                        defaultPolicy = (UmlClass) ModuleController.getInstance().getModelMap().getUmlElement(pol.getModelElement());
                    }
                }

                if  ( defaultPolicy == null ) {
//					Namespace nm;
//					if ( unrefined != null && unrefined.size() > 0) {
//						nm = ((UmlClass) unrefined.iterator().next().getModelElement()).getNamespace();
//					} else {
//						nm = GenericDialectModelMapper.getInstance().getInitNamespace();
//					}

                    try {
                        aLog.debug("#############################################");
                        aLog.debug("START ModuleController.getInstance().createPolicy");
                        defaultPolicy =  ModuleController.getInstance().createPolicy(SecureUmlConstants.DEFAULT_POLICY_NAME, unrefined, GenericDialectModelMapper.getInstance().getSecUMLPackage());
                        //as long as the model object of defaultPolicy is null...
                        //defaultPolicy = null;
                        aLog.debug("END ModuleController.getInstance().createPolicy: " + defaultPolicy);
                        aLog.debug("#############################################2");

                    } catch(Exception e) {
                        aLog.error("error at creating default policy: " + e.getClass().toString() + ": " + e.getMessage(), e);
                    }
                }
            } else
                aLog.debug("ModuleController.getInstance() == null");
        }
        if (aLog.isDebugEnabled()) {
            aLog.debug("getDefaultPolicy: " + (defaultPolicy == null ? "NULL" : defaultPolicy.getName()));
        }

        return defaultPolicy;
    }

//	public UmlClass getDefaultPolicy() {
//		aLog.debug("getDefaultPolicy");
//		if ( defaultPolicy == null ) {
//			ModuleController moduleController = ModuleController.getInstance();
//			if ( moduleController != null ) {
//
//				Set<PolicyWrapper> unrefined = getUnrefinedPolicies();
//				aLog.debug("got " + unrefined.size() + " unrefined policies");
//				if ( unrefined != null && unrefined.size() > 0) {
//					PolicyWrapper pol = unrefined.iterator().next();
//					aLog.debug("First name: " + pol.getName());
//					if ( unrefined.size() == 1 && pol.getName().equals(SecureUmlConstants.DEFAULT_POLICY_NAME)) {
//						aLog.debug("default policy already defined");
//						//pol.get
//						defaultPolicy = (UmlClass) ModuleController.getInstance().getModelMap().getUmlElement(pol.getModelElement());
//						//defaultPolicy = pol;
//					} else {
//						try {
//							aLog.debug("#############################################");
//							aLog.debug("START ModuleController.getInstance().createPolicy");
//							defaultPolicy =  ModuleController.getInstance().createPolicy(SecureUmlConstants.DEFAULT_POLICY_NAME, unrefined);
//							//as long as the model object of defaultPolicy is null...
//							//defaultPolicy = null;
//							aLog.debug("END ModuleController.getInstance().createPolicy: " + defaultPolicy);
//							aLog.debug("#############################################2");
//
//						} catch(Exception e) {
//							aLog.error("error at creating default policy: " + e.getClass().toString() + ": " + e.getMessage(), e);
//						}
//					}
//				} else {
//					aLog.error("TODO create default policy and get namespace from somewhere different... ");
//				}
//			} else
//				aLog.debug("ModuleController.getInstance() == null");
//		}
//		if (aLog.isDebugEnabled()) {
//			aLog.debug("getDefaultPolicy: " + (defaultPolicy == null ? "NULL" : defaultPolicy.getName()));
//		}
//
//		return defaultPolicy;
//	}

    public PolicyWrapper getDefaultPolicyWrapper() {
        return new PolicyWrapper(ModuleController.getInstance().getModelMap().getElement(getDefaultPolicy()));
    }




    private Set<PolicyWrapper> getUnrefinedPolicies() {
        ModuleController moduleController = ModuleController.getInstance();
        if (moduleController != null) {
            List<Object> allPolicies = moduleController.getAllPolicies();
            aLog.debug("found " + allPolicies.size() + " polices");

            Set<PolicyWrapper> policies = new HashSet<PolicyWrapper>();

            boolean default_exists = false;

            for ( Object policy : allPolicies) {
                PolicyWrapper policyWrapper = new PolicyWrapper(policy);
                Collection refinedBy = policyWrapper.getRefinedBy();
                if ( policyWrapper.getRefinedBy() == null || refinedBy.size() == 0 ) {
                    policies.add(policyWrapper);
                }
                if (SecureUmlConstants.DEFAULT_POLICY_NAME.equals(policyWrapper.getName())) {
                    default_exists = true;
                    if ( policyWrapper.getRefinedBy() != null && policyWrapper.getRefinedBy().size() > 0) {
                        aLog.error("INVALID DEFAULT POLICY! Policy with name " + SecureUmlConstants.DEFAULT_POLICY_NAME + " must not have a refining policy: " + policyWrapper.getRefinedByWrappers().iterator().next().getName() + "!");
                    }
                }

            }
            if ( default_exists && policies.size() != 1) {
                aLog.error("INVALID DEFAULT POLICY! Policy with name " + SecureUmlConstants.DEFAULT_POLICY_NAME + " must be the only one not being refined!");
            }
            return policies;
        } else {
            return null;
        }
    }

    //public List<PolicyWrapper> getSortedPolicies() {
    public List<UmlClass> getSortedPolicies() {

        PolicyWrapper defaultPolicyWrapper = getDefaultPolicyWrapper();


        //list which contains the end result
        List<PolicyWrapper> sortedPolicies = new ArrayList<PolicyWrapper>();
        //hash set for fast lookup, if a policy is already inserted
        Set<PolicyWrapper> alreadyInserted = new HashSet<PolicyWrapper>();

        if (defaultPolicyWrapper == null) {
            aLog.debug("no default policy found... adding unrefined policies");

            //hack... currently the adding of the policy requires a reload.. should be eleminated sooner or later..
            alreadyInserted = getUnrefinedPolicies();

            sortedPolicies.add(null);

            for (PolicyWrapper pol : alreadyInserted) {
                sortedPolicies.add(pol);
            }
        } else {
            sortedPolicies.add(defaultPolicyWrapper);
            alreadyInserted.add(defaultPolicyWrapper);//?? needed?
        }


        int start, end;
        end = 0;
        boolean added = true;
        boolean allIn;

        //as long as a policy has been added, restart a new round
        while (added) {
            // do only bother the policies inserted in the last round
            start = end;
            end = sortedPolicies.size();
            added = false;
            //clear all marks

            // for every policy in the last round
            for (int i = start; i < end; ++i) {
                //get the policies which refine this policy
                Collection<PolicyWrapper> refinesPolicies = sortedPolicies.get(i).getRefinesWrappers();
                if ( refinesPolicies != null && refinesPolicies.size() > 0 ) {
                    //for all refining polices
                    for ( PolicyWrapper refines : refinesPolicies) {
                        //if the policy only is refined by one policy, we can add it
                        if ( refines.getRefinedBy().size() == 1) {
                            sortedPolicies.add(refines);
                            added = true;
                        } else {
                            allIn = true;
                            for ( PolicyWrapper supPol : refines.getRefinedByWrappers() ) {
                                if ( ! alreadyInserted.contains(supPol) ) {
                                    allIn = false;
                                }
                            }
                            if ( allIn ) {
                                sortedPolicies.add(refines);
                                added = true;
                            }
                        }
                    }
                }
            }
            //last, we add all this round added policies in the "lookup" set:
            //we cannot add them during the main loop, as this would result in wrong loopups
            //      L0
            //   /  |  \
            // L1  L2   L3
            // |    |   |
            // L4   |   L5
            //  \  /
            //   L6
            //should result in L0 -> L1 -> L2 -> L3 -> L4 -> L5 -> L6
            // if we add L4 to alreadyInserted in the loop, the lookup for L6 says, that all
            // refinedBy policies are already inserted, i.e., L6 is inserted (before L3)
            // resulting in L0 -> L1 -> L2 -> L3 -> L4 -> L6 -> L5
            if ( added ) {
                for ( int i = end; i < sortedPolicies.size(); ++i ) {
                    alreadyInserted.add(sortedPolicies.get(i));
                }
            }
        }
        aLog.debug("getSortedPolicies: return " + sortedPolicies.size() + " policies");

        //hack
        ModelMap map = ModuleController.getInstance().getModelMap();

        List<UmlClass> sortedPoliciesResolved = new ArrayList<UmlClass>();
        for ( PolicyWrapper policy : sortedPolicies) {
            sortedPoliciesResolved.add((UmlClass) map.getUmlElement(policy.getModelElement()));
        }

        return sortedPoliciesResolved;
    }


//	public static List<PolicyWrapper> getSortedPolicies() {
//
//		ModuleController moduleController = ModuleController.getInstance();
//
//		if ( moduleController == null ) {
//			aLog.error("Received null for ModuleController");
//			return new ArrayList<PolicyWrapper>();
//		}
//
//		List<Object> allPolicies = moduleController.getAllPolicies();
//		aLog.debug("found " + allPolicies.size() + " polices");
//
//		Set<PolicyWrapper> policies = new HashSet<PolicyWrapper>();
//
//		for ( Object policy : allPolicies) {
//			PolicyWrapper policyWrapper = new PolicyWrapper(policy);
//			if ( policyWrapper.getRefined_by() != null ) {
//				policies.add(policyWrapper);
//			}
//		}
//		aLog.debug("found " + policies.size() + " not beeing refined");
//
//
//
//		//TODO get default policy ?
//		//GenericDialectModelMapper.getInstance().getDefaultPolicyWrapper();
//
//		List<PolicyWrapper> sortedPolicies = new ArrayList<PolicyWrapper>();
//
//
//		for ( PolicyWrapper policy : policies ) {
//			sortedPolicies.add(policy);
//		}
//
//		int start, end;
//		end = 0;
//		boolean added = true;
//
//		while (added) {
//			start = end;
//			end = sortedPolicies.size();
//			for (int i = start; i < end; ++i) {
//				for ( PolicyWrapper refines : sortedPolicies.get(i).getRefinesWrappers()) {
//					if ( ! policies.contains(refines) ) {
//						sortedPolicies.add(refines);
//						policies.add(refines);
//						added = true;
//					}
//				}
//			}
//		}
//		return sortedPolicies;
//	}

}

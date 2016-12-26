package ch.ethz.infsec.secureumlgui;

import org.argouml.ui.*;
import org.omg.uml.foundation.core.ModelElement;
import org.argouml.model.Model;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.ui.TabModelTarget;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import java.util.LinkedHashMap;
import java.util.Map;

import ch.ethz.infsec.secureumlgui.gui.*;
import ch.ethz.infsec.secureumlgui.ModuleController;
import ch.ethz.infsec.secureumlgui.ResourceFilesManager;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelConst;
import ch.ethz.infsec.secureumlgui.wrapper.ResourceWrapper;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.ActionPermissionSet;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectHelper;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectModelMapper;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.AtomicActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.CompositeActionWrapper;
import ch.ethz.infsec.secureumlgui.transformation.ModelMap;
import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

// (JD) I'm trying to move only the needed parts from SecureUmlComponentContainer
// into this class. then probably only SecureUmlComponentManager has to be adjusted.

/**
 * Represents the SecureUML properties tab in the ArgoUML details pane.
 *
 * Also manages to select which SecureUML Component to show in the tab,
 * i.e., one of {@link SecureUmlComponent}, {@link SecureUmlRoleComponent},
 * {@link SecureUmlPermissionComponent}, depending on the type of the selected element.
 *
 * @version 1.0
 */
public class TabSecureUml
    extends AbstractArgoJPanel
    implements TabModelTarget
    // extends AbstractArgoJPanel
    //implements TabModelTarget, ActionListener, ListSelectionListener, ComponentListener
{

    private Object target;
    private boolean shouldBeEnabled = false;

    private boolean hack_init = false;

    public TabSecureUml()
    {
        this.setLayout(new BorderLayout());
    }


    /**
     * show the given component in the SecureUML properties tab.
     *
     * the previously shown component is hidden.
     * @param comp the component to be shown.
     */
    public void setComponent(AbstractSecureUmlComponent comp) {
        this.removeAll();
        if(comp != null) {
            this.add(comp,BorderLayout.CENTER);
        }
        this.validate();
        this.repaint();
    }

    public void setEnabled(boolean enabled) {
        shouldBeEnabled = enabled;
    }

    private Map<ResourceType, AbstractSecureUmlComponent> secureUmlComponents
        = new LinkedHashMap<ResourceType, AbstractSecureUmlComponent>();

    MultiContextLogger logger = new MultiContextLogger(MultiContextLogger.GUI);

    public AbstractSecureUmlComponent getSecureUMLComponent(String stereotype)
    {
        if(stereotype == null)
            return null;
        else
            return secureUmlComponents.get(stereotype);
    }

    public void registerSecureUmlComponent(ResourceType resourceType,
                                           AbstractSecureUmlComponent suComponent)
    {
        if(resourceType == null)
        {
            logger.error("stereotype == null @ SecureUMLComponentContainer.registerSecureUMLComponent");
        }
        else
            secureUmlComponents.put(resourceType, suComponent);
    }

    public void registerSecureUmlComponent(ResourceType resourceType)
    // TODO: change s.t. modelElements which are Resources,
    //        but not directly stereotyped are supported, too
    {
        registerSecureUmlComponent(resourceType, null);
    }


    //  // Target Events Handlers

    /**
     * Display the proper panel component for this resourcetype.
     *
     * This method is only called, if the newTarget
     * is a SecureUML Element
     *
     */
    public void onTargetSet(Object newTarget, ResourceType rt)
    {
        if (newTarget instanceof ModelElement)  {
            ModelElement me = (ModelElement) newTarget;


            AbstractSecureUmlComponent suComponent = null;
            if(me != null
                    && me.getName() != null
                    && me.getName().length()>0
                    && rt != null)            {
                suComponent = secureUmlComponents.get(rt);
            }

            if(suComponent != null) {
                Object suElement = ModelMap.getDefault().getElement(me);
                if(suElement != null) {
                    suComponent.setDisplayedSecureUmlElement(suElement, rt);
                    this.setComponent(suComponent);
                }
            }
            else {
                logger.info("no SecureUML Component found " +
                            "for ResourceType: " + rt);
                this.setComponent(null);
            }
        }
        else logger.error("new Target is not an ArgoUML ModelElement: "
                              + newTarget);
    }


    public Object getTarget() {
        return target;
    }

    public void refresh() {
        setTarget(target);
    }

    public void setTarget(Object target) {
        if (!(Model.getFacade().isAModelElement(target))) {
            this.target = null;
            shouldBeEnabled = false;
            return;
        }

        this.target = target;
        shouldBeEnabled = false;


    }

    public boolean shouldBeEnabled() {
        return shouldBeEnabled;
    }

    public boolean shouldBeEnabled(Object target) {
        boolean shouldBeEnabled=false;
        if (target instanceof ModelElement) {
            if (Model.getFacade().isAModelElement(target))
                shouldBeEnabled=isSecureUmlElement((ModelElement)target);
        }
        this.shouldBeEnabled=shouldBeEnabled;
        return shouldBeEnabled;
    }

    public void onTargetSet()
    {
        onTargetSet(target);
    }

    /**
     * Find out the type of the newTarget (Role, Permission, or Resource), and act accordingly.
     *
     * @param newTarget
     */
    public void onTargetSet(Object newTarget)
    {
        // logger.info("executing SecureUmlComponentManager.onTargetSet()");// + newTarget + ")");

        if(GenericDialectModelMapper.getInstance()==null) return;
        target = newTarget;

        if(newTarget == null)
            return;

        String newTargetClassName = newTarget.getClass().getSimpleName()
                                    .split(MetaModelConst.MDR_IMPL_SUFFIX_REGEXP)[0];



        if (newTarget instanceof ModelElement) {
            ModelElement me = (ModelElement) newTarget;

            // don't handle unnamed Elements
            if(me.getName() == null || me.getName().length() == 0) {
                logger.info("unnamed model element");
                onTargetSet(newTarget, null);
            } else { // if Element != null and named
                logger.info("model element: " + me.getName());

                try {
                    ResourceType targetResourceType = GenericDialectHelper.getInstance().getSecureUmlType(me);
                    if(targetResourceType != null ) {
                        logger.info("targetResourceType: " + targetResourceType.getName());
                        ModuleController.getInstance().initModelMapper();
                        Object secureUmlElement = ModuleController.getInstance().transform(me);

                        if(secureUmlElement == null) {
                            logger.error("new target could not be mapped");
                            return;
                        }
                    } else {
                        logger.info("Could not find ResourceType for " + me);
                        if ( ! hack_init && !me.getName().contains("untitled")) { //untitledModel
                            hack_init = true;
                            GenericDialectModelMapper.getInstance().transform(me);
                        }

                    }

                    onTargetSet(newTarget, targetResourceType);
                }

                catch (Exception ex)  {
                    //secureumlComponent.setErrorMessage(ex.getMessage());
                    logger.logException(ex);
                }
            }
        }
    }


    public boolean isSecureUmlElement(ModelElement me) {
        return GenericDialectHelper.getInstance().isSecureUmlRole(me)
               || GenericDialectHelper.getInstance().isSecureUmlPermission(me)
               || GenericDialectHelper.getInstance().getResourceType(me) != null;
    }

    public void targetSet(TargetEvent e) {
        onTargetSet(e.getNewTarget());
    }

    public void targetAdded(TargetEvent e)
    {
        //logger.info("** target Added");

        try
        {
            if(e != null && e.getAddedTargetCollection() != null
                    && e.getAddedTargetCollection().size()>0)
            {
                Object addedTarget =
                    e.getAddedTargetCollection().iterator().next();
                onTargetSet(addedTarget);
            }
            else
            {
                logger.error("added target = null");
                onTargetSet(null);
            }
        }
        catch (Exception ex)
        {
            logger.error("** targetAdded: AddedTargetCollection is empty!");
        }


    }


    public void targetRemoved(TargetEvent e)
    {
        if(e != null)
        {
            Object newTarget = e.getNewTarget();
            //FIXME: and now?
        }
    }
}

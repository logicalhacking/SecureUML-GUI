/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectHelper;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ActionType;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;

/**
 *
 */
public class ActionNameTableCellRendererComponent extends JPanel
{
    /**
     *
     */
    public ActionNameTableCellRendererComponent(ActionWrapper actionWrapper)
    {


        JLabel lbActionName =
            new JLabel(actionWrapper.getName());
        add(lbActionName);

        String tooltip = "";
        //txActionName.setToolTipText();//"Action Composition Info..."

        ActionType at = GenericDialectHelper.getInstance().
                        getActionType(actionWrapper.getModelElement());

        if(at == null)
        {
            tooltip = "couldn't determine actiontype";
        }
        else if(at.getSubactionsDefinition() == null
                || at.getSubactionsDefinition().length() == 0)
        {
            tooltip = "Atomic Action";
        }
        else
        {
            tooltip = "Composite Action";// ("
            //   + at.getSubactionsDefinition() + ")";

            Collection<ActionWrapper> subactions =
                actionWrapper.getSubActionWrappers();

            if(subactions.size() == 0)
            {
                //tooltip += "with no Subactions";
            }
            else
            {
                if(subactions.size() == 1)
                    tooltip += "\n - Subaction (";
                else
                    tooltip += "\n - Subactions (";

                for (Iterator iter = subactions.iterator(); iter.hasNext();)
                {
                    ActionWrapper aw = (ActionWrapper) iter.next();

                    tooltip += //"\n" +
                        aw.getResourceWrapper().getResourcePath()
                        + "." +aw.getName();

                    if(iter.hasNext())
                        tooltip += ", ";
                }
                tooltip += ")";
            }
        }

        Collection superactions =
            actionWrapper.getSuperActionWrappers();

        if(superactions.size() == 0)
        {
            //tooltip += "with no Superactions";
        }
        else
        {
            if(superactions.size() == 1)
                tooltip += "\n - Superaction (";
            else
                tooltip += "\n - Superactions (";

            for (Iterator iter = superactions.iterator(); iter.hasNext();)
            {
                ActionWrapper aw = (ActionWrapper) iter.next();

                tooltip += //"\n" +
                    aw.getResourceWrapper().getResourcePath()
                    + "." +aw.getName();

                if(iter.hasNext())
                    tooltip += ", ";
            }
            tooltip += ")";
        }


//    logger.info("set action tooltip: " + tooltip);
//
        setToolTipText(tooltip);

        //return txActionName;

    }

    @Override
    public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip();

        tip.setComponent(this);
        return tip;
    }
}

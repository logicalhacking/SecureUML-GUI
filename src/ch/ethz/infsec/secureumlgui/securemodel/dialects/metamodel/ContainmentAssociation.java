/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
@Deprecated
public class ContainmentAssociation extends InterResourceAssociation
{
    /**
     *
     */
    public ContainmentAssociation(String name,
                                  AssociationEnd containerEnd, AssociationEnd contentsEnd)
    {
        super(name, containerEnd, contentsEnd);


        this.setName(name);

        this.containerEnd = containerEnd;
        this.contentsEnd = contentsEnd;
    }

    private AssociationEnd containerEnd;

    public AssociationEnd getContainerEnd()
    {
        return containerEnd;
    }

    public void setContainerEnd(AssociationEnd containerEnd)
    {
        this.containerEnd = containerEnd;
    }

    private AssociationEnd contentsEnd;

    public AssociationEnd getContentsEnd()
    {
        return contentsEnd;
    }

    public void setContentsType(AssociationEnd contentsEnd)
    {
        this.contentsEnd = contentsEnd;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String string =
            getContainerEnd().getType().getName()

            + getContainerEnd().toString()
            +  " ----"
            + getName()
            + "----> "
            + getContentsEnd().toString()

            + getContentsEnd().getType().getName();

        return string;
    }
}

/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel;

/**
 *
 */
public class InterResourceAssociation extends MetaModelAssociation
{
    /**
     *
     */
    public InterResourceAssociation(String name, AssociationEnd end1, AssociationEnd end2)
    {
        super(name);

        this.end1 = end1;
        this.end2 = end2;

        end1.setOwner(this);
        end2.setOwner(this);

    }



    private AssociationEnd end1;

    public AssociationEnd getEnd1()
    {
        return end1;
    }

    public void setEnd1(AssociationEnd end1)
    {
        this.end1 = end1;
        end1.setOwner(this);
    }

    private AssociationEnd end2;

    public AssociationEnd getEnd2()
    {
        return end2;
    }

    public void setEnd2(AssociationEnd end2)
    {
        this.end2 = end2;
        end2.setOwner(this);
    }

    /**
     *
     * @param mmClass
     * @return the Association End which is
     *   not associated to mmEntity or 'null' if
     *   mmEntity is not an anchor of this Association
     */
    public AssociationEnd getOtherEnd(MetaModelClass mmClass)
    {
        if(end1.getType() == mmClass)
            return end2;
        else if(end2.getType() == mmClass)
            return end1;
        else
            return null;
    }

    public String toString()
    {
        String string =
            getEnd1().getType().getName()

            + getEnd1().toString()
            +  " ----"
            + getName()
            + "---- "
            + getEnd2().toString()

            + getEnd2().getType().getName();

        return string;
    }

}

/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

/**
 *
 *  access of a property (Attribute or AssociationEnd)
 *  through the '.' Operator
 */
public class PropertyAccessStep extends PathStep
{
    /**
     *
     */
    public PropertyAccessStep(String propertyName)
    {
        this.propertyName = propertyName;
    }


    private String propertyName;

    // the value of the step
    // - i.e. name of the Property to access
    public String getPropertyName()
    {
        return propertyName;
    }

    public void setPropertyName(String value)
    {
        this.propertyName = value;
    }

    private int repetition;

    /** repetition: specifies how many time, this step
     * is to be repeated.
     * n = 0, 1, 2, ... stands for n repetitions,
     * n = -1 stands for (0 - \infinity) repetitions
     *
     * @return the number of repitions
     */
    public int getRepetition()
    {
        return repetition;
    }

    public void setRepetition(int repetition)
    {
        this.repetition = repetition;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "PropertyAccessStep("
               + propertyName
               + ((repetition == -1)?"*":("*"+repetition))
               + ")";
    }

}

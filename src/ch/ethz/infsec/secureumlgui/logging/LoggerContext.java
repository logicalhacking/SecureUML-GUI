/**
 *
 */
package ch.ethz.infsec.secureumlgui.logging;

/**
 *
 */
public class LoggerContext
{
    public static final LoggerContext other = new LoggerContext("other", "other");

    public LoggerContext(String longName, String shortName)
    {
        this.longName = longName;

        this.shortName = shortName;
        //this.enabled = true;
    }

//    public LoggerContext(String longName, String shortName, boolean enabled)
//    {
//        this.longName = longName;
//        this.shortName = shortName;
//        //this.enabled = enabled;
//    }


    String longName;
    public String getLongName()
    {
        return longName;
    }
//    public void setLongName(String longName)
//    {
//        this.longName = longName;
//    }

    String shortName;
    public String getShortName()
    {
        return shortName;
    }
//    public void setShortName(String longName)
//    {
//        this.longName = longName;
//    }

    // moved to MultiContextLogger Class
//    boolean enabled;
//    public boolean isEnabled()
//    {
//        return enabled;
//    }
//    public void setEnabled(boolean enabled)
//    {
//        this.enabled = enabled;
//    }
}

package ch.ethz.infsec.secureumlgui.logging;

import java.util.HashSet;
import ch.ethz.infsec.secureumlgui.util.NotImplementedException;

public class MultiContextLogger extends SimpleLogger
{
    public MultiContextLogger()
    {
        super();

//        /* enabled by default */
//        enabledLoggers.add(MODELMAPPER);
//        enabledLoggers.add(MY_JMI_IMPL);
//        enabledLoggers.add(TARGET_EVENTS);
//        enabledLoggers.add(MODELMAP);
//        enabledLoggers.add(GUI);
    }
    public MultiContextLogger(LoggerContext context)
    {
        super();

        this.defaultContext = context;

    }

    public static MultiContextLogger getDefault()
    {
        return defaultLogger;
    }

    public static MultiContextLogger defaultLogger = new MultiContextLogger();


    /* Contexts */

    public static final LoggerContext STARTUP = new LoggerContext("Startup and Loading of Module", "Startup");
    public static final LoggerContext METAMODEL_PARSER =
        new LoggerContext("MetamodelParser", "MetamodelParser");

    public static final LoggerContext MODELMAPPER = new LoggerContext("Modelmapper", "ModelMapper");
    public static final LoggerContext MODELWRITER = new LoggerContext("Modelwriter", "ModelWriter");
    public static final LoggerContext ACTIONINSTANTIATOR = new LoggerContext("ActionInstantiator", "Actions");

    public static final LoggerContext MY_JMI_IMPL = new LoggerContext("My JMI Implementation", "JMIImpl");
    public static final LoggerContext TARGET_EVENTS = new LoggerContext("Target Events", "Target");
    public static final LoggerContext MODELMAP = new LoggerContext("SecureUml Model Map", "Modelmap");
    public static final LoggerContext GUI = new LoggerContext("Graphical User Interface (SecureModel Component)", "GUI");

    public static LoggerContext MODELMAPPER_DETAILLED =
        new LoggerContext(
        "Modelmapper Detailled Logging Information",
        "ModelMapperDetailed");


    private boolean debug = true;

    public boolean getDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        //this.debug = debug;
    }

    // disabling / enabling contexts
    HashSet<LoggerContext> disabledLoggers = new HashSet<LoggerContext>();

    public boolean isLoggerContextDisabled(LoggerContext context)
    {
        return disabledLoggers.contains(context);
    }


    public void enableLoggerContext(LoggerContext context)
    {
        disabledLoggers.remove(context);
    }

    public void disableLoggerContext(LoggerContext context)
    {
        //disabledLoggers.add(context);
    }


    /**
     * @param context
     *
     */
    private void logContext(LoggerContext context)
    {
        if(getGlobalCurrentContext() != context)
        {
            if(defaultContext == null)
            {
                setIndent(indent = " ");
            }
            else
            {
                setIndent("");
                log(" o ");// + getCurrentContext().getLongName() + " ");
            }

            if(context != null)
            {
                setIndent(indent = "");
                log("");
                log("-+ <<< " + context.getLongName() + " >>>");
                //System.out.println("");
                //setCurrentContext(context);
                setGlobalCurrentContext(context);
                setIndent(indent = " |- ");
                setAdditionalLineIndent(" |   ");
            }
        }
        else
        {
            ;
        }

    }


    /* Global Properties */

    private static LoggerContext globalCurrentContext = null;

    public static LoggerContext getGlobalCurrentContext()
    {
        return globalCurrentContext;
    }

    public static void setGlobalCurrentContext(LoggerContext globalCurrentContext)
    {
        MultiContextLogger.globalCurrentContext = globalCurrentContext;
    }

    /* Logger Properties */


    private LoggerContext defaultContext = null;
    public LoggerContext getDefaultContext()
    {
        return defaultContext;
    }
//    public void setCurrentContext(LoggerContext currentContext)
//    {
//        this.defaultContext = currentContext;
//    }

    //Set<LoggerContext> loggerContexts = new HashSet<LoggerContext>();

    /* standard logging */

    public void log(int type, LoggerContext context, String message)
    {
//        if(context != null && !isLoggerContextDisabled(context))
//        {
        logContext(context);
//
        super.log(type, message);
//        }
//
//        else
//            ;
    }

    public void log(int type, String message)
    {
        logContext(getDefaultContext());
        super.log(type, message);
    }

//    public void log(String message)
//    {
//        logContext(getCurrentContext());
//        super.log(message);
//    }

    public void info(String message)
    {
        //For release, just log warnings and errors, not informational messages.
        log(2,message);
    }
    public void warn(String message)
    {
        log(1,message);
    }
    public void error(String message)
    {
        log(0,message);
    }

    /* special debug logging */

    /** standard logging action when an Exception occurred
     *  (to call from 'catch' or 'finally' blocks)
     */
    public void logException(Exception e)
    {
        //if(debug)
        {
            try
            {
                e.printStackTrace();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /** log Method to call from unimplemented Methods
     *  (currently used from unimplemented Methods
     *  in my Implementations of the JMI Interfaces)
     */
    public void logUnimplemented()
    {
        try
        {
            throw new NotImplementedException();
        }
        catch (Exception e)
        {
            logException(e);
        }
    }

    public void logUnimplemented(LoggerContext context)
    {
        if(context != null && !isLoggerContextDisabled(context))
        {
            try
            {
                throw new NotImplementedException();
            }
            catch (Exception e)
            {
                log(ERROR, MY_JMI_IMPL, e.getStackTrace().toString());
            }
        }
        else
            ;
    }

    public void logCallstack()
    {
        try
        {
            throw new Exception();
        }
        catch (Exception e)
        {
            logException(e);
        }
    }


    /* shortcuts */


    public void info(LoggerContext context, String message)
    {
        if(context != null && !isLoggerContextDisabled(context))
        {
            //logContext(context);

            //            log(INFORMATIONAL, context, message);
        }

    }

    public void warn(LoggerContext context, String message)
    {
        if(context != null && !isLoggerContextDisabled(context))
        {
            //logContext(context);

            log(WARNING, context, message);
        }

    }

    public void error(LoggerContext context, String message)
    {
        if(context != null && !isLoggerContextDisabled(context))
        {
            //            logContext(context);

            log(ERROR, context, message);
        }

    }




}

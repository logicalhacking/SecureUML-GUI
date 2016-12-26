package ch.ethz.infsec.secureumlgui.logging;

import java.util.ArrayList;


public class SimpleLogger implements SimpleMessageSink
{
    public SimpleLogger()
    {
        ;
    }


    /* Types */
    public static final int ERROR = 0;
    public static final int WARNING = 1;
    public static final int INFORMATIONAL = 2;



    /* Logger Properties */

    protected boolean verbose = true;
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    public boolean isVerbose() {
        return verbose;
    }

    private int maxCharactersPerLine = 78;

    public int getMaxCharactersPerLine()
    {
        return maxCharactersPerLine;
    }

    //@require maxCharactersPerLine>10
    public void setMaxCharactersPerLine(int maxCharactersPerLine)
    {
        this.maxCharactersPerLine = maxCharactersPerLine;
    }


    protected String indent = "  ";
    protected String additionalLineIndent = " ";

    public String getAdditionalLineIndent()
    {
        return additionalLineIndent;
    }
    public void setAdditionalLineIndent(String additionalLineIndent)
    {
        this.additionalLineIndent = additionalLineIndent;
        if(additionalLineIndent == null)
            additionalLineIndent = "";
    }
    public String getIndent()
    {
        return indent;
    }
    public void setIndent(String indent)
    {
        this.indent = indent;
        if(indent == null)
            this.indent = "";
    }


    /* standard logging */

    protected void log(String message)
    {

        try
        {
            printString(message);
            //System.out.println(message);
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public void log(int type, String message)
    {
        String out_message = "";
        try
        {
            switch (type) {
            case 0:
                out_message += "[ERROR] " + message;
                break;
            case 1:
                out_message += "[WARN] " + message;
                break;
            case 2:
                out_message +=  "[INFO] " + message;
                break;
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }
        log(out_message);
    }


//    public void printString(String s)
//    {
//        try
//        {
//            if(s!=null)
//            {
//
//
//                if(s.length() <= maxCharactersPerLine)
//                {
//                    System.out.println(indent + s);
//                }
//                else
//                {
//
//                    while(s.length() > maxCharactersPerLine)
//                    {
//                        System.out.println(additionalLineIndent + s.substring(0, maxCharactersPerLine));
//                        s = s.substring(maxCharactersPerLine);
//                    }
//
//                    System.out.println(additionalLineIndent + s);
//                }
//
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

    // advanced multiline support
    public void printString(String s)
    {
        if(maxCharactersPerLine < 1)
            return;
        try
        {
            if(s!=null)
            {

                String[] lines = s.split("\n");

                //if(lines.length > 1)
                //System.out.println("@@@@@ logging " + lines.length + " lines");

                String firstline = lines[0];
                int startindex = 0;
                // print first line - with original indent
                if(firstline.length() <= maxCharactersPerLine)
                {
                    System.out.println(indent + firstline);
                    startindex = 1;
                }
                else
                {
                    System.out.println(indent + firstline.substring(0, maxCharactersPerLine));
                    lines[0] = firstline.substring(maxCharactersPerLine);
                    startindex = 0;
                }

                // print other lines with empty indent
                // one longer than the orignial indent
                for (int i = startindex; i < lines.length; i++)
                {
                    String line = lines[i];
                    while(line.length() > maxCharactersPerLine)
                    {
                        System.out.println(additionalLineIndent + line.substring(0, maxCharactersPerLine));
                        line = line.substring(maxCharactersPerLine);
                    }

                    System.out.println(additionalLineIndent + line);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // advanced multiline support
//    public void printString(String s)
//    {
//        try
//        {
//            if(s!=null)
//            {
//                System.out.println(s);
//                ArrayList<String> lines = split(s, '\n');
//                lines = split(s, '\r');
//
//                if(lines.size() > 1)
//                    System.out.println("@@@@@ logging " + lines.size() + " lines");
//
//                String firstline = lines.get(0);
//                // print first line - with original indent
//                if(firstline.length() <= maxCharactersPerLine)
//                {
//                    System.out.println(indent + firstline);
//                }
//                else
//                {
//                    System.out.println(indent + s.substring(0, maxCharactersPerLine));
//                    firstline = firstline.substring(maxCharactersPerLine);
//
//                    lines.set(0, firstline);
//                    // print other lines with empty indent
//                    // one longer than the orignial indent
//                    for (int i = 0; i < lines.size(); i++)
//                    {
//                        String line = lines.get(i);
//                        while(line.length() > maxCharactersPerLine)
//                        {
//                            System.out.println(additionalLineIndent + line.substring(0, maxCharactersPerLine));
//                            line = line.substring(maxCharactersPerLine);
//                        }
//
//                        System.out.println(additionalLineIndent + line);
//                    }
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

    /* shortcuts */

    public void info(String s) {
        if (verbose)
        {
            log(INFORMATIONAL, s);
        }
    }

    public void warning(String s) {
        log(WARNING, s);
    }

    public void error(String s) {
        log(ERROR, s);
    }

    public void error(Exception e) {
        log(ERROR, e.toString());

        try
        {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            // TODO: handle exception
            ex.printStackTrace();
        }
    }


    public ArrayList<String> split(String s, Character c)
    {
        ArrayList<String> lines = new ArrayList<String>();

        while(s.length() > 1 &&  s.indexOf(c)>=0)
        {

            if(s.indexOf(c) == 0)
            {
                s = s.substring(1);
            }
            else
            {
                String line = s.substring(0, s.indexOf(c));
                lines.add(line);

                s = s.substring(s.indexOf(c));
            }
        }
        lines.add(s);

        return lines;

    }


    /* simplemessagesink */

    public void processMessage(String s) {
        info(s);
    }

}

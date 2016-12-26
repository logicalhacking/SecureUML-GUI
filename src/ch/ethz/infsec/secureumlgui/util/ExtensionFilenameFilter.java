/**
 *
 */
package ch.ethz.infsec.secureumlgui.util;

import java.io.File;
import java.io.FilenameFilter;;

/**
 *
 */
public class ExtensionFilenameFilter implements FilenameFilter
{
    /**
     *
     */
    public ExtensionFilenameFilter(String fileExtension)
    {
        this.fileExtension = fileExtension;

        while(fileExtension.startsWith("\\."))
            fileExtension = fileExtension.substring(1);
    }

    String fileExtension;

    /* (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File dir, String name)
    {
        if(name.endsWith("." + fileExtension))
            return true;
        else
            return false;


    }
}

package ch.ethz.infsec.secureumlgui.main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

public class FileLoader {

    private static MultiContextLogger logger = new MultiContextLogger();

    public static File loadXmiFromArgoFile(String inputFile) {
        try {
            // load the .argo file
            File argoFile = loadFile(inputFile);
            // extract the xmi file
            String argoName = argoFile.getName();
            String baseName = argoName.substring(0, argoName.indexOf("."));
            File temp = File.createTempFile(baseName, ".xmi");
            temp.deleteOnExit();
            ZipFile argoZipFile = new ZipFile(argoFile);
            ZipEntry xmiFile = argoZipFile.getEntry(baseName + ".xmi");
            if (xmiFile != null) {
                argoZipFile.getInputStream(xmiFile);
                copyInputStream(argoZipFile.getInputStream(argoZipFile
                                .getEntry(baseName + ".xmi")),
                                new BufferedOutputStream(new FileOutputStream(temp)));
                argoZipFile.close();
                return temp;
            }
            logger
            .error("invalid argo file, does not contain a xmi file named "
                   + baseName + ".xmi");
            return null;
        } catch (IOException ioe) {
            System.err.println("Unhandled exception:");
            ioe.printStackTrace();
        }
        return null;
    }

    public static File loadFile(String fileName) {
        if (fileName == null || fileName.equals("")) {
            return null;
        }
        try {
            File file = new File(fileName);
            return file;
        } catch (Exception e) {
            logger.error("could not load " + fileName);
            return null;
        }
    }

    public static File createFile(String fileName) {
        try {
            File file = loadFile(fileName);
            file.createNewFile();
            return file;
        } catch (Exception e) {
            logger.error("could not load " + fileName);
            return null;
        }
    }

    private static void copyInputStream(InputStream in, OutputStream out)
    throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }

}

package ch.ethz.infsec.secureumlgui.main;

//import org.argouml.uml.reveng.ImportClassLoader;
import org.netbeans.mdr.handlers.ClassLoaderProvider;

import ch.ethz.infsec.secureumlgui.securemodel.SecureModelPackage;
//import ch.ethz.infsec.secureumlgui.securemodelimpl.SecureModelFactory;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

import org.argouml.application.Main;

public class ClassLoaderProviderImpl implements ClassLoaderProvider
{

    private MultiContextLogger logger = new MultiContextLogger(
        MultiContextLogger.STARTUP);


    public ClassLoaderProviderImpl() {
        super();
        logger.info("ClassLoaderProviderImpl constructed");
    }

    public ClassLoader getClassLoader()
    {

        //return ImportClassLoader.getSystemClassLoader();
        logger.info("*** ClassLoaderProvider invoked - returned Classlodader: "
                    + Main.class.getClassLoader());


        return Main.class.getClassLoader();
    }

    public Class defineClass(String className, byte[] classFile)
    {
        //return this.getClassLoader().defineClass(className, classFile, 0, classFile.length);
        // TODO Auto-generated method stub
        logger.info("*** defineClass called!");
        //return this.getClassLoader().defineClass(arg0, arg1, 0, arg0.length());
        return null;
    }

}

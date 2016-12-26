package ch.ethz.infsec.secureumlgui;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.omg.uml.foundation.core.Stereotype;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelConst;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

/**
 * Misc. Utility methods.
 *
 * @version 1.0
 */
public class Util
{
    /**
     * Returns a capitalized string
     *
     * @param name the string to capitalize
     * @return the capitalized String
     */
    public static String capitalize(String name)
    {
        if(name != null && name.length() > 0)
            return name.substring(0, 1).toUpperCase()
                   + name.substring(1, name.length());
        else
            return name;
    }

    /**
     * @param type
     * @param methodName
     * @return the first Method of Type 'type' with name equals 'methodName'
     */
    public static Method findMethodByName(Class type, String methodName)
    {
        if(type != null && methodName != null)
        {
            Method[] methods = type.getMethods();

            for (int i = 0; i < methods.length; i++)
            {
                Method m = methods[i];

                if(methodName.equals(m.getName()))
                    return m;
            }
        }

        return null;
    }

    public static boolean isInstanceof(Class c, String parentClassName)
    {
        if(c == null
                || parentClassName == null
                || parentClassName.length()==0)
        {
            return false;
        }
        String className = c.getSimpleName();

        if(className.equals(parentClassName))
            return true;
        else
        {
            return isInstanceof(c.getSuperclass(), parentClassName);
        }
    }

    public static boolean hasType(Object modelElement, String className)
    {
        className += MetaModelConst.MDR_IMPL_SUFFIX;

        if (modelElement != null && className != null)
            return modelElement.getClass().getSimpleName().startsWith(className);
        else
            return false;
    }

    public static boolean hasStereotype(
        org.omg.uml.foundation.core.ModelElement element, String stereotype)
    {
        if (element == null || stereotype == null || stereotype.length() == 0)
            return false;

        Collection stereotypes = element.getStereotype();
        int nofStereotypes = stereotypes.size();

        if (stereotypes == null || stereotypes.size() == 0)
            return false;

        for (Iterator it = stereotypes.iterator(); it.hasNext();)
        {
            Stereotype s = (Stereotype) it.next();
            if (s.getName().equals(stereotype))
            {
                return true;
            }
        }
        return false;
    }

    public static Object invokeParameterlessMethod(Object object, String methodName)
    {
        try
        {
            java.lang.reflect.Method getter =
                object.getClass().getMethod(
                    methodName, new Class[0]);

            return getter.invoke(object, new Object[0]);
        }
        catch (Exception e)
        {
            logger.info("Problem invoking method '"
                        + methodName + "' on: " + object);
            Util.printInterfaces(object.getClass());

            logger.logException(e);
        }
        return null;
    }

    public static Object getProperty(Object object, String propertyName)
    {
        // exception is only logged if both trys fail
        Exception ex;
        if (object==null)
            logger.error("trying to get property "+propertyName+" from null object");
        try
        {
            //logger.info("trying get"+capitalize(propertyName));
            java.lang.reflect.Method getter = object.getClass().getMethod(
                                                  "get" + capitalize(propertyName), new Class[0]);

            return getter.invoke(object, new Object[0]);
        }
        catch (Exception e)
        {
            ex = e;
        }
        // boolean case
        try
        {
            //logger.info("trying is"+capitalize(propertyName));
            java.lang.reflect.Method getter = object.getClass().getMethod(
                                                  "is" + capitalize(propertyName), new Class[0]);

            return getter.invoke(object, new Object[0]);
        }
        catch (Exception e)
        {
            logger.info("Problem getting property '"
                        + propertyName + "' from: " + object);
            Util.printInterfaces(object.getClass());

            logger.logException(ex);
            logger.logException(e);
        }
        return null;
    }

    public static Object tryGetProperty(Object object, String propertyName)
    {
        try
        {
            java.lang.reflect.Method getter = object.getClass().getMethod(
                                                  "get" + capitalize(propertyName), new Class[0]);

            return getter.invoke(object, new Object[0]);
        }
        catch (Exception e)
        {
            //logger.logException(e);

            // case for boolean properties
            try
            {
                java.lang.reflect.Method getter = object.getClass().getMethod(
                                                      "is" + capitalize(propertyName), new Class[0]);

                return getter.invoke(object, new Object[0]);
            }
            catch (Exception ex)
            {
                //    logger.logException(e);
            }
        }
        return null;
    }

    /**
     * @param object
     * @param value
     * @param propertyName
     */
    public static void setProperty(Object object, String propertyName, Object value)
    {
        try
        {
            java.lang.reflect.Method getter = null;
            try
            {
                getter = object.getClass().getMethod(
                             "get" + capitalize(propertyName), new Class[0]);
            }
            catch (Exception e)
            {
            }
            if(getter == null)
            {
                try
                {
                    getter = object.getClass().getMethod(
                                 "is" + capitalize(propertyName), new Class[0]);
                }
                catch (Exception e)
                {
                    logger.logException(e);
                }
            }

            if(getter.getReturnType() == Collection.class
                    && getter.getParameterTypes().length == 0)
            {
                Collection collectionValue = (Collection)
                                             getter.invoke(object, new Object[0]);

                if(collectionValue != null)
                {
                    collectionValue.add(value);
                }
            }
            else
            {
                Class[] setterArgTypes = { getter.getReturnType() };

                java.lang.reflect.Method setter = object.getClass().getMethod(
                                                      "set" + capitalize(propertyName), setterArgTypes);

                Object[] setterArgs = { value };
                setter.invoke(object, setterArgs);
            }
        }
        catch(NoSuchMethodException e)
        {
            logger.error("Setter Method for Property '"
                         + propertyName
                         + "' not found: \n"
                         + "Available Methods: \n");
            Util.printInterfaces(object.getClass());

        }
        catch (Exception e)
        {
            logger.logException(e);
        }
    }

    static MultiContextLogger logger = MultiContextLogger.getDefault();

    /**
     * checks whether a file name exists and the file can be read.
     *
     * Aborts the program, if not.
     * @param filename the file name to check for
     * @return the File object of the given file name
     */
    public static File checkAndGetFile(String filename)
    {
        if(filename == null || filename.length() == 0)
        {
            logger.error("empty filename given for required file!" +
                         "  ...exiting");
        }
        else
        {
            File file =
                new File(filename);
            if(file.exists() && file.canRead())
            {
                return file;
            }
        }

        System.exit(1);

        // unreachable, but needed to compile
        return null;
    }

    public static void printInterfaces(Class cl)
    {
        if (cl != null)
        {

            Class[] interfaces = cl.getInterfaces();
            System.out
            .println(" - Interfaces of class '" + cl.getName() + " :");
            for (int i = 0; i < interfaces.length; i++)
            {
                System.out.println("i: " + interfaces[i].getName());

                for (int j = 0; j < interfaces[i].getMethods().length; j++)
                {
                    Method method = interfaces[i].getMethods()[j];

                    System.out.println("  m: " + method.toString());
                }
                //
            }
        }
    }



    public static void printJmiNamespace(javax.jmi.model.Namespace namespace, String prefix)
    {
        Collection packages = namespace.getContents();
        for (Iterator iter = packages.iterator(); iter.hasNext();)
        {
            javax.jmi.model.ModelElement element = (javax.jmi.model.ModelElement) iter.next();

            logger.info(prefix + element.getName());



            if (element instanceof javax.jmi.model.Namespace)
            {
                prefix = " " + prefix;

                javax.jmi.model.Namespace n = (javax.jmi.model.Namespace) element;
                printJmiNamespace(n, prefix);
            }
        }
    }
    public static void printJmiNamespace(javax.jmi.model.Namespace namespace)
    {
        printJmiNamespace(namespace, "");
    }


    /**
     * Adds all elements from the Collection addables to the Collection c - and
     * performs each add-operation in a seperate try-catch-statement
     *
     *
     * @param c
     *            a Collection
     * @param addables
     *            collection of Elements which are to be added to c
     */
    public static void addAllSave(Collection c, Collection addables)
    {
        if (c == null || addables == null)
            return;

        for (Iterator iter = addables.iterator(); iter.hasNext();)
        {
            try
            {
                Object o = (Object) iter.next();

                c.add(o);
            }
            catch (Exception e)
            {
                logger.logException(e);
            }
        }
    }


//    public static Stereotype findStereotypeByName(Collection availableStereotypes, String name, boolean loggingOn)
//    {
//        String stereotypeMessge = "searching Stereotype: " + name + " among: \n";
//
//        Stereotype result = null;
//
//        for (Iterator iter = availableStereotypes.iterator(); iter.hasNext();)
//        {
//            Stereotype stereotype = (Stereotype) iter.next();
//
//            if( stereotype.getName().equals(name))
//            {
//                stereotypeMessge += ">" + stereotype.getName() + "\n";
//
//                result = stereotype;
//            }
//            stereotypeMessge += stereotype.getName() + "\n";
//        }
//        if(loggingOn)
//            logger.info(logger.MODELWRITER,stereotypeMessge);
//
//        return result;
//    }

//    private static String permissionNamePrefix = "Permission";
//    private static String permissionNameSuffix = "Permission";
    private static int permissionNumber = 42;

    /* returns a number for a permission that is being created
     * (currenty strategy is to number'em ascending from 42)
     */
    public static int getNewPermissionNumber()
    {
        return permissionNumber++;
    }

    private static int roleNumber = 42;

    /* returns a number for a permission that is being created
     * (currenty strategy is to number'em ascending from 42)
     */
    public static int getNewRoleNumber()
    {

        return roleNumber++;
    }

}

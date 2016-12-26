/**
 *
 */
package ch.ethz.infsec.secureumlgui.util;

import java.lang.reflect.Method;

/**
 *
 */
public class Various
{

    public static void printInterfaces(Class cl)
    {
        if(cl != null)
        {

            Class[] interfaces = cl.getInterfaces();
            System.out.println(" - Interfaces of class '" + cl.getName() + " :");
            for (int i = 0; i < interfaces.length; i++)
            {
                System.out.println("i: "+  interfaces[i].getName());

                for (int j = 0; j < interfaces[i].getMethods().length; j++)
                {
                    Method method = interfaces[i].getMethods()[j];

                    System.out.println("  m: " +  method.toString());
                }
//
            }
        }

    }
}

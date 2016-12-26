package ch.ethz.infsec.secureumlgui.transformation;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.jmi.reflect.RefBaseObject;
import javax.jmi.reflect.RefObject;

import org.apache.log4j.Logger;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Stereotype;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

/**
 * stores the mapping from UML model elements to SecureUML dialect elements, and vice versa.
 *
 */

public class ModelMap
{
    /** Singleton, use getDefault
     *
     */
    private ModelMap()
    {

    }


    private static ModelMap instance = new ModelMap();

    private static Logger aLog = Logger.getLogger(ModelMap.class);

    public static ModelMap getDefault()
    {
        return instance;
    }

    private static MultiContextLogger logger = new MultiContextLogger();

    /** from UML elements to SecureUML elements */
    private Map<RefObject, Object> map = new HashMap<RefObject, Object>();

    /** from SecureUML elements to UML elements */
    private Map<Object, RefObject> reverseMap = new HashMap<Object, RefObject>();

//  private Set<RefObject> deletables = new HashSet<RefObject>();

    /**
     * @param elem UML-Modelelement
     * @return true, if the Map contains a mapping for @param elem
     *
     */
    public boolean mapContainsKey(RefObject elem)
    {
        return map.containsKey(elem);
    }

    /**
     * @param elem SecureUML Element
     * @return true, if the reverse map contains a mapping for elem
     */
    public boolean reverseMapContainsKey(Object elem)
    {
        return reverseMap.containsKey(elem);
    }

    /**
     * delete all contained Mappings
     *
     */
    public void clear()
    {
        //logger.info(logger.MODELMAP, "##### ModelMap cleared");
        map.clear();
        reverseMap.clear();

        actionMap.clear();
        stereotypeMap.clear();
    }

    /**
     * Adds a mapping (src -> target).
     * @param src - the new mapping source
     * @param target - the new mapping's target
     *
     *
     */
    public void put(RefObject src, Object target)
    {
        map.put(src, target);
        reverseMap.put(target, src);
        // too verbose
        //        logger.log(logger.INFORMATIONAL, logger.MODELMAP,
        //                "put Item to modelmap: " + src + "\n <-> \n" + target);

        aLog.debug("Modelmap.put(" + src.getClass() + " (" + (src instanceof Stereotype) + "), " + target.getClass() +" (" + (target instanceof Stereotype) + ")");

    }

//  public void addForDeletion(RefObject obj)
//  {
//    deletables.add(obj);
//  }

    /**
     * find the SecureUML extent element associated with the source UML object.
     *
     * @param umlElement
     * @return the found element
     */
    public Object getElement(RefObject umlElement)
    {
        if (!mapContainsKey(umlElement))
        {
            error(" (getElement) "+umlElement);
            return null;
        }
        else
        {
            return map.get(umlElement);
        }
    }

    /**
     * find the source UML object associated with the extent element
     *
     * @param suElement
     * @return the found element
     */
    public RefObject getUmlElement(Object suElement)
    {
        if (!reverseMapContainsKey(suElement))
        {
            error(" (getUmlElement) "+suElement);
            return null;
        }
        else
        {
            return reverseMap.get(suElement);
        }
//    for (RefObject o : map.keySet())
//    {
//      if ((map.get(o) != null) && (map.get(o).equals(abstractElement)))
//      {
//        return o;
//      }
//    }
//    error(abstractElement.toString());
//    return null;
    }

//  public Set<RefObject> getAllDeletableUmlElements()
//  {
//    return deletables;
//  }

    public Set<RefObject> getAllUmlElements()
    {
        return map.keySet();
    }

    public Collection<Object> getAllElements()
    {
        return map.values();
    }

    private void error(String elem)
    {
        //logger.error("requested element " + elem + " not mapped!");
        //logger.error("requested element not mapped");
    }

    public void printMap()
    {

        logger.info(logger.MODELMAP, toString());
    }

    public String toString()
    {

        String mapString = "################### MAP ##################### \n"
                           + " \n";

        for (Iterator iter = map.keySet().iterator(); iter.hasNext();)
        {
            mapString += "\n@ ";
            // add key String
            Object key = null;
            Object value = null;
            try
            {
                key = iter.next();
                if (key instanceof ModelElement)
                {
                    mapString += ((ModelElement) key).getClass().getSimpleName() + " "
                                 + ((ModelElement) key).getName();
                }
                else
                {
                    mapString += key.toString();
                }
            }
            catch (Exception e)
            {
                mapString += "## ERROR ## \n";
            }

            mapString += " -> ";

            // add value String
            try
            {
                value = map.get(key);
                Method getNameMethod = null;
                try
                {
                    getNameMethod = value.getClass().getMethod("getName", new Class[0]);

                    mapString += value.getClass().getSimpleName() + " "
                                 + (String) getNameMethod.invoke(value, new Object[0]);

                }
                catch (Exception e)
                {
                    mapString += key.toString();
                }
            }
            catch (Exception e)
            {
                mapString += "## ERROR ## \n";
            }

        }

        mapString += "\n\n################### REVERSE MAP ##################### \n";

        for (Iterator iter = reverseMap.keySet().iterator(); iter.hasNext();)
        {
            mapString += "\n@ ";
            // add key String
            Object key = null;
            Object value = null;
            try
            {
                key = iter.next();
                Method getNameMethod = null;
                //                  try
                //                  {
                getNameMethod = key.getClass().getMethod("getName", new Class[0]);

                mapString += key.getClass().getSimpleName() + " "
                             + (String) getNameMethod.invoke(key, new Object[0]);
                //                  }
                //                  catch (Exception e)
                //                  {
                //                      //mapString +=  key.toString();
                //                  }
            }
            catch (Exception e)
            {
                mapString += "## ERROR ## \n";
            }

            mapString += " -> ";

            //              // add value String
            //              try
            //              {
            //                  mapString += reverseMap.get(key).toString();
            //              }
            //              catch (Exception e)
            //              {
            //                  mapString += "## ERROR ## \n";
            //              }

            // add value String
            try
            {
                value = reverseMap.get(key);
                Method getNameMethod = null;
                //                  try
                //                  {
                getNameMethod = value.getClass().getMethod("getName", new Class[0]);

                mapString += value.getClass().getSimpleName() + " "
                             + (String) getNameMethod.invoke(value, new Object[0]);

                //                  }
                //                  catch (Exception e)
                //                  {
                //                      mapString +=  value.toString();
                //                  }
            }
            catch (Exception e)
            {
                mapString += "## ERROR ## \n";
            }
        }

        mapString += "#############################################";

        return mapString;

    }

    //        public String findPackage(ModelElement elem) {
    //            Namespace result = null;
    //            while (result == null) {
    //                result = elem.getNamespace();
    //                elem = (ModelElement) elem.refImmediateComposite();
    //            }
    //            if (elem instanceof Classifier) {
    //                result = elem.getNamespace();
    //            }
    //            return result.getName();
    //        }

//  @SuppressWarnings("unchecked")
//  public String findContext(ModelElement elem)
//  {
//    String result = "";
//    if (elem instanceof Operation)
//    {
//      Operation elemOp = (Operation) elem;
//
//      String ownerName = elemOp.getOwner().getName();
//      result += ownerName + "::" + elem.getName();
//      // operation signature
//      result += "(";
//      for (Parameter p : (Collection<Parameter>) elemOp.getInParametersA())
//      {
//        result += p.getName();
//        result += ":" + p.getType().getName();
//        result += ",";
//      }
//      if (result.endsWith(","))
//      {
//        result = result.substring(0, result.length() - 1);
//      }
//      result += ")";
//      result += ":" + elemOp.getReturnParameterA().getTypeA().getNameA();
//    }
//    else if (elem instanceof Transition)
//    {
//      tudresden.ocl20.core.jmi.uml15.statemachines.Transition transition = (tudresden.ocl20.core.jmi.uml15.statemachines.Transition) elem;
//      result = elem.getName();
//      StateMachine statemachine = transition.getStateMachine();
//      if (statemachine != null)
//      {
//        result = statemachine.getName() + "::" + result;
//      }
//      else
//      {
//        return result;
//      }
//      ModelElement context = statemachine.getContext();
//      if (context != null)
//      {
//        result = context.getName() + "::" + result;
//      }
//      return result;
//    }
//    else
//    {
//      logger.error("could not determine context for " + elem.getName());
//      result = null;
//    }
//    return result;
//  }

    /* extensions
     *
     *
     */


    /**  stores the action classes found in the UML model
     * (SimpleName->ActionClassifier).  Action classes are classes with
     * stereotype secuml.action. They are used in a SecureUML dialect
     * profile as type of the attribute of a permission association
     * class
     */
    Map<String, Classifier> actionMap = new LinkedHashMap<String, Classifier>();

    public Classifier getActionClass(String shortname)
    {
        if(shortname != null && actionMap.containsKey(shortname))
            return actionMap.get(shortname);
        else
        {
            logger.error("couldn't find Action Class for '"
                         + shortname + "' among " + actionMap.size()
                         + " actionClasses.");
            return null;
        }
    }

    public void putActionClass(String shortname, Classifier actionClass)
    {
        if(shortname != null && actionClass != null)
            actionMap.put(shortname, actionClass);
    }

    /** stores the stereotypes found in the UML model (stereotype name -> Stereotype).
     *  Stereotypes that are needed, but not found, are later created by us.
     */
    Map<String, Stereotype> stereotypeMap = new LinkedHashMap<String, Stereotype>();

    public Stereotype getStereotype(String name)
    {
        if(name != null && stereotypeMap.containsKey(name))
            return stereotypeMap.get(name);
        else
            return null;
    }

    public void putStereotype(Stereotype stereotype)
    {
        if(stereotype != null && stereotype.getName() != null)
            stereotypeMap.put(stereotype.getName(), stereotype);
    }



//{

//Map<ResourceType, Map<ActionType, Classifier>> actionMap =
//  new LinkedHashMap<ResourceType, Map<ActionType, Classifier>>();
//
//public Classifier getActionClass(ResourceType resourceType, ActionType actionType)
//{
//  if(actionMap.containsKey(resourceType))
//  {
//    Map<ActionType, Classifier> m = actionMap.get(resourceType);
//    if(m.containsKey(actionType))
//      return m.get(actionType);
//  }
//  return null;
//}

//public void putActionClass(ResourceType resourceType, ActionType actionType, Classifier actionClass)
//{
//  if(actionType != null && actionClass != null)
//  {
//    if(actionMap.containsKey(resourceType))
//    {
//      Map<ActionType, Classifier> m = actionMap.get(resourceType);
//
//      m.put(actionType, actionClass);
//    }
//    else
//    {
//      Map<ActionType, Classifier> m = new LinkedHashMap<ActionType, Classifier>();
//      m.put(actionType, actionClass);
//      actionMap.put(resourceType, m);
//    }
//  }
//  else
//    logger.error("Modelmap.putActionClass: null argument");
//}


}

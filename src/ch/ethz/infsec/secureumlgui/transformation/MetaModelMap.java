package ch.ethz.infsec.secureumlgui.transformation;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

//import javax.jmi.model.ModelElement;
//import javax.jmi.reflect.RefObject;

//import tudresden.ocl20.core.jmi.ocl.commonmodel.ModelElement;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelEntity;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.TaggedValue;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

public class MetaModelMap
{

    private MetaModelMap()
    {

    }

    private static MetaModelMap  instance = new MetaModelMap();
    public static MetaModelMap getDefault()
    {
        return instance;

    }

    protected MultiContextLogger logger = new MultiContextLogger(MultiContextLogger.MODELMAP);

    private Map<Object, MetaModelEntity> map = new HashMap<Object, MetaModelEntity>();

    private Map<MetaModelEntity, Object> reverseMap = new HashMap<MetaModelEntity, Object>();

    private static Logger aLog = Logger.getLogger(MetaModelMap.class);


    /**
     * @param elem UML-Modelelement
     * @return true, if the Map contains a mapping for @param elem
     *
     */
    public boolean containsMapping(Object elem)
    {
        return map.containsKey(elem);
    }

    /**
     * @param elem SecureUML Element
     */
    public boolean containsReverseMapping(MetaModelEntity elem)
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
    }

    /**
     * @param src - the new mapping source
     * @param target - the new mapping's target
     *
     * Adds a mapping (src -> target)
     */
    public void put(Object src, MetaModelEntity target) {
        map.put(src, target);
        reverseMap.put(target, src);
        // too verbose
        //aLog.debug("put Item to modelmap: " + src + "\n <-> \n" + target);
//        logger.log(logger.INFORMATIONAL, logger.MODELMAP,
//                "put Item to modelmap: " + src + "\n <-> \n" + target);
    }


    /* deletion stuff - taken from christian -
     * currently not in use, but there because
     * christian's modelmapper uses it
     */

    private Set<Object> deletables = new HashSet<Object>();

    public void addForDeletion(Object obj) {
        deletables.add(obj);
    }

    /**
     * finds the extent element associated with the source MOF object
     *
     * @param mofElementö
     * @return the found element
     */
    public MetaModelEntity getElement(Object mofElement) {
        if (!containsMapping(mofElement))
        {
            error(mofElement.toString());

            return null;
        }
        else
        {
            return map.get(mofElement);
        }
    }

    public Object getMofElement(MetaModelEntity entity) {
        if (!containsReverseMapping(entity))
        {
//           uncommented manually 57
//            (method is used to check whether the element is there
//             -> not to be logged as error!)
            if(entity == null)
                error("cannot get Value for Key 'null'");
            else
                error(entity.toString());

            return null;
        }
        else
        {
            return reverseMap.get(entity);
        }
    }

//	public Set<RefObject> getAllDeletableMofElements() {
//		return deletables;
//	}

    public Set<Object> getAllMofElements() {
        return map.keySet();
    }

    public Collection<MetaModelEntity> getAllElements() {
        return map.values();
    }



    /* helper Map (ModelElement<->TaggedValues) */

    Map<Object, Collection<TaggedValue>> modelElementTaggedValues = new HashMap<Object, Collection<TaggedValue>>();

    public Collection<TaggedValue> getModelElementTaggedValues(Object modelElement)
    {
        return modelElementTaggedValues.get(modelElement);
    }

    public TaggedValue getModelElementTaggedValue(Object modelElement, String tagName)
    {
        Collection<TaggedValue> taggedValues =
            modelElementTaggedValues.get(modelElement);

        if(taggedValues == null)
            return null;

        for (Iterator iter = taggedValues.iterator(); iter.hasNext();)
        {
            TaggedValue taggedValue = (TaggedValue) iter.next();

            if(taggedValue.getName().equals(tagName))
                return taggedValue;
        }
        return null;
    }

    public void putModelElementTaggedValue(Object modelElement, TaggedValue taggedValue)
    {
        if(!modelElementTaggedValues.containsKey(modelElement))
        {
            modelElementTaggedValues.put(modelElement, new LinkedList<TaggedValue>());
        }
        modelElementTaggedValues.get(modelElement).add(taggedValue);
    }




    private void error(String elem) {
        logger.error("requested element " + elem + " not mapped!"
                     + "(mapsize is: " + map.size() + " / " + reverseMap.size() + ")");

        aLog.error("requested element " + elem + " not mapped!"
                   + "(mapsize is: " + map.size() + " / " + reverseMap.size() + ")");
        //logger.logCallstack();
        //printMap();
    }

    public void printMap()
    {

        logger.info(MultiContextLogger.MODELMAP, toString());
    }

    public String toString()
    {

        String mapString =
            "################### MAP ##################### \n"
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
                //if(key instanceof ModelElement)
                {
                    mapString +=
                        (/*(ModelElement)*/key).getClass().getSimpleName()
                        + " ";
                    // + (/*(ModelElement)*/key).getName();
                }
                //else
                {
                    mapString +=  key.toString();
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
                    getNameMethod =
                        value.getClass().getMethod(
                            "getName", new Class[0]);

                    mapString +=
                        value.getClass().getSimpleName() + " "
                        + (String) getNameMethod.invoke(
                            value, new Object[0]);

                }
                catch (Exception e)
                {
                    mapString +=  key.toString();
                }
            }
            catch (Exception e)
            {
                mapString += "## ERROR ## \n";
            }

        }

        mapString +=
            "\n\n################### REVERSE MAP ##################### \n";

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
//                try
//                {
                getNameMethod =
                    key.getClass().getMethod(
                        "getName", new Class[0]);

                mapString +=
                    key.getClass().getSimpleName() + " "
                    + (String) getNameMethod.invoke(
                        key, new Object[0]);
//                }
//                catch (Exception e)
//                {
//                    //mapString +=  key.toString();
//                }
            }
            catch (Exception e)
            {
                mapString += "## ERROR ## \n";
            }

            mapString += " -> ";

//            // add value String
//            try
//            {
//                mapString += reverseMap.get(key).toString();
//            }
//            catch (Exception e)
//            {
//                mapString += "## ERROR ## \n";
//            }

            // add value String
            try
            {
                value = reverseMap.get(key);
                Method getNameMethod = null;
//                try
//                {
                getNameMethod =
                    value.getClass().getMethod(
                        "getName", new Class[0]);

                mapString +=
                    value.getClass().getSimpleName() + " "
                    + (String) getNameMethod.invoke(
                        value, new Object[0]);

//                }
//                catch (Exception e)
//                {
//                    mapString +=  value.toString();
//                }
            }
            catch (Exception e)
            {
                mapString += "## ERROR ## \n";
            }
        }


        mapString += "#############################################";

        return mapString;

    }



//	public String findPackage(ModelElement elem) {
//		Namespace result = null;
//		while (result == null) {
//			result = elem.getNamespace();
//			elem = (ModelElement) elem.refImmediateComposite();
//		}
//		if (elem instanceof Classifier) {
//			result = elem.getNamespace();
//		}
//		return result.getName();
//	}

//	@SuppressWarnings("unchecked")
//	public String findContext(ModelElement elem) {
//		String result = "";
//		if (elem instanceof Operation) {
//			Operation elemOp = (Operation) elem;
//
//			String ownerName = elemOp.getOwner().getName();
//			result += ownerName + "::" + elem.getName();
//			// operation signature
//			result += "(";
//
//                        /* TODO: start changed manually 18 */
//                        List params = elemOp.getParameter();
//                        List<Parameter> paramsTyped = new LinkedList<Parameter>();
//                        for (Iterator iter = params.iterator(); iter.hasNext();)
//                        {
//                            Object element = (Object) iter.next();
//                            paramsTyped.add((Parameter)element);
//                        }
//
//                        for (Parameter p : (Collection<Parameter>)
//                                paramsTyped) {
//                        /* end changed manually 18*/
//			//for (Parameter p : (Collection<Parameter>) elemOp
//			//		.getInParametersA()) {
//				result += p.getName();
//				result += ":" + p.getType().getName();
//				result += ",";
//			}
//			if (result.endsWith(",")) {
//				result = result.substring(0, result.length() - 1);
//			}
//			result += ")";
//			/* TODO: uncommented manually 19 */
//                        //result += ":" + elemOp.getReturnParameterA().getTypeA().getNameA();
//		} else if (elem instanceof Transition) {
//			org.omg.uml.behavioralelements.statemachines.Transition transition = (org.omg.uml.behavioralelements.statemachines.Transition) elem;
//			result = elem.getName();
//			StateMachine statemachine = transition.getStateMachine();
//			if (statemachine != null) {
//				result = statemachine.getName() + "::" + result;
//			} else {
//				return result;
//			}
//			ModelElement context = statemachine.getContext();
//			if (context != null) {
//				result = context.getName() + "::" + result;
//			}
//			return result;
//		}  else {
//			logger.error("could not determine context for " + elem.getName());
//			result = null;
//		}
//		return result;
//	}

}

/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel;

/**
 *
 */
public class MetaModelFactory
{
    /**
     *
     */
    private MetaModelFactory()
    {

    }

    private static MetaModelFactory instance;

    public static MetaModelFactory getInstance()
    {
        if(instance == null)
            instance = new MetaModelFactory();


        return instance;
    }


//    public ActionType createActionType(ActionKind actionKind)
//    {
//        if(actionKind == ActionKind.ATOMIC)
//            return new AtomicActionType();
//        else if(actionKind == ActionKind.COMPOSITE)
//            return new CompositeActionType();
//
//
//        else return null;
//    }

    public AtomicActionType createAtomicActionType()
    {
        return new AtomicActionType();
    }

    public CompositeActionType createCompositeActionType()
    {
        return new CompositeActionType();
    }

}

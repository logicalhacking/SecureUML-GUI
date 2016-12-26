package ch.ethz.infsec.secureumlgui.wrapper;

import ch.ethz.infsec.secureumlgui.Util;

public class ExceptionPermissionWrapper extends PermissionWrapper {

    public ExceptionPermissionWrapper(Object secureModelElementWrapper) {
        super(secureModelElementWrapper);
        // TODO Auto-generated constructor stub
    }

    public Object getExcpLevel()
    {
        Object roleObject = Util.getProperty(modelElement, "excpLevel");

        return roleObject;
    }

    public void setRole(Object excpLevel)
    {
        Util.setProperty(getModelElement(), "excpLevel", excpLevel);
    }


}

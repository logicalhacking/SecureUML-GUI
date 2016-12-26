package ch.ethz.infsec.secureumlgui.util;

public class ExcpPermissionDummy extends PermissionDummy {

    public ExcpPermissionDummy(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    //

    private Object excpLevel;

    public Object getExcpLevel() {
        return this.excpLevel;
    }

    public void setExcpLeve(Object excpLevel) {
        this.excpLevel = excpLevel;
    }

}

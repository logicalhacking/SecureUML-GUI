package ch.ethz.infsec.secureumlgui.modelmanagement;

public class ExtentInfo {

    private final String name;

    private final String topPackage;

    private final Class type;

    private final String metamodelName;

    public ExtentInfo(String name, String topPackage, String metamodelName, Class type) {
        this.name = name;
        this.topPackage = topPackage;
        this.type = type;
        this.metamodelName = metamodelName;
    }

    public String getName() {
        return name;
    }

    public String getTopPackage() {
        return topPackage;
    }

    public Class getType() {
        return type;
    }

    public String getMetaModelName() {
        return metamodelName;
    }

}

package ch.ethz.infsec.secureumlgui.modelmanagement;

public class OclModelInfo {

    public static enum MetaModelName {
        UML15, MOF14
    }

    private final String xmi;

    private final String name;

    private MetaModelName metaModelName;

    private String oclUri;

    public OclModelInfo(String xmi, String oclUri, String name, MetaModelName metaModelName) {
        this.xmi = xmi;
        this.name = name;
        this.metaModelName = metaModelName;
        this.oclUri = oclUri;
    }

    public String getName() {
        return name;
    }

    public String getXmi() {
        return xmi;
    }

    public MetaModelName getMetaModelName() {
        return metaModelName;
    }

    public String getMetaModelNameString() {
        switch (metaModelName) {
        case MOF14:
            return "MOF14";
        case UML15:
            return "UML15";
        }
        return null;
    }

    public String getOclUri() {
        return oclUri;
    }

}

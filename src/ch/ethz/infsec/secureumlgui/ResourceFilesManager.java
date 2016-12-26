package ch.ethz.infsec.secureumlgui;

import javax.swing.ImageIcon;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

/**
 * Provides access to the different icons needed by the SecureUML GUI.
 * The icons are loaded from the folder <code>IMAGE_FOLDER</code> in the jar file.
 * Note this makes it necessary, this class resides at the same point
 * in the directory hierarchy, as the <code>IMAGE_FOLDER</code>.
 *
 * @version 1.0
 */
public class ResourceFilesManager
{
    public static final String IMAGE_FOLDER = "icons/";

    MultiContextLogger logger =  MultiContextLogger.getDefault();

    private ImageIcon createRoleIcon;
    private ImageIcon constrainedIcon;
    private ImageIcon inheritedRoleIcon;
    private ImageIcon inheritedPolicyIcon;
    private ImageIcon implicitIcon;
    private ImageIcon compositeFullIcon;
    private ImageIcon implicitByCompositeIcon;

    public ResourceFilesManager()
    {
        initImageIcons();
    }

    /**
     * Loads the icons from the jar file. If an icon cannot be loaded,
     * the logger will report an error.
     */
    private void initImageIcons()
    {
        try {
            createRoleIcon = createImageIcon(IMAGE_FOLDER + "createRoleIcon.png","create Role");
            constrainedIcon = createImageIcon(IMAGE_FOLDER + "constrained_icon.png","constrained Permission");
            inheritedRoleIcon = createImageIcon(IMAGE_FOLDER + "inherited_icon.png","inherited Permission");
            inheritedPolicyIcon = createImageIcon(IMAGE_FOLDER + "inherited_policy_icon.png","inherited Permission");
            implicitIcon = createImageIcon(IMAGE_FOLDER + "implicit_icon.png","implicit Permission");
            compositeFullIcon = createImageIcon(IMAGE_FOLDER + "composite_full_icon.png","composite Permission");
            implicitByCompositeIcon = createImageIcon(IMAGE_FOLDER + "implicit_by_composite.png", "implicit by composite Permission");
        }
        catch(Exception e) {
            logger.error("Could not load image icons in folder: " + IMAGE_FOLDER);
        }

        if (createRoleIcon == null ||
                constrainedIcon == null||
                inheritedRoleIcon == null  ||
                implicitIcon == null   ||
                compositeFullIcon == null ||
                implicitByCompositeIcon == null) {
            logger.error("error creating icons");

        }

    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    private ImageIcon createImageIcon(String path,
                                      String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            logger.error("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * @return the compositeFullIcon
     */
    public ImageIcon getCompositeFullIcon()
    {
        return compositeFullIcon;
    }

    /**
     * @return the constrainedIcon
     */
    public ImageIcon getConstrainedIcon()
    {
        return constrainedIcon;
    }

    /**
     * @return the implicitIcon
     */
    public ImageIcon getImplicitIcon()
    {
        return implicitIcon;
    }

    public ImageIcon getImplicitByInheritedIcon() {
        return implicitByCompositeIcon;
    }


    /**
     * @return the inheritedIcon
     */
    public ImageIcon getInheritedRoleIcon()
    {
        return inheritedRoleIcon;
    }

    public ImageIcon getInheritedPolicyIcon() {
        return inheritedPolicyIcon;
    }

    /**
     * @return the createRoleIcon
     */
    public ImageIcon getCreateRoleIcon()
    {
        return createRoleIcon;
    }
}

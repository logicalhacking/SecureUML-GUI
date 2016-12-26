package ch.ethz.infsec.secureumlgui.usecasemapper.mapping;

import org.apache.log4j.Logger;

import org.argouml.model.Model;
import org.argouml.model.Facade;
import org.argouml.model.ExtensionMechanismsHelper;

import java.util.Collection;
import java.util.ArrayList;

/**
 * This class encapsulates SecureUML permissions.
 * It may be initialized by a sender, a message and a receiver.
 * The constructor then analyzes these parts and checks if it
 * is a SecureUML permission and if, if it is valid.
 * The authorization constraint is only calculated on demand.
 * This prevents an endless loop, if there is an error in the
 * predecessor and activator relationships of the messages in
 * a sequence diagram. In ArgoUML 0.24 sequence diagrams with more
 * than two messages are corrupt in the sense that there is no
 * message with no predecessors and activators, what is obviously
 * wrong, as the first message in a sequence diagram has neiher
 * a predecessor nor an activator.
 * If the authorization constraint would be calculated in the
 * {@link #initPermission() initPermission-routine}, the
 * {@link #buildAuthConstraint() buildAuthConstraint-routine}
 * would enter an endless loop during the analyzation of
 * a message of such a sequence diagram. As there will be always
 * a predecessor or an activator which has to be analyzed,
 * it will loop until a stack overflow.
 *
 * @version 1.1
 */
public class Permission {

    /**
     * The <code>log4j</code>-logger of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Permission.class);

    /**
     * Name constant for the action "create".
     */
    private static final String CREATE_NAME = "create";
    /**
     * Name constant for the action DELETE_NAME.
     */
    private static final String DELETE_NAME = "delete";
    /**
     * Name constant for the action EXECUTE_NAME.
     */
    private static final String EXECUTE_NAME = "execute";
    /**
     * Name constant for the action "read".
     */
    private static final String READ_NAME = "read";
    /**
     * Name constant for the action "update".
     */
    private static final String UPDATE_NAME = "update";

    /**
     * The name for the permission. Note if the name is
     * not set, the name returned from
     * {@link #getName() getName()} is built from the
     * name of the sender and the receiver of the initializing
     * message.
     */
    private String name = "";

    /**
     * The sender of the initializing message.
     */
    private Object sender;
    /**
     * The initializing message.
     */
    private Object message;
    /**
     * The receiver of the initializing message.
     */
    private Object receiver;

    /**
     * The additional constraints of this permission.
     */
    private final Collection constraints = new ArrayList();

    /**
     * The action of the initializing message.
     */
    private String action = "";
    /**
     * The operation of the action.
     */
    private String operation = "";
    /**
     * The resource class determined from the receiver.
     */
    private String resourceClass = "";
    /**
     * The resource member determined from the receiver.
     */
    private String resourceMember = "";
    /**
     * The resource class determined from the message
     * if specified.
     */
    private String resourceClassMsg = "";

    /**
     * The authorization constraint for this permission.
     * This constraint is built from the possible predecessors
     * and activators.
     */
    private String authConstraint = "";

    /**
     * The reasons as a String why this permission is
     * invalid or <code>null</code> if it is valid.
     */
    private String invalidityReasons = null;

    /**
     * The reasons as a String why the authorization constraint
     * of this permission is invalid or <code>null</code>
     * if it is valid.
     */
    private String authInvalidReason = null;

    /**
     * A reference to the ArgoUML facade for enhanced readability.
     */
    private final Facade facade = Model.getFacade();

    /**
     * Default constructor without parameters.
     * Needed if a permission should be shared for several messages
     * or receivers without having to create new permissions
     * each time.
     */
    public Permission() {
        /*
          Needed if a permission should be shared for several messages
          or receivers without having to create new permissions
          each time.
        */

    }

    /**
     * Constructor to initialize the permission with
     * a sender, a message and a receiver.
     *
     * @param aSender   The object being the sender of the given message.
     * @param aMessage  The object being the message used for initialization.
     * @param aReceiver The object being the receiver of the message.
     */
    public Permission(final Object aSender, final Object aMessage,
                      final Object aReceiver) {
        sender = aSender;
        message = aMessage;
        receiver = aReceiver;

        initPermission();
    }

    /**
     * Constructor to initialize the permission with
     * a sender, a message, a receiver and a collection of constraints.
     *
     * @param aSender   The object being the sender of the given message.
     * @param aMessage  The object being the message used for initialization.
     * @param aReceiver The object being the receiver of the message.
     * @param theConstraints The collection containing the constraints.
     */
    public Permission(final Object aSender, final Object aMessage,
                      final Object aReceiver,
                      final Collection theConstraints) {
        sender = aSender;
        message = aMessage;
        receiver = aReceiver;
        constraints.addAll(theConstraints);

        initPermission();
    }

    /**
     * Sets the name of this permission to a custom name.
     *
     * @param aName The string containing the name for this permission.
     */
    public final void setName(final String aName) {
        if (aName != null) {
            name = aName;
        }
    }

    /**
     * Gets the name of this permission. If no name has been
     * set, the name is constructed from the sender and the
     * receiver of the initializing message.
     * The method is <code>final</code> as it is called
     * during the instantiation of the permission object to
     * ensure proper creation.
     *
     * @return The string containing this permission's name.
     * @see #buildAuthConstraint
     */
    public final String getName() {
        if ("".equals(name)) {
            return facade.getName(sender) + facade.getName(receiver)
                   + "Perm";
        } else {
            return name;
        }
    }

    /**
     * Adds constraints to this permission.
     *
     * @param theConstraints A collection containing additional constraints.
     */
    public final void addConstraints(final Collection theConstraints) {
        constraints.addAll(theConstraints);
    }

    /**
     * Gets the sender of the initializing message.
     *
     * @return The sender of the initializing message.
     */
    public final Object getSender() {
        return sender;
    }

    /**
     * Sets the sender of the initial message. This is useful
     * if the permission is for an abstract actor and this
     * permission is later on dispatched to concrete actors.
     *
     * @param aSender The object containing the new sender.
     */
    public final void setSender(final Object aSender) {
        sender = aSender;
    }

    /**
     * Gets the initializing message.
     *
     * @return The initializing message.
     */
    public final Object getMessage() {
        return message;
    }

    /**
     * Sets the message of this permission.
     * This triggers the initialization during which the
     * permission is reanalyzed and checked.
     *
     * @param aMessage The object being the message.
     *
     * @see #initPermission()
     */
    public final void setMessage(final Object aMessage) {
        message = aMessage;
        initPermission();
    }

    /**
     * Gets the receiver of the initializing message.
     *
     * @return The receiver of the initializing message.
     */
    public final Object getReceiver() {
        return receiver;
    }

    /**
     * Sets the receiver of this permission.
     * This triggers the initialization during which the
     * permission is reanalyzed and checked.
     *
     * @param aReceiver The object being the new receiver of the message.
     *
     * @see #initPermission()
     */
    public final void setReceiver(final Object aReceiver) {
        receiver = aReceiver;
        initPermission();
    }

    /**
     * Gets the constraints of this permission.
     *
     * @return The constraints of this permission.
     */
    public final Collection getConstraints() {
        return new ArrayList(constraints);
    }

    /**
     * Adds a constraint to this permission.
     *
     * @param aConstraint The constraint being added to this permission.
     */
    public final void addConstraint(final Object aConstraint) {
        constraints.add(aConstraint);
    }

    /**
     * Set the constraints of this permission.
     *
     * @param theConstraints A collection being the new constraints.
     */
    public final void setConstraints(final Collection theConstraints) {
        constraints.clear();
        constraints.addAll(theConstraints);
    }

    /**
     * Clears the constraints of this permission.
     */
    public final void clearConstraints() {
        constraints.clear();
    }

    /**
     * Returns whether this permission is valid.
     * NOTE: Each permission not being a SecureUML Permission
     *       is valid by definition, as there are no further
     *       validity checks possible.
     *
     * @return <code>True</code> if this permission is valid.
     */
    public final boolean isValid() {
        return !isSecureUMLPermission() || invalidityReasons == null;
    }

    /**
     * Gets the reasons why a permission is invalid.
     * If the permission is valid, <code>null</code>
     * is returned.
     *
     * @return The string containing the invalidity reasons,
     *         or <code>null</code> if this permission is valid.
     */
    public final String getInvalidityReasons() {
        return invalidityReasons;
    }

    /**
     * Returns <code>true</code> if this is a
     * SecureUML permission.
     *
     * @return <code>True</code> if this is a SecureUML permission.
     */
    public final boolean isSecureUMLPermission() {
        return getAction() != null;
    }

    /**
     * Returns the authorization constraint of this permission.
     * The constraint is calculated on demand. To check whether
     * the calculation is necessary or not, the member variable
     * <code>authConstraint</code> is checked whether it is
     * still equal its initial value.
     *
     * @return The authorization constraint of this permission.
     */
    public final String getAuthConstraint() {
        if ("".equals(authConstraint)) {
            buildAuthConstraint();
        }
        return authConstraint;
    }

    /**
     * Returns whether the authorization constraint of this
     * permission is valid or not.
     * The constraint is calculated on demand. To check whether
     * the calculation is necessary or not, the member variable
     * <code>authConstraint</code> is checked whether it is
     * still equal its initial value.
     *
     * @return <code>True</code> if the authorization constraint
     *         of this permission is valid.
     */
    public final boolean isAuthValid() {
        if ("".equals(authConstraint)) {
            buildAuthConstraint();
        }
        return authInvalidReason == null;
    }

    /**
     * Gets the reason why the authorization constraint is invalid.
     * If this permission is valid <code>null</code> is returned.
     * The constraint is calculated on demand. To check whether
     * the calculation is necessary or not, the member variable
     * <code>authConstraint</code> is checked whether it is
     * still equal its initial value.
     *
     * @return The string containing the invalidity reasons,
     *         or <code>null</code> if this permission is valid.
     */
    public final String getAuthInvalidReason() {
        if ("".equals(authConstraint)) {
            buildAuthConstraint();
        }
        return authInvalidReason;
    }

    /**
     * Returns the resource of this permission.
     *
     * @return The string containing the complete resource of this permission.
     */
    public final String getResource() {
        if ("".equals(resourceMember)) {
            return resourceClass;
        } else {
            return resourceClass + "." + resourceMember;
        }
    }

    /**
     * Returns the permission class attribute for this permission.
     *
     * @return A string containing the description for a SecureUML
     *         permission class attribute for this permission.
     */
    public final String getPermAttribute() {
        return getResource() + ":" + action;
    }

    /**
     * Returns the error string if this permission is invalid
     * or if there is an error in the authorization constraint.
     *
     * @return A string containing the error message or <code>null</code>
     *        if there is no error.
     */
    public final String getErrorMessage() {
        if (isValid() && isAuthValid()) {
            return null;
        } else {
            final StringBuffer errMessage = new StringBuffer();
            errMessage.append("The permission:\n");
            errMessage.append(getName());
            errMessage.append("\nis invalid.\nThe permission has the sender:");
            errMessage.append(facade.getName(getSender()));
            errMessage.append("\nThe message:");
            errMessage.append(facade.getName(getMessage()));
            errMessage.append("\nThe receiver:");
            errMessage.append(facade.getName(getReceiver()));
            errMessage.append("\nThe error reason is:");

            if (!isValid()) {
                errMessage.append("\nThe permission is not valid:\n");
                errMessage.append(getInvalidityReasons());
            }
            if (!isAuthValid()) {
                errMessage.
                append("\nThe authorization constraint is invalid:\n");
                errMessage.append(getAuthInvalidReason());
            }

            return errMessage.toString();
        }
    }

    /**
     * Initializes this permission.
     * This method is called by the constructors of this class.
     * The member variable <code>authConstraint</code> is set
     * to its initial value "", as the constraint is calculated
     * on demand and triggered if the member is equal to
     * its intial value.
     */
    private void initPermission() {
        if (sender != null && receiver != null
                && message != null && getAction() != null) {
            analyzePermission();
            checkPermission();
            authConstraint = "";
        }
    }

    /**
     * Analyzes the permission and sets the member variables.
     * Looks at the message and the receiver of the message
     * for analyzation.
     */
    private void analyzePermission() {

        action = getAction();

        if (facade.isACallAction(facade.getAction(message))
                && facade.getOperation(facade.getAction(message)) != null) {
            operation = facade.
                        getName(facade.getOperation(facade.getAction(message)));
        }
        if (facade.getName(receiver) != null) {
            resourceClass = facade.getName(receiver);
        }
        if (facade.getName(message) != null
                && !resourceClass.equals(facade.getName(message))) {
            resourceMember = facade.getName(message);
        }
        if (resourceMember.indexOf('.') != -1) {
            resourceClassMsg = resourceMember.
                               substring(0, resourceMember.indexOf('.'));
            resourceMember = resourceMember.
                             substring(resourceMember.indexOf('.') + 1);
        }
        if (EXECUTE_NAME.equals(action) && "".equals(resourceMember)) {
            resourceMember = operation;
        }
    }

    /**
     * Checks this permission for correctness.
     *
     * @see #checkExecute()
     * @see #checkNonExistAttribute()
     * @see #checkClassActionOnAttr()
     */
    private void checkPermission() {
        final StringBuffer errorMsg = new StringBuffer(110);

        // class name in message does not match target
        if (!"".equals(resourceClassMsg)
                && !resourceClass.equals(resourceClassMsg)) {
            errorMsg.append("\nMessage target: ");
            errorMsg.append(resourceClassMsg);
            errorMsg.append("\nis different from:");
            errorMsg.append(resourceClass);
        }

        if (EXECUTE_NAME.equals(action)) {
            errorMsg.append(checkExecute());

        } else if (READ_NAME.equals(action) || UPDATE_NAME.equals(action)) {
            errorMsg.append(checkNonExistAttribute());

        } else if (CREATE_NAME.equals(action) || DELETE_NAME.equals(action)) {
            errorMsg.append(checkClassActionOnAttr());
        }

        if (errorMsg.length() > 0) {
            invalidityReasons = errorMsg.toString();
            LOGGER.error("Exception occured during predicate check:\n"
                         + errorMsg.toString());
        }
    }

    /**
     * Tests the validity of a "execute"-action.
     * Performs three tests:
     * 1. If the operation of the message's action is set,
     *    checks if the resource member equals the operation.
     * 2. If the resource member is set at all.
     * 3. If the resource member is a method.
     *
     * @return A string containing the error message or an empty
     *         string if everything is fine.
     */
    private String checkExecute() {
        final StringBuffer errorMsg = new StringBuffer(80);
        final Collection<String> methods = getMethods();

        // operation and resourceMember don't match
        if (!"".equals(operation) && !resourceMember.equals(operation)) {
            errorMsg.append("\nOperation does not match\n(");
            errorMsg.append(resourceMember);
            errorMsg.append(')');
        }
        if ("".equals(resourceMember)) {
            errorMsg.append("\nexecute must have method set\n"
                            + "either in the message or as an\noperation");
        }
        if (!methods.contains(resourceMember)) {
            errorMsg.append("execute must have a method as target\n(");
            errorMsg.append(resourceMember);
            errorMsg.append(')');
        }

        return errorMsg.toString();
    }

    /**
     * Tests for a attribute action on a non-existing attribute.
     * Returns an empty string if everything is fine or the
     * error message otherwise.
     *
     * @return A string containing the error message or an empty
     *         string if everything is fine.
     */
    private String checkNonExistAttribute() {
        final StringBuffer errorMsg = new StringBuffer();
        final Collection<String> attributes = getAttributes();

        if (!"".equals(resourceMember)
                && !attributes.contains(resourceMember)) {
            errorMsg.append("Access to non-existing attribute\n(");
            errorMsg.append(resourceMember);
            errorMsg.append(')');
        }
        return errorMsg.toString();
    }

    /**
     * Tests for a class action on a attribute.
     * For example a create or delete on a attribute.
     * Returns an empty String if everything is fine or
     * the error message otherwise.
     *
     * @return A string containing the error message or an empty
     *         string if everything is fine.
     */
    private String checkClassActionOnAttr() {
        final StringBuffer errorMsg = new StringBuffer();

        if (!"".equals(resourceMember)) {
            errorMsg.append("\ncreate and delete are not allowed\n"
                            + "for members (");
            errorMsg.append(resourceMember);
            errorMsg.append(')');
        }
        return errorMsg.toString();
    }

    /**
     * Determines the action of a message by looking at its stereotype.
     * Returns <code>null</code> if it is not a SecureUML action or
     * the action could not be determined.
     * Return messages are by default a READ_NAME.
     *
     * @return the string action the message symbolizes.
     */
    private String getAction() {
        final ExtensionMechanismsHelper helper = Model.
                getExtensionMechanismsHelper();

        String foundAction = null;

        if (message != null) {
            if (helper.hasStereoType(message, "isCreate")) {
                foundAction = CREATE_NAME;
            } else if (helper.hasStereoType(message, "isDelete")) {
                foundAction = DELETE_NAME;
            } else if (helper.hasStereoType(message, "isUpdate")) {
                foundAction = UPDATE_NAME;
            } else if (helper.hasStereoType(message, "isQuery")) {
                foundAction = READ_NAME;
            } else if (facade.isAReturnAction(facade.getAction(message))) {
                foundAction = READ_NAME;
            } else if (helper.hasStereoType(message, "isExecute")) {
                foundAction = EXECUTE_NAME;
            }
        }
        return foundAction;
    }

    /**
     * Gets the attibutes of a given target class as a collection of strings.
     * Only returns the names which could be resolved.
     *
     * @return A collection of strings containing the attribute names.
     */
    private Collection<String> getAttributes() {
        final Collection<String> attributes = new  ArrayList<String>();
        for (Object attribute : facade.getAttributes(receiver)) {
            if (facade.getName(attribute) != null) {
                attributes.add(facade.getName(attribute));
            }
        }
        return attributes;
    }

    /**
     * Gets the methods of a given target class as a collection of strings.
     * Only returns the names  which could be resolved.
     *
     * @return A collection of strings containing the method names.
     */
    private Collection<String> getMethods() {
        final Collection<String> methods = new ArrayList<String>();
        for (Object method : facade.getOperations(receiver)) {
            if (facade.getName(method) != null) {
                methods.add(facade.getName(method));
            }
        }
        return methods;
    }

    /**
     * Constructs the authorization sequence constraint expression
     * for this permission.
     * The constraints for a message are all predecessors and
     * activators.
     */
    private void buildAuthConstraint() {
        final StringBuffer authText = new StringBuffer(17);
        final Collection predecessors = Model.getCollaborationsHelper().
                                        getAllPossiblePredecessors(message);
        final Collection activators = Model.getCollaborationsHelper().
                                      getAllPossibleActivators(message);

        authText.append("context:" + getName() + "\nauth:");

        if (!predecessors.isEmpty()) {
            authText.append("\n(");
            authText.append(getAuthByMessages(predecessors));
            authText.append(')');
        }

        if (!activators.isEmpty()) {
            if (!predecessors.isEmpty()) {
                authText.append("\nand");
            }
            authText.append("\n(");
            authText.append(getAuthByMessages(activators));
            authText.append(')');
        }

        if (!("context:" + getName() + "\nauth:").
                equals(authText.toString())) {
            authConstraint = authText.toString();
        }
    }

    /**
     * Constructs an authorization sequence for a collection
     * of given messages.
     *
     * @param messages A collection containing the necessary messages.
     *
     * @return A string containing the authorization constraint.
     */
    private String getAuthByMessages(final Collection messages) {
        final StringBuffer authText = new StringBuffer();
        final StringBuffer senders = new StringBuffer();
        final StringBuffer receivers = new StringBuffer();
        final Permission permission = new Permission();
        final String delimiter = " and\n";

        for (Object currMessage : messages) {
            permission.setMessage(currMessage);
            senders.setLength(0);
            receivers.setLength(0);

            if (permission.isSecureUMLPermission()) {
                senders.append('{');

                permission.setSender(facade.
                                     getBases(facade.getSender(currMessage)).
                                     iterator().next());

                for (Object currSender : facade.
                        getBases(facade.getSender(currMessage))) {
                    senders.append(facade.getName(currSender));
                    senders.append(", ");
                }
                senders.delete(senders.length() - 2, senders.length());
                senders.append('}');

                authText.append("auth: ");
                authText.append(senders);
                authText.append(' ');

                receivers.append(" {");
                for (Object currReceiver : facade.
                        getBases(facade.getReceiver(currMessage))) {
                    permission.setReceiver(currReceiver);

                    if (permission.isSecureUMLPermission()
                            &&  permission.isValid()) {
                        receivers.append(facade.getName(currReceiver));
                        receivers.append(", ");
                    } else {
                        authInvalidReason = permission.invalidityReasons;
                    }
                }
                receivers.delete(receivers.length() - 2, receivers.length());
                receivers.append('}');

                authText.append(permission.resourceMember);
                authText.append(':');
                authText.append(permission.action);

                authText.append(receivers);
                authText.append(delimiter);
            }
        }

        if (authText.length() >= delimiter.length()) {
            authText.delete(authText.length() - delimiter.length(),
                            authText.length());
        }

        return authText.toString();
    }
}

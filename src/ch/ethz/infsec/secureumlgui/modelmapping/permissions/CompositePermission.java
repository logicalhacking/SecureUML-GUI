package ch.ethz.infsec.secureumlgui.modelmapping.permissions;

import java.util.Collection;
import java.util.List;

import javax.jmi.reflect.RefClass;
import javax.jmi.reflect.RefException;
import javax.jmi.reflect.RefFeatured;
import javax.jmi.reflect.RefObject;
import javax.jmi.reflect.RefPackage;

import ch.ethz.infsec.secureumlgui.securemodel.secureuml.AuthorizationConstraint;
import ch.ethz.infsec.secureumlgui.securemodel.secureuml.Permission;
import ch.ethz.infsec.secureumlgui.securemodel.secureuml.Role;

public class CompositePermission implements Permission {

    //public CompositePermission(String name,

    public Collection getAction() {
        // TODO Auto-generated method stub
        return null;
    }

    public AuthorizationConstraint getAuthorizationConstraint() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public Role getRole() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setAuthorizationConstraint(AuthorizationConstraint newValue) {
        // TODO Auto-generated method stub

    }

    public void setName(String newValue) {
        // TODO Auto-generated method stub

    }

    public void setRole(Role newValue) {
        // TODO Auto-generated method stub

    }

    public RefClass refClass() {
        // TODO Auto-generated method stub
        return null;
    }

    public void refDelete() {
        // TODO Auto-generated method stub

    }

    public RefFeatured refImmediateComposite() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean refIsInstanceOf(RefObject arg0, boolean arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    public RefFeatured refOutermostComposite() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object refGetValue(RefObject arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object refGetValue(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object refInvokeOperation(RefObject arg0, List arg1)
    throws RefException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object refInvokeOperation(String arg0, List arg1)
    throws RefException {
        // TODO Auto-generated method stub
        return null;
    }

    public void refSetValue(RefObject arg0, Object arg1) {
        // TODO Auto-generated method stub

    }

    public void refSetValue(String arg0, Object arg1) {
        // TODO Auto-generated method stub

    }

    public RefPackage refImmediatePackage() {
        // TODO Auto-generated method stub
        return null;
    }

    public RefObject refMetaObject() {
        // TODO Auto-generated method stub
        return null;
    }

    public String refMofId() {
        // TODO Auto-generated method stub
        return null;
    }

    public RefPackage refOutermostPackage() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection refVerifyConstraints(boolean arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}

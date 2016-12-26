package ch.ethz.infsec.secureumlgui.modelmapping.counters;

public class SecureUmlMappingCounter extends Counter {

    private int permissionCount = 0;

    private int permissionClassCount = 0;

    private int roleCount = 0;

    private int roleInheritanceCount = 0;

    private int policyCount = 0;

    private int policyInheritanceCount = 0;

    public int getPermissionClassCount() {
        return permissionClassCount;
    }

    public void incPermissionClassCount() {
        this.permissionClassCount++;
    }

    public int getPermissionCount() {
        return permissionCount;
    }

    public void incPermissionCount() {
        this.permissionCount++;
    }

    public int getRoleCount() {
        return roleCount;
    }

    public void incRoleCount() {
        this.roleCount++;
    }

    public int getRoleInheritanceCount() {
        return roleInheritanceCount;
    }

    public void incRoleInheritance() {
        roleInheritanceCount++;
    }

    public int getPolicyCount() {
        return policyCount;
    }

    public void incPolicyCount() {
        this.policyCount++;
    }

    public int getPolicyInheritanceCount() {
        return policyInheritanceCount;
    }

    public void incPolicyInheritanceCount() {
        this.policyInheritanceCount++;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("  SecureUML\n");
        buf.append("  " + getRoleCount() + " role class"
                   + sinPlur(getRoleCount(), "", "es") + "\n");
        buf.append("  " + getRoleInheritanceCount()
                   + " role inheritance relation"
                   + sinPlur(getRoleInheritanceCount(), "", "s") + "\n");
        buf.append("  " + getPermissionClassCount() + " permission class"
                   + sinPlur(getPermissionClassCount(), "", "es") + "\n");
        buf.append("  " + getPermissionCount() + " permission"
                   + sinPlur(getPermissionCount(), "", "s") + "\n");
        return buf.toString();
    }



}

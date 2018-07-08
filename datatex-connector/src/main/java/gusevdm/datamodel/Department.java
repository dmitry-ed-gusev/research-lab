package gusevdm.datamodel;

/** Domain object - departments. */
public class Department {

    private final long uniqueId;
    private final String deptId;
    private final String deptName;

    public Department(long uniqueId, String deptId, String deptName) {
        this.uniqueId = uniqueId;
        this.deptId = deptId;
        this.deptName = deptName;
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public String getDeptId() {
        return deptId;
    }

    public String getDeptName() {
        return deptName;
    }

}

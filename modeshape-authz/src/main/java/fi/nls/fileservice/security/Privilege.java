package fi.nls.fileservice.security;

public class Privilege {

    public static final Privilege READ = new Privilege("read");
    public static final Privilege ADD_NODE = new Privilege("add_node");
    public static final Privilege SET_PROPERTY = new Privilege("set_property");
    public static final Privilege REMOVE = new Privilege("remove");
    public static final Privilege REMOVE_CHILD_NODES = new Privilege("remove_child_nodes");
    public static final Privilege INDEX_WORKSPACE = new Privilege("index_workspace");
    public static final Privilege REGISTER_NAMESPACE = new Privilege("register_namespace");
    public static final Privilege BACKUP = new Privilege("backup");
    public static final Privilege RESTORE = new Privilege("restore");
    public static final Privilege MONITOR = new Privilege("monitor");
    
    private final String name;

    private static Privilege[] PRIVILEGES = new Privilege[] { Privilege.READ,
            Privilege.ADD_NODE, Privilege.SET_PROPERTY, Privilege.REMOVE,
            Privilege.REMOVE_CHILD_NODES, Privilege.INDEX_WORKSPACE, Privilege.REGISTER_NAMESPACE,
            Privilege.BACKUP, Privilege.RESTORE, Privilege.MONITOR };

    private Privilege(String name) {
        this.name = name;
    }

    public static Privilege forName(String name) {
        for (Privilege p : PRIVILEGES) {
            if (p.equals(name)) {
                return p;
            }
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(String privilege) {
        return name.equals(privilege);
    }

    public boolean equals(Privilege anotherPrivilege) {
        return this.name.equals(anotherPrivilege.getName());
    }

}

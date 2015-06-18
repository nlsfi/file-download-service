package fi.nls.fileservice.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Access Control Entry
 * 
 * Maps path - [0..n] -> privileges 
 */
public class ACE {

    private final long id;
    private String path;
    private Set<Privilege> privileges = new HashSet<Privilege>();

    public ACE() {
        this(-1, null);
    }
    
    public ACE(String path) {
        this(-1, path);
    }
    
    public ACE(long id, String path) {
        this.id = id;
        this.path = path;
    }
    
    public long getId() {
        return this.id;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }

    public Collection<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

}

package fi.nls.fileservice.security;

import java.util.ArrayList;
import java.util.List;

public class ACE {

    private String path;
    private List<Privilege> privileges = new ArrayList<Privilege>();

    public ACE() {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }

}

package fi.nls.fileservice.security;


public class ChangeSet {
    
    public enum ChangeType { MODIFY, REMOVE, ADD };
    
    private final ACE ace;
    private final ChangeType changeType;
    
    public ChangeSet(ACE ace, ChangeType type) {
        this.ace = ace;
        this.changeType = type;
    }
    
    public ACE getACE() {
        return this.ace;
    }
    
    public ChangeType getType() {
        return this.changeType;
    }
}

package fi.nls.fileservice.dataset;

public enum Licence {

    RESTRICTED("Rajoitettu käyttöoikeus"), OPENDATA("Avoin data");

    private String description;

    Licence(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

}

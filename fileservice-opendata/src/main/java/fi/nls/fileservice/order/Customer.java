package fi.nls.fileservice.order;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class Customer {

    @AssertTrue
    private boolean licenceAccepted;

    @Size(max = 60)
    private String firstName;

    @Size(max = 60)
    private String lastName;

    @Size(max = 120)
    private String organisation;

    @NotEmpty
    @Email
    private String email;

    private String language;

    public boolean isLicenceAccepted() {
        return licenceAccepted;
    }

    public void setLicenceAccepted(boolean licenceAccepted) {
        this.licenceAccepted = licenceAccepted;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getEmail() {
        return this.email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public void setEmail(String name) {
        this.email = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}

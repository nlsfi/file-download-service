package fi.nls.fileservice.order;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

public class OpenDataOrder {

    private String timestamp;

    @Valid
    private Customer customer;

    // TODO cannot validate Strings with regexp with JSR-303, must do so
    // manually in isValid()
    @NotEmpty
    private List<String> files = new ArrayList<String>();

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public boolean isValid() {
        java.util.regex.Pattern pattern = java.util.regex.Pattern
                .compile("([a-zA-Z]:)?(/[a-zA-Z0-9_.-]+)+/?");
        for (String path : files) {
            if (!pattern.matcher(path).matches()) {
                return false;
            }
        }
        return true;
    }

}

package fi.nls.fileservice.order;

import java.util.Collection;
import java.util.Locale;

import org.springframework.web.util.UriComponentsBuilder;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.dataset.Dataset;

public interface OrderService {

    public void saveOpenDataOrder(OpenDataOrder order,
            UriComponentsBuilder builder, Locale locale)
            throws DataAccessException;

    public Collection<Dataset> getOpenDataOrder(String tokenVariable);

    public void saveMtpOrder(MtpCustomer customer, Locale locale)
            throws DataAccessException;

}

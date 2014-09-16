package fi.nls.fileservice.order;

import java.security.AccessControlException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.common.NotFoundException;
import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetDAO;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.files.DetachedNode;
import fi.nls.fileservice.files.FileService;
import fi.nls.fileservice.files.PropertiesFilter;
import fi.nls.fileservice.jcr.MetadataProperty;
import fi.nls.fileservice.mail.MailService;
import fi.nls.fileservice.mail.TemplateResolver;
import fi.nls.fileservice.mail.TemplateResolvingException;
import fi.nls.fileservice.security.AuthorizationContextHolder;
import fi.nls.fileservice.security.PermissionDeniedException;
import fi.nls.fileservice.util.TokenGenerator;

public class OrderServiceImpl implements OrderService {

    private static Logger logger = LoggerFactory
            .getLogger(OrderServiceImpl.class);

    private final OrderDAO orderDao;
    private final TokenGenerator tokenGenerator;
    private final MailService mailService;
    private final FileService fileService;
    private final DatasetDAO datasetDAO;
    private final PropertiesFilter propertiesFilter;
    private final Repository repository;
    private final TemplateResolver templateResolver;
    private final MessageSource messageSource;

    public OrderServiceImpl(OrderDAO dao, MailService mailService,
            FileService fileService, DatasetDAO datasetDAO,
            PropertiesFilter filter, Repository repository,
            TokenGenerator tokenGenerator, TemplateResolver templateResolver,
            MessageSource messageSource) {
        this.orderDao = dao;
        this.tokenGenerator = tokenGenerator;
        this.mailService = mailService;
        this.datasetDAO = datasetDAO;
        this.fileService = fileService;
        this.propertiesFilter = filter;
        this.repository = repository;
        this.templateResolver = templateResolver;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public void saveOpenDataOrder(OpenDataOrder order,
            UriComponentsBuilder builder, Locale locale)
            throws DataAccessException {

        String email = order.getCustomer().getEmail();
        String tokenString = tokenGenerator.generateToken();

        orderDao.saveOpenDataOrder(tokenString, order);

        // orderdao throws dataaccessexception if error occurs so we won't send
        // email in that case
        Map<String, Object> msgBodyModel = new HashMap<String, Object>();
        msgBodyModel.put(MailTemplateConstants.URI,
                builder.buildAndExpand(tokenString).toUriString());
        msgBodyModel.put(MailTemplateConstants.EMAIL, email);
        msgBodyModel.put(MailTemplateConstants.DATE, new Date());

        mailService.sendMessage(email, messageSource.getMessage(
                "order_email_subject", null, locale), templateResolver
                .getMessage("mail/tilaus_sp_" + locale.getLanguage() + ".ftl",
                        msgBodyModel));

    }

    @Override
    public Collection<Dataset> getOpenDataOrder(String token) {

        Session session = null;
        try {

            session = repository.login(AuthorizationContextHolder
                    .getCredentials());

            Map<String, Dataset> datasetsCache = new HashMap<String, Dataset>();

            OpenDataOrder order = orderDao.getOpenDataOrder(token);
            for (String file : order.getFiles()) {

                DetachedNode node = fileService.getNode(file, session);

                if (node.hasProperty(MetadataProperty.NLS_DATASET)
                        && node.hasProperty(MetadataProperty.NLS_DATASETVERSION)) {
                    String datasetName = node.getProperty(
                            MetadataProperty.NLS_DATASET).getValue();
                    String datasetVersion = node.getProperty(
                            MetadataProperty.NLS_DATASETVERSION).getValue();

                    // remove properties that are not allowed for customers
                    if (propertiesFilter != null) {
                        propertiesFilter.filter(node);
                    }

                    Dataset dataset = datasetsCache.get(datasetName);
                    if (dataset == null) {
                        dataset = datasetDAO.getDatasetById(datasetName,
                                session);
                        datasetsCache.put(datasetName, dataset);
                    }

                    List<DatasetVersion> versions = dataset.getVersions();
                    for (DatasetVersion version : versions) {
                        if (version.getName().equals(datasetVersion)) {
                            version.getNodes().add(node);
                        }
                    }
                } else {
                    logger.warn("Dataset identifier not found for node: {}",
                            node.getPath());
                }
            }
            return datasetsCache.values();
        } catch (LoginException le) {
            throw new PermissionDeniedException(le);
        } catch (AccessControlException ace) {
            throw new PermissionDeniedException(ace);
        } catch (PathNotFoundException pnfe) {
            throw new NotFoundException(pnfe);
        } catch (RepositoryException re) {
            throw new DataAccessException(re);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    @Override
    public void saveMtpOrder(MtpCustomer customer, Locale locale)
            throws DataAccessException {

        // check first if an API-key already exists for the email address
        // provided
        // if so, we just send the existing apikey as email
        MtpCustomer existingUser = orderDao.getMtpCustomer(customer.getEmail());
        if (existingUser == null) {
            // create new api key
            String token = tokenGenerator.generateToken();
            customer.setApiKey(token);
            if (locale != null) {
                customer.setLanguage(locale.getLanguage());
            }
            orderDao.saveMtpOrder(customer);
            existingUser = customer;
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put(MailTemplateConstants.USER, existingUser);

        try {
            mailService.sendMessage(existingUser.getEmail(), messageSource
                    .getMessage("mtp_order_email_subject", null, locale),
                    templateResolver.getMessage(
                            "mail/mtp_tilaus_sp_" + locale.getLanguage()
                                    + ".ftl", model));
        } catch (NoSuchMessageException e) {
            throw new DataAccessException(e);
        } catch (TemplateResolvingException e) {
            throw new DataAccessException(e);
        }
    }

}

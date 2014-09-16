package fi.nls.fileservice.dataset.jcr;

import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlException;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.common.NotFoundException;
import fi.nls.fileservice.dataset.Dataset;
import fi.nls.fileservice.dataset.DatasetDAO;
import fi.nls.fileservice.dataset.DatasetService;
import fi.nls.fileservice.dataset.DatasetVersion;
import fi.nls.fileservice.dataset.Licence;
import fi.nls.fileservice.security.AccessPolicy;
import fi.nls.fileservice.security.AccessPolicyManager;
import fi.nls.fileservice.security.AuthorizationContextHolder;
import fi.nls.fileservice.security.PermissionDeniedException;
import fi.nls.fileservice.security.Privilege;

public class DatasetServiceImpl implements DatasetService {

    private Repository repository;
    private DatasetDAO dao;
    private AccessPolicyManager accessPolicyManager;
    private String openDataAccount;

    public DatasetServiceImpl(DatasetDAO dao, Repository repository,
            AccessPolicyManager accessPolicyManager) {
        this.dao = dao;
        this.repository = repository;
        this.accessPolicyManager = accessPolicyManager;
    }

    public DatasetServiceImpl(DatasetDAO dao, Repository repository,
            AccessPolicyManager accessPolicyManager, String openDataAccount) {
        this(dao, repository, accessPolicyManager);
        this.openDataAccount = openDataAccount;
    }

    @Override
    public List<Dataset> getAllDatasets() {
        return getDatasets(false);
    }

    @Override
    public List<Dataset> getPublishedDatasets() {
        return getDatasets(true);
    }

    public List<Dataset> getDatasets(boolean publishedOnly) {
        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder.getCredentials());
            return dao.getDatasets(publishedOnly, session);
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
    public Dataset getDatasetById(String datasetName) {
        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder.getCredentials());
            return dao.getDatasetById(datasetName, session);
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
    public void saveDataset(Dataset dataset) {
        Session session = null;
        try {

            // generate id from path, if not given
            if (dataset.getName() == null) {
                String path = dataset.getPath();
                if (path.indexOf("/") >= 0) {
                    int index = path.lastIndexOf("/") + 1;
                    path = path.substring(index);
                }
                dataset.setName(path);
            }

            session = repository.login(AuthorizationContextHolder.getCredentials());
            String datasetMetadataPath = dao.saveDataset(dataset, session);

            AccessPolicy policy = accessPolicyManager.getAccessPolicy(openDataAccount);
            if (policy != null) {
                if (dataset.getLicence() == Licence.OPENDATA) {
                    policy.addPrivileges(dataset.getPath(), Privilege.READ);
                    policy.addPrivileges(datasetMetadataPath, Privilege.READ);
                } else {
                    policy.removePrivilege(dataset.getPath(), Privilege.READ);
                    policy.removePrivilege(datasetMetadataPath, Privilege.READ);
                }
                accessPolicyManager.saveAccessPolicy(policy);
            }

            session.save();

        } catch (LoginException le) {
            throw new PermissionDeniedException(le);
        } catch (AccessControlException ace) {
            throw new PermissionDeniedException(ace);
        } catch (PathNotFoundException pnfe) {
            throw new NotFoundException(pnfe);
        } catch (Exception pae) {
            throw new DataAccessException(pae);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    @Override
    public DatasetVersion getDatasetVersion(String datasetName,
            String datasetVersion) {
        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder.getCredentials());
            return dao.getDatasetVersion(datasetName, datasetVersion, session);
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
    public void deleteDataset(String datasetName) {
        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder.getCredentials());

            Dataset dataset = this.getDatasetById(datasetName);
            if (dataset != null) {
                dao.deleteDataset(datasetName, session);

                if (dataset.getLicence() == Licence.OPENDATA) {
                    if (openDataAccount == null) {
                        throw new IllegalStateException(
                                "Configuration error: 'opendata.account.name' not set!");
                    }
                    AccessPolicy policy = accessPolicyManager.getAccessPolicy(openDataAccount);
                    if (policy != null) {
                        policy.removePrivilege(dataset.getPath(), Privilege.READ);
                        policy.removePrivilege(dataset.getMetadataPath(), Privilege.READ);
                        accessPolicyManager.saveAccessPolicy(policy);
                    }
                }

                session.save();
            }
        } catch (LoginException le) {
            throw new PermissionDeniedException(le);
        } catch (AccessControlException ace) {
            throw new PermissionDeniedException(ace);
        } catch (PathNotFoundException pnfe) {
            throw new NotFoundException(pnfe);
        } catch (Exception re) {
            throw new DataAccessException(re);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    @Override
    public void saveDatasetVersion(String datasetName,
            DatasetVersion datasetVersion) {
        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder.getCredentials());
            dao.saveDatasetVersion(datasetName, datasetVersion, session);
            session.save();
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
    public void deleteDatasetVersion(String datasetName,
            String datasetVersionName) {
        Session session = null;
        try {
            session = repository.login(AuthorizationContextHolder.getCredentials());
            dao.deleteDatasetVersion(datasetName, datasetVersionName, session);

            // remove published status from dataset if this was last
            // datasetversion
            Dataset dataset = dao.getDatasetById(datasetName, session);
            if (dataset != null && dataset.getVersions().size() == 0) {
                if (dataset.isPublished()) {
                    dataset.setPublished(false);
                    dao.saveDataset(dataset, session);
                }
            }
            session.save();
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

}

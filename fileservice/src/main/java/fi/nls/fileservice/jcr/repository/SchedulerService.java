package fi.nls.fileservice.jcr.repository;

//Disabled for now
//@Service
//@PropertySource("classpath:/config.properties")
//disable for now..
public class SchedulerService {

    private RepositoryJanitor janitor;

    // @Autowired
    public SchedulerService(RepositoryJanitor janitor) {
        this.janitor = janitor;
    }

    // @Scheduled(cron="${repository.cleaner.cron}") TODO
    // @Scheduled(cron="0 1 * * * ?")
    public void execute() {
        janitor.execute();
    }
}

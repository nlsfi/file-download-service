package fi.nls.fileservice.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fi.nls.fileservice.mail.MailService;
import fi.nls.fileservice.web.config.WebConfig;

@Configuration
@Import({ WebConfig.class })
public class TestWebConfig {
    
    @Bean
    public BeanFactoryPostProcessor bfpp() {
        return new BeanFactoryPostProcessor() {

            @Override
            public void postProcessBeanFactory(
                    ConfigurableListableBeanFactory arg0) throws BeansException {
               
                //override MailService defined in ApplicationConfiguration
                //with dummy version for tests
                MailService service = new MailService() {

                    @Override
                    public void sendMessage(String to, String Subject, String body) {
                     //noop, this a mock for tests
                        
                    }        
                  
                
            };
             arg0.registerSingleton("mailService", service);
        }
    };
    }
 }

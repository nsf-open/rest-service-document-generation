package gov.nsf.psm.documentgeneration.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.nsf.psm.documentgeneration.service.DocumentGenerationService;
import gov.nsf.psm.documentgeneration.service.DocumentGenerationServiceImpl;

@Configuration
@EnableConfigurationProperties({ ProposalDataServiceConfig.class, FileStorageServiceConfig.class })
public class DocumentGenerationServiceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentGenerationServiceConfig.class);

    @Bean
    public DocumentGenerationService documentGenerationService() {
        LOGGER.debug("DocumentGenerationServiceConfig.documentgenerationService");
        return new DocumentGenerationServiceImpl();
    }
}

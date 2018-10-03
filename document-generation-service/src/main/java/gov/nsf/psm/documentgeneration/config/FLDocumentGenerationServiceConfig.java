package gov.nsf.psm.documentgeneration.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.nsf.psm.documentgeneration.service.FLDocumentGenerationService;
import gov.nsf.psm.documentgeneration.service.FLDocumentGenerationServiceImpl;

@Configuration
@EnableConfigurationProperties({ ProposalDataServiceConfig.class, FileStorageServiceConfig.class })
public class FLDocumentGenerationServiceConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(FLDocumentGenerationServiceConfig.class);

    @Bean
    public FLDocumentGenerationService flDocumentGenerationService() {
        LOGGER.debug("FLDocumentGenerationServiceConfig.flDocumentGenerationService");
        return new FLDocumentGenerationServiceImpl();
    }

}

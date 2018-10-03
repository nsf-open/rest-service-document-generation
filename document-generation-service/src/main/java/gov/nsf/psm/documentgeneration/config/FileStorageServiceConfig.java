package gov.nsf.psm.documentgeneration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import gov.nsf.psm.foundation.config.ExternalServiceConfig;

@ConfigurationProperties(prefix = "fileStorageService")
public class FileStorageServiceConfig extends ExternalServiceConfig {

}

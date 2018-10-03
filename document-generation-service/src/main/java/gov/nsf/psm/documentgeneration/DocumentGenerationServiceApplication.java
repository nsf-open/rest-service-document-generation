package gov.nsf.psm.documentgeneration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class DocumentGenerationServiceApplication extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentGenerationServiceApplication.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DocumentGenerationServiceApplication.class);
    }

    public static void main(String[] args) {
        LOGGER.debug("DocumentGenerationServiceApplication.main");
        setEmbeddedContainerEnvironmentProperties();
        SpringApplication.run(DocumentGenerationServiceApplication.class, args);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        LOGGER.debug("DocumentGenerationServiceApplication.onStartup");
        setExternalContainerEnvironmentProperties();
        super.onStartup(servletContext);
    }

    private static void setEmbeddedContainerEnvironmentProperties() {
        LOGGER.debug("DocumentGenerationServiceApplication.setEmbeddedContainerEnvironment");
        setEnvironmentProperties();
        System.setProperty("server.context-path", "/psm-docgensvc");
    }

    private static void setExternalContainerEnvironmentProperties() {
        LOGGER.debug("DocumentGenerationServiceApplication.setExternalContainterEnvironmentProperties");
        setEnvironmentProperties();
    }

    private static void setEnvironmentProperties() {
        LOGGER.debug("DocumentGenerationServiceApplication.setEnvironmentProperties");
        System.setProperty("spring.config.name", "docgensvc");
    }
}

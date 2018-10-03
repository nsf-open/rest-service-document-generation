package gov.nsf.psm.documentgeneration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import gov.nsf.psm.foundation.exception.CommonUtilException;
import gov.nsf.psm.foundation.model.GRFPProjectSummary;
import gov.nsf.psm.foundation.model.fastlane.ProjectSummary;
import gov.nsf.psm.foundation.restclient.NsfRestTemplate;

public class FLDocumentGenerationServiceClientImpl implements FLDocumentGenerationServiceClient {

    private String serverURL;
    private String username;
    private String password;
    private boolean authenticationRequired;
    private int requestTimeout;

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentGenerationServiceClientImpl.class);

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAuthenticationRequired() {
        return authenticationRequired;
    }

    public void setAuthenticationRequired(boolean authenticationRequired) {
        this.authenticationRequired = authenticationRequired;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    private static HttpHeaders createHttpHeaders(String documentgenerationServiceUsername,
            String documentgenerationServicePassword) {
        return NsfRestTemplate.createHeaderswithAuthentication(documentgenerationServiceUsername,
                documentgenerationServicePassword);
    }

    @Override
    public int getProjectSummaryPDFPageCount(String overview, String brodImpact, String intelleMerit)
            throws CommonUtilException {
        try {
            RestTemplate documentgenerationServiceClient = NsfRestTemplate.setupRestTemplate(authenticationRequired,
                    requestTimeout);
            ProjectSummary ps = prepareProjectSummary(overview, brodImpact, intelleMerit);
            StringBuilder endpointUrl = new StringBuilder(serverURL);
            endpointUrl.append("/dgsv/projectSummPageCount");
            LOGGER.debug("Executing POST request to getProjectSummaryPDFPageCount : " + endpointUrl);
            ResponseEntity<ProjectSummary> response = null;
            HttpHeaders headers = createHttpHeaders(username, password);
            HttpEntity<ProjectSummary> httpEntity = new HttpEntity<ProjectSummary>(ps, headers);
            response = documentgenerationServiceClient.exchange(endpointUrl.toString(), HttpMethod.POST, httpEntity,
                    ProjectSummary.class);
            return response.getBody().getPageCount();
        } catch (Exception e) {
            throw new CommonUtilException("Got error getting Project Summary page count ", e);
        }

    }

    @Override
    public GRFPProjectSummary getGrfpProjectSummaryPDF(String overview, String brodImpact, String intelleMerit,
            String projectTitle) throws CommonUtilException {
        ResponseEntity<GRFPProjectSummary> response = null;
        try {
            RestTemplate documentgenerationServiceClient = NsfRestTemplate.setupRestTemplate(authenticationRequired,
                    requestTimeout);
            GRFPProjectSummary ps = prepareGrfpProjectSummary(overview, brodImpact, intelleMerit, projectTitle);
            StringBuilder endpointUrl = new StringBuilder(serverURL);
            endpointUrl.append("/dgsv/grfpProjectSummary");
            LOGGER.debug("Executing POST request to getProjectSummaryPDF : " + endpointUrl);
            HttpHeaders headers = createHttpHeaders(username, password);
            HttpEntity<GRFPProjectSummary> httpEntity = new HttpEntity<GRFPProjectSummary>(ps, headers);
            response = documentgenerationServiceClient.exchange(endpointUrl.toString(), HttpMethod.POST, httpEntity,
                    GRFPProjectSummary.class);
        } catch (Exception e) {
            throw new CommonUtilException("Got error while generating Project Summary", e);
        }
        return response.getBody();
    }

    @Override
    public ProjectSummary getProjectSummaryPDF(String overview, String brodImpact, String intelleMerit)
            throws CommonUtilException {
        ResponseEntity<ProjectSummary> response = null;
        try {
            RestTemplate documentgenerationServiceClient = NsfRestTemplate.setupRestTemplate(authenticationRequired,
                    requestTimeout);
            ProjectSummary ps = prepareProjectSummary(overview, brodImpact, intelleMerit);
            StringBuilder endpointUrl = new StringBuilder(serverURL);
            endpointUrl.append("/dgsv/projectSumm");
            LOGGER.debug("Executing POST request to getProjectSummaryPDF : " + endpointUrl);
            HttpHeaders headers = createHttpHeaders(username, password);
            HttpEntity<ProjectSummary> httpEntity = new HttpEntity<ProjectSummary>(ps, headers);
            response = documentgenerationServiceClient.exchange(endpointUrl.toString(), HttpMethod.POST, httpEntity,
                    ProjectSummary.class);
        } catch (Exception e) {
            throw new CommonUtilException("Got error while generating Project Summary", e);
        }
        return response.getBody();
    }

    private static ProjectSummary prepareProjectSummary(String overview, String brodImpact, String intelleMerit) {
        ProjectSummary ps = new ProjectSummary();
        ps.setOverview(overview);
        ps.setBrodimpact(brodImpact);
        ps.setIntellmerit(intelleMerit);
        return ps;
    }

    private static GRFPProjectSummary prepareGrfpProjectSummary(String overview, String brodImpact, String intelleMerit,
            String projectTitle) {
        GRFPProjectSummary ps = new GRFPProjectSummary();
        ps.setProjectTitle(projectTitle);
        ps.setOverview(overview);
        ps.setBrodimpact(brodImpact);
        ps.setIntellmerit(intelleMerit);
        return ps;
    }
}

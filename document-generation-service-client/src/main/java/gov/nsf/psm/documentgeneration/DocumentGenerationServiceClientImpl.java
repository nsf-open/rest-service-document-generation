package gov.nsf.psm.documentgeneration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import gov.nsf.psm.documentgeneration.constants.Constants;
import gov.nsf.psm.foundation.exception.CommonUtilException;
import gov.nsf.psm.foundation.model.COA;
import gov.nsf.psm.foundation.model.PdfGenerationData;
import gov.nsf.psm.foundation.model.ProjectSummary;
import gov.nsf.psm.foundation.model.generation.GetGeneratedDocumentResponse;
import gov.nsf.psm.foundation.model.generation.GetGeneratedDocumentResponseWrapper;
import gov.nsf.psm.foundation.restclient.NsfRestTemplate;

public class DocumentGenerationServiceClientImpl implements DocumentGenerationServiceClient {

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

    private static ProjectSummary prepareProjectSummary(String overview, String brodImpact, String intelleMerit) {
        ProjectSummary ps = new ProjectSummary();
        ps.setOverview(overview);
        ps.setBrodimpact(brodImpact);
        ps.setIntellmerit(intelleMerit);
        return ps;
    }

    private static HttpHeaders createHttpHeaders(String documentgenerationServiceUsername,
            String documentgenerationServicePassword) {
        // HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_JSON);
        // headers.set("username", documentgenerationServiceUsername);
        // headers.set("password", documentgenerationServicePassword);
        // return headers;
        return NsfRestTemplate.createHeaderswithAuthentication(documentgenerationServiceUsername,
                documentgenerationServicePassword);
    }

    @Override
    public GetGeneratedDocumentResponse getProposalDocument(PdfGenerationData pdf) throws CommonUtilException {

        ResponseEntity<GetGeneratedDocumentResponseWrapper> response = null;
        try {
            RestTemplate documentgenerationServiceClient = NsfRestTemplate.setupRestTemplate(authenticationRequired,
                    requestTimeout);
            StringBuilder endpointUrl = new StringBuilder(serverURL);
            endpointUrl.append("/proposal/printEntireProposal");
            LOGGER.debug("Executing POST request to Get Print Entire Proposal : " + endpointUrl);

            HttpHeaders headers = createHttpHeaders(username, password);
            HttpEntity<PdfGenerationData> httpEntity = new HttpEntity<PdfGenerationData>(pdf, headers);

            response = documentgenerationServiceClient.exchange(endpointUrl.toString(), HttpMethod.POST, httpEntity,
                    GetGeneratedDocumentResponseWrapper.class);
            return response.getBody().getGetGeneratedDocumentResponse();
        } catch (Exception e) {
            throw new CommonUtilException(Constants.PRINT_ENTIRE_PDF_PROPOSAL_ERROR, e);
        }

    }

    @Override
    public GetGeneratedDocumentResponse generateCOAPdf(COA coa) throws CommonUtilException {
        ResponseEntity<GetGeneratedDocumentResponseWrapper> response = null;
        try {
            RestTemplate documentgenerationServiceClient = NsfRestTemplate.setupRestTemplate(authenticationRequired,
                    requestTimeout);
            StringBuilder endpointUrl = new StringBuilder(serverURL);
            endpointUrl.append("/generateCOA");
            LOGGER.debug("Executing POST request to GenerateCOAPdf : " + endpointUrl);

            HttpHeaders headers = createHttpHeaders(username, password);
            HttpEntity<COA> httpEntity = new HttpEntity<COA>(coa, headers);
            response = documentgenerationServiceClient.exchange(endpointUrl.toString(), HttpMethod.POST, httpEntity,
                    GetGeneratedDocumentResponseWrapper.class);
            return response.getBody().getGetGeneratedDocumentResponse();
        } catch (Exception e) {
            throw new CommonUtilException(Constants.COA_PDF_GENERATION_ERROR, e);
        }
    }

    @Override
    public GetGeneratedDocumentResponse getBudgetPdf(PdfGenerationData pdf) throws CommonUtilException {
        ResponseEntity<GetGeneratedDocumentResponseWrapper> response = null;
        try {
            RestTemplate documentgenerationServiceClient = NsfRestTemplate.setupRestTemplate(authenticationRequired,
                    requestTimeout);
            StringBuilder endpointUrl = new StringBuilder(serverURL);
            endpointUrl.append("/generateBudgetPdf");
            LOGGER.debug("Executing POST request to GetBudgetPdf : " + endpointUrl);

            HttpHeaders headers = createHttpHeaders(username, password);
            HttpEntity<PdfGenerationData> httpEntity = new HttpEntity<PdfGenerationData>(pdf, headers);

            response = documentgenerationServiceClient.exchange(endpointUrl.toString(), HttpMethod.POST, httpEntity,
                    GetGeneratedDocumentResponseWrapper.class);
            return response.getBody().getGetGeneratedDocumentResponse();
        } catch (Exception e) {
            throw new CommonUtilException(Constants.BUDGET_PDF_GENERATION_ERROR, e);
        }
    }

    @Override
    public GetGeneratedDocumentResponse generateCoverSheetPdf(PdfGenerationData pdf) throws CommonUtilException {
        ResponseEntity<GetGeneratedDocumentResponseWrapper> response = null;
        try {
            RestTemplate documentgenerationServiceClient = NsfRestTemplate.setupRestTemplate(authenticationRequired,
                    requestTimeout);

            StringBuilder endpointUrl = new StringBuilder(serverURL);
            endpointUrl.append("/generateCoverSheet");

            HttpHeaders headers = NsfRestTemplate.createHeaderswithAuthentication(username, password);
            HttpEntity<PdfGenerationData> httpEntity = new HttpEntity<PdfGenerationData>(pdf, headers);

            LOGGER.debug("Executing POST request to GenerateCoverSheetPdf: " + endpointUrl);
            response = documentgenerationServiceClient.exchange(endpointUrl.toString(), HttpMethod.POST, httpEntity,
                    GetGeneratedDocumentResponseWrapper.class);

            return response.getBody().getGetGeneratedDocumentResponse();
        } catch (Exception e) {
            throw new CommonUtilException(Constants.CV_PDF_GENERATION_ERROR, e);
        }
    }

    @Override
    public GetGeneratedDocumentResponse generateSeniorPersonnelsPdf(PdfGenerationData pdf) throws CommonUtilException {
        ResponseEntity<GetGeneratedDocumentResponseWrapper> response = null;
        try {
            RestTemplate documentgenerationServiceClient = NsfRestTemplate.setupRestTemplate(authenticationRequired,
                    requestTimeout);
            StringBuilder endpointUrl = new StringBuilder(serverURL);
            endpointUrl.append("/generateSeniorPersonsPdf");
            LOGGER.debug("Executing POST request to GenerateSeniorPersonnelsPdf : " + endpointUrl);
            HttpHeaders headers = createHttpHeaders(username, password);
            HttpEntity<PdfGenerationData> httpEntity = new HttpEntity<PdfGenerationData>(pdf, headers);

            response = documentgenerationServiceClient.exchange(endpointUrl.toString(), HttpMethod.POST, httpEntity,
                    GetGeneratedDocumentResponseWrapper.class);

            return response.getBody().getGetGeneratedDocumentResponse();
        } catch (Exception e) {
            throw new CommonUtilException(Constants.SR_PERSONAL_PDF_GENERATION_ERROR, e);
        }
    }

    @Override
    public GetGeneratedDocumentResponse getConcatenatedPdf(PdfGenerationData pdf) throws CommonUtilException {
        ResponseEntity<GetGeneratedDocumentResponseWrapper> response = null;
        try {
            RestTemplate documentgenerationServiceClient = NsfRestTemplate.setupRestTemplate(authenticationRequired,
                    requestTimeout);
            StringBuilder endpointUrl = new StringBuilder(serverURL);
            endpointUrl.append("/concatenatedPdf");
            LOGGER.debug("Executing POST request to GetBudgetPdf : " + endpointUrl);

            HttpHeaders headers = createHttpHeaders(username, password);
            HttpEntity<PdfGenerationData> httpEntity = new HttpEntity<PdfGenerationData>(pdf, headers);

            response = documentgenerationServiceClient.exchange(endpointUrl.toString(), HttpMethod.POST, httpEntity,
                    GetGeneratedDocumentResponseWrapper.class);
            return response.getBody().getGetGeneratedDocumentResponse();
        } catch (Exception e) {
            throw new CommonUtilException(Constants.CONCATENATE_PDF_GENERATION_ERROR, e);
        }
    }

}

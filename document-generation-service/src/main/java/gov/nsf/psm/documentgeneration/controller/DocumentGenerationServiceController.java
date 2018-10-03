package gov.nsf.psm.documentgeneration.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import gov.nsf.psm.documentgeneration.service.DocumentGenerationService;
import gov.nsf.psm.foundation.controller.PsmBaseController;
import gov.nsf.psm.foundation.ember.model.EmberModel;
import gov.nsf.psm.foundation.exception.CommonUtilException;
import gov.nsf.psm.foundation.model.COA;
import gov.nsf.psm.foundation.model.PdfGenerationData;
import gov.nsf.psm.foundation.model.generation.GetGeneratedDocumentResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/api/v1")
@ApiResponses(value = { @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Resource not found"),
        @ApiResponse(code = 500, message = "Internal server error") })
public class DocumentGenerationServiceController extends PsmBaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentGenerationServiceController.class);
    @Autowired
    DocumentGenerationService documentGenerationService;

    /**
     * This method is used to generate the print entire proposal document,
     * compiled from a template specifying which sections to compile
     * 
     * @param propPrepId
     *            - proposal prep id
     * @param revId
     *            - proposal revision id
     * @param sectionList
     *            - list of sections
     * @return
     * @throws CommonUtilException
     * @throws IOException
     */
    @ApiOperation(value = "Generate a print entire proposal PDF file", notes = "This API returns a JSON object represents a print entire proposal PDF file", response = GetGeneratedDocumentResponse.class)
    @RequestMapping(value = "/proposal/printEntireProposal", method = RequestMethod.POST)
    @ResponseBody
    public EmberModel getProposalDocument(@RequestBody PdfGenerationData pdf) throws CommonUtilException, IOException {
        LOGGER.info("DocumentGenerationServiceController.PrintEntireProposal");
        GetGeneratedDocumentResponse response = new GetGeneratedDocumentResponse();
        ByteArrayOutputStream outputStream = documentGenerationService.generateProposalDocument(pdf);
        response.setFile(outputStream.toByteArray());
        return new EmberModel.Builder<>(GetGeneratedDocumentResponse.getClassCamelCaseName(), response).build();

    }

    /**
     * 
     * @param projectSummary
     * @return
     * @throws CommonUtilException
     */

    @ApiOperation(value = "Get generated COA PDF", notes = "This API returns JSON representing a generated COA PDF file")
    @RequestMapping(value = "/generateCOA", method = RequestMethod.POST)
    @ResponseBody
    public EmberModel generateCOAPdf(@RequestBody COA coa) throws CommonUtilException {
        LOGGER.info("DocumentGenerationServiceController.generateCOAPdf");
        GetGeneratedDocumentResponse response = new GetGeneratedDocumentResponse();
        Monitor coaMonitor;
        coaMonitor = MonitorFactory.start("generateCOAPdf");
        COA responseCOA = documentGenerationService.generateCOAPdf(coa);
        coaMonitor.stop();
        LOGGER.info(coaMonitor.toString());
        response.setFile(responseCOA.getFile());
        response.setPageCount(responseCOA.getPageCount());
        response.setGetSuccessful(true);
        return new EmberModel.Builder<>(GetGeneratedDocumentResponse.getClassCamelCaseName(), response).build();
    }

    @ApiOperation(value = "Get a generated Budget PDF file", notes = "This API returns a JSON object representing a generated Budget PDF file", response = GetGeneratedDocumentResponse.class)
    @RequestMapping(value = "/generateBudgetPdf", method = RequestMethod.POST)
    @ResponseBody
    public EmberModel getBudgetPdf(@RequestBody PdfGenerationData pdf) throws CommonUtilException, IOException {
        LOGGER.info("DocumentGenerationServiceController.getBudgetPdf");
        GetGeneratedDocumentResponse response = new GetGeneratedDocumentResponse();
        ByteArrayOutputStream outputStream = documentGenerationService.generateBudgetPdf(pdf);
        response.setFile(outputStream.toByteArray());
        return new EmberModel.Builder<>(GetGeneratedDocumentResponse.getClassCamelCaseName(), response).build();

    }

    @ApiOperation(value = "Get a generated CoverSheet PDF file", notes = "This API returns a JSON object representing a generated CoverSheet PDF file", response = GetGeneratedDocumentResponse.class)
    @RequestMapping(value = "/generateCoverSheet", method = RequestMethod.POST)
    @ResponseBody
    public EmberModel getCoverSheetPdf(@RequestBody PdfGenerationData pdf) throws CommonUtilException {
        LOGGER.info("DocumentGenerationServiceController.getCoverSheetPdf()");
        GetGeneratedDocumentResponse response = new GetGeneratedDocumentResponse();
        ByteArrayOutputStream outputStream = documentGenerationService.generateCoverSheetPdf(pdf);
        response.setFile(outputStream.toByteArray());
        return new EmberModel.Builder<>(GetGeneratedDocumentResponse.getClassCamelCaseName(), response).build();
    }

    @ApiOperation(value = "Get a Concatenated Pdf for All Senior Personals", notes = "This API returns a JSON object representing a Concatenated Pdf for All Senior Personals", response = GetGeneratedDocumentResponse.class)
    @RequestMapping(value = "/generateSeniorPersonsPdf", method = RequestMethod.POST)
    @ResponseBody
    public EmberModel getSeniorPeronsConcatenatedPdf(@RequestBody PdfGenerationData pdf)
            throws CommonUtilException, IOException {
        LOGGER.info("DocumentGenerationServiceController.getSeniorPeronsConcatenatedPdf()");
        GetGeneratedDocumentResponse response = new GetGeneratedDocumentResponse();
        ByteArrayOutputStream outputStream = documentGenerationService.generateSeniorPersonnelsPdf(pdf);
        response.setFile(outputStream.toByteArray());
        return new EmberModel.Builder<>(GetGeneratedDocumentResponse.getClassCamelCaseName(), response).build();
    }

    @ApiOperation(value = "Get a Concatednated PDF file", notes = "This API returns a JSON object representing a Concatednated PDF file", response = GetGeneratedDocumentResponse.class)
    @RequestMapping(value = "/concatenatedPdf", method = RequestMethod.POST)
    @ResponseBody
    public EmberModel getConcatenatedPdf(@RequestBody PdfGenerationData pdf) throws CommonUtilException, IOException {
        LOGGER.info("DocumentGenerationServiceController.getBudgetPdf");
        GetGeneratedDocumentResponse response = new GetGeneratedDocumentResponse();
        ByteArrayOutputStream outputStream = documentGenerationService.generateConcatenatedPdf(pdf);
        response.setFile(outputStream.toByteArray());
        return new EmberModel.Builder<>(GetGeneratedDocumentResponse.getClassCamelCaseName(), response).build();

    }
}

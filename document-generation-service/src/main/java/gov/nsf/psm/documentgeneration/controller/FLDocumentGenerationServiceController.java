package gov.nsf.psm.documentgeneration.controller;

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

import gov.nsf.psm.documentgeneration.service.FLDocumentGenerationService;
import gov.nsf.psm.foundation.controller.PsmBaseController;
import gov.nsf.psm.foundation.exception.CommonUtilException;
import gov.nsf.psm.foundation.model.GRFPProjectSummary;
import gov.nsf.psm.foundation.model.fastlane.ProjectSummary;

@RestController
@RequestMapping(value = "/dgsv")
public class FLDocumentGenerationServiceController extends PsmBaseController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FLDocumentGenerationService flDocGenService;

    /**
     * This method is used to get the PDF and page count.
     * 
     * @param projectSummary
     * @return projectSummary object
     * @throws CommonUtilException
     */
    @RequestMapping(value = "/projectSumm", method = RequestMethod.POST)
    @ResponseBody
    public ProjectSummary generateProjectSummaryPDF(@RequestBody ProjectSummary projectSummary)
            throws CommonUtilException {
        LOGGER.debug("DocumentGenerationServiceController.generateProjectSummaryPDF");
        try {
            Monitor projectSummaryMonitor;
            projectSummaryMonitor = MonitorFactory.start("generateProjectSummaryPDF");
            ProjectSummary responseProjectSummary = flDocGenService.generateProjectSummaryPDF(projectSummary);
            projectSummaryMonitor.stop();
            LOGGER.debug(projectSummaryMonitor.toString());
            return responseProjectSummary;

        } catch (CommonUtilException e) {
            LOGGER.error("Error in generateProjectSummaryPDF: ", e);
            throw new CommonUtilException(e.getMessage());
        }

    }

    /**
     * This method is used to generate the PDF and page count for GRFP
     * application.
     * 
     * @param projectSummary
     * @return projectSummary object
     * @throws CommonUtilException
     */
    @RequestMapping(value = "/grfpProjectSummary", method = RequestMethod.POST)
    @ResponseBody
    public GRFPProjectSummary generateGrfpProjectSummaryPDF(@RequestBody GRFPProjectSummary projectSummary)
            throws CommonUtilException {
        LOGGER.debug("DocumentGenerationServiceController.generateGrfpProjectSummaryPDF");
        try {
            Monitor projectSummaryMonitor;
            projectSummaryMonitor = MonitorFactory.start("generateGrfpProjectSummaryPDF");
            GRFPProjectSummary responseProjectSummary = flDocGenService.generateGrfpProjectSummaryPDF(projectSummary);
            projectSummaryMonitor.stop();
            LOGGER.debug(projectSummaryMonitor.toString());
            return responseProjectSummary;

        } catch (CommonUtilException e) {
            LOGGER.error("Error in generateGrfpProjectSummaryPDF: ", e);
            throw new CommonUtilException(e.getMessage());
        }

    }

    /**
     * This method is used to get the project summary PDF page count.
     * 
     * @param projectSummary
     * @return pagecount
     * @throws CommonUtilException
     */
    @RequestMapping(value = "/projectSummPageCount", method = RequestMethod.POST)
    @ResponseBody
    public int generateProjectSummaryPDFPageCount(@RequestBody ProjectSummary projectSummary)
            throws CommonUtilException {
        LOGGER.info("DocumentGenerationServiceController.generateProjectSummaryPDFPageCount");
        try {
            Monitor projectSummaryMonitor;
            projectSummaryMonitor = MonitorFactory.start("generateProjectSummaryPDFPageCount");
            int pageCount = flDocGenService.getProjectSummaryPDFPageCount(projectSummary);
            projectSummaryMonitor.stop();
            LOGGER.info(projectSummaryMonitor.toString());
            return pageCount;
        } catch (CommonUtilException e) {
            LOGGER.error("Error in generateProjectSummaryPDFPageCount: ", e);
            throw new CommonUtilException(e.getMessage());
        }

    }

}

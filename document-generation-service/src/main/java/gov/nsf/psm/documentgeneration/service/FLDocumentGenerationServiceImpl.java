package gov.nsf.psm.documentgeneration.service;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nsf.psm.documentgeneration.common.constants.Constants;
import gov.nsf.psm.documentgeneration.common.utility.FLPdfUtil;
import gov.nsf.psm.foundation.exception.CommonUtilException;
import gov.nsf.psm.foundation.model.GRFPProjectSummary;
import gov.nsf.psm.foundation.model.fastlane.ProjectSummary;

public class FLDocumentGenerationServiceImpl implements FLDocumentGenerationService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public ProjectSummary generateProjectSummaryPDF(ProjectSummary projectSummary) throws CommonUtilException {
        LOGGER.debug("DocumentGenerationServiceImpl.generateProjectSummaryPDF()");
        ProjectSummary ps = null;
        try {
            ByteArrayOutputStream baos = FLPdfUtil.generateProjectSummaryPDF(projectSummary);
            LOGGER.debug("DocumentGenerationServiceImpl.generateProjectSummaryPDF()-PDF Created.");

            int pageCount = FLPdfUtil.getPdfPageCount(baos);
            LOGGER.debug("DocumentGenerationServiceImpl.generateProjectSummaryPDF()-pageCount.");

            ps = new ProjectSummary();
            ps.setPageCount(pageCount);
            ps.setFile(baos.toByteArray());
            ps.setOverview(projectSummary.getOverview());
            ps.setBrodimpact(projectSummary.getBrodimpact());
            ps.setIntellmerit(projectSummary.getIntellmerit());
        } catch (Exception e) {
            LOGGER.error(Constants.GENERATE_PDF_ERROR, e);
            throw new CommonUtilException(Constants.GENERATE_PDF_ERROR, e);
        }
        return ps;
    }

    @Override
    public int getProjectSummaryPDFPageCount(ProjectSummary projectSummary) throws CommonUtilException {
        LOGGER.debug("DocumentGenerationServiceImpl.getProjectSummaryPDFPageCount");
        try {
            ByteArrayOutputStream baos = FLPdfUtil.generateProjectSummaryPDF(projectSummary);
            LOGGER.debug("FLDocumentGenerationServiceImpl.getProjectSummaryPDFPageCount() Size :::  " + baos.size());
            return FLPdfUtil.getPdfPageCount(baos);
        } catch (Exception e) {
            LOGGER.error(Constants.GENERATE_PDF_ERROR, e);
            throw new CommonUtilException(Constants.GENERATE_PDF_ERROR, e);
        }

    }

    @Override
    public GRFPProjectSummary generateGrfpProjectSummaryPDF(GRFPProjectSummary projectSummary)
            throws CommonUtilException {
        LOGGER.debug("DocumentGenerationServiceImpl.generateGrfpProjectSummaryPDF()");
        GRFPProjectSummary ps = null;
        try {
            ByteArrayOutputStream baos = FLPdfUtil.generateGrfpProjectSummaryPDF(projectSummary);
            LOGGER.debug("DocumentGenerationServiceImpl.generateGrfpProjectSummaryPDF()-PDF Created.");
            int pageCount = FLPdfUtil.getPdfPageCount(baos);
            LOGGER.debug("DocumentGenerationServiceImpl.generateGrfpProjectSummaryPDF()-pageCount.");
            ps = new GRFPProjectSummary();
            ps.setPageCount(pageCount);
            ps.setFile(baos.toByteArray());
            ps.setProjectTitle(projectSummary.getProjectTitle());
            ps.setOverview(projectSummary.getOverview());
            ps.setBrodimpact(projectSummary.getBrodimpact());
            ps.setIntellmerit(projectSummary.getIntellmerit());
        } catch (Exception e) {
            LOGGER.error(Constants.GENERATE_PDF_ERROR, e);
            throw new CommonUtilException(Constants.GENERATE_PDF_ERROR, e);
        }
        return ps;
    }
}

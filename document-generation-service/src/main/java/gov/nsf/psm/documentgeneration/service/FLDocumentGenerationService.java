package gov.nsf.psm.documentgeneration.service;

import gov.nsf.psm.foundation.exception.CommonUtilException;
import gov.nsf.psm.foundation.model.GRFPProjectSummary;
import gov.nsf.psm.foundation.model.fastlane.ProjectSummary;;

public interface FLDocumentGenerationService {

    /**
     * 
     * @param projectSummary
     * @return projectSummary
     * @throws CommonUtilException
     */
    public ProjectSummary generateProjectSummaryPDF(ProjectSummary projectSummary) throws CommonUtilException;

    /**
     * 
     * @param projectSummary
     * @return projectSummary
     * @throws CommonUtilException
     */
    public GRFPProjectSummary generateGrfpProjectSummaryPDF(GRFPProjectSummary projectSummary)
            throws CommonUtilException;

    /**
     * 
     * @param projectSummary
     * @return
     * @throws CommonUtilException
     */
    public int getProjectSummaryPDFPageCount(ProjectSummary projectSummary) throws CommonUtilException;
}

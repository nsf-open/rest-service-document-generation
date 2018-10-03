package gov.nsf.psm.documentgeneration;

import gov.nsf.psm.foundation.exception.CommonUtilException;
import gov.nsf.psm.foundation.model.GRFPProjectSummary;
import gov.nsf.psm.foundation.model.fastlane.ProjectSummary;

public interface FLDocumentGenerationServiceClient {
    /**
     * 
     * @param overview
     * @param brodImpact
     * @param intelleMerit
     * @return
     * @throws CommonUtilException
     */
    public ProjectSummary getProjectSummaryPDF(String overview, String brodImpact, String intelleMerit)
            throws CommonUtilException;

    /**
     * 
     * @param overview
     * @param brodImpact
     * @param intelleMerit
     * @return
     * @throws CommonUtilException
     */
    public GRFPProjectSummary getGrfpProjectSummaryPDF(String overview, String brodImpact, String intelleMerit,
            String projectTitle) throws CommonUtilException;

    /**
     * 
     * @param overview
     * @param brodImpact
     * @param intelleMerit
     * @return
     * @throws CommonUtilException
     */

    public int getProjectSummaryPDFPageCount(String overview, String brodImpact, String intelleMerit)
            throws CommonUtilException;

}

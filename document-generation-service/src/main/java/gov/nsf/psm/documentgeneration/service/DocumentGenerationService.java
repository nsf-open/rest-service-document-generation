package gov.nsf.psm.documentgeneration.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import gov.nsf.psm.foundation.exception.CommonUtilException;
import gov.nsf.psm.foundation.model.COA;
import gov.nsf.psm.foundation.model.PdfGenerationData;

public interface DocumentGenerationService {

    public ByteArrayOutputStream generateProposalDocument(PdfGenerationData pdf)
            throws CommonUtilException, IOException;

    /**
     * 
     * @param coa
     * @return
     * @throws CommonUtilException
     */
    public COA generateCOAPdf(COA coa) throws CommonUtilException;

    public ByteArrayOutputStream generateBudgetPdf(PdfGenerationData pdf) throws CommonUtilException;

    public ByteArrayOutputStream generateCoverSheetPdf(PdfGenerationData pdf) throws CommonUtilException;

    public ByteArrayOutputStream generateSeniorPersonnelsPdf(PdfGenerationData pdf) throws CommonUtilException;

    public ByteArrayOutputStream generateConcatenatedPdf(PdfGenerationData pdf) throws CommonUtilException;

}

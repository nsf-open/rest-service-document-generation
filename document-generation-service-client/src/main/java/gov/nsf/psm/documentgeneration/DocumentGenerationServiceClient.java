package gov.nsf.psm.documentgeneration;

import gov.nsf.psm.foundation.exception.CommonUtilException;
import gov.nsf.psm.foundation.model.COA;
import gov.nsf.psm.foundation.model.PdfGenerationData;
import gov.nsf.psm.foundation.model.generation.GetGeneratedDocumentResponse;

public interface DocumentGenerationServiceClient {

    public GetGeneratedDocumentResponse getProposalDocument(PdfGenerationData pdf) throws CommonUtilException;

    /**
     * This method is is ued to generate COA Pdf.
     * 
     * @param coa
     * @return
     * @throws CommonUtilException
     */
    public GetGeneratedDocumentResponse generateCOAPdf(COA coa) throws CommonUtilException;

    public GetGeneratedDocumentResponse getBudgetPdf(PdfGenerationData pdf) throws CommonUtilException;

    public GetGeneratedDocumentResponse generateCoverSheetPdf(PdfGenerationData pdf) throws CommonUtilException;

    public GetGeneratedDocumentResponse generateSeniorPersonnelsPdf(PdfGenerationData pdf) throws CommonUtilException;

    public GetGeneratedDocumentResponse getConcatenatedPdf(PdfGenerationData pdf) throws CommonUtilException;

}

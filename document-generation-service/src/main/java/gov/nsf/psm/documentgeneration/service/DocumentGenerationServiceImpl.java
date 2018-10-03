package gov.nsf.psm.documentgeneration.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nsf.psm.documentgeneration.common.constants.Constants;
import gov.nsf.psm.documentgeneration.common.constants.PDFDocConstants;
import gov.nsf.psm.documentgeneration.common.utility.PdfUtil;
import gov.nsf.psm.documentgeneration.common.utility.PrepareCoverSheetData;
import gov.nsf.psm.foundation.exception.CommonUtilException;
import gov.nsf.psm.foundation.model.COA;
import gov.nsf.psm.foundation.model.PdfGenerationData;
import gov.nsf.psm.foundation.model.Section;
import gov.nsf.psm.foundation.model.UploadableProposalSection;
import gov.nsf.psm.foundation.model.budget.BudgetPdf;
import gov.nsf.psm.foundation.model.budget.BudgetRecord;
import gov.nsf.psm.foundation.model.budget.BudgetTotals;
import gov.nsf.psm.foundation.model.coversheet.CoverSheetPdfMapper;

public class DocumentGenerationServiceImpl implements DocumentGenerationService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public ByteArrayOutputStream generateProposalDocument(PdfGenerationData pdf)
            throws CommonUtilException, IOException {
        LOGGER.debug("DocumentGenerationServiceImpl.PrintEntireProposal");

        Map<Section, UploadableProposalSection> pdfMap = pdf.getSectionPdfMap();
        // Map<String, ByteArrayOutputStream> sectionPDFs = new
        // LinkedHashMap<String, ByteArrayOutputStream>();
        Map<String, ByteArrayOutputStream> sectionPDFs = new LinkedHashMap<String, ByteArrayOutputStream>();
        Map<Section, String> pageCountMap = new EnumMap<Section, String>(Section.class);
        ByteArrayOutputStream proposalDocumentPDF = new ByteArrayOutputStream();
        String headerText = "";
        try {
            for (Map.Entry<Section, UploadableProposalSection> secMap : pdfMap.entrySet()) {
                Section sec = secMap.getKey();
                byte[] file = secMap.getValue().getFile();
                if (file != null && file.length > 0) {
                    ByteArrayOutputStream bao = new ByteArrayOutputStream(file.length);
                    bao.write(file, 0, file.length);
                    pageCountMap.put(sec, String.valueOf(PdfUtil.getPdfPageCount(bao)));
                    sectionPDFs.put(sec.getName(), bao);
                }
            }

            // Generate TOC content after pageNumbers acquired
            LOGGER.debug("DocumentGenerationServiceImpl.generateProposalDocument() - Creating Table of Contents");
            sectionPDFs.put(Section.TABLE_OF_CONTENTS.getName(),
                    PdfUtil.generateTableOfContents(Constants.TOC_SRC, pageCountMap));

            // concatenate the list of section documents
            proposalDocumentPDF = PdfUtil.concatenatePDFs(sectionPDFs);

            // do "second pass" addition of page numbers to generated document
            StringBuilder footerText = new StringBuilder();
            String revisionType = pdf.getProp().getPropPrepRevnTypeCode();
            String submissionStatus = pdf.getProp().getProposalStatus();
            LOGGER.info("********* DocumentGenerationServiceImpl --> Submission Status :: " + submissionStatus
                    + " Revn Type : " + revisionType);

            if (pdf.getProp().getNsfPropId() != null) {
                if (Constants.PFU.contains(revisionType) && submissionStatus.equalsIgnoreCase(Constants.ORIG_STATUS)) {
                    headerText = "Corrected : " + pdf.getProp().getAorElecSignDate();
                }

                if (revisionType.equalsIgnoreCase(Constants.ORIGINAL)
                        && submissionStatus.equalsIgnoreCase(Constants.ORIG_STATUS)) {
                    if (pdf.getProp().getPi().getFirstName() != null) {
                        footerText.append("Submitted/PI: " + pdf.getProp().getPi().getFirstName()).append(" ");
                    }
                    if (pdf.getProp().getPi().getMiddleName() != null) {
                        footerText.append(pdf.getProp().getPi().getMiddleName()).append(" ");
                    }
                    if (pdf.getProp().getPi().getLastName() != null) {
                        footerText.append(pdf.getProp().getPi().getLastName()).append(" ");
                    }
                    footerText.append("/Proposal No: " + pdf.getProp().getNsfPropId());
                } else if (revisionType.equalsIgnoreCase(Constants.PREV_BUDG)
                        && submissionStatus.equalsIgnoreCase(Constants.ORIG_STATUS)) {
                    footerText.append("Revised Proposal Budget Revision # " + pdf.getProp().getRevNum() + " for "
                            + pdf.getProp().getNsfPropId() + " Submitted On " + pdf.getProp().getSubmissionDate()
                            + " Electronic Signature");
                }
            } else {
                footerText.append("");
            }
            LOGGER.debug("*********footerText : " + footerText.toString() + " ********* headerText : " + headerText);
            proposalDocumentPDF = PdfUtil.addPageNumbers(proposalDocumentPDF, headerText, footerText.toString());
        } catch (Exception e) {
            throw new CommonUtilException(Constants.GENERATE_PDF_DOCUMENT_ERROR, e);
        }
        return proposalDocumentPDF;
    }

    @Override
    public COA generateCOAPdf(COA coa) throws CommonUtilException {
        try {
            ByteArrayOutputStream baos = PdfUtil.createCOAPDF(coa);
            int pageCount = PdfUtil.getPdfPageCount(baos);
            coa.setFile(baos.toByteArray());
            coa.setPageCount(pageCount);
        } catch (Exception e) {
            throw new CommonUtilException(Constants.GENERATE_PDF_COA_ERROR, e);
        }
        return coa;
    }

    @Override
    public ByteArrayOutputStream generateBudgetPdf(PdfGenerationData pdf) throws CommonUtilException {
        ByteArrayOutputStream budgetPdf = new ByteArrayOutputStream();
        ByteArrayOutputStream commentsPdf = null;
        ByteArrayOutputStream budgetPagePdf = new ByteArrayOutputStream();

        List<ByteArrayOutputStream> budgPagesList = new ArrayList<ByteArrayOutputStream>();
        LOGGER.debug("DocumentGenerationServiceImpl.generateBudgetPdf() instBudget :: " + pdf.getInstBudget());

        String propPrepId = pdf.getInstBudget().getPropPrepId();
        String proposalNumber = "";
        String aorName = "";
        if (pdf.getProp().getNsfPropId() != null) {
            proposalNumber = pdf.getProp().getNsfPropId();
        }

        if (pdf.getProp().getAorName() != null) {
            aorName = pdf.getProp().getAorName();
        }

        List<BudgetRecord> budgRecList = pdf.getInstBudget().getBudgetRecordList();
        if (budgRecList != null && !budgRecList.isEmpty()) {
            try {
                int i = 1;
                for (BudgetRecord br : budgRecList) {
                    BudgetPdf budgPage = new BudgetPdf();
                    if (br.getBudgetYear() == i) {
                        budgPage = PrepareCoverSheetData.setBudgetPageData(proposalNumber, aorName, pdf.getOrgName(),
                                br);
                        commentsPdf = PdfUtil.generateSummaryProposalBudgetCommentsPdf(budgPage);
                        i++;
                    }
                    budgetPagePdf = PdfUtil.generateBudgetPage(propPrepId, Constants.BUDG_SOURCE, budgPage);
                    budgPagesList.add(budgetPagePdf);
                    if (commentsPdf != null && commentsPdf.toByteArray().length > 1095) {
                        budgPagesList.add(commentsPdf);
                    }
                }
                // Generate Cummulative Budget Page.
                BudgetPdf cumPage = PrepareCoverSheetData.setBudgetPageData(proposalNumber, aorName, pdf.getOrgName(),
                        budgRecList);
                ByteArrayOutputStream cumPagePdf = PdfUtil.generateBudgetPage(propPrepId, Constants.BUDG_SOURCE,
                        cumPage);
                budgPagesList.add(cumPagePdf);

                // Get BudgetJustification and append it.
                List<UploadableProposalSection> upsList = pdf.getUploadedFileList();

                if (upsList != null && !upsList.isEmpty()) {
                    for (UploadableProposalSection ups : upsList) {
                        byte[] file = ups.getFile();
                        ByteArrayOutputStream budgetJust = new ByteArrayOutputStream(file.length);
                        budgetJust.write(file, 0, file.length);
                        LOGGER.debug(
                                "DocumentGenerationServiceImpl.generateBudgetPdf() file path :: " + ups.getFilePath());
                        budgPagesList.add(budgetJust);
                    }
                }

                budgetPdf = PdfUtil.createBudgetPdf(budgPagesList);

                // Stamp on each page of BREV revision.
                StringBuilder footerText = new StringBuilder();
                String revisionType = pdf.getProp().getPropPrepRevnTypeCode();
                String submissionStatus = pdf.getProp().getProposalStatus();
                if (revisionType.equalsIgnoreCase(Constants.PREV_BUDG)
                        && submissionStatus.equalsIgnoreCase(Constants.ORIG_STATUS) && !pdf.isPrintEntire()) {
                    // String.format("Revised Proposal Budget Revision # %s
                    // for %s Submitted On %s",revn_num,prop_id,last_updt_tmsp);
                    footerText.append("Revised Proposal Budget Revision # " + pdf.getProp().getRevNum() + " for "
                            + pdf.getProp().getNsfPropId() + " Submitted On " + pdf.getProp().getAorElecSignDate()
                            + " Electronic Signature");

                    budgetPdf = PdfUtil.stampForRevisedBudget(budgetPdf, footerText.toString());
                }

            } catch (Exception e) {
                throw new CommonUtilException(Constants.GENERATE_PDF_BUDJ_ERROR, e);
            }
        }
        return budgetPdf;
    }

    @Override
    public ByteArrayOutputStream generateCoverSheetPdf(PdfGenerationData pdf) throws CommonUtilException {

        ByteArrayOutputStream cvPdf = new ByteArrayOutputStream();
        try {
            LOGGER.debug("PdfGenerationData : " + pdf.getProp() + " -- cv : " + pdf.getCoverSheet());
            BudgetTotals bt = new BudgetTotals();
            String requestedAmount = PDFDocConstants.formatDollorAmount(
                    bt.getRequestedTotalAmountsForThisProposal(pdf.getInstBudget().getBudgetRecordList())
                            .getAmountOfThisRequest());

            LOGGER.debug("*****  Total Amount for this Request : " + requestedAmount);
            CoverSheetPdfMapper cvPage = PrepareCoverSheetData.setCoverSheetPdfMapperData(pdf.getCoverSheet(),
                    pdf.getProp(), requestedAmount);
            LOGGER.debug("*****  No os UOCs : " + cvPage.getUocs().length + "] PO View " + pdf.isExternalView());

            String src_uoc = Constants.COVERSHEET_UOC_SRC;
            String src = Constants.COVERSHEET_SRC;

            // In fucture we can remove following commented code.
            // if (pdf.isExternalView()) {
            // src_uoc = Constants.COVERSHEET_UOC_PV_SRC;
            // src = Constants.COVERSHEET_PV_SRC;
            // }

            if (cvPage.getUocs().length > 1) {
                cvPdf = PdfUtil.generateCoverSheet(src_uoc, cvPage, pdf.isExternalView());
            } else {
                cvPdf = PdfUtil.generateCoverSheet(src, cvPage, pdf.isExternalView());
            }

            String headerText = "";
            String revisionType = pdf.getProp().getPropPrepRevnTypeCode();
            String submissionStatus = pdf.getProp().getProposalStatus();
            LOGGER.info("********* generateCoverSheetPdf --> Submission Status :: " + submissionStatus + " Revn Type : "
                    + revisionType + " Cvr Pcv Check Indicator: " + pdf.getCoverSheet().isPcvCheckIndicator());

            if (Constants.PFU.contains(revisionType) && submissionStatus.equalsIgnoreCase(Constants.ORIG_STATUS)) {
                if (pdf.getCoverSheet().isPcvCheckIndicator()) {
                    headerText = "Corrected : " + pdf.getProp().getAorElecSignDate();
                }
            }

            if (pdf.isPrintPageNumbers()) {
                cvPdf = PdfUtil.addPageNumbers(cvPdf, headerText, "");
            }
        } catch (Exception e) {
            throw new CommonUtilException(Constants.GENERATE_PDF_CV_ERROR, e);
        }
        return cvPdf;
    }

    @Override
    public ByteArrayOutputStream generateSeniorPersonnelsPdf(PdfGenerationData pdf) throws CommonUtilException {
        ByteArrayOutputStream srPrPdf = new ByteArrayOutputStream();
        List<ByteArrayOutputStream> pdfList = new ArrayList<ByteArrayOutputStream>();

        try {
            if (pdf.getUploadedFileList() != null && !pdf.getUploadedFileList().isEmpty()) {
                int i = 1;
                for (UploadableProposalSection ups : pdf.getUploadedFileList()) {
                    byte[] file = ups.getFile();
                    if (file != null && file.length > 0) {
                        LOGGER.debug("********** [" + i + "] ********* :: " + file.length + " path : "
                                + ups.getFilePath() + " page count : " + ups.getPageCount());
                        ByteArrayOutputStream budgetJust = new ByteArrayOutputStream(file.length);
                        budgetJust.write(file, 0, file.length);
                        i++;
                        pdfList.add(budgetJust);
                        file = null;
                    }
                }
            } else {
                if (pdf.getSection() != null) {
                    pdfList.add(PdfUtil.generateDataNotAvailablePdf(pdf.getSection().getName().toString()));
                } else {
                    pdfList.add(PdfUtil.generateDataNotAvailablePdf("Section"));
                }
            }
            if (!pdfList.isEmpty()) {
                srPrPdf = PdfUtil.createBudgetPdf(pdfList);
            }
        } catch (

        Exception e) {
            throw new CommonUtilException(Constants.GENERATE_CONCATENATE_ERROR, e);
        }
        return srPrPdf;
    }

    @Override
    public ByteArrayOutputStream generateConcatenatedPdf(PdfGenerationData pdf) throws CommonUtilException {
        ByteArrayOutputStream concatenatedBaos = new ByteArrayOutputStream();

        // Get BudgetJustification and append it.
        List<UploadableProposalSection> upsList = pdf.getUploadedFileList();
        List<ByteArrayOutputStream> baosList = new ArrayList<ByteArrayOutputStream>();
        try {
            if (upsList != null && !upsList.isEmpty()) {
                for (UploadableProposalSection ups : upsList) {
                    byte[] file = ups.getFile();
                    ByteArrayOutputStream bao = new ByteArrayOutputStream(file.length);
                    bao.write(file, 0, file.length);
                    LOGGER.debug("DocumentGenerationServiceImpl.generateConcatenatedPdf() file path :: "
                            + ups.getFilePath());
                    baosList.add(bao);
                }
            } else {
                baosList.add(PdfUtil.generateDataNotAvailablePdf(pdf.getSection().getName()));
            }
            if (!baosList.isEmpty()) {
                concatenatedBaos = PdfUtil.createBudgetPdf(baosList);
            }
        } catch (

        Exception e) {
            throw new CommonUtilException(Constants.GENERATE_CONCATENATE_ERROR, e);
        }
        return concatenatedBaos;
    }
}

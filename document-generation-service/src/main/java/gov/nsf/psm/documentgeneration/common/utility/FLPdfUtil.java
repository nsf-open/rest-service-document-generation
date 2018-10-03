package gov.nsf.psm.documentgeneration.common.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import gov.nsf.psm.documentgeneration.common.constants.Constants;
import gov.nsf.psm.foundation.model.GRFPProjectSummary;
import gov.nsf.psm.foundation.model.fastlane.ProjectSummary;

public class FLPdfUtil {

    /**
     * Constructor
     */
    public FLPdfUtil() {
        //
    }

    private static final Logger log = LoggerFactory.getLogger(FLPdfUtil.class);

    /**
     * 
     * @param projectSummary
     * @return
     * @throws DocumentException
     */
    public static ByteArrayOutputStream generateProjectSummaryPDF(ProjectSummary projectSummary) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Document Size
        log.debug("FLPdfUtil.generateProjectSummaryPDF() -- Generating FastLane project summary pdf.");
        Document document = getDocumentForFLAndGrfp(outputStream);

        try {

            // Document Title
            prepareDocumentTitle(document, Constants.PROJ_SUMM_TITLE);
            // Document Sections
            prepareDocumentSection(document, Constants.OVERVIEW, projectSummary.getOverview());
            prepareDocumentSection(document, Constants.INTELLECTUAL_MERIT, projectSummary.getIntellmerit());
            prepareDocumentSection(document, Constants.BRODER_IMPACT, projectSummary.getBrodimpact());

        } finally {
            document.close();
        }
        return outputStream;
    }

    private static void drawLineForFLProjSumm(Document document) {
        // Creating a new page
        PdfPage pdfPage = document.getPdfDocument().addNewPage();
        // Creating a PdfCanvas object
        PdfCanvas canvas = new PdfCanvas(pdfPage);
        float left = document.getPdfDocument().getDefaultPageSize().getLeft();
        float top = document.getPdfDocument().getDefaultPageSize().getTop();
        float right = document.getPdfDocument().getDefaultPageSize().getRight();
        // float bottom =
        // document.getPdfDocument().getDefaultPageSize().getBottom();
        // log.debug("Margins LEFT : " + left + " Right : " + right + " TOP : "
        // + top + " Botton : " + bottom);
        canvas.moveTo(left + 72, top - 87).lineTo(right - 72, top - 87).closePathStroke();
    }

    /**
     * 
     * @param projectSummary
     * @return
     * @throws DocumentException
     */
    public static ByteArrayOutputStream generateGrfpProjectSummaryPDF(GRFPProjectSummary projectSummary)
            throws Exception {

        log.debug("FLPdfUtil.generateGrfpProjectSummaryPDF() -- Generating GRFP project summary pdf.");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Document Size
        Document document = getDocumentForFLAndGrfp(outputStream);
        try {
            // Document Sections
            prepareDocumentSection(document, Constants.PROJECT_TITLE, projectSummary.getProjectTitle());
            prepareDocumentSection(document, Constants.OVERVIEW, projectSummary.getOverview());
            prepareDocumentSection(document, Constants.INTELLECTUAL_MERIT, projectSummary.getIntellmerit());
            prepareDocumentSection(document, Constants.BRODER_IMPACT, projectSummary.getBrodimpact());

        } finally {
            document.close();
        }
        return outputStream;

    }

    /**
     * 
     * @param bios
     * @return
     * @throws IOException
     */
    public static int getPdfPageCount(ByteArrayOutputStream bios) throws Exception {
        int pageNos = 0;
        try {
            InputStream is = new ByteArrayInputStream(bios.toByteArray());
            PdfDocument pdfDocument = new PdfDocument(new PdfReader(is));
            pageNos = pdfDocument.getNumberOfPages();
            pdfDocument.close();
        } catch (Exception e) {

        }
        return pageNos;
    }

    /**
     * This method returns Title font.
     *
     * @return Title font.
     */
    // private static PdfFont getTitleFont() throws Exception {
    // return PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
    // }

    /**
     * This method returns Section font.
     *
     * @return Section font.
     */
    private static PdfFont getSectionFont() throws Exception {
        return PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

    }

    /**
     * This method returns Text font.
     *
     * @return Text font.
     */
    private static PdfFont getTextFont() throws Exception {
        return PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

    }

    /**
     * It returns the Document Object.
     * 
     * @param outputStream
     * @return
     * @throws Exception
     */
    public static Document getDocumentForFLAndGrfp(ByteArrayOutputStream outputStream) throws Exception {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.LETTER);
        document.setMargins(65, 72, 65, 72);
        return document;
    }

    public static Document prepareDocumentSection(Document document, String sectionTitle, String sectionText)
            throws Exception {
        // LOGGER.debug("PdfUtil22.prepareDocumentSection()");

        Paragraph secTitle = new Paragraph(sectionTitle + Constants.COLON).setFont(getSectionFont()).setFontSize(13)
                .setBold();
        secTitle.setFixedLeading(0);

        Paragraph secText = new Paragraph(sectionText + Constants.NEW_LINE).setFont(getTextFont()).setFontSize(11);
        secText.setFixedLeading(12);

        document.add(secTitle);
        document.add(secText);
        return document;
    }

    public static Document prepareDocumentTitle(Document document, String projectTitle) throws Exception {
        Paragraph title = new Paragraph(projectTitle).setTextAlignment(TextAlignment.CENTER).setBold();
        drawLineForFLProjSumm(document);
        document.add(title);
        return document;
    }

}

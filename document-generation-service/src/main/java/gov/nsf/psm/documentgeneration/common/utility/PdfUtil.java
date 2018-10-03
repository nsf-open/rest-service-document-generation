package gov.nsf.psm.documentgeneration.common.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import gov.nsf.psm.documentgeneration.common.constants.Constants;
import gov.nsf.psm.foundation.exception.CommonUtilException;
import gov.nsf.psm.foundation.model.Advisee;
import gov.nsf.psm.foundation.model.Affiliation;
import gov.nsf.psm.foundation.model.COA;
import gov.nsf.psm.foundation.model.CoEditor;
import gov.nsf.psm.foundation.model.Collaborator;
import gov.nsf.psm.foundation.model.Relationship;
import gov.nsf.psm.foundation.model.Section;
import gov.nsf.psm.foundation.model.UnitOfConsideration;
import gov.nsf.psm.foundation.model.budget.BudgetPdf;
import gov.nsf.psm.foundation.model.budget.BudgetSeniorPersonnel;
import gov.nsf.psm.foundation.model.budget.EquipmentCost;
import gov.nsf.psm.foundation.model.budget.IndirectCost;
import gov.nsf.psm.foundation.model.budget.OtherPersonnelCost;
import gov.nsf.psm.foundation.model.budget.SeniorPersonnelCost;
import gov.nsf.psm.foundation.model.coversheet.CoverSheetPdfMapper;
import gov.nsf.psm.foundation.model.coversheet.PiCoPiInformation;

public class PdfUtil {

    private PdfUtil() {

    }

    private static final Logger log = LoggerFactory.getLogger(PdfUtil.class);
    static float FONT_SIZE = 9;
    static float TABLE_WIDTH = 470;

    /**
     * This method is used to genearate cover sheet pdf.
     * 
     * @param src
     * @param cv
     * @param isExternalView
     * @return
     * @throws Exception
     */
    public static ByteArrayOutputStream generateCoverSheet(String src, CoverSheetPdfMapper cv, boolean isExternalView)
            throws CommonUtilException {
        log.debug("********** Generating Cover Sheet @@ " + new Date() + " *************");

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document doc = getDocument(outputStream, src);
            log.debug("No of Pages : " + doc.getPdfDocument().getNumberOfPages());
            // for (int i = 1; i <= doc.getPdfDocument().getNumberOfPages();
            // i++) {
            float left = doc.getPdfDocument().getPage(1).getPageSize().getLeft();
            float top = doc.getPdfDocument().getPage(1).getPageSize().getTop();

            // getMargins(doc.getPdfDocument().getPage(i));
            log.debug("X: " + left + " Y: " + top);

            // PROGRAM ANNOUNCEMENT/SOLICITATION NO.
            doc.showTextAligned(getText(cv.getSolicitationId()), left + 35, top - 95, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // DUE DATE
            doc.showTextAligned(getText(cv.getDueDate()), left + 185, top - 95, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // Special Exception to Deadline Date Policy
            doc.showTextAligned(getText(cv.getDeadlineDate()), left + 275, top - 95, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // NSF PROPOSAL NUMBER -- Title
            doc.showTextAligned(getText("NSF PROPOSAL NUMBER"), left + 445, top - 100, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // NSF PROPOSAL NUMBER
            doc.showTextAligned(getProposalText(cv.getNsfProposalNum()), left + 445, top - 138, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // FOR CONSIDERATION BY NSF ORGANIZATION UNIT(S) (Indicate the most
            // specific unit known, i.e. program, division, etc.)
            doc.showTextAligned(getText(cv.getNsfOrganizationUnits()), left + 35, top - 130, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // DateReceived
            doc.showTextAligned(getText(cv.getDateReceived()), left + 35, top - 170, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);
            // Number of Copies
            doc.showTextAligned(getText(String.valueOf(cv.getNumberOfCopies())), left + 145, top - 170, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // DIVISION ASSIGNED
            doc.showTextAligned(getText(cv.getDivisionAssigned()), left + 205, top - 170, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // FUND CODE
            doc.showTextAligned(getText(cv.getFundCode()), left + 305, top - 170, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // DUNS# (Data Universal Numbering System)
            doc.showTextAligned(getText(cv.getDunsId()), left + 385, top - 170, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // FILE LOCATION
            doc.showTextAligned(getText(cv.getFileLocation()), left + 485, top - 170, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // EMPLOYER IDENTIFICATION NUMBER (EIN) OR TAXPAYER IDENTIFICATION
            // NUMBER (TIN)
            doc.showTextAligned(getText(cv.getEmployerIdNumber()), left + 35, top - 205, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // SHOW PREVIOUS AWARD NO. IF THIS IS --A RENEWAL

            // A RENEWAL
            if (cv.isRenewal()) {
                doc.showTextAligned(getText("x"), left + 199, top - 192, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }
            // AN ACCOMPLISHMENT-BASED RENEWAL
            if (cv.isAccmpBasedRenewal()) {
                doc.showTextAligned(getText("x"), left + 199, top - 201, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // IS THIS PROPOSAL BEING SUBMITTED TO ANOTHER FEDERAL
            if (cv.isPropSubmittedToOthFedAgency()) {
                doc.showTextAligned(getText("x"), left + 431, top - 193, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);

                doc.showTextAligned(getText(cv.getOtherFedAgencies()), left + 370, top - 205, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            } else {
                doc.showTextAligned(getText("x"), left + 456, top - 193, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // NAME OF ORGANIZATION TO WHICH AWARD SHOULD BE MADE
            doc.showTextAligned(getSmallText(cv.getAwardeeOrgName()), left + 35, top - 240, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // ADDRESS OF AWARDEE ORGANIZATION, INCLUDING 9 DIGIT ZIP CODE
            doc.showTextAligned(getText(cv.getAwardeeOrgAddressLine1()), left + 275, top - 240, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);
            doc.showTextAligned(getText(cv.getAwardeeOrgAddressLine2()), left + 275, top - 250, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);
            doc.showTextAligned(getText(cv.getAwardeeOrgAddressLine3()), left + 275, top - 260, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // AWARDEE ORGANIZATION CODE (IF KNOWN)
            doc.showTextAligned(getText(cv.getAwardeeOrgCode()), left + 38, top - 268, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // NAME OF PRIMARY PLACE OF PERF
            doc.showTextAligned(getSmallText(cv.getPrimaryPerfOrgName()), left + 35, top - 298, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // ADDRESS OF PRIMARY PLACE OF PERF, INCLUDING 9 DIGIT ZIP CODE
            doc.showTextAligned(getText(cv.getPrimaryPerfOrgAddressLine1()), left + 275, top - 295, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            doc.showTextAligned(getText(cv.getPrimaryPerfOrgAddressLine2()), left + 275, top - 305, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            doc.showTextAligned(getText(cv.getPrimaryPerfOrgAddressLine3()), left + 275, top - 315, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // IS AWARDEE ORGANIZATION (Check All That Apply)
            // SMALL BUSINESS
            if (cv.isSmallBusiness()) {
                doc.showTextAligned(getText("x"), left + 211, top - 338, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }
            // MINORITY BUSINESS
            if (cv.isMinorityBusiness()) {
                doc.showTextAligned(getText("x"), left + 318, top - 338, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // IF THIS IS A PRELIMINARY PROPOSAL THEN CHECK HERE
            if (cv.isPreliminaryProp()) {
                doc.showTextAligned(getText("x"), left + 420, top - 338, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // FOR-PROFIT ORGANIZATION
            if (cv.isProfitOrg()) {
                doc.showTextAligned(getText("x"), left + 211, top - 346, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // WOMAN-OWNED BUSINESS THEN CHECK HERE
            if (cv.isWomanOwnedBusiness()) {
                doc.showTextAligned(getText("x"), left + 318, top - 346, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // TITLE OF PROPOSED PROJECT
            doc.showTextAligned(getText(cv.getProposedProjectTitle()), left + 35, top - 370, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // REQUESTED AMOUNT
            doc.showTextAligned(getText("$  " + cv.getRequestedAmount()), left + 35, top - 405, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // PROPOSED DURATION (1-60 MONTHS)
            doc.showTextAligned(getText(cv.getProposedDuration() + "   months"), left + 190, top - 405, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // REQUESTED STARTING DATE
            doc.showTextAligned(getText(cv.getRequestedStartingDate()), left + 340, top - 405, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // SHOW RELATED PRELIMINARY PROPOSAL NO.
            doc.showTextAligned(getText(cv.getPrelimProposalNo()), left + 500, top - 405, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // THIS PROPOSAL INCLUDES ANY OF THE ITEMS LISTED BELOW
            // BEGINNING INVESTIGATOR
            if (cv.isBeginningInvestigator()) {
                doc.showTextAligned(getText("x"), left + 36, top - 426, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM,
                        0);
            }

            // HUMAN SUBJECTS
            if (cv.isHumanSubjects()) {
                doc.showTextAligned(getText("x"), left + 303, top - 426, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // HUMAN SUBJECTS Human Subjects Assurance Number
            doc.showTextAligned(getText(cv.getHumanSubjectsAssurNum()), left + 526, top - 424, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // DISCLOSURE OF LOBBYING ACTIVITIES
            if (cv.isDiscOfLobbyingActivities()) {
                doc.showTextAligned(getText("x"), left + 36, top - 437, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM,
                        0);
            }

            // Exemption Subsection or IRB App. Date
            doc.showTextAligned(getSmallText(cv.getExemptionSubsection()), left + 373, top - 434, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(cv.getIrbAppDate()), left + 460, top - 436, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // PROPRIETARY & PRIVILEGED INFORMATION
            if (cv.isProprietaryPrivInformation()) {
                doc.showTextAligned(getText("x"), left + 36, top - 448, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM,
                        0);
            }

            // INTERNATIONAL ACTIVITIES: COUNTRY/COUNTRIES INVOLVED
            if (!StringUtils.isEmpty(cv.getIntActivitiesCountries())) {
                doc.showTextAligned(getText("x"), left + 303, top - 448, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(cv.getIntActivitiesCountries()), left + 310, top - 462, 1,
                        TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            }

            // HISTORIC PLACES
            if (cv.isHistoricPlace()) {
                doc.showTextAligned(getText("x"), left + 36, top - 459, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM,
                        0);
            }

            // VERTEBRATE ANIMALS IACUC App. Date
            if (cv.isVertebrateAnimals()) {
                doc.showTextAligned(getText("x"), left + 36, top - 470, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM,
                        0);
                doc.showTextAligned(getText(cv.getIacucAppDate()), left + 230, top - 468, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // COLLABORATIVE STATUS
            if ("yes".equalsIgnoreCase(cv.getCollabStatus())) {
                doc.showTextAligned(getText(cv.getCollabStatusValue()), left + 303, top - 489, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            } else {
                doc.showTextAligned(getText("x"), left + 303, top - 476, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(cv.getCollabStatusValue()), left + 303, top - 489, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // PHS Animal Welfare Assurance Number
            doc.showTextAligned(getText(cv.getPhsAnimalWelfareAssuNum()), left + 170, top - 480, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // TYPE OF PROPOSAL
            if (!StringUtils.isEmpty(cv.getTypeOfProposal())) {
                doc.showTextAligned(getText("x"), left + 36, top - 489, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM,
                        0);
                doc.showTextAligned(getText(cv.getTypeOfProposal()), left + 120, top - 489, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // --------

            // NAMES (TYPED)
            PiCoPiInformation piPd = null;
            PiCoPiInformation coPi1 = null;
            PiCoPiInformation coPi2 = null;
            PiCoPiInformation coPi3 = null;
            PiCoPiInformation coPi4 = null;

            int i = 1;
            for (PiCoPiInformation cvPer : cv.getPiCopiList()) {
                if (Constants.USER_PI.equalsIgnoreCase(cvPer.getPersonType().trim())) {
                    piPd = cvPer;
                } else {
                    if (Constants.USER_CO_PI.equalsIgnoreCase(cvPer.getPersonType().trim())) {
                        switch (i) {
                        case 1:
                            coPi1 = cvPer;
                            break;
                        case 2:
                            coPi2 = cvPer;
                            break;
                        case 3:
                            coPi3 = cvPer;
                            break;
                        default:
                            coPi4 = cvPer;
                            break;
                        }
                        i++;
                    }
                }
            }

            // PI/PD DEPARTMENT
            if (piPd != null) {
                doc.showTextAligned(getText(piPd.getDepartmentName()), left + 35, top - 512, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);

                // PI/PD POSTAL ADDRESS
                String add1 = piPd.getAddress().getStreetAddress();
                String add2 = piPd.getAddress().getStreetAddress2();
                String add3 = piPd.getAddress().getCityName() + ", " + piPd.getAddress().getStateCode() + " "
                        + piPd.getAddress().getPostalCode() + " " + piPd.getAddress().getCountryCode();

                doc.showTextAligned(getText(add1), left + 225, top - 512, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);

                float top2 = 532;
                if (!StringUtils.isEmpty(add2)) {
                    doc.showTextAligned(getText(add2), left + 225, top - 522, 1, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                } else {
                    top2 = 522;
                }
                doc.showTextAligned(getText(add3), left + 225, top - top2, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);

                // PI/PD FAX NUMBER
                doc.showTextAligned(getText(piPd.getFaxNumber()), left + 35, top - 537, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);

                doc.showTextAligned(getText(piPd.getName()), left + 35, top - 577, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(piPd.getDegree()), left + 200, top - 577, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(piPd.getDegreeYear()), left + 260, top - 577, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(piPd.getTelephoneNum()), left + 310, top - 577, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(piPd.getEmailAddress()), left + 390, top - 577, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }
            // PiCoPiInformation coPi1 = getPerson(cv, "coPiPd1");
            if (coPi1 != null) {
                doc.showTextAligned(getText(coPi1.getName()), left + 35, top - 604, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi1.getDegree()), left + 200, top - 604, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi1.getDegreeYear()), left + 260, top - 604, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi1.getTelephoneNum()), left + 310, top - 604, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi1.getEmailAddress()), left + 390, top - 604, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // PiCoPiInformation coPi2 = getPerson(cv, "coPiPd2");
            if (coPi2 != null) {
                doc.showTextAligned(getText(coPi2.getName()), left + 35, top - 629, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi2.getDegree()), left + 200, top - 629, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi2.getDegreeYear()), left + 260, top - 629, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi2.getTelephoneNum()), left + 310, top - 629, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi2.getEmailAddress()), left + 390, top - 629, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // PiCoPiInformation coPi3 = getPerson(cv, "coPiPd3");
            if (coPi3 != null) {
                doc.showTextAligned(getText(coPi3.getName()), left + 35, top - 656, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi3.getDegree()), left + 200, top - 656, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi3.getDegreeYear()), left + 260, top - 656, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi3.getTelephoneNum()), left + 310, top - 656, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi3.getEmailAddress()), left + 390, top - 656, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }
            // PiCoPiInformation coPi4 = getPerson(cv, "coPiPd4");
            if (coPi4 != null) {
                doc.showTextAligned(getText(coPi4.getName()), left + 35, top - 682, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi4.getDegree()), left + 200, top - 682, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi4.getDegreeYear()), left + 260, top - 682, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi4.getTelephoneNum()), left + 310, top - 682, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(coPi4.getEmailAddress()), left + 390, top - 682, 1, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // 2nd page:
            if (cv.getDebrFlag().equalsIgnoreCase("y")) {
                doc.showTextAligned(getText("x"), left + 464, top - 273, 2, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                doc.showTextAligned(getText(cv.getDebrFlagExpLanation()), left + 225, top - 274, 2, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }
            if (cv.getDebrFlag().equalsIgnoreCase("n")) {
                doc.showTextAligned(getText("x"), left + 538, top - 273, 2, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
            }

            // 3rd page:
            // AOR NAME
            doc.showTextAligned(getText(cv.getAorName()), left + 35, top - 690, 3, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);
            // AOR Ele sign
            doc.showTextAligned(getText(cv.getAorSignature()), left + 290, top - 690, 3, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);
            // sign date
            doc.showTextAligned(getText(cv.getAorSignDate()), left + 460, top - 690, 3, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);
            // telphone
            doc.showTextAligned(getText(cv.getAorTelephoneNum()), left + 35, top - 717, 3, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);
            // email
            doc.showTextAligned(getText(cv.getAorEmailAddress()), left + 178, top - 717, 3, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // fax
            doc.showTextAligned(getText(cv.getAorFaxNum()), left + 425, top - 717, 3, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // 4th page:

            if (cv.getUocs() != null && cv.getUocs().length > 1) {

                int top4 = 125;
                for (int j = 1; j < cv.getUocs().length; j++) {

                    UnitOfConsideration uoc = getSelectedUOC(cv.getUocs(), j);
                    // log.debug("UOC : " + uoc);
                    String nsfOrganizationUnits = "";
                    String divCode = uoc.getDivision().getDivisionCode();
                    String divAbbr = uoc.getDivision().getDivisionAbbrv();
                    String pgmEleCode = uoc.getProgramElement().getProgramElementCode();
                    String pgmEleName = uoc.getProgramElement().getProgramElementName();

                    if (divCode == null)
                        divCode = "";
                    if (divAbbr == null) {
                        divAbbr = "";
                    } else {
                        divAbbr = " " + divAbbr;
                    }
                    if (pgmEleCode == null)
                        pgmEleCode = "";
                    if (pgmEleName == null) {
                        pgmEleName = "";
                    } else {
                        pgmEleName = " - " + pgmEleName;
                    }

                    nsfOrganizationUnits = divAbbr + pgmEleName;

                    doc.showTextAligned(getText(nsfOrganizationUnits), left + 40, top - top4, 4, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                    top4 = top4 + 15;

                }
            }

            doc.close();
            return outputStream;
        } catch (Exception e) {
            throw new CommonUtilException(e.getMessage());
        }
    }

    private static Paragraph getText(String text) throws CommonUtilException {
        Paragraph dr;
        try {
            String newText = "";
            if (text != null) {
                newText = text;
            }
            dr = new Paragraph(newText).setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN)).setFontSize(10)
                    .setFontColor(ColorConstants.BLACK).setBold();
            return dr;
        } catch (IOException e) {
            throw new CommonUtilException(e.getMessage());
        }
    }

    private static Paragraph getSmallText(String text) throws CommonUtilException {
        Paragraph dr;
        try {
            String newText = "";
            if (text != null) {
                newText = text;
            }
            dr = new Paragraph(newText).setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN)).setFontSize(6.5f)
                    .setFontColor(ColorConstants.BLACK).setBold();
            return dr;
        } catch (IOException e) {
            throw new CommonUtilException(e.getMessage());
        }
    }

    private static Paragraph getProposalText(String text) throws CommonUtilException {
        Paragraph dr;
        try {
            String newText = "";
            if (text != null) {
                newText = text;
            }
            dr = new Paragraph(newText).setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN)).setFontSize(35)
                    .setFontColor(ColorConstants.BLACK).setBold();
            return dr;
        } catch (IOException e) {
            throw new CommonUtilException(e.getMessage());
        }
    }

    private static Paragraph getFooterText(String text) throws CommonUtilException {
        Paragraph dr;
        try {
            String newText = "";
            if (text != null) {
                newText = text;
            }
            dr = new Paragraph(newText).setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN)).setFontSize(8)
                    .setFontColor(ColorConstants.BLACK);// .setBold();
            return dr;
        } catch (IOException e) {
            throw new CommonUtilException(e.getMessage());
        }
    }

    private static Paragraph getRevisedBudgetStampingText(String text) throws CommonUtilException {
        Paragraph dr;
        try {
            dr = new Paragraph(text).setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN)).setFontSize(8)
                    .setFontColor(ColorConstants.RED).setBold();
            return dr;
        } catch (IOException e) {
            throw new CommonUtilException(e.getMessage());
        }
    }

    private static Paragraph getText(String text, int fontSize) throws CommonUtilException {
        Paragraph dr;
        try {
            dr = new Paragraph(text).setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN)).setFontSize(fontSize)
                    .setFontColor(ColorConstants.BLACK).setBold();
            return dr;
        } catch (IOException e) {
            throw new CommonUtilException("Got Error : ", e);
        }
    }

    public static Document getDocument(ByteArrayOutputStream outputStream, String src) throws CommonUtilException {
        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), writer);
            Document document = new Document(pdfDoc);
            return document;
        } catch (Exception e) {
            throw new CommonUtilException("Got Error : ", e);
        }
    }

    /**
     * This methtod will return particular UOC based on orderNum
     * 
     * @param uocs
     * @param orderNum
     * @return
     */
    public static UnitOfConsideration getSelectedUOC(UnitOfConsideration[] uocs, int orderNum) {

        log.debug("Requested Order Num : " + orderNum + " Size: " + uocs.length);
        UnitOfConsideration uoc = null;
        if (uocs.length > 0) {
            for (int i = 0; i < uocs.length; i++) {
                log.debug("for : " + uocs[i].getDirectorate().getDirectorateAbbrv());
                if (uocs[i].getUocOrdrNum() == orderNum) {
                    uoc = uocs[i];
                    log.debug("Checking UOC {" + i + "} : " + uoc.getUocOrdrNum() + " :: "
                            + uoc.getDivision().getDivisionAbbrv() + " :: "
                            + uoc.getProgramElement().getProgramElementName());
                    return uoc;
                }
            }
        }
        return uoc;
    }

    /**
     * 
     * @param propPrepId
     * @param src
     * @param bp
     * @return
     * @throws CommonUtilException
     */
    public static ByteArrayOutputStream generateBudgetPage(String propPrepId, String src, BudgetPdf bp)
            throws CommonUtilException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Document doc = getDocument(outputStream, src);
            log.debug("No of Pages : " + doc.getPdfDocument().getNumberOfPages());
            float left = doc.getPdfDocument().getPage(1).getPageSize().getLeft();
            float top = doc.getPdfDocument().getPage(1).getPageSize().getTop();

            // Page Number
            String pageNum = "YEAR   " + bp.getPageNum();
            if ("C".equalsIgnoreCase(bp.getPageNum())) {
                pageNum = "Cummulative";
            }
            doc.showTextAligned(getText(pageNum, 8), left + 395, top - 50, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // ORGANIZATION
            doc.showTextAligned(getText(bp.getOrganization()), left + 80, top - 84, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // PROPOSAL NO.
            doc.showTextAligned(getText(bp.getProposalNum()), left + 405, top - 84, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // PRINCIPAL INVESTIGATOR / PROJECT DIRECTOR
            doc.showTextAligned(getText(bp.getPrincipalInvestigator()), left + 80, top - 106, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // AWARD NO.
            doc.showTextAligned(getText(bp.getAwardNum()), left + 405, top - 106, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // DURATION (months)
            // Proposed
            doc.showTextAligned(getText(bp.getProposedMonths()), left + 478, top - 106, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);
            // Granted
            doc.showTextAligned(getText(bp.getGrantedMonths()), left + 515, top - 106, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // SENIOR PERSONNEL: PI/PD, Co-PI’s,

            for (int i = 0; i < bp.getBudgetSrPersList().size(); i++) {

                BudgetSeniorPersonnel bSrPer = bp.getBudgetSrPersList().get(i);

                Map<String, Float> srMarginMap = new HashMap<String, Float>();
                srMarginMap.put("left1", left + 95f); // 95,150,390,420,450,505//150
                srMarginMap.put("left2", left + 358f);
                srMarginMap.put("left3", left + 390f);
                srMarginMap.put("left4", left + 420f);
                srMarginMap.put("left5", left + 445f);
                srMarginMap.put("left6", left + 505f);

                switch (i) {
                case 0:
                    srMarginMap.put("top", top - 141f);
                    stampSrPerson(bSrPer, srMarginMap, doc);
                    break;
                case 1:
                    srMarginMap.put("top", top - 152f);
                    stampSrPerson(bSrPer, srMarginMap, doc);
                    break;
                case 2:
                    srMarginMap.put("top", top - 163f);
                    stampSrPerson(bSrPer, srMarginMap, doc);
                    break;
                case 3:
                    srMarginMap.put("top", top - 174f);
                    stampSrPerson(bSrPer, srMarginMap, doc);
                    break;
                case 4:
                    srMarginMap.put("top", top - 185f);
                    stampSrPerson(bSrPer, srMarginMap, doc);
                    break;
                case 5:
                    srMarginMap.put("top", top - 196f);
                    stampSrPerson(bSrPer, srMarginMap, doc);
                    break;
                default:

                }

            }

            // OTHERS (LIST INDIVIDUALLY ON BUDGET JUSTIFICATION PAGE)

            doc.showTextAligned(getText(bp.getTotalOtherSrPerCount()), left + 98, top - 197, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(bp.getTotalOtherSrPerCalMonths()), left + 358, top - 197, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getAcadMonths(bp.getBudgetSrPersList())), left + 390, top - 197, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getSumMonths(bp.getBudgetSrPersList())), left + 420, top - 197, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(bp.getTotalOtherSrPerNsfReqFunds()), left + 445, top - 197, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 197, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // TOTAL SENIOR PERSONNEL (1 - 6)
            doc.showTextAligned(getText(bp.getTotalSrPrCount()), left + 98, top - 208, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(bp.getTotalSrPrCalMonth()), left + 358, top - 208, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getAcadMonths(bp.getBudgetSrPersList())), left + 390, top - 208, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getSumMonths(bp.getBudgetSrPersList())), left + 420, top - 208, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            // Funds Requested By proposer
            doc.showTextAligned(getText(bp.getTotalSeniorPersonnelNsfReqFunds()), left + 445, top - 208, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // getFundGrantedByNsf
            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 208, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // B. OTHER PERSONNEL (SHOW NUMBERS IN BRACKETS)

            List<OtherPersonnelCost> othPerList = bp.getOtherPersonnelList();

            if (othPerList != null && !othPerList.isEmpty()) {
                Map<String, Float> othPersMarginMap = new HashMap<String, Float>();
                othPersMarginMap.put("left1", left + 100f);
                othPersMarginMap.put("left2", left + 358f);
                othPersMarginMap.put("left3", left + 390f);
                othPersMarginMap.put("left4", left + 420f);
                othPersMarginMap.put("left5", left + 445f);
                othPersMarginMap.put("left6", left + 505f);
                for (OtherPersonnelCost oc : othPerList) {
                    String code = oc.getOtherPersonTypeCode();

                    switch (code) {

                    // 1. () POST DOCTORAL SCHOLARS
                    case Constants.CODE_STUDENTS_POST_DOCTORAL:
                        othPersMarginMap.put("top", top - 231f);
                        stampOtherPersonal(oc, othPersMarginMap, doc);
                        break;

                    // 2. () OTHER PROFESSIONALS (TECHNICIAN, PROGRAMMER, ETC.)
                    case Constants.CODE_OTHER_PROFESSIONALS:
                        othPersMarginMap.put("top", top - 242f);
                        stampOtherPersonal(oc, othPersMarginMap, doc);
                        break;

                    // 3. () GRADUATE STUDENTS
                    case Constants.CODE_STUDENTS_GRADUATE:
                        othPersMarginMap.put("top", top - 253f);
                        stampOtherPersonal(oc, othPersMarginMap, doc);
                        break;

                    // 4. () UNDERGRADUATE STUDENTS
                    case Constants.CODE_STUDENTS_UNDERGRADUATE:
                        othPersMarginMap.put("top", top - 264f);
                        stampOtherPersonal(oc, othPersMarginMap, doc);
                        break;

                    // 5. () SECRETARIAL - CLERICAL (IF CHARGED DIRECTLY)
                    case Constants.CODE_CLERICAL:
                        othPersMarginMap.put("top", top - 275f);
                        stampOtherPersonal(oc, othPersMarginMap, doc);
                        break;

                    // 6. () OTHER
                    case Constants.CODE_OTHER:
                        othPersMarginMap.put("top", top - 286f);
                        stampOtherPersonal(oc, othPersMarginMap, doc);
                        break;

                    }
                }
            }

            // TOTAL SALARIES AND WAGES (A + B)
            doc.showTextAligned(getText(bp.getTotalSalariesAndWages()), left + 443, top - 298, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 298, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // C. FRINGE BENEFITS (IF CHARGED AS DIRECT COSTS)
            doc.showTextAligned(getText(bp.getFringeBenefits()), left + 445, top - 309, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 309, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // TOTAL SALARIES, WAGES AND FRINGE BENEFITS (A + B + C)
            doc.showTextAligned(getText(bp.getTotalSalariesWagesAndFingeBenefits()), left + 443, top - 320, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 320, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // D. EQUIPMENT (LIST ITEM AND DOLLAR AMOUNT FOR EACH ITEM EXCEEDING
            List<EquipmentCost> eqCstList = bp.getEquipmentList();
            if (eqCstList != null && !eqCstList.isEmpty()) {
                // 1
                if (eqCstList.size() > 0) {
                    doc.showTextAligned(getText(eqCstList.get(0).getEquipmentName()), left + 80, top - 343, 1,
                            TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

                    doc.showTextAligned(getText("$ " + eqCstList.get(0).getEquipmentDollarAmount().toString()),
                            left + 385, top - 343, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
                }
                // 2
                if (eqCstList.size() > 1) {
                    doc.showTextAligned(getText(eqCstList.get(1).getEquipmentName()), left + 80, top - 355, 1,
                            TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

                    doc.showTextAligned(getText("$ " + eqCstList.get(1).getEquipmentDollarAmount().toString()),
                            left + 385, top - 355, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
                }
                // 3
                if (eqCstList.size() > 2) {
                    doc.showTextAligned(getText(eqCstList.get(2).getEquipmentName()), left + 80, top - 367, 1,
                            TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

                    doc.showTextAligned(getText("$ " + eqCstList.get(2).getEquipmentDollarAmount().toString()),
                            left + 385, top - 367, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
                }

                // 4
                if (eqCstList.size() > 3) {
                    doc.showTextAligned(getText("Others (See Budget Comments Page...)"), left + 80, top - 378, 1,
                            TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
                }
            }

            // TOTAL EQUIPMENT
            doc.showTextAligned(getText(bp.getTotalEquipment()), left + 445, top - 387, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 387, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // E. TRAVEL 1. DOMESTIC (INCL. U.S. POSSESSIONS)
            doc.showTextAligned(getText(bp.getDomestic()), left + 445, top - 398, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 398, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // 2. INTERNATIONAL
            doc.showTextAligned(getText(bp.getInternational()), left + 445, top - 409, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 409, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // F. PARTICIPANT SUPPORT COSTS
            // 1. STIPENDS $
            doc.showTextAligned(getText(bp.getStipends()), left + 228, top - 450, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // 2. TRAVEL
            doc.showTextAligned(getText(bp.getTravel()), left + 228, top - 461, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // 3. SUBSISTENCE
            doc.showTextAligned(getText(bp.getSubsistence()), left + 228, top - 472, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // 4. OTHER
            doc.showTextAligned(getText(bp.getOtherParticipant()), left + 228, top - 483, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // TOTAL NUMBER OF PARTICIPANTS ( ) TOTAL PARTICIPANT COSTS
            doc.showTextAligned(getText(bp.getTotalNumberOfParticipants()), left + 240, top - 498, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(bp.getTotalParticipantCosts()), left + 443, top - 498, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 498, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // G. OTHER DIRECT COSTS
            // 1. MATERIALS AND SUPPLIES
            doc.showTextAligned(getText(bp.getMaterialsAndSupplies()), left + 445, top - 521, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 521, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // 2. PUBLICATION COSTS/DOCUMENTATION/DISSEMINATION
            doc.showTextAligned(getText(bp.getPublicationCosts()), left + 445, top - 532, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 532, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // 3. CONSULTANT SERVICES
            doc.showTextAligned(getText(bp.getConsultantServices()), left + 445, top - 543, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 543, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // 4. COMPUTER SERVICES
            doc.showTextAligned(getText(bp.getComputerServices()), left + 445, top - 554, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 554, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // 5. SUBAWARDS
            doc.showTextAligned(getText(bp.getSubawards()), left + 445, top - 565, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 565, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            // 6. OTHER
            doc.showTextAligned(getText(bp.getOtherDirectCost()), left + 445, top - 577, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 577, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            // TOTAL OTHER DIRECT COSTS
            doc.showTextAligned(getText(bp.getTotalOtherDirectCosts()), left + 445, top - 587, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 587, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            // H. TOTAL DIRECT COSTS (A THROUGH G)
            doc.showTextAligned(getText(bp.getTotaldirectCostsAtoG()), left + 443, top - 599, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 599, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // I. INDIRECT COSTS (F&A)(SPECIFY RATE AND BASE)
            List<IndirectCost> idrList = bp.getIndirectCosts();
            if (idrList != null && !idrList.isEmpty()) {
                if (idrList.size() > 1) {
                    doc.showTextAligned(
                            getText(idrList.get(0).getIndirectCostItemName() + " (Rate: "
                                    + idrList.get(0).getIndirectCostRate() + ", Base:"
                                    + idrList.get(0).getIndirectCostBaseDollarAmount() + ") (Cont. on Comments Page)"),
                            left + 80, top - 618, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
                } else {
                    doc.showTextAligned(
                            getText(idrList.get(0).getIndirectCostItemName() + " (Rate: "
                                    + idrList.get(0).getIndirectCostRate() + ", Base:"
                                    + idrList.get(0).getIndirectCostBaseDollarAmount() + ")"),
                            left + 80, top - 618, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
                }
            }

            // TOTAL INDIRECT COSTS (F&A)
            doc.showTextAligned(getText(bp.getTotalIndirectCosts()), left + 445, top - 633, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 633, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // J. TOTAL DIRECT AND INDIRECT COSTS (H + I)
            doc.showTextAligned(getText(bp.getTotalDirectAndIndirectCosts()), left + 443, top - 644, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 644, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // K. SMALL BUSINESS FEE
            doc.showTextAligned(getText(bp.getSmallBusinessFee()), left + 445, top - 655, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 655, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // L. AMOUNT OF THIS REQUEST (J) OR (J MINUS K)
            doc.showTextAligned(getText(bp.getAmountOfThisRequest()), left + 443, top - 666, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);
            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 505, top - 666, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // M. COST SHARING PROPOSED LEVEL $
            doc.showTextAligned(getText(bp.getCostSharingProposedLevel()), left + 260, top - 677, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // AGREED LEVEL IF DIFFERENT $
            doc.showTextAligned(getText(getFundGrantedByNsf(bp.getBudgetSrPersList())), left + 445, top - 677, 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // PI/PD NAME
            doc.showTextAligned(getText(bp.getPiPdName()), left + 80, top - 699, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // ORG. REP. NAME*
            doc.showTextAligned(getText(bp.getOrgRepName()), left + 80, top - 721, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // Page Number
            doc.showTextAligned(getText(bp.getPageNum()), left + 304, top - 734, 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // Close Document
            doc.close();
            // log.debug("returning os : " + outputStream.size());
            return outputStream;
        } catch (Exception ex) {
            throw new CommonUtilException("Got Error : ", ex);
        }
    }

    /**
     * This method is useful to print Other Personnel information.
     * 
     * @param canvas
     * @param rect
     * @param oc
     * @param marginsMap
     */
    private static void stampOtherPersonal(OtherPersonnelCost oc, Map<String, Float> marginsMap, Document doc)
            throws CommonUtilException {
        // 9 chars -- left1 450
        // 6 chars -- left1 461
        // person count
        try {
            doc.showTextAligned(getText(String.valueOf(oc.getOtherPersonCount())), marginsMap.get("left1"),
                    marginsMap.get("top"), 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // cal month count
            if (oc.getOtherPersonTypeCode().equalsIgnoreCase(Constants.CODE_STUDENTS_POST_DOCTORAL)
                    || oc.getOtherPersonTypeCode().equalsIgnoreCase(Constants.CODE_OTHER_PROFESSIONALS)) {
                doc.showTextAligned(getText(String.valueOf(oc.getOtherPersonMonthCount())), marginsMap.get("left2"),
                        marginsMap.get("top"), 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            }

            // AcadMonths
            doc.showTextAligned(getText(""), marginsMap.get("left3"), marginsMap.get("top"), 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            // SumrMonths
            doc.showTextAligned(getText(""), marginsMap.get("left4"), marginsMap.get("top"), 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(formatDollorAmount(oc.getOtherPersonDollarAmount())), marginsMap.get("left5"),
                    marginsMap.get("top"), 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            // FundGrantedByNsf
            doc.showTextAligned(getText(""), marginsMap.get("left6"), marginsMap.get("top"), 1, TextAlignment.LEFT,
                    VerticalAlignment.BOTTOM, 0);
        } catch (Exception e) {
            throw new CommonUtilException("Got Error : ", e);
        }

    }

    /**
     * 
     * @param bSrPer
     * @param marginsMap
     * @param doc
     * @param left
     * @param top
     * @throws Exception
     */
    private static void stampSrPerson(BudgetSeniorPersonnel bSrPer, Map<String, Float> marginsMap, Document doc)
            throws CommonUtilException {
        // log.debug("Map : " + marginsMap + " SRPersons : " + bSrPer);
        try {
            if (bSrPer.getPersonType().equalsIgnoreCase(Constants.USER_PI)) {
                doc.showTextAligned(getText(bSrPer.getSrPersonFullName() + " - Principal Inv"), marginsMap.get("left1"),
                        marginsMap.get("top"), 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            } else {
                doc.showTextAligned(getText(bSrPer.getSrPersonFullName()), marginsMap.get("left1"),
                        marginsMap.get("top"), 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            }

            doc.showTextAligned(getText(bSrPer.getCalMonth()), marginsMap.get("left2"), marginsMap.get("top"), 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(bSrPer.getAcadMonths()), marginsMap.get("left3"), marginsMap.get("top"), 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(bSrPer.getSumrMonths()), marginsMap.get("left4"), marginsMap.get("top"), 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(bSrPer.getFundsRequested()), marginsMap.get("left5"), marginsMap.get("top"), 1,
                    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);

            doc.showTextAligned(getText(bSrPer.getFundGrantedByNsf()), marginsMap.get("left6"), marginsMap.get("top"),
                    1, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        } catch (Exception e) {
            throw new CommonUtilException("Got Error : ", e);
        }
    }

    /**
     * 
     * @param list
     * @return
     */
    private static String getFundGrantedByNsf(List<BudgetSeniorPersonnel> list) {
        String funds = "";
        if (!list.isEmpty()) {
            funds = list.get(0).getFundGrantedByNsf();
        }
        return funds;
    }

    /**
     * 
     * @param list
     * @return
     */
    private static String getAcadMonths(List<BudgetSeniorPersonnel> list) {
        String months = "";
        if (!list.isEmpty()) {
            months = list.get(0).getAcadMonths();
        }
        return months;
    }

    /**
     * 
     * @param list
     * @return
     */
    private static String getSumMonths(List<BudgetSeniorPersonnel> list) {
        String months = "";
        if (!list.isEmpty()) {
            months = list.get(0).getSumrMonths();
        }
        return months;

    }

    /**
     * 
     * @param amount
     * @return
     */
    public static String formatDollorAmount(BigDecimal amount) {
        NumberFormat nfm = NumberFormat.getNumberInstance(Locale.US);
        return nfm.format(amount.setScale(0, RoundingMode.HALF_UP));
    }

    public static String formatDate(java.util.Date date) {
        final String FORMAT = "MM/dd/yyyy";

        String formatStr = null;

        if (date != null) {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(FORMAT);
            formatStr = format.format(date);
        }
        return formatStr;
    }

    /**
     * Generate table of Contents.
     * 
     * @param src
     * @param pageCountMap
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static ByteArrayOutputStream generateTableOfContents(String src, Map<Section, String> pageCountMap)
            throws CommonUtilException {
        try {
            log.debug("@@@@ Genearting TOC ....");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document doc = getDocument(outputStream, src);
            float left = doc.getPdfDocument().getPage(1).getPageSize().getLeft();
            float top = doc.getPdfDocument().getPage(1).getPageSize().getTop();

            for (Map.Entry<Section, String> pagCount : pageCountMap.entrySet()) {

                Paragraph pageCountTxt = null;
                Paragraph pageDmpPmpCountTxt = null;
                int dmpPageCnt = 0;
                if (pagCount.getValue() != null) {
                    if (pagCount.getKey().getName().equalsIgnoreCase(Section.DMP.getName())
                            || pagCount.getKey().getName().equalsIgnoreCase(Section.PMP.getName())) {
                        dmpPageCnt += Integer.parseInt(pagCount.getValue());
                        pageDmpPmpCountTxt = getText(String.valueOf(dmpPageCnt));
                    } else {
                        pageCountTxt = getText(pagCount.getValue());
                    }
                    log.debug("Section Name : " + pagCount.getKey().getName() + " PageCount : " + pageCountTxt
                            + " pageDmpPmpCountTxt : " + pageDmpPmpCountTxt);
                }
                // else {
                // pageCountTxt = printErrorTOCText(
                // String.format(Constants.GENERATE_SECTION_PDF_ERROR_MSG_TEMPLATE,
                // pagCount.getKey().getName()));
                // }

                switch (pagCount.getKey()) {

                case COVER_SHEET:
                    break;
                case PROJ_SUMM:
                    // Project Summary
                    doc.showTextAligned(pageCountTxt, left + 415, top - 167, 1, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                    break;
                case TABLE_OF_CONTENTS:
                    // Table of Contents
                    doc.showTextAligned(getText("1"), left + 415, top - 190, 1, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                    break;
                case PROJ_DESC:
                    // Project Description
                    doc.showTextAligned(pageCountTxt, left + 415, top - 214, 1, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                    break;
                case REF_CITED:
                    // References Cited
                    doc.showTextAligned(pageCountTxt, left + 415, top - 272, 1, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                    break;
                case BIOSKETCH:
                    // Biographical Sketches
                    doc.showTextAligned(pageCountTxt, left + 415, top - 296, 1, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                    break;
                case BUDGETS:
                    // Budget
                    doc.showTextAligned(pageCountTxt, left + 415, top - 320, 1, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                    break;
                case COA:
                    break;
                case CURR_PEND_SUPP:
                    doc.showTextAligned(pageCountTxt, left + 415, top - 366, 1, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                    break;
                case FER:
                    // Facilities, Equipment and Other Resources
                    doc.showTextAligned(pageCountTxt, left + 415, top - 390, 1, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                    break;
                // case APPENDIX:
                // break;
                // case SUPPL:
                // break;
                case RES_PRIOR_SUPP:
                    break;
                case DMP:
                    // DMP + PMP
                    doc.showTextAligned(pageDmpPmpCountTxt, left + 415, top - 414, 1, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                    break;
                case COLLAB_PLAN:
                    break;
                case MGT_PLAN:
                    break;
                case PMP:
                    break;
                case DEV_AUTH:
                    break;
                case LOS:
                    break;
                case RIS:
                    break;
                case SRL:
                    break;
                default:
                    break;
                }
            }
            doc.close();
            return outputStream;
        } catch (Exception e) {
            throw new CommonUtilException("Got Error : ", e);
        }
    }

    /**
     * This method is will create concatenated Pdf.
     * 
     * @param sectionPDFs
     * @return
     * @throws CommonUtilException
     */
    public static ByteArrayOutputStream concatenatePDFs(Map<String, ByteArrayOutputStream> sectionPDFs)
            throws CommonUtilException {
        log.debug("@@@ Concatenating Pdfs.....");
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
            PdfMerger merger = new PdfMerger(pdfDoc);
            PdfOutline rootOutline = pdfDoc.getOutlines(false);
            PdfOutline secOutline = rootOutline.addOutline("Proposal Sections");

            PdfDocument secDoc;
            Document document = new Document(pdfDoc);

            // page count
            int page = 1;

            for (Map.Entry<String, ByteArrayOutputStream> sectionPDFEntry : sectionPDFs.entrySet()) {
                if (sectionPDFEntry.getValue() != null) {
                    secDoc = new PdfDocument(
                            new PdfReader(new ByteArrayInputStream(sectionPDFEntry.getValue().toByteArray())));
                    merger.merge(secDoc, 1, secDoc.getNumberOfPages());
                    PdfOutline newSecOutline = secOutline.addOutline(sectionPDFEntry.getKey());
                    newSecOutline.addDestination(PdfExplicitDestination.createFit(pdfDoc.getPage(page)));
                    page += secDoc.getNumberOfPages();
                }
            }
            document.close();
        } catch (Exception e) {
            throw new CommonUtilException(e);
        }
        return baos;
    }

    /**
     * This method will print page numbers
     * 
     * @param proposalDocument
     * @param headerText
     * @param footerText
     * @return
     * @throws CommonUtilException
     */
    public static ByteArrayOutputStream addPageNumbers(ByteArrayOutputStream proposalDocument, String headerText,
            String footerText) throws CommonUtilException {

        try {

            InputStream is = new ByteArrayInputStream(proposalDocument.toByteArray());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(is), new PdfWriter(baos));
            Document doc = new Document(pdfDoc);

            float left = doc.getPdfDocument().getPage(1).getPageSize().getLeft();
            float top = doc.getPdfDocument().getPage(1).getPageSize().getTop();
            float bottom = doc.getPdfDocument().getPage(1).getPageSize().getBottom();
            int totalPages = pdfDoc.getNumberOfPages();
            log.debug("### Total no pages : " + totalPages);

            for (int currPage = 1; currPage <= totalPages; currPage++) {
                doc.showTextAligned(getFooterText(headerText), left + 35, top - 50, currPage, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);
                String pageNumber = String.format("Page %d of %d", currPage, totalPages);
                doc.showTextAligned(getText(pageNumber), 285, 35, currPage, TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM, 0);

                if (footerText.contains("Submitted/PI")) {
                    doc.showTextAligned(getFooterText(footerText), left + 340, top - 38, currPage, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                } else {
                    doc.showTextAligned(getFooterText(footerText), left + 35, bottom + 20, currPage, TextAlignment.LEFT,
                            VerticalAlignment.BOTTOM, 0);
                }
            }
            doc.close();
            is.close();
            return baos;
        } catch (Exception e) {
            throw new CommonUtilException(e);
        }
    }

    /**
     * This method will create concatenated budget Pdf.
     * 
     * @param budgPageList
     * @return
     * @throws CommonUtilException
     */
    public static ByteArrayOutputStream createBudgetPdf(List<ByteArrayOutputStream> budgPageList)
            throws CommonUtilException {

        log.debug("@@@@ Genearting budget pdf for Multiple years.......!!");
        try {
            ByteArrayOutputStream budgPdf = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(budgPdf));
            PdfMerger merger = new PdfMerger(pdfDoc);
            Document doc = new Document(pdfDoc);
            PdfDocument budgDoc;

            for (int i = 0; i < budgPageList.size(); i++) {
                InputStream is = new ByteArrayInputStream(budgPageList.get(i).toByteArray());
                budgDoc = new PdfDocument(new PdfReader(is));
                log.debug("Each Budg Page : " + budgDoc.getNumberOfPages());
                merger.merge(budgDoc, 1, budgDoc.getNumberOfPages());
                is.close();
            }
            doc.close();
            return budgPdf;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonUtilException(Constants.GENERATE_PDF_BUDJ_ERROR, e);
        }
    }

    /**
     * This method is used to generate new pdf page for budget line items for
     * each year.
     * 
     * @param Year
     * @return
     * @throws DocumentException
     */
    public static ByteArrayOutputStream generateSummaryProposalBudgetCommentsPdf(BudgetPdf budgPage)
            throws CommonUtilException {
        log.debug("@@@ Generating generateSummaryProposalBudgetCommentsPdf ....");
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = getDocument(outputStream);

            Paragraph commentsText = null;
            Paragraph mainHeader = null;
            List<EquipmentCost> eqipCstList = budgPage.getEquipmentList();
            List<IndirectCost> idrCstList = budgPage.getIndirectCosts();
            List<SeniorPersonnelCost> srPrCostlList = budgPage.getSrPrCostlList();

            mainHeader = getText(Constants.PDF_COMMENTS_TITLE + " - " + budgPage.getPageNum())
                    .setTextAlignment(TextAlignment.CENTER);
            // draw line
            drawLine(document);
            document.add(mainHeader);

            // mainHeader.setAlignment(Element.ALIGN_CENTER);
            // document.add(mainHeader);
            // Chunk linebreak = new Chunk(new LineSeparator());
            // document.add(linebreak);
            StringBuilder data = new StringBuilder();
            if (srPrCostlList != null && srPrCostlList.size() > 5) {
                data.append("Other Senior Personnel\n");
                data.append("Name - Title            ").append("Cal                     ")
                        .append("Funds Requested         ");
                data.append("\n----------------           ").append("----------------          ")
                        .append("----------------        \n");

                for (int i = 5; i < srPrCostlList.size(); i++) {
                    SeniorPersonnelCost srPrCost = srPrCostlList.get(i);
                    String mi = "";
                    if (srPrCost.getSeniorPersonMiddleInitial() != null) {
                        mi = srPrCost.getSeniorPersonMiddleInitial();
                        mi = mi + " ";
                    }
                    String name = srPrCost.getSeniorPersonFirstName() + " " + mi + srPrCost.getSeniorPersonLastName();
                    String dollarAmt = formatDollorAmount(srPrCost.getSeniorPersonDollarAmount());
                    double calMonth = srPrCost.getSeniorPersonMonthCount();

                    if (name.length() < 30) {
                        for (int j = name.length(); j < 30; j++) {
                            name = name + ' ';
                        }
                    }

                    String cal = String.valueOf(calMonth);

                    if (cal.length() < 30) {
                        for (int j = cal.length(); j < 30; j++) {
                            cal = cal + ' ';
                        }
                    }

                    data.append(name + cal + dollarAmt + "\n");
                }
            }
            if (eqipCstList != null && eqipCstList.size() > 3) {
                log.debug("PdfUtil.generateSummaryProposalBudgetCommentsPdf.eqipCstList :: " + eqipCstList.size());
                data.append("\n\n** D- Equipment\n");
                for (int i = 3; i < eqipCstList.size(); i++) {
                    EquipmentCost ec = eqipCstList.get(i);
                    String dollarAmt = formatDollorAmount(ec.getEquipmentDollarAmount());
                    data.append(ec.getEquipmentName() + "     " + dollarAmt + "\n");
                }
            }
            if (idrCstList != null && idrCstList.size() > 1) {
                log.debug("PdfUtil.generateSummaryProposalBudgetCommentsPdf.idrCstList :: " + idrCstList.size());
                data.append("\n\n** I- Indirect Costs\n");
                for (int i = 1; i < idrCstList.size(); i++) {
                    IndirectCost ic = idrCstList.get(i);
                    String dollarAmt = formatDollorAmount(ic.getIndirectCostBaseDollarAmount());
                    String rate = String.valueOf(ic.getIndirectCostRate());
                    data.append(ic.getIndirectCostItemName() + "     (Rate: " + rate + ", Base: " + dollarAmt + ")\n");

                }
            }
            commentsText = new Paragraph(data.toString());
            document.add(commentsText);

            document.close();
            return outputStream;
        } catch (Exception e) {
            throw new CommonUtilException("Got Error : ", e);
        }
    }

    /**
     * 
     * @param document
     */
    private static void drawLine(Document document) {
        // Creating a new page
        PdfPage pdfPage = document.getPdfDocument().addNewPage();
        // Creating a PdfCanvas object
        PdfCanvas canvas = new PdfCanvas(pdfPage);
        float left = document.getPdfDocument().getDefaultPageSize().getLeft();
        float top = document.getPdfDocument().getDefaultPageSize().getTop();
        float right = document.getPdfDocument().getDefaultPageSize().getRight();
        float bottom = document.getPdfDocument().getDefaultPageSize().getBottom();
        log.debug("Margins LEFT : " + left + " Right : " + right + " TOP : " + top + " Botton : " + bottom);
        canvas.moveTo(left + 35, top - 83).lineTo(right - 35, top - 83).closePathStroke();
    }

    /**
     * This method Stamps revised budget message on each page of Revised budget
     * pdf.
     * 
     * @param proposalDocument
     * @param footerText
     * @return
     * @throws CommonUtilException
     */
    public static ByteArrayOutputStream stampForRevisedBudget(ByteArrayOutputStream proposalDocument, String footerText)
            throws CommonUtilException {

        log.debug("@@@@@@@@ Creating Revised Budget Stamping Pdf......!!!");
        ByteArrayOutputStream stampedDocument = new ByteArrayOutputStream();
        try {
            InputStream is = new ByteArrayInputStream(proposalDocument.toByteArray());
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(is), new PdfWriter(stampedDocument));
            Document doc = new Document(pdfDoc);

            float left = doc.getPdfDocument().getPage(1).getPageSize().getLeft();
            float bottom = doc.getPdfDocument().getPage(1).getPageSize().getBottom();
            int totalPages = pdfDoc.getNumberOfPages();

            for (int currPage = 1; currPage <= totalPages; currPage++) {

                doc.showTextAligned(getRevisedBudgetStampingText(footerText), left + 35, bottom + 20, currPage,
                        TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            }

            doc.close();
        } catch (Exception e) {
            throw new CommonUtilException("Got error while stamping for revised budget : ", e);
        }
        return stampedDocument;
    }

    /**
     * This method returns total number of pages.
     * 
     * @param bios
     * @return
     * @throws CommonUtilException
     */
    public static int getPdfPageCount(ByteArrayOutputStream bios) throws CommonUtilException {
        int totalNoPages = 0;
        try {
            InputStream is = new ByteArrayInputStream(bios.toByteArray());
            PdfDocument pdfDocument = new PdfDocument(new PdfReader(is));
            totalNoPages = pdfDocument.getNumberOfPages();
            pdfDocument.close();
        } catch (Exception e) {
            throw new CommonUtilException("Got error while retrieving total number of pages from Pdf : ", e);
        }
        return totalNoPages;
    }

    /**
     * This method is useful to generate COA PDF.
     *
     * @param coa
     * @return
     * @throws Exception
     * @throws DocumentException
     */
    public static ByteArrayOutputStream createCOAPDF(COA coa) throws Exception {
        log.debug("@@@ PdfUtil.createCOAPDF()...Generating COA Pdf.........");
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = getDocument(outputStream);

            // Add COA Description
            // document.add(coaDescriptionText());
            // document.add(Chunk.NEWLINE);

            // Table 1 Description
            // document.add(tableADescription().setBold());
            document.add(getDisambiguateText("1"));
            document.add(createTableA(coa.getAffiliations()));
            // document.add(Chunk.NEWLINE);

            // Table 2
            // document.add(tableBDescription().setBold());
            document.add(new Paragraph("\n"));
            document.add(getDisambiguateText("2"));
            document.add(createTable2(coa.getRelationships()));
            // document.add(Chunk.NEWLINE);

            // Table 3:
            // document.add(tableCDescription().setBold());
            document.add(new Paragraph("\n"));
            document.add(getDisambiguateText("3"));
            document.add(createTable3(coa.getAdvisees()));
            // document.add(Chunk.NEWLINE);

            // Table 4:
            // document.add(tableDDescription().setBold());
            document.add(new Paragraph("\n"));
            document.add(getDisambiguateText("4"));
            document.add(createTable4(coa.getCollaborators()));
            // document.add(Chunk.NEWLINE);

            // Table 5:
            // document.add(tableEDescription().setBold());
            document.add(new Paragraph("\n"));
            document.add(getDisambiguateText("5"));
            document.add(createTable5(coa.getCoEditors()));

            document.close();
            return outputStream;
        } catch (Exception e) {
            throw new CommonUtilException("Got Error : ", e);
        }
    }

    private static PdfFont getCoaTextFont() throws Exception {
        return PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

    }

    public static Cell createDataCell(String content) throws Exception {
        // log.debug("@@@@@@@@@ PdfUtil.createDataCell() ..........." +
        // content);
        if (content == null) {
            content = " ";
        }
        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        Paragraph text = new Paragraph(content).setFont(font).setFontSize(9);
        Cell cell = new Cell().add(text.setHorizontalAlignment(HorizontalAlignment.LEFT));
        // Font dataFont = new Font(FontFamily.TIMES_ROMAN, 9, Font.NORMAL,
        // GrayColor.BLACK);
        // cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        // cell.setBorderColor(BaseColor.BLACK);
        return cell;
    }

    public static Cell createHederCell(String content) throws Exception {
        // Font headerFont = new Font(FontFamily.TIMES_ROMAN, 9, Font.BOLD,
        // GrayColor.BLACK);
        // Cell cell = new Cell(new Phrase(content, headerFont));
        // cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        // cell.setBorderColor(BaseColor.BLACK);
        // cell.setBackgroundColor(GrayColor.CYAN);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        Paragraph text = new Paragraph(content).setFont(font).setFontSize(FONT_SIZE).setBold();
        Cell cell = new Cell().add(text.setHorizontalAlignment(HorizontalAlignment.LEFT))
                .setBackgroundColor(new DeviceRgb(232, 232, 232));
        return cell;
    }

    public static Paragraph getDisambiguateText(String tableNo) throws Exception {
        // Font textFont = new Font(FontFamily.TIMES_ROMAN, 8, Font.BOLDITALIC,
        // GrayColor.MAGENTA);
        // Paragraph pe = new Paragraph();
        // pe.setTabSettings(new TabSettings(375f));
        // pe.add(Chunk.TABBING);
        // pe.add(new Chunk("to disambiguate common names", textFont));

        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        Paragraph pe = new Paragraph();
        pe.setTextAlignment(TextAlignment.LEFT);
        pe.add("Table " + tableNo).setFont(font).setFontSize(FONT_SIZE).setFontColor(ColorConstants.BLACK).setBold();
        return pe;
    }

    public static Paragraph coaDescriptionText() throws Exception {

        Paragraph text = new Paragraph(
                "The following information regarding collaborators and other affiliations (COA) must be separately provided for each individual identified "
                        + "as senior project personnel.  The COA information must be provided through use of this COA template."
                        + "\n\nPlease complete this template (e.g., Excel, Google Sheets, LibreOffice), save as .xlsx or .xls, and upload directly as a Fastlane "
                        + "Collaborators and Other Affiliations single copy doc.  Do not upload .pdf."
                        + "\n\nIf there are more than 10 individuals designated as senior project personnel on the proposal, or if there are print preview issues, "
                        + "each completed template must be saved as a .txt file [select the Text (Tab Delimited) option] rather than as an .xlsx or .xls file. This "
                        + "format will still enable preservation of searchable text and avoid delays in processing and review of the proposal. Please note that some "
                        + "information requested in prior versions of the PAPPG is no longer requested.  THIS IS PURPOSEFUL AND WE NO LONGER REQUIRE THIS INFORMATION "
                        + "TO BE REPORTED.   Certain relationships will be reported in other sections (i.e., the names of postdoctoral scholar sponsors should not be "
                        + "reported, however if the individual collaborated on research with their postdoctoral scholar sponsor, then they would be reported as a "
                        + "collaborator). The information in the tables is not required to be sorted, alphabetically or otherwise.\n\nThere are five separate "
                        + "categories of information which correspond to the five tables in the COA template:\n\nCOA template Table 1:\nList the individual’s "
                        + "last name, first name, middle initial, and organizational affiliation in the last 12 months."
                        + "\n\nCOA template Table 2:\nList names as last name, first name, middle initial, for whom a personal, family, or business relationship "
                        + "would otherwise preclude their service as a reviewer.\n\nCOA template Table 3:\nList names as last name, first name, middle initial, and "
                        + "provide organizational affiliations, if known, for the following: The individual’s Ph.D. advisors; and  All of the individual’s Ph.D. thesis "
                        + "advisees.\n\nCOA template Table 4:\nList names as last name, first name, middle initial, and provide organizational affiliations, if known, for "
                        + "the following: Co-authors on any book, article, report, abstract or paper with collaboration in the last 48 months (publication date may be later); "
                        + "and Collaborators on projects, such as funded grants, graduate research or others in the last 48 months. \n\nCOA template Table 5: \nList editorial board, "
                        + "editor-in chief and co-editors with whom the individual interacts.  An editor-in-chief must list the entire editorial board.   Editorial Board:   "
                        + "List name(s) of editor-in-chief and journal in the past 24 months; and  Other co-Editors of journal or collections with whom the individual has "
                        + "directly interacted in the last 24 months.\n\nThe template has been developed to be fillable, however, the content and format requirements must "
                        + "not be altered by the user.  This template must be saved in .xlsx or .xls format, and directly uploaded into FastLane as a Collaborators and "
                        + "Other Affiliations Single Copy Document.  Using the .xlsx or .xls format will enable preservation of searchable text that otherwise would be lost.  "
                        + "It is therefore imperative that this document be uploaded in .xlsx or .xls only.  Uploading a document in any format other than .xlsx or .xls"
                        + " may delay the timely processing and review of the proposal.\n\nThis information is used to manage reviewer selection.  See Exhibit II-2 for "
                        + "additional information on potential reviewer conflicts.\n1 Note that graduate advisors are no longer required to be reported.\n2 Editorial Board "
                        + "does not include Editorial Advisory Board, International Advisory Board, Scientific Editorial Board, or any other subcategory of Editorial Board.  "
                        + "It is limited to those individuals who perform editing duties or manage the editing process (i.e., editor in chief).\n\nList names as Last Name, "
                        + "First Name, Middle Initial.  Additionally, provide email, organization, and department (optional) to disambiguate common names.  Fixed column "
                        + "widths keep this sheet one page wide; if you cut and paste text, set font size at 10pt or smaller, and abbreviate, where necessary, to make the "
                        + "data fit.\nTo insert n blank rows, select n row numbers to move down, right click, and choose Insert from the menu. \n\nYou may fill-down (crtl-D) "
                        + "to mark a sequence of collaborators, or copy affiliations.  Excel has arrows that enable sorting. For \"Last Active Date\" and \"Last Active\" "
                        + "columns dates are optional, but will help NSF staff easily determine which information remains relevant for reviewer selection.  \"Last Active Date\" "
                        + "and \"Last Active\" columns may be left blank for ongoing or current affiliations.")
                                .setFont(getCoaTextFont()).setFontSize(FONT_SIZE);

        text.setFixedLeading(12);
        return text;
    }

    public static Paragraph tableADescription() throws Exception {

        Paragraph tableADesc = new Paragraph(
                "Table 1: List the individual’s last name, first name, middle initial, and organizational affiliation in the last 12 months.")
                        .setFont(getCoaTextFont());
        tableADesc.setFontSize(FONT_SIZE).setFixedLeading(12);

        return tableADesc;
    }

    public static Table createTableA(List<Affiliation> Affiliation) throws Exception {
        Table tableA = new Table(4);// new float[] { 0.5f, 2, 6, 2 });
        tableA.setWidth(TABLE_WIDTH);
        // tableA.setWidth(new float[] { 0.5f, 2, 6, 2 });
        // tableA.setWidthPercentage(100);
        tableA.addCell(createHederCell("1"));
        tableA.addCell(createHederCell("Your Name:"));
        tableA.addCell(createHederCell("Your Organizational Affiliation(s), last 12 mo"));
        tableA.addCell(createHederCell("Last Active Date"));

        // List<Affiliation> Affiliation = coa.getAffiliation();
        if (Affiliation != null && !Affiliation.isEmpty()) {
            for (Affiliation aff : Affiliation) {
                tableA.addCell(createDataCell(" "));
                tableA.addCell(createDataCell(aff.getSrPersName()));
                tableA.addCell(createDataCell(aff.getOrgAfflName()));
                if (aff.getLastActvDate() != null) {
                    tableA.addCell(createDataCell(formatActiveDate(aff.getLastActvDate())));
                } else {
                    tableA.addCell(createEmptyDataCell());
                }
            }
        } else {
            generateEmptyTable(tableA, 3, 4);
        }
        return tableA;
    }

    public static Paragraph tableBDescription() throws Exception {
        Paragraph tableBDesc = new Paragraph(
                "Table 2: List names as last name, first name, middle initial, for whom a personal, family, or business relationship would otherwise preclude their "
                        + "service as a reviewer. \nR:   Additional names for whom some relationship would otherwise preclude their service as a reviewer.")
                                .setFont(getCoaTextFont()).setFontSize(FONT_SIZE);
        tableBDesc.setFixedLeading(12);
        return tableBDesc;
    }

    public static Table createTable3(List<Advisee> Advisee) throws Exception {
        Table tableB = new Table(4);// new float[] { 0.5f, 3, 4, 3 });
        tableB.setWidth(TABLE_WIDTH);
        // tableB.setSpacingRatio(5);
        // tableB.setWidths(new float[] { 0.5f, 3, 4, 3 });
        // tableB.setWidth(100);
        //
        tableB.addCell(createHederCell("3"));
        tableB.addCell(createHederCell("Advisor/Advisee Name:"));
        tableB.addCell(createHederCell("Organizational Affiliation"));
        tableB.addCell(createHederCell("Optional  (email, Department)"));

        // List<Advisee> Advisee = coa.getAdvisee();
        if (Advisee != null && !Advisee.isEmpty()) {
            for (Advisee ad : Advisee) {
                tableB.addCell(createDataCell(ad.getAdviseeTypeCode()));
                tableB.addCell(createDataCell(ad.getAdviseeName()));
                tableB.addCell(createDataCell(ad.getOrgAfflName()));
                tableB.addCell(createDataCell(ad.getEmailDeptName()));
            }
        } else {
            generateEmptyTable(tableB, 3, 4);
        }
        return tableB;
    }

    public static Paragraph tableCDescription() throws Exception {
        Paragraph tableCDesc = new Paragraph(
                "Table 3: List names as last name, first name, middle initial, and provide organizational affiliations, if known, for the "
                        + "following.\nG:  The individual’s Ph.D. advisors; and \nT:  All of the individual’s Ph.D. thesis advisees.")
                                .setFont(getCoaTextFont()).setFontSize(FONT_SIZE);
        tableCDesc.setFixedLeading(12);
        return tableCDesc;
    }

    public static Table createTable4(List<Collaborator> Collaborator) throws Exception {
        Table tableC = new Table(5);// new float[] { 0.5f, 2, 3, 3, 2 });
        tableC.setWidth(TABLE_WIDTH);
        // tableC.setSpacingRatio(5);
        // tableC.setWidths(new float[] { 0.5f, 2, 3, 3, 2 });
        // tableC.setWidthPercentage(100);
        tableC.addCell(createHederCell("4"));
        tableC.addCell(createHederCell("Name:"));
        tableC.addCell(createHederCell("Organizational Affiliation"));
        tableC.addCell(createHederCell("Optional  (email, Department)"));
        tableC.addCell(createHederCell("Last Active Date"));

        if (Collaborator != null && !Collaborator.isEmpty()) {
            for (Collaborator cb : Collaborator) {
                tableC.addCell(createDataCell(cb.getClbrTypeCode()));
                tableC.addCell(createDataCell(cb.getClbrName()));
                tableC.addCell(createDataCell(cb.getOrgAfflName()));
                tableC.addCell(createDataCell(cb.getEmailDeptName()));
                if (cb.getLastActvDate() != null) {
                    tableC.addCell(createDataCell(formatActiveDate(cb.getLastActvDate())));
                } else {
                    tableC.addCell(createEmptyDataCell());
                }
            }
        } else {
            generateEmptyTable(tableC, 3, 5);
        }
        return tableC;
    }

    public static Paragraph tableDDescription() throws Exception {
        Paragraph tableDDesc = new Paragraph(
                "Table 4: List names as last name, first name, middle initial, and provide organizational affiliations, if known, "
                        + "for the following:\nA:  Co-authors on any book, article, report, abstract or paper with collaboration in the "
                        + "last 48 months (publication date may be later); and\nC:  Collaborators on projects, such as funded grants, graduate "
                        + "research or others in the last 48 months.").setFont(getCoaTextFont()).setFontSize(FONT_SIZE);

        tableDDesc.setFixedLeading(12);
        return tableDDesc;
    }

    public static Table createTable5(List<CoEditor> CoEditor) throws Exception {
        Table tableD = new Table(5);// new float[] { 0.5f, 2, 3, 3, 2 });
        tableD.setWidth(TABLE_WIDTH);
        // tableD.setSpacingBefore(5);
        // tableD.setWidths(new float[] { 0.5f, 2, 3, 3, 2 });
        // tableD.setWidthPercentage(100);
        tableD.addCell(createHederCell("5"));
        tableD.addCell(createHederCell("Name:"));
        tableD.addCell(createHederCell("Organizational Affiliation"));
        tableD.addCell(createHederCell("Journal/Collection"));
        tableD.addCell(createHederCell("Last Active Date"));

        if (CoEditor != null && !CoEditor.isEmpty()) {
            for (CoEditor ce : CoEditor) {
                tableD.addCell(createDataCell(ce.getCoEditorTypeCode()));
                tableD.addCell(createDataCell(ce.getCoEditorName()));
                tableD.addCell(createDataCell(ce.getOrgAfflName()));
                tableD.addCell(createDataCell(ce.getJournalCollection()));
                if (ce.getLastActvDate() != null) {
                    tableD.addCell(createDataCell(formatActiveDate(ce.getLastActvDate())));
                } else {
                    tableD.addCell(createEmptyDataCell());
                }
            }
        } else {
            generateEmptyTable(tableD, 3, 5);
        }
        // tableD.setKeepTogether(true);
        return tableD;
    }

    public static Paragraph tableEDescription() throws Exception {
        Paragraph tableEDesc = new Paragraph(
                "Table 5: List editorial board, editor-in chief and co-editors with whom the individual interacts.  "
                        + "An editor-in-chief must list the entire editorial board.\nB:  Editorial Board:  List name(s) of "
                        + "editor-in-chief and journal in the past 24 months; and\nE:  Other co-Editors of journal or collections "
                        + "with whom the individual has directly interacted in the last 24 months.")
                                .setFont(getCoaTextFont()).setFontSize(FONT_SIZE);

        tableEDesc.setFixedLeading(12);
        return tableEDesc;
    }

    public static Table createTable2(List<Relationship> Relationship) throws Exception {
        Table tableE = new Table(5);// new float[] { 0.5f, 2, 3, 3, 2 });
        tableE.setWidth(TABLE_WIDTH);
        // tableE.setSpacingBefore(5);
        // tableE.setWidths(new float[] { 0.5f, 2, 3, 3, 2 });
        // tableE.setWidthPercentage(100);
        tableE.addCell(createHederCell("2"));
        tableE.addCell(createHederCell("Name:"));
        tableE.addCell(createHederCell("Type of Relationship"));
        tableE.addCell(createHederCell("Optional  (email, Department)"));
        tableE.addCell(createHederCell("Last Active Date"));

        if (Relationship != null && !Relationship.isEmpty()) {
            for (Relationship re : Relationship) {
                tableE.addCell(createDataCell(re.getRelationshipTypeCode()));
                tableE.addCell(createDataCell(re.getRelationshipName()));
                tableE.addCell(createDataCell(re.getOrgAfflName()));
                tableE.addCell(createDataCell(re.getEmailDeptName()));
                if (re.getLastActvDate() != null) {
                    tableE.addCell(createDataCell(formatActiveDate(re.getLastActvDate())));
                } else {
                    tableE.addCell(createEmptyDataCell());
                }
            }
        } else {
            generateEmptyTable(tableE, 3, 5);
        }
        return tableE;
    }

    /**
     * This utility method format's the last active date.
     * 
     * @param date
     * @return
     */
    public static String formatActiveDate(Date date) {
        SimpleDateFormat activeDateFormat = new SimpleDateFormat("MM/dd/yy");
        return activeDateFormat.format(date);

    }

    /**
     * This method generates Empty table with 3 rows.
     * 
     * @param table
     * @param noOfRows
     * @param noOfColumns
     * @throws Exception
     */
    public static void generateEmptyTable(Table table, int noOfRows, int noOfColumns) throws Exception {
        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < noOfColumns; j++) {
                table.addCell(createEmptyDataCell());
            }
        }
    }

    private static Cell createEmptyDataCell() throws Exception {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        Paragraph text = new Paragraph(".").setFont(font).setFontSize(9).setFontColor(ColorConstants.WHITE);
        Cell cell = new Cell().add(text.setHorizontalAlignment(HorizontalAlignment.LEFT));
        return cell;
    }

    public static Document getDocument(ByteArrayOutputStream outputStream) throws Exception {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.LETTER);
        document.setMargins(65, 72, 65, 72);
        return document;
    }

    /**
     * This method will generate Data Not Avaialable Pdf.
     * 
     * @param title
     * @return
     * @throws DocumentException
     */
    public static ByteArrayOutputStream generateDataNotAvailablePdf(String title) throws CommonUtilException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Document document = new Document(PageSize.LETTER, 73, 72, 64, 67);

        try {
            Document document = getDocument(outputStream);
            Paragraph notAvail = null;
            Paragraph mainHeader = null;
            drawLine(document);
            mainHeader = getText(title);
            mainHeader.setTextAlignment(TextAlignment.CENTER);
            notAvail = getText(Constants.DATA_NOT_AVAIL);
            document.add(mainHeader);
            document.add(notAvail);
            document.close();
            return outputStream;
        } catch (Exception e) {
            throw new CommonUtilException("Got error while generating data not available Pdf : ", e);
        }
    }
}

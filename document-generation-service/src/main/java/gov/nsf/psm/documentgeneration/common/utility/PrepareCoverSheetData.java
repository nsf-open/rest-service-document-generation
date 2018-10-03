package gov.nsf.psm.documentgeneration.common.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nsf.psm.documentgeneration.common.constants.Constants;
import gov.nsf.psm.documentgeneration.common.constants.PDFDocConstants;
import gov.nsf.psm.foundation.model.Institution;
import gov.nsf.psm.foundation.model.UnitOfConsideration;
import gov.nsf.psm.foundation.model.budget.BudgetPdf;
import gov.nsf.psm.foundation.model.budget.BudgetRecord;
import gov.nsf.psm.foundation.model.budget.BudgetSeniorPersonnel;
import gov.nsf.psm.foundation.model.budget.EquipmentCost;
import gov.nsf.psm.foundation.model.budget.FringeBenefitCost;
import gov.nsf.psm.foundation.model.budget.IndirectCost;
import gov.nsf.psm.foundation.model.budget.InstitutionBudget;
import gov.nsf.psm.foundation.model.budget.OtherDirectCost;
import gov.nsf.psm.foundation.model.budget.OtherPersonnelCost;
import gov.nsf.psm.foundation.model.budget.ParticipantSupportCost;
import gov.nsf.psm.foundation.model.budget.SeniorPersonnelCost;
import gov.nsf.psm.foundation.model.budget.TravelCost;
import gov.nsf.psm.foundation.model.coversheet.CoverSheet;
import gov.nsf.psm.foundation.model.coversheet.CoverSheetPdfMapper;
import gov.nsf.psm.foundation.model.coversheet.FederalAgency;
import gov.nsf.psm.foundation.model.coversheet.InternationalActyCountry;
import gov.nsf.psm.foundation.model.coversheet.PiCoPiInformation;
import gov.nsf.psm.foundation.model.proposal.ProposalPackage;

public class PrepareCoverSheetData {

    private static final Logger logger = LoggerFactory.getLogger(PrepareCoverSheetData.class);

    List<BudgetRecord> budgRecList;
    BigDecimal amountOfThisRequest = new BigDecimal(0);

    /**
     * @param budgRecList
     */
    public PrepareCoverSheetData(List<BudgetRecord> budgRecList) {
        super();
        this.budgRecList = budgRecList;
    }

    public static CoverSheetPdfMapper setCoverSheetPdfMapperData(CoverSheet cv, ProposalPackage prop,
            String requestedAmount) throws Exception {
        CoverSheetPdfMapper cvMapper = new CoverSheetPdfMapper();
        cvMapper.setSolicitationId(prop.getFundingOp().getFundingOpportunityId());

        if (prop.getDeadline().getDeadlineTypeCode() != null) {
            if (!prop.getDeadline().getDeadlineTypeCode().equalsIgnoreCase(PDFDocConstants.ACCEPTED_ANYTIME)) {
                cvMapper.setDueDate(formatDate(prop.getDeadline().getDeadlineDate()));
            }
        }
        cvMapper.setDeadlineDate("");
        if (prop.getDebrFlag() != null) {
            cvMapper.setDebrFlag(prop.getDebrFlag());
        } else {
            cvMapper.setDebrFlag("X");
        }

        if (StringUtils.isNotBlank(prop.getDebrFlagExpLanation())) {
            cvMapper.setDebrFlagExpLanation(prop.getDebrFlagExpLanation().trim());
        }

        String propNum = "";

        if (prop.getNsfPropId() != null) {
            propNum = prop.getNsfPropId();
        }
        cvMapper.setNsfProposalNum(propNum);
        String dateReceived = "";
        if (prop.getSubmissionDate() != null) {
            dateReceived = formatDate(prop.getSubmissionDate());
        }
        cvMapper.setDateReceived(dateReceived);
        cvMapper.setNumberOfCopies(1);
        cvMapper.setUocs(prop.getUocs());

        if (prop.getUocs() != null && prop.getUocs().length > 0) {

            UnitOfConsideration uoc = PdfUtil.getSelectedUOC(prop.getUocs(), 0);
            String divAssigned = "";
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

            divAssigned = divCode + divAbbr;
            nsfOrganizationUnits = divAbbr + pgmEleName;
            if (prop.getUocs().length > 1) {
                nsfOrganizationUnits = nsfOrganizationUnits + " ,(continued)";
            }
            cvMapper.setDivisionAssigned(divAssigned);
            cvMapper.setNsfOrganizationUnits(nsfOrganizationUnits);
            cvMapper.setFundCode(pgmEleCode);
        }

        cvMapper.setFileLocation("");
        cvMapper.setPropSubmittedToOthFedAgency(false);
        Institution inst = prop.getInstitution();

        if (inst != null) {
            cvMapper.setAwardeeOrgName(inst.getOrganizationName());
            String add23 = inst.getAddress().getStreetAddress() + " " + inst.getAddress().getStreetAddress2();
            String cyzip = inst.getAddress().getCityName() + "," + inst.getAddress().getStateCode() + " "
                    + inst.getAddress().getPostalCode() + " " + inst.getAddress().getCountryCode();
            cvMapper.setAwardeeOrgAddressLine1(inst.getOrganizationName());
            cvMapper.setAwardeeOrgAddressLine2(add23);
            cvMapper.setAwardeeOrgAddressLine3(cyzip);
            cvMapper.setAwardeeOrgCode(inst.getId());
            cvMapper.setDunsId(inst.getDunsNumber());
            cvMapper.setEmployerIdNumber(inst.getTaxId());
        }
        if (cv.getPpop() != null) {
            cvMapper.setPrimaryPerfOrgName(cv.getPpop().getOrganizationName());
            cvMapper.setPrimaryPerfOrgAddressLine1(cv.getPpop().getOrganizationName());
            String add12 = cv.getPpop().getStreetAddress();
            if (!StringUtils.isEmpty(cv.getPpop().getStreetAddress2())) {
                add12 = add12 + ", " + cv.getPpop().getStreetAddress2();
            }
            cvMapper.setPrimaryPerfOrgAddressLine2(add12);
            cvMapper.setPrimaryPerfOrgAddressLine3(cv.getPpop().getCityName() + "," + cv.getPpop().getStateCode() + " "
                    + cv.getPpop().getPostalCode() + " " + cv.getPpop().getCountryCode());
        }

        cvMapper.setSmallBusiness(cv.isSmallbusiness());
        cvMapper.setMinorityBusiness(cv.isMinoritybusiness());
        cvMapper.setPreliminaryProp(false);
        cvMapper.setProfitOrg(cv.isForProfit());
        cvMapper.setWomanOwnedBusiness(cv.isWomenOwnedbusiness());
        cvMapper.setProposedProjectTitle(prop.getProposalTitle());
        cvMapper.setRequestedAmount(requestedAmount);
        cvMapper.setProposedDuration(String.valueOf(cv.getProposalDuration()));
        cvMapper.setRequestedStartingDate(formatDate(cv.getRequestedStartDate()));//
        cvMapper.setPrelimProposalNo("");
        cvMapper.setBeginningInvestigator(cv.isBeginningInvestigator());
        cvMapper.setDiscOfLobbyingActivities(cv.isDisclosureLobbyActy());
        cvMapper.setProprietaryPrivInformation(cv.isProprietaryPrivileged());
        cvMapper.setHistoricPlace(cv.isHistoricPlace());
        cvMapper.setVertebrateAnimals(cv.isVertebrateAnimal());//

        if (cv.getiAcucSAppDate() != null) {
            cvMapper.setIacucAppDate(formatDate(cv.getiAcucSAppDate()));//
        }

        String animalAssuNum = cv.getAnimalWelfareAssuNumber();
        if (!StringUtils.isNotBlank(animalAssuNum)) {
            animalAssuNum = cv.getVrtbAnimalAPType();
        }
        cvMapper.setPhsAnimalWelfareAssuNum(animalAssuNum);
        cvMapper.setTypeOfProposal(prop.getProposalType().trim());

        if (prop.getCollabType().equalsIgnoreCase("Non-Collaborative")) {
            cvMapper.setCollabStatus("No");
            cvMapper.setCollabStatusValue(prop.getCollabType());
        } else {
            cvMapper.setCollabStatus("Yes");
            cvMapper.setCollabStatusValue("A collaborative proposal from one organization (GPG II.D.4.a)");
        }
        cvMapper.setHumanSubjects(cv.isHumanSubject());
        cvMapper.setHumanSubjectsAssurNum(cv.getHumanSubjectAssuNumber());
        String exmptSubSec = cv.getExemptionSubsection();

        if (!StringUtils.isNotBlank(exmptSubSec)) {
            exmptSubSec = cv.getHumanSubjectsAPEType();
        }

        cvMapper.setExemptionSubsection(exmptSubSec);

        if (cv.getiRbAppDate() != null) {
            cvMapper.setIrbAppDate(formatDate(cv.getiRbAppDate()));
        }
        // List<InternationalActyCountry>
        String icName = "";
        if (cv.getInternationalActyCountries() != null && !cv.getInternationalActyCountries().isEmpty()) {
            int i = 1;
            for (InternationalActyCountry iac : cv.getInternationalActyCountries()) {
                if (i == 6) {
                    break;
                }
                icName = icName + "  " + iac.getIntlCountryCode();
                i++;
            }
        }
        cvMapper.setIntActivitiesCountries(icName);

        List<FederalAgency> fedList = cv.getFederalAgencies();
        if (fedList != null && !fedList.isEmpty()) {
            cvMapper.setPropSubmittedToOthFedAgency(true);
            List<String> faList = new ArrayList<String>();
            String otherFedAgencies = "";
            int i = 1;
            for (FederalAgency fa : fedList) {
                faList.add(fa.getFedAgencyNameAbbreviation());
                otherFedAgencies = otherFedAgencies + "  " + fa.getFedAgencyNameAbbreviation();
                i++;
                if (i == 6)
                    break;
            }
            cvMapper.setOtherFedAgencyList(faList);
            cvMapper.setOtherFedAgencies(otherFedAgencies);
        }

        cvMapper.setDebarmentAndSuspensioncert(false);
        String aorName = "";
        String aorEmail = "";
        String aorTel = "";
        String aorFax = "";
        String aorSignDate = "";
        String signText = "";

        if (prop.getAorName() != null) {
            aorName = prop.getAorName();
            signText = "Electronic Signature";
        }

        if (prop.getAorEmail() != null) {
            aorEmail = prop.getAorEmail();
        }
        if (prop.getAorPhoneNumber() != null) {
            aorTel = formatPhoneNumber(prop.getAorPhoneNumber());
        }

        if (prop.getAorFaxNumber() != null) {
            aorFax = formatPhoneNumber(prop.getAorFaxNumber());
        }

        if (prop.getAorElecSignDate() != null) {
            aorSignDate = new SimpleDateFormat("MMM dd yyyy  hh:mm aaa").format(prop.getAorElecSignDate());
        }

        cvMapper.setAorName(aorName);
        cvMapper.setAorTelephoneNum(aorTel);
        cvMapper.setAorEmailAddress(aorEmail);
        cvMapper.setAorSignature(signText);
        cvMapper.setAorSignDate(aorSignDate);
        cvMapper.setAorFaxNum(aorFax);
        cvMapper.setRenewal(false);
        cvMapper.setAccmpBasedRenewal(false);
        // adding persons info
        PiCoPiInformation piCopi;
        List<PiCoPiInformation> piCopiList = new ArrayList<PiCoPiInformation>();
        List<PiCoPiInformation> piCopiInfoList = cv.getPiCopiList();

        for (PiCoPiInformation piCopiInfo : piCopiInfoList) {
            piCopi = new PiCoPiInformation();
            if (piCopiInfo.getPersonRoleCode().equalsIgnoreCase("01")) {
                piCopi.setDepartmentName(piCopiInfo.getDepartmentName());
                piCopi.setFaxNumber(piCopiInfo.getFaxNumber());
                piCopi.setAddress(piCopiInfo.getAddress());
            }
            piCopi.setPersonType(piCopiInfo.getPersonRoleCode()); // PI/PD
            piCopi.setName(piCopiInfo.getName());// NAME --FN+LN
            piCopi.setDegree(piCopiInfo.getDegree());
            piCopi.setDegreeYear(piCopiInfo.getDegreeYear());
            piCopi.setEmailAddress(piCopiInfo.getEmailAddress());
            piCopi.setTelephoneNum(piCopiInfo.getTelephoneNum());
            piCopiList.add(piCopi);
        }
        cvMapper.setPiCopiList(piCopiList);
        return cvMapper;

    }

    /**
     * This method will data for each budget Page.
     * 
     * @param budgRec
     * @return
     */

    public static BudgetPdf setBudgetPageData(String proposalNumber, String aorName, String orgName,
            BudgetRecord budgRec) {
        BudgetPdf bp = new BudgetPdf();
        String totalSrPrCount = "";
        String totalOtherSrPerCount = "";
        double totalSrPrCalMonth = 0;
        BigDecimal totalSeniorPersonnelNsfReqFunds = new BigDecimal(0);
        double totalOtherSrPerCalMonths = 0;
        BigDecimal totalOtherPersonnelAmount = new BigDecimal(0);
        BigDecimal totalOtherSrPerNsfReqFunds = new BigDecimal(0);
        BigDecimal totalParticipantCosts = new BigDecimal(0);
        BigDecimal totalOtherDirectCosts = new BigDecimal(0);
        BigDecimal totalSalariesAndWages = new BigDecimal(0);
        BigDecimal totalSalariesWagesAndFingeBenefits = new BigDecimal(0);
        BigDecimal totalDirectAndIndirectCosts = new BigDecimal(0);
        BigDecimal smallBusinessFee = new BigDecimal(0);
        BigDecimal amountOfThisRequest = new BigDecimal(0);
        BigDecimal totalEquipmentCost = new BigDecimal(0);
        BigDecimal totalDomesticCost = new BigDecimal(0);
        BigDecimal totalInternationalCost = new BigDecimal(0);
        BigDecimal totaldirectCostsAtoG = new BigDecimal(0);
        BigDecimal totalIndirectCosts = new BigDecimal(0);

        String piName = "";
        bp.setAwardNum("");
        bp.setProposedMonths("");
        bp.setGrantedMonths("");
        List<BudgetSeniorPersonnel> budgetSrPersList = new ArrayList<BudgetSeniorPersonnel>();

        List<SeniorPersonnelCost> srPersonnelList = getActiveSrPrBudgetList(budgRec.getSrPersonnelList());

        BudgetSeniorPersonnel srPer;
        if (srPersonnelList != null && !srPersonnelList.isEmpty()) {

            bp.setSrPrCostlList(srPersonnelList);
            totalSrPrCount = String.valueOf(srPersonnelList.size());
            if (srPersonnelList.size() > 5) {
                totalOtherSrPerCount = String.valueOf(srPersonnelList.size() - 5);
            }
            int i = 1;
            for (SeniorPersonnelCost sc : srPersonnelList) {
                totalSrPrCalMonth = totalSrPrCalMonth + sc.getSeniorPersonMonthCount();
                totalSeniorPersonnelNsfReqFunds = totalSeniorPersonnelNsfReqFunds.add(sc.getSeniorPersonDollarAmount());

                if (i < 6) {

                    if (sc.getSeniorPersonRoleCode().equalsIgnoreCase(Constants.USER_PI)) {
                        piName = sc.getSeniorPersonFirstName() + " " + sc.getSeniorPersonLastName();
                    }
                    srPer = new BudgetSeniorPersonnel(sc.getSeniorPersonRoleCode(),
                            sc.getSeniorPersonFirstName() + " " + sc.getSeniorPersonLastName(),
                            String.valueOf(sc.getSeniorPersonMonthCount()), Constants.BLANK_SPACE,
                            Constants.BLANK_SPACE, formatDollorAmount(sc.getSeniorPersonDollarAmount()),
                            Constants.BLANK_SPACE);
                    budgetSrPersList.add(srPer);
                } else {
                    totalOtherSrPerCalMonths = totalOtherSrPerCalMonths + sc.getSeniorPersonMonthCount();
                    totalOtherSrPerNsfReqFunds = totalOtherSrPerNsfReqFunds.add(sc.getSeniorPersonDollarAmount());
                }
                i++;
            }
        }

        List<OtherPersonnelCost> otherPersonnelList = budgRec.getOtherPersonnelList();
        if (otherPersonnelList != null && !otherPersonnelList.isEmpty()) {
            for (OtherPersonnelCost oc : otherPersonnelList) {
                totalOtherPersonnelAmount = totalOtherPersonnelAmount.add(oc.getOtherPersonDollarAmount());
            }
        }

        // Equipment
        List<EquipmentCost> equipmentList = budgRec.getEquipmentList();
        if (equipmentList != null && !equipmentList.isEmpty()) {
            bp.setEquipmentList(equipmentList);
            for (EquipmentCost ec : equipmentList) {
                totalEquipmentCost = totalEquipmentCost.add(ec.getEquipmentDollarAmount());
            }
        }

        // TravelCost travelCost
        TravelCost tc = budgRec.getTravelCost();
        if (tc != null) {
            totalDomesticCost = totalDomesticCost.add(tc.getDomesticTravelDollarAmount());
            totalInternationalCost = totalInternationalCost.add(tc.getForeignTravelDollarAmount());
        }

        // Participants Cost
        ParticipantSupportCost pc = budgRec.getParticipantsSupportCost();
        if (pc != null) {
            bp.setStipends(formatDollorAmount(pc.getStipendDollarAmount()));
            bp.setTravel(formatDollorAmount(pc.getTravelDollarAmount()));
            bp.setSubsistence(formatDollorAmount(pc.getSubsistenceDollarAmount()));
            bp.setOtherParticipant(formatDollorAmount(pc.getOtherDollarAmount()));
            bp.setTotalNumberOfParticipants(String.valueOf(pc.getPartNumberCount()));
            totalParticipantCosts = pc.getStipendDollarAmount().add(pc.getTravelDollarAmount())
                    .add(pc.getSubsistenceDollarAmount()).add(pc.getOtherDollarAmount());
            bp.setTotalParticipantCosts(formatDollorAmount(totalParticipantCosts));
        }

        // totalOtherDirectCosts
        OtherDirectCost oc = budgRec.getOtherDirectCost();
        if (oc != null) {
            totalOtherDirectCosts = totalOtherDirectCosts.add(oc.getComputerServicesDollarAmount())
                    .add(oc.getConsultantServicesDollarAmount()).add(oc.getMaterialsDollarAmount())
                    .add(oc.getOtherDirectCostDollarAmount()).add(oc.getPublicationDollarAmount())
                    .add(oc.getSubContractDollarAmount());

            bp.setConsultantServices(formatDollorAmount(oc.getConsultantServicesDollarAmount()));
            bp.setComputerServices(formatDollorAmount(oc.getComputerServicesDollarAmount()));
            bp.setMaterialsAndSupplies(formatDollorAmount(oc.getMaterialsDollarAmount()));
            bp.setOtherDirectCost(formatDollorAmount(oc.getOtherDirectCostDollarAmount()));
            bp.setSubawards(formatDollorAmount(oc.getSubContractDollarAmount()));
            bp.setPublicationCosts(formatDollorAmount(oc.getPublicationDollarAmount()));

            bp.setTotalOtherDirectCosts(formatDollorAmount(totalOtherDirectCosts));
        }
        // List<IndirectCost> indirectCostsList;

        List<IndirectCost> icList = budgRec.getIndirectCostsList();
        if (icList != null && !icList.isEmpty()) {
            bp.setIndirectCosts(icList);
            for (IndirectCost ic : icList) {
                BigDecimal itemCost = new BigDecimal(ic.getIndirectCostRate()).divide(new BigDecimal(100));
                BigDecimal idrAmt = ic.getIndirectCostBaseDollarAmount().multiply(itemCost);
                idrAmt = idrAmt.setScale(0, RoundingMode.HALF_UP);
                totalIndirectCosts = totalIndirectCosts.add(idrAmt);
            }
        }

        totalSalariesAndWages = totalSalariesAndWages.add(totalSeniorPersonnelNsfReqFunds)
                .add(totalOtherPersonnelAmount);

        FringeBenefitCost fb = budgRec.getFringeBenefitCost();
        if (fb != null) {
            BigDecimal fbAmt = fb.getFringeBenefitDollarAmount();
            totalSalariesWagesAndFingeBenefits = totalSalariesWagesAndFingeBenefits.add(totalSalariesAndWages)
                    .add(fbAmt);
        } else {
            totalSalariesWagesAndFingeBenefits = totalSalariesWagesAndFingeBenefits.add(totalSalariesAndWages);
        }

        totaldirectCostsAtoG = totaldirectCostsAtoG.add(totalSalariesWagesAndFingeBenefits).add(totalEquipmentCost)
                .add(totalDomesticCost).add(totalInternationalCost).add(totalParticipantCosts)
                .add(totalOtherDirectCosts);
        totalDirectAndIndirectCosts = totalDirectAndIndirectCosts.add(totaldirectCostsAtoG).add(totalIndirectCosts);

        amountOfThisRequest = amountOfThisRequest.add(totalDirectAndIndirectCosts).subtract(smallBusinessFee);

        bp.setOtherPersonnelList(budgRec.getOtherPersonnelList());
        bp.setPrincipalInvestigator(piName);
        bp.setBudgetSrPersList(budgetSrPersList);
        if (fb != null) {
            BigDecimal fbAmt = fb.getFringeBenefitDollarAmount();
            bp.setFringeBenefits(formatDollorAmount(fbAmt));
        }
        bp.setTotalSrPrCount(totalSrPrCount);
        bp.setTotalSeniorPersonnelNsfReqFunds(formatDollorAmount(totalSeniorPersonnelNsfReqFunds));
        bp.setTotalSrPrCalMonth(String.valueOf(totalSrPrCalMonth));
        bp.setTotalSalariesWagesAndFingeBenefits(formatDollorAmount(totalSalariesWagesAndFingeBenefits));

        bp.setTotalOtherSrPerCount(totalOtherSrPerCount);
        bp.setTotalOtherSrPerNsfReqFunds(formatDollorAmount(totalOtherSrPerNsfReqFunds));
        bp.setTotalOtherSrPerCalMonths(String.valueOf(totalOtherSrPerCalMonths));
        bp.setTotalSalariesAndWages(formatDollorAmount(totalSalariesAndWages));
        bp.setTotalEquipment(formatDollorAmount(totalEquipmentCost));
        bp.setDomestic(formatDollorAmount(totalDomesticCost));
        bp.setInternational(formatDollorAmount(totalInternationalCost));
        bp.setTotaldirectCostsAtoG(formatDollorAmount(totaldirectCostsAtoG));
        bp.setTotalIndirectCosts(formatDollorAmount(totalIndirectCosts));
        bp.setTotalDirectAndIndirectCosts(formatDollorAmount(totalDirectAndIndirectCosts));
        bp.setSmallBusinessFee(formatDollorAmount(smallBusinessFee));
        bp.setAmountOfThisRequest(formatDollorAmount(amountOfThisRequest));

        bp.setCostSharingProposedLevel("");
        bp.setOrganization(orgName);
        bp.setProposalNum(proposalNumber);
        bp.setPiPdName(piName);
        bp.setOrgRepName(aorName);
        bp.setPageNum(String.valueOf(budgRec.getBudgetYear()));
        return bp;
    }

    /**
     * This method sets the Cumulative BudgePage data.
     * 
     * @param budgRecList
     * @return
     */
    public static BudgetPdf setBudgetPageData(String proposalNumber, String aorName, String orgName,
            List<BudgetRecord> budgRecList) {
        BudgetPdf bp = new BudgetPdf();
        String piName = "";
        bp.setOrganization(orgName);
        bp.setProposalNum(proposalNumber);
        bp.setAwardNum("");
        bp.setProposedMonths("");
        bp.setGrantedMonths("");

        List<OtherPersonnelCost> newOPList = new ArrayList<OtherPersonnelCost>();
        List<BudgetSeniorPersonnel> newSrPersList = new ArrayList<BudgetSeniorPersonnel>();

        // double totalOtherSrPerCalMonths = 0;
        // BigDecimal totalOtherPersonnelAmount = new BigDecimal(0);
        // BigDecimal totalOtherSrPerNsfReqFunds = new BigDecimal(0);

        BigDecimal pdocTotal = new BigDecimal(0);
        double pdocMonthSum = 0;
        int pdocPersCount = 0;

        String totalSrPrCount = "";
        String totalOtherSrPerCount = "";

        BigDecimal othProfTotal = new BigDecimal(0);
        double othProfMonthSum = 0;
        int othProfCount = 0;

        BigDecimal gradStudentTotal = new BigDecimal(0);
        int gradStudentCount = 0;

        BigDecimal undGradTotal = new BigDecimal(0);
        int undGradCount = 0;

        BigDecimal clerTotal = new BigDecimal(0);
        int clerCount = 0;

        BigDecimal otherTotal = new BigDecimal(0);
        int otherCount = 0;

        BigDecimal fringeBenfitTotal = new BigDecimal(0);

        BigDecimal totalEquipment = new BigDecimal(0);
        BigDecimal totalDomesticCost = new BigDecimal(0);
        BigDecimal totalInternationalCost = new BigDecimal(0);
        BigDecimal totalStipendsCost = new BigDecimal(0);
        BigDecimal totalTravelCost = new BigDecimal(0);
        BigDecimal totalSubsistenceCost = new BigDecimal(0);
        BigDecimal totalOtherParticipantCost = new BigDecimal(0);
        double totalNumberOfParticipants = 0;
        BigDecimal totalParticipantCosts = new BigDecimal(0);

        BigDecimal materialsAndSupplies = new BigDecimal(0);
        BigDecimal publicationCosts = new BigDecimal(0);
        BigDecimal consultantServices = new BigDecimal(0);
        BigDecimal computerServices = new BigDecimal(0);
        BigDecimal subawards = new BigDecimal(0);
        BigDecimal otherDirectCost = new BigDecimal(0);
        BigDecimal totalOtherDirectCosts = new BigDecimal(0);
        BigDecimal totalIndirectCosts = new BigDecimal(0);

        BigDecimal totalSeniorPersonnelNsfReqFunds = new BigDecimal(0);
        double totalSrPrCalMonth = 0;

        BigDecimal totalSalariesAndWages = new BigDecimal(0);
        BigDecimal totalSalariesWagesAndFingeBenefits = new BigDecimal(0);
        BigDecimal totalDirectAndIndirectCosts = new BigDecimal(0);
        BigDecimal smallBusinessFee = new BigDecimal(0);
        BigDecimal amountOfThisRequest = new BigDecimal(0);

        BigDecimal totalSrPrNsfReq1 = new BigDecimal(0);
        BigDecimal totalSrPrNsfReq2 = new BigDecimal(0);
        BigDecimal totalSrPrNsfReq3 = new BigDecimal(0);
        BigDecimal totalSrPrNsfReq4 = new BigDecimal(0);
        BigDecimal totalSrPrNsfReq5 = new BigDecimal(0);
        BigDecimal totalSrPrNsfReq6 = new BigDecimal(0);

        double totalSrPrNsfCalMonth1 = 0;
        double totalSrPrNsfCalMonth2 = 0;
        double totalSrPrNsfCalMonth3 = 0;
        double totalSrPrNsfCalMonth4 = 0;
        double totalSrPrNsfCalMonth5 = 0;
        double totalSrPrNsfCalMonth6 = 0;

        String srPrType1 = "";
        String srPrType2 = "";
        String srPrType3 = "";
        String srPrType4 = "";
        String srPrType5 = "";
        String srPrType6 = "";

        String srPrFullName1 = "";
        String srPrFullName2 = "";
        String srPrFullName3 = "";
        String srPrFullName4 = "";
        String srPrFullName5 = "";
        String srPrFullName6 = "";

        List<SeniorPersonnelCost> srPrList1 = new ArrayList<SeniorPersonnelCost>();
        List<SeniorPersonnelCost> srPrList2 = new ArrayList<SeniorPersonnelCost>();
        List<SeniorPersonnelCost> srPrList3 = new ArrayList<SeniorPersonnelCost>();
        List<SeniorPersonnelCost> srPrList4 = new ArrayList<SeniorPersonnelCost>();
        List<SeniorPersonnelCost> srPrList5 = new ArrayList<SeniorPersonnelCost>();
        List<SeniorPersonnelCost> srPrList6 = new ArrayList<SeniorPersonnelCost>();

        BigDecimal totaldirectCostsAtoG = new BigDecimal(0);

        for (BudgetRecord br : budgRecList) {
            List<OtherPersonnelCost> list = br.getOtherPersonnelList();
            List<SeniorPersonnelCost> srPersonnelList = getActiveSrPrBudgetList(br.getSrPersonnelList());

            if (srPersonnelList != null && !srPersonnelList.isEmpty()) {
                totalSrPrCount = String.valueOf(srPersonnelList.size());
            }

            if (list != null && !list.isEmpty()) {
                for (OtherPersonnelCost oc : list) {
                    if (oc.getOtherPersonTypeCode().equalsIgnoreCase(Constants.CODE_STUDENTS_POST_DOCTORAL)) {
                        pdocTotal = pdocTotal.add(oc.getOtherPersonDollarAmount());
                        pdocMonthSum = pdocMonthSum + oc.getOtherPersonMonthCount();
                        pdocPersCount = pdocPersCount + oc.getOtherPersonCount();
                    } else if (oc.getOtherPersonTypeCode().equalsIgnoreCase(Constants.CODE_OTHER_PROFESSIONALS)) {
                        othProfTotal = othProfTotal.add(oc.getOtherPersonDollarAmount());
                        othProfMonthSum = othProfMonthSum + oc.getOtherPersonMonthCount();
                        othProfCount = othProfCount + oc.getOtherPersonCount();
                    } else if (oc.getOtherPersonTypeCode().equalsIgnoreCase(Constants.CODE_STUDENTS_GRADUATE)) {
                        gradStudentTotal = gradStudentTotal.add(oc.getOtherPersonDollarAmount());
                        gradStudentCount = gradStudentCount + oc.getOtherPersonCount();
                    } else if (oc.getOtherPersonTypeCode().equalsIgnoreCase(Constants.CODE_OTHER)) {
                        otherTotal = otherTotal.add(oc.getOtherPersonDollarAmount());
                        otherCount = otherCount + oc.getOtherPersonCount();
                    } else if (oc.getOtherPersonTypeCode().equalsIgnoreCase(Constants.CODE_STUDENTS_UNDERGRADUATE)) {
                        undGradTotal = undGradTotal.add(oc.getOtherPersonDollarAmount());
                        undGradCount = undGradCount + oc.getOtherPersonCount();
                    } else if (oc.getOtherPersonTypeCode().equalsIgnoreCase(Constants.CODE_CLERICAL)) {
                        clerTotal = clerTotal.add(oc.getOtherPersonDollarAmount());
                        clerCount = clerCount + oc.getOtherPersonCount();
                    }
                }
            }
            FringeBenefitCost fb = br.getFringeBenefitCost();
            if (fb != null) {
                fringeBenfitTotal = fringeBenfitTotal.add(fb.getFringeBenefitDollarAmount());
            }

            List<EquipmentCost> equipmentList = br.getEquipmentList();
            if (equipmentList != null && !equipmentList.isEmpty()) {
                for (EquipmentCost ec : equipmentList) {
                    totalEquipment = totalEquipment.add(ec.getEquipmentDollarAmount());
                }
            }

            TravelCost tc = br.getTravelCost();
            if (tc != null) {
                totalDomesticCost = totalDomesticCost.add(tc.getDomesticTravelDollarAmount());
                totalInternationalCost = totalInternationalCost.add(tc.getForeignTravelDollarAmount());
            }

            OtherDirectCost oc = br.getOtherDirectCost();
            if (oc != null) {
                materialsAndSupplies = materialsAndSupplies.add(oc.getMaterialsDollarAmount());
                publicationCosts = publicationCosts.add(oc.getPublicationDollarAmount());
                consultantServices = consultantServices.add(oc.getConsultantServicesDollarAmount());
                computerServices = computerServices.add(oc.getComputerServicesDollarAmount());
                subawards = subawards.add(oc.getSubContractDollarAmount());
                otherDirectCost = otherDirectCost.add(oc.getOtherDirectCostDollarAmount());
            }

            if (srPersonnelList != null && !srPersonnelList.isEmpty()) {

                if (srPersonnelList.size() > 5) {
                    totalOtherSrPerCount = String.valueOf(srPersonnelList.size() - 5);
                }

                for (SeniorPersonnelCost sc : srPersonnelList) {

                    String propPersId = sc.getPropPersId();
                    String rcode = sc.getSeniorPersonRoleCode();

                    if (rcode.equalsIgnoreCase(Constants.USER_PI)) {
                        srPrList1.add(sc);
                    } else if (rcode.equalsIgnoreCase(Constants.USER_CO_PI)) {
                        if (srPrList2.isEmpty()) {
                            srPrList2.add(sc);
                        } else {
                            SeniorPersonnelCost sc2 = srPrList2.get(0);
                            if (sc2.getPropPersId().equalsIgnoreCase(propPersId)) {
                                srPrList2.add(sc);
                            } else if (srPrList3.isEmpty()) {
                                srPrList3.add(sc);
                            } else if (!srPrList3.isEmpty()) {
                                SeniorPersonnelCost sc3 = srPrList3.get(0);
                                if (sc3.getPropPersId().equalsIgnoreCase(propPersId)) {
                                    srPrList3.add(sc);
                                } else if (srPrList4.isEmpty()) {
                                    srPrList4.add(sc);
                                } else if (!srPrList4.isEmpty()) {
                                    SeniorPersonnelCost sc4 = srPrList4.get(0);
                                    if (sc4.getPropPersId().equalsIgnoreCase(propPersId)) {
                                        srPrList4.add(sc);
                                    } else if (srPrList5.isEmpty()) {
                                        srPrList5.add(sc);
                                    } else if (!srPrList5.isEmpty()) {
                                        SeniorPersonnelCost sc5 = srPrList5.get(0);
                                        if (sc5.getPropPersId().equalsIgnoreCase(propPersId)) {
                                            srPrList5.add(sc);
                                        } else {
                                            srPrList6.add(sc);
                                        }
                                    }
                                }

                            }
                        }
                    } else if (rcode.equalsIgnoreCase(Constants.USER_OSP)) {
                        if (srPrList2.isEmpty()) {
                            srPrList2.add(sc);
                        } else {
                            SeniorPersonnelCost sc2 = srPrList2.get(0);
                            if (sc2.getPropPersId().equalsIgnoreCase(propPersId)) {
                                srPrList2.add(sc);
                            } else if (srPrList3.isEmpty()) {
                                srPrList3.add(sc);
                            } else if (!srPrList3.isEmpty()) {
                                SeniorPersonnelCost sc3 = srPrList3.get(0);
                                if (sc3.getPropPersId().equalsIgnoreCase(propPersId)) {
                                    srPrList3.add(sc);
                                } else if (srPrList4.isEmpty()) {
                                    srPrList4.add(sc);
                                } else if (!srPrList4.isEmpty()) {
                                    SeniorPersonnelCost sc4 = srPrList4.get(0);
                                    if (sc4.getPropPersId().equalsIgnoreCase(propPersId)) {
                                        srPrList4.add(sc);
                                    } else if (srPrList5.isEmpty()) {
                                        srPrList5.add(sc);
                                    } else if (!srPrList5.isEmpty()) {
                                        SeniorPersonnelCost sc5 = srPrList5.get(0);
                                        if (sc5.getPropPersId().equalsIgnoreCase(propPersId)) {
                                            srPrList5.add(sc);
                                        } else {
                                            srPrList6.add(sc);
                                        }
                                    }
                                }

                            }
                        }
                    } else if (rcode.equalsIgnoreCase(Constants.USER_OAU)) {
                        if (srPrList2.isEmpty()) {
                            srPrList2.add(sc);
                        } else {
                            SeniorPersonnelCost sc2 = srPrList2.get(0);
                            sc2.getPropPersId();
                            if (sc2.getPropPersId().equalsIgnoreCase(propPersId)) {
                                srPrList2.add(sc);
                            } else if (srPrList3.isEmpty()) {
                                srPrList3.add(sc);
                            } else if (!srPrList3.isEmpty()) {
                                SeniorPersonnelCost sc3 = srPrList3.get(0);
                                if (sc3.getPropPersId().equalsIgnoreCase(propPersId)) {
                                    srPrList3.add(sc);
                                } else if (srPrList4.isEmpty()) {
                                    srPrList4.add(sc);
                                } else if (!srPrList4.isEmpty()) {
                                    SeniorPersonnelCost sc4 = srPrList4.get(0);
                                    if (sc4.getPropPersId().equalsIgnoreCase(propPersId)) {
                                        srPrList4.add(sc);
                                    } else if (srPrList5.isEmpty()) {
                                        srPrList5.add(sc);
                                    } else if (!srPrList5.isEmpty()) {
                                        SeniorPersonnelCost sc5 = srPrList5.get(0);
                                        if (sc5.getPropPersId().equalsIgnoreCase(propPersId)) {
                                            srPrList5.add(sc);
                                        } else {
                                            srPrList6.add(sc);
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }

            List<IndirectCost> idrList = br.getIndirectCostsList();
            if (idrList != null && !idrList.isEmpty()) {
                for (IndirectCost idr : idrList) {
                    BigDecimal itemCost = new BigDecimal(idr.getIndirectCostRate()).divide(new BigDecimal(100));
                    BigDecimal idrAmt = idr.getIndirectCostBaseDollarAmount().multiply(itemCost);
                    idrAmt = idrAmt.setScale(0, RoundingMode.HALF_UP);
                    totalIndirectCosts = totalIndirectCosts.add(idrAmt);
                }
            }

            ParticipantSupportCost pc = br.getParticipantsSupportCost();
            if (pc != null) {
                totalStipendsCost = totalStipendsCost.add(pc.getStipendDollarAmount());
                totalTravelCost = totalTravelCost.add(pc.getTravelDollarAmount());
                totalSubsistenceCost = totalSubsistenceCost.add(pc.getSubsistenceDollarAmount());
                totalOtherParticipantCost = totalOtherParticipantCost.add(pc.getOtherDollarAmount());
                totalNumberOfParticipants = totalNumberOfParticipants + pc.getPartNumberCount();

            }
        }

        if (!srPrList1.isEmpty()) {
            SeniorPersonnelCost sc1 = srPrList1.get(0);
            piName = sc1.getSeniorPersonFirstName() + " " + sc1.getSeniorPersonLastName();

            for (SeniorPersonnelCost sc : srPrList1) {
                srPrType1 = sc.getSeniorPersonRoleCode();
                srPrFullName1 = sc.getSeniorPersonFirstName() + " " + sc.getSeniorPersonLastName();
                totalSrPrNsfCalMonth1 = totalSrPrNsfCalMonth1 + sc.getSeniorPersonMonthCount();
                totalSrPrNsfReq1 = totalSrPrNsfReq1.add(sc.getSeniorPersonDollarAmount());
            }
        }

        if (!srPrList2.isEmpty()) {
            for (SeniorPersonnelCost sc : srPrList2) {
                srPrType2 = sc.getSeniorPersonRoleCode();
                srPrFullName2 = sc.getSeniorPersonFirstName() + " " + sc.getSeniorPersonLastName();
                totalSrPrNsfCalMonth2 = totalSrPrNsfCalMonth2 + sc.getSeniorPersonMonthCount();
                totalSrPrNsfReq2 = totalSrPrNsfReq2.add(sc.getSeniorPersonDollarAmount());
            }
        }

        if (!srPrList3.isEmpty()) {
            for (SeniorPersonnelCost sc : srPrList3) {
                srPrType3 = sc.getSeniorPersonRoleCode();
                srPrFullName3 = sc.getSeniorPersonFirstName() + " " + sc.getSeniorPersonLastName();
                totalSrPrNsfCalMonth3 = totalSrPrNsfCalMonth3 + sc.getSeniorPersonMonthCount();
                totalSrPrNsfReq3 = totalSrPrNsfReq3.add(sc.getSeniorPersonDollarAmount());
            }
        }

        if (!srPrList4.isEmpty()) {
            for (SeniorPersonnelCost sc : srPrList4) {
                srPrType4 = sc.getSeniorPersonRoleCode();
                srPrFullName4 = sc.getSeniorPersonFirstName() + " " + sc.getSeniorPersonLastName();
                totalSrPrNsfCalMonth4 = totalSrPrNsfCalMonth4 + sc.getSeniorPersonMonthCount();
                totalSrPrNsfReq4 = totalSrPrNsfReq4.add(sc.getSeniorPersonDollarAmount());
            }
        }
        if (!srPrList5.isEmpty()) {
            for (SeniorPersonnelCost sc : srPrList5) {
                srPrType5 = sc.getSeniorPersonRoleCode();
                srPrFullName5 = sc.getSeniorPersonFirstName() + " " + sc.getSeniorPersonLastName();
                totalSrPrNsfCalMonth5 = totalSrPrNsfCalMonth5 + sc.getSeniorPersonMonthCount();
                totalSrPrNsfReq5 = totalSrPrNsfReq5.add(sc.getSeniorPersonDollarAmount());
            }
        }
        if (!srPrList6.isEmpty()) {
            for (SeniorPersonnelCost sc : srPrList6) {
                srPrType6 = sc.getSeniorPersonRoleCode();
                srPrFullName6 = "";
                totalSrPrNsfCalMonth6 = totalSrPrNsfCalMonth6 + sc.getSeniorPersonMonthCount();
                totalSrPrNsfReq6 = totalSrPrNsfReq6.add(sc.getSeniorPersonDollarAmount());
            }
        }

        BudgetSeniorPersonnel srPer1 = new BudgetSeniorPersonnel(srPrType1, srPrFullName1,
                String.valueOf(totalSrPrNsfCalMonth1), Constants.BLANK_SPACE, Constants.BLANK_SPACE,
                formatDollorAmount(totalSrPrNsfReq1), Constants.BLANK_SPACE);

        BudgetSeniorPersonnel srPer2 = new BudgetSeniorPersonnel(srPrType2, srPrFullName2,
                String.valueOf(totalSrPrNsfCalMonth2), Constants.BLANK_SPACE, Constants.BLANK_SPACE,
                formatDollorAmount(totalSrPrNsfReq2), Constants.BLANK_SPACE);

        BudgetSeniorPersonnel srPer3 = new BudgetSeniorPersonnel(srPrType3, srPrFullName3,
                String.valueOf(totalSrPrNsfCalMonth3), Constants.BLANK_SPACE, Constants.BLANK_SPACE,
                formatDollorAmount(totalSrPrNsfReq3), Constants.BLANK_SPACE);

        BudgetSeniorPersonnel srPer4 = new BudgetSeniorPersonnel(srPrType4, srPrFullName4,
                String.valueOf(totalSrPrNsfCalMonth4), Constants.BLANK_SPACE, Constants.BLANK_SPACE,
                formatDollorAmount(totalSrPrNsfReq4), Constants.BLANK_SPACE);

        BudgetSeniorPersonnel srPer5 = new BudgetSeniorPersonnel(srPrType5, srPrFullName5,
                String.valueOf(totalSrPrNsfCalMonth5), Constants.BLANK_SPACE, Constants.BLANK_SPACE,
                formatDollorAmount(totalSrPrNsfReq5), Constants.BLANK_SPACE);

        BudgetSeniorPersonnel srPer6 = new BudgetSeniorPersonnel(srPrType6, srPrFullName6,
                String.valueOf(totalSrPrNsfCalMonth6), Constants.BLANK_SPACE, Constants.BLANK_SPACE,
                formatDollorAmount(totalSrPrNsfReq6), Constants.BLANK_SPACE);

        newSrPersList.add(srPer1);
        newSrPersList.add(srPer2);
        newSrPersList.add(srPer3);
        newSrPersList.add(srPer4);
        newSrPersList.add(srPer5);
        newSrPersList.add(srPer6);

        // totalStipendsCost
        bp.setTravel(formatDollorAmount(totalTravelCost));
        bp.setSubsistence(formatDollorAmount(totalSubsistenceCost));
        bp.setOtherParticipant(formatDollorAmount(totalOtherParticipantCost));
        bp.setStipends(formatDollorAmount(totalStipendsCost));
        bp.setTotalNumberOfParticipants(String.valueOf(totalNumberOfParticipants));

        totalParticipantCosts = totalParticipantCosts.add(totalStipendsCost).add(totalTravelCost)
                .add(totalOtherParticipantCost).add(totalSubsistenceCost);
        bp.setTotalParticipantCosts(formatDollorAmount(totalParticipantCosts));

        // totalSrPrCount;
        bp.setTotalSrPrCount(totalSrPrCount);
        // Total of All Senior Personals NSF requested funds.
        totalSeniorPersonnelNsfReqFunds = totalSeniorPersonnelNsfReqFunds.add(totalSrPrNsfReq1).add(totalSrPrNsfReq2)
                .add(totalSrPrNsfReq3).add(totalSrPrNsfReq4).add(totalSrPrNsfReq5).add(totalSrPrNsfReq6);

        bp.setTotalSeniorPersonnelNsfReqFunds(formatDollorAmount(totalSeniorPersonnelNsfReqFunds));

        // totalSeniorPersonnelCount
        totalSrPrCalMonth = totalSrPrCalMonth + totalSrPrNsfCalMonth1 + totalSrPrNsfCalMonth2 + totalSrPrNsfCalMonth3
                + totalSrPrNsfCalMonth4 + totalSrPrNsfCalMonth5 + totalSrPrNsfCalMonth6;
        bp.setTotalSrPrCalMonth(String.valueOf(totalSrPrCalMonth));

        // totalSalariesAndWages
        totalSalariesAndWages = totalSalariesAndWages.add(totalSeniorPersonnelNsfReqFunds).add(pdocTotal)
                .add(othProfTotal).add(gradStudentTotal).add(undGradTotal).add(clerTotal).add(otherTotal);

        bp.setTotalSalariesAndWages(formatDollorAmount(totalSalariesAndWages));

        // totalSalariesWagesAndFingeBenefits
        totalSalariesWagesAndFingeBenefits = totalSalariesWagesAndFingeBenefits.add(totalSalariesAndWages)
                .add(fringeBenfitTotal);

        bp.setTotalSalariesWagesAndFingeBenefits(formatDollorAmount(totalSalariesWagesAndFingeBenefits));

        // totalEquipment
        bp.setTotalEquipment(formatDollorAmount(totalEquipment));

        // totalDomesticCost && totalInternationalCost
        bp.setDomestic(formatDollorAmount(totalDomesticCost));
        bp.setInternational(formatDollorAmount(totalInternationalCost));

        // totalOtherDirectCosts
        bp.setOtherDirectCosts(formatDollorAmount(totalOtherDirectCosts));

        // materialsAndSupplies;
        bp.setMaterialsAndSupplies(formatDollorAmount(materialsAndSupplies));

        // publicationCosts;
        bp.setPublicationCosts(formatDollorAmount(publicationCosts));

        // consultantServices;
        bp.setConsultantServices(formatDollorAmount(consultantServices));

        // computerServices;
        bp.setComputerServices(formatDollorAmount(computerServices));

        // subawards;
        bp.setSubawards(formatDollorAmount(subawards));

        // otherDirectCost;
        bp.setOtherDirectCost(formatDollorAmount(otherDirectCost));

        // totalOtherDirectCosts;
        totalOtherDirectCosts = totalOtherDirectCosts.add(materialsAndSupplies).add(publicationCosts)
                .add(consultantServices).add(computerServices).add(subawards).add(otherDirectCost);

        bp.setTotalOtherDirectCosts(formatDollorAmount(totalOtherDirectCosts));

        // totalIndirectCosts
        bp.setTotalIndirectCosts(formatDollorAmount(totalIndirectCosts));

        OtherPersonnelCost pdocUser = new OtherPersonnelCost(Constants.CODE_STUDENTS_POST_DOCTORAL, pdocPersCount,
                pdocMonthSum, pdocTotal);

        OtherPersonnelCost othProfUser = new OtherPersonnelCost(Constants.CODE_OTHER_PROFESSIONALS, othProfCount,
                othProfMonthSum, othProfTotal);

        OtherPersonnelCost gradStudentUser = new OtherPersonnelCost(Constants.CODE_STUDENTS_GRADUATE, gradStudentCount,
                0, gradStudentTotal);

        OtherPersonnelCost undGradUser = new OtherPersonnelCost(Constants.CODE_STUDENTS_UNDERGRADUATE, undGradCount, 0,
                undGradTotal);

        OtherPersonnelCost clerUser = new OtherPersonnelCost(Constants.CODE_CLERICAL, clerCount, 0, clerTotal);

        OtherPersonnelCost othUser = new OtherPersonnelCost(Constants.CODE_OTHER, otherCount, 0, otherTotal);

        newOPList.add(pdocUser);
        newOPList.add(othProfUser);
        newOPList.add(gradStudentUser);
        newOPList.add(undGradUser);
        newOPList.add(clerUser);
        newOPList.add(othUser);

        bp.setOtherPersonnelList(newOPList);
        bp.setPrincipalInvestigator(piName);
        bp.setBudgetSrPersList(newSrPersList);

        // totaldirectCostsAtoG
        totaldirectCostsAtoG = totaldirectCostsAtoG.add(totalSalariesWagesAndFingeBenefits).add(totalEquipment)
                .add(totalDomesticCost).add(totalInternationalCost).add(totalOtherDirectCosts)
                .add(totalParticipantCosts);

        bp.setTotaldirectCostsAtoG(formatDollorAmount(totaldirectCostsAtoG));
        bp.setFringeBenefits(String.valueOf(formatDollorAmount(fringeBenfitTotal)));

        // totalDirectAndIndirectCosts
        totalDirectAndIndirectCosts = totalDirectAndIndirectCosts.add(totaldirectCostsAtoG).add(totalIndirectCosts);
        bp.setTotalDirectAndIndirectCosts(formatDollorAmount(totalDirectAndIndirectCosts));

        // amountOfThisRequest

        amountOfThisRequest = amountOfThisRequest.add(totalDirectAndIndirectCosts).subtract(smallBusinessFee);
        bp.setAmountOfThisRequest(formatDollorAmount(amountOfThisRequest));

        bp.setTotalOtherSrPerCount(totalOtherSrPerCount);
        bp.setPiPdName(piName);
        bp.setOrgRepName(aorName);
        bp.setPageNum("C");
        return bp;
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
     * @return the amountOfThisRequest
     */
    public static BigDecimal getTotalRequestedforThisProposal(List<InstitutionBudget> instBudget) {
        BigDecimal amountOfThisRequest = new BigDecimal(0);
        return amountOfThisRequest;
    }

    public void processBudgetTotals() {

    }

    /**
     * This method will return only Active Senior Personnel in budget.
     * 
     * @param srPersonnelList
     * @return
     */
    public static List<SeniorPersonnelCost> getActiveSrPrBudgetList(List<SeniorPersonnelCost> srPersonnelList) {
        List<SeniorPersonnelCost> activeList = new ArrayList<SeniorPersonnelCost>();
        for (SeniorPersonnelCost srPrCst : srPersonnelList) {
            logger.debug("PrepareCoverSheetData.getActiveSrPrBudgetList() : " + srPrCst);
            if (!srPrCst.isHidden()) {
                activeList.add(srPrCst);
            }
        }

        return activeList;
    }

    private static String formatPhoneNumber(String s) {
        // Discard all non-digits.
        s = s.replaceAll("\\D", "");
        if (s.length() == 10) {
            return String.format("%s-%s-%s", s.substring(0, 3), s.substring(3, 6), s.substring(6, 10));
        } else {
            return "";
        }
    }
}

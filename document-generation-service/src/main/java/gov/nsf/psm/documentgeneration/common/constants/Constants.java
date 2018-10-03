package gov.nsf.psm.documentgeneration.common.constants;

public final class Constants {

    // Project Summary PDF constants
    public static final String PROJ_SUMM_PDF = "projectSummaryPdf";
    public static final String PROJ_SUMM_PAGE_COUNT = "projectSummaryPageCount";
    public static final String OVERVIEW = "Overview";
    public static final String BRODER_IMPACT = "Broader Impacts";
    public static final String INTELLECTUAL_MERIT = "Intellectual Merit";
    public static final String PROJ_SUMM_TITLE = "PROJECT SUMMARY";
    public static final String COLON = ":";
    public static final String NEW_LINE = "\n\n";
    public static final String PROJECT_TITLE = "Project Title";

    // PDF templates
    public static final String COVERSHEET_SRC = "/opt/apps/psm/templates/coversheet_template_v1.pdf";
    public static final String COVERSHEET_UOC_SRC = "/opt/apps/psm/templates/coversheet_template_uocs_v1.pdf";
    public static final String COVERSHEET_PV_SRC = "/opt/apps/psm/templates/coversheet_template_pv_v1.pdf";
    public static final String COVERSHEET_UOC_PV_SRC = "/opt/apps/psm/templates/coversheet_template_pv_uocs_v1.pdf";
    public static final String TOC_SRC = "/opt/apps/psm/templates/toc_template_v1.pdf";
    public static final String ERROR_TEMPLATE_SRC = "/opt/apps/psm/templates/error_template_v0.pdf";
    public static final String BUDG_SOURCE = "/opt/apps/psm/templates/budget_template_v1.pdf";

    // Security constants
    public static final String HEADER_UNAME = "username";
    public static final String HEADER_PWORD = "password";
    private static final String[] SITE_EXCEPTIONS = new String[] { "^health", "^f5health", "^v2/api-docs",
            "^swagger-resources/?.*", "^swagger-ui.html", "^webjars/springfox-swagger-ui/.*" };

    // Error Messages
    public static final String GENERATE_PDF_DOCUMENT_ERROR = "Get Project Summary PDF has encountered error while generating PDF Document.";
    public static final String GENERATE_PDF_IO_ERROR = "Get Project Summary PDF has encountered error while reading PDF Page Count.";
    public static final String GENERATE_PDF_ERROR = "Unable to generate Project Summary PDF.";
    public static final String GENERATE_SECTION_PDF_ERROR_MSG_TEMPLATE = "%s unavailable";
    public static final String GENERATE_PROPOSAL_DOCUMENT_ERROR = "An error was encountered while attemtping to generate this proposal document. Please refer to the Table of Contents section for further details and contact the NSF Help Desk XXX-XXX-XXXX for assistance in resolving this issue.";
    public static final String GET_SECTION_DOCUMENT_ERROR_TEMPLATE = "Error while attempting to retrieve section document %s";
    public static final String RECEIVED_SECTION_LIST_ERROR = "The section list received was null.";

    public static final String GENERATE_PDF_COA_ERROR = "COA has encountered error while generating PDF Document.";

    public static final String GET_UPLOADED_PDF_ERROR = "Encountered error while getting Uploaded COA Document.";

    public static final String GENERATE_PDF_BUDJ_ERROR = "Budget has encountered error while generating PDF Document : ";
    public static final String GENERATE_PDF_CV_ERROR = "CoverSheet has encountered error while generating PDF Document : ";

    public static final String GENERATE_CONCATENATE_ERROR = "Senior Personal Pdfs Concatenation has encountered error : ";
    // PDF document messages
    public static final String HEADER_ERROR_TEXT_L1 = "The system has encountered an error and was unable to generate one or more sections in this proposal file. ";
    public static final String HEADER_ERROR_TEXT_L2 = "Please try again and if this issue persists, you may contact the Help Desk at 1-800-673-6188 or helpdesk@nsf.gov.";
    public static final String BLANK_SPACE = "";
    public static final String PI = "pi";

    // Other Personals in Budget
    public static final String CODE_STUDENTS_POST_DOCTORAL = "01";
    public static final String CODE_OTHER_PROFESSIONALS = "02";
    public static final String CODE_STUDENTS_GRADUATE = "03";
    public static final String CODE_STUDENTS_UNDERGRADUATE = "04";
    public static final String CODE_CLERICAL = "05";
    public static final String CODE_OTHER = "06";

    // Senior personal in Budget
    public static final String USER_PI = "01";
    public static final String USER_CO_PI = "02";
    public static final String USER_OSP = "03";
    public static final String USER_OAU = "04";
    public static final String YES = "yes";

    public static final String DATA_NOT_AVAIL = "Data Not Available";

    public static final String PDF_COMMENTS_TITLE = "SUMMARY PROPOSAL BUDGET COMMENTS";

    public static final String ORIGINAL = "ORIG";
    public static final String PFU = "PFUD,BUPD,PUPD";
    public static final String PREV_BUDG = "BREV";
    public static final String ORIG_STATUS = "05";
    public static final String REV_STATUS = "12";

    // Security constants
    public static final String SITE_EXCEPTION = "health";
    public static final String SERVER_500_ERROR = "Server Error";
    public static final String SERVER_401_ERROR = "Not Authorized";
    public static final String SERVER_UNEXPECTED_ERROR = "The system encountered an unexpected error, please contact ITHC.";
    public static final String SERVER_RESOURCE_NOT_FOUND = "Resource Not Found";
    public static final String ACCESSMANAGEMENT_ERROR_MESSAGE = "AccessManagement Error Message";
    public static final String UNAVAILABLE = "Unavailable";

    private Constants() {
        super();
    }

    public static final String[] getSiteExceptions() {
        return SITE_EXCEPTIONS.clone();
    }

}
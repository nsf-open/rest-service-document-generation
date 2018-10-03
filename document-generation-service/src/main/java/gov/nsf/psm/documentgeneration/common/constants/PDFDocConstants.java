package gov.nsf.psm.documentgeneration.common.constants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class PDFDocConstants {

    public static final String ACCEPTED_ANYTIME = "03";

    private PDFDocConstants() {
        super();
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
}

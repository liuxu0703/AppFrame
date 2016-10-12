package lx.af.utils.UnitFormatters;

import java.text.DecimalFormat;

/**
 * author: lx
 * date: 16-8-18
 */
public final class MoneyFormatter {

    private static DecimalFormat CENT_FORMATTER = new DecimalFormat("0.0#");

    private MoneyFormatter() {}

    public static String cent2yuan(int cent) {
        return CENT_FORMATTER.format(cent / 100f);
    }

}

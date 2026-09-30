package dev.delesk1jx.miningspeedinfo.text;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Formats mining speeds the same way in every language. {@link java.text.DecimalFormat} would follow
 * the system locale, which turns 6.5 into "6,5" on some machines and makes the value jump around
 * between clients.
 */
public final class SpeedFormatter {

    private SpeedFormatter() {
    }

    /**
     * @param value    the speed to print
     * @param decimals how many decimals to keep, or {@code -1} to keep whole numbers whole and trim
     *                 the trailing zeros of everything else
     */
    public static String format(float value, int decimals) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return "0";
        }
        if (decimals < 0) {
            decimals = 2;
        }
        BigDecimal number = BigDecimal.valueOf(value).setScale(decimals, RoundingMode.HALF_UP);
        return number.stripTrailingZeros().toPlainString();
    }
}

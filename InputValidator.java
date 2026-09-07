import java.util.regex.Pattern;

public final class InputValidator {

    // Exactly 3 letters, a dash, then exactly 4 digits — e.g. "CLT-1234", "elc-0001".
    private static final Pattern ID_FORMAT = Pattern.compile("^([A-Za-z]{3})-(\\d{4})$");

    // Keep MAX_NAME_LENGTH in sync with Item's "%-20s" name column so a long
    // name can never overflow the boxed table and break its alignment.
    public static final int MAX_NAME_LENGTH = 20;
    public static final int MAX_QUANTITY = 100_000;
    public static final double MAX_PRICE = 1_000_000;

    private InputValidator() {
        // Utility class — not meant to be instantiated.
    }

    /** Exactly 3 letters, a dash, then exactly 4 digits (e.g. "CLT-0001"). */
    public static boolean isValidIdFormat(String id) {
        return id != null && ID_FORMAT.matcher(id).matches();
    }

    /**
     * Non-blank, within the table's display width, and free of characters
     * that would corrupt the boxed table's borders (currently just "|").
     */
    public static boolean isValidName(String name) {
        if (name == null || name.isBlank()) return false;
        if (name.length() > MAX_NAME_LENGTH) return false;
        return !name.contains("|");
    }

    /** Non-negative and within a sane upper bound (catches accidental typos like an extra zero). */
    public static boolean isValidQuantity(int quantity) {
        return quantity >= 0 && quantity <= MAX_QUANTITY;
    }

    /** Non-negative, within a sane upper bound, and at most 2 decimal places. */
    public static boolean isValidPrice(double price) {
        if (price < 0 || price > MAX_PRICE) return false;
        double rounded = Math.round(price * 100) / 100.0;
        return Math.abs(rounded - price) < 1e-9;
    }
}
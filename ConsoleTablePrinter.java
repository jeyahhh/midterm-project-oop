import java.util.List;

public final class ConsoleTablePrinter {

    private ConsoleTablePrinter() {
    }

    
    public static void printBoxedTable(String title, boolean withCategory, List<Item> items, String emptyMessage) {
        String header = withCategory
                ? String.format("%-8s %-20s %-10s %-10s %-15s", "ID", "Name", "Quantity", "Price", "Category")
                : String.format("%-8s %-20s %-10s %-10s", "ID", "Name", "Quantity", "Price");

        int innerWidth = header.length() + 4; // 2-space margin on each side
        String border = repeat('_', innerWidth);

        System.out.println(" " + border + " ");
        System.out.println(boxRow(title, innerWidth, true));
        System.out.println(boxRow("", innerWidth, false));

        if (items.isEmpty()) {
            System.out.println(boxRow(emptyMessage, innerWidth, false));
        } else {
            System.out.println(boxRow(header, innerWidth, false));
            for (Item item : items) {
                String row = withCategory ? item.toTableRowWithCategory() : item.toTableRow();
                System.out.println(boxRow(row, innerWidth, false));
            }
        }

        System.out.println("|" + border + "|");
    }

    /** One "|  content...  |" row, either left-aligned or centered, clamped to innerWidth. */
    private static String boxRow(String content, int innerWidth, boolean centered) {
        String padded;
        if (centered) {
            int totalPad = innerWidth - content.length();
            if (totalPad < 0) {
                padded = content.substring(0, innerWidth);
            } else {
                int left = totalPad / 2;
                int right = totalPad - left;
                padded = repeat(' ', left) + content + repeat(' ', right);
            }
        } else {
            padded = "  " + content;
            if (padded.length() > innerWidth) {
                padded = padded.substring(0, innerWidth);
            } else {
                padded = padded + repeat(' ', innerWidth - padded.length());
            }
        }
        return "|" + padded + "|";
    }

    private static String repeat(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
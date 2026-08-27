import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Entry point. Drives the console menu and handles ALL user-input validation
 * so that Item / Inventory stay focused on data, not on parsing console text.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Inventory inventory = new Inventory();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readMenuChoice();
            switch (choice) {
                case 1: addItem(); break;
                case 2: updateItem(); break;
                case 3: removeItem(); break;
                case 4: displayItemsByCategory(); break;
                case 5: displayAllItems(); break;
                case 6: searchItem(); break;
                case 7: sortItems(); break;
                case 8: displayLowStockItems(); break;
                case 9:
                    System.out.println("Exiting program. Goodbye!");
                    running = false;
                    break;
                default:
                    // Unreachable because readMenuChoice() already validates range,
                    // kept here defensively.
                    System.out.println("Invalid choice!");
            }
        }
        scanner.close();
    }

    // ---------------------------------------------------------------------
    // Menu
    // ---------------------------------------------------------------------

    private static void printMenu() {
        System.out.println();
        System.out.println(" _______________________________________________________________________________ ");
        System.out.println("|                                     MENU                                      |");
        System.out.println("|                                                                               |");
        System.out.println("|   1 - Add Item                 2 - Update Item            3 - Remove Item     |");
        System.out.println("|   4 - Display Items Category   5 - Display All Items      6 - Search Item     |");
        System.out.println("|   7 - Sort Items               8 - Display Low Stock      9 - Exit            |");
        System.out.println("|_______________________________________________________________________________|");
        System.out.print("Enter choice: ");
    }

    private static int readMenuChoice() {
        boolean readingChoice = true;
        while (readingChoice) {
            String line = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice < 1 || choice > 9) {
                    System.out.print("Invalid choice! Please enter a number from 1-9: ");
                    continue;
                }
                return choice;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input! Please enter a number from 1-9: ");
            }
        }
        // Unreachable in practice — every valid path returns from inside the loop —
        // but required because the compiler can no longer prove the loop is infinite
        // now that it depends on a boolean variable instead of the literal `true`.
        throw new IllegalStateException("Unreachable");
    }

    // ---------------------------------------------------------------------
    // 1. Add Item
    // ---------------------------------------------------------------------

    private static void addItem() {
        System.out.println(" _______________________________________________________ ");
        System.out.println("|\t\t\tCATEGORIES \t\t\t|");
        System.out.println("|\t\t\t\t\t\t\t|");
        System.out.println("|   - CLOTHING\t\t\t\t\t\t|");
        System.out.println("|   - ELECTRONICS\t\t\t\t\t|");
        System.out.println("|   - ENTERTAINMENT\t\t\t\t\t|");
        System.out.println("|_______________________________________________________|");
        System.out.println("Enter Category : ");
        String categoryInput = scanner.nextLine().trim();

        if (!Inventory.isValidCategory(categoryInput)) {
            System.out.println("Category " + categoryInput + " does not exist!");
            return;
        }
        String category = Inventory.normalizeCategory(categoryInput);

        String id = readFormattedUniqueId(category);
        String name = readNonEmptyString("Enter Name: ");
        int quantity = readNonNegativeInt("Enter Quantity: ");
        double price = readNonNegativeDouble("Enter Price: ");

        Item item = ItemFactory.createItem(category, id, name, quantity, price);
        inventory.addItem(item);
        System.out.println("Item added successfully!");
    }

    // ---------------------------------------------------------------------
    // 2. Update Item
    // ---------------------------------------------------------------------

    private static void updateItem() {
        String id = readIdFormat("Enter ID: ");
        Item item = inventory.findById(id);

        if (item == null) {
            System.out.println("Item not found!");
            return;
        }

        String field = readFieldChoice();
        if (field.equals("quantity")) {
            int oldValue = item.getQuantity();
            int newValue = readNonNegativeInt("Enter new Quantity: ");
            item.setQuantity(newValue);
            System.out.println("Quantity of Item " + item.getName() + " is updated from "
                    + oldValue + " to " + newValue);
        } else {
            double oldValue = item.getPrice();
            double newValue = readNonNegativeDouble("Enter new Price: ");
            item.setPrice(newValue);
            System.out.println("Price of Item " + item.getName() + " is updated from "
                    + oldValue + " to " + newValue);
        }
    }

    private static String readFieldChoice() {
        boolean readingField = true;
        while (readingField) {
            System.out.print("Update Quantity or Price? (Q/P): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("q") || input.equals("quantity")) return "quantity";
            if (input.equals("p") || input.equals("price")) return "price";
            System.out.println("Invalid choice! Please enter Q for Quantity or P for Price.");
        }
        throw new IllegalStateException("Unreachable");
    }

    
    // 3. Remove Item
   

        private static void removeItem() {
        String id = readIdFormat("Enter ID: ");
        Item item = inventory.findById(id);

        if (item == null) {
            System.out.println("Item not found!");
            return;
        }

        boolean confirmed = readYesNo("Are you sure you want to remove Item" + item.getName() + "? (Y/N): ");
        if (!confirmed){
            System.out.println("Removal cancelled.");
            return;
        }

        String name = item.getName();
        inventory.removeItem(id);
        System.out.println("Item " + name + " has been removed from the inventory");
    }

        private static boolean readYesNo(String prompt) {
            boolean readConfirmation = true;
            while (readConfirmation) {
                System.out.print(prompt);
                String input = scanner.nextLine().trim().toLowerCase();
                if(input.equals("y") || input.equals("yes")) return true;
                if(input.equals("n") || input.equals("no")) return false;
                System.out.println("Invalid choice! Please enter Y or N.");
            }
            throw new IllegalStateException("Unreachable");
        }

    
    // 4. Display Items by Category
    

    private static void displayItemsByCategory() {
        System.out.print("Enter Category (Clothing/Electronics/Entertainment): ");
        String categoryInput = scanner.nextLine().trim();

        if (!Inventory.isValidCategory(categoryInput)) {
            System.out.println("Category " + categoryInput + " does not exist!");
            return;
        }
        String category = Inventory.normalizeCategory(categoryInput);
        List<Item> results = inventory.getByCategory(category);

        printBoxedTable(category.toUpperCase() + " ITEMS", false, results,
                "No items found in category " + category + ".");
    }

    
    // 5. Display All Items
   

    private static void displayAllItems() {
        printBoxedTable("ALL ITEMS", true, inventory.getAllItems(), "Inventory is empty.");
    }

    
    // 6. Search Item
    

    private static void searchItem() {
        String id = readIdFormat("Enter ID: ");
        Item item = inventory.findById(id);

        if (item == null) {
            System.out.println("Item not found!");
            return;
        }
        printBoxedTable("ITEM FOUND!", true, List.of(item), "");
    }

    
    // 7. Sort Items
    

    private static void sortItems() {
        String sortBy = readSortByChoice();
        boolean ascending = readSortOrderChoice();

        List<Item> sorted = inventory.sortItems(sortBy, ascending);
        String title = "ITEMS SORTED BY " + sortBy.toUpperCase() + " ("
                + (ascending ? "ASCENDING" : "DESCENDING") + ")";
        printBoxedTable(title, true, sorted, "Inventory is empty.");
    }

    private static String readSortByChoice() {
        boolean readingSortBy = true;
        while (readingSortBy) {
            System.out.print("Sort by Quantity or Price? (Q/P): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("q") || input.equals("quantity")) return "quantity";
            if (input.equals("p") || input.equals("price")) return "price";
            System.out.println("Invalid choice! Please enter Q for Quantity or P for Price.");
        }
        throw new IllegalStateException("Unreachable");
    }

    private static boolean readSortOrderChoice() {
        boolean readingOrder = true;
        while (readingOrder) {
            System.out.print("Ascending or Descending? (A/D): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("a") || input.equals("ascending")) return true;
            if (input.equals("d") || input.equals("descending")) return false;
            System.out.println("Invalid choice! Please enter A for Ascending or D for Descending.");
        }
        throw new IllegalStateException("Unreachable");
    }

    
    // 8. Display Low Stock Items
   

    private static void displayLowStockItems() {
        printBoxedTable("LOW STOCK ITEMS (QTY <= 5)", true, inventory.getLowStockItems(), "No low stock items.");
    }

    
    // Shared input-validation helpers
    

    private static String readNonEmptyString(String prompt) {
        boolean readingInput = true;
        while (readingInput) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Input cannot be empty!");
        }
        throw new IllegalStateException("Unreachable");
    }

    // Matches exactly 3 letters, a dash, then exactly 4 digits — e.g. "CLT-1234", "elc-0001".
    // The letters are captured so we can check they match the expected category prefix.
    private static final Pattern ID_FORMAT = Pattern.compile("^([A-Za-z]{3})-(\\d{4})$");

    /**
     * Reads any ID and validates it against the required format only:
     * exactly 3 letters, a dash, exactly 4 digits (e.g. "CLT-1234", "elc-0001").
     * Used for Update/Remove/Search, where the category isn't known up front,
     * so it can't check the prefix against a specific category — that check
     * only happens in readFormattedUniqueId() during Add Item.
     * Returns the ID normalized to uppercase.
     */
    private static String readIdFormat(String prompt) {
        boolean readingId = true;
        while (readingId) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!ID_FORMAT.matcher(input).matches()) {
                System.out.println("Invalid ID format! Must be 3 letters, a dash, then 4 digits (e.g. CLT-0001).");
                continue;
            }
            return input.toUpperCase();
        }
        throw new IllegalStateException("Unreachable");
    }

    /**
     * Reads a full item ID and validates it against the required format:
     * exactly 3 letters, a dash, exactly 4 digits — where the 3 letters must
     * be the prefix for the given category (CLT/Clothing, ELC/Electronics,
     * ENT/Entertainment), in any letter case. Stored/compared as uppercase.
     */
    private static String readFormattedUniqueId(String category) {
        String expectedPrefix = Inventory.getPrefixForCategory(category);
        boolean readingId = true;
        while (readingId) {
            String input = readIdFormat("Enter ID (format: " + expectedPrefix + "-####, e.g. " + expectedPrefix + "-0001): ");

            String enteredPrefix = input.substring(0, 3);
            if (!enteredPrefix.equals(expectedPrefix)) {
                System.out.println("Invalid prefix! IDs for " + category + " must start with \""
                        + expectedPrefix + "\" (got \"" + enteredPrefix + "\").");
                continue;
            }

            if (inventory.idExists(input)) {
                System.out.println("ID " + input + " already exists! Please enter a different ID.");
                continue;
            }
            return input;
        }
        throw new IllegalStateException("Unreachable");
    }

    private static int readNonNegativeInt(String prompt) {
        boolean readingNumber = true;
        while (readingNumber) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < 0) {
                    System.out.println("Value cannot be negative!");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a whole number.");
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    private static double readNonNegativeDouble(String prompt) {
        boolean readingNumber = true;
        while (readingNumber) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.println("Value cannot be negative!");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    // ---------------------------------------------------------------------
    // Boxed table rendering — same bordered look as the menu and Add Item's
    // category box, used by every display/search/sort screen.
    // ---------------------------------------------------------------------

    /**
     * Prints a titled, bordered table of items. If the list is empty,
     * prints emptyMessage inside the box instead of a header + rows.
     */
    private static void printBoxedTable(String title, boolean withCategory, List<Item> items, String emptyMessage) {
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
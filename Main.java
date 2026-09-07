import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Entry point. Drives the console menu and handles the read-prompt-retry
 * loops for user input.
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
    }

    private static int readMenuChoice() {
        boolean readingChoice = true;
        while (readingChoice) {
            String line = readLine("Enter choice: ");
            try {
                int choice = Integer.parseInt(line);
                if (choice < 1 || choice > 9) {
                    System.out.println("Invalid choice! Please enter a number from 1-9.");
                    continue;
                }
                return choice;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number from 1-9.");
            }
        }
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

        String category = readValidCategory("Enter Category: ");
        String id = readFormattedUniqueId(category);
        String name = readValidName("Enter Name: ");

        if (nameAlreadyUsedInCategory(name, category)) {
            boolean proceed = readYesNo("An item named \"" + name + "\" already exists in " + category
                    + ". Add it anyway? (Y/N): ");
            if (!proceed) {
                System.out.println("Add item cancelled.");
                return;
            }
        }

        int quantity = readValidQuantity("Enter Quantity: ");
        double price = readValidPrice("Enter Price: ");

        Item item = ItemFactory.createItem(category, id, name, quantity, price);
        inventory.addItem(item);
        System.out.println("Item added successfully!");
    }

    private static boolean nameAlreadyUsedInCategory(String name, String category) {
        for (Item item : inventory.getByCategory(category)) {
            if (item.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
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
            int newValue = readValidQuantity("Enter new Quantity: ");
            if (newValue == oldValue) {
                System.out.println("New value is the same as the current value — no change made.");
                return;
            }
            item.setQuantity(newValue);
            System.out.println("Quantity of Item " + item.getName() + " is updated from "
                    + oldValue + " to " + newValue);
        } else {
            double oldValue = item.getPrice();
            double newValue = readValidPrice("Enter new Price: ");
            if (newValue == oldValue) {
                System.out.println("New value is the same as the current value — no change made.");
                return;
            }
            item.setPrice(newValue);
            System.out.println("Price of Item " + item.getName() + " is updated from "
                    + oldValue + " to " + newValue);
        }
    }

    private static String readFieldChoice() {
        boolean readingField = true;
        while (readingField) {
            String input = readLine("Update Quantity or Price? (Q/P): ").toLowerCase();
            if (input.equals("q") || input.equals("quantity")) return "quantity";
            if (input.equals("p") || input.equals("price")) return "price";
            System.out.println("Invalid choice! Please enter Q for Quantity or P for Price.");
        }
        throw new IllegalStateException("Unreachable");
    }

    // ---------------------------------------------------------------------
    // 3. Remove Item
    // ---------------------------------------------------------------------

    private static void removeItem() {
        String id = readIdFormat("Enter ID: ");
        Item item = inventory.findById(id);

        if (item == null) {
            System.out.println("Item not found!");
            return;
        }

        boolean confirmed = readYesNo("Are you sure you want to remove Item " + item.getName() + "? (Y/N): ");
        if (!confirmed) {
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
            String input = readLine(prompt).toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("Invalid choice! Please enter Y or N.");
        }
        throw new IllegalStateException("Unreachable");
    }

    // ---------------------------------------------------------------------
    // 4. Display Items by Category
    // ---------------------------------------------------------------------

    private static void displayItemsByCategory() {
        String category = readValidCategory("Enter Category (Clothing/Electronics/Entertainment): ");
        List<Item> results = inventory.getByCategory(category);

        ConsoleTablePrinter.printBoxedTable(category.toUpperCase() + " ITEMS", false, results,
                "No items found in category " + category + ".");
    }

    // ---------------------------------------------------------------------
    // 5. Display All Items
    // ---------------------------------------------------------------------

    private static void displayAllItems() {
        ConsoleTablePrinter.printBoxedTable("ALL ITEMS", true, inventory.getAllItems(), "Inventory is empty.");
    }

    // ---------------------------------------------------------------------
    // 6. Search Item
    // ---------------------------------------------------------------------

    private static void searchItem() {
        String id = readIdFormat("Enter ID: ");
        Item item = inventory.findById(id);

        if (item == null) {
            System.out.println("Item not found!");
            return;
        }
        ConsoleTablePrinter.printBoxedTable("ITEM FOUND!", true, List.of(item), "");
    }

    // ---------------------------------------------------------------------
    // 7. Sort Items
    // ---------------------------------------------------------------------

    private static void sortItems() {
        String sortBy = readSortByChoice();
        boolean ascending = readSortOrderChoice();

        List<Item> sorted = inventory.sortItems(sortBy, ascending);
        String title = "ITEMS SORTED BY " + sortBy.toUpperCase() + " ("
                + (ascending ? "ASCENDING" : "DESCENDING") + ")";
        ConsoleTablePrinter.printBoxedTable(title, true, sorted, "Inventory is empty.");
    }

    private static String readSortByChoice() {
        boolean readingSortBy = true;
        while (readingSortBy) {
            String input = readLine("Sort by Quantity or Price? (Q/P): ").toLowerCase();
            if (input.equals("q") || input.equals("quantity")) return "quantity";
            if (input.equals("p") || input.equals("price")) return "price";
            System.out.println("Invalid choice! Please enter Q for Quantity or P for Price.");
        }
        throw new IllegalStateException("Unreachable");
    }

    private static boolean readSortOrderChoice() {
        boolean readingOrder = true;
        while (readingOrder) {
            String input = readLine("Ascending or Descending? (A/D): ").toLowerCase();
            if (input.equals("a") || input.equals("ascending")) return true;
            if (input.equals("d") || input.equals("descending")) return false;
            System.out.println("Invalid choice! Please enter A for Ascending or D for Descending.");
        }
        throw new IllegalStateException("Unreachable");
    }

    // ---------------------------------------------------------------------
    // 8. Display Low Stock Items
    // ---------------------------------------------------------------------

    private static void displayLowStockItems() {
        ConsoleTablePrinter.printBoxedTable("LOW STOCK ITEMS (QTY <= 5)", true, inventory.getLowStockItems(), "No low stock items.");
    }

    // ---------------------------------------------------------------------
    // Shared input-reading helpers.
    // These own the "keep asking until it's valid" loop; the actual
    // validity rules live in InputValidator so they can be reused/tested
    // without a Scanner attached.
    // ---------------------------------------------------------------------

    private static String readValidCategory(String prompt) {
        boolean readingCategory = true;
        while (readingCategory) {
            String input = readLine(prompt);
            if (!Inventory.isValidCategory(input)) {
                System.out.println("Category " + input + " does not exist! Choose Clothing, Electronics, or Entertainment.");
                continue;
            }
            return Inventory.normalizeCategory(input);
        }
        throw new IllegalStateException("Unreachable");
    }

    private static String readValidName(String prompt) {
        boolean readingName = true;
        while (readingName) {
            String input = readLine(prompt);
            if (input.isBlank()) {
                System.out.println("Name cannot be empty!");
                continue;
            }
            if (input.length() > InputValidator.MAX_NAME_LENGTH) {
                System.out.println("Name is too long! Maximum length is "
                        + InputValidator.MAX_NAME_LENGTH + " characters.");
                continue;
            }
            if (!InputValidator.isValidName(input)) {
                System.out.println("Name cannot contain the \"|\" character.");
                continue;
            }
            return input;
        }
        throw new IllegalStateException("Unreachable");
    }

    private static int readValidQuantity(String prompt) {
        boolean readingNumber = true;
        while (readingNumber) {
            String input = readLine(prompt);
            try {
                int value = Integer.parseInt(input);
                if (value < 0) {
                    System.out.println("Quantity cannot be negative!");
                    continue;
                }
                if (value > InputValidator.MAX_QUANTITY) {
                    System.out.println("Quantity is too large! Maximum allowed is "
                            + InputValidator.MAX_QUANTITY + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a whole number.");
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    private static double readValidPrice(String prompt) {
        boolean readingNumber = true;
        while (readingNumber) {
            String input = readLine(prompt);
            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.println("Price cannot be negative!");
                    continue;
                }
                if (value > InputValidator.MAX_PRICE) {
                    System.out.println("Price is too large! Maximum allowed is "
                            + InputValidator.MAX_PRICE + ".");
                    continue;
                }
                if (!InputValidator.isValidPrice(value)) {
                    System.out.println("Price can have at most 2 decimal places (e.g. 19.99).");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
        throw new IllegalStateException("Unreachable");
    }

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
            String input = readLine(prompt);
            if (!InputValidator.isValidIdFormat(input)) {
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

    /**
     * Prints prompt and reads one line, trimmed. Centralizes console input so
     * every read consistently guards against the input stream running out
     * (e.g. piped/redirected input during testing) instead of letting a raw
     * NoSuchElementException crash the program.
     */
    private static String readLine(String prompt) {
        System.out.print(prompt);
        try {
            return scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            System.out.println("\nNo more input available. Exiting program.");
            scanner.close();
            System.exit(0);
            throw new IllegalStateException("Unreachable"); // keeps compiler happy
        }
    }
}
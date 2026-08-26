import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Manages the collection of Items. Encapsulates the underlying list so
 * the rest of the program can only interact with it through these methods.
 */
public class Inventory {
    private final List<Item> items;
    private static final List<String> CATEGORIES = Arrays.asList("Clothing", "Electronics", "Entertainment");

    // Fixed prefix per category, used to auto-format IDs like "ELC-0001".
    private static final Map<String, String> CATEGORY_PREFIXES = Map.of(
            "Clothing", "CLT",
            "Electronics", "ELC",
            "Entertainment", "ENT"
    );

    public Inventory() {
        items = new ArrayList<>();
    }

    public static List<String> getCategories() {
        return CATEGORIES;
    }

    /** Returns the fixed ID prefix for a (properly-cased) category, e.g. "Electronics" -> "ELC". */
    public static String getPrefixForCategory(String category) {
        return CATEGORY_PREFIXES.get(category);
    }

    public static boolean isValidCategory(String category) {
        for (String c : CATEGORIES) {
            if (c.equalsIgnoreCase(category)) return true;
        }
        return false;
    }

    /** Returns the properly-cased category name (e.g. "clothing" -> "Clothing"). */
    public static String normalizeCategory(String category) {
        for (String c : CATEGORIES) {
            if (c.equalsIgnoreCase(category)) return c;
        }
        return category;
    }

    public boolean idExists(String id) {
        return findById(id) != null;
    }

    public boolean addItem(Item item) {
        if (idExists(item.getId())) {
            return false;
        }
        items.add(item);
        return true;
    }

    public Item findById(String id) {
        for (Item i : items) {
            if (i.getId().equalsIgnoreCase(id)) return i;
        }
        return null;
    }

    public boolean removeItem(String id) {
        Item item = findById(id);
        if (item == null) return false;
        items.remove(item);
        return true;
    }

    public List<Item> getByCategory(String category) {
        List<Item> result = new ArrayList<>();
        for (Item i : items) {
            if (i.getCategory().equalsIgnoreCase(category)) result.add(i);
        }
        return result;
    }

    public List<Item> getAllItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<Item> getLowStockItems() {
        List<Item> result = new ArrayList<>();
        for (Item i : items) {
            if (i.getQuantity() <= 5) result.add(i);
        }
        return result;
    }

    /**
     * Returns a new sorted list (does not mutate the original order).
     * @param sortBy    "quantity" or "price"
     * @param ascending true for ascending, false for descending
     */
    public List<Item> sortItems(String sortBy, boolean ascending) {
        List<Item> sorted = new ArrayList<>(items);
        Comparator<Item> comparator;
        if (sortBy.equalsIgnoreCase("quantity")) {
            comparator = Comparator.comparingInt(Item::getQuantity);
        } else {
            comparator = Comparator.comparingDouble(Item::getPrice);
        }
        if (!ascending) {
            comparator = comparator.reversed();
        }
        sorted.sort(comparator);
        return sorted;
    }
}
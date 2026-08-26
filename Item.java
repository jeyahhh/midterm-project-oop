/**
 * Abstract base class representing an inventory item.
 * Demonstrates ABSTRACTION: getCategory() is left abstract and implemented
 * differently by each concrete subclass (ClothingItem, ElectronicsItem, EntertainmentItem).
 * Demonstrates ENCAPSULATION: all fields are private and only reachable through
 * public getters/setters, which also enforce basic invariants (no negative values).
 */
public abstract class Item {
    private final String id;
    private String name;
    private int quantity;
    private double price;

    public Item(String id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }

    /**
     * Abstract method — every concrete item type must define its own category.
     * This is the core abstraction point of the hierarchy.
     */
    public abstract String getCategory();

    /**
     * Formats this item as one row of the output table.
     * Columns: ID, Name, Quantity, Price
     */
    public String toTableRow() {
        return String.format("%-8s %-20s %-10d %-10.2f", id, name, quantity, price);
    }

    /**
     * Formats this item as one row including the Category column.
     * Columns: ID, Name, Quantity, Price, Category
     */
    public String toTableRowWithCategory() {
        return String.format("%-8s %-20s %-10d %-10.2f %-15s", id, name, quantity, price, getCategory());
    }
}
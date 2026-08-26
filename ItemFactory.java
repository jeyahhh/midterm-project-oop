/**
 * Simple factory that creates the correct Item subclass based on category name.
 * Keeps the "which subclass do I build" decision in one place.
 */
public class ItemFactory {

    public static Item createItem(String category, String id, String name, int quantity, double price) {
        String normalized = Inventory.normalizeCategory(category);
        switch (normalized) {
            case "Clothing" -> {
                return new ClothingItem(id, name, quantity, price);
            }
            case "Electronics" -> {
                return new ElectronicsItem(id, name, quantity, price);
            }
            case "Entertainment" -> {
                return new EntertainmentItem(id, name, quantity, price);
            }
            default -> // Should never happen if category was validated beforehand.
                throw new IllegalArgumentException("Unknown category: " + category);
        }
    }
}
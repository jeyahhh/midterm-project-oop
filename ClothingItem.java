/**
 * Concrete Item subclass for the "Clothing" category.
 */
public class ClothingItem extends Item {

    public ClothingItem(String id, String name, int quantity, double price) {
        super(id, name, quantity, price);
    }

    @Override
    public String getCategory() {
        return "Clothing";
    }
}
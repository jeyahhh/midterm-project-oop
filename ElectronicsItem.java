/**
 * Concrete Item subclass for the "Electronics" category.
 */
public class ElectronicsItem extends Item {

    public ElectronicsItem(String id, String name, int quantity, double price) {
        super(id, name, quantity, price);
    }

    @Override
    public String getCategory() {
        return "Electronics";
    }
}
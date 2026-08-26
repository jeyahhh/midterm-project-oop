/**
 * Concrete Item subclass for the "Entertainment" category.
 */
public class EntertainmentItem extends Item {

    public EntertainmentItem(String id, String name, int quantity, double price) {
        super(id, name, quantity, price);
    }

    @Override
    public String getCategory() {
        return "Entertainment";
    }
}
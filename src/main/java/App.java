import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class App {
    static public void main(String[] args)
    {
        Product door = new Product("Wooden Door", 35);
        Product floorPanel = new Product("Floor Panel", 25);
        Product window = new Product("Glass Window", 10);

        Collection<Product> products = new ArrayList<>();

        products.add(door);
        products.add(floorPanel);
        products.add(window);

        Iterator<Product> productIterator = products.iterator();
        while (productIterator.hasNext()) {
            System.out.println(productIterator.next());
        }
    }
}

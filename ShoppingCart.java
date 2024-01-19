import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;

class Product {
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price= price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

class Pair<S, D>{
    private S discount_name;
    private D discount;

    public Pair(S discount_name, D discount) {
        this.discount_name = discount_name;
        this.discount = discount;
    }

    public S getDiscountName() {
        return discount_name;
    }

    public D getDiscount() {
        return discount;
    }

}

class ShoppingCart{
    private Map<Product, Integer> products;
    private int giftWrapFee = 1;
    private int shippingFee = 5;
    private int unitsPerPackage = 10;
    private double giftWrapCharge = 0;
    public Map<Product, Integer> getProducts() {
        return products;
    }

    public ShoppingCart() {
        this.products = new HashMap<Product, Integer>();
    }

    public void add_product(Product product, int quantity, boolean isGiftWrap) {
        products.put(product, quantity);
        if(isGiftWrap) {
            giftWrapCharge += quantity*giftWrapFee;
        }
    }

    public double getGiftWrapFee() {
        return giftWrapCharge;
    }

    public double getShippingFee() {
        double totalshippingFee = shippingFee * Math.floor(total_units() / unitsPerPackage);
        return totalshippingFee;
    }
 
    public double subtotal() {
        double subtotal = 0;

        for(Map.Entry<Product, Integer> e : products.entrySet()) {
            Product product = e.getKey();
            int quantity = e.getValue();
            subtotal += product.getPrice() * quantity;
        }

        return subtotal;
    }

    public int total_units() {
        int totalUnits = 0;

        for(Map.Entry<Product, Integer> e : products.entrySet()) {
            int quantity = e.getValue();
            totalUnits += quantity;
        }

        return totalUnits;
    }

    public Pair<String, Double> apply_discount(Map<Product, Integer> products){
        double subtotal = subtotal();
        int totalUnits = total_units();
        int max_quantity = Integer.MIN_VALUE;
        double discount = 0;

        for(Map.Entry<Product, Integer> e : products.entrySet()) {
            int quantity = e.getValue();
            if(quantity > max_quantity){
                max_quantity = quantity;
            }
        }
        
        double flat_10 = flat_10(subtotal);
        double bulk_5 = bulk_5(products);
        double bulk_10 = bulk_10(subtotal, totalUnits);
        double tiered_50 = tiered_50(products, max_quantity, totalUnits);
        
        String discount_name = new String();

        if(flat_10 > bulk_5 && flat_10 > bulk_10 && flat_10 > tiered_50){
            discount_name = "flat_10";
            discount = flat_10;
        }else if(bulk_5 > flat_10 && bulk_5 > bulk_10 && bulk_5 > tiered_50){
            discount_name = "bulk_5";
            discount = bulk_5;
        }else if(bulk_10 > flat_10 && bulk_10 > bulk_5 && bulk_10 > tiered_50){
            discount_name = "bulk_10";
            discount = bulk_10;
        }else if(tiered_50 > flat_10 && tiered_50 > bulk_5 && tiered_50 > bulk_10){
            discount_name = "tiered_50";
            discount = tiered_50;
        }
        
    
        return new Pair<>(discount_name, discount);
    }

    public double total() {
        double total = subtotal();

        total -= apply_discount(products).getDiscount();
        total +=  getShippingFee() + getGiftWrapFee();
        return total;
    }



    public double flat_10(double subtotal){
        double discount = 0;
        if(subtotal > 200){
            discount = 10;
        }
        return discount;
    }

    public double bulk_5(Map<Product, Integer> products){
        double discount = 0;
        for(Map.Entry<Product, Integer> e : products.entrySet()) {
            Product product = e.getKey();
            int quantity = e.getValue();
            if(quantity > 10){
                discount += (product.getPrice() * quantity) * 0.05;
            }
        }
        return discount;
    }

    public double bulk_10(double subtotal, int totalUnits){
        double discount = 0;
        
        if(totalUnits > 20){
            discount = subtotal * 0.1;
        }
        return discount;
    }

    public double tiered_50(Map<Product, Integer> products, int max_quantity, int totalUnits){
        int discount = 0;

        if(totalUnits > 30 && max_quantity > 15){
            for(Map.Entry<Product, Integer> e : products.entrySet()) {
                Product product = e.getKey();
                int quantity = e.getValue();
                if(quantity > 15){
                    discount += (product.getPrice() * (quantity-15)) * 0.05;
                }
            }
        }

        return discount;
    }
}

class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Product productA = new Product("A", 20);
        Product productB = new Product("B", 40);
        Product productC = new Product("C", 50);

        ShoppingCart cart = new ShoppingCart();

        System.out.println("Enter the quantity of Product A: ");
        int quantityA = sc.nextInt();
        System.out.println("Is product A gift wrapped ? Enter true or false: ");
        boolean isGiftWrap = sc.nextBoolean();
        cart.add_product(productA, quantityA, isGiftWrap);
        
        System.out.println("Enter the quantity of Product B: ");
        int quantityB = sc.nextInt();
        System.out.println("Is product B gift wrapped ? Enter true or false: ");
        isGiftWrap = sc.nextBoolean();
        cart.add_product(productB, quantityB, isGiftWrap);
        
        System.out.println("Enter the quantity of Product C: ");
        int quantityC = sc.nextInt();
        System.out.println("Is product C gift wrapped ? Enter true or false: ");
        isGiftWrap = sc.nextBoolean();
        cart.add_product(productC, quantityC, isGiftWrap);
        
        System.out.println("Product: " + productA.getName() + "\t" + "quantity: " + quantityA + "\t" + "total amount: " + quantityA*productA.getPrice());
        System.out.println("Product: " + productB.getName() + "\t" + "quantity: " + quantityB + "\t" + "total amount: " + quantityB*productB.getPrice());
        System.out.println("Product: " + productC.getName() + "\t" + "quantity: " + quantityC + "\t" + "total amount: " + quantityC*productC.getPrice());
        System.out.println("Subtotal: " + cart.subtotal());
        System.out.println("Discount name: " + cart.apply_discount(cart.getProducts()).getDiscountName());
        System.out.println("Discount: " + cart.apply_discount(cart.getProducts()).getDiscount());
        System.out.println("Shipping Fee: " + cart.getShippingFee());
        System.out.println("Gift Wrap Fee: " + cart.getGiftWrapFee());
        System.out.println("Total: " + cart.total());


        sc.close();
    }
}
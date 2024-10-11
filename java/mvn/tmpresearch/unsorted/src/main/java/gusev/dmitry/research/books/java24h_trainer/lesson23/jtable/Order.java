package gusev.dmitry.research.books.java24h_trainer.lesson23.jtable;

/**
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 06.10.12)
 */
public class Order {

    private int    orderId;
    private String stockSymbol;
    private int    quantity;
    private float  price;

    public Order(int orderId, String stockSymbol, int quantity, float price) {
        this.orderId = orderId;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }
}

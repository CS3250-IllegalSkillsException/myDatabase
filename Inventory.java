
public class Inventory {
	 //private instance variables 
    private String product_id;
    private int quantity;
    private int wholesale_cost;
    private int sale_price;
    private String supplier_id;
    
    //getters
    public String getProduct_id() {
    	return product_id;
    }
    
    public int getQuantity() {
    	return quantity;
    }
    
    public int getWholesale_cost() {
    	return wholesale_cost;
    }
    
    public int getSale_price() {
    	return sale_price;
    }
    
    public String getSupplier_id() {
    	return supplier_id;
    }
    
    //setters
    public void setProduct_id(String newProduct_id) {
    	this.product_id = newProduct_id;
    }
    
    public void setQuantity(int newQuantity) {
    	this.quantity = newQuantity;
    }
    
    public void setWholesale_cost(int newWholesale_cost) {
    	this.wholesale_cost = newWholesale_cost;
    }
    
    public void setSale_price(int newSale_price) {
    	this.sale_price = newSale_price;
    }
    
    public void setSupplier_id(String newSupplier) {
    	this.supplier_id = newSupplier;
    }
    
    
    public void preparedStatement() {
    	return;
    }
}

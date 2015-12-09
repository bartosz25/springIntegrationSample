package com.waitingforcode.model;

import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Sample entity representing store's order.
 *
 * @author Bartosz Konieczny
 */
public class Order {

    private int id;
    private List<Product> products;
    private Double finalPrice;

    public Order() {}

    public Order(Collection<?> c) {
        System.out.println("c is "+c+ " of class "+c.getClass());
        System.out.println("Class is "+c.getClass().getName());
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Product> getProducts() {
        if (products == null) {
            setProducts(new ArrayList<Product>());
        }
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        getProducts().add(product);
    }

    public void calculateFinalPrice() {
        double finalPrice = 0d;
        for (Product product : getProducts()) {
            finalPrice += product.getPrice();
        }
        setFinalPrice(finalPrice);
    }

    private void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Double getFinalPrice() {
        if (this.finalPrice == null) {
            calculateFinalPrice();
        }
        return this.finalPrice;
    }

    @Override
    public String toString() {
        return "Order {products: "+this.products+", final price: "+this.finalPrice+"}";
    }

}

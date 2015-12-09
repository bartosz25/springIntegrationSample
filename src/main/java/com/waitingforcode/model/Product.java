package com.waitingforcode.model;

/**
 * Product instance used to compose, among others, orders.
 *
 * @author Bartosz Konieczny
 */
public class Product {

    private String name;
    private int priorityLevel;
    private double price;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public int getPriorityLevel() {
        return this.priorityLevel;
    }

    public boolean canApplyPriorityLevel() {
        return this.priorityLevel == 0;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return this.price;
    }

    @Override
    public String toString() {
        return "Product {name: "+this.name+", priority level: "+this.priorityLevel+", price: "+this.price+"}";
    }
}

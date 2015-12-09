package com.waitingforcode.model;

import java.util.Date;

/**
 * TODO : comment this !
 *
 * @author Bartosz Konieczny
 */
public class ShoppingCart {

    private int id;
    private Date creationDate;
    private Order order;

    public void setId(int id) {
        this.id = id;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public void setOrder(Order order) {
        this.order = order;
    }

    public int getId() {
        return this.id;
    }
    public Date getCreationDate() {
        return this.creationDate;
    }
    public Order getOrder() {
        return this.order;
    }

    @Override
    public String toString() {
        return "ShoppingCart {order: "+order+"}";
    }

}

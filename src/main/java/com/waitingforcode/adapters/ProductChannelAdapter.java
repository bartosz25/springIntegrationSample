package com.waitingforcode.adapters;

import com.waitingforcode.model.Product;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Sample channel adapter receiving messages in handleProduct and preparing them into further sending in prepareToFurtherSend.
 *
 * @author Bartosz Konieczny
 */
@Component
public class ProductChannelAdapter {

    private Queue<Product> received = new LinkedList<Product>();

    public static final double PRICE = 399.99d;

    public void handleProduct(Product product) {
        // even if we change the price here, it won't be sent to receiver's message channel
        product.setPrice(43d);
        received.add(product);
    }

    public Product prepareToFurtherSend() {
        Product product = getLastProduct();
        product.setPrice(PRICE);
        return product;
    }

    public Product getLastProduct() {
        return received.poll();
    }

}

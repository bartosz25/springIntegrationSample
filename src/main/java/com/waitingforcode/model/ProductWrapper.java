package com.waitingforcode.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO : comment this !
 *
 * @author Bartosz Konieczny
 */
public class ProductWrapper {

    private List<Product> products;

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


    @Override
    public String toString() {
        return "ProductsWrapper {products: "+this.products+"}";
    }
}

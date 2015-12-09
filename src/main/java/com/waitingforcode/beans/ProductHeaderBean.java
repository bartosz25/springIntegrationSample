package com.waitingforcode.beans;

import com.waitingforcode.model.Product;
import org.springframework.stereotype.Component;

/**
 * TODO : comment this !
 *
 * @author Bartosz Konieczny
 */
@Component
public class ProductHeaderBean {

    public String getReversedName(Product product) {
        return new StringBuilder(product.getName()).reverse().toString();
    }
}

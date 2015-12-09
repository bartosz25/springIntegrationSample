package com.waitingforcode.services;

import com.waitingforcode.model.Product;
import com.waitingforcode.model.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * This service activator lives only in the goal to serve message to output channels. It's why
 * all of its methods return an instance of {@link Message}.
 *
 * @author Bartosz Konieczny
 */
@Component
public class ProductActivator {

    @Autowired
    private PriceService priceService;

    public Message<Product> handleBookingProduct(Message<Product> msg) {
        return msg;
    }

    public Message<Product> handleSellingProduct(Message<Product> msg) {
        return msg;
    }

    public Message<Product> handleBuyingProduct(Message<Product> msg) {
        return msg;
    }

    public Message<Product> quoteForTheBestPrice(Message<Product> msg) {
        Product product = msg.getPayload();
        product.setPrice(priceService.priceFromProduct(product));
        return msg;
    }

    public String handleJson(String json) {
        System.out.println("Handling JSON "+json);
        return json;
    }

    public Message<Product> handleFromJson(Product product) {
        System.out.println("Handling product "+product);
        return MessageBuilder.<Product>withPayload(product).build();
    }
}

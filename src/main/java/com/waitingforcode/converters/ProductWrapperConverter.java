package com.waitingforcode.converters;

import com.waitingforcode.model.Order;
import com.waitingforcode.model.Product;
import com.waitingforcode.model.ProductWrapper;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.support.MutableMessageBuilderFacfory;
import org.springframework.integration.transformer.AbstractPayloadTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

/**
 * TODO : comment this !
 *
 * @author Bartosz Konieczny
 */
@Component
public class ProductWrapperConverter implements MessageConverter {

    @Override
    public Object fromMessage(Message<?> message, Class<?> aClass) {
        if (message.getPayload() instanceof Product) {
            Product product = (Product)message.getPayload();
            ProductWrapper wrapper = new ProductWrapper();
            wrapper.addProduct(product);
            return wrapper;
        } else if (message.getPayload() instanceof Order) {
            Order order = (Order)message.getPayload();
            ProductWrapper wrapper = new ProductWrapper();
            wrapper.getProducts().addAll(order.getProducts());
            return wrapper;
        }
        return null;
    }

    @Override
    public Message<?> toMessage(Object o, MessageHeaders messageHeaders) {
        if (o instanceof Product) {
            System.out.println("Transforming");
            Product product = (Product)o;
            ProductWrapper wrapper = new ProductWrapper();
            wrapper.addProduct(product);
            return MessageBuilder.withPayload(wrapper).build();
        } else if (o instanceof Order) {
            Order order = (Order)o;
            ProductWrapper wrapper = new ProductWrapper();
            wrapper.getProducts().addAll(order.getProducts());
            return MessageBuilder.withPayload(wrapper).build();
        }
        return null;
    }

}

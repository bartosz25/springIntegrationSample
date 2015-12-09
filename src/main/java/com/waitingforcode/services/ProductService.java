package com.waitingforcode.services;

import com.waitingforcode.model.Product;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.GatewayHeader;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Payload;

/**
 * Messaging gateway example used to messaging-gateways.xml context file. As you can see, we use here the annotations to
 * understand better the idea of gateways.
 *
 * @author Bartosz Konieczny
 */
public interface ProductService {

    /**
     * This method is the simplest form of gateway's method declaration. By default, the first parameter is used as message's
     * payload. requestChannel and replyChannel represent input and output channels.
     *
     * @param product Product used as message payload.
     */
    @Gateway(requestChannel = "inputBookingChannel", replyChannel = "outputBookingChannel")
    void bookProduct(Product product);

    /**
     * This form of method is more sophisticated. It contains a supplementary attribute called "headers" which defines
     * headers appended to message. The first supplementary header "fixedHeader", will always contain the same value
     * ("hardCodedValue"). The second one, "dynamicHeader", will contain the value returned by
     * {@link com.waitingforcode.beans.ProductHeaderBean#getReversedName(com.waitingforcode.model.Product)} method, represented
     * in annotation thanks to Spring Expression Language.
     *
     * @param product Product used as message payload.
     */
    @Gateway(requestChannel = "inputSellingChannel", replyChannel = "outputSellingChannel", headers =
            {
                    @GatewayHeader(name = "fixedHeader", value = "hardCodedValue"),
                    @GatewayHeader(name = "dynamicHeader", expression = "@productHeaderBean.getReversedName(#args[0])")
            }
    )
    void sellProduct(Product product);

    /**
     * This form of method also defines supplementary headers. It makes that with @Header annotation. Both, "companyName" and
     * "price" headers, will be appended to message headers sent with the message. Note also the presence of @Payload annotation
     * representing message's payload.
     *
     * You can make a try and remove all annotations from this signature. Normally it should produce following exception:
     * <pre>
     * org.springframework.messaging.converter.MessageConversionException: failed to convert object to Message
     * // ...
     * Caused by: org.springframework.messaging.MessagingException: At most one parameter (or expression via
     * method-level @Payload) may be mapped to the payload or Message. Found more than one on method
     * [public abstract void com.waitingforcode.services.ProductService.buyProduct
     * (java.lang.String,double,com.waitingforcode.model.Product)]
     * </pre>
     *
     * @param companyName Company name passed in message header.
     * @param price Product price passed in message header
     * @param product Product instance used as payload.
     */
    @Gateway(requestChannel = "inputBuyingChannel", replyChannel = "outputBuyingChannel")
    void buyProduct(@Header("companyName") String companyName, @Header("price") double price, @Payload Product product);
}

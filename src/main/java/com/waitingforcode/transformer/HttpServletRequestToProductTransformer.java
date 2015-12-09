package com.waitingforcode.transformer;

import com.waitingforcode.model.Product;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * TODO : comment this !
 *
 * @author Bartosz Konieczny
 */
@Component
public class HttpServletRequestToProductTransformer {

    public Product transformRequest(LinkedMultiValueMap<String, String> params) {
System.out.println("Converting from "+params.getClass());
System.out.println("Request is "+params.toSingleValueMap());
        Map<String, String> requestParams = params.toSingleValueMap();
        System.out.println("name is "+requestParams.get("name"));
        Product product = new Product();
        product.setName(requestParams.get("name"));
        if (requestParams.containsKey("price")) {
            product.setPrice(Double.valueOf(requestParams.get("price")));
        }
        return product;
    }

}

package com.waitingforcode.services;

import com.waitingforcode.model.Product;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Sample service providing prices for products.
 *
 * @author Bartosz Konieczny
 */
@Service
public class PriceService {

    private static final double defaultPrice = 5.0d;
    private static final Map<String, Double> pricesByLetters = new HashMap<String, Double>();
    static {
        pricesByLetters.put("a", 11d);
        pricesByLetters.put("b", 3.6d);
        pricesByLetters.put("c", 2d);
        pricesByLetters.put("l", 2d);
        pricesByLetters.put("p", 10d);
    };

    public double priceFromProduct(Product product) {
        String idLetter = (""+product.getName().charAt(0)).toLowerCase();
        if (pricesByLetters.containsKey(idLetter)) {
            return pricesByLetters.get(idLetter);
        }
        return defaultPrice;
    }

}

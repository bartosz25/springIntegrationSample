package com.waitingforcode.converters;

import com.waitingforcode.model.Product;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author Bartosz Konieczny
 */
@Component
public class ProductHttpMessageConverter implements HttpMessageConverter<Product> {
    @Override
    public boolean canRead(Class<?> aClass, MediaType mediaType) {
        System.out.println("Can read ?" +aClass);
        return false;
    }

    @Override
    public boolean canWrite(Class<?> aClass, MediaType mediaType) {
        System.out.println("Can canWrite ?" +aClass);
        return false;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        System.out.println("getSupportedMediaTypes");
        return null;
    }

    @Override
    public Product read(Class<? extends Product> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        System.out.println("httpInputMessage " + httpInputMessage);
        return null;
    }

    @Override
    public void write(Product product, MediaType mediaType, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        System.out.println("httpOutputMessage "+httpOutputMessage);
    }
}

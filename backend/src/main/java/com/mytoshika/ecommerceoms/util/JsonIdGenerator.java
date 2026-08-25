package com.mytoshika.ecommerceoms.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class JsonIdGenerator {

    private static final String CHARACTERS= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final Random random;

    public JsonIdGenerator(Random RANDOM){
        this.random = RANDOM;
    }

    public String requestId(){
        return "abc" + randomChars(3);
    }

    public String responseId(){
        return "xyz" + randomChars(3);
    }

    private String randomChars(int length){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i<length; i++){
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

}

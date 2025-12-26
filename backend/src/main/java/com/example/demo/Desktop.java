package com.example.demo;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary // for confusion between two same method
public class Desktop implements Computer {

    public void build(){
        System.out.println("Building in background of desktop");
    }
}

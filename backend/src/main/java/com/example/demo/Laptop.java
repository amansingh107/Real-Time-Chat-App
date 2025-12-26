package com.example.demo;

import org.springframework.stereotype.Component;

@Component
public class Laptop implements Computer {

    public void build(){
        System.out.println("Building in background of laptop");
    }
}

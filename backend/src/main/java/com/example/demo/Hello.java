package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class Hello {
    @Autowired
    @Qualifier("laptop")
    private Computer computer;
    // field injection

//    private Laptop laptop;

//    public Hello(Laptop laptop){
//        this.laptop=laptop;
//    }
//    @Autowired
//    public void setLaptop(Computer computer){
//        this.computer = computer;
//    }

    public void compile(){
        computer.build();
        System.out.println("WORKING ON SPRINGBOOT");
    }
}

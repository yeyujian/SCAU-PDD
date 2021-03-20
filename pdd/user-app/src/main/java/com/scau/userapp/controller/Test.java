package com.scau.userapp.controller;

import javax.validation.constraints.NotNull;

/**
 * @program: pdd
 * @description:
 * @create: 2020-12-16 00:27
 **/
public class Test {


    @NotNull
    private String hello;

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }
}

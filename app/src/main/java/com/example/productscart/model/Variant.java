package com.example.productscart.model;

public class Variant {

   public String name;
    public int price;

    public Variant(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public Variant() {
    }

    @Override
    public String toString() {
        return name + " - Rs. " + price;
    }
}


package com.example.productscart.model;

import java.util.ArrayList;
import java.util.List;

public class Product {

    public static  final  byte WEIGHT_BASED = 0, VARIANTS_BASED = 1;

    //common
    public String name;
    public int type;

    //WeightBased
    public int pricePerKg;
    public float minQty;

    //VariantBased
    public List<Variant> variants;

    public Product() {
    }

    //Weight Based constructor
    public Product(String name, int pricePerKg, float minQty) {
        this.name = name;
        this.pricePerKg = pricePerKg;
        this.minQty = minQty;
    }


   // variant based constructor
    public Product(String name) {
        type = VARIANTS_BASED;
        this.name = name;
    }




    public void fromVariantStrings(String[] vs){

        variants = new ArrayList<>();
        for (String s:vs) {
            String[] v = s.split(",");
            variants.add(new Variant(v[0], Integer.parseInt(v[1])));
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", pricePerKg=" + pricePerKg +
                ", minQty=" + minQty +
                ", variants=" + variants +
                '}';
    }

    public String variantsString(){
        String variantsString = variants.toString();
        return variantsString
                .replaceFirst("\\[", "")
                .replaceFirst("]", "")
                .replaceAll(",", "\n");
    }


}

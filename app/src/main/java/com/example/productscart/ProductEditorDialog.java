package com.example.productscart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.productscart.databinding.DialogProductEditBinding;
import com.example.productscart.model.Product;
import com.example.productscart.model.Variant;

import java.util.regex.Pattern;

public class ProductEditorDialog {

    private DialogProductEditBinding b;
    private Product product;


    void show(final Context context, final Product product, final OnProductEditedListener listener){
        this.product = product;

        //Inflate
        b = DialogProductEditBinding.inflate(
                LayoutInflater.from(context)
        );

        //Create dialog
        new AlertDialog.Builder(context)
                .setTitle("Edit Product")
                .setView(b.getRoot())
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(areProductDetailsValid())
                            listener.onProductEdited(ProductEditorDialog.this.product);
                        else
                            Toast.makeText(context, "Invalid details!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onCancelled();
                    }
                })
                .show();

        setupRadioGroup();
        preFillPreviousDetails();
    }

    private void preFillPreviousDetails() {
        //Set name
        b.name.setText(product.name);

        //Change RadioGroup Selected
        b.productType.check(product.type == Product.WEIGHT_BASED
                ? R.id.weight_based_rbtn : R.id.variants_based_rbtn);


        //Setup views according to type
        if(product.type == Product.WEIGHT_BASED){
            b.price.setText(product.pricePerKg + "");
            b.minQty.setText(product.minQtyToString());
        } else {
            b.variants.setText(product.variantsString());
        }
    }


    //Checks if all product details are valid
    private boolean areProductDetailsValid() {
        //Check name
        String name = b.name.getText().toString().trim();
        if(name.isEmpty())
            return false;


        switch (b.productType.getCheckedRadioButtonId()){
            case R.id.weight_based_rbtn :

                //Get values from views
                String pricePerKg = b.price.getText().toString().trim()
                        , minQty = b.minQty.getText().toString().trim();

                //Check inputs
                if(pricePerKg.isEmpty() || minQty.isEmpty() || !minQty.matches("\\d+(kg|g)"))
                    return false;

                //All good, set values of product
                product.initWeightBasedProduct(name
                        , Integer.parseInt(pricePerKg)
                        , extractMinQtyFromString(minQty));

                return true;
            case R.id.variants_based_rbtn :

                //Get value from view
                String variants = b.variants.getText().toString().trim();

                //Create product
                product.initVariantsBasedProduct(name);

                return areVariantsValid(variants);
        }

        return false;
    }


    //Checks for valid Variants input and extracts Variants from it
    private boolean areVariantsValid(String variants) {
        if(variants.length() == 0)
            return true;

        //Get strings of each variant
        String[] vs = variants.split("\n");

        //Check for each variant format using RegEx
        Pattern pattern = Pattern.compile("^\\w+(\\s|\\w)*,\\s*\\d+$");
        for (String variant : vs)
            if (!pattern.matcher(variant).matches())
                return false;

        //Extracts Variants from String[]
        product.fromVariantStrings(vs);

        return true;
    }


    //Returns weight in float from strings like "100kg" or "500g"
    private float extractMinQtyFromString(String minQty) {
        if(minQty.contains("kg"))
            return Integer.parseInt(minQty.replace("kg", ""));
        else
            return Integer.parseInt(minQty.replace("g", "")) / 1000f;
    }


    //Change visibility of views based on ProductType Selection
    private void setupRadioGroup() {
        b.productType.clearCheck();

        b.productType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.weight_based_rbtn){
                    b.weightBasedRoot.setVisibility(View.VISIBLE);
                    b.variantsRoot.setVisibility(View.GONE);
                } else {
                    b.variantsRoot.setVisibility(View.VISIBLE);
                    b.weightBasedRoot.setVisibility(View.GONE);
                }
            }
        });
    }


    //Listener Interface to notify Activity of Dialog events
    interface OnProductEditedListener{
        void onProductEdited(Product product);
        void onCancelled();
    }

}


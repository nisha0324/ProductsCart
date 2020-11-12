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
    public Product product;

    void show(final Context context, final Product products, final OnProductEditedListener listener){

        b = DialogProductEditBinding.inflate(LayoutInflater.from(context));
        this.product = products;

        new AlertDialog.Builder(context)
                .setTitle("Edit Product")
                .setView(b.getRoot())
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (areProductDetailsValid())
                            listener.onProductEdited(ProductEditorDialog.this.product);
                        else
                          Toast.makeText(context,"Invalid details!",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCELL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancelled();
                    }
                })
                .show();

        setUpRadioGroup();
        prefilldetails();

    }

    private void prefilldetails() {
        b.name.setText(product.name);

        b.productType.check(product.type == product.WEIGHT_BASED? R.id.weight_based_rbtn : R.id.variants_based_rbtn);

        if (product.type == Product.WEIGHT_BASED) {
            b.name.setText(product.name);
            b.minQty.setText(" "+product.minQty);
            b.price.setText(product.pricePerKg + "");
        }else {
            b.variants.setText(product.variantsString()+"");
        }
    }


    //To set Visibility of views based on the radiobutton checked
    private void setUpRadioGroup() {
        b.productType.clearCheck();

        b.productType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                if (i == R.id.weight_based_rbtn){
                    b.weightBasedRoot.setVisibility(View.VISIBLE);
                    b.variantsRoot.setVisibility(View.GONE);
                }

                else {
                    b.variantsRoot.setVisibility(View.VISIBLE);
                    b.weightBasedRoot.setVisibility(View.GONE);
                }
            }
        });
    }


    //To check the values entered are valid or not
    private boolean areProductDetailsValid() {
        //Check Name
        String name = b.name.getText().toString().trim();
        if (name.isEmpty())
            return false;
        product.name = name;

        switch (b.productType.getCheckedRadioButtonId()){
            case R.id.weight_based_rbtn :

                //get values from views
                String pricePerKg = b.price.getText().toString().trim()
                        ,minQty = b.minQty.getText().toString().trim();

                //check Inputs
                if (pricePerKg.isEmpty() || minQty.isEmpty() || !minQty.matches("\\d+(kg|g)"))
                    return false;

                //set values of products
                product = new Product(name,Integer.parseInt(pricePerKg),extractMinQtyFromString(minQty));

                return true;

            case R.id.variants_based_rbtn :

                String variants = b.variants.getText().toString().trim();

                product = new Product(name);

                return areVariantValid(variants);

        }
        return false;
    }




    //Returns weight in float from string
    private float extractMinQtyFromString(String minQty) {
        if (minQty.contains("kg"))
            return Integer.parseInt(minQty.replace("kg",""));
        else
            return Integer.parseInt(minQty.replace("g",""))/1000f;
    }


    //Check for valid Variants input and extract variants from it
    private boolean areVariantValid(String variants) {

        if (variants.length() == 0)
            return false;

        String[] vs = variants.split("\n");

        Pattern pattern = Pattern.compile("^\\w+(\\s|\\w)+,\\d+$");
        for (String variant: vs)
            if (!pattern.matcher(variant).matches())
                return false;

            // Extracts variants from string
            product.fromVariantStrings(vs);

         return true;
    }



    //Listener Interface to notify activity of dialog events
    interface OnProductEditedListener{
        void onProductEdited(Product product);
        void onCancelled();
   }

}

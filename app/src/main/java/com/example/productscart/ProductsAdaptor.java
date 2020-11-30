package com.example.productscart;


import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productscart.databinding.ProductItemBinding;
import com.example.productscart.databinding.VariantBasedProductBinding;
import com.example.productscart.databinding.WeightBasedProductBinding;
import com.example.productscart.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductsAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    //List of data
    public List<Product> visibleProducts
            , allProducts;

    int lastSelectedItemPosition;

    public ProductsAdaptor(Context context, List<Product> products) {
        this.context = context;
        allProducts = products;

        //Dynamic (Changes according to search query), so create at a new address. To avoid data loss
        visibleProducts = new ArrayList<>(products);
    }

    //Inflate the view for item and create a ViewHolder object based on viewType
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == Product.WEIGHT_BASED){
            //Inflate WeightBasedProduct layout
            WeightBasedProductBinding b = WeightBasedProductBinding.inflate(
                    LayoutInflater.from(context)
                    , parent
                    , false
            );

            //Create & Return WeightBasedProductVH
            //Child -> Parent
            return new WeightBasedProductVH(b);
        } else {
            //Inflate VariantsBasedProduct layout
            VariantBasedProductBinding b = VariantBasedProductBinding.inflate(
                    LayoutInflater.from(context)
                    , parent
                    , false
            );

            //Create & Return VariantsBasedProductVH
            return new VariantsBasedProductVH(b);
        }
    }

    //Return ViewType based on position
    @Override
    public int getItemViewType(int position) {
        return visibleProducts.get(position).type;
    }



    //Binds the data to view
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        //Get the data at position
        final Product product = visibleProducts.get(position);

        if(product.type == Product.WEIGHT_BASED){

            //Get binding
            //Parent -> Child
            WeightBasedProductVH vh = (WeightBasedProductVH) holder;
            WeightBasedProductBinding b = vh.b;

            //Bind data
            b.name.setText(product.name);
            b.pricePerKg.setText("Rs. " + product.pricePerKg);
            b.minQty.setText("MinQty - " + product.minQty + "kg");

            //Setup Contextual Menu inflation
            setupContextMenu(b.getRoot());

        } else {

            //Get binding
            VariantBasedProductBinding b = ((VariantsBasedProductVH) holder).b;

            //Bind data
            b.name.setText(product.name);
            b.variants.setText(product.variantsString());

            //Setup Contextual Menu inflation
            setupContextMenu(b.getRoot());

        }

        //Save dynamic position of selected item to access it in Activity
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lastSelectedItemPosition = holder.getAdapterPosition();
                return false;
            }
        });

    }


    public void filter(String query){
        //query = ""
        query = query.toLowerCase();
        visibleProducts = new ArrayList<>();

        for(Product product : allProducts){
            if(product.name.toLowerCase().contains(query))
                visibleProducts.add(product);
        }

        notifyDataSetChanged();
    }


    private void setupContextMenu(ConstraintLayout root) {
        root.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                if(!(context instanceof MainActivity))
                    return;

                MainActivity activity = ((MainActivity) context);

                if(!activity.isDragAndDropModeOn)
                    activity.getMenuInflater().inflate(R.menu.product_contextual_menu, contextMenu);
            }
        });
    }


    @Override
    public int getItemCount() {
        return visibleProducts.size();
    }



    //ViewHolder for WeightBasedProduct
    public static class WeightBasedProductVH extends RecyclerView.ViewHolder{

        WeightBasedProductBinding b;

        public WeightBasedProductVH(@NonNull WeightBasedProductBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }

    //ViewHolder for VariantsBasedProduct
    public static class VariantsBasedProductVH extends RecyclerView.ViewHolder{

        VariantBasedProductBinding b;

        public VariantsBasedProductVH(@NonNull VariantBasedProductBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }

}

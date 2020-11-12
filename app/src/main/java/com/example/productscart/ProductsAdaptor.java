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
    public List<Product> visibleProducts,
                           productList;

   public int lastSelectedItemPosition;

    public ProductsAdaptor(Context context, List<Product> products) {
        this.context = context;
        productList = products;
        this.visibleProducts = new ArrayList<>(products);
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == Product.WEIGHT_BASED){

            WeightBasedProductBinding b = WeightBasedProductBinding.inflate(LayoutInflater.from(context)
                                                                  ,parent
                                                                  ,false);
            return new WeightBasedProductVH(b);
        }

        else {
            VariantBasedProductBinding b = VariantBasedProductBinding.inflate(LayoutInflater.from(context)
                    , parent
                    , false);

            return new VariantsBasedProductVH(b);
        }
    }


    //Binds the data at position
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,final int position) {

        final Product product = visibleProducts.get(position);

        if (product.type == Product.WEIGHT_BASED){

            WeightBasedProductVH vh = (WeightBasedProductVH) holder;
            WeightBasedProductBinding b = vh.b;

            b.name.setText(product.name);
            b.pricePerKg.setText("Rs "+ product.pricePerKg);
            b.minQty.setText("MinQty - "+product.minQty+"kg");

            setUpContextMenu(b.getRoot());

        }else {

            VariantBasedProductBinding b = ((VariantsBasedProductVH) holder).b;
            b.name.setText(product.name);
            b.variants.setText(product.variantsString());

            setUpContextMenu(b.getRoot());

        }

        //Save dynamic position of selected item to access it in activity
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lastSelectedItemPosition = holder.getAdapterPosition();
                return false;
            }
        });

    }

    private void setUpContextMenu(ConstraintLayout root) {

        root.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (!(context instanceof MainActivity)) {
                    return;
                }
                MainActivity activity=(MainActivity)context;
                if (!activity.isDragModeOn) {

                    ((MainActivity) context).getMenuInflater().inflate(R.menu.product_contextual_menu, menu);
                }

            }
        });
    }

    //Return ViewType based on position
    @Override
    public int getItemViewType(int position) {
        return visibleProducts.get(position).type;
    }

    @Override
    public int getItemCount() {
        return visibleProducts.size();
    }


    public void filter(String query){
        query = query.toLowerCase();
        visibleProducts = new ArrayList<>();

        for(Product product: productList){
            if (product.name.toLowerCase().contains(query))
                visibleProducts.add(product);
        }

        notifyDataSetChanged();
    }





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

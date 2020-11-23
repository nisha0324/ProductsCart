package com.example.productscart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.app.Presentation;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.productscart.databinding.ActivityMainBinding;
import com.example.productscart.model.Product;
import com.example.productscart.model.Variant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ProductsAdaptor adaptor;
    private ArrayList<Product> products;
    private SearchView searchView;
    private ItemTouchHelper itemTouchHelper;
    public boolean isDragModeOn;

    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // FireBase
        setUpData();


      //  loadPreviousData();
       // setUpProductList();
    }


    //To set data for firebase
    private void setUpData() {
        List<Product> product = new ArrayList<>(Arrays.asList( new Product("Apple",100,1)
                                                       , new Product("Banana",50, 0.5f)));
        Map<String, Product> map = new HashMap<>();
        map.put("a",product.get(0));
        map.put("b",product.get(1));


         db.collection("Products").document("fruits and vegetables").set(map);

         //To update data
        //db.collection("Products").document("fruits and vegetables").update("a.pricePerKg","200");

    }



    //Save Data and Load previous Data
    private void saveData(){
        SharedPreferences preferences = getSharedPreferences("products_data", MODE_PRIVATE);
        preferences.edit().putString("data",new Gson().toJson(products)).apply();
    }

    private void loadPreviousData(){
        SharedPreferences preferences = getSharedPreferences("products_data",MODE_PRIVATE);
        String jsonData = preferences.getString("data", null);
        
        if (jsonData != null){
            products = new Gson().fromJson(jsonData, new TypeToken<List<Product>>() {
            }.getType());
        }else {
            products = new ArrayList<>();
        }
    }

    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();

    }


    private void setUpProductList() {
        products = new ArrayList<>();

        adaptor = new ProductsAdaptor(MainActivity.this,products);

        binding.productList.setAdapter(adaptor);
        binding.productList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        binding.productList.addItemDecoration(
                new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL)
        );
        setUpDragDrop();
    }



    private void setUpDragDrop(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(adaptor.visibleProducts, fromPosition, toPosition);
                binding.productList.getAdapter().notifyItemMoved(fromPosition, toPosition);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        itemTouchHelper = new ItemTouchHelper(simpleCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_catalog_options, menu);

         searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        //Meta Data
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
               // Log.i("MyLog", "onQueryTextChange : " +  query);
                adaptor.filter(query);
                return true;

            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("MyLog", "onQueryTextSubmit : " +  query);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_item :
                 showMenu();
                 return true;
            case R.id.drag_mode :
                toggleDragMode(item);
                return true;
            case R.id.sort :
                sortByName();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void changeIconBackground(MenuItem item) {
        Drawable icon = item.getIcon();
        if (isDragModeOn) {
            icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        } else {
            icon.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
        item.setIcon(icon);
    }

    private void toggleDragMode(MenuItem item) {
        changeIconBackground(item);
        if (isDragModeOn) {
            itemTouchHelper.attachToRecyclerView(null);
        }else {
            itemTouchHelper.attachToRecyclerView(binding.productList);
        }
        isDragModeOn = !isDragModeOn;
    }

    private void sortByName(){
        Collections.sort(adaptor.visibleProducts, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.name.compareToIgnoreCase(o2.name);
            }
        });
        adaptor.notifyDataSetChanged();
        Toast.makeText(this, "List sorted", Toast.LENGTH_SHORT).show();
    }


    // context Menu
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.product_edit :
                editLastSelectedItem();
                return true;

            case R.id.product_remove :
                removeLastSelectedItem();

                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void removeLastSelectedItem() {
        new AlertDialog.Builder(this)
                .setTitle("Do you really want to remove this product?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Product productToBeRemoved = adaptor.visibleProducts.get(adaptor.lastSelectedItemPosition);

                        adaptor.visibleProducts.remove(productToBeRemoved);
                        adaptor.productList.remove(productToBeRemoved);

                        adaptor.notifyItemRemoved(adaptor.lastSelectedItemPosition);

                        Toast.makeText(MainActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void editLastSelectedItem() {

        Product lastSelectedProduct = adaptor.visibleProducts.get(adaptor.lastSelectedItemPosition);
//TODO: ask products ku use kiya why not visibleProducts
        new ProductEditorDialog().show(this, lastSelectedProduct, new ProductEditorDialog.OnProductEditedListener() {
            @Override
            public void onProductEdited(Product product) {
                //Update View
                products.set(adaptor.lastSelectedItemPosition, product);
                adaptor.notifyItemChanged(adaptor.lastSelectedItemPosition);
            }

            @Override
            public void onCancelled() {
                Toast.makeText(MainActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
            }
        });


    }


    //EditItem Menu
    private void showMenu() {

        new ProductEditorDialog().show(MainActivity.this, new Product(), new ProductEditorDialog.OnProductEditedListener() {
            @Override
            public void onProductEdited(Product product) {
                adaptor.productList.add(product);

                if (isNameInQuery(product.name)) {
                    adaptor.visibleProducts.add(product);
                }
                adaptor.notifyItemChanged(products.size()-1);
                Toast.makeText(MainActivity.this, "" + product.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {

                Toast.makeText(MainActivity.this,"Cancelled",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private boolean isNameInQuery(String name) {
        String query = searchView.getQuery().toString().toLowerCase();
        return name.toLowerCase().contains(query);
    }
}
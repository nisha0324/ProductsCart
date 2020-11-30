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
import com.example.productscart.model.Inventory;
import com.example.productscart.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class  MainActivity extends AppCompatActivity {

    ActivityMainBinding b;
    ProductsAdaptor adaptor;
    private List<Product> products;
    private SearchView searchView;
    private ItemTouchHelper itemTouchHelper;
    public boolean isDragAndDropModeOn;
    private MyApp app;

    public FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        db = FirebaseFirestore.getInstance();

        app = (MyApp)getApplicationContext();
        loadPreviousData();
    }

    //Data Save & Reload

    private void saveData() {
        if(app.isOffline()){
            app.showToast(this, "Unable to save. You are offline!");
            return;
        }

        app.showLoadingDialog(this);

        Inventory inventory = new Inventory(products);

        //Save on cloud
        db.collection("inventory")
                .document("products")
                .set(inventory)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                        saveLocally();

                        app.hideLoadingDialog();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to save on cloud", Toast.LENGTH_SHORT).show();
                        app.hideLoadingDialog();
                        e.printStackTrace();
                    }
                });
    }

    private void saveLocally() {
        SharedPreferences preferences = getSharedPreferences("products_data", MODE_PRIVATE);
        preferences.edit()
                .putString("data", new Gson().toJson(products))
                .apply();
    }

    private void loadPreviousData() {
        SharedPreferences preferences = getSharedPreferences("products_data", MODE_PRIVATE);
        String jsonData = preferences.getString("data", null);

        if(jsonData != null){
            products = new Gson().fromJson(jsonData, new TypeToken<List<Product>>(){}.getType());
            setupProductsList();
        }
        else
            fetchFromCloud();
    }

    private void fetchFromCloud() {
        if(app.isOffline()){
            app.showToast(this, "Unable to save. You are offline!");
            return;
        }

        app.showLoadingDialog(this);


        db.collection("inventory")
                .document("products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Inventory inventory = documentSnapshot.toObject(Inventory.class);
                            products = inventory.products;
                            saveLocally();
                        } else
                            products = new ArrayList<>();
                        setupProductsList();
                        app.hideLoadingDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to save on cloud", Toast.LENGTH_SHORT).show();
                        app.hideLoadingDialog();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Unsaved changes")
                .setMessage("Do you want to save?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveData();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    private void setupProductsList() {
        //Create DataSet
         products = new ArrayList<>(Arrays.asList(new Product("Apple",200,1.5f)));
        //Create adapter object
        adaptor = new ProductsAdaptor(this, products);

        //Set the adapter & LayoutManager to RV
        b.productList.setAdapter(adaptor);
        b.productList.setLayoutManager(new LinearLayoutManager(this));
        b.productList.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );

        setupDragAndDrop();
    }

    private void setupDragAndDrop() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(adaptor.visibleProducts, fromPosition, toPosition);
                b.productList.getAdapter().notifyItemMoved(fromPosition, toPosition);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
    }



    //OPTIONS MENU

    //Inflates the option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_catalog_options, menu);


        searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        //Meta data
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                Log.i("MyLog", "onQueryTextChange : " +  query);
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

    //OnItem Click Listener for Options Menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_item :
                showDialogForNewProduct();
                return true;

            case R.id.sort :
                sortList();
                return true;

            case R.id.drag_mode :
                toggleDragAndDropMode(item);
                return true;
        }



        return super.onOptionsItemSelected(item);
    }



    //Drag & Drop

    private void toggleDragAndDropMode(@NonNull MenuItem item) {
        changeIconBackground(item);

        if(isDragAndDropModeOn)
            itemTouchHelper.attachToRecyclerView(null);
        else
            itemTouchHelper.attachToRecyclerView(b.productList);

        isDragAndDropModeOn = !isDragAndDropModeOn;
    }

    private void changeIconBackground(@NonNull MenuItem item) {
        Drawable icon = item.getIcon();
        if(isDragAndDropModeOn){
            icon.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        } else {
            icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
        item.setIcon(icon);
    }





    //OnClick handler for ContextualMenu of Product
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

    //Callbacks for edit & remove

    private void editLastSelectedItem() {
        //Get data to be edited                         104
        //products = allProducts = ["Apple", "Orange", "Grapes", "Kiwi"] //Grapes index = 2
        //query : "grap"        104
        //visibleProducts = ["Grapes1"] //Grapes index = 0
        //104

        Product lastSelectedProduct = adaptor.visibleProducts.get(adaptor.lastSelectedItemPosition);

        //Show Editor Dialog
        new ProductEditorDialog()
                .show(this, lastSelectedProduct, new ProductEditorDialog.OnProductEditedListener() {
                    @Override
                    public void onProductEdited(Product product) {
                        //Update view
                        if(!isNameInQuery(product.name)) {
                            adaptor.visibleProducts.remove(product);
                            adaptor.notifyItemRemoved(adaptor.lastSelectedItemPosition);
                        } else
                            adaptor.notifyItemChanged(adaptor.lastSelectedItemPosition);
                    }

                    @Override
                    public void onCancelled() {
                        Toast.makeText(MainActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeLastSelectedItem() {
        new AlertDialog.Builder(this)
                .setTitle("Do you really want to remove this product?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Product productToBeRemoved = adaptor.visibleProducts.get(adaptor.lastSelectedItemPosition);

                        adaptor.visibleProducts.remove(productToBeRemoved);
                        adaptor.allProducts.remove(productToBeRemoved);

                        adaptor.notifyItemRemoved(adaptor.lastSelectedItemPosition);

                        Toast.makeText(MainActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }


    //Add new item

    private void showDialogForNewProduct() {
        new ProductEditorDialog()
                .show(this, new Product(), new ProductEditorDialog.OnProductEditedListener() {
                    @Override
                    public void onProductEdited(Product product) {
                        adaptor.allProducts.add(product);

                        if(isNameInQuery(product.name)){
                            adaptor.visibleProducts.add(product);
                            adaptor.notifyItemInserted(adaptor.visibleProducts.size() - 1);
                        }
                    }

                    @Override
                    public void onCancelled() {
                        Toast.makeText(MainActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    //Utils

    private void sortList() {
        Collections.sort(adaptor.visibleProducts, new Comparator<Product>(){
            @Override
            public int compare(Product a, Product b) {
                return a.name.compareTo(b.name);
            }
        });
        adaptor.notifyDataSetChanged();
        Toast.makeText(this, "List sorted!", Toast.LENGTH_SHORT).show();
    }

    private boolean isNameInQuery(String name) {
        String query = searchView.getQuery().toString().toLowerCase();
        return name.toLowerCase().contains(query);
    }
}
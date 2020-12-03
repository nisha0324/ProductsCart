package com.example.productscart.orderListActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.productscart.MyApp;
import com.example.productscart.databinding.ActivityOrderListBinding;
import com.example.productscart.model.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    
    ActivityOrderListBinding b;
    MyApp app;
    public FirebaseFirestore db;
    private Order order;
    List<Order> orderList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityOrderListBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        MyApp app = (MyApp) getApplicationContext();
        loadData();

    }

    private void loadData() {

            if(app.isOffline()){
                app.showToast(this, "Unable to save. You are offline!");
                return;
            }

            fetchData();
        }

        private void fetchData() {
            app.showLoadingDialog(this);

            db.collection("inventory")
                    .document("products")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                               Order order = documentSnapshot.toObject(Order.class);
                               orderList.add(order);
                            }
                            else
                                orderList = new ArrayList<>();
                            setUpProductsList();
                            app.hideLoadingDialog();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            app.hideLoadingDialog();
                            app.showToast(OrderListActivity.this, e.getMessage());
                            e.printStackTrace();
                        }
                    });
        }

    private void setUpProductsList() {
    }
}
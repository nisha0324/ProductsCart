package com.example.productscart.orderListActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.productscart.MyApp;
import com.example.productscart.databinding.ActivityOrderListBinding;
import com.example.productscart.model.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    
    ActivityOrderListBinding b;
    MyApp app;
    public FirebaseFirestore db;
    OrderAdaptor adaptor;
    List<Order> orderList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityOrderListBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        db = FirebaseFirestore.getInstance();

        app = (MyApp) getApplicationContext();
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

            db.collection("Orders")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                Order order = snapshot.toObject(Order.class);
                                orderList.add(order);
                                app.hideLoadingDialog();
                            }

                            setUpProductsList();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            orderList = new ArrayList<>();
                            Toast.makeText(OrderListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    private void setUpProductsList() {


        adaptor = new OrderAdaptor(OrderListActivity.this, orderList);
        b.orderListView.setAdapter(adaptor);
        b.orderListView.setLayoutManager(new LinearLayoutManager(OrderListActivity.this));
        b.orderListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }
}
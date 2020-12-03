package com.example.productscart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.productscart.orderListActivity.OrderListActivity;
import com.example.productscart.databinding.ActivityFirstBinding;

public class firstActivity extends AppCompatActivity {

    ActivityFirstBinding b;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityFirstBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.productsDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(firstActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        b.orderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(firstActivity.this, OrderListActivity.class);
                startActivity(intent);
            }
        });
    }
}
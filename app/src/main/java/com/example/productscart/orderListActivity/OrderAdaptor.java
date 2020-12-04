package com.example.productscart.orderListActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.productscart.databinding.OrderListBinding;
import com.example.productscart.databinding.UserOrderDetailssBinding;
import com.example.productscart.model.Order;

import java.util.List;

public class OrderAdaptor extends RecyclerView.Adapter< OrderAdaptor.OrderViewHolder> {

     Context context;
     List<Order> orders;

    public OrderAdaptor(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserOrderDetailssBinding b = UserOrderDetailssBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false);

        return new OrderViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
             Order order = orders.get(position);
             UserOrderDetailssBinding b = ((OrderViewHolder) holder).b;

             b.userName.setText(" " +order.userName);
             b.userAddress.setText(""+order.UserAddress);
             b.PhoneNumber.setText(""+order.userPhoneNo);
             b.totalAmount.setText(" Total Amount : Rs " +order.subTotal);
             b.orderId.setText(""+order.orderId);

             for (int i = 0; i < order.orderList.size(); i++ ){

                 OrderListBinding binding = OrderListBinding.inflate(LayoutInflater.from(context));
                 binding.productName.setText(""+order.orderList.get(i).name);
                 binding.quaantity.setText(""+(int) order.orderList.get(i).qty);
                 binding.Price.setText("RS: "+order.orderList.get(i).price);

             }

             b.accept.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Toast.makeText(context, "Accepted"+order.userName, Toast.LENGTH_SHORT).show();
                 }
             });
             
             b.reject.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show();
                 }
             });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder{

         UserOrderDetailssBinding b;

        public OrderViewHolder( UserOrderDetailssBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}

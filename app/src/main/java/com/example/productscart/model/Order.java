package com.example.productscart.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;


    public class Order {

        public String orderId;
        public int orderSat;
        public Timestamp orderPlacedTs ;

        public String userName, userPhoneNo, UserAddress;
        public List<CartItem> orderedItems;
        public   int subTotal;

        public Order() {
        }

        public void inItOrder(String userName, String userPhoneNo, String userAddress) {
            this.userName = userName;
            this.userPhoneNo = userPhoneNo;
            UserAddress = userAddress;
            orderSat = orderStatus.PLACED;
            orderPlacedTs = Timestamp.now();

        }






        public static class orderStatus{

            public static final int PLACED = 1
                    , DELIVERED = 0, DECLINED = -1;
        }

    }


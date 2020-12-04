package com.example.productscart.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


    public class Order implements Serializable {

        public String orderId;
        public int orderSat;
        public Timestamp orderPlacedTs ;

        public String userName, userPhoneNo, UserAddress;
        public List<CartItem> cartItems;
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

        public Order(String orderId, int orderSat, Timestamp orderPlacedTs, String userName, String userPhoneNo, String userAddress, List<CartItem> cartItems, int subTotal) {
            this.orderId = orderId;
            this.orderSat = orderSat;
            this.orderPlacedTs = orderPlacedTs;
            this.userName = userName;
            this.userPhoneNo = userPhoneNo;
            UserAddress = userAddress;
            this.cartItems = cartItems;
            this.subTotal = subTotal;
        }

        public static class orderStatus{

            public static final int PLACED = 1
                    , DELIVERED = 0, DECLINED = -1;
        }

    }


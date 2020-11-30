package com.example.productscart;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import android.app.AlertDialog;

import com.google.firebase.firestore.FirebaseFirestore;

public class MyApp extends Application {


    private ConnectivityManager connectivityManager;
    private AlertDialog dialog;


    @Override
    public void onCreate() {
        super.onCreate();


        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    /** Loading Dialog **/
    public void showLoadingDialog(Context context){
        dialog = new AlertDialog.Builder(context)
                .setTitle("Loading.....")
                .setMessage("Please Wait!!")
                .show();
    }
    public void hideLoadingDialog(){
        if (dialog!=null){
            dialog.dismiss();
        }
    }

    /** Toast **/
    public void showToast(Context context,String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /** Internet Connectivity **/
    public boolean isOffline(){
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo dataNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return !(wifiNetworkInfo.isConnected() || dataNetworkInfo.isConnected());
    }
}

package com.example.foodrunner

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
//class to check if device is connected to internet
class ConnectionManager{
    fun checkConnectivity(context: Context):Boolean{
        val connectionManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork:NetworkInfo?=connectionManager.activeNetworkInfo
        if(activeNetwork?.isConnected !=null){
            return activeNetwork.isConnected
        }else{
            return false
        }

    }
}
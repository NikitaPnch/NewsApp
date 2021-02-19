package com.example.newsapp.base

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

open class BaseFragment(@LayoutRes layoutRes: Int) : Fragment(layoutRes) {

    private val connectivityManager: ConnectivityManager by lazy {
        requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val connectivityManagerCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onAvailable()
            }

            override fun onLost(network: Network) {
                onLost()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateConnection()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> marshmallowNetworkAvailableRequest()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun marshmallowNetworkAvailableRequest() {
        val builder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        connectivityManager.registerNetworkCallback(builder.build(), connectivityManagerCallback)
    }

    override fun onPause() {
        super.onPause()
        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
    }

    private fun updateConnection() {
        if (isInternetAvailable()) {
            onAvailable()
        } else {
            onLost()
        }
    }

    protected open fun onAvailable() {
    }

    protected open fun onLost() {
    }

    private fun isInternetAvailable(): Boolean {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
package com.android.books.utils.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkUtil {

  public static int TYPE_WIFI = 1;
  public static int TYPE_MOBILE = 2;
  public static int TYPE_NOT_CONNECTED = 0;

  public static int getConnectivityStatus(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    if (networkInfo != null) {
      if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
          && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
        return TYPE_WIFI;
      } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
          && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
        return TYPE_MOBILE;
      }
    }
    return TYPE_NOT_CONNECTED;
  }

  public static boolean isNetworkConnected(Context context) {
    int networkStatus = getConnectivityStatus(context);
    return networkStatus == TYPE_WIFI || networkStatus == TYPE_MOBILE;
  }
}

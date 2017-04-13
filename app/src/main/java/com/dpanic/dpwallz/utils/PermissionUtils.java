package com.dpanic.dpwallz.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import com.dpanic.dpwallz.R;

/**
 * Created by dpanic on 3/3/2017.
 * Project: DPWallz
 */

public class PermissionUtils {

    public static boolean requestPermissions(final Context context, final String permission, final int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(context, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                                                                        permission)) {
                    showMessageDialog(context, context.getResources().getString(R.string.permission_explaination),
                                      new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              performRequest(context, permission, requestCode);
                                          }
                                      });
                } else {
                    performRequest(context, permission, requestCode);
                }
                return true;
            }
        }

        return false;
    }

    private static void performRequest(Context context, String permission, int requestCode) {
        ActivityCompat.requestPermissions((Activity) context, new String[] { permission }, requestCode);
    }

    public static boolean isHasPermission(Context context, String permission) {
        return Build.VERSION.SDK_INT < 23 ||
                ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;

    }

    private static void showMessageDialog(Context context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(context.getResources().getString(R.string.string_ok), okListener)
                .setNegativeButton(context.getResources().getString(R.string.string_cancel), null)
                .create()
                .show();
    }
}

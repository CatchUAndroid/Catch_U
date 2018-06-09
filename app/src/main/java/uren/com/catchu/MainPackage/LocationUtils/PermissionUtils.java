package uren.com.catchu.MainPackage.LocationUtils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ASUS on 9.6.2018.
 */

public class PermissionUtils {

    private static PermissionUtils instance = null;

    private ArrayList<String> permission_list;
    private String dialog_content;
    private int request_code;
    private Activity current_activity;
    public boolean isPermissionGranted = false;

    public static PermissionUtils getInstance() {
        if (instance == null) {
            instance = new PermissionUtils();
        }
        return(instance);
    }

    private PermissionUtils() {
    }

    public void checkPermissions(ArrayList<String> permissions, String dialog_content, int request_code, Activity activity){

        this.permission_list = permissions;
        this.dialog_content = dialog_content;
        this.request_code = request_code;
        this.current_activity = activity;

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions(permissions, request_code)) {
                isPermissionGranted = true;
                Log.i("all permissions", "granted");
                Log.i("proceed", "to callback");
            }
        } else {
            //permissionResultCallback.PermissionGranted(request_code);

            Log.i("all permissions", "granted");
            Log.i("proceed", "to callback");
        }


    }

    private boolean checkAndRequestPermissions(ArrayList<String> permissions, int request_code) {
        if (permissions.size() > 0) {

            ArrayList<String> listPermissionsNeeded = new ArrayList<>();
            listPermissionsNeeded = new ArrayList<>();

            for (int i = 0; i < permissions.size(); i++) {
                int hasPermission = ContextCompat.checkSelfPermission(current_activity, permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions.get(i));
                }

            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(current_activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), request_code);
                return false;
            }
        }

        return true;
    }

}

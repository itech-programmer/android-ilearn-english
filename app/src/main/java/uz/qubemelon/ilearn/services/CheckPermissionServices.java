package uz.qubemelon.ilearn.services;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CheckPermissionServices {

    /* all global field instances are here */
    private Activity activity;

    public CheckPermissionServices(Activity activity) {
        this.activity = activity;
    }

    // CHECK PERMISSIONS -------------------------------------------------------------------------

    public boolean check_permission_for_read_external_storage() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean check_permission_for_write_external_storage() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    // REQUEST PERMISSIONS -------------------------------------------------------------------------
    public void permission_request_for_read_external_storage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        }
    }

    public void permission_request_for_write_external_storage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
        }
    }

}

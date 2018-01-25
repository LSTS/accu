package pt.lsts.accu;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;

import pt.lsts.accu.state.Accu;
import pt.lsts.imc.IMCDefinition;

public class PermissionActivity extends Activity {

    public static PermissionActivity fa;
    public boolean result_permission = true;
    public boolean statePermissionFlag = false;

    public boolean statePermission(){
        return statePermissionFlag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fa = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Log.i("MEU", "AQUI 0 "+Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT > 19)
            requestForSpecificPermission();
        else
            Log.i("MEU", "AQUI 3");
    }

    private void requestForSpecificPermission() {
        int PERMISSION_ALL = 199;
        String[] PERMISSIONS = {
                Manifest.permission.VIBRATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS};

        hasPermissions(this, PERMISSIONS);
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
    }

    public void hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                ActivityCompat.checkSelfPermission(context, permission);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == -1)
                result_permission = false;
        }
        if (!result_permission) {
            close_app();
        } else {
            Log.i("AQUI", "AQUI 0");

            IMCDefinition.getInstance();

            // Sequence of calls needed to properly initialize ACCU
            Accu.getInstance(this);
            Accu.getInstance().load();
            Accu.getInstance().start();
            System.out.println("Global ACCU Object Initialized");

            statePermissionFlag = true;

            Log.i("AQUI", "AQUI 1");
            Intent intent = new Intent(PermissionActivity.this, Main.class);
            Log.i("AQUI", "AQUI 2");
            //finish();
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Log.i("AQUI", "AQUI 3");

            this.finish();
            Log.i("AQUI", "AQUI 4");
        }
    }

    public void close_app() {
        //Log.i(TAG, "close app");
        showErrorInfo("Please accept the permissions!!!\nIt is necessary to accept the permissions to run da app!!!");
    }

    void showErrorInfo(String text){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Error !!!");
        alertDialogBuilder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PermissionActivity.this.finish();
                        System.exit(0);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

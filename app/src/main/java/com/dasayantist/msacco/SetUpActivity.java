package com.dasayantist.msacco;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.toe.chowder.Chowder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;

public class SetUpActivity extends AppCompatActivity {

    String PhoneType, IMEINumber, SubscriberID, SIMSerialNumber, NetworkCountryISO, SIMCountryISO, SoftwareVersion, strphoneType;
    static final int PERMISSION_READ_STATE = 123;

    private ProgressDialog progressDialog;
    String oid, rid, uid, add, od, ba;
    private int success = 0;
    private String path = "http://192.168.43.248/zsacco/order.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait while we setup your a/c");
        progressDialog.setCancelable(false);

        start();
    }

    public void start() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            MyTelephonyManager();
            RegPhone();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_READ_STATE);
        }
    }

    public void RegPhone() {

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int phoneType = manager.getPhoneType();
        switch (phoneType) {
            case (TelephonyManager.PHONE_TYPE_CDMA):
                ;
                strphoneType = "CDMA";
                break;
            case (TelephonyManager.PHONE_TYPE_GSM):
                ;
                strphoneType = "GSM";
                break;
            case (TelephonyManager.PHONE_TYPE_NONE):
                ;
                strphoneType = "NONE";
                break;
        }
        boolean isRoaming = manager.isNetworkRoaming();

        PhoneType = strphoneType;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
             return;
        }
        IMEINumber = manager.getDeviceId();
        SubscriberID =manager.getSubscriberId();
        SIMSerialNumber = manager.getSimSerialNumber();
        NetworkCountryISO = manager.getNetworkCountryIso();
        SIMCountryISO = manager.getSimCountryIso();
        SoftwareVersion = manager.getDeviceSoftwareVersion();
        oid = strphoneType;
        rid = manager.getDeviceId();
        //uid = etPhoneNumber.getText().toString();
        uid = manager.getSubscriberId();
        add = manager.getSimSerialNumber();
        od = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        ba = manager.getSimCountryIso();

        new PostDataTOServer().execute();
    }
    private class PostDataTOServer extends AsyncTask<Void, Void, Void> {

        String response = "";
        //Create hashmap Object to send parameters to web service
        HashMap<String, String> postDataParams;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            try {
                //uid,dateid,pbill,mtel,acdetail,amount
                String link = path;
                String data = URLEncoder.encode("acdetail", "UTF-8") + "=" +
                        URLEncoder.encode(oid, "UTF-8");
                data += "&" + URLEncoder.encode("pbill", "UTF-8") + "=" +
                        URLEncoder.encode(rid, "UTF-8");
                data += "&" + URLEncoder.encode("mtel", "UTF-8") + "=" +
                        URLEncoder.encode(uid, "UTF-8");
                data += "&" + URLEncoder.encode("uid", "UTF-8") + "=" +
                        URLEncoder.encode(add, "UTF-8");
                data += "&" + URLEncoder.encode("dateid", "UTF-8") + "=" +
                        URLEncoder.encode(od, "UTF-8");
                data += "&" + URLEncoder.encode("amount", "UTF-8") + "=" +
                        URLEncoder.encode(ba, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                return null;
            } catch (Exception e) {
                String s = "Exception: " + e.getMessage();
                System.out.print(s);
                return null;

            }
        }


        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (success == 1) {
                Toast.makeText(getApplicationContext(), "Payment  successfully..!", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

                Intent n = new Intent(SetUpActivity.this, PrincipalActivity.class);
                startActivity(n);

            }else if(success == 0){
                Toast.makeText(getApplicationContext(), "A/C setup fail, The account is currently active on another phone, please use the phone or delete a/c in old phone", Toast.LENGTH_LONG).show();
            }
        }

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_STATE: {
                if (grantResults.length>=0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyTelephonyManager();
                } else {
                    Toast.makeText(this, "you don't have required permission to make the Action", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void MyTelephonyManager() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int phoneType = manager.getPhoneType();
        switch (phoneType) {
            case (TelephonyManager.PHONE_TYPE_CDMA):
                ;
                strphoneType = "CDMA";
                break;
            case (TelephonyManager.PHONE_TYPE_GSM):
                ;
                strphoneType = "GSM";
                break;
            case (TelephonyManager.PHONE_TYPE_NONE):
                ;
                strphoneType = "NONE";
                break;
        }
        boolean isRoaming = manager.isNetworkRoaming();

        PhoneType = strphoneType;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        IMEINumber = manager.getDeviceId();
        SubscriberID =manager.getSubscriberId();
        SIMSerialNumber = manager.getSimSerialNumber();
        NetworkCountryISO = manager.getNetworkCountryIso();
        SIMCountryISO = manager.getSimCountryIso();
        SoftwareVersion = manager.getDeviceSoftwareVersion();

    Toast.makeText(this, PhoneType + IMEINumber + SubscriberID + SIMSerialNumber + NetworkCountryISO + SIMCountryISO + SoftwareVersion + strphoneType
    , Toast.LENGTH_SHORT).show();

    }

}

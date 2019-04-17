package com.dasayantist.msacco;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dasayantist.msacco.app.AppController;
import com.dasayantist.msacco.interfaces.IMainActivityListener;
import com.toe.chowder.Chowder;
import com.toe.chowder.interfaces.PaymentListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

public class Premium extends BaseActivity implements PaymentListener, IMainActivityListener {

    @IntDef({NAVIGATION_MODE_STANDARD, NAVIGATION_MODE_TABS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationMode {}

    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_TABS = 1;
    public static final String NAVIGATION_POSITION = "navigation_position";

    private int mCurrentMode = NAVIGATION_MODE_STANDARD;

    //Test parameters you can replace these with your own PayBill details
    String PAYBILL_NUMBER = "877433";
    String PASSKEY = "be9dc35907bd98ee471bbfe9ddd87e724cdef18ac3eabfecdfd08f2cc4a5c3e0";

    EditText  etPhoneNumber, etLAmount;
    TextView acno, fname, pno, mtype;
    TextView txtacno, txtfname, txtpno, txtmtype;
    Button bPay, bConfirm, bAccess, mPay, pstatus, bLogin;
    CardView cardparea;
    int attempt_counter = 3;
    private static TextView attempts;
   // private PrefManager prefManager;
    private SharedPreference sharedPreference;
    private String urlForJsonObject = AppController.baseUrl+"get_all_products.php";

    //view products
   private static String TAG = DBActivity.class.getSimpleName();

    //progress dialog
    private ProgressDialog pDialog;

    private ListView productsList;
    private TextView noConnection;
    private TextView viewDescription;
    private SwipeRefreshLayout swipeLayout;

    //temporary string to show the parsed response
    private String jsonResponse;


    Chowder chowder;
    String oid, rid, uid, add, od, ba;
    private JSONObject json;
    private String pid;


    private int success = 0;
    private String path = "http://10.0.1.113/zsacco/order.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        WebView webView = (WebView) findViewById(R.id.webViewMusic);
        webView.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings=webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        webView.loadUrl("file:///android_asset/MusicContent.html");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait....");
        pDialog.setCancelable(false);

        /*prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            accessMainApp();
            finish();
        } */
        productsList = (ListView) findViewById(R.id.listloan);
        noConnection = (TextView) findViewById(R.id.no_connection);
        viewDescription = (TextView) findViewById(R.id.view_description);
//        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
//        swipeLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait....");
        pDialog.setCancelable(false);

        if (checkForConnection()){
            viewDescription.setVisibility(View.VISIBLE);
            makeJsonObjectRequest();
        }
        else{
            viewDescription.setVisibility(View.GONE);
            noConnection.setVisibility(View.VISIBLE);
        }


        setUp();

    }




    private void setUp() {
        chowder = new Chowder(Premium.this, PAYBILL_NUMBER, PASSKEY, this);


        etLAmount = (EditText) findViewById(R.id.etLAmount) ;
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);

        acno = (TextView) findViewById(R.id.acno);
        fname = (TextView) findViewById(R.id.fname);
        pno = (TextView) findViewById(R.id.pno);
        mtype = (TextView) findViewById(R.id.mtype);


        txtacno = (TextView) findViewById(R.id.txtacno);
        txtfname = (TextView) findViewById(R.id.txtfname);
        txtpno = (TextView) findViewById(R.id.txtpno);
        txtmtype = (TextView) findViewById(R.id.txtmtype);



        mPay =(Button) findViewById(R.id.mpay);
        pstatus =(Button) findViewById(R.id.pstatus);
        bPay = (Button) findViewById(R.id.bPay);
        bConfirm = (Button) findViewById(R.id.bConfirm);
        bAccess = (Button) findViewById(R.id.bAccess);
        cardparea = (CardView) findViewById(R.id.cardparea) ;

        attempts = (TextView)findViewById(R.id.textView_attemt_Count);
        attempts.setText(Integer.toString(attempt_counter));


        mPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardparea.setVisibility(View.VISIBLE);
            }
        });
        pstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardparea.setVisibility(View.GONE);
                makeJsonObjectRequest();
                //Toast.makeText(getApplicationContext(), "Unable to find the database url. Invalid database url", Toast.LENGTH_SHORT).show();
            }
        });

        bPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (etPhoneNumber.getText().toString().trim().length() <= 0 || !isNetworkStatusAvialable(getApplicationContext()) ) {

                    android.app.AlertDialog.Builder a_builder = new android.app.AlertDialog.Builder(Premium .this);
                    a_builder.setMessage("Ensure your internet is turned on\n" +
                            " And  Enter Your Phone Number to make payment").setCancelable(false)

                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();





                }else {


                    RegSer();
                    String amount = etLAmount.getText().toString().trim();
                    String phoneNumber = etPhoneNumber.getText().toString().trim();
                    //Your product's ID must have 13 digits
                    String productId = "SACCO Loan";

                    chowder.processPayment(amount, phoneNumber.replaceAll("\\+", ""), productId);
                    //      That's it! You can now process payments using the M-Pesa API
                    //      IMPORTANT: Any cash you send to the test PayBill number is non-refundable, so use small amounts to test


                    //   ##What's happening:
                    //      The Merchant captures the payment details and prepares call to the SAG’s endpoint
                    //      The Merchant invokes SAG’s processCheckOut interface
                    //      The SAG validates the request sent and returns a response
                    //      Merchant receives the processCheckoutResponse parameters namely
                    //      TRX_ID, ENC_PARAMS, RETURN_CODE, DESCRIPTION and CUST_MSG (Customer message)
                    //      The merchant is supposed to display the CUST_MSG to the customer after which the merchant should invoke SAG’s confirmPaymentRequest interface to confirm the transaction
                    //      The system will push a USSD menu to the customer and prompt the customer to enter their BONGA PIN and any other validation information.
                    //      The transaction is processed on M-PESA and a callback is executed after completion of the transaction
                }     }
        });





        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLastPayment();
            }
        });
        bAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessApp();
            }
        });
    }
    private void makeJsonObjectRequest() {

        showpDialog();

        final List<String> productsArrayList = new ArrayList<String>();


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlForJsonObject,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString()); //for android log cat??

                        try {
                            // Parsing json object response
                            // response will be a json object
                            int success = response.getInt("success");
                            if (success == 1){
                                JSONArray products = response.getJSONArray("products");

                                for(int i =0; i<products.length(); i++){
                                    JSONObject phone = products.getJSONObject(i);
                                    String name = phone.get("name").toString();
                                    String price = phone.get("price").toString();
                                    String description = phone.get("description").toString();
                                    jsonResponse = "";
                                    jsonResponse += "Loan ID: " + name + "\n\n";
                                    jsonResponse += "Principal: " + price + "\n\n";
                                    jsonResponse += "Interest: " + description + "\n\n";
                                    productsArrayList.add(jsonResponse);

                                }
//                                acno.setText("AC: " + price  );
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        Premium.this,
                                        android.R.layout.simple_list_item_1,
                                        productsArrayList );

                                productsList.setAdapter(arrayAdapter);
                            }
                            else {
                                Toast.makeText(Premium.this, "No products in the database", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        hidepDialog();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG,"Error: "+ volleyError.getMessage() );
                Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
        //adding request to request queue
        AppController.getmInstance().addToRequestQueue(jsonObjReq);
    }



    private void showpDialog(){
        if(!pDialog.isShowing()){
            pDialog.show();
        }
    }

    private void hidepDialog(){
        if(pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

//    @Override
//    public void onRefresh() {
//        if (checkForConnection()){
//            viewDescription.setVisibility(View.VISIBLE);
//            noConnection.setVisibility(View.GONE);
//            makeJsonObjectRequest();
//            swipeLayout.setRefreshing(false);
//
//        }
//        else{
//            productsList.setVisibility(View.GONE);
//            viewDescription.setVisibility(View.GONE);
//            noConnection.setVisibility(View.VISIBLE);
//            swipeLayout.setRefreshing(false);
//
//        }
//    }

    @Override
    public void onDestroy() {
        // You must call this or the ad adapter may cause a memory leak
        super.onDestroy();
    }

    private Boolean checkForConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    private void confirmLastPayment() {
        SharedPreferences sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        //We saved the last transaction id to Shared Preferences
        String transactionId = sp.getString("chowderTransactionId", null);

        //Call chowder.checkTransactionStatus to check a transaction
        //Check last transaction
        if (transactionId != null) {
            chowder.checkTransactionStatus(PAYBILL_NUMBER, transactionId);


        } else {
            Toast.makeText(getApplicationContext(), "No previous transaction available", Toast.LENGTH_SHORT).show();
        }
    }

    private void accessApp() {
        //SharedPreferences sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        //We saved the last transaction id to Shared Preferences
        //String transactionStatus = sp.getString("transactionStatus", null);

        //Call chowder.checkTransactionStatus to check a transaction
        //Check last transaction
        sharedPreference = new SharedPreference();
        String transactionStatus;



        //Retrieve a value from SharedPreference
        Activity context = this;
        transactionStatus = sharedPreference.getValue(context);




        if (transactionStatus !=null) {
            //chowder.checkTransactionStatus(PAYBILL_NUMBER, transactionStatus);
            Intent n = new Intent(Premium.this, MainActivity.class);
            startActivity(n);

        } else {
            Toast.makeText(getApplicationContext(), "You haven't made payment yet.No transaction data available", Toast.LENGTH_LONG).show();
            attempt_counter--;
            attempts.setText(Integer.toString(attempt_counter));
            if(attempt_counter == 0){
                bAccess.setEnabled(false);
                android.app.AlertDialog.Builder a_builder = new android.app.AlertDialog.Builder(Premium.this);
                a_builder.setMessage("Have you made your payment?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                android.app.AlertDialog.Builder a_builder = new android.app.AlertDialog.Builder(Premium.this);
                                a_builder.setMessage("Ensure your internet connection is on,then click on the confirm payment button\n" +
                                        "If your last transaction is shown on the screen ,restart app and login again\n" +
                                        "if you dont get any message call:0729314341 for further assistance").setCancelable(false)

                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();




                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                android.app.AlertDialog.Builder a_builder = new android.app.AlertDialog.Builder(Premium.this);
                                a_builder.setMessage("Ensure your internet connection is on,then  enter your phone number on the space provided\n" +
                                        "click on the pay button..it is a mpesa payment method\n" +
                                        "if your payment is successful ,restart app and press the login button").setCancelable(false)

                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        });

                android.app.AlertDialog alert = a_builder.create();
                alert.setTitle("Login Assistant");
                alert.show();
            }
        }
    }



public  void RegSer(){

    oid = "12345";
    rid = PAYBILL_NUMBER;
    uid = etPhoneNumber.getText().toString();
    add = "Loan Repayment";
    od = "9th April 2019";
    ba = etLAmount.getText().toString();

    new PostDataTOServer().execute();
}

    @Override
    public void setMode(int mode) {

    }

    @Override
    public void setTabs(List<String> tabList, TabLayout.OnTabSelectedListener onTabSelectedListener) {

    }

    @Override
    public void setFAB(int drawableId, View.OnClickListener onClickListener) {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setPager(ViewPager vp, TabLayout.ViewPagerOnTabSelectedListener viewPagerOnTabSelectedListener) {

    }

    @Override
    public void setExpensesSummary(int dateMode) {

    }

    @Override
    public ActionMode setActionMode(ActionMode.Callback actionModeCallback) {
        return null;
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

                String link = path;
                String data = URLEncoder.encode("orderid", "UTF-8") + "=" +
                        URLEncoder.encode(oid, "UTF-8");
                data += "&" + URLEncoder.encode("restid", "UTF-8") + "=" +
                        URLEncoder.encode(rid, "UTF-8");
                data += "&" + URLEncoder.encode("uid", "UTF-8") + "=" +
                        URLEncoder.encode(uid, "UTF-8");
                data += "&" + URLEncoder.encode("add", "UTF-8") + "=" +
                        URLEncoder.encode(add, "UTF-8");
                data += "&" + URLEncoder.encode("orderdetail", "UTF-8") + "=" +
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
                String s = new String("Exception: " + e.getMessage());
                System.out.print(s);
                return null;

            }
        }





        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (success == 1) {
                Toast.makeText(getApplicationContext(), "Payment  successfully..!", Toast.LENGTH_LONG).show();
            }
        }

    }




    @Override
    public void onPaymentReady(String returnCode, String processDescription, String merchantTransactionId, String transactionId) {
        //The user is now waiting to enter their PIN
        //You can use the transaction id to confirm payment to make sure you store the ids somewhere if you want the user to be able to check later
        //Save the transaction ID
        SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        sp.edit().putString("chowderTransactionId", transactionId).apply();

        new AlertDialog.Builder(Premium.this)

                .setTitle("Payment in progress")
                .setMessage("Please wait for a pop up from Safaricom and enter your Bonga PIN")
                .setIcon(android.R.drawable.sym_contact_card)
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Well you can skip the dialog if you want, but it will make the user feel safer, they'll know what's going on instead of sitting there
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onPaymentSuccess(String merchantId, String msisdn, String amount, String mpesaTransactionDate, String mpesaTransactionId, String transactionStatus, String returnCode, String processDescription, String merchantTransactionId, String encParams, String transactionId) {
        //The payment was successful.
        sharedPreference = new SharedPreference();
        Activity context = this;
        sharedPreference.save(context, transactionStatus);
        new AlertDialog.Builder(Premium.this)
                .setTitle("Payment confirmed")
                .setMessage(transactionStatus + ". Your amount of Ksh." + amount + " has been successfully paid from " + msisdn + " to PayBill number " + merchantId + " with the M-Pesa transaction code " + mpesaTransactionId + " on " + mpesaTransactionDate + ".\n\n iSing App\n\n Thank you for using the service\n iSing getting you Entertain")
                .setIcon(android.R.drawable.sym_contact_card)
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //Well you can skip the dialog if you want, but it might make the user feel safer
                        //The user has successfully paid so give them their goodies
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onPaymentFailure(String merchantId, String msisdn, String amount, String transactionStatus, String processDescription) {
        //The payment failed.
        new AlertDialog.Builder(Premium.this)
                .setTitle("Payment failed")
                .setMessage(transactionStatus + ". Your amount of Ksh." + amount + " was not paid from " + msisdn + " to PayBill number " + merchantId + ". Please try again.")
                .setIcon(android.R.drawable.sym_contact_card)
                .setCancelable(false)
                .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String amount = etLAmount.getText().toString().trim();
                        String phoneNumber = etPhoneNumber.getText().toString().trim();
                        //Your product's ID must have 13 digits
                        String productId = "sacco loan";

                        chowder.processPayment(amount, phoneNumber.replaceAll("\\+", ""), productId);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Well you can skip the dialog if you want, but it might make the user feel safer
                //The user has successfully paid so give them their goodies
                dialog.dismiss();
            }
        }).show();
    }
   /* private void accessMainApp() {
        prefManager.setFirstTimeLaunch(false);
        SharedPreferences sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        //We saved the last transaction id to Shared Preferences
        String transactionId = sp.getString("chowderTransactionId", null);

        //Call chowder.checkTransactionStatus to check a transaction
        //Check last transaction
        if (transactionId != null) {
            chowder.checkTransactionStatus(PAYBILL_NUMBER, transactionId);
            Intent i = new Intent(Premium.this, MainActivity.class);

            startActivity(i);

        } else {

            Intent j= new Intent("com.safariappreneurs.kikuyuapp.PAYMENT");
            startActivity(j);

        }
    }*/

    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                if(netInfos.isConnected())
                    return true;
        }
        return false;
    }


}

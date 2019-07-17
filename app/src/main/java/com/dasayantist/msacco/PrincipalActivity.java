package com.dasayantist.msacco;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dasayantist.msacco.adapters.LoanListAdapter;
import com.dasayantist.msacco.app.AppController;
import com.dasayantist.msacco.model.Loan;
import com.dasayantist.msacco.onboard.OnBoardingActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PrincipalActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    //json object response url

    private static String TAG = MainActivity.class.getSimpleName();
    ListView list;
    LoanListAdapter adapter;
    ArrayList<Loan> data;
    FirebaseAuth auth;
    String queryP = " ";
    String queryPhone = "0710947709";
    public String urlForJsonObject = AppController.baseUrl + "get_all_products.php?username=" + queryPhone;
    private ProgressDialog pDialog;
    private ListView productsList;
    private TextView noConnection;
    private TextView viewDescription;
    private SwipeRefreshLayout swipeLayout;
    //temporary string to show the parsed response
    private String jsonResponse;
    // public String urlForInterestJsonObject = AppController.baseUrl + "get_interest.php?loantypeid=";
    private     SharedPref               sharedPreferenceObj; // Declare Global

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);


        sharedPreferenceObj=new SharedPref(PrincipalActivity.this);
        if(sharedPreferenceObj.getApp_runFirst().equals("FIRST"))
        {
            // That's mean First Time Launch
            // After your Work , SET Status NO
            sharedPreferenceObj.setApp_runFirst("NO");

            Intent n = new Intent(PrincipalActivity.this, OnBoardingActivity.class);
            startActivity(n);
        }
//        else
//        {
//            // App is not First Time Launch
//        }


        productsList = findViewById(R.id.listviewallproducts);
        noConnection = findViewById(R.id.no_connection);
        viewDescription = findViewById(R.id.view_description);
        //swipeLayout = findViewById(R.id.swipe_layout);
        // swipeLayout.setOnRefreshListener(this);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait....");
        pDialog.setCancelable(false);
        auth = FirebaseAuth.getInstance();
        TextView txtstat = findViewById(R.id.txt_status);

//     String main_data[] = {"data1", "is_primary", "data3", "data2", "data1", "is_primary", "photo_uri", "mimetype"};
//     Object object = getContentResolver().query(Uri.withAppendedPath(android.provider.ContactsContract.Profile.CONTENT_URI, "data"),
//             main_data, "mimetype=?",
//             new String[]{"vnd.android.cursor.item/phone_v2"},
//             "is_primary DESC");
//     if (object != null) {
//         while (((Cursor) (object)).moveToNext()) {
//             // This is the phoneNumber
//             queryP = ((Cursor) (object)).getString(4);
//             txtstat.setText(queryP);
//         }
//         ((Cursor) (object)).close();
//     }


        if (checkForConnection()) {
            //viewDescription.setVisibility(View.VISIBLE);
            //makeJsonObjectRequest();
            loanJson();
        } else {
            //viewDescription.setVisibility(View.GONE);
            noConnection.setVisibility(View.VISIBLE);
        }


    }

    private void loanJson() {
        list = findViewById(R.id.listviewallproducts);
        data = new ArrayList<>();
        adapter = new LoanListAdapter(this, data);
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
                            if (success == 1) {
                                JSONArray products = response.getJSONArray("products");
                                for (int i = 0; i < products.length(); i++) {
                                    JSONObject phone = products.getJSONObject(i);
                                    if (!JSONObject.NULL.equals(phone)) {
                                        String loan_type = phone.get("loantypename").toString();
                                        String loan_id = phone.get("loanno").toString();
                                        String loan_amount = phone.get("loanamount").toString();
                                        String principal = phone.get("loanbalance").toString();
                                        String interest = phone.get("interest").toString();
                                        String s_time = phone.get("s_time").toString();
                                        String penalty = phone.get("penalty").toString();
                                        //loantypename loanamount loanno interest s_time penalty loanbalance

                                        Loan loan = new Loan(loan_type, loan_id, loan_amount, principal, interest, s_time, penalty);
                                        data.add(loan);
                                    }
                                }
                                list.setAdapter(adapter);
                            } else {
                                Toast.makeText(PrincipalActivity.this, "No products in the database", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            // Toast.makeText(PrincipalActivity.this, "No products in the database"  + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        hidepDialog();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
                Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
        //adding request to request queue
        AppController.getmInstance().addToRequestQueue(jsonObjReq);


    }
//    private void interestJson() {
//        JsonObjectRequest jsonObjRe = new JsonObjectRequest(Request.Method.GET,
//                urlForInterestJsonObject ,
//                null,
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG, response.toString()); //for android log cat??
//
//                        try {
//                            int success = response.getInt("success");
//                            if (success == 1){
//                                JSONArray interest = response.getJSONArray("interest");
//                                for(int i =0; i<interest.length(); i++){
//                                    JSONObject pinterest = interest.getJSONObject(i);
//                                    if (!JSONObject.NULL.equals(pinterest)) {
//                                        String ainterest = pinterest.get("ainterest").toString();
////
//                                    }
//                                }
//                            }
//                            else {
//                                Toast.makeText(PrincipalActivity.this, "No products in the database", Toast.LENGTH_LONG).show();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(getApplicationContext(),
//                                    "Error: " + e.getMessage(),
//                                    Toast.LENGTH_LONG).show();
//                            // Toast.makeText(PrincipalActivity.this, "No products in the database"  + e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//
//                        hidepDialog();
//
//                    }
//
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                VolleyLog.d(TAG,"Error: "+ volleyError.getMessage() );
//                Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
//                hidepDialog();
//            }
//        });
//        //adding request to request queue
//        AppController.getmInstance().addToRequestQueue(jsonObjRe);
//
//
//    }

    private void showpDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    private void hidepDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    public void onRefresh() {
        if (checkForConnection()) {
            viewDescription.setVisibility(View.VISIBLE);
            noConnection.setVisibility(View.GONE);
            loanJson();
            swipeLayout.setRefreshing(false);

        } else {
            productsList.setVisibility(View.GONE);
            viewDescription.setVisibility(View.GONE);
            noConnection.setVisibility(View.VISIBLE);
            swipeLayout.setRefreshing(false);

        }
    }

    @Override
    public void onDestroy() {
        // You must call this or the ad adapter may cause a memory leak
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            startActivity(new Intent(this, DBActivity.class));
//            return true;
//        } else
//            if (id == R.id.action_help) {
//            startActivity(new Intent(this, MainActivity.class));
//            return true;
//        } else
        if (id == R.id.action_logout) {
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            fAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean checkForConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
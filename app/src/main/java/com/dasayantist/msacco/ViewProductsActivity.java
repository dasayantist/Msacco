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
import android.widget.ArrayAdapter;
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
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ViewProductsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    //json object response url
    String pne = "0710947709";
    private String urlForJsonObject = AppController.baseUrl + "get_all_products.php?username=" + pne;

    private static String TAG = MainActivity.class.getSimpleName();

    //progress dialog
    private ProgressDialog pDialog;

    private ListView productsList;
    private TextView noConnection;
    private TextView viewDescription;
    private SwipeRefreshLayout swipeLayout;

    //temporary string to show the parsed response
    private String jsonResponse;

    ListView list;
    LoanListAdapter adapter;
    ArrayList<Loan> data;
    ProgressDialog progressDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);

        productsList = findViewById(R.id.listview_all_products);
        noConnection = findViewById(R.id.no_connection);
        viewDescription = findViewById(R.id.view_description);
        swipeLayout = findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait....");
        pDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() == null) {
//            startActivity(new Intent(ViewProductsActivity.this, LoginActivity.class));
//            finish();
//        }


        if (checkForConnection()){
            viewDescription.setVisibility(View.VISIBLE);
            //makeJsonObjectRequest();
            loanJson();
        }
        else{
            viewDescription.setVisibility(View.GONE);
            noConnection.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
//                                    jsonResponse += "Interest: " + description + "\n\n";
                                    productsArrayList.add(jsonResponse);

                                }

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        ViewProductsActivity.this,
                                        android.R.layout.simple_list_item_1,
                                        productsArrayList );

                                productsList.setAdapter(arrayAdapter);
                            }
                            else {
                                Toast.makeText(ViewProductsActivity.this, "No products in the database", Toast.LENGTH_LONG).show();
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

private void loanJson(){
    list = findViewById(R.id.listview_all_products);
    data=new ArrayList<>();
    adapter=new LoanListAdapter(this,data);

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
                                //for (int i = 0; i<wordsInJSONArray.length(); i++) {
//                                    Object o = products.get(i);
                                JSONObject phone = products.getJSONObject(i);
                                    if (!JSONObject.NULL.equals(phone)) {


                               // }



                               // new Gson().fromJson(jsonString, MyObject.class)
                                String name = phone.get("name").toString();
                                String price = phone.get("price").toString();
                                String description = phone.get("description").toString();

                                //Map<String, Object> map = (Map<String, Object>) phone;
                                //Loan loan = new Loan(map.get(name).toString(), map.get(name).toString(), map.get(price).toString(), map.get(description).toString());

                                        Loan loan = new Loan(name, price, name, price, price, name, price);
//                                jsonResponse = "";
//                                jsonResponse += "Loan ID: " + name + "\n\n";
//                                jsonResponse += "Principal: " + price + "\n\n";
//                                    jsonResponse += "Interest: " + description + "\n\n";
                                data.add(loan);
                                    }
                            }

//                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                                    ViewProductsActivity.this,
//                                    android.R.layout.simple_list_item_1,
//                                    LoanListAdapter );

                            list.setAdapter(adapter);
                        }
                        else {
                            Toast.makeText(ViewProductsActivity.this, "No products in the database", Toast.LENGTH_LONG).show();
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

    @Override
    public void onRefresh() {
        if (checkForConnection()){
            viewDescription.setVisibility(View.VISIBLE);
            noConnection.setVisibility(View.GONE);
            makeJsonObjectRequest();
            swipeLayout.setRefreshing(false);

        }
        else{
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

    private Boolean checkForConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

package com.b4group.busmembershipdriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText userEmail,userPassword;
    Button sign_in_button;
    ProgressBar progressBar;
    TextView clickToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.title_activity_login);

        userEmail = (EditText) findViewById(R.id.driverEmail);
        userPassword = (EditText) findViewById(R.id.driverPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        clickToRegister = (TextView) findViewById(R.id.clickToRegister);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        sign_in_button = (Button) findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                sign_in_button.setEnabled(false);
                api_call();

            }

        });

        clickToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
    }

    public String api_call(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String controller_name="driver/login";
        //String api_string = pull == 1 ? "pull_coordinates" : "push_coordinates";
//        api_string += "?bus_id=" + bus_id;
//        api_string += "&x=" + latitude;
//        api_string += "&y=" + longitude;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.base_url+controller_name,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //textView.setText("Response is: "+ response.substring(0,500));

                        Log.i("Json Response",response.toString());
                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        try {
//                            JSONArray jArray = new JSONArray(response);
//                            for (int i=0;i<jArray.length();i++){
                            JSONObject obj = new JSONObject(response);
//                              globalLatLng = new LatLng(obj.getDouble("x"),obj.getDouble("y"));
                            Log.i("Json Response",obj.toString());
                            Log.i("Extract", obj.getString("result"));
                            if(obj.getString("result").equals("Success")){
                                Intent myIntent = new Intent(getBaseContext(), MapsActivity.class);
                                startActivityForResult(myIntent, 0);
                            }
//                            }
                        } catch (JSONException e) {
                            Log.e("Json Response",e.toString());
                        }
                        progressBar.setVisibility(View.GONE);
                        sign_in_button.setEnabled(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");
                Log.e("API Response", "Error in API Request" + error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                sign_in_button.setEnabled(true);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("email", userEmail.getText().toString());
                params.put("password", userPassword.getText().toString());
                params.put("api", "1");
                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return "";
    }

}

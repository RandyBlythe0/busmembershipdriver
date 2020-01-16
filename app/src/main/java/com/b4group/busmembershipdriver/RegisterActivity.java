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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText driverName,driverEmail,driverPassword,driverMobile,driverAddress;
    Button register_button;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle(R.string.title_activity_register);

        driverName =(EditText) findViewById(R.id.driverName);
        driverEmail = (EditText) findViewById(R.id.driverEmail);
        driverPassword =(EditText) findViewById(R.id.driverPassword);
        driverMobile =(EditText) findViewById(R.id.driverMobile);
        driverAddress =(EditText) findViewById(R.id.driverAddress);

        register_button = (Button) findViewById(R.id.register_button);
        progressBar =(ProgressBar) findViewById(R.id.progressBar1);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                api_call();
            }
        });

    }

    public String api_call(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String controller_name="driver/register";
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
                        progressBar.setVisibility(View.GONE);
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
                                Intent myIntent = new Intent(getBaseContext(), LoginActivity.class);
                                startActivityForResult(myIntent, 0);
                            }
//                            }
                        } catch (JSONException e) {
                            Log.e("Json Response",e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");
                Log.e("API Response", "Error in API Request" + error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", driverEmail.getText().toString());
                params.put("email", driverPassword.getText().toString());
                params.put("mobile", driverMobile.getText().toString());
                params.put("address", driverAddress.getText().toString());
                params.put("api", "1");
                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return "";
    }

}

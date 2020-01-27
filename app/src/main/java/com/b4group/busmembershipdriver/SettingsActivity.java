package com.b4group.busmembershipdriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements OnItemSelectedListener{

    Spinner optionSetBusSpinner;
    List<String> spinnerArray;
    HashMap<String, Integer> spinnerHashMap;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //map views by id
        optionSetBusSpinner = (Spinner) findViewById(R.id.optionSetBusSpinner);


        spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Select One");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        optionSetBusSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerHashMap = new HashMap<String, Integer>();
        spinnerHashMap.put("Select One", 0);

//        optionSetBusSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String msg = (String) parent.getItemAtPosition(position);
//
//                Toast.makeText(getApplicationContext(), msg ,Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        optionSetBusSpinner.setOnItemSelectedListener(this);

//        optionSetBusSpinner = (Spinner) findViewById(R.id.optionSetBusSpinner);
//        optionSetBusSpinner.setAdapter(optionList);
        //optionList.insert("Select One",0);
        listBuses();

        sharedPreferences = getApplicationContext().getSharedPreferences("busmembershipdriver", 0);
        sharedPreferencesEditor = sharedPreferences.edit();

        if(sharedPreferences.contains("BusPassingNumber")){
            optionSetBusSpinner.setSelection(getIndex(optionSetBusSpinner, sharedPreferences.getString("BusPassingNumber","N/A")));
            Log.i("PrefFound",sharedPreferences.getString("BusPassingNumber","N/A"));
        }
    }

private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    public String listBuses(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String controller_name="bus/listBuses";
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
                        //progressBar.setVisibility(View.GONE);
                        Log.i("Json Response",response.toString());
                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        try {
//                            JSONArray jArray = new JSONArray(response);
//                            for (int i=0;i<jArray.length();i++){
                            JSONObject obj = new JSONObject(response);
//                              globalLatLng = new LatLng(obj.getDouble("x"),obj.getDouble("y"));
                            Log.i("Json Try Parse Response",obj.toString());
                            Log.i("Extract", obj.getJSONArray("buses").toString());

                            JSONArray busArray = new JSONArray();
                            busArray = obj.getJSONArray("buses");
                            for (int i=0;i<busArray.length();i++){
                                JSONObject bus = busArray.getJSONObject(i);
                                spinnerArray.add(bus.getString("passing_number"));
                                spinnerHashMap.put(bus.getString("passing_number"),bus.getInt("id"));
                            }


//                            }
                        } catch (JSONException e) {
                            Log.e("Json Exception",e.toString());
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

                params.put("api", "1");
                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return "";
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String msg = (String) parent.getItemAtPosition(position);
        sharedPreferencesEditor.putString("BusPassingNumber",msg);
        sharedPreferencesEditor.putInt("BusId",spinnerHashMap.get((String) parent.getItemAtPosition(position)));
        sharedPreferencesEditor.commit();
        Log.i("SharedPreferences",sharedPreferences.getString("BusPassingNumber","N/A"));
        Log.i("SharedPreferences",""+sharedPreferences.getInt("BusId",0));
        //Log.i("SharedPreferences",sharedPreferences.getAll().toString());
        msg += "is At";
        msg += spinnerHashMap.get((String) parent.getItemAtPosition(position));
        Log.i("Spinner Selection",  msg);

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.i("SharedPreferences", entry.getKey() + ": " + entry.getValue().toString());
        }

        //((MapsActivity)this.getApplicationContext()).setBusId(spinnerHashMap.get((String) parent.getItemAtPosition(position)));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

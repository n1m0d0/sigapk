package com.example.sigapk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Institution extends AppCompatActivity {

    EditText etBusinessName, etTradeName, etNit, etEmail, etPassword;
    Spinner spSociety;
    Button btnRegister;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    ArrayList<ObjectSociety> societies = new ArrayList<ObjectSociety>();
    ArrayList<String> names = new ArrayList<String>();
    ProgressDialog progressDialog;
    int society_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution);

        etBusinessName = findViewById(R.id.etBusinessName);
        etTradeName = findViewById(R.id.etTradeName);
        etNit = findViewById(R.id.etNit);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spSociety = findViewById(R.id.spSociety);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setEnabled(false);

        callRequest();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            btnRegister.setEnabled(true);
        } else {
            // No hay conexión a Internet en este momento
            Toast.makeText(getApplicationContext(), "No se cuenta con conexion a Internet", Toast.LENGTH_LONG).show();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etBusinessName.getText().toString().trim().equals("") ||
                        etTradeName.getText().toString().trim().equals("") ||
                        etNit.getText().toString().trim().equals("") ||
                        etEmail.getText().toString().trim().equals("") ||
                        etPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Debe completar todos los datos", Toast.LENGTH_LONG).show();
                } else {
                    if(!validateEmail(etEmail.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "No es un correo electronico", Toast.LENGTH_LONG).show();
                    } else {
                        JSONObject petition = new JSONObject();
                        try {
                            petition.put("razonSocial", etBusinessName.getText().toString().trim());
                            petition.put("nombreComercial", etTradeName.getText().toString().trim());
                            petition.put("society", society_id);
                            petition.put("nit", etNit.getText().toString().trim());
                            petition.put("email", etEmail.getText().toString().trim());
                            petition.put("password", etPassword.getText().toString().trim());
                            Log.w("json", petition.toString());
                            sendRequest(petition);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        spSociety.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                society_id = societies.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void sendRequest(JSONObject petition) {
        String url = "http://192.168.100.20/api/registro-empresa";
        requestQueue = Volley.newRequestQueue(this);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, petition, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.w("response", response.toString());
                try {
                    if (response.getInt("code") == 200) {
                        Toast.makeText(Institution.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        Uri uri = Uri.parse("http://192.168.100.20");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        finish();
                    } else {
                        if (response.getInt("code") == 400) {
                            Toast.makeText(Institution.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Institution.this, "Problemas en la conexion", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("error", error);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void callRequest() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();
        String url = "http://192.168.100.20/api/sociedades";
        requestQueue = Volley.newRequestQueue(this);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.w("response", response.toString());
                try {
                    if (response.getInt("code") == 200) {
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++){
                            JSONObject society = data.getJSONObject(i);
                            Log.w("society", society.toString());
                            int id = society.getInt("id");
                            Log.w("id", "" + id);
                            String name = society.getString("nombre");
                            societies.add(new ObjectSociety(id, name));
                            names.add(name);
                        }
                        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, names);
                        spSociety.setAdapter(adapter);*/

                        AdapterSociety adapterSociety = new AdapterSociety(getApplicationContext(), societies);
                        spSociety.setAdapter(adapterSociety);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Institution.this, "Problemas en la conexion", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("error", error);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private boolean validateEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
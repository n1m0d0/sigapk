package com.example.sigapk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class Person extends AppCompatActivity {

    EditText etName, etPaternal, etMaternal, etEmail, etPassword;
    EditText etCaptcha;
    Button btnRegister;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        etName = findViewById(R.id.etName);
        etPaternal = findViewById(R.id.etPaternal);
        etMaternal = findViewById(R.id.etMaternal);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setEnabled(false);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            btnRegister.setEnabled(true);
        } else {
            // No hay conexión a Internet en este momento
            Toast.makeText(Person.this, "No se cuenta con conexion a Internet", Toast.LENGTH_LONG).show();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etName.getText().toString().trim().equals("") || etPaternal.getText().toString().trim().equals("") || etMaternal.getText().toString().trim().equals("") || etEmail.getText().toString().trim().equals("") || etPassword.getText().toString().trim().equals("")){

                    Toast.makeText(Person.this, "Debe completar todos los datos", Toast.LENGTH_LONG).show();

                } else {

                    if (!validateEmail(etEmail.getText().toString().trim())) {

                        Toast.makeText(Person.this, "No es un correo electronico", Toast.LENGTH_LONG).show();

                    } else {
                        JSONObject petition = new JSONObject();
                        try {
                            petition.put("nombres", etName.getText().toString().trim());
                            petition.put("paterno", etPaternal.getText().toString().trim());
                            petition.put("materno", etMaternal.getText().toString().trim());
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
    }

    private boolean validateEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void sendRequest(JSONObject petition) {
        String url = "http://192.168.100.20/api/registro-persona";
        requestQueue = Volley.newRequestQueue(this);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, petition, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.w("response", response.toString());
                try {
                    if (response.getInt("code") == 200) {
                        Toast.makeText(Person.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        Uri uri = Uri.parse("http://192.168.100.20");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        finish();
                    } else {
                        if (response.getInt("code") == 400) {
                            Toast.makeText(Person.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Person.this, "Problemas en la conexion", Toast.LENGTH_LONG).show();
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
}
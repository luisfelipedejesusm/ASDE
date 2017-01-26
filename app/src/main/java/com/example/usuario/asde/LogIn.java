package com.example.usuario.asde;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity{

    public static final String LOGIN_URL = "http://199.89.55.4/ASDE/api/v1/operador/login";


    public static final String KEY_CORREO = "correo";
    public static final String KEY_PASSWORD = "clave";

    private EditText editTextCorreo;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegistro;
    private Switch switch_Recordar;

    private String correo;
    private String clave;
    private String id; // Valor de respuesta del servidor utilizado para validar el login


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        textViewRegistro = (TextView)findViewById(R.id.txtViewRegistrarse); // Para registro

        editTextCorreo = (EditText) findViewById(R.id.editTextCorreo);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (Button) findViewById(R.id.buttonLogin); // Para login

        switch_Recordar = (Switch)findViewById(R.id.switchRecordar);


/*
        switch_Recordar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                setEstadoSwitch(isChecked);

            }
        });


    }


    void setEstadoSwitch(boolean x){

        if(x){
            editTextCorreo.setText(null);
            editTextPassword.setText(null);

    }

*/



        //Click en el textview registro que envia a la aqctividad registroBD

        textViewRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, registro.class);
                startActivity(intent);
            }
        });




        //Boton que valida el login y envia a la activity  openProfile();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                correo = editTextCorreo.getText().toString().trim();
                clave = editTextPassword.getText().toString().trim();

                if(TextUtils.isEmpty(correo)||TextUtils.isEmpty(clave)){

                    Toast.makeText(LogIn.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }else {

                    userLogin(); //Ir a Login
                }
            }
        });
    }


    private void userLogin() {

        // correo = editTextCorreo.getText().toString().trim();
        // clave = editTextPassword.getText().toString().trim();


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject j = new JSONObject(response);
                                        JSONArray arrayJson = j.getJSONArray("posts");
                                        JSONObject reader = arrayJson.getJSONObject(0);
                                        id = reader.getString("id");

                                        Log.v("ID", id);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (true) {
                                        //id != "not found"
                                        //!TextUtils.equals(id,"not found")
                                        Log.v("Response:", response);
                                        getPrincipal();

                                    } else {

                                        Toast.makeText(LogIn.this, "Error en Usuario o Password" + response, Toast.LENGTH_LONG).show();
                                    }


                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //Toast.makeText(login.this, error.toString(), Toast.LENGTH_LONG).show();
                                    Toast.makeText(LogIn.this, "Tiempo para conexión finalizado, revise su conexion a internet" , Toast.LENGTH_LONG).show();

                                }
                            }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> map = new HashMap<String, String>();
                            map.put(KEY_CORREO, correo);
                            map.put(KEY_PASSWORD, clave);
                            return map;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);

                } catch (Exception e) {
                    AlertDialog.Builder builderDos = new AlertDialog.Builder(LogIn.this);
                    builderDos.setMessage("Por favor, revise su conexión a internet")
                            .setNegativeButton("Intente de nuevo", null)
                            .create()
                            .show();
                }


            }
        }).start();


    }// Cierre de Clase User Login

    private void getPrincipal() {
        Intent intent = new Intent(LogIn.this, principal.class);
        //intent.putExtra(KEY_USERNAME, username);
        startActivity(intent);
    }

}
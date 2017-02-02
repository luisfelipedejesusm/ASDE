package com.example.usuario.asde;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.usuario.asde.modelo.Eventos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class myEvents extends AppCompatActivity {


    ListView listViewGeneral;
    clientesAdapter itemsAdapter;
    ArrayList<Eventos> eventos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        listViewGeneral = (ListView)findViewById(R.id.list_view_my_events);
        getMyEvents();


    }
// Getting the request result and passing it to itemsAdapter
    private void setArrayToList( ArrayList<Eventos> e){
        eventos = e;
        itemsAdapter = new clientesAdapter(this,0,eventos);
        listViewGeneral.setAdapter(itemsAdapter);
        listViewGeneral.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Eventos evento = (Eventos) arg0.getItemAtPosition(position);

                Intent intent = new Intent(myEvents.this,MyEventDetails.class);
                intent.putExtra("EventID",evento.getId());
                startActivity(intent);
            }
        });
    }
//method to get the filename
    private String getFileName(String path){
        int size = path.length();
        String filename = "";
        String chr;
        do {
            size = size - 1;
            chr = String.valueOf(path.charAt(size));
            filename = path.charAt(size) + filename;

        }while (!chr.equals("/"));

        return filename;
    }

    //Requesting all events from logged in user from api
    private void getMyEvents() {


        final String LOGIN_URL = "http://199.89.55.4/ASDE/api/v1/operador/events";

        // correo = editTextCorreo.getText().toString().trim();
//                PersonModel person = (PersonModel) arg0.getItemAtPosition(position);

        RequestQueue requestQueue = Volley.newRequestQueue(this);


                    StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        ArrayList<Eventos> eventos = new ArrayList<Eventos>();
                                        JSONObject j = new JSONObject(response);
                                        JSONArray arrayJson = j.getJSONArray("posts");
                                        for (int i = 0; i<arrayJson.length();i++){
                                            Eventos evento = new Eventos();
                                            JSONObject obj = arrayJson.getJSONObject(i);
                                            evento.setDireccion(obj.getString("direccion"));
                                            evento.setPathFoto(getFileName(obj.getString("image_path")));
                                            evento.setHoraevento(obj.getString("timeCreated"));
                                            evento.setId(obj.getString("id"));
                                            eventos.add(evento);

                                        }

                                        setArrayToList(eventos);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //Toast.makeText(login.this, error.toString(), Toast.LENGTH_LONG).show();
                                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                        Toast.makeText(myEvents.this, "Tiempo para conexion finalizado, revise su conexion a internet",Toast.LENGTH_LONG).show();
                                    } else if (error instanceof ServerError) {
                                        Toast.makeText(myEvents.this, "Error en el servidor, Contactese con el suplidor de su aplicacion",Toast.LENGTH_LONG).show();
                                    } else if (error instanceof NetworkError) {
                                        Toast.makeText(myEvents.this, "Error de coneccion. Revise el estado de su coneccion a internet",Toast.LENGTH_LONG).show();
                                    } else if (error instanceof ParseError) {
                                        Toast.makeText(myEvents.this, "Problemas al ejecutar la aplicacion, Contactese con el suplidor de su aplicacion",Toast.LENGTH_LONG).show();
                                    }

                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            SharedPreferences preferences = getSharedPreferences("CustomPreferences",Context.MODE_PRIVATE);
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("id", preferences.getString("userID",null));
                            //map.put("eventoID","4927");
                            return map;
                        }

                    };


                    requestQueue.add(stringRequest);




}
}

package com.example.usuario.asde;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyEventDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event_details);

        //get clicked event using its id
        getMyEvents();
    }

    //showing the panel for closing events
    public void cerrarEvento(View view){
        LinearLayout cerrarEventoPanel = (LinearLayout) findViewById(R.id.cerrar_eventos_panel);
        cerrarEventoPanel.setVisibility(View.VISIBLE);

        final ScrollView scroll = (ScrollView) findViewById(R.id.scroll_view);

        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
      //
    }

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

//--------------------------------------getting event-----------------------------------------------------
    private void getMyEvents() {
        final String LOGIN_URL = "http://199.89.55.4/ASDE/api/v1/operador/event";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Eventos evento = new Eventos();
                            JSONObject j = new JSONObject(response);
                            JSONArray arrayJson = j.getJSONArray("posts");
                            JSONObject obj = arrayJson.getJSONObject(0);
                            evento.setId(obj.getString("id"));
                            evento.setHoraevento(obj.getString("created_at"));
                            evento.setNombre(obj.getString("nombre"));
                            evento.setDetalle(obj.getString("detalle"));
                            evento.setDireccion(obj.getString("direccion"));
                            evento.setPathFoto(getFileName(obj.getString("image_path")));

                            updateInfo(evento);
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
                            Toast.makeText(MyEventDetails.this, "Tiempo para conexion finalizado, revise su conexion a internet",Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(MyEventDetails.this, "Error en el servidor, Contactese con el suplidor de su aplicacion",Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(MyEventDetails.this, "Error de coneccion. Revise el estado de su coneccion a internet",Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(MyEventDetails.this, "Problemas al ejecutar la aplicacion, Contactese con el suplidor de su aplicacion",Toast.LENGTH_LONG).show();
                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences preferences = getSharedPreferences("CustomPreferences", Context.MODE_PRIVATE);
                String eventoID = getIntent().getStringExtra("EventID");
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", preferences.getString("userID",null));
                map.put("eventoID",eventoID);
                return map;
            }

        };


        requestQueue.add(stringRequest);




    }
//here we take the eent from the other thread and update the textviews in the activity
    private void updateInfo(Eventos evento) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        TextView txtFecha = (TextView) findViewById(R.id.txtfecha);
        TextView txtDescripcion = (TextView) findViewById(R.id.txtdescripcion);
        TextView txtCategoria = (TextView) findViewById(R.id.txtcategoria);
        TextView txtDireccion = (TextView) findViewById(R.id.txtDireccion);
        ImageView imgEvento = (ImageView) findViewById(R.id.foto_evento);

        imageLoader.displayImage("http://199.89.55.4/ASDE/storage/app/"+evento.getPathFoto(), imgEvento);
        txtFecha.setText(evento.getHoraevento());
        txtDescripcion.setText(evento.getDetalle());
        txtCategoria.setText(evento.getNombre());
        txtDireccion.setText(evento.getDireccion());



    }


}

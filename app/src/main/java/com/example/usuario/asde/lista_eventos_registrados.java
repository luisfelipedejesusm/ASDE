package com.example.usuario.asde;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.usuario.asde.modelo.Eventos;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class lista_eventos_registrados extends AppCompatActivity {

    ArrayList<Eventos> arrayEvento = new ArrayList<Eventos>();
    ListView listViewGeneral;
    clientesAdapter itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos_registrados);

        Bundle contenedor = this.getIntent().getExtras();
        Gson gson = new Gson();
        String JsonArray = contenedor.getString("lista");
        Type type = new TypeToken<ArrayList<Eventos>>(){}.getType(); //Esta linea de codigo le asigna el tipo de dato nuevamente a ArrayList<Cliente>
        arrayEvento = gson.fromJson(JsonArray,type); // Aqui le damos el tipo de dato



        listViewGeneral = (ListView)findViewById(R.id.lista_general_eventos_registrados);
        itemsAdapter = new clientesAdapter(this,0,arrayEvento);
        listViewGeneral.setAdapter(itemsAdapter);



    }




}

package com.example.usuario.asde;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.usuario.asde.modelo.Eventos;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by User-abreu on 18/01/2017.
 */

public class clientesAdapter extends ArrayAdapter<Eventos> {

    public clientesAdapter(Activity context, int resource, List<Eventos> objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_evento, parent,false);
        }


        // Get the {@link AndroidFlavor} object located at this position in the list
        Eventos CurrentCliente = getItem(position);


        // Se obtiene la direccion del evento y se asigna a la lista
        TextView direccionEvento = (TextView) listItemView.findViewById(R.id.txtDireccion);
        direccionEvento.setText(CurrentCliente.getDireccion());

        // Se obtiene la hora del evento
        TextView horaEvento = (TextView) listItemView.findViewById(R.id.txtHora);
        horaEvento.setText(CurrentCliente.getHoraevento());


        ImageView imagenEvento = (ImageView) listItemView.findViewById(R.id.imageViewlistEvento);

        String filepath = CurrentCliente.getPathFoto();
        File imgFile = new  File(filepath);
        if(imgFile.exists()){

            imagenEvento.setImageURI(Uri.fromFile(imgFile));

        }else{
            //instace of imageloader
            ImageLoader imageLoader = ImageLoader.getInstance();
            // with this method I get the image from the server and place it into my ImageView
            imageLoader.displayImage("http://199.89.55.4/ASDE/storage/app/"+CurrentCliente.getPathFoto(),imagenEvento );

        }



        //Esto funciona
        //Bitmap bmImg = BitmapFactory.decodeFile(CurrentCliente.getPathFoto());
        //imagenEvento.setImageBitmap(bmImg);


        return listItemView;
    }

}

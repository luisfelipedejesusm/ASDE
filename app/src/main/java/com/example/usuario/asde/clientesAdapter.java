package com.example.usuario.asde;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.usuario.asde.modelo.Eventos;

import java.io.File;
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


        TextView  textCategoria = (TextView) listItemView.findViewById(R.id.txtCategoria);
        textCategoria.setText("Categoría: " + CurrentCliente.getCategoria());


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
            //ImageLoader imageLoader = ImageLoader.getInstance();
            // with this method I get the image from the server and place it into my ImageView
            //imageLoader.displayImage("http://199.89.55.4/ASDE/storage/app/"+CurrentCliente.getPathFoto(),imagenEvento );
            final ProgressBar pb = (ProgressBar) listItemView.findViewById(R.id.progressbar_events);
            Glide.with(getContext())
                    .load("http://199.89.55.4/ASDE/storage/app"+CurrentCliente.getPathFoto())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            pb.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            pb.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imagenEvento);


        }



        //Esto funciona
        //Bitmap bmImg = BitmapFactory.decodeFile(CurrentCliente.getPathFoto());
        //imagenEvento.setImageBitmap(bmImg);


        return listItemView;
    }

}

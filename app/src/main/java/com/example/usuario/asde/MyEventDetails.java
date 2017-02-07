package com.example.usuario.asde;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.usuario.asde.modelo.Eventos;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MyEventDetails extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleApiClient mGoogleApiClient = null;
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final String LOGTAG = "android-localizacion";
    Eventos c_evento;

    ProgressDialog progressDialog;

    String Latitud;
    String Longitud;
    private static String  APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;

    String imagen64; // imagen en formato string64
    String mPath; // direccion de la imagen en el celular
    String fechaFoto;

    ImageView imgfoto;//Imagen foto de cierre del evento

    TextView textFotoCierre; //Aqui se colocara la fecha de captura de la foto de cierre

    Button btnConfirmarCierre;


    public static final String UPDATE_EVENT = "http://199.89.55.4/ASDE/api/v1/operador/updatevent";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event_details);

        imgfoto = (ImageView) findViewById(R.id.img_foto_cerrar);
        textFotoCierre = (TextView) findViewById(R.id.textFechaFotoCierre);
        btnConfirmarCierre = (Button)findViewById(R.id.btnConfirmar);
        btnConfirmarCierre.setEnabled(false);


        //get clicked event using its id
        getMyEvents();



        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }
    } // Cierre de la Calse onCreate


    private void sendingData(){
        progressDialog = ProgressDialog.show(this,"","Cerrando Evento, Espere....", true);

    }
    private void dataRetrived(){
        progressDialog.dismiss();
    }


public void cerrar_evento_request(){
try {
    sendingData();
    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());


    StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_EVENT,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response != null) {
                        dataRetrived();
                        Toast.makeText(MyEventDetails.this, "Evento Cerrado Exitosamente", Toast.LENGTH_SHORT).show();
                        redirectToMenu();
                    }


                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dataRetrived();
                    // Toast.makeText(principal.this, error.toString(), Toast.LENGTH_LONG).show();
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(MyEventDetails.this, "Tiempo para conexión finalizado, revise su conexión a internet", Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(MyEventDetails.this, "Usuario o Contraseña Incorrecta, Revise nuevamente su información", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(MyEventDetails.this, "Error en el servidor, Contacte con el suplidor de su aplicación", Toast.LENGTH_LONG).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(MyEventDetails.this, "Error de conexión. Revise el estado de su conexión a internet", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(MyEventDetails.this, "Problemas al ejecutar la aplicación, Contacte con el suplidor de su aplicación", Toast.LENGTH_LONG).show();
                    }

                }
            }) {

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {

            Map<String, String> map = new HashMap<String, String>();
            map.put("eventoID", c_evento.getId());
            map.put("foto", imagen64);
            return map;
        }
    };


    requestQueue.add(stringRequest);
}catch (Exception e){

   dataRetrived();
}

}





    private void redirectToMenu(){
        onPause();
        Intent intent = new Intent(MyEventDetails.this,principal.class);
        startActivity(intent);
        finish();
    }

    public void cerrarEventoConfirmar(View view){ //Confirmar el Cierre del Evento una vez se tiene la imagen64
        if (imagen64 != null){
            cerrar_evento_request();
        }
    }

    public void cancelarCierre(View view){// Cancelamos la actividad y volvemos a principal (agregar un BuilderDialog)
        onPause();
        Intent i = new Intent(MyEventDetails.this,principal.class);
        startActivity(i);
        finish();


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
                            evento.setCategoria(obj.getString("nombre"));
                            evento.setDetalle(obj.getString("detalle"));
                            evento.setDireccion(obj.getString("direccion"));
                            evento.setLatitud(obj.getString("latitud"));
                            evento.setLongitud(obj.getString("longitud"));
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

    public void showFullImage(View view){
        ImageView imgEvento = (ImageView) findViewById(R.id.foto_evento);
        imgEvento.buildDrawingCache();
        Bitmap bitmap = imgEvento.getDrawingCache();

        Intent intent = new Intent(this,ShowFoto.class);
        intent.putExtra("imgBitmap", bitmap);
        startActivity(intent);

    }

//here we take the eent from the other thread and update the textviews in the activity
    private void updateInfo(Eventos evento) {
        c_evento = evento;
        //ImageLoader imageLoader = ImageLoader.getInstance();
        TextView txtFecha = (TextView) findViewById(R.id.txtfecha);
        TextView txtDescripcion = (TextView) findViewById(R.id.txtdescripcion);
        TextView txtCategoria = (TextView) findViewById(R.id.txtcategoria);
        TextView txtDireccion = (TextView) findViewById(R.id.txtDireccion);
        ImageView imgEvento = (ImageView) findViewById(R.id.foto_evento);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.progress_bar_my_event_detail);

        //imageLoader.displayImage("http://199.89.55.4/ASDE/storage/app/"+evento.getPathFoto(), imgEvento);
        Glide.with(this)
                .load("http://199.89.55.4/ASDE/storage/app"+evento.getPathFoto())
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
                .into(imgEvento);
        txtFecha.setText(evento.getHoraevento());
        txtDescripcion.setText(evento.getDetalle());
        txtCategoria.setText(evento.getCategoria());
        txtDireccion.setText(evento.getDireccion());



    }

    public void tomarFoto(View view){
        Location loc1 = new Location("punto1");
        Location loc2 = new Location("punto2");
        loc1.setLatitude(Double.parseDouble(c_evento.getLatitud()));
        loc1.setLongitude(Double.parseDouble(c_evento.getLongitud()));
        loc2.setLatitude(Double.parseDouble(Latitud));
        loc2.setLongitude(Double.parseDouble(Longitud));
        double distancia_metros = loc1.distanceTo(loc2);
        if (distancia_metros<50){
            openCamara();
        }else{
            Toast.makeText(this,"Fuera de área", Toast.LENGTH_SHORT).show();
        }
    }


    public void openCamara(){

        File file = new File (Environment.getExternalStorageDirectory(),MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if(!isDirectoryCreated){
            isDirectoryCreated = file.mkdirs();
        }

        if(isDirectoryCreated){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            fechaFoto = df.format(c.getTime()).trim(); //Obtenemos fecha de la foto


            textFotoCierre.setText(fechaFoto);


            String imagename = fechaFoto + ".png";
            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY + File.separator + imagename;

            File newfile = new File(mPath);
            Intent intenCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intenCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newfile));
            startActivityForResult(intenCamera,PHOTO_CODE);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode == RESULT_OK && requestCode == PHOTO_CODE){

            MediaScannerConnection.scanFile(this,
                    new String[]{mPath}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned" + path + ":");
                            Log.i("ExternalStorage", "-> Uri" + uri);
                        }
                    });

            //Bitmap bitmap = BitmapFactory.decodeFile(mPath);

            Toast.makeText(this, "Procesando Imagen", Toast.LENGTH_LONG).show();

           Bitmap bitmap = getBitmap(mPath);

            imgfoto.setImageBitmap(bitmap);

           // Bitmap bit = BitmapFactory.decodeFile(mPath);
           // getStringImage(bit);
            new ConvertStringImage().execute(bitmap);


        }
    }


    public Bitmap getBitmap(String path){//Funcion que maneja el tamano o resolucion de una imagen en un Bitmap y la acomoda hasta una dimension manejable en memoria
        Bitmap bitmap = null;
        BitmapFactory.Options options;
        try {
            bitmap = BitmapFactory.decodeFile(path);
            return resizePhoto(bitmap,500,true);
        } catch (OutOfMemoryError e) {
            try {
                options = new BitmapFactory.Options();
                for (options.inSampleSize = 1;options.inSampleSize<=32; options.inSampleSize++){
                    try{
                        bitmap = BitmapFactory.decodeFile(path, options);
                        break;
                    }catch (OutOfMemoryError oom){
                        bitmap = null;
                    }
                }
            } catch(Exception ex) {
                return null;
            }
        }
       // Toast.makeText(this, bitmap.getWidth(), Toast.LENGTH_SHORT).show();
        return resizePhoto(bitmap,500,true);

    }


    /**
     * This method resize the photo
     *
     * @param realImage    the bitmap of image
     * @param maxImageSize the max image size percentage
     * @param filter       the filter
     * @return a bitmap of the photo rezise
     */
    public static Bitmap resizePhoto(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);

        return newBitmap;
    }




    private class ConvertStringImage extends AsyncTask<Bitmap, Void, Boolean> {//Tarea asogcrona que convierte un Bitmap en un String64

        @Override
        protected Boolean doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos); //bm is the bitmap object the 10 is de quality 100 is the maximus
            byte[] b = baos.toByteArray();
            String aux = Base64.encodeToString(b, Base64.DEFAULT);
            imagen64 = aux;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean V) {
            super.onPostExecute(V);

            if(V){
                Toast.makeText(MyEventDetails.this, "Confirme el Cierre", Toast.LENGTH_SHORT).show();
                btnConfirmarCierre.setEnabled(true);
            }
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);

        }else{
            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            updateUI(lastLocation);
        }

    }

    private void updateUI(Location loc) {
        if (loc != null) {
            Latitud = String.valueOf(loc.getLatitude());
            Longitud =  String.valueOf(loc.getLongitude());


        } else {
            Latitud = "18.459892";
            Longitud = "-69.95942";

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
        Toast.makeText(getApplicationContext(),"Se ha interrumpido la conexión con Google Play Services", Toast.LENGTH_SHORT).show();



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
        Toast.makeText(this, "Error grave al conectar con Google Play Services", Toast.LENGTH_SHORT).show();

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permiso concedido

                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                updateUI(lastLocation);

            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.

                Log.e(LOGTAG, "Permiso denegado");
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

package com.example.usuario.asde;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.usuario.asde.auxiliares.Cadena;
import com.example.usuario.asde.auxiliares.Utiles;
import com.example.usuario.asde.modelo.Eventos;
import com.frosquivel.magicalcamera.Functionallities.PermissionGranted;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.Utilities.ConvertSimpleImage;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class principal extends AppCompatActivity implements LocationListener {

    public static final String SEND_DATA_URL = "http://199.89.55.4/ASDE/api/v1/operador/senddata";

    PermissionGranted permissionGranted = new PermissionGranted(this); //Para solicitar el permisos para MagicCamera


    /********Entradas de la Interfaz de la Clase Principal.java***********/

    Button btnEnviar;   // Enviar evento primera vez a webservice
    Button btnRegistro; //

    ImageView imgFoto;
    EditText editNombre;
    EditText editApellido;
    EditText editDetalle;
    EditText editDireccion;

    /***************Valores usados en Magic Camera para la obtencion de la foto******/
    String imagen64; // imagen en formato string64
    String rutaImagen; // direccion de la imagen en el celular
    String fechaFoto, fechaFotoGmt;

    boolean bandera = true;

    private MagicalCamera magicalCamera;
    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 10;

    /*******************Valores Usados en la Localizacion *********************/
    public LocationManager handle;  //Manejador de de localizacion
    private String provider;  // Alojar el proveedor de localizacion



    /**************** VALORES PRINCIPALES DEL MODELO (Localizacion)***********************/

    String Longitud;
    String Latitud;

    String Direccion; // Direccion cuando se obtiene de Geocoder
    Cadena cadDireccion; // Direccion cuando se obtiene del Api de Google

    // API de GOOGLE para Obtener la Direccion
    String URL_API_GEOCODER = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + Latitud + ","+ Longitud + "&sensor=true";

    /*******************VARIABLES PARA EL ENVIO DE EVENTOS AL SERVER *********************/

    Eventos evento;

    /*************************Variable ListView para contener los registros nuevos **********/
    /********************Posiblemente sea sustituido por  la tabla de SQLITE ***************/

    ArrayList<Eventos> listaEvento = new ArrayList<Eventos>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);



        //permission for take photo, it is false if the user check deny
        permissionGranted.checkCameraPermission();
        //for search and write photoss in device internal memory
        //normal or SD memory
        permissionGranted.checkReadExternalPermission();
        permissionGranted.checkWriteExternalPermission();
        //permission for location for use the `photo information device.
        permissionGranted.checkLocationPermission();

        magicalCamera = new MagicalCamera(this,RESIZE_PHOTO_PIXELS_PERCENTAGE, permissionGranted);


        btnEnviar = (Button)findViewById(R.id.buttonEnviar);
        btnRegistro=(Button)findViewById(R.id.buttonRegistros);


        imgFoto = (ImageView)findViewById(R.id.imageFoto);
        editNombre = (EditText)findViewById(R.id.editNombre);
        editApellido = (EditText)findViewById(R.id.editApellido);
        editDetalle = (EditText)findViewById(R.id.editDetalle);
        editDireccion = (EditText)findViewById(R.id.editDireccion);


        IniciarServicio(); // Inicio el servicio de localizacion




        /****************Click para Obtener Foto (Aqui tambien debemos iniciar el servicio de localizacion o antes) *****************/

        imgFoto.setOnClickListener(new View.OnClickListener() { /*Activar con el mismo boton de la CAMARA y Llamada para obtener latiud y longitud y por consiguiente Direccion */
            @Override
            public void onClick(View v) {
                magicalCamera.takePhoto();
                bandera = false;
                muestraPosicionActual();
                editDireccion.setText(cadDireccion.valor);
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() { // Validando formulario
            @Override
            public void onClick(View v) {


                String n = editNombre.getText().toString().trim();
                String a = editApellido.getText().toString().trim();
                String d = editDetalle.getText().toString().trim();

                if(TextUtils.isEmpty(n) || TextUtils.isEmpty(a) || TextUtils.isEmpty(d) || TextUtils.equals(d,null)) {

                    Toast.makeText(principal.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();

                }else if (bandera){

                    Toast.makeText(principal.this,"No puede enviar la misma imagen", Toast.LENGTH_SHORT).show();
                }

                else if (imagen64 != null && rutaImagen != null && cadDireccion.valor != null){ // Validando datos generados a excepcion de Direccion

                    evento = new Eventos(n,a,d,Latitud,Longitud,cadDireccion.valor,imagen64,rutaImagen,fechaFoto,fechaFotoGmt,"1");

                    listaEvento.add(new Eventos (n,a,d,Latitud,Longitud,cadDireccion.valor,rutaImagen,fechaFoto,fechaFotoGmt,"1"));

                    bandera = true;

                    envioEvento(); //Envio del evento al WebService
                }

            }
        });




        /**************************   A CONTINUACION SE IMPLANTA EL LISTVIEW DE LOS EVENTOS GUARDADOS Y SE PASA AL   *****************/
        /*************************************    ACTIVITY QUE LOS MUESTRA (CONTROLA)   ********************************************/


        btnRegistro.setOnClickListener(new View.OnClickListener() { //Enviamos al activity lista_eventos_registrados.class el array list
            @Override
            public void onClick(View v) {
                Intent i = new Intent(principal.this,lista_eventos_registrados.class);

                Gson gson = new Gson();
                String jsonString = gson.toJson(listaEvento);

                i.putExtra("lista",jsonString);
                startActivity(i);
            }
        });









    } // Cierre de la funcion onCreate





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        magicalCamera.resultPhoto(requestCode, resultCode, data);

        imgFoto.setImageBitmap(magicalCamera.getPhoto());

        //Se realiza la transformacion de la imagen a String64

        byte[] myArrayBytes = ConvertSimpleImage.bitmapToBytes(magicalCamera.getPhoto(),MagicalCamera.PNG);

        imagen64 = ConvertSimpleImage.bytesToStringBase64(myArrayBytes);

        // Guardamos la ruta de la imagen en el celular

        // magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(),"asde","Magical Camera Text", MagicalCamera.PNG,true);
        String path = magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(),"asde","Magical Camera Text", MagicalCamera.PNG,true);

        rutaImagen = path;

        String aux;

        String[] splitUno = rutaImagen.split("_");
        aux = splitUno[1];
        splitUno = aux.split("\\.");
        fechaFoto = splitUno[0].trim(); // Obtengo la fecha de la foto en una cadena larga

        //********************************************************************//


        SimpleDateFormat formato = new SimpleDateFormat("yyyyMMddHHmmss");
        // formato.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(splitUno[0].trim());
        }
        catch (ParseException ex)
        {
            System.out.println(ex);        }

        fechaFotoGmt = fechaDate.toString().trim();





        if(path!= null){

            Toast.makeText(getApplicationContext(),
                    "La foto se ha guardado en el celular en esta direccion: " + path,
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Su fotografia no fue guardada con exito",
                    Toast.LENGTH_SHORT).show();
        }



    }


    //Activa permisos para MagicCamera invocados arriba en la clase principal
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        magicalCamera.permissionGrant(requestCode, permissions, grantResults);


    }




    /********************************* INICIO DE FUNCIONES PARA EL CONTROL DE LATITUD Y LONGITUD Y OBTENER DIRECCION************************/




    public void IniciarServicio() {

        Toast.makeText(this,"Ubicacion activada",Toast.LENGTH_LONG).show();

        handle = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria c = new Criteria();

        c.setAccuracy(Criteria.ACCURACY_FINE);
        provider = handle.getBestProvider(c, true);
        //Proveedor.setText("Proveedor: " + provider);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        handle.requestLocationUpdates(provider, 100000, 1, this);

    }

    public void muestraPosicionActual() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = handle.getLastKnownLocation(provider);

        if (location == null) {
            Longitud = "Desconocida";
            Latitud = "Desconocida";
        } else {
            Longitud = String.valueOf(location.getLongitude());
            Latitud = String.valueOf(location.getLatitude());
        }
        //llamamos a muestra direccion
        setDireccionActual(location);
    }




    public void setDireccionActual(Location loc) {



        if (loc != null) {


            if (loc.getLongitude() != 0.0 && loc.getLatitude() != 0.0) {



                try {

                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                    List<Address> list = geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);

                    if (!list.isEmpty()) {
                        Address direccion = list.get(0);
                        Direccion = direccion.getAddressLine(0).toString();
                    }else{
                        DireccionConexionAsyncTask task = new DireccionConexionAsyncTask();
                        task.execute("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + Latitud + ","+ Longitud + "&sensor=true");
                    }

                } catch (IOException e) {
                    //Toast.makeText(this,"entra al Catch",Toast.LENGTH_LONG).show();
                    Direccion = String.format("%s", e);
                }
            }
        }
    }

    /**************************************************Accedemos a la API de GOOGLE para obtener direccion *********************************/
    /**************************************************CAMBIAR POR UNA TAREA ASINCRONA *********************************/

    private void updateUi(Cadena cadena){

        cadDireccion = new Cadena(cadena.valor);

    }





    public class DireccionConexionAsyncTask extends AsyncTask<String,Void,Cadena>{

        @Override
        protected Cadena doInBackground(String... strings) {

            if (strings.length < 1 || strings[0] == null) {
                return null;
            }


            // Perform the HTTP request for earthquake data and process the response.

            Cadena cad = Utiles.fetchEarthquakeData(strings[0]);

            return cad;
        }

        @Override
        protected void onPostExecute(Cadena cadena) {
            //super.onPostExecute(cadena);

            if (cadena == null) {
                return;
            }

            // Update the information displayed to the user.
            updateUi(cadena);
        }
    }








    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {
            Longitud = "Desconocida";
            Latitud = "Desconocida";
        } else {
            Longitud = String.valueOf(location.getLongitude());
            Latitud = String.valueOf(location.getLatitude());
        }
        //llamamos a muestra direccion
        setDireccionActual(location);

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




    /******************ENVIO DE INFORMACION FINAL (CLASE CLIENTE) A WEB SERVICE ************/







    private void envioEvento() {


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_DATA_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    if(response != null){

                                        Toast.makeText(principal.this, "Evento Registrado Exitosamente", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Toast.makeText(principal.this, error.toString(), Toast.LENGTH_LONG).show();
                                    Toast.makeText(principal.this, "Tiempo para conexión finalizado, revise su conexion a internet" , Toast.LENGTH_LONG).show();

                                }
                            }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> map = new HashMap<String, String>();
                            map.put("nombre", evento.getNombre());
                            map.put("apellido", evento.getApellido());
                            map.put("detalle", evento.getDetalle());
                            map.put("latitud",evento.getLatitud());
                            map.put("longitud",evento.getLongitud());
                            map.put("direccion",evento.getDireccion());
                            map.put("foto",evento.getFoto());
                            map.put("horaevento",evento.getHoraevento());
                            map.put("estatus",evento.getEstatus());


                            return map;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);

                } catch (Exception e) {
                    AlertDialog.Builder builderDos = new AlertDialog.Builder(principal.this);
                    builderDos.setMessage("No se ha logrado enviar el evento, revise su conexión a internet")
                            .setNegativeButton("Intente de nuevo", null)
                            .create()
                            .show();
                }


            }
        }).start();


    }

}




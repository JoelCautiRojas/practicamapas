package com.example.aulac.practicamapas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    Button boton1;
    Snackbar snack;
    RelativeLayout milayout;
    GoogleApiClient cliente;
    Location miubicacion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boton1 = findViewById(R.id.button2);
        milayout = findViewById(R.id.layout1);
        //---- INICIALIZAR COMPONENTES
        cliente = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        boton1.setEnabled(false);

        //---- SOLICITAR LOS PERMISOS
        if (verfificarPermisos()) {
            iniciarComponentes();
        } else {
            solicitarPermisos();
        }
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //---- OBTENER COORDENADAS
                if(miubicacion != null)
                {
                    Intent verMapa = new Intent(MainActivity.this,MapsActivity.class);
                    verMapa.putExtra("latitud",miubicacion.getLatitude());
                    verMapa.putExtra("longitud",miubicacion.getLongitude());
                    startActivity(verMapa);
                    Log.d("LATITUD",String.valueOf(miubicacion.getLatitude()));
                    Log.d("LONGITUD",String.valueOf(miubicacion.getLongitude()));
                }
                else
                {
                    Log.d("ERROR","mi ubicacion es nulo");
                }
            }
        });
    }

    private void solicitarPermisos() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, ACCESS_COARSE_LOCATION))
        {
            snack = Snackbar.make(milayout,"Te has olvidado de los permisos.",Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                            ACCESS_FINE_LOCATION,
                            ACCESS_COARSE_LOCATION
                    },100);
                }
            });
            snack.show();
        }
        else
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
            },100);
        }
    }

    private boolean verfificarPermisos() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            return true;
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this,ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        return false;
    }

    private void iniciarComponentes() {
        boton1.setEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
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
        miubicacion = LocationServices.FusedLocationApi.getLastLocation(cliente);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("ERROR","Conexion suspendida");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("ERROR","sin conexion al servidor");
    }
}

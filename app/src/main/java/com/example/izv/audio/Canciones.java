package com.example.izv.audio;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class Canciones extends Activity {

    private ArrayList<String> cancionesdireccion;
    private ArrayList<String> cancionesnombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_canciones);
        TextView tv=(TextView)findViewById(R.id.tvcanciones);
        ListView lv=(ListView)findViewById(R.id.lvcanciones);
        cancionesdireccion= new ArrayList<String>();
        cancionesnombre=new ArrayList<String>();
        String cantante = getIntent().getExtras().getString("cantante");
        String disco = getIntent().getExtras().getString("disco");
        tv.setText("Canciones del disco "+disco);
        Uri ur= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] proyeccion = null;
        String condicion = "artist=? AND album=?";
        String[] parametros = new String[]{cantante,disco};
        String orden = "track";
        Cursor cursor=getContentResolver().query(
                ur,
                proyeccion,
                condicion,
                parametros,
                orden);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int numc=cursor.getColumnIndex("title");
            String nombre = cursor.getString(numc);
            cancionesnombre.add(nombre);
            numc=cursor.getColumnIndex("_data");
            String ruta = cursor.getString(numc);
            cancionesdireccion.add(ruta);
            cursor.moveToNext();
        }
        Adaptador ad = new Adaptador(this,R.layout.detalle,cancionesnombre);
        lv.setAdapter(ad);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in = new Intent(Canciones.this, Reproductor.class);
                in.putExtra("nombres", cancionesnombre);
                in.putExtra("rutas", cancionesdireccion);
                in.putExtra("contador",i);
                startActivity(in);
            }
        });
    }


}

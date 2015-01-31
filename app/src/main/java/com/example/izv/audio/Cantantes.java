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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Cantantes extends Activity {

    private ArrayList<String> artistas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_cantantes);
        ListView lv=(ListView)findViewById(R.id.lvcantantes);
        ArrayList<String> artistasrepetidos= new ArrayList<String>();
        Uri ur= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] proyeccion = null;
        String condicion = null;
        String[] parametros = null;
        String orden = null;
        Cursor cursor=getContentResolver().query(
                ur,
                proyeccion,
                condicion,
                parametros,
                orden);
        cursor.moveToFirst();
        artistas= new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            int numc=cursor.getColumnIndex("artist");
            String nombre = cursor.getString(numc);
            if (nombre.compareTo("<unknown>")==0){
                nombre="Artista desconocido";
            }
            artistasrepetidos.add(nombre);
            cursor.moveToNext();
        }
        for (int x=0 ;x<artistasrepetidos.size();x++){
            boolean esta=false;
            String nombre=artistasrepetidos.get(x);
            for (int i=0; i<artistas.size();i++){
                String nombre2=artistas.get(i);
                if (nombre.compareTo(nombre2)==0){
                    esta=true;
                }
            }
            if (esta==false){
                artistas.add(nombre);
            }
        }
        Collections.sort(artistas,new Comparator <String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        Adaptador ad = new Adaptador(this,R.layout.detalle,artistas);
        lv.setAdapter(ad);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String cantante=artistas.get(i);
                Intent in = new Intent(Cantantes.this, Discos.class);
                in.putExtra("cantante", cantante);
                startActivity(in);
            }
        });
    }

}

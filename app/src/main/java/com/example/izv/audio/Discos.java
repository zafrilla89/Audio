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
import java.util.Collections;
import java.util.Comparator;


public class Discos extends Activity {

    private ArrayList<String> discos;
    private String cantante;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_discos);
        TextView tv=(TextView)findViewById(R.id.tvdicos);
        ListView lv=(ListView)findViewById(R.id.lvdiscos);
        ArrayList<String> discosrepetidos= new ArrayList<String>();
        cantante = getIntent().getExtras().getString("cantante");
        tv.setText("Discos de "+cantante);
        if (cantante.compareTo("Artista desconocido")==0){
            cantante="<unknown>";
        }
        Uri ur= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] proyeccion = null;
        String condicion = "artist=?";
        String[] parametros = new String[]{cantante};
        String orden = null;
        Cursor cursor=getContentResolver().query(
                ur,
                proyeccion,
                condicion,
                parametros,
                orden);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int numc=cursor.getColumnIndex("album");
            String nombre = cursor.getString(numc);
            discosrepetidos.add(nombre);
            cursor.moveToNext();
        }
        discos=new ArrayList<String>();
        for (int x=0 ;x<discosrepetidos.size();x++){
            boolean esta=false;
            String nombre=discosrepetidos.get(x);
            for (int i=0; i<discos.size();i++){
                String nombre2=discos.get(i);
                if (nombre.compareTo(nombre2)==0){
                    esta=true;
                }
            }
            if (esta==false){
                discos.add(nombre);
            }
        }
        Collections.sort(discos, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        Adaptador ad = new Adaptador(this,R.layout.detalle,discos);
        lv.setAdapter(ad);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String disco=discos.get(i);
                Intent in = new Intent(Discos.this, Canciones.class);
                in.putExtra("cantante", cantante);
                in.putExtra("disco", disco);
                startActivity(in);
            }
        });
    }



}

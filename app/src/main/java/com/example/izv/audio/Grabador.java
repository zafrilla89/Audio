package com.example.izv.audio;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;


public class Grabador extends Activity {

    private MediaRecorder grabador;
    private TextView tv;
    private File carpeta;
    private boolean guardar=false;
    private String nombre="";
    private Button btguardar,btborrar;
    private ImageButton btgrabar,btparar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_grabador);
        btgrabar=(ImageButton)findViewById(R.id.btgrabar);
        btborrar=(Button)findViewById(R.id.btborrar);
        btguardar=(Button)findViewById(R.id.btguardar);
        btparar=(ImageButton)findViewById(R.id.btparar);
        btparar.setEnabled(false);
        btguardar.setEnabled(false);
        btborrar.setEnabled(false);
        grabador = new MediaRecorder();
        tv=(TextView)findViewById(R.id.tvgrabar);
        carpeta = new File(Environment.getExternalStoragePublicDirectory("Grabador").toString());
        if(carpeta.exists()){
        }else{
            carpeta.mkdir();
        }
    }

    @Override
    protected void onDestroy() {
        File archivo = new File(Environment.getExternalStoragePublicDirectory("Grabador") + "/"+nombre+".mp3");
        archivo.delete();
        Uri uri =MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; ;
        String condicion = "title=?";
        String[] parametros = {"t"};
        getContentResolver().delete(
                uri,
                condicion,
                parametros);
        finish();
        super.onDestroy();
    }

    public void grabar(View view){
        Date d=new Date();
        nombre=(d.getYear() + 1900) + "_" + (d.getMonth() + 1) + "_" + d.getDate() + "_" + d.getHours() + "_" + d.getMinutes() + "_" + d.getSeconds();
        grabador.setAudioSource(
                MediaRecorder.AudioSource.MIC);
        grabador.setOutputFormat(
                MediaRecorder.OutputFormat.MPEG_4);
        grabador.setOutputFile(Environment.getExternalStoragePublicDirectory
                ("Grabador")+"/"+nombre+".mp3" );
        grabador.setAudioEncoder(
                MediaRecorder.AudioEncoder.AMR_NB);
        try {
            grabador.prepare();
            grabador.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        btparar.setEnabled(true);
        btgrabar.setEnabled(false);
    }

    public void parar(View view){
        grabador.stop();
        grabador.release();
        tv.setText("");
        btparar.setEnabled(false);
        btguardar.setEnabled(true);
        btborrar.setEnabled(true);

    }

    public void guardar(View view) {
        guardar=true;
        Intent intent = new Intent
                (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File archivo = new File(Environment.getExternalStoragePublicDirectory("Grabador") + "/"+nombre+".mp3");
        Uri uri = Uri.fromFile(archivo);
        intent.setData(uri);
        this.sendBroadcast(intent);
        finish();
    }

    public void borrar(View view){
            File archivo = new File(Environment.getExternalStoragePublicDirectory("Grabador") + "/"+nombre+".mp3");
            archivo.delete();
            Uri uri =MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; ;
            String condicion = "title=?";
            String[] parametros = {"t"};
            getContentResolver().delete(
                    uri,
                    condicion,
                    parametros);
        finish();
    }


}

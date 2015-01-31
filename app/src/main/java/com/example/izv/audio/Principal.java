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


public class Principal extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
    }

    public void grabador(View v){
        Intent i = new Intent(this,Grabador.class);
        startActivity(i);
    }

    public void reproductor(View v){
        Intent i = new Intent(this,Cantantes.class);
        startActivity(i);
    }

}

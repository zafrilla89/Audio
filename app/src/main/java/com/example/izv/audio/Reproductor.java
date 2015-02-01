package com.example.izv.audio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class Reproductor extends Activity {

    private ArrayList<String> canciones, rutas;
    private int cont;
    private TextView tv;
    private Intent intent;
    private Button pl, pa, st;
    private BroadcastReceiver receptor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Log.v("AAAAAAAAAAAAAAAAAAAA","entra en broadcast");
            if (bundle != null) {
                cont = bundle.getInt("contador");
                tv.setText(canciones.get(cont));
            }
        }
    };
    private ImageButton btnr, btr1, btrt, bta, btna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_reproductor);
        pl=(Button)findViewById(R.id.pl);
        pa=(Button)findViewById(R.id.pa);
        st=(Button)findViewById(R.id.st);
        btnr=(ImageButton)findViewById(R.id.btnr);
        btr1=(ImageButton)findViewById(R.id.btr1);
        btrt=(ImageButton)findViewById(R.id.btrt);
        btnr.setEnabled(false);
        bta=(ImageButton)findViewById(R.id.bta);
        btna=(ImageButton)findViewById(R.id.btna);
        btna.setEnabled(false);
        pl.setEnabled(false);
        canciones = getIntent().getStringArrayListExtra("nombres");
        rutas = getIntent().getStringArrayListExtra("rutas");
        cont=getIntent().getIntExtra("contador", -1);
        tv=(TextView)findViewById(R.id.tvreproductor);
        tv.setText(canciones.get(cont));
        intent = new Intent(this, Audio.class);
        intent.putExtra("canciones", rutas);
        intent.putExtra("contador", cont);
        intent.putExtra("repeticion","no");
        intent.putExtra("aleatoria","no");
        intent.setAction(Audio.ADD);
        startService(intent);
        intent.setAction(Audio.PLAY);
        startService(intent);
        registerReceiver(receptor, new IntentFilter(Audio.MENSAJE));
        unregisterReceiver(receptor);
    }

    public void play(View view){
        pl.setEnabled(false);
        pa.setEnabled(true);
        st.setEnabled(true);
        intent.setAction(Audio.PLAY);
        startService(intent);
    }

    public void stop(View view){
        pl.setEnabled(true);
        pa.setEnabled(false);
        st.setEnabled(false);
        intent.setAction(Audio.STOP);
        startService(intent);
    }

    public void pause(View view){
        pl.setEnabled(true);
        pa.setEnabled(false);
        st.setEnabled(true);
        intent.setAction(Audio.PAUSE);
        startService(intent);
    }

    public void pararservicio(View view){
        stopService(intent);
        finish();
    }

    public void anterior(View view){
        pl.setEnabled(false);
        pa.setEnabled(true);
        st.setEnabled(true);
        cont=cont-1;
        if (cont<0){
            cont=canciones.size()-1;
        }
        tv.setText(canciones.get(cont));
        intent.setAction(Audio.ANTERIOR);
        startService(intent);
        intent.setAction(Audio.PLAY);
        startService(intent);
    }

    public void siguiente(View view){
        pl.setEnabled(false);
        pa.setEnabled(true);
        st.setEnabled(true);
        cont=cont+1;
        if (cont==canciones.size()){
            cont=0;
        }
        tv.setText(canciones.get(cont));
        intent.setAction(Audio.SIGUIENTE);
        startService(intent);
        intent.setAction(Audio.PLAY);
        startService(intent);
    }

    public void norepetir(View view){
        intent.setAction(Audio.NOREPETIR);
        startService(intent);
        btnr.setEnabled(false);
        btrt.setEnabled(true);
        btr1.setEnabled(true);
    }

    public void repetir1(View view){
        intent.setAction(Audio.REPETIR1);
        startService(intent);
        btnr.setEnabled(true);
        btrt.setEnabled(true);
        btr1.setEnabled(false);
    }

    public void repetirtoras(View view){
        intent.setAction(Audio.REPETIRTODAS);
        startService(intent);
        btnr.setEnabled(true);
        btrt.setEnabled(false);
        btr1.setEnabled(true);
    }

    public void noaleatorio(View view){
        intent.setAction(Audio.NOALEATORIA);
        startService(intent);
        btna.setEnabled(false);
        bta.setEnabled(true);
    }

    public void aleatorio(View view){
        intent.setAction(Audio.ALEATORIA);
        startService(intent);
        btna.setEnabled(true);
        bta.setEnabled(false);
    }

}

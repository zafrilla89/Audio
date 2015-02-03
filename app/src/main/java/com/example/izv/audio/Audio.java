package com.example.izv.audio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Audio extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener{

    private MediaPlayer mp;
    private enum Estados{
        idle,
        initialized,
        preparing,
        prepared,
        started,
        paused,
        completed,
        stoped,
        end,
        error
    };
    private ArrayList<String> canciones;
    private int cont, conta, milisegundo=1000;
    private Estados estado;
    public static final String PLAY="play";
    public static final String PAUSE="pause";
    public static final String STOP="stop";
    public static final String ADD="add";
    public static final String SIGUIENTE="siguiente";
    public static final String ANTERIOR="anterior";
    public static final String NOREPETIR="no repetir";
    public static final String REPETIR1="repetir 1";
    public static final String REPETIRTODAS="repetir todas";
    public static final String NOALEATORIA="no aleatoria";
    public static final String ALEATORIA="aleatoria";
    public static final String MOVERBARRA="mover barra";
    public final static String CONTADOR ="contador";
    public final static String DURACION ="duracion";
    public final static String BARRASEGUNDO ="barrasegundo";
    public final static String COMPLETADA ="completada";
    private AvanceDeCancion adc;
    private Uri cancion=null;
    private boolean reproducir;
    private String repeticion, aleatoria;
    private boolean pause=false;

    /**********************************************************************************************/
    // CONSTRUCTOR //
    /**********************************************************************************************/
    public Audio() {
    }
    /**********************************************************************************************/
    // METODOS HEREDAROS//
    /**********************************************************************************************/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            String action = intent.getAction();
            if (action.equals(PLAY)){
                play();
            }else{
                if(action.equals(ADD)){
                    canciones=intent.getExtras().getStringArrayList("canciones");
                    cont=intent.getIntExtra("contador", -1);
                    repeticion=intent.getExtras().getString("repeticion");
                    aleatoria=intent.getExtras().getString("aleatoria");
                    add(canciones.get(cont));
                }else {
                    if (action.equals(STOP)) {
                        stop();
                    } else {
                        if (action.equals(PAUSE)) {
                            pause();
                        } else {
                            if (action.equals(SIGUIENTE)) {
                                siguiente();
                            } else {
                                if (action.equals(ANTERIOR)) {
                                    anterior();
                                } else {
                                    if (action.equals(NOREPETIR)) {
                                        this.repeticion = "no";
                                        this.conta = 0;
                                    } else {
                                        if (action.equals(REPETIR1)) {
                                            this.repeticion = "1";
                                        } else {
                                            if (action.equals(REPETIRTODAS)) {
                                                this.repeticion = "todas";
                                            } else {
                                                if (action.equals(NOALEATORIA)) {
                                                    this.aleatoria = "no";
                                                } else {
                                                    if (action.equals(ALEATORIA)) {
                                                        this.aleatoria = "si";
                                                        this.conta = 0;
                                                    } else {
                                                        if (action.equals(MOVERBARRA)) {
                                                            milisegundo = intent.getIntExtra("milisegundo", -1);
                                                            mp.seekTo(milisegundo);

                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        AudioManager am = (AudioManager)
                getSystemService(Context.AUDIO_SERVICE);
        int r = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(r==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mp= new MediaPlayer();
            mp.setOnPreparedListener(this);
            mp.setOnCompletionListener(this);
            mp.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
            estado= Estados.idle;
        }else{
            stopSelf();
        }
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        // mp.reset();
        mp.release();
        mp=null;
        adc.cancel(true);
        super.onDestroy();
    }


    /**********************************************************************************************/
    // METODO OMPREPAREDLLISTENER //
    /**********************************************************************************************/

    @Override
    public void onPrepared(MediaPlayer mp) {
        estado=Estados.prepared;
        if (reproducir) {
            mp.start();
            estado=Estados.started;
            Intent in = new Intent(DURACION);
            in.putExtra("duracion", mp.getDuration());
            sendBroadcast(in);
            milisegundo=1000;
            adc=new AvanceDeCancion();
            adc.execute();
        }
    }

    /**********************************************************************************************/
    // METODO ONCOMPLETIONLISTENER //
    /**********************************************************************************************/

    @Override
    public void onCompletion(MediaPlayer mp) {
        estado=Estados.completed;
            if (repeticion.compareTo("no") == 0) {
                if (aleatoria.compareTo("no")==0) {
                    if ((cont + 1) != canciones.size()) {
                        siguiente();
                        play();
                    }else {
                        Intent in = new Intent(COMPLETADA);
                        sendBroadcast(in);
                    }
                }else{
                    if ((conta + 1) != canciones.size()) {
                        siguiente();
                        play();
                        conta=conta+1;
                    }else {
                        Intent in = new Intent(COMPLETADA);
                        sendBroadcast(in);
                    }
                }
            } else {
                if (repeticion.compareTo("1") == 0) {
                    play();
                } else {
                    if (repeticion.compareTo("todas") == 0) {
                        siguiente();
                        play();
                    }
                }
            }
    }

    /**********************************************************************************************/
    // METODO ONAUDIOFOCUSCHANGELISTENER //
    /**********************************************************************************************/

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                play();
                mp.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mp.setVolume(0.1f, 0.1f);
                break;
        }
    }

    /**********************************************************************************************/
    // METODOS DE AUDIO //
    /**********************************************************************************************/

    private void play(){
        if (cancion!=null){
            if (estado==Estados.error){
                estado=Estados.idle;
            }
            if (estado==Estados.idle){
                try {
                    mp.setDataSource(this,cancion);
                    estado=Estados.initialized;
                } catch (IOException e) {
                    estado=Estados.error;
                }
            }
            if (estado==Estados.initialized || estado==Estados.stoped){
                reproducir=true;
                mp.prepareAsync();
                estado=Estados.preparing;
            }else{
                if (estado==Estados.preparing){
                    reproducir=true;
                }
                if (estado==Estados.prepared || estado==Estados.paused ){
                    pause=false;
                    mp.start();
                    estado=Estados.started;
                }
                if (estado==Estados.completed) {
                    pause = false;
                    milisegundo = 1000;
                    mp.start();
                    adc = new AvanceDeCancion();
                    adc.execute();
                    estado=Estados.started;
                }
                if(estado==Estados.started){
                }
            }
        }
    }

    private void stop(){
        if (estado==Estados.prepared || estado==Estados.started || estado==Estados.paused || estado==Estados.completed){
            mp.seekTo(0);
            mp.stop();
            estado=Estados.stoped;
            adc.cancel(true);
        }reproducir=false;
    }

    private void add(String cancion){
        File f=new File(cancion);
        Uri uri=Uri.fromFile(f);
        this.cancion=uri;
        estado= Estados.idle;
        mp.reset();
    }

    private void pause(){
        if(estado==Estados.started){
            mp.pause();
            estado=Estados.paused;
            pause=true;
        }
    }

    private void siguiente(){
        if (aleatoria.compareTo("no")==0) {
            cont=cont+1;
            if (cont == canciones.size()) {
                cont = 0;
            }
        }else{
            Random rnd = new Random();
            cont=rnd.nextInt(canciones.size());
        }
        Intent in = new Intent(CONTADOR);
        in.putExtra("contador", cont);
        sendBroadcast(in);
        stop();
        add(canciones.get(cont));
    }

    private void anterior(){
        cont=cont-1;
        if (cont<0){
            cont=canciones.size()-1;
        }
        Intent in = new Intent(CONTADOR);
        in.putExtra("contador", cont);
        sendBroadcast(in);
        stop();
        add(canciones.get(cont));
    }

    /**********************************************************************************************/
    // CLASE ASYNTASK //
    /**********************************************************************************************/

    class AvanceDeCancion extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            for (milisegundo=1000;milisegundo<mp.getDuration();milisegundo=milisegundo+1000){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                    if (this.isCancelled()){
                        return null;
                    }
                    while (pause==true){

                    }

                publishProgress(milisegundo);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Intent in=new Intent(BARRASEGUNDO);
            in.putExtra("segundo", values[values.length - 1]);
            sendBroadcast(in);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}


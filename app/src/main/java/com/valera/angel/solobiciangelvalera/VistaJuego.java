package com.valera.angel.solobiciangelvalera;


import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

public class VistaJuego extends View implements SensorEventListener {
    // COCHES //
    private Vector<Grafico> Coches; // Vector con los Coches
    private int numCoches = 5; // Número inicial de Coches
    private int numMotos = 3; // Fragmentos/Motos en que se divide un Coche
    private int puntuacion = 0;

    // BICI //
    private Grafico bici;
    private int giroBici; // Incremento en la dirección de la bici
    private float aceleracionBici; // Aumento de velocidad en la bici
    private static final int PASO_GIRO_BICI = 5;
    private static final float PASO_ACELERACION_BICI = 1.5f;

    // THREAD Y TIEMPO //
    // Hilo encargado de procesar el tiempo
    private HiloJuego hiloJuego;
    // Tiempo que debe transcurrir para procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Momento en el que se realizó el último proceso
    private long ultimoProceso = 0;

    // PANTALLA TÁCTIL //
    // Las variables mX y mY se utilizarán para recordar
    // las coordenadas del último evento.
    private float mX = 0, mY = 0;
    private boolean disparo = false;

    // RUEDA //
    private Grafico rueda;
    private static float VELOCIDAD_RUEDA = 12;
    private boolean ruedaActiva;
    private int distanciaRueda;
    private double ruedaIncX;
    private double ruedaIncY;

    // Controlar si la aplicación está en segundo plano
    private boolean corriendo = false;
    // Controlar si la aplicación está en pausa
    private boolean pausa;


    private LocationManager locManager;
    private LocationListener locListener;
    private Drawable graficoBici, graficoCoche, graficoRueda, graficoMoto;

    public VistaJuego(Context contexto, AttributeSet atributos) {
        super(contexto, atributos);

        // Obtenemos la imagen/recurso del coche
        graficoCoche = contexto.getResources().getDrawable(R.drawable.coche);
        graficoMoto = contexto.getResources().getDrawable(R.drawable.moto);
        graficoBici = contexto.getResources().getDrawable(R.drawable.bici);
        // Creamos un vector para todos los coches que irán por pantalla
        // y lo rellenamos con gráficos de coches
        // con valores aleatorios para velocidad, dirección y rotación.
        Coches = new Vector<Grafico>();
        for (int i = 0; i < numCoches; i++) {
            Grafico coche = new Grafico(this, graficoCoche);
            coche.setIncX(Math.random() * 4 - 2);
            coche.setIncY(Math.random() * 4 - 2);
            coche.setAngulo((int) (Math.random() * 360));
            coche.setRotacion((int) (Math.random() * 8 - 4));
            Coches.add(coche);
        }
        bici = new Grafico(this, graficoBici);

        graficoRueda = contexto.getResources().getDrawable(R.drawable.rueda);
        rueda = new Grafico(this, graficoRueda);
        ruedaActiva = false;

        // CONTROL DEL HILO DEL JUEGO
        corriendo = true;

        // REGISTRO DE SENSORES
        SensorManager miSensorManager = (SensorManager) getContext()
                .getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listaSensores = miSensorManager
                .getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!listaSensores.isEmpty()) {
            Sensor sensorAcelerometro = listaSensores.get(0);
            miSensorManager.registerListener(this, sensorAcelerometro,
                    SensorManager.SENSOR_DELAY_UI);
        }

    }

    // Al comenzar y dibujar por primera vez la pantalla del juego
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Dibujamos los coches en posiciones aleatorias
        for (Grafico coche : Coches) {
            do {
                coche.setPosX(Math.random() * (w - coche.getAncho()));
                coche.setPosY(Math.random() * (h - coche.getAlto()));
            } while (coche.distancia(bici) < (w + h) / 5);
        }

        bici.setPosX((this.getWidth() - bici.getAncho()) / 2);
        bici.setPosY((this.getHeight() - bici.getAlto()) / 2);

        // HILO QUE CONTROLA EL JUEGO
        hiloJuego = new HiloJuego();
        hiloJuego.start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Dibujamos cada uno de los coches
        try{
        for (Grafico coche : Coches) {
            coche.dibujaGrafico(canvas);
        }}catch(ConcurrentModificationException e){
            
        }
        bici.dibujaGrafico(canvas);

        // Dibujamos la rueda si lo indica la variable ruedaActiva
        if (ruedaActiva)
            rueda.dibujaGrafico(canvas);

    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    private class HiloJuego extends Thread {
        @Override
        public void run() {
            while (corriendo) {
                actualizaMovimiento();
            }
        }
    }

    protected synchronized void actualizaMovimiento() {
        long ahora = System.currentTimeMillis();
        // No hacemos nada si el período de proceso no se ha cumplido.
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return;
        }
        // Para una ejecución en tiempo real calculamos retardo
        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        // Actualizamos la posición de la bici
        bici.setAngulo((int) (bici.getAngulo() + giroBici * retardo));
        double nIncX = bici.getIncX() + aceleracionBici
                * Math.cos(Math.toRadians(bici.getAngulo())) * retardo;
        double nIncY = bici.getIncY() + aceleracionBici
                * Math.sin(Math.toRadians(bici.getAngulo())) * retardo;
        if (Grafico.distanciaE(0, 0, nIncX, nIncY) <= Grafico.getMaxVelocidad()) {
            bici.setIncX(nIncX);
            bici.setIncY(nIncY);
        }
        bici.incrementaPos();
        bici.setIncX(0);
        bici.setIncY(0);
        // Movemos los coches
        for (Grafico coche : Coches) {
            coche.incrementaPos();
        }
        ultimoProceso = ahora;

        // Movemos la rueda
        if (ruedaActiva) {
            rueda.incrementaPos();
            distanciaRueda--;
            if (distanciaRueda < 0) {
                ruedaActiva = false;
            } else {
                for (int i = 0; i < Coches.size(); i++) {
                    if (rueda.verificaColision(Coches.elementAt(i))) {
                        destruyeCoche(i);
                        i = Coches.size();
                        ruedaActiva = false;
                        if (Coches.size() == 0)
                            TerminarJuego();
                    }
                }
            }
        }
    }

    private void destruyeCoche(int i) {
        //Coches.remove(i);
        double PosX, PosY;
        if (Coches.get(i).getDrawable()==graficoCoche){
            PosX=Coches.get(i).getPosX();
            PosY=Coches.get(i).getPosY();
            Coches.remove(i);
            for (int n=0;n<numMotos;n++){
                Grafico moto=new Grafico(this, graficoMoto);
                moto.setPosX(PosX);
                moto.setPosY(PosY);
                moto.setIncX(Math.random()*7-3);
                moto.setIncY(Math.random() * 7 - 3);
                moto.setAngulo((int)Math.random()*360);
                moto.setRotacion((int)Math.random()*8-4);
                Coches.add(moto);

            }
        }
        else{
            Coches.remove(i);
        }


        MediaPlayer explos = MediaPlayer.create(getContext(), R.raw.explosion);
        explos.start();
        setPuntuacion(getPuntuacion() + 1);
        ruedaActiva = false;

    }

    private void lanzarRueda() {
        rueda.setPosX(bici.getPosX() + bici.getAncho() / 2 - rueda.getAncho()
                / 2);
        rueda.setPosY(bici.getPosY() + bici.getAlto() / 2 - rueda.getAlto() / 2);
        rueda.setAngulo(bici.getAngulo());
        rueda.setIncX(Math.cos(Math.toRadians(rueda.getAngulo()))
                * VELOCIDAD_RUEDA);
        rueda.setIncY(Math.sin(Math.toRadians(rueda.getAngulo()))
                * VELOCIDAD_RUEDA);
        distanciaRueda = (int) Math.min(
                this.getWidth() / Math.abs(rueda.getIncX()), this.getHeight()
                        / Math.abs(rueda.getIncY())) - 2;
        ruedaActiva = true;
    }

    private Activity padre;

    public void setPadre(Activity padre) {

        this.padre = padre;

        obtenerLocalizacion();
    }

    private void obtenerLocalizacion() {

        locManager =
                (LocationManager) padre.getSystemService(Context.LOCATION_SERVICE);


        Location loc =
                locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        mostrarDireccion(loc);


        locListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mostrarDireccion(location);
            }

            public void onProviderDisabled(String provider) {
                padre.setTitle("Provider OFF");
            }

            public void onProviderEnabled(String provider) {
                padre.setTitle("Provider ON ");
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("", "Provider Status: " + status);
                padre.setTitle("Provider Status: " + status);
            }
        };

        locManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 30000, 0, locListener);
    }

    private void mostrarDireccion(Location loc){
        Geocoder geocoder = new Geocoder(padre);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = addressList.get(0);
        if (address != null)
        {
            StringBuilder deviceAddress = new StringBuilder();
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
            {
                deviceAddress.append(address.getAddressLine(i))
                        .append(",");
            }
            padre.setTitle(deviceAddress.toString());
        }
        else
        {
            padre.setTitle("Imposible determinar la dirección.");
        }


    }




    private void mostrarPosicion(Location loc) {

        if (loc != null) {

            padre.setTitle("Lat:" + String.valueOf(loc.getLatitude()) + "Lon:" + String.valueOf(loc.getLongitude()));

        } else {
            padre.setTitle("Posicion no encontrada.");

        }
    }


    private void TerminarJuego() {
        if (padre != null) {
            Bundle datos = new Bundle();
            datos.putInt("puntuacion", puntuacion);
            Intent intent = new Intent();
            intent.putExtras(datos);
            padre.setResult(RESULT_OK, intent);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent evento) {
        super.onTouchEvent(evento);
        // Obtenemos la posición de la pulsación
        float x = evento.getX();
        float y = evento.getY();
        switch (evento.getAction()) {
            // Si comienza una pulsación (ACTION_DOWN) activamos la variable disparo
            case MotionEvent.ACTION_DOWN:
                disparo = true;
                break;
            // Comprobamos si la pulsación es continuada con un
            // desplazamiento horizontal o vertical.
            // En caso de ser asi, desactivamos disparo porque se tratará de un
            // movimiento
            // en lugar de un disparo.
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dy < 6 && dx > 6) // Un desplazamiento del dedo horizontal hace
                // girar la bici.
                {
                    giroBici = Math.round((x - mX) / 2);
                    disparo = false;
                } else if (dx < 6 && dy > 6) // Un desplazamiento vertical produce
                // una aceleración.

                {
                    aceleracionBici = Math.round((mY - y) / 25);

                    disparo = false;
                }
                break;
            // Si se levanta el dedo (ACTION_UP) sin haberse producido
            // desplazamiento horizontal o vertical
            // disparo estará activado y lo que hacemos es disparar
            case MotionEvent.ACTION_UP:
                giroBici = 0;
                aceleracionBici = 0;
                if (disparo) {
                    lanzarRueda();
                }
                break;
        }
        mX = x;
        mY = y;
        return true;
    }

    public HiloJuego getHilo() {
        return hiloJuego;
    }

    public void setCorriendo(boolean corriendo) {
        this.corriendo = corriendo;
    }

    public void setPausa(boolean pausa) {
        this.pausa = pausa;
    }


    private boolean hayValorInicial = false;
    private float valorInicial;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float valor = event.values[1];
        if (!hayValorInicial) {
            valorInicial = valor;
            hayValorInicial = true;
        }
        giroBici = (int) (valor - valorInicial) / 3;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

}

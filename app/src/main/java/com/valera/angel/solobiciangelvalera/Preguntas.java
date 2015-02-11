package com.valera.angel.solobiciangelvalera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Preguntas extends Activity {
    private LinearLayout interfaz;
    private ArrayList<Pregunta> listaPreguntas;
    public static int PUNTOSTEMP=0;
    private Juego Juego;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitypreguntas);




        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        AlmacenPreguntas almacen=new AlmacenPreguntas(this);
        listaPreguntas=almacen.listaPreguntas();


        interfaz=(LinearLayout)findViewById(R.id.miLinearLayout);
        TextView pregunta[]=new TextView[listaPreguntas.size()];
        final Spinner[] respuesta= new Spinner[listaPreguntas.size()];

        ArrayAdapter<String> adaptador=null;

        for (int i=0;i<listaPreguntas.size();i++){
            listaPreguntas.get(i);
            pregunta[i]=new TextView(this);
            pregunta[i].setText(listaPreguntas.get(i).enunciado);
            interfaz.addView(pregunta[i]);
            respuesta[i]=new Spinner(this);
            interfaz.addView(respuesta[i]);

            adaptador=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listaPreguntas.get(i).respuestas);

            adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            respuesta[i].setAdapter(adaptador);
        }

        Button corregir= new Button(this);
        corregir.setText("Corregir");
        interfaz.addView(corregir);

        corregir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int acertadas=0;
                for (int i=0;i<listaPreguntas.size();i++){
                    int seleccionada=respuesta[i].getSelectedItemPosition()+1;
                    String correcta=listaPreguntas.get(i).correcta;

                    System.out.println("Correcta="+correcta);
                    System.out.println("Seleccionada="+String.valueOf(seleccionada));

                    System.out.println("TamañoCorrecta="+ correcta.substring(1));
                    if (String.valueOf(seleccionada).equals(correcta.substring(1))){
                        System.out.println("OK="+acertadas);
                        acertadas=acertadas+1;
                    }
                }
                System.out.println("Acertadas="+acertadas);
                MostrarAciertos(acertadas);
                lanzaJuego(acertadas);
            }
        });

    }


    public void MostrarAciertos(int acertadas){
        Toast mensaje= Toast.makeText(Preguntas.this,"Has acertado "+acertadas+" y por lo tanto el número de vidas en el juego será de "+acertadas+".", Toast.LENGTH_LONG);
        mensaje.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER,0,0);
        mensaje.show();
    }

    public void lanzaJuego(int numVidas){
        Intent i= new Intent(Preguntas.this,Juego.class);
        startActivityForResult(i, numVidas);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preguntas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int codigoEnviado, int
            codigoResultado, Intent datos) {
        if (codigoEnviado == 0) {
            if (codigoResultado == -1) {
                    System.out.println("PUNTOS:"+datos.getExtras().getInt("puntuacion"));
                    Bundle datos1 = new Bundle();
                    datos1.putInt("puntuacion", datos.getExtras().getInt("puntuacion"));
                    Intent intent = new Intent();
                    intent.putExtras(datos1);
                    super.setResult(RESULT_OK, intent);
            }
        }
    }
}

package com.valera.angel.solobiciangelvalera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends Activity {

    private Button bAcercaDe;
    private Button bJuego;
    private Button bPreferencias;
    private Button bSalir;
    private Button bPuntuaciones;
    public static AlmacenPuntuaciones almacen;
    public static int PUNTOS=0;
    public static int categoria=0;
    public static int nivel=0;
    //SQLiteDatabase bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        almacen = new AlmacenPuntuacionesSQLite(this,"base",null,1);
       // bd = almacen.getWritableDatabase();

        bJuego = (Button) findViewById(R.id.Boton01);
        bJuego.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                lanzarPreguntas();
            }

        });

        //Botón y escuchador para la pantalla de Preferencias
        //Al hacer click en el botón llamamos al método lanzarPreferencias()
        bPreferencias = (Button) findViewById(R.id.Boton02);
        bPreferencias.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                lanzarPreferencias();
            }
        });

        bSalir = (Button) findViewById(R.id.Boton04);
        bSalir.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                mostrarPreferencias();
                finish();
            }
        });

        bPuntuaciones = (Button) findViewById(R.id.Boton05);
        bPuntuaciones.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                lanzarPuntuaciones();
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.config) {
            //Toast.makeText(this, "Configurar",Toast.LENGTH_SHORT).show();
            lanzarPreferencias();
            return true;
        }

        if (id == R.id.acerca) {
            Toast.makeText(this, "Acerca de...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void lanzarPreguntas() {
        final boolean[] seguir = new boolean[1];
        String[] categorias, niveles;
        seguir[0] = false;

        LayoutInflater li = LayoutInflater.from(this);

        View promptsView = li.inflate(R.layout.activityselectipopregunta, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

        // set dialog message

        alertDialogBuilder.setTitle("Elegir juego.");
        // alertDialogBuilder.setIcon(R.drawable.ic_launcher);
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final Spinner categoria = (Spinner) promptsView
                .findViewById(R.id.spinner);
        final Spinner nivel = (Spinner) promptsView
                .findViewById(R.id.spinner2);
        final Button cancel = (Button) promptsView
                .findViewById(R.id.buttonCancel);
        final Button aceptar = (Button) promptsView
                .findViewById(R.id.buttonAceptar);

        // reference UI elements from my_dialog_layout in similar fashion
        categorias = new String[]{
                "REDES", "PROGRAMACIÓN", "BASES DE DATOS", "SISTEMAS OPERATIVOS"
        };
        niveles = new String[]{
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
        };

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, categorias);
        categoria.setAdapter(adapter);
        ArrayAdapter adapter2 = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, niveles);
        nivel.setAdapter(adapter2);


        categoria.setOnItemSelectedListener(new OnCategoriaItemClicked());
        nivel.setOnItemSelectedListener(new OnNivelItemClicked());

        aceptar.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                alertDialog.cancel();
                lanzartrivial();
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                seguir[0] = false;
                alertDialog.cancel();
            }
        });

        categoria.setMinimumHeight(100);
        nivel.setMinimumHeight(100);

        // show it
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }
    public void lanzartrivial() {

                Intent i;
                i = new Intent(this, Preguntas.class);
                startActivityForResult(i, PUNTOS);



    }
    public void lanzarjuego() {
        Intent i = new Intent(this, Juego.class);
        startActivityForResult(i, PUNTOS);

    }

    public void lanzarPreferencias() {
        Intent i = new Intent(this, Preferencias.class);
        startActivity(i);
    }

    public void mostrarPreferencias() {
        SharedPreferences pref =
                getSharedPreferences("com.angelv.solobiciangelvalera_preferences", MODE_PRIVATE);
        String s = "Música: " + pref.getBoolean("musica", true)
                + ", Preguntas: " + pref.getString("preguntas", "3")
                + ", Num.Motos: " + pref.getString("numMotos", "2")
                + ", Multijugador: " + pref.getBoolean("modomulti", true)
                + ", Num. Jugadores: " + pref.getString("jugadores", "2")
                + ", Conexión: " + pref.getString("conexión", "2");
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void lanzarPuntuaciones() {
        Intent i = new Intent(this, Puntuaciones.class);
        // i.getIntExtra("bd", bd);
        startActivity(i);
    }


    @Override
    protected void onActivityResult(int codigoEnviado, int
            codigoResultado, Intent datos) {

        if (codigoEnviado == PUNTOS) {
            if (codigoResultado == Activity.RESULT_OK) {
                almacen.guardarPuntuacion(datos.getExtras().getInt("puntuacion"),"Solobici 4",System.currentTimeMillis());
            }
        }
    }

}
package com.valera.angel.solobiciangelvalera;


        import android.app.Activity;
        import android.media.MediaPlayer;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;

public class Juego extends Activity {

    private VistaJuego vistaJuego;
    MediaPlayer miMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
        vistaJuego.setPadre(this);
        miMediaPlayer= MediaPlayer.create(this,R.raw.audio);
        miMediaPlayer.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.juego, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        // Al poner la variable corriendo a false permitimos que el thread pueda
        // acabar
        vistaJuego.setCorriendo(false);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Ponemos el thread en suspensión
        vistaJuego.setPausa(true);
        miMediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Continuamos ejecutando el thread
        vistaJuego.setPausa(false);
        miMediaPlayer.start();
    }
}

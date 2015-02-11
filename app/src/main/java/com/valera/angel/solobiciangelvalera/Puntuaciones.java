package com.valera.angel.solobiciangelvalera;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

        import static com.valera.angel.solobiciangelvalera.MainActivity.*;

public class Puntuaciones extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puntuaciones);

        setListAdapter(new
                ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                MainActivity.almacen.listaPuntuaciones(10)));



    }
}


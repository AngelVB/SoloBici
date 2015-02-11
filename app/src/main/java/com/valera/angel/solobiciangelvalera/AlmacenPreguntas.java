package com.valera.angel.solobiciangelvalera;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.*;

/**
 * Created by Angel on 08/12/14.
 */
public class AlmacenPreguntas {
    private Context contexto;
    private ArrayList<Pregunta> listaPreguntas;
    private boolean cargadaLista;

    public AlmacenPreguntas (Context contexto){
        this.contexto=contexto;
        listaPreguntas=new ArrayList<Pregunta>();
        cargadaLista=false;

    }

    public ArrayList<Pregunta> listaPreguntas(){
        try{
            if (!cargadaLista){
                HttpClient comunicacion=new DefaultHttpClient();
                HttpGet peticion=new HttpGet("http://f5-preview.awardspace.com/cursoandroid.cursoj2me.com/preguntas.php?categoria="+MainActivity.categoria+"&dificultad="+MainActivity.nivel);
                peticion.setHeader("content-type","application/xml");
                HttpResponse respuesta= comunicacion.execute(peticion);
                String result = EntityUtils.toString(respuesta.getEntity());
                System.out.println("resultado="+result);

                listaPreguntas=leerXML(result);
            }
        } catch (Exception e) {
            Log.e("Almacén Preguntas", e.getMessage(), e);
        }
        return listaPreguntas;
    }
    public ArrayList<Pregunta> leerXML(String entrada) throws Exception{
        SAXParserFactory fabrica;
        fabrica = SAXParserFactory.newInstance();
        SAXParser parser=fabrica.newSAXParser();
        XMLReader lector=parser.getXMLReader();
        ManejadorXML manejadorXML=new ManejadorXML();
        lector.setContentHandler(manejadorXML);
        lector.parse(new InputSource(new StringReader(entrada)));
        cargadaLista=true;
        return manejadorXML.getPreguntas();

    }


}

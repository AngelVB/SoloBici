package com.valera.angel.solobiciangelvalera;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import org.xml.sax.Attributes;

/**
 * Created by Angel on 08/12/14.
 */
public class ManejadorXML extends DefaultHandler {
    private StringBuilder cadena;
    private Pregunta pregunta;
    private ArrayList<Pregunta> listaPreguntas;

    public ArrayList<Pregunta> getPreguntas() {
        return listaPreguntas;
    }

    @Override
    public void startDocument() throws SAXException {
        listaPreguntas = new ArrayList<Pregunta>();
        cadena = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String nombrelocal, String nombreCualif, Attributes atr) throws SAXException{

        if (nombrelocal.equals("pregunta")) {
            pregunta = new Pregunta();
        }
    }

    @Override
    public void characters(char ch[], int comienzo, int longitud){
        cadena.append(ch,comienzo,longitud);
    }

    @Override
    public void endElement (String uri, String nombrelocal, String nombreCualif) throws SAXException{
        if (nombrelocal.equals("enunciado")) {
            pregunta.enunciado = cadena.toString();
        }else if (nombrelocal.equals("respuesta1")){
            pregunta.respuestas[0]=cadena.toString();
        }else if (nombrelocal.equals("respuesta2")){
            pregunta.respuestas[1]=cadena.toString();
        }else if (nombrelocal.equals("respuesta3")){
            pregunta.respuestas[2]=cadena.toString();
        }else if (nombrelocal.equals("respuesta4")){
            pregunta.respuestas[3]=cadena.toString();
        }else if (nombrelocal.equals("correcta")){
            pregunta.correcta=cadena.toString();
            listaPreguntas.add(pregunta);
        }
        cadena.setLength(0);
    }

    @Override
    public void endDocument() throws SAXException{

    }
}
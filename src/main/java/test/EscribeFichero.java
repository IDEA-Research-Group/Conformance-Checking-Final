package test;

import java.io.FileWriter;

public class EscribeFichero {
   
	
	
    public static void execute(String texto, FileWriter fichero){
   		try{

	      //Creamos un Nuevo objeto FileWriter dandole
	      //como par�metros la ruta y nombre del fichero
	
	      //Insertamos el texto creado y si trabajamos
	      //en Windows terminaremos cada l�nea con "\r\n"
	      fichero.write(texto);
	
	      //cerramos el fichero
	      
	      fichero.close();
	
	    }catch(Exception ex){
	      ex.printStackTrace();
	    }
    }
  
}
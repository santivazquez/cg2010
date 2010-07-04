package ar.edu.itba.cg_final.settings;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class GlobalSettings {

	GlobalSettings instance;
	
	Properties p = new Properties();
	
	final URL settingsPath = GlobalSettings.class.getClassLoader().getResource("settings/global.properties");
	
	public GlobalSettings() {
		try {
			p.load(settingsPath.openStream());
		} catch (FileNotFoundException e) {
			System.out.println("Falta el archivo de configuracion");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error al cargar el archivo de configuracion");
			e.printStackTrace();
		}
	}
	
	public GlobalSettings getInstance(String filepath) {
		if (instance == null) {
			instance = new GlobalSettings();
		}
		return instance;
	}

	public String getProperty(String property) {
		return p.getProperty(property);
	}
	
	public String toString(){
		return p.toString();
	}
	
	public void setProperty(String property, String value){
		p.setProperty(property, value);
		return;
	}
	
	public void store(){
		try {
			p.store(new FileOutputStream(settingsPath.getFile()), "Archivos de Configuracion de Rally\n#Las teclas son hexadecimales");
		} catch (FileNotFoundException e) {
			System.out.println("Falta el archivo de configuracion");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error al guardar el archivo de configuracion");
			e.printStackTrace();
		}
	}
	
	public int getHexProperty(String property){
		return Integer.parseInt(p.getProperty(property), 16);
	}
	
	public int getIntProperty(String property){
		return Integer.parseInt(p.getProperty(property));
	}	
	
}

package br.main.player.util;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Propriedades {

	private static final String pathPropriedades = new java.io.File("").getAbsolutePath() + "/config.properties";
	
	public static void salvarWH(int w, int h) {
		salvarWH(new Dimension(w, h));
	}
	
	public static void salvarWH(Dimension d) {
		if (d==null) {return;}

		try (OutputStream output = new FileOutputStream(pathPropriedades)) {
            Properties prop = new Properties();
            prop.setProperty("player.width", d.getWidth() + "");
            prop.setProperty("player.height", d.getHeight() + "");
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }
	}
	
	public static Dimension getWH() {
		File f = new File(pathPropriedades);
		if (!f.exists()) {return null;}
		try (InputStream input = new FileInputStream(f)) {
            Properties prop = new Properties();
            prop.load(input);

            String w = prop.getProperty("player.width");
            String h = prop.getProperty("player.height");
            
            if (w==null||h==null||w.isEmpty()||h.isEmpty()) {return null;}
            double wD = Double.parseDouble(w);
            double hD = Double.parseDouble(h);
            int wI = (int) Math.round(wD);
            int hI = (int) Math.round(hD);
            return new Dimension(wI, hI);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
			f = null;
		}
		return null;
	}
	
}
package br.main.player.util;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.function.Function;

public class Propriedades {

	private static final String pathPropriedades = new java.io.File("").getAbsolutePath() + "/config.properties";
	
	public static void salvar(int x, int y, int width, int height) {
		salvar(new Rectangle(x, y, width, height));
	}
	
	public static void salvar(Rectangle rect) {
		if (rect==null) {return;}

		try (OutputStream output = new FileOutputStream(pathPropriedades)) {
            Properties prop = new Properties();
            prop.setProperty("player.x", rect.getX() + "");
            prop.setProperty("player.y", rect.getY() + "");
            prop.setProperty("player.width", rect.getWidth() + "");
            prop.setProperty("player.height", rect.getHeight() + "");
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }
	}
	
	public static Rectangle getRectangle() {
		File f = new File(pathPropriedades);
		if (!f.exists()) {return null;}
		try (InputStream input = new FileInputStream(f)) {
            Properties prop = new Properties();
            prop.load(input);
            
            Function<String, Integer> toInt = valor -> {
            	if (valor==null||valor.isEmpty()) {return 0;}
            	return (int) Math.round(Double.parseDouble(valor));
            };
            
            String x = prop.getProperty("player.x");
            String y = prop.getProperty("player.y");
            String w = prop.getProperty("player.width");
            String h = prop.getProperty("player.height");
            
            if (x==null||x.isEmpty()||y==null||y.isEmpty()|| w==null||h==null||w.isEmpty()||h.isEmpty()) {return null;}
            return new Rectangle(toInt.apply(x), toInt.apply(y), toInt.apply(w), toInt.apply(h));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
			f = null;
		}
		return null;
	}
	
}
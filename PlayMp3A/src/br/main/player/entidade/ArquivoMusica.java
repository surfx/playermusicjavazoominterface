package br.main.player.entidade;

import java.io.File;
import java.io.Serializable;

import br.main.player.mp3propriedades.LerPropriedadesMp3;

public class ArquivoMusica implements Serializable {

	private static final long serialVersionUID = 1L;

	private String path;

	public ArquivoMusica() {}
	public ArquivoMusica(String path) { this.setPath(path); }
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public boolean isValid() {
		return getPath() != null && !getPath().isEmpty();
	}
	
	public boolean existe() {
		if (getPath() == null || getPath().isEmpty()) { return false; }
		return new File(getPath()).exists();
	}
	
	// remove o path
	public String getName() {
		if (!isValid()) {return null;}
		path = path.replace("\\", "/");
		if (!path.contains("/")) {return path;}
		return path.substring(path.lastIndexOf("/")+1);
	}
	
//	String musicacompleto = LerPropriedadesMp3.tratarNomeMusica(idxf.getFile().getName());
//	if (musicacompleto.contains("-")) {
//		String artista = musicacompleto.substring(0, musicacompleto.lastIndexOf("-")).trim();
//		String musica = musicacompleto.substring(musicacompleto.lastIndexOf("-") + 1).trim();
	
	public String getNomeMusica() {
		return LerPropriedadesMp3.tratarNomeMusica(getName());
	}
	
	public String getArtista() {
		String nm = getNomeMusica();
		return nm!=null && !nm.isEmpty() && nm.contains("-") ? nm.substring(0, nm.lastIndexOf("-")).trim() : nm;
	}

	public String getTituloMusica() {
		String nm = getNomeMusica();
		return nm!=null && !nm.isEmpty() && nm.contains("-") ? nm.substring(nm.lastIndexOf("-") + 1).trim() : nm;
	}
	
	@Override
	public String toString() {
		return getArtista();
	}
	
}
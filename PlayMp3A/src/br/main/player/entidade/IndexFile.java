package br.main.player.entidade;

import java.io.File;
import java.io.Serializable;

public class IndexFile implements Serializable {

	private static final long serialVersionUID = 1L;

	private int indice;
	private File file;

	public IndexFile(int indice, File file) {
		this.indice = indice;
		this.file = file;
	}

	public int getIndice() {
		return this.indice;
	}
	public void setIndice(int indice) {
		this.indice = indice;
	}
	public File getFile() {
		return this.file;
	}
	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "IndexFile [indice=" + this.indice + ", file=" + this.file + "]";
	}

}
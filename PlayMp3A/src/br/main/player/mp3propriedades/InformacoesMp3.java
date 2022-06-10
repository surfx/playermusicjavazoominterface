package br.main.player.mp3propriedades;

import java.io.Serializable;

/**
 * informacoes do arquivo mp3
 * @author spy
 *
 */
public class InformacoesMp3 implements Serializable {

	private static final long serialVersionUID = 1L;

	private String titulo;
	private String artista;
	private String artistaAlbum;
	private String data;
	private String album;
	
	public InformacoesMp3() {}
	
	public InformacoesMp3(String titulo, String artista, String artistaAlbum, String data, String album) {
		super();
		this.titulo = titulo;
		this.artista = artista;
		this.artistaAlbum = artistaAlbum;
		this.data = data;
		this.album = album;
	}

	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getArtista() {
		return artista;
	}
	public void setArtista(String artista) {
		this.artista = artista;
	}
	public String getArtistaAlbum() {
		return artistaAlbum;
	}
	public void setArtistaAlbum(String artistaAlbum) {
		this.artistaAlbum = artistaAlbum;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}

	@Override
	public String toString() {
		return "InformacoesMp3 [titulo=" + titulo + ", artista=" + artista + ", artistaAlbum=" + artistaAlbum
				+ ", data=" + data + ", album=" + album + "]";
	}

}
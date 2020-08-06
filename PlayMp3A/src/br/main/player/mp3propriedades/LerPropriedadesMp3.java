package br.main.player.mp3propriedades;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import br.main.arquivos.util.hash.SHA1Calculo;

/**
 * 
 * https://mvnrepository.com/artifact/com.mpatric/mp3agic/0.9.1<br />
 * https://github.com/mpatric/mp3agic<br /><br />
 * 
 * Obs: essa lib é muito lenta, pensar numa forma de fazer cache
 *
 */
public class LerPropriedadesMp3 implements Serializable {
	private static final long serialVersionUID = 1L;

	private static LerPropriedadesMp3 instance;
	public static LerPropriedadesMp3 getInstance() {
		if(instance==null) {instance=new LerPropriedadesMp3();}
		return instance;
	}
	private LerPropriedadesMp3() {}

	// <SHA, InformacoesMp3>
	private Map<String, InformacoesMp3> mapInformacoesArquivos;
	
	public void calcularInformacoes(List<File> arquivos, boolean forcar) {
		if(arquivos==null||arquivos.isEmpty()) {return;}
		if(forcar && mapInformacoesArquivos!=null) { mapInformacoesArquivos.clear(); }
		mapInformacoesArquivos = mapInformacoesArquivos != null ? mapInformacoesArquivos : new HashMap<String, InformacoesMp3>();
		for(File f : arquivos) {
			if(f==null||!f.exists()) {continue;}
			String sha = SHA1Calculo.getInstance().hashFile(f);
			if(sha==null||sha.isEmpty()) {continue;}
			if(mapInformacoesArquivos.containsKey(sha)) {continue;} //já calculado, arquivo repetido
			InformacoesMp3 info = getInfo(f);
			if (info==null) {continue;}
			mapInformacoesArquivos.put(sha, info);
		}
	}
	
	public InformacoesMp3 getInformacoesMp3(File f) {
		if (f == null || !f.exists()) { return null; }
		String sha = SHA1Calculo.getInstance().hashFile(f);
		if (sha == null || sha.isEmpty()) { return null; }
		if (mapInformacoesArquivos == null || !mapInformacoesArquivos.containsKey(sha)) {
			mapInformacoesArquivos = mapInformacoesArquivos != null ? mapInformacoesArquivos : new HashMap<String, InformacoesMp3>();
			//if (mapInformacoesArquivos.containsKey(sha)) { return mapInformacoesArquivos.get(sha); } // já calculado, arquivo repetido
			InformacoesMp3 info = getInfo(f);
			if (info == null) { return null; }
			mapInformacoesArquivos.put(sha, info);
			return info;
		}
		return mapInformacoesArquivos.get(sha);
	}
	
	
	private InformacoesMp3 getInfo(File f) {
		if (f == null) { return null; }
		try {
			Mp3File mp3file = new Mp3File(f); // essa lib é muito lenta
			if (mp3file.hasId3v1Tag()) {
				ID3v1 id3v1tag = mp3file.getId3v1Tag();
				return new InformacoesMp3(id3v1tag.getTitle(), id3v1tag.getArtist(), null, null, id3v1tag.getAlbum());
			} else if (mp3file.hasId3v2Tag()) {
				ID3v2 id3v2tag = mp3file.getId3v2Tag();
				return new InformacoesMp3(id3v2tag.getTitle(), id3v2tag.getArtist(), id3v2tag.getAlbumArtist(), id3v2tag.getDate(), null);
			}
		} catch (UnsupportedTagException | InvalidDataException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String tratarNomeMusica(File f) {
		if (f==null) {return "";}
		return tratarNomeMusica(f.getName());
	}
	
	public static String tratarNomeMusica(String nome) {
		if (nome==null||nome.isEmpty()) {return "";}
		if (!nome.contains(".")) {return nome;}
		nome = nome.substring(0, nome.lastIndexOf("."));
		return nome;
	}
	
}
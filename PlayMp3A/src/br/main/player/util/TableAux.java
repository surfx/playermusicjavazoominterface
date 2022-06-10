package br.main.player.util;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.main.player.entidade.IndexFile;
import br.main.player.mp3propriedades.LerPropriedadesMp3;

public class TableAux implements Serializable {
	private static final long serialVersionUID = 1L;

	public static Object[][] getDadosJTable(List<IndexFile> listaMusicas) {
		if (listaMusicas == null || listaMusicas.isEmpty()) { return null; }

		final int size = listaMusicas.size();
		Object[][] dados = new Object[size][3];
		for (int i = 0; i < size; i++) {
			IndexFile idxf = listaMusicas.get(i);

			// essa leitura e muito demorada :(
			//InformacoesMp3 info = null; // LerPropriedadesMp3.getInstance().getInformacoesMp3(f);
			//if (info != null) { // por enquanto...
			//	dados[i] = new Object[] { info.getArtista(), info.getTitulo() /* , f.getAbsolutePath() */ };
			//} else {
			String musicacompleto = LerPropriedadesMp3.tratarNomeMusica(idxf.getFile().getName());
			if (musicacompleto.contains("-")) {
				String artista = musicacompleto.substring(0, musicacompleto.lastIndexOf("-"));
				String musica = musicacompleto.substring(musicacompleto.lastIndexOf("-") + 1);
				dados[i] = new Object[] { (idxf.getIndice()+1) + "", artista, musica /* , f.getAbsolutePath() */ };
			} else {
				dados[i] = new Object[] { (idxf.getIndice()+1) + "", musicacompleto, musicacompleto /* , f.getAbsolutePath() */ };
			}
			//}
		}
		return dados;
	}

	public static Map<Integer, IndexFile> toMapMusicas(List<IndexFile> listaMusicas){
		if (listaMusicas == null || listaMusicas.isEmpty()) { return null; }
		Map<Integer, IndexFile> map = new HashMap<Integer, IndexFile>();
		for(int i = 0; i < listaMusicas.size(); i++) {
			map.put(i, listaMusicas.get(i));
		}
		return map;
	}

	public static List<IndexFile> toListMusicas(Map<Integer, IndexFile> mapMusicas){
		if (mapMusicas == null || mapMusicas.isEmpty()) { return null; }
		List<IndexFile> rt = new ArrayList<IndexFile>();
		for(Entry<Integer, IndexFile> par : mapMusicas.entrySet()) {
			rt.add(par.getValue());
		}
		return rt;
	}

	public static List<IndexFile> convert(List<File> lista){
		if (lista==null||lista.isEmpty()) {return null;}
		List<IndexFile> rt = new ArrayList<IndexFile>();
		for(int i = 0; i < lista.size(); i++) {
			rt.add(new IndexFile(i, lista.get(i)));
		}
		return rt;
	}

	public static List<File> convertIdx(List<IndexFile> lista){
		if (lista==null||lista.isEmpty()) {return null;}
		List<File> rt = new ArrayList<File>();
		for(int i = 0; i < lista.size(); i++) {
			rt.add(lista.get(i).getFile());
		}
		return rt;
	}

}
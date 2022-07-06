package br.main.arquivos;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import br.main.arquivos.util.Arquivos;

public class ListaMusicas {

	private final String[] extensions = new String[] { "mp3", "wav" };
	private final String pathListaSalvar = new java.io.File("").getAbsolutePath() + "/lista.txt";

	public void verificarArquivosMusicas() {
		if (this.pathListaSalvar==null||this.pathListaSalvar.isEmpty()) {return;}
		List<String> musicasPath = Arquivos.reader(this.pathListaSalvar);
		if (musicasPath==null||musicasPath.isEmpty()) {return;}
		int size = musicasPath.size();
		musicasPath = musicasPath.stream().filter(m -> m!=null && !m.isEmpty() && new File(m).exists()).collect(Collectors.toList());
		if (size == musicasPath.size()) {return;}
		Arquivos.write(this.pathListaSalvar, musicasPath, false);
	}
	
	public List<String> salvarListaMusicas(String pathMusicas) {
		verificarArquivosMusicas();
		if (pathMusicas==null||pathMusicas.isEmpty()) {return null;}
		List<File> musicas = getListaMusicasFile();
		List<File> musicasPath = Arquivos.listarArquivos(pathMusicas, this.extensions);
		List<File> musicasMerge = Arquivos.mergeLists(musicas, musicasPath);
		if (musicasMerge==null||musicasMerge.isEmpty()) {return null;}
		List<String> listaSalvar = musicasMerge.stream().filter(m -> m != null && m.exists())
				.map(m -> m.getAbsolutePath()).collect(Collectors.toList());
		Arquivos.write(this.pathListaSalvar, listaSalvar, false);
		System.out.println("Musicas salvas em: " + this.pathListaSalvar);
		return listaSalvar;
	}

	public List<String> getListaMusicas(){
		if (this.pathListaSalvar==null||this.pathListaSalvar.isEmpty()) {return null;}
		verificarArquivosMusicas();
		return Arquivos.reader(this.pathListaSalvar);
	}
	
	public List<File> getListaMusicasFile(){
		List<String> musicas = getListaMusicas();
		if (musicas==null||musicas.isEmpty()) {return null;}
		return musicas.stream().filter(m -> m!=null && !m.isEmpty()).map(m -> new File(m)).filter(m->m.exists()).collect(Collectors.toList());
	}

}
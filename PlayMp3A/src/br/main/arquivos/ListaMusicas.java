package br.main.arquivos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.main.arquivos.util.Arquivos;
import br.main.player.entidade.IndexFile;
import br.main.player.util.TableAux;

public class ListaMusicas {

	private final String[] extensions = new String[] { "mp3", "wav" };
	private final String pathListaSalvar = new java.io.File("").getAbsolutePath() + "/lista.txt";

	public List<IndexFile> salvarListaMusicas(String pathMusicas) {
		if (pathMusicas==null||pathMusicas.isEmpty()) {return null;}
		List<IndexFile> larquivos = Arquivos.listarArquivosIdxF(pathMusicas, this.extensions);
		larquivos = Arquivos.mergeListsIdx(this.getListaMusicas(), larquivos);
		if (larquivos==null||larquivos.isEmpty()) {return null;}

		List<String> listaSalvar = new ArrayList<String>();
		for(IndexFile f : larquivos) {
			listaSalvar.add(f.getFile().getAbsolutePath());
		}

		Arquivos.write(this.pathListaSalvar, listaSalvar, false);
		System.out.println("Musicas salvas em: " + this.pathListaSalvar);
		return larquivos;
	}

	public List<IndexFile> getListaMusicas(){
		if (this.pathListaSalvar==null||this.pathListaSalvar.isEmpty()) {return null;}
		List<String> musicasPath = Arquivos.reader(this.pathListaSalvar);
		if (musicasPath==null||musicasPath.isEmpty()) {return null;}
		List<File> rt = new ArrayList<File>();

		for(String p : musicasPath) {
			if(p==null||p.trim().isEmpty()) {continue;}
			File f = new File(p);
			if (f==null||!f.exists()||f.isDirectory()) {continue;}
			rt.add(f);
		}

		//musicasPath.forEach(f->{ rt.add(new File(f)); });

		// calcula as informacoes no momento do get - muito demorado :(
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				System.out.println("--- iniciado th calcular informacoes ---");
//				long start = System.currentTimeMillis();
//				LerPropriedadesMp3.getInstance().calcularInformacoes(rt, true);
//				long elapsed = System.currentTimeMillis() - start;
//				System.out.println("--- end th calcular informacoes ---");
//				System.out.printf("%.3f ms%n", (start - elapsed) / 1000d);
//			}
//		}).start();


		return TableAux.convert(rt);
	}

}
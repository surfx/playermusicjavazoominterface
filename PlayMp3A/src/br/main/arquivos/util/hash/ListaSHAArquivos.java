package br.main.arquivos.util.hash;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class ListaSHAArquivos implements Serializable {
	private static final long serialVersionUID = 1L;

	private static ListaSHAArquivos instance;
	public static ListaSHAArquivos getInstance() {
		if(instance==null) {instance=new ListaSHAArquivos();}
		return instance;
	}
	private ListaSHAArquivos() {}

	private Map<String, File> mapSHAArquivos;
	
	public Map<String, File> getMapSHAArquivos() {
		return mapSHAArquivos;
	}
	public void setMapSHAArquivos(Map<String, File> mapSHAArquivos) {
		this.mapSHAArquivos = mapSHAArquivos;
	}
	
	public void calcularMapSHAArquivos(List<File> arquivos, boolean forcar) {
		if(arquivos==null||arquivos.isEmpty()) {return;}
		if (forcar && mapSHAArquivos!=null) {mapSHAArquivos.clear();}
		mapSHAArquivos = mapSHAArquivos != null ? mapSHAArquivos : new HashMap<String, File>();
		
		for(File f:arquivos) {
			if(f==null||!f.exists()) {continue;}
			String sha = SHA1Calculo.getInstance().hashFile(f);
			if(sha==null||sha.isEmpty()) {continue;}
			if(mapSHAArquivos.containsKey(sha)) {continue;} //já calculado, arquivo repetido
			mapSHAArquivos.put(sha, f);
		}
	}

	public String getSHA1(File f) {
		if(f==null) {return null;}
		return SHA1Calculo.getInstance().hashFile(f);
	}
	
}
package br.main.arquivos.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.main.arquivos.util.hash.SHA1Calculo;
import br.main.player.entidade.IndexFile;
import br.main.player.util.TableAux;

public class Arquivos {

	private static String nl = System.lineSeparator();

	public static List<IndexFile> listarArquivosIdxF(String path){
		return TableAux.convert(listarArquivos(path));
	}
	public static List<IndexFile> listarArquivosIdxF(String path, String[] extensions){
		return TableAux.convert(listarArquivos(path, extensions));
	}

	private static List<File> listarArquivos(String path){
		return listarArquivos(path, null);
	}
	private static List<File> listarArquivos(String path, String[] extensions){
		if (path==null||path.trim().isEmpty()) {return null;}
		File folder = new File(path);
		if (!folder.exists()) {return null;}
		List<File> rt = new ArrayList<File>();
		for(File file : folder.listFiles()) {
			if (file==null) {continue;}
			if (file.isDirectory()) {
				List<File> aux = listarArquivos(file.getAbsolutePath(), extensions);
				if (aux==null||aux.isEmpty()) {continue;}
				rt.addAll(aux);
				continue;
			}
			if (extensions != null && extensions.length > 0 && !chechExtension(file, extensions)) {continue;}
			rt.add(file);
		}

		return removerDuplicatas(rt);
	}

	private static boolean chechExtension(File file, String[] extensions) {
		for(String extension : extensions) {
			if(file.getName().toLowerCase().endsWith(extension.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	// ----------------------------------------

	public static List<String> reader(String path) {
		if (path==null||path.trim().isEmpty()) {return null;}

		File f = new File(path);
		if (!f.exists()) {return null;}

		List<String> rt = new ArrayList<String>();
		try {
			BufferedReader buffRead = new BufferedReader(new FileReader(path));
			String linha = "";
			while ((linha = buffRead.readLine())!=null) {
				rt.add(linha==null?"":linha);
			}
			buffRead.close();
			return rt;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void write(String path, List<String> texto, boolean append) {
		if (path==null||path.trim().isEmpty()) {return;}
		try {
			BufferedWriter buffWrite = new BufferedWriter(new FileWriter(path, append));
			for(String l : texto) {
				buffWrite.append((l==null?"":l) + nl);
			}
			buffWrite.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ----------------------------------------

	public static List<IndexFile> mergeListsIdx(List<IndexFile> lista1, List<IndexFile> lista2) {
		return TableAux.convert(mergeLists(TableAux.convertIdx(lista1), TableAux.convertIdx(lista2)));
	}

	/**
	 * faz o merge de duas listas.
	 * @param lista1
	 * @param lista2
	 * @return
	 */
	public static List<File> mergeLists(List<File> lista1, List<File> lista2) {
		if (lista1==null||lista1.isEmpty()) {return lista2;}
		if (lista2==null||lista2.isEmpty()) {return lista1;}

		// não usa List pq eu só vou add um arquivo e ignorar o outro
		Map<String, File> map = new HashMap<String, File>();
		for(File f : lista1) {
			if (f==null||!f.exists()) {continue;}
			String sha = SHA1Calculo.getInstance().hashFile(f); if (sha == null) {continue;}
			if (map.containsKey(sha)) {continue;} //ignora o arquivo
			map.put(sha, f);
		}
		for(File f : lista2) {
			if (f==null||!f.exists()) {continue;}
			String sha = SHA1Calculo.getInstance().hashFile(f); if (sha == null) {continue;}
			if (map.containsKey(sha)) {continue;} //ignora o arquivo
			map.put(sha, f);
		}

		List<File> rt = new ArrayList<File>();
		for(Entry<String, File> par : map.entrySet()) {
			if(par==null||par.getValue()==null) {continue;}
			rt.add(par.getValue());
		}
		return rt;
	}

	private static List<File> removerDuplicatas(List<File> lista){
		if (lista==null||lista.isEmpty()) {return lista;}

		// não usa List pq eu só vou add um arquivo e ignorar o outro
		Map<String, File> map = new HashMap<String, File>();
		for(File f : lista) {
			if (f==null||!f.exists()) {continue;}
			String sha = SHA1Calculo.getInstance().hashFile(f); if (sha == null) {continue;}
			if (map.containsKey(sha)) {continue;} //ignora o arquivo
			map.put(sha, f);
		}

		List<File> rt = new ArrayList<File>();
		for(Entry<String, File> par : map.entrySet()) {
			if(par==null||par.getValue()==null) {continue;}
			rt.add(par.getValue());
		}
		return rt;
	}

}
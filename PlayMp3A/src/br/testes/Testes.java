package br.testes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import br.main.arquivos.util.Arquivos;
import br.main.player.entidade.IndexFile;

public class Testes {


	@SuppressWarnings("unused")
	private static void shatest() {
		// https://www.guj.com.br/t/verificar-igualdade-entre-arquivos/55860/4
		String folderPath1 = "D:\\Documentos\\musicas\\musicas2\\";

		String folderPath2 = "C:\\Users\\spy\\Desktop\\musicas2\\";

		List<IndexFile> arquivos1 = Arquivos.listarArquivosIdxF(folderPath1);
		List<IndexFile> arquivos2 = Arquivos.listarArquivosIdxF(folderPath2);

		System.out.println(computeFileMD5(arquivos1.get(1).getFile()));
		System.out.println(hashFile(arquivos1.get(1).getFile()));
		System.out.println("----------------");

		long start = System.currentTimeMillis();
//		List<File> mergeList = mergeLists(arquivos1, arquivos2);
//		long elapsed = System.currentTimeMillis() - start;
//
//		System.out.println("arquivos1: " + arquivos1.size());
//		System.out.println("arquivos2: " + arquivos2.size());
//		System.out.println("mergeList: " + mergeList.size());
//		long t1 = start - elapsed;
//		System.out.printf("%.3f ms%n", t1 / 1000d);
//		mergeList.clear();
//		System.out.println("-----------------------------------------------------------");
//
//		start = System.currentTimeMillis();
//		mergeList = mergeLists2(arquivos1, arquivos2);
//		elapsed = System.currentTimeMillis() - start;
//
//		System.out.println("arquivos1: " + arquivos1.size());
//		System.out.println("arquivos2: " + arquivos2.size());
//		System.out.println("mergeList: " + mergeList.size());
//		long t2 = start - elapsed;
//		System.out.printf("%.3f ms%n", t2 / 1000d);
//		System.out.println("-----------------------------------------------------------");
//
//		System.out.println((t1==t2?"t1==t2":  t1>t2?"t1>t2":"t1<t2") + ", diff: " + Math.abs(t1-t2));

		//mergeList.forEach(System.out::println);

		//File f = arquivos.get(3); System.out.println(hashFile(f));
//		System.out.println("----------------------------");
//		Map<BigInteger, List<File>> map = analiseSHAFiles(folderPath2);
//		map.forEach((k,v)->{
//			//System.out.println("k: " + k);
//			if(v.size()>1) {
//				System.out.println("k: " + k);
//				System.err.println("ERRO");
//				v.forEach(System.out::println);
//				System.out.println("----------------------------");
//			}
//			//System.out.println("----------------------------");
//		});
	}

	public static List<File> mergeLists(List<File> lista1, List<File> lista2) {
		if (lista1==null||lista1.isEmpty()) {return lista2;}
		if (lista2==null||lista2.isEmpty()) {return lista1;}

		// nao usa List pq eu so vou add um arrquivo e ignorar o outro
		Map<String, File> map = new HashMap<String, File>();
		for(File f : lista1) {
			if (f==null||!f.exists()) {continue;}
			String sha = hashFile(f); if (sha== null) {continue;}
			if (map.containsKey(sha)) {continue;} //ignora o arquivo
			map.put(sha, f);
		}
		for(File f : lista2) {
			if (f==null||!f.exists()) {continue;}
			String sha = hashFile(f); if (sha== null) {continue;}
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

	public static List<File> mergeLists2(List<File> lista1, List<File> lista2) {
		if (lista1==null||lista1.isEmpty()) {return lista2;}
		if (lista2==null||lista2.isEmpty()) {return lista1;}

		// n�o usa List pq eu s� vou add um arrquivo e ignorar o outro
		Map<String, File> map = new HashMap<String, File>();
		for(File f : lista1) {
			if (f==null||!f.exists()) {continue;}
			String sha = computeFileMD5(f); if (sha== null) {continue;}
			if (map.containsKey(sha)) {continue;} //ignora o arquivo
			map.put(sha, f);
		}
		for(File f : lista2) {
			if (f==null||!f.exists()) {continue;}
			String sha = computeFileMD5(f); if (sha== null) {continue;}
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


	private static MessageDigest dgst = null;

	private static String hashFile(File arq) {
		try {
			dgst = dgst == null ? MessageDigest.getInstance("SHA1") : dgst;
			FileInputStream fis = new FileInputStream(arq);
			byte[] buffer = new byte[20480];
			int nBytes;
			dgst.reset();
			while ((nBytes = fis.read(buffer)) > 0) {
				dgst.update(buffer, 0, nBytes);
			}
			byte[] bytes = dgst.digest();
			fis.close();
			return new BigInteger(bytes).toString(16);
		} catch (NoSuchAlgorithmException ex) {
		} catch (IOException ex) {
		}
		return null;
	}

	private static MessageDigest md5 = null;

	public static String computeFileMD5(File file) {
		try {
			md5 = md5 == null ? MessageDigest.getInstance("MD5") : md5;
			try (InputStream input = new FileInputStream(file)) {

				byte[] buffer = new byte[8192];
				int len = input.read(buffer);

				while (len != -1) {
					md5.update(buffer, 0, len);
					len = input.read(buffer);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new HexBinaryAdapter().marshal(md5.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

//	@SuppressWarnings("unused")
//	private static Map<String, List<File>> analiseSHAFiles(String path){
//		if (path==null||path.isEmpty()) {return null;}
//		List<File> arquivos = Arquivos.listarArquivos(path);
//		if (arquivos==null||arquivos.isEmpty()) {return null;}
//		Map<String, List<File>> rt = new HashMap<String, List<File>>();
//		for(File f : arquivos) {
//			String sha = hashFile(f);
//			if (sha==null) { sha="erro"; }
//
//			List<File> l = rt.containsKey(sha) ? rt.get(sha) : new ArrayList<File>();
//			l.add(f);
//			rt.put(sha, l);
//		}
//		return rt;
//	}

//	@SuppressWarnings("unused")
//	private static void readAttributes() {
//		String folderPath = "D:\\Documentos\\musicas\\musicas2\\";
//
//		List<File> arquivos = Arquivos.listarArquivos(folderPath);
//		File f = arquivos.get(3);
//
//		f = new File("C:\\Users\\spy\\Desktop\\musicas2\\10cc - Im Not In Love.mp3");
//		System.out.println(f);
//
//		// https://mvnrepository.com/artifact/com.mpatric/mp3agic/0.9.1,
//		// https://github.com/mpatric/mp3agic
//
//		arquivos.forEach(item -> {
//			System.out.println(LerPropriedadesMp3.getInstance().getInformacoesMp3(item));
//		});
//
////		try {
////			Mp3File mp3file = new Mp3File(f);
////			System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
////			System.out.println("Bitrate: " + mp3file.getBitrate() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
////			System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
////			System.out.println("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
////			System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
////			System.out.println("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));
////
////			ID3v2 id3v1tag = mp3file.getId3v2Tag();
////			System.out.println(id3v1tag.getAlbum() + " - " + id3v1tag.getTitle() + " - " + id3v1tag.getArtist() + " - " + id3v1tag.getAlbumArtist());
////
////
////		} catch (UnsupportedTagException | InvalidDataException | IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//
//	}

//	private static void testeArquivos() {
//		String folderPath = "D:\\Documentos\\musicas\\musicas1\\";
//		folderPath = "C:\\Users\\spy\\Desktop\\musicas2";
//
//
//		ListaMusicas lm = new ListaMusicas();
//		lm.salvarListaMusicas(folderPath);
//
//		List<File> lista = lm.getListaMusicas();
//		lista.forEach(System.out::println);
//
//		PlayMp3 playmp3 = new PlayMp3(lista.get(3).getAbsolutePath());
//		playmp3.playPause();
//	}
//
//
//
//
//	private static void teste1(String[] args) {
//		String filename = args == null || args.length <= 0 ? null : args[0];
//		if (filename == null || filename.isEmpty()) {
//			filename = "D:\\Documentos\\musicas\\musicas2\\Rhye - Open.mp3";
//		}
//
//		PlayMp3 playmp3 = new PlayMp3(filename);
//
//		playmp3.playPause();
//		System.out.println(playmp3.toString());
//		p(3000);
//		System.out.println("-p");
//		playmp3.playPause();
//		p(3000);
////		playmp3.playPause();
////		p(3000);
////		playmp3.playPause(true);
//		System.out.println("-avancar");
//		playmp3.avancar(0);
//
//		p(3000);
//		System.out.println("-new");
//		playmp3.close();
//
//		filename = "D:\\Documentos\\musicas\\musicas2\\Brandon Flowers - Lonely Town.mp3";
//		playmp3 = new PlayMp3(filename);
//		playmp3.playPause();
//	}
//
//	private static void p(int t) {try { Thread.sleep(t); } catch (InterruptedException e) { }}

}

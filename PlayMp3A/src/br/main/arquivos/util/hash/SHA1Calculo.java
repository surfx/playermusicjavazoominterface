package br.main.arquivos.util.hash;

import java.io.File;
import java.io.Serializable;

import br.main.player.mp3propriedades.LerPropriedadesMp3;

public class SHA1Calculo implements Serializable {

	private static final long serialVersionUID = 1L;

	private static SHA1Calculo instance;
	
	public static SHA1Calculo getInstance() {
		if(instance==null) { instance = new SHA1Calculo(); }
		return instance;
	}
	
	private SHA1Calculo() {}
	
	//private MessageDigest dgst = null;
	
	/**
	 * Não calcula o hash porque senão seria muito demorado
	 * @param arq
	 * @return
	 */
	public String hashFile(File arq) {

		// o método para calcular o hash é muito demorado, por isso agora o hash será o nome do arquivo
		return LerPropriedadesMp3.tratarNomeMusica(arq);
		
//		try {
//			dgst = dgst == null ? MessageDigest.getInstance("SHA1") : dgst;
//			FileInputStream fis = new FileInputStream(arq);
//			byte[] buffer = new byte[20480];
//			int nBytes;
//			dgst.reset();
//			while ((nBytes = fis.read(buffer)) > 0) {
//				dgst.update(buffer, 0, nBytes);
//			}
//			byte[] bytes = dgst.digest();
//			fis.close();
//			return new BigInteger(bytes).toString(16);
//		} catch (NoSuchAlgorithmException ex) {
//		} catch (IOException ex) {
//		}
//		return null;
	}
	
}
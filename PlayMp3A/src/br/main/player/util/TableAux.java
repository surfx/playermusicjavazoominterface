package br.main.player.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import br.main.player.entidade.ArquivoMusica;
import br.main.player.mp3propriedades.LerPropriedadesMp3;

public class TableAux implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Object[][] data;
	private List<ArquivoMusica> listaMusicasAux;
	
	public List<ArquivoMusica> getListaMusicas() {
		return listaMusicasAux;
	}
	
	public void preencherTabelaMusicas(final List<String> listaMusicas, JTable table, boolean force) {
		if (table == null) { return; }
		Object[][] dataAux = toDataJTable(listaMusicas, force);
		String[] colunas = { "Artista", "Title" };

		table.clearSelection();

		DefaultTableModel model = new DefaultTableModel(dataAux, colunas);
		table.setModel(model);
		table.setRowSorter(new TableRowSorter<DefaultTableModel>(model)); // https://stackoverflow.com/questions/30644611/selected-row-in-jtable-along-with-sorting
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(205);
	}
	
	public void filtrar(final String filtro, JTable table) {
		if (table == null) { return; }
		Object[][] dataAux = filtrarData(filtro);
		String[] colunas = { "Artista", "Title" };

		table.clearSelection();

		DefaultTableModel model = new DefaultTableModel(dataAux, colunas);
		table.setModel(model);
		table.setRowSorter(new TableRowSorter<DefaultTableModel>(model)); // https://stackoverflow.com/questions/30644611/selected-row-in-jtable-along-with-sorting
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(205);
	}
	
	private Object[][] toDataJTable(List<String> listaMusicas, boolean force) {
		if (force) { data = null; listaMusicasAux = null; }
		if (data != null) { return data; }
		if (listaMusicas == null || listaMusicas.isEmpty()) { return null; }

		final int size = listaMusicas.size();
		data = new Object[size][2];
		List<ArquivoMusica> larquivosMusica = listaMusicas.stream().map(m -> new ArquivoMusica(m)).collect(Collectors.toList());
		return toDataJTableFromArquivoMusica(larquivosMusica, force);
	}

	private Object[][] toDataJTableFromArquivoMusica(List<ArquivoMusica> larquivosMusica, boolean force) {
		if (force) { data = null; listaMusicasAux = null; }
		if (data != null) { return data; }
		if (larquivosMusica == null || larquivosMusica.isEmpty()) { return null; }

		final int size = larquivosMusica.size();
		data = new Object[size][2];
		boolean add = listaMusicasAux == null || force;
		listaMusicasAux = add ? new ArrayList<ArquivoMusica>() : listaMusicasAux;

		for (int i = 0; i < size; i++) {
			ArquivoMusica m = larquivosMusica.get(i);

			// essa leitura e muito demorada :(
			//InformacoesMp3 info = null; // LerPropriedadesMp3.getInstance().getInformacoesMp3(f);
			//if (info != null) { // por enquanto...
			//	dados[i] = new Object[] { info.getArtista(), info.getTitulo() /* , f.getAbsolutePath() */ };
			//} else {
			String tituloMusica = m.getTituloMusica();
			data[i] = new Object[] { m, tituloMusica };
			if (add) { listaMusicasAux.add(m); }
		}
		return data;
	}
	
	private Object[][] filtrarData(final String filtro) {
		if (listaMusicasAux==null || listaMusicasAux.isEmpty()) { return null; }

		List<ArquivoMusica> aux = filtrarListaArquivoMusica(filtro);
		
		final int size = aux.size();
		Object[][] dataAux = new Object[size][2];

		for (int i = 0; i < size; i++) {
			ArquivoMusica m = aux.get(i);
			String tituloMusica = m.getTituloMusica();
			dataAux[i] = new Object[] { m, tituloMusica };
		}
		return dataAux;
	}

	private List<ArquivoMusica> filtrarListaArquivoMusica(final String filtro){
		if (listaMusicasAux==null || listaMusicasAux.isEmpty()) { return null; }
		return filtro == null || filtro.isEmpty() ? new ArrayList<ArquivoMusica>(listaMusicasAux) : listaMusicasAux.stream().filter(
				am -> am != null && am.existe() && !am.getNomeMusica().isEmpty()
				&& am.getNomeMusica().toLowerCase().contains(filtro.toLowerCase())).collect(Collectors.toList());
	}
	
	/**
	 * retorna o índice da música filtrada
	 * @param filtro
	 * @param musica
	 * @return
	 */
	public int getIndiceMusicaFiltrada(final String filtro, String musica) {
		if (listaMusicasAux==null || listaMusicasAux.isEmpty()) { return -1; }
		
		List<ArquivoMusica> aux = filtrarListaArquivoMusica(filtro);
		
		musica = tratarFiltroNomeMusica(musica);
		if (musica==null||musica.isEmpty()) {return -1;}
		for (int i = 0; i < aux.size(); i++) {
			ArquivoMusica m = aux.get(i);
			if (m==null) {continue;}
			String nm = m.getNomeMusica();
			if (nm==null||nm.isEmpty()) {continue;}
			if (nm.equalsIgnoreCase(musica)) {return i;}
		}
		
		return -1;
	}
	
	private String tratarFiltroNomeMusica(String musica) {
		musica = musica.replace("\\", "/");
		if (!musica.contains("/")) {return LerPropriedadesMp3.tratarNomeMusica(musica);}
		return LerPropriedadesMp3.tratarNomeMusica(musica.substring(musica.lastIndexOf("/")+1));
	}
	
}
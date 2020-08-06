package br.main.player.visual;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatLightLaf;

import br.main.Main;
import br.main.arquivos.ListaMusicas;
import br.main.player.PlayMp3;
import br.main.player.animacao.AnimacaoBarra;
import br.main.player.entidade.IndexFile;
import br.main.player.interfaces.IHandlerMusicPosition;
import br.main.player.interfaces.IHandlerMusicStopped;
import br.main.player.mp3propriedades.LerPropriedadesMp3;
import br.main.player.util.RandomNumber;
import br.main.player.util.TableAux;

public class JPlayerMp3 extends JFrame {

	private static final long serialVersionUID = 1L;
	private final String appname = "JPlayerMp3 1.0.0";

	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTable table;
	private JLabel lblStatus;
	private JTextField txtPesquisar;

	private Map<Integer, IndexFile> mapMusicas;

	private PlayMp3 playMp3;
	private AnimacaoBarra animacaobarra;
	private AtomicBoolean cancallplayfunction = new AtomicBoolean(true);

	/**
	 * Create the frame.
	 */
	public JPlayerMp3() {
		// https://mvnrepository.com/artifact/com.formdev/flatlaf/0.38, https://www.formdev.com/flatlaf/#download, https://www.formdev.com/flatlaf/themes/
		FlatLightLaf.install(); try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception ex) { }

		this.setResizable(false);

		JPlayerMp3 isto = this;

		URL local = Main.class.getClassLoader().getResource("cap-icon.png");
		if (local!=null) { ImageIcon icon = new ImageIcon(local); if (icon!=null) { this.setIconImage(icon.getImage()); } }

		this.setTitle(this.appname);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 685, 422);
		this.setLocationRelativeTo(null); // centraliza no meio da tela
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(null);
		this.setContentPane(this.contentPane);

		JButton btnAddPasta = new JButton("add pasta");
		btnAddPasta.setBounds(10, 11, 102, 23);
		btnAddPasta.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnAddPasta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser j = new JFileChooser();
				String pathinitial = ""; // new java.io.IndexFile(".") // start at application current directory
				pathinitial = System.getProperty("user.home") + "/Desktop";
				j.setCurrentDirectory(new File(pathinitial)); // start at application current directory
				j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				Integer returnVal = j.showSaveDialog(isto);
				// System.out.println("returnVal: " + returnVal);
				if (returnVal == JFileChooser.APPROVE_OPTION) {

					new Thread(new Runnable() {
						@Override
						public void run() {
							File f = j.getSelectedFile();
							List<IndexFile> musicas = new ListaMusicas().salvarListaMusicas(f.getAbsolutePath());
							JPlayerMp3.this.mapMusicas = TableAux.toMapMusicas(musicas); // atualiza a memória de músicas
							JPlayerMp3.this.preencherTabelaMusicas(musicas);
						}
					}).start();

				}
			}
		});
		this.contentPane.add(btnAddPasta);

		JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean playEstado = btnPlay.getText().equals("Play");
				btnPlay.setText(playEstado ? "Pause" : "Play");

				//if (playEstado) {
					if (JPlayerMp3.this.playMp3==null) {
						JPlayerMp3.this.playMusica(JPlayerMp3.this.getMusicaToPlay(), true, false);
						return;
					}
				//}
				JPlayerMp3.this.playMp3.playPause(); // o componente sabe em qual estado está
			}
		});
		btnPlay.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnPlay.setBounds(274, 11, 102, 23);
		this.contentPane.add(btnPlay);

		JButton btnAnterior = new JButton("<");
		btnAnterior.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnPlay.setText("Play");
				JPlayerMp3.this.playMusica(JPlayerMp3.this.getPreviousMusic(), true, false);
			}
		});
		btnAnterior.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnAnterior.setBounds(124, 11, 41, 23);
		this.contentPane.add(btnAnterior);

		JButton btnProxima = new JButton(">");
		btnProxima.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnPlay.setText("Play");
				JPlayerMp3.this.playMusica(JPlayerMp3.this.getNextMusic(), true, false);
			}
		});
		btnProxima.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnProxima.setBounds(172, 11, 41, 23);
		this.contentPane.add(btnProxima);

		JButton btnRandozimarListaMusicas = new JButton("r");
		btnRandozimarListaMusicas.setToolTipText("Randomizar Lista de M\u00FAsicas");
		btnRandozimarListaMusicas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnPlay.setText("Play");
				JPlayerMp3.this.playMusica(JPlayerMp3.this.getMusicAleatoria(), true, true);
			}
		});
		btnRandozimarListaMusicas.setBounds(223, 11, 41, 23);
		this.contentPane.add(btnRandozimarListaMusicas);

		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JPlayerMp3.this.playMp3 != null) {
					JPlayerMp3.this.playMp3.close();
				}
				JPlayerMp3.this.setTitle(JPlayerMp3.this.appname);
				JPlayerMp3.this.animacaobarra.preencherBarraStatus("");
				JPlayerMp3.this.cancallplayfunction.set(true);
			}
		});
		btnStop.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnStop.setBounds(386, 11, 102, 23);
		this.contentPane.add(btnStop);

		this.lblStatus = new JLabel("");
		this.lblStatus.setBounds(10, 371, 329, 14);
		this.contentPane.add(this.lblStatus);

		this.txtPesquisar = new JTextField();
		this.txtPesquisar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);

				if (JPlayerMp3.this.table.getRowCount() <= 0) { JPlayerMp3.this.preencherTabelaMusicas(); }
				if (JPlayerMp3.this.table.getRowCount() <= 0) {return;}
				//if (e.getKeyCode() != KeyEvent.VK_ENTER || JPlayerMp3.this.table.getRowCount() <= 0) { return; }
				JPlayerMp3.this.filtrarListaMusicas();
			}
		});
		this.txtPesquisar.setBounds(349, 369, 320, 20);
		this.contentPane.add(this.txtPesquisar);
		this.txtPesquisar.setColumns(10);

		this.table = new JTable() { private static final long serialVersionUID = 1L; @Override public boolean isCellEditable(int row, int column) { return false; } };
		this.table.setAutoCreateRowSorter(true); // sorting of the rows on a particular column
		this.table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				int row = JPlayerMp3.this.getRowSelecionado();
				if (mouseEvent.getClickCount() != 2 || JPlayerMp3.this.table.getSelectedRow() == -1 || row == -1) { //double click
					return;
				}

				JPlayerMp3.this.playMusica(JPlayerMp3.this.getMusicaToPlay(), true, true);
				JPlayerMp3.this.repaintThis();
			}
		});

		this.scrollPane = new JScrollPane(this.table);
		this.scrollPane.setBounds(10, 45, 659, 320);
		this.contentPane.add(this.scrollPane);

		this.animacaobarra = new AnimacaoBarra(this.lblStatus);

		this.preencherTabelaMusicas();
	}

	private void playMusica(IndexFile musicaToPlay, boolean playNext, boolean isRandom) {
		if (musicaToPlay == null) { this.cancallplayfunction.set(true); return; } // erro
		if (!this.cancallplayfunction.get()) { return; } // System.out.println("wait to play");
		this.cancallplayfunction.set(false);
		if (this.playMp3 != null) {
			this.playMp3.close();
		}

		final String nomeMusica = LerPropriedadesMp3.tratarNomeMusica(musicaToPlay.getFile().getName());
		this.setTitle(this.appname + " | " + nomeMusica);
		this.animacaobarra.preencherBarraStatus(nomeMusica);

		this.playMp3 = new PlayMp3(musicaToPlay.getFile().getAbsolutePath(), new IHandlerMusicStopped() {
			@Override
			public void executarHandler() {
				JPlayerMp3.this.setTitle(JPlayerMp3.this.appname);
				//System.out.println("Música has ended");
				if (!playNext) { return; }
				JPlayerMp3.this.playMusica(isRandom ? JPlayerMp3.this.getMusicAleatoria() : JPlayerMp3.this.getNextMusic(), playNext, isRandom);
			}
		}, new IHandlerMusicPosition() {
			@Override
			public void setPosition(int posicao) {
				JPlayerMp3.this.animacaobarra.setPosicaoMusicaHandler(posicao);
			}
		});
		this.playMp3.playPause(true);
		this.cancallplayfunction.set(true);
		this.repaint();
	}

	private void preencherTabelaMusicas() {

		List<IndexFile> listaMusicas = null;
		if (this.mapMusicas==null||this.mapMusicas.isEmpty()) {
			listaMusicas = new ListaMusicas().getListaMusicas();
			this.mapMusicas = TableAux.toMapMusicas(listaMusicas);
		} else {
			listaMusicas = TableAux.toListMusicas(this.mapMusicas);
		}

		this.preencherTabelaMusicas(listaMusicas);
	}

	private void preencherTabelaMusicas(final List<IndexFile> listaMusicas) {
		//this.mapMusicas = TableAux.toMapMusicas(listaMusicas);
		Object[][] dados = TableAux.getDadosJTable(listaMusicas);
		String[] colunas = { "#", "Artista", "Música" /* , "Caminho" */ };

		if (this.table != null) {
			this.table.clearSelection();
		}

		DefaultTableModel model = new DefaultTableModel(dados, colunas);
		this.table.setModel(model);
		this.table.getColumnModel().getColumn(0).setPreferredWidth(5);
		this.table.getColumnModel().getColumn(1).setPreferredWidth(205);
		this.table.getColumnModel().getColumn(2).setPreferredWidth(200);

		this.repaintThis();
	}

	private void repaintThis() {
		this.table.repaint();
		this.contentPane.repaint();
		this.validate();
		this.repaint();
	}

	//-------------
	private int getRowSelecionado() {
		if (this.table==null) {return -1;}
		return JPlayerMp3.this.table.getSelectedRow();
	}

	private Integer getIndiceMusica() {return this.getIndiceMusica(this.getRowSelecionado());}
	private Integer getIndiceMusica(int row) {
		if (row<0) {row=0;}
		if (this.table == null || row < 0 || row >= this.table.getRowCount()) { return null; }
		Object oindice = JPlayerMp3.this.table.getValueAt(row, 0);
		if (oindice==null) {return null;}
		return Integer.parseInt(oindice+"")-1;
	}

	private IndexFile getMusicaToPlay() { Integer indice = this.getIndiceMusica(); return indice==null ? null: this.getMusicaToPlay(indice); }
	private IndexFile getMusicaToPlay(int indice) {
		if (this.mapMusicas==null||this.mapMusicas.isEmpty()||indice<0||indice>=this.mapMusicas.size()||!this.mapMusicas.containsKey(indice)) {return null;}
		return this.mapMusicas.get(indice);
	}

	private IndexFile getPreviousMusic() {
		int row = JPlayerMp3.this.getRowSelecionado();
		if (row < 0) { row = 0; } // não selecionou nenhum
		row--;
		if (row < 0) { row = JPlayerMp3.this.table.getRowCount() - 1; }
		if (row >= JPlayerMp3.this.table.getRowCount()) { row = 0; }

		Integer indice = JPlayerMp3.this.getIndiceMusica(row);
		if (indice == null) { return null; }
		JPlayerMp3.this.selecionarMusicaTable(row); //seleciona a música na table
		return JPlayerMp3.this.getMusicaToPlay(indice);
	}

	private IndexFile getNextMusic() {
		int row = JPlayerMp3.this.getRowSelecionado();
		if (row < 0) { row = 0; } // não selecionou nenhum
		row++;
		if (row < 0) { row = JPlayerMp3.this.table.getRowCount() - 1; }
		if (row >= JPlayerMp3.this.table.getRowCount()) { row = 0; }

		Integer indice = JPlayerMp3.this.getIndiceMusica(row);
		if (indice == null) { return null; }
		JPlayerMp3.this.selecionarMusicaTable(row); //seleciona a música na table
		return JPlayerMp3.this.getMusicaToPlay(indice);
	}

	private IndexFile getMusicAleatoria() {
		int rowAleatorio = 0;

		final int rowAtualSelecionado = this.getRowSelecionado();
		do {
			rowAleatorio = RandomNumber.getRandomNumber(0, JPlayerMp3.this.table.getRowCount() - 1);
		} while(rowAleatorio==rowAtualSelecionado);

		Integer indice = JPlayerMp3.this.getIndiceMusica(rowAleatorio);
		if (indice == null) { return null; }
		JPlayerMp3.this.selecionarMusicaTable(rowAleatorio); //seleciona a música na table
		return JPlayerMp3.this.getMusicaToPlay(indice);
	}

//	private void selecinarMusicaAtualTable() {
//		this.selecionarMusicaTable(this.getRowSelecionado());
//	}

	private void selecionarMusicaTable(int row) {
		if(row<0) {return;}
		// seleciona a música na tabela
		this.table.clearSelection();
		this.table.setRowSelectionInterval(row, row);
		this.table.scrollRectToVisible(this.table.getCellRect(this.table.getSelectedRow(), 0, true)); // move o scroll até ela
		this.repaintThis();
	}

	private void filtrarListaMusicas() {
		List<IndexFile> listaMusicas = TableAux.toListMusicas(this.mapMusicas);
		if (listaMusicas==null||listaMusicas.isEmpty()) {JPlayerMp3.this.preencherTabelaMusicas(); return;}

		String filtrar = JPlayerMp3.this.txtPesquisar.getText();
		if (filtrar == null || filtrar.isEmpty() /* || filtrar.length() < 2 */) { JPlayerMp3.this.preencherTabelaMusicas(listaMusicas); return; }
		filtrar = filtrar.toLowerCase();
		// System.out.println("Filtrar: " + filtrar);

		List<IndexFile> listatemporaria = new ArrayList<IndexFile>();
		for (IndexFile f : listaMusicas) {
			if (f == null || !f.getFile().exists()) {
				continue;
			}
			String nome = LerPropriedadesMp3.tratarNomeMusica(f.getFile());
			if (nome == null || nome.isEmpty()) { continue; }
			if (!nome.toLowerCase().contains(filtrar)) { continue; }
			listatemporaria.add(f);
		}
		JPlayerMp3.this.preencherTabelaMusicas(listatemporaria);
	}

}

/*
 *
 * // public static void main(String[] args) { // EventQueue.invokeLater(new
 * Runnable() { // public void run() { // try { // JPlayerMp3 frame = new
 * JPlayerMp3(); // frame.setVisible(true); // } catch (Exception e) { //
 * e.printStackTrace(); // } // } // }); // }
 *
 * Click no header de uma coluna da table
 *
 * table.getTableHeader().addMouseListener(new MouseAdapter() {
 *
 * @Override public void mouseClicked(MouseEvent mouseEvent) {
 *
 * //não precisa se preocupar com a ordenação pois o double click pega pelo
 * número do índice da lista, que está no índice 0 da JTable
 *
 * // // necessário reordenar a lista lógica de acordo com a ordem da lista no
 * JTable // // int rowCount = table.getRowCount(); // int colCount =
 * table.getColumnCount(); // //// System.out.println("rowCount: " + rowCount);
 * //// System.out.println("colCount: " + colCount); // // Point point =
 * mouseEvent.getPoint(); // int row = table.rowAtPoint(point); // int col =
 * table.columnAtPoint(point); // System.out.println("row: " + row + ", col: " +
 * col); } });
 *
 */

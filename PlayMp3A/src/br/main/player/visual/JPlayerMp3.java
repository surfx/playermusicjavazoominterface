package br.main.player.visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

import javax.swing.BorderFactory;
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

import br.main.arquivos.ListaMusicas;
import br.main.player.PlayMp3;
import br.main.player.animacao.AnimacaoBarra;
import br.main.player.entidade.IndexFile;
import br.main.player.interfaces.IHandlerMusicPosition;
import br.main.player.interfaces.IHandlerMusicStopped;
import br.main.player.mp3propriedades.LerPropriedadesMp3;
import br.main.player.util.Propriedades;
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

		URL local = JPlayerMp3.class.getResource("/icones/cap-icon.png");
		if (local!=null) { ImageIcon icon = new ImageIcon(local); if (icon!=null) { this.setIconImage(icon.getImage()); } }

		//this.setResizable(false);
		this.setTitle(this.appname);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension d = Propriedades.getWH();
		int w = d==null||d.getWidth()<=0.0? 900: (int) Math.round(d.getWidth());
		int h = d==null||d.getHeight()<=0.0? 650: (int) Math.round(d.getHeight());
		this.setBounds(100, 100, w, h);
		this.setLocationRelativeTo(null); // centraliza no meio da tela
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));

		adicionarBotoes(contentPane);
		addJtable(contentPane);
		addRodape(contentPane);

		this.setContentPane(this.contentPane);

		this.animacaobarra = new AnimacaoBarra(this.lblStatus);

		this.preencherTabelaMusicas();
		
		final JPlayerMp3 isto = this;
		addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
            	Propriedades.salvarWH(isto.getWidth(), isto.getHeight());
                System.exit(0);
            }
        });
	}

	// #region botões
	@FunctionalInterface
	private interface IButtonBase<T, U, V, R> {
		public R apply(T t, U u, V v);
	}

	private void adicionarBotoes(final JPanel jpanel) {
		IButtonBase<String, Integer, Integer, JButton> buttonBase = (icone, width, height) -> {
			JButton btn = new JButton("");
			btn.setSize(width, height);
			btn.setIcon(new ImageIcon(JPlayerMp3.class.getResource(icone)));
			btn.setFont(new Font("Tahoma", Font.PLAIN, 11));
			btn.setBackground(Color.WHITE);
			btn.setBorder(BorderFactory.createEmptyBorder());
			btn.setContentAreaFilled(false);
			btn.setFocusPainted(false);
			return btn;
		};

		final JPlayerMp3 isto = this;
		// #region btnAddPasta
		JButton btnAddPasta = buttonBase.apply("/icones/player/icons8-pasta-16.png", 25, 23);
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
							JPlayerMp3.this.mapMusicas = TableAux.toMapMusicas(musicas); // atualiza a memoria de musicas
							JPlayerMp3.this.preencherTabelaMusicas(musicas);
						}
					}).start();

				}
			}
		});
		// #endregion

		// #region btnPlay
		ImageIcon imgPlay = new ImageIcon(JPlayerMp3.class.getResource("/icones/player/icons8-reproduzir-16.png"));
		ImageIcon imgPause = new ImageIcon(JPlayerMp3.class.getResource("/icones/player/icons8-pausa-16.png"));
		JButton btnPlay = buttonBase.apply("/icones/player/icons8-reproduzir-16.png", 25, 23);
		btnPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!existemMusicas()) {
					btnPlay.setIcon(imgPlay);
					return;
				}
				boolean playEstado = btnPlay.getIcon().toString() == imgPlay.toString();
				btnPlay.setIcon(playEstado ? imgPause : imgPlay);
				//if (playEstado) {
					if (JPlayerMp3.this.playMp3==null) {
						JPlayerMp3.this.playMusica(JPlayerMp3.this.getMusicaToPlay(), true, false);
						return;
					}
				//}
				JPlayerMp3.this.playMp3.playPause(); // o componente sabe em qual estado está
			}
		});
		// #endregion

		// #region btnAnterior
		JButton btnAnterior = buttonBase.apply("/icones/player/icons8-voltar-16.png", 25, 23);
		btnAnterior.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnPlay.setIcon(imgPlay);
				if (!existemMusicas()) {return;}
				JPlayerMp3.this.playMusica(JPlayerMp3.this.getPreviousMusic(), true, false);
			}
		});
		// #endregion

		// #region btnProxima
		JButton btnProxima = buttonBase.apply("/icones/player/icons8-avançar-16.png", 25, 23);
		btnProxima.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnPlay.setIcon(imgPlay);
				if (!existemMusicas()) {return;}
				JPlayerMp3.this.playMusica(JPlayerMp3.this.getNextMusic(), true, false);
			}
		});
		// #endregion

		// #region btnRandomizarListaMusicas
		JButton btnRandomizarListaMusicas = buttonBase.apply("/icones/player/icons8-embaralhar-16.png", 25, 23);
		btnRandomizarListaMusicas.setToolTipText("Randomizar Lista de M\u00FAsicas");
		btnRandomizarListaMusicas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!existemMusicas()) { return; }
				IndexFile indice = JPlayerMp3.this.getMusicAleatoria();
				if (indice==null) {return;}
				btnPlay.setIcon(imgPlay);
				JPlayerMp3.this.playMusica(indice, true, true);
			}
		});
		// #endregion

		// #region btnStop
		JButton btnStop = buttonBase.apply("/icones/player/icons8-parar-16.png", 25, 23);
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
		// #endregion


		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));

		panel.add(btnAddPasta);
		panel.add(btnAnterior);
		panel.add(btnProxima);
		panel.add(btnRandomizarListaMusicas);
		panel.add(btnPlay);
		panel.add(btnStop);

		jpanel.add(panel, BorderLayout.NORTH);
	}
	// #endregion

	// #region Jtable
	private void addJtable(final JPanel jpanel) {
		this.table = new JTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
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
		// this.contentPane.add(this.scrollPane);

		preencherTabelaMusicas();

		jpanel.add(scrollPane, BorderLayout.CENTER);
	}
	// #endregion
	
	//#region rodapé
	private void addRodape(final JPanel jpanel) {

		this.lblStatus = new JLabel("");
		//lblStatus.setSize(329, 14);

		this.txtPesquisar = new JTextField();
		//txtPesquisar.setSize(400, 20);
		//this.txtPesquisar.setColumns(10);
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

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BorderLayout(100, 0));

		panel.add(lblStatus, BorderLayout.WEST);
		panel.add(this.txtPesquisar, BorderLayout.CENTER);

		jpanel.add(panel, BorderLayout.SOUTH);
	}
	//#endregion
	
	
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
				if (!existemMusicas()) { return; }
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
		String[] colunas = { "#", "Artista", "Music" /* , "Caminho" */ };

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
		if (row < 0) { row = 0; } // nao selecionou nenhum
		row--;
		if (row < 0) { row = JPlayerMp3.this.table.getRowCount() - 1; }
		if (row >= JPlayerMp3.this.table.getRowCount()) { row = 0; }

		Integer indice = JPlayerMp3.this.getIndiceMusica(row);
		if (indice == null) { return null; }
		JPlayerMp3.this.selecionarMusicaTable(row); //seleciona a musica na table
		return JPlayerMp3.this.getMusicaToPlay(indice);
	}

	private IndexFile getNextMusic() {
		int row = JPlayerMp3.this.getRowSelecionado();
		if (row < 0) { row = 0; } // nao selecionou nenhum
		row++;
		if (row < 0) { row = JPlayerMp3.this.table.getRowCount() - 1; }
		if (row >= JPlayerMp3.this.table.getRowCount()) { row = 0; }

		Integer indice = JPlayerMp3.this.getIndiceMusica(row);
		if (indice == null) { return null; }
		JPlayerMp3.this.selecionarMusicaTable(row); //seleciona a musica na table
		return JPlayerMp3.this.getMusicaToPlay(indice);
	}

	private IndexFile getMusicAleatoria() {
		if (!existemMusicas()) { return null; }
		int rowAleatorio = 0;

		final int rowAtualSelecionado = this.getRowSelecionado();
		if (JPlayerMp3.this.table.getRowCount() >= 2) {
			do {
				rowAleatorio = RandomNumber.getRandomNumber(0, JPlayerMp3.this.table.getRowCount() - 1);
			} while(rowAleatorio==rowAtualSelecionado);
		}

		Integer indice = JPlayerMp3.this.getIndiceMusica(rowAleatorio);
		if (indice == null) { return null; }
		JPlayerMp3.this.selecionarMusicaTable(rowAleatorio); //seleciona a musica na table
		return JPlayerMp3.this.getMusicaToPlay(indice);
	}

//	private void selecinarMusicaAtualTable() {
//		this.selecionarMusicaTable(this.getRowSelecionado());
//	}

	private void selecionarMusicaTable(int row) {
		if(row<0) {return;}
		// seleciona a musica na tabela
		this.table.clearSelection();
		this.table.setRowSelectionInterval(row, row);
		this.table.scrollRectToVisible(this.table.getCellRect(this.table.getSelectedRow(), 0, true)); // move o scroll ate ela
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

	private boolean existemMusicas() {
		return JPlayerMp3.this.table != null && JPlayerMp3.this.table.getRowCount() > 0;
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
 * //nao precisa se preocupar com a ordenacao pois o double click pega pelo
 * numero do indice da lista, que esta no indice 0 da JTable
 *
 * // // necessario reordenar a lista logica de acordo com a ordem da lista no
 * JTable // // int rowCount = table.getRowCount(); // int colCount =
 * table.getColumnCount(); // //// System.out.println("rowCount: " + rowCount);
 * //// System.out.println("colCount: " + colCount); // // Point point =
 * mouseEvent.getPoint(); // int row = table.rowAtPoint(point); // int col =
 * table.columnAtPoint(point); // System.out.println("row: " + row + ", col: " +
 * col); } });
 *
 */

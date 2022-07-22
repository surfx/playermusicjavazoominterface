package br.main.player.visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatLightLaf;

import br.main.arquivos.ListaMusicas;
import br.main.player.PlayMp3;
import br.main.player.animacao.AnimacaoBarra;
import br.main.player.entidade.ArquivoMusica;
import br.main.player.interfaces.IHandlerMusicPosition;
import br.main.player.interfaces.IHandlerMusicStopped;
import br.main.player.util.Propriedades;
import br.main.player.util.RandomNumber;
import br.main.player.util.TableAux;

public class JPlayerMp3 extends JFrame implements KeyListener {

	private static final long serialVersionUID = 1L;
	private final String appname = "JPlayerMp3 1.1.0";

	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTable table;
	private JLabel lblStatus;
	private JTextField txtPesquisar;
	private JButton btnPlay;

	//private Map<Integer, IndexFile> mapMusicas;
	private TableAux tableAux = null;

	private PlayMp3 playMp3;
	private AnimacaoBarra animacaobarra;
	private AtomicBoolean cancallplayfunction = new AtomicBoolean(true);

	public JPlayerMp3() {
		// https://mvnrepository.com/artifact/com.formdev/flatlaf/0.38, https://www.formdev.com/flatlaf/#download, https://www.formdev.com/flatlaf/themes/
		FlatLightLaf.install(); try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception ex) { }

		URL local = JPlayerMp3.class.getResource("/icones/cap-icon.png");
		if (local!=null) { ImageIcon icon = new ImageIcon(local); if (icon!=null) { this.setIconImage(icon.getImage()); } }

		//this.setResizable(false);
		this.setTitle(this.appname);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.posicaoInicial();

		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));

		adicionarBotoes(contentPane);
		addJtable(contentPane);
		addRodape(contentPane);

		this.preencherTabelaMusicas(true);
		this.setContentPane(this.contentPane);

		this.animacaobarra = new AnimacaoBarra(this.lblStatus);
		
		final JPlayerMp3 isto = this;
		addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
            	Propriedades.salvar(isto.getX(), isto.getY(), isto.getWidth(), isto.getHeight());
                System.exit(0);
            }
        });

		addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
		
		this.repaintThis();
	}
	
	private void posicaoInicial() {
		Rectangle rect = Propriedades.getRectangle();
		int x = rect==null||rect.getX()<=0.0? 100: (int) Math.round(rect.getX());
		int y = rect==null||rect.getY()<=0.0? 100: (int) Math.round(rect.getY());
		int w = rect==null||rect.getWidth()<=0.0? 900: (int) Math.round(rect.getWidth());
		int h = rect==null||rect.getHeight()<=0.0? 650: (int) Math.round(rect.getHeight());
		this.setBounds(x, y, w, h);
		if (rect == null) {
			this.setLocationRelativeTo(null); // centraliza no meio da tela	
		}
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

		// #region btnAddPasta
		JButton btnAddPasta = buttonBase.apply("/icones/player/icons8-pasta-16.png", 25, 23);
		btnAddPasta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selecionarMusicasPasta();
			}
		});
		// #endregion

		// #region btnPlay
		ImageIcon imgPlay = new ImageIcon(JPlayerMp3.class.getResource("/icones/player/icons8-reproduzir-16.png"));
		btnPlay = buttonBase.apply("/icones/player/icons8-reproduzir-16.png", 25, 23);
		btnPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnPlayMusic(btnPlay, imgPlay);
			}
		});
		// #endregion

		// #region btnAnterior
		JButton btnAnterior = buttonBase.apply("/icones/player/icons8-voltar-16.png", 25, 23);
		btnAnterior.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnAnteriorMusic(btnPlay, imgPlay);
			}
		});
		// #endregion

		// #region btnProxima
		JButton btnProxima = buttonBase.apply("/icones/player/icons8-avançar-16.png", 25, 23);
		btnProxima.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnProximaMusic(btnPlay, imgPlay);
			}
		});
		// #endregion

		// #region btnRandomizarListaMusicas
		JButton btnRandomizarListaMusicas = buttonBase.apply("/icones/player/icons8-embaralhar-16.png", 25, 23);
		btnRandomizarListaMusicas.setToolTipText("Randomizar Lista de M\u00FAsicas");
		btnRandomizarListaMusicas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnRandomizarMusic(btnPlay, imgPlay);
			}
		});
		// #endregion

		// #region btnStop
		JButton btnStop = buttonBase.apply("/icones/player/icons8-parar-16.png", 25, 23);
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnStopMusic(btnPlay, imgPlay);
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
				clickJTable(mouseEvent);
			}
		});

		this.scrollPane = new JScrollPane(this.table);
		this.scrollPane.setBounds(10, 45, 659, 320);

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
				txtPesquisar(e);
			}
		});

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, lblStatus, txtPesquisar);
		splitPane.setOneTouchExpandable(false);
		splitPane.setResizeWeight(0.4);
		jpanel.add(splitPane, BorderLayout.SOUTH);
	}
	//#endregion
	
	private void selecionarMusicasPasta() {
		JFileChooser j = new JFileChooser();
		String pathinitial = ""; // new java.io.IndexFile(".") // start at application current directory
		pathinitial = System.getProperty("user.home") + "/Desktop";
		j.setCurrentDirectory(new File(pathinitial)); // start at application current directory
		j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		Integer returnVal = j.showSaveDialog(this);
		// System.out.println("returnVal: " + returnVal);
		if (returnVal != JFileChooser.APPROVE_OPTION) { return; }
		new Thread(new Runnable() {
			@Override
			public void run() {
				File f = j.getSelectedFile();
				List<String> musicas = new ListaMusicas().salvarListaMusicas(f.getAbsolutePath());
				//JPlayerMp3.this.mapMusicas = TableAux.toMapMusicas(musicas); // atualiza a memoria de musicas
				JPlayerMp3.this.preencherTabelaMusicas(musicas, true);
			}
		}).start();
	}
	
	private void btnPlayMusic(JButton btnPlay, ImageIcon imgPlay) {
		ImageIcon imgPause = new ImageIcon(JPlayerMp3.class.getResource("/icones/player/icons8-pausa-16.png"));
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
	
	private void btnAnteriorMusic(JButton btnPlay, ImageIcon imgPlay) {
		btnPlay.setIcon(imgPlay);
		if (!existemMusicas()) {return;}
		this.playMusica(this.getPreviousMusic(), true, false);
	}
	
	private void btnProximaMusic(JButton btnPlay, ImageIcon imgPlay) {
		btnPlay.setIcon(imgPlay);
		if (!existemMusicas()) {return;}
		this.playMusica(this.getNextMusic(), true, false);
	}
	
	private void btnRandomizarMusic(JButton btnPlay, ImageIcon imgPlay) {
		if (!existemMusicas()) { return; }
		ArquivoMusica arquivoMusica = this.getMusicAleatoria();
		if (arquivoMusica==null) {return;}
		btnPlay.setIcon(imgPlay);
		this.playMusica(arquivoMusica, true, true);
	}
	
	private void btnStopMusic(JButton btnPlay, ImageIcon imgPlay) {
		btnPlay.setIcon(imgPlay);
		if (this.playMp3 != null) {
			this.playMp3.close();
		}
		this.setTitle(JPlayerMp3.this.appname);
		this.animacaobarra.preencherBarraStatus("");
		this.cancallplayfunction.set(true);
	}
	
	private ArquivoMusica getArquivoMusica(int row) {
		return (ArquivoMusica)table.getModel().getValueAt(row, 0);
	}
	
	private void clickJTable(MouseEvent mouseEvent) {
		if (mouseEvent.getClickCount() != 2 || this.table.getSelectedRow() == -1) { //double click
			return;
		}
		int row = this.getRowSelecionado();
		if (row < 0) {return;}
		row = table.getRowSorter().convertRowIndexToModel(row); // https://stackoverflow.com/questions/30644611/selected-row-in-jtable-along-with-sorting
		playRow(row);
	}
	
	private void playRow(int row) {
		if (row < 0) {return;}
		ArquivoMusica arquivo = (ArquivoMusica)table.getModel().getValueAt(row, 0);
		if (arquivo==null) {return;}
		this.playMusica(arquivo, true, true);
	}
	
	private void txtPesquisar(KeyEvent e) {
		 if (this.table.getRowCount() <= 0) { this.preencherTabelaMusicas(true); }
		 if (this.table.getRowCount() <= 0) { return; }
		 
		 if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			 playRow(0);
		 }
		 
		 //if (e.getKeyCode() != KeyEvent.VK_ENTER || this.table.getRowCount() <= 0) { return; }
		 tableAux.filtrar(this.txtPesquisar.getText(), table);
		 
		 table.clearSelection();
		 // selecionar a música pelo nome
		 String musica = playMp3 == null ? null : playMp3.getFilename(); // música que estava tocando
		 if (musica == null) { return; }
		 int indiceMusicaFiltrada = tableAux.getIndiceMusicaFiltrada(this.txtPesquisar.getText(), musica);
		 if (indiceMusicaFiltrada < 0) { return; } // -1 não achou na lista para o filtro informado
		 //System.out.println("musica tocando: " + musica + ", " + indiceMusicaFiltrada);

		 //table.setRowSelectionInterval(indiceMusicaFiltrada,indiceMusicaFiltrada);
		 selecionarMusicaTable(indiceMusicaFiltrada);
	}
	
	private void playMusica(ArquivoMusica arquivoMusica, boolean playNext, boolean isRandom) {
		if (arquivoMusica == null || !arquivoMusica.existe()) { this.cancallplayfunction.set(true); return; } // erro
		if (!this.cancallplayfunction.get()) { return; } // System.out.println("wait to play");
		this.cancallplayfunction.set(false);
		if (this.playMp3 != null) {
			this.playMp3.close();
		}
		
		final String nomeMusica = arquivoMusica.getNomeMusica();
		this.setTitle(this.appname + " | " + nomeMusica);
		this.animacaobarra.preencherBarraStatus(nomeMusica);

		this.playMp3 = new PlayMp3(arquivoMusica.getPath(), new IHandlerMusicStopped() {
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

	private void preencherTabelaMusicas(boolean force) {
		this.preencherTabelaMusicas(new ListaMusicas().getListaMusicas(), force);
	}

	private void preencherTabelaMusicas(final List<String> listaMusicas, boolean force) {
		if (tableAux == null) { tableAux = new TableAux(); }
		tableAux.preencherTabelaMusicas(listaMusicas, this.table, force);
		this.repaintThis();
	}

	//-------------
	private int getRowSelecionado() {
		return table == null ? -1 : table.getSelectedRow();
	}

	private ArquivoMusica getPreviousMusic() {
		int row = this.getRowSelecionado();
		if (row < 0) { row = 0; } // nao selecionou nenhum
		row--;
		if (row < 0) { row = this.table.getRowCount() - 1; }
		if (row >= this.table.getRowCount()) { row = 0; }

		ArquivoMusica arquivoMusica = getArquivoMusica(row);
		if (arquivoMusica == null) { return null; }
		this.selecionarMusicaTable(row); //seleciona a musica na table
		return arquivoMusica;
	}

	private ArquivoMusica getNextMusic() {
		int row = JPlayerMp3.this.getRowSelecionado();
		if (row < 0) { row = 0; } // nao selecionou nenhum
		row++;
		if (row < 0) { row = this.table.getRowCount() - 1; }
		if (row >= this.table.getRowCount()) { row = 0; }

		ArquivoMusica arquivoMusica = getArquivoMusica(row);
		if (arquivoMusica == null) { return null; }
		this.selecionarMusicaTable(row); //seleciona a musica na table
		return arquivoMusica;
	}

	private ArquivoMusica getMusicAleatoria() {
		if (!existemMusicas()) { return null; }
		int rowAleatorio = 0;

		final int rowAtualSelecionado = this.getRowSelecionado();
		if (JPlayerMp3.this.table.getRowCount() >= 2) {
			do {
				rowAleatorio = RandomNumber.getRandomNumber(0, JPlayerMp3.this.table.getRowCount() - 1);
			} while(rowAleatorio==rowAtualSelecionado);
		}

		ArquivoMusica arquivoMusica = getArquivoMusica(rowAleatorio);
		if (arquivoMusica == null) { return null; }
		this.selecionarMusicaTable(rowAleatorio); //seleciona a musica na table
		return arquivoMusica;
	}

	private ArquivoMusica getMusicaToPlay() { int indice = this.getRowSelecionado(); return this.getMusicaToPlay(indice); }
	private ArquivoMusica getMusicaToPlay(int indice) {
		if (indice < 0) {indice = 0;}
		return getArquivoMusica(indice);
	}

	private void selecionarMusicaTable(int row) {
		if (row < 0) {return;}
		// seleciona a musica na tabela
		this.table.clearSelection();
		this.table.setRowSelectionInterval(row, row);
		this.table.scrollRectToVisible(this.table.getCellRect(this.table.getSelectedRow(), 0, true)); // move o scroll ate ela
		this.repaintThis();
	}

	private boolean existemMusicas() {
		return this.table != null && this.table.getRowCount() > 0;
	}
	
	private void repaintThis() {
		this.table.repaint();
		this.contentPane.repaint();
		this.validate();
		this.repaint();
	}
	
	// --- KeyListener
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		ImageIcon imgPlay = new ImageIcon(JPlayerMp3.class.getResource("/icones/player/icons8-reproduzir-16.png"));
		switch (e.getKeyCode()) {
			case KeyEvent.VK_R:
				this.btnRandomizarMusic(this.btnPlay, imgPlay);
				break;
			case KeyEvent.VK_RIGHT:
				this.btnProximaMusic(this.btnPlay, imgPlay);
				break;
			case KeyEvent.VK_LEFT:
				this.btnAnteriorMusic(this.btnPlay, imgPlay);
				break;
			case KeyEvent.VK_ENTER:
				this.btnPlayMusic(this.btnPlay, imgPlay);
				break;
			default:
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
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

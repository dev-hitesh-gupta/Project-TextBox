package gui;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.UndoManager;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import javax.swing.ScrollPaneConstants;

@SuppressWarnings("serial")
public class TextBox extends JFrame {

	private JPanel contentPane;
	JSplitPane splitPane;
	JScrollPane scrollPane_1;
	JScrollPane scrollPane;
	JTextPane textPane = new JTextPane();;
	JEditorPane textPane_dict;
	private JFileChooser dialogFileChooser = new JFileChooser(System.getProperty("user.dir"));
	private String currentFile = "Untitled";
	private boolean changed = false;
	protected UndoManager undo = new UndoManager();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					TextBox frame = new TextBox();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public TextBox() throws IOException {
		setFont(new Font("Arial Black", Font.BOLD, 14));
		setTitle("LemmaPad");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(ImageIO.read(TextBox.class.getResourceAsStream("icon/textbox.png")));
		setBounds(100, 100, 700, 600);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mnFile.add(Open);
		mnFile.add(Save);
		mnFile.add(SaveAs);
		mnFile.add(Quit);
		mnFile.getItem(0).setIcon(new ImageIcon(ImageIO.read(TextBox.class.getResourceAsStream("icon/open.png"))));
		mnFile.getItem(1).setIcon(new ImageIcon(ImageIO.read(TextBox.class.getResourceAsStream("icon/save.png"))));
		mnFile.getItem(2).setIcon(new ImageIcon(ImageIO.read(TextBox.class.getResourceAsStream("icon/saveas.png"))));
		mnFile.getItem(3).setIcon(new ImageIcon(ImageIO.read(TextBox.class.getResourceAsStream("icon/exit.png"))));
		Save.setEnabled(false);
		SaveAs.setEnabled(false);
		
		mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
	
		mnEdit.add(Cut);
		mnEdit.add(Copy);
		mnEdit.add(Paste);

		mnEdit.getItem(0).setText("Cut");
		mnEdit.getItem(0).setMnemonic(KeyEvent.VK_X);
		mnEdit.getItem(0).setIcon(new ImageIcon(ImageIO.read(TextBox.class.getResourceAsStream("icon/cut.png"))));
		mnEdit.getItem(0).setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		
		mnEdit.getItem(1).setText("Copy");
		mnEdit.getItem(1).setMnemonic(KeyEvent.VK_C);
		mnEdit.getItem(1).setIcon(new ImageIcon(ImageIO.read(TextBox.class.getResourceAsStream("icon/copy.png"))));
		mnEdit.getItem(1).setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		
		mnEdit.getItem(2).setText("Paste");
		mnEdit.getItem(2).setMnemonic(KeyEvent.VK_V);
		mnEdit.getItem(2).setIcon(new ImageIcon(ImageIO.read(TextBox.class.getResourceAsStream("icon/paste.png"))));
		mnEdit.getItem(2).setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		JCheckBox chckbxDictionary = new JCheckBox("Dictionary");
		chckbxDictionary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxDictionary.isSelected()){
					scrollPane_1.setVisible(true);
					splitPane.setDividerLocation((int)(getSize().getWidth())-(int)(getSize().getWidth()/2));
				}
				else
				{
					scrollPane_1.setVisible(false);
					splitPane.setDividerLocation((int)getSize().getWidth());
				}
				
				
			}
		});
		mnView.add(chckbxDictionary);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		splitPane.setLeftComponent(scrollPane);
		textPane.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		scrollPane.setViewportView(textPane);
		textPane.addKeyListener(k1);
		InputMap inputMap = textPane.getInputMap();
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_D,Event.CTRL_MASK);
		inputMap.put(key, Dict);
		
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane_1.setViewportBorder(new TitledBorder(new LineBorder(new Color(64, 64, 64), 2, true), "WordNet Dictionary", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setRightComponent(scrollPane_1);
		
		textPane_dict = new JEditorPane();
		textPane_dict.setToolTipText("WordNet Dictionary");
		textPane_dict.setBackground(Color.LIGHT_GRAY);
		textPane_dict.setForeground(Color.BLUE);
		textPane_dict.setFont(new Font("Times New Roman", Font.BOLD, 17));
		textPane_dict.setEditable(false);
		scrollPane_1.setViewportView(textPane_dict);
		splitPane.setDividerLocation((int)getSize().getWidth());
		splitPane.setDividerSize(0);
	}
	private KeyListener k1 = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			changed = true;
			Save.setEnabled(true);
			SaveAs.setEnabled(true);
		}
	};

	Action Dict = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		textPane_dict.setText(Dictionary(textPane.getSelectedText()));
		}
	};
	
	
	ActionMap map = textPane.getActionMap();
	Action Cut = map.get(DefaultEditorKit.cutAction);
	Action Copy = map.get(DefaultEditorKit.copyAction);
	Action Paste = map.get(DefaultEditorKit.pasteAction);

	
	Action Open = new AbstractAction("Open", new ImageIcon("open.gif")) {
		public void actionPerformed(ActionEvent e) {
			saveOld();
			if(dialogFileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				readInFile(dialogFileChooser.getSelectedFile().getAbsolutePath());
			}
			SaveAs.setEnabled(true);
		}
	};
	
	Action Save = new AbstractAction("Save", new ImageIcon("save.gif")) {
		public void actionPerformed(ActionEvent e) {
			if(!currentFile.equals("Untitled"))
				saveFile(currentFile);
			else
				saveFileAs();
		}
	};
	
	Action SaveAs = new AbstractAction("Save as...") {
		public void actionPerformed(ActionEvent e) {
			saveFileAs();
		}
	};
	Action Quit = new AbstractAction("Quit") {
		public void actionPerformed(ActionEvent e) {
			saveOld();
			System.exit(0);
		}
	};
	private JMenu mnEdit;
	private void saveFileAs() {
		if(dialogFileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
			saveFile(dialogFileChooser.getSelectedFile().getAbsolutePath());
	}
	
	private void saveOld() {
		if(changed) {
			if(JOptionPane.showConfirmDialog(this, "Would you like to save "+ currentFile +" ?","Save",JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION)
				saveFile(currentFile);
		}
	}
	private void readInFile(String fileName) {
		try {
			FileReader r = new FileReader(fileName);
			textPane.read(r,null);
			r.close();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
		}
		catch(IOException e) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(this,"Editor can't find the file called "+fileName);
		}
	}
	
	private void saveFile(String fileName) {
		try {
			FileWriter w = new FileWriter(fileName);
			textPane.write(w);
			w.close();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
			Save.setEnabled(false);
		}
		catch(IOException e) {
		}
	}
	public String Dictionary(String lemma)
	{
		try{
			String meaning;
			JWNL.initialize(new FileInputStream("C:/dict/properties.xml"));
			Dictionary dictionary = Dictionary.getInstance();
			IndexWord word= dictionary.lookupIndexWord(POS.NOUN, lemma);
			meaning="\n"+"Meaning of the word '"+lemma+"':";
			Synset[] senses=word.getSenses();
			for (int i=0; i<senses.length; i++) {
				Synset sense = senses[i];
				meaning=meaning+"\n"+sense.getGloss();
				Pointer[] holo = sense.getPointers(PointerType.PART_HOLONYM);
				for (int j=0; j<holo.length; j++) {
					Synset synset=(Synset) (holo[j].getTarget());
					Word synsetWord = synset.getWord(0);
					meaning=meaning+"\n"+"#"+ synsetWord.getLemma();
					meaning=meaning+"==> " + synset.getGloss();
				}
			}
		return meaning;
			}catch(Exception e1){System.out.println(e1);}
		return null;
	}

}

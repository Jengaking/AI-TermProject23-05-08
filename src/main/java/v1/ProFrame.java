package v1;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class ProFrame extends JFrame implements ActionListener{
	private JButton doOcrButton = new JButton("문자인식");
	private JButton browseButton = new JButton("이미지 탐색");
	private JButton translateButton = new JButton("번역 하기");
	private JFileChooser filebrowser = new JFileChooser("이미지 탐색기");
	private Container contentPane = getContentPane();
	private JLabel planeImageLabel = new JLabel();
	private JScrollPane imageSCPane = new JScrollPane(planeImageLabel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	private JViewport scviewport = new JViewport();
	
	private Font buttonFont = new Font("굴림", Font.BOLD, 13);
	private JTextField engTextField = new JTextField();
	private JScrollPane engSCPane = new JScrollPane(engTextField, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	private JTextField korTextField = new JTextField();
	private JScrollPane korSCPane = new JScrollPane(korTextField, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	private OCREventHandler ocre = null;
	private TranslationHandler th = null;
	
	public ProFrame() {
		super("Image Translator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setLocation(200, 100);
		setSize(1000, 600);
		doOcrButton.setSize(150, 80);
		doOcrButton.setLocation(820, 20);
		doOcrButton.setFont(buttonFont);
		ocre = new OCREventHandler(engTextField, this);
		doOcrButton.addActionListener(ocre);
		
		browseButton.setSize(150, 80);
		browseButton.setLocation(820, 100);
		browseButton.addActionListener(this);
		browseButton.setFont(buttonFont);
		
		translateButton.setSize(150, 80);
		translateButton.setLocation(820, 180);
		translateButton.setFont(buttonFont);
		th = new TranslationHandler(engTextField, korTextField, this);
		translateButton.addActionListener(th);
		
		filebrowser.setFileFilter(new FileNameExtensionFilter("jpg", "jpeg"));
		filebrowser.setMultiSelectionEnabled(false);
		filebrowser.setSize(500, 500);
		
		imageSCPane.setSize(800, 300);
		imageSCPane.setLocation(5, 5);
		imageSCPane.setViewport(scviewport);
		
		engSCPane.setSize(950, 100);
		engSCPane.setLocation(5, 310);
		
		korSCPane.setSize(950, 100);
		korSCPane.setLocation(5, 415);
		
		contentPane.add(doOcrButton);
		contentPane.add(browseButton);
		contentPane.add(translateButton);
		contentPane.add(planeImageLabel);
		contentPane.add(imageSCPane);
		contentPane.add(engSCPane);
		contentPane.add(korSCPane);
		
		
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		filebrowser.showOpenDialog(this);
		File select = filebrowser.getSelectedFile();
		String imagePath = select.getAbsolutePath();
		ImageIcon image = new ImageIcon(imagePath);
		planeImageLabel.setIcon(image);
		planeImageLabel.setSize(image.getIconWidth(), image.getIconHeight());
		scviewport.setView(planeImageLabel);
		
		ocre.setImagePath(imagePath);
		System.out.println("Image selected. Path : " + ocre.getImagePath());
		this.repaint();
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		new ProFrame();
	}
}

final class OCREventHandler implements ActionListener {
	String imagePath = null;
	JTextField ptf = null;
	JFrame parent = null;
	
	public OCREventHandler(String s, JTextField tf, JFrame f) {
		this.imagePath = s;
		this.ptf = tf;
		parent = f;
	}
	
	public OCREventHandler(JTextField tf, JFrame f) {
		this("", tf, f);
	}
	
	public String getImagePath() { return imagePath; }
	
	public void setImagePath(String path) {
		imagePath = path;
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("OCR Event occurs");
		String result = TessMod.procOCR(imagePath);
		ptf.setText(result);
		parent.repaint();
	}
}

final class TranslationHandler implements ActionListener {
	String planeText = null;
	JTextField ptf = null;
	JTextField ttf = null;
	JFrame parent = null;
	public TranslationHandler(String s, JTextField ptf, JTextField ttf, JFrame f) {
		this.planeText = s;
		this.ptf = ptf;
		this.ttf = ttf;
		parent = f;
	}
	
	public TranslationHandler(JTextField ptf, JTextField ttf, JFrame f) {
		this("", ptf, ttf, f);
	}
	
	public void actionPerformed(ActionEvent e) {
		planeText = ptf.getText();
		String transText = PapaGoMod.doTranslation(planeText);
		ttf.setText(transText);
		parent.repaint();
	}
}
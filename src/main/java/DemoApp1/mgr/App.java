package DemoApp1.mgr;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.opencv.core.Core;


public class App 
{
	private JFrame frame;
	private JLabel infoLabel;
	private JButton buttonOpenFile;
	private JLabel statusLabel;
	private JPanel histogramSearchPanel;
	private JPanel methodChoosePanel;
	private JButton buttonSearch;
	private JPanel panelSearchResult;
	private JPanel panelCountingResult;
	private JLabel labelShowingChoosenImage;
	public JLabel labelTest;
	
	public App() {

		JMenuBar menuBar = new JMenuBar();
		JMenu methodMenu = new JMenu("Metoda");
		JMenuItem histogramShow = new JMenuItem("Pokaż histogram");
		JMenuItem histogramCompare = new JMenuItem("Porównaj histogram");
		JMenuItem countourFind = new JMenuItem("Znajdz kontur");
		methodMenu.add(histogramShow);
		methodMenu.add(histogramCompare);
		methodMenu.add(countourFind);
		menuBar.add(methodMenu);
		
        //search panel
        infoLabel = new JLabel("Wybierz zdjęcie do porównania:");
        buttonOpenFile = new JButton("Otwórz plik");
        statusLabel = new JLabel("");
        
        histogramSearchPanel = new JPanel(new FlowLayout());
        histogramSearchPanel.setPreferredSize(new Dimension(700, 50));
        histogramSearchPanel.add(infoLabel);
        histogramSearchPanel.add(buttonOpenFile);
        histogramSearchPanel.add(statusLabel);
        
        //method panel
        JLabel infoChooseMethodLabel = new JLabel("Wybierz metodę:",JLabel.RIGHT);
        JPanel methodChoosingMethodPanel  = new JPanel(new GridLayout(3,1));
        final JCheckBox checkHistogram = new JCheckBox("Histogram");
        final JCheckBox checkContour = new JCheckBox("Kontur");
        final JCheckBox checkAAAA = new JCheckBox("AAAA");
        methodChoosingMethodPanel.add(checkHistogram);
        methodChoosingMethodPanel.add(checkContour);
        methodChoosingMethodPanel.add(checkAAAA);
        
        methodChoosePanel = new JPanel(new GridLayout(1,2));
        methodChoosePanel.setPreferredSize(new Dimension(700, 50));
        methodChoosePanel.add(infoChooseMethodLabel);
        methodChoosePanel.add(methodChoosingMethodPanel);
        
        //search button
        buttonSearch = new JButton("Szukaj");
        buttonSearch.setPreferredSize(new Dimension(100, 50));
        
        //searching result
        panelSearchResult = new JPanel(new FlowLayout());
        panelSearchResult.setPreferredSize(new Dimension(800, 300));
        labelShowingChoosenImage = new JLabel();
        labelShowingChoosenImage.setPreferredSize(new Dimension(400, 300));
        labelTest = new JLabel();
        panelSearchResult.add(labelShowingChoosenImage);
        panelSearchResult.add(labelTest);
        
        //counting result
        panelCountingResult = new JPanel(new FlowLayout());

		frame = new JFrame();
        frame.setTitle("mgr");
        //frame.setLayout(new GridLayout(6,1));
        frame.setLayout(new FlowLayout());
        frame.add(histogramSearchPanel);
        frame.add(methodChoosePanel);
        frame.add(buttonSearch);
        frame.add(panelSearchResult);
        frame.add(panelCountingResult);
        frame.setSize(new Dimension(800, 650));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setJMenuBar(menuBar);
        frame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
	         }        
	      });
        
        
	}
	
	public static void main( String[] args )
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		App app = new App();
		app.showFileSelected ();
		
		new CompareHistogram().run(args);
		//new CompareContour(args);
    }
	
	public void showFileSelected () {
		final JFileChooser fileSelected = new JFileChooser();
		
		buttonOpenFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				int returnValue = fileSelected.showOpenDialog(histogramSearchPanel);
				
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					   java.io.File file = fileSelected.getSelectedFile();
		               statusLabel.setText("Wybrany plik :" + file.getName());
		               
		               try {
		            	   BufferedImage inputImage = ImageIO.read(file);
		            	   inputImage.getScaledInstance(labelShowingChoosenImage.getWidth(), labelShowingChoosenImage.getHeight(), Image.SCALE_DEFAULT);
		            	   labelShowingChoosenImage.setIcon(new ImageIcon(inputImage));
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
		            } else {
		               statusLabel.setText("Nie wybrano pliku" );           
		            }
			}
		});
	}
}

package DemoApp1.mgr;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class CompareContour {
    private Mat srcGray = new Mat();
    private int threshold = 120;
    private List<String> matLoadArrayListString = new ArrayList<String>();
    private List<Mat> convertArrayList = new ArrayList<Mat>();
    private JFrame frame;
    private JLabel imgSrcLabel;
    private JLabel imgContoursLabel;
    private static final int MAX_THRESHOLD = 255;
	private static final int LINE_8 = 0;

    public CompareContour(String[] args) {
    	final File dir = new File("C:\\Users\\Lolek\\Desktop\\mgr");
    	final String[] EXTENSIONS = new String[]{
    	        "jpg", "jpeg"
    	    };
    	final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

            @Override
            public boolean accept(final File dir, final String name) {
                for (final String ext : EXTENSIONS) {
                    if (name.endsWith("." + ext)) {
                        return true;
                    }
                }
                return false;
            }
        };
        //Load images
        String sep = "\\";
		if (dir.isDirectory()) {
            for (final File f : dir.listFiles(IMAGE_FILTER)) {
            	matLoadArrayListString.add(f.getAbsolutePath().replace(File.separator, Matcher.quoteReplacement(sep)));
            }
        }
		//System.out.println("matLoadArrayListString" + matLoadArrayListString);
        
        //String filename = "C:\\Users\\Lolek\\Desktop\\mgr\\IMG_20190730_154754.jpg";
        String filename = "C:\\Users\\Lolek\\Desktop\\mgr\\123test.jpg";
        Mat src = Imgcodecs.imread(filename);
        if (src.empty()) {
            System.err.println("Cannot read image: " + filename);
            System.exit(0);
        }
        
        List<Mat> matLoadArrayList = new ArrayList<Mat>();
        for (int i = 0; i < matLoadArrayListString.size(); i++) {
        	matLoadArrayList.add(Imgcodecs.imread(matLoadArrayListString.get(i)));
        }
        //System.out.println("matLoadArrayList" + matLoadArrayList);
        //Load images
        
        //Convert images
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(srcGray, srcGray, new Size(3, 3));//doczytac box blur, kernel
        //Imgproc.blur:Blurs an image using the normalized box filter. The function smoothes an image using the kernel
        
        
        for (int i = 0; i < matLoadArrayList.size(); i++) {
        	convertArrayList.add(new Mat());
        	Imgproc.cvtColor(matLoadArrayList.get(i), convertArrayList.get(i), Imgproc.COLOR_BGR2GRAY);
        	Imgproc.blur(convertArrayList.get(i), convertArrayList.get(i), new Size(3, 3));
        }
        //System.out.println("convertArrayList " + convertArrayList);
        //Convert images
        
        
        // Create and set up the window.
        frame = new JFrame("Finding contours in your image demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set up the content pane.
        Image img = HighGui.toBufferedImage(src);
        addComponentsToPane(frame.getContentPane(), img);
        // Use the content pane's default BorderLayout. No need for
        // setLayout(new BorderLayout());
        // Display the window.
        frame.pack();
        frame.setVisible(true);
        
        update();
    }
    
    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel("Canny threshold: "));
        JSlider slider = new JSlider(0, MAX_THRESHOLD, threshold);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                threshold = source.getValue();
                update();
            }
        });
        sliderPanel.add(slider);
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        JPanel imgPanel = new JPanel();
        imgSrcLabel = new JLabel(new ImageIcon(img));
        imgPanel.add(imgSrcLabel);
        Mat blackImg = Mat.zeros(srcGray.size(), CvType.CV_8U);
        imgContoursLabel = new JLabel(new ImageIcon(HighGui.toBufferedImage(blackImg)));
        imgPanel.add(imgContoursLabel);
        pane.add(imgPanel, BorderLayout.CENTER);
    }
    
    private void update() {
        Mat cannyOutput = new Mat();
        Imgproc.Canny(srcGray, cannyOutput, threshold, threshold * 2);
        /*
         * Imgproc.Canny:The function finds edges in the input image and marks them
         *  in the output map edges using the Canny algorithm.
         */
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cannyOutput, contours, hierarchy, 
        		Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        
        List<Mat> cannyArrayList = new ArrayList<Mat>();
        for (int i = 0; i < convertArrayList.size(); i++) {
        	cannyArrayList.add(new Mat());
        	Imgproc.Canny(convertArrayList.get(i), cannyArrayList.get(i), threshold, threshold * 2);
        }
        
        List<Integer> indexArraylist = new ArrayList<Integer>();
        List<MatOfPoint> contoursArrayList = new ArrayList<>();
        List<Mat> hierarchyArrayList = new ArrayList<Mat>();
        for (int i = 0; i < cannyArrayList.size(); i++) {
        	hierarchyArrayList.add(new Mat());
        	Imgproc.findContours(cannyArrayList.get(i), contoursArrayList, 
        			hierarchyArrayList.get(i), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        	int index = contoursArrayList.size() - indexArraylist.size();
        	for (int j = 0; j < index; j++) {
        		indexArraylist.add(i);//ilosc indeksow odpowiadajacym zdjeciom
        	}
        }
        //System.out.println("srcGray " + srcGray);
        //System.out.println("contours " + contours);
        System.out.println("rozmiar " + contoursArrayList.size());
        System.out.println("contours size " + contours.size());
        System.out.println("indexArraylist size " + indexArraylist.size());
        //System.out.println("convertArrayList size " + convertArrayList.size());
        
        //double resultMatchShapes = Imgproc.matchShapes(contour1, contour2, method, 0);
        List<Double> matchResultArraylist = new ArrayList<Double>();
        List<Double> tempArrayList = new ArrayList<Double>();
        List<Integer> indexMatchArraylist = new ArrayList<Integer>();
        
        for (int a = 0; a < contours.size(); a++) {
        	for (int i = 0; i < contoursArrayList.size(); i++) {
            	tempArrayList.add(Imgproc.matchShapes(contours.get(a), contoursArrayList.get(i), 1, 0));
            	//System.out.println(tempArrayList.get(i));
            	if (tempArrayList.get(i)<0.19 && !(matchResultArraylist.contains(tempArrayList.get(i)))) {
            		matchResultArraylist.add(tempArrayList.get(i));
            		if (!(indexMatchArraylist.contains(indexArraylist.get(i)))) {
            			indexMatchArraylist.add(indexArraylist.get(i));
            		}
            	}	
            }
        }
        System.out.println("rozmiar matchResultArraylist " + matchResultArraylist.size());
        System.out.println("indexMatchArraylist " + indexMatchArraylist);
        for (int i = 0; i < indexMatchArraylist.size(); i++) {
        	System.out.println(matLoadArrayListString.get(indexMatchArraylist.get(i)));
        }
        
        Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
        for (int i = 0; i < contours.size(); i++) {//
            Scalar color = new Scalar(0, 0, 256);
            Imgproc.drawContours(drawing, contours, i, color, 2, LINE_8, hierarchy, 0); //, new Point()
        }
        imgContoursLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(drawing)));
        frame.repaint();
    }

}

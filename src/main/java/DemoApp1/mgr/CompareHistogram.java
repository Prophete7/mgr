package DemoApp1.mgr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import javax.swing.ImageIcon;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Range;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class CompareHistogram {
	private App app;
	
	private final File dir = new File("C:\\Users\\Lolek\\Desktop\\mgr");
	static final String[] EXTENSIONS = new String[]{
	        "jpg", "jpeg"
	    };
	static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

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
    
	public void run(String[] args) {
        //Load images
        String sep = "\\";
		List<String> matLoadArrayListString = new ArrayList<String>();
		if (dir.isDirectory()) {
            for (final File f : dir.listFiles(IMAGE_FILTER)) {
            	matLoadArrayListString.add(f.getAbsolutePath().replace(File.separator, Matcher.quoteReplacement(sep)));
            }
        }
		System.out.println(matLoadArrayListString);
		
        String filename1 =
        		"C:\\Users\\Lolek\\Desktop\\mgr\\IMG_20190730_154754.jpg"; //app.showFileSelected().file.getName()
        Mat srcBase = Imgcodecs.imread(filename1);
        //System.out.println("srcBase" + srcBase);
        
        List<Mat> matLoadArrayList = new ArrayList<Mat>();
        for (int i = 0; i < matLoadArrayListString.size(); i++) {
        	matLoadArrayList.add(Imgcodecs.imread(matLoadArrayListString.get(i)));
        }
        //System.out.println("matLoadArrayList" + matLoadArrayList);
        //Load images

        //Convert to HSV
        Mat hsvBase = new Mat();
        Imgproc.cvtColor( srcBase, hsvBase, Imgproc.COLOR_BGR2HSV );
        //System.out.println("hsvBase" + hsvBase);
        
        List<Mat> hsvArrayList = new ArrayList<Mat>();
        for (int i =0; i<matLoadArrayList.size(); i++) {
        	hsvArrayList.add(new Mat());
        	Imgproc.cvtColor( matLoadArrayList.get(i), hsvArrayList.get(i), Imgproc.COLOR_BGR2HSV );
        }
        //System.out.println("hsvArrayList" + hsvArrayList );
        //Convert to HSV
        
        //test
        System.out.println("hsvArrayList.get(2) " + hsvArrayList.get(2));
        //app.labelTest.setIcon(new ImageIcon(HighGui.toBufferedImage(hsvArrayList.get(2))));//nullPointer??
        System.out.println("matLoadArrayListString.get(2) " + matLoadArrayListString.get(2));
        //app.labelTest.setIcon(new ImageIcon(matLoadArrayListString.get(2)));//nullPointer??
        createImageIcon(matLoadArrayListString.get(2), "aaaaa");
        //test

        //Convert to HSV half
        Mat hsvHalfDown = hsvBase.submat( new Range( hsvBase.rows()/2, hsvBase.rows() - 1 ), new Range( 0, hsvBase.cols() - 1 ) );
        //Convert to HSV half

        //Using 50 bins for hue and 60 for saturation
        int hBins = 50, sBins = 60;
        int[] histSize = { hBins, sBins };

        // hue varies from 0 to 179, saturation from 0 to 255
        float[] ranges = { 0, 180, 0, 256 };

        // Use the 0-th and 1-st channels
        int[] channels = { 0, 1 };
        //Using 50 bins for hue and 60 for saturation

        //Calculate the histograms
        Mat histBase = new Mat(), histHalfDown = new Mat();

        List<Mat> hsvBaseList = Arrays.asList(hsvBase);
        Imgproc.calcHist(hsvBaseList, new MatOfInt(channels), new Mat(), histBase, 
        		new MatOfInt(histSize), new MatOfFloat(ranges), false);
        Core.normalize(histBase, histBase, 0, 1, Core.NORM_MINMAX);
        //System.out.println("histBase" + histBase);

        List<Mat> hsvHalfDownList = Arrays.asList(hsvHalfDown);
        Imgproc.calcHist(hsvHalfDownList, new MatOfInt(channels), new Mat(), histHalfDown,
        		new MatOfInt(histSize), new MatOfFloat(ranges), false);
        Core.normalize(histHalfDown, histHalfDown, 0, 1, Core.NORM_MINMAX);
        
        List<Mat> histArrayList = new ArrayList<Mat>();
        for (int i =0; i<hsvArrayList.size(); i++) {
        	histArrayList.add(new Mat()); 
        	Imgproc.calcHist(hsvArrayList, new MatOfInt(channels), new Mat(), histArrayList.get(i),
            		new MatOfInt(histSize), new MatOfFloat(ranges), false);
        	Core.normalize(histArrayList.get(i), histArrayList.get(i), 0, 1, Core.NORM_MINMAX);
        }
        //System.out.println("histArrayList "+ histArrayList);
        //Calculate the histograms

        //Apply the histogram comparison methods
        for( int compareMethod = 0; compareMethod < 4; compareMethod++ ) { 
            double baseBase = Imgproc.compareHist( histBase, histBase, compareMethod);
            double baseHalf = Imgproc.compareHist( histBase, histHalfDown, compareMethod );

            System.out.println("Method " + compareMethod + " Perfect, Base-Half "
            		+ baseBase + " / " + baseHalf); 
        }
        System.out.println("histArrayList.size() " + histArrayList.size());
        List <Double> resultList = new ArrayList<Double>();
        int a = 0;
        for(int i = 0; i < histArrayList.size(); i++) {
        	for(int compareMethod = 0; compareMethod < 4; compareMethod++) {
        		double baseBase = Imgproc.compareHist(histBase, histBase, compareMethod);
        		resultList.add(Imgproc.compareHist(histBase, histArrayList.get(i), compareMethod));
        		System.out.println(baseBase + " / " + resultList.get(a));
        		a++;
        	}
        	System.out.println("/////////////////////////");
        }
        //System.out.println(resultList);
        System.out.println("resultList.size() " + resultList.size());
        
        /*switch
         * case 0=correlation
         * case 1=chi-square
         * case 2=intersection
         * case 3=Bhattacharyya
         */
        //Apply the histogram comparison methods
    }
	protected ImageIcon createImageIcon(String path,
            String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
}

package v1;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public final class TessMod {
	private static Tesseract tesseract = new Tesseract();
	
	public static String procOCR(String path) {
		tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
		System.out.println("OCR : start extraction (Image Path : "+path+" )" );
		StringTokenizer st = new StringTokenizer(path, "\\");
		StringBuilder sb = new StringBuilder();
		while(st.hasMoreTokens()) {
			sb.append(st.nextToken());
			System.out.println("OCR : in while block : token = " + sb.toString());
			
			if(sb.charAt(sb.length()-1) == ':') {
				sb.append("\\");
			}
			sb.append("\\");
		}
		System.out.println("OCR : amended path (Image Path : "+sb.toString()+" )" );
		
		Mat timg = Imgcodecs.imread(sb.substring(0, sb.length()-1));
		
		timg = TessMod.imagePreproc(timg);
		String outputPath ="D:\\\\testImages\\ImagePocessing\\result.jpg"; 
		Imgcodecs.imwrite(outputPath, timg);
		
		String result = null;
		try {
			result = tesseract.doOCR(new File(outputPath));
			System.out.println("OCR : the result of OCR = "+result);
			
		} catch(TesseractException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

		System.out.println("OCR : end of extraction");
		return result;
	}
	
	public static Mat imagePreproc(Mat input_image) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat image = input_image;
	    //if(image.width() > 800) image = resize(image);
	    	
	    System.out.println(image.width() + " " + image.height());
	    showGUI(image, "원본 이미지");
	    Mat destination= new Mat(image.rows(), image.cols(), image.type());
	    // 가우시안 블러
	   	Imgproc.GaussianBlur(image, destination,new Size(0, 0), 10);
	   	showGUI(destination, "가우시안 블러");
	   	// 가중치 더하기 (Sharpening)
	   	Core.addWeighted(image, 1.5, destination, -0.5, 10, destination);
	   	showGUI(destination, "가중치");
	   	image = destination;
	   	// 그레이 스케일
	   	Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY); // gray scaling
	   	showGUI(image, "그레이 스케일");
	   	
	   	// 이진화
	   	Imgproc.threshold(image, image, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
	   	showGUI(image, "이진화");
//
//	   	// adaptive threshold
//	   	Imgproc.adaptiveThreshold(image, image, 125,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY, 11, 12);
//	   	showGUI(image, "이진화");
//	    	
//	   	// morphological transformation
//	   	//Preparing the kernel matrix object
//      Mat kernel = Mat.ones(1,1, CvType.CV_32F);
//	    //Applying dilate on the Image
//	    Imgproc.morphologyEx(image, image, Imgproc.MORPH_OPEN, kernel);
//	 	showGUI(image, "변환");
	    	
	    
	    return image;
	}
	    
	public static void showGUI(Mat i, String message) {
		MatOfByte mat = new MatOfByte();
	        
	    //show scaled image on frame
	    Imgcodecs.imencode(".jpg", i, mat); 
	    byte[] byteArray = mat.toArray(); 
	    InputStream in = new ByteArrayInputStream(byteArray); 
	    BufferedImage buf;
		try {
			buf = ImageIO.read(in);
			JFrame fr = new JFrame(message); 
			fr.getContentPane().add(new JLabel(new ImageIcon(buf))); 
	        fr.pack();
	        fr.setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	    
	public static Mat resize(Mat image) {
		double ratio = image.width() / image.height();
		Size size = new Size(200 * ratio, 200);
		Mat resized = new Mat();
		Imgproc.resize(image, resized, size);
		return resized;
	}
}

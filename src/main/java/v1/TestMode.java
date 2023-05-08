package v1;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

public class TestMode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Tesseract tesseract = new Tesseract();
	    try {
	    	String imagePath = new String("D:\\testImages\\testImage.jpg");
	    	Mat image = Imgcodecs.imread(imagePath);
	    	showGUI(image, "원본 이미지");
	    	// 가우시안 블러
	    	Imgproc.GaussianBlur(image, image,new Size(0, 0), 1);
	    	showGUI(image, "가우시안 블러");
	    	// 가중치 더하기 (Sharpening)
	    	Core.addWeighted(image, 1.5, image, -0.5, 50, image);
	    	showGUI(image, "가중치");
	    	// 그레이 스케일
	    	Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY); // gray scaling
	    	showGUI(image, "그레이 스케일");
	    	
	    	// 이진화
	    	Imgproc.threshold(image, image, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
	    	showGUI(image, "이진화");
	    	
	    	tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata"); // Tesseract OCR이 설치된 디렉토리의 경로를 지정합니다.
	    	String result = tesseract.doOCR(new File("D:\\testImages\\testImage.jpg")); // 이미지 파일에서 텍스트를 추출합니다.
	    	System.out.println(result);
	      
	      
	    } catch (TesseractException e) {
	    	System.err.println(e.getMessage());
	    }
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
}

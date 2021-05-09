package com.truegeometry.mkhilbertml.ImageTo3D;

 
import boofcv.struct.feature.Match;
import com.truegeometry.mkhilbertml.FitPolygon;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.IplImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import static org.bytedeco.opencv.global.opencv_core.cvFlip;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvSaveImage;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_stitching.Stitcher;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_stitching.createStitcher;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_stitching.Stitcher;

import org.opencv.core.Core;
import org.opencv.core.MatOfByte;
 
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


/**
 * Created by gtiwari on 1/3/2017.
 * Updated by azmechatech@gmail.com 5/1/2021
 */

public class PatternMatch implements Runnable {
    final int INTERVAL = 500;///you may use interval
    //CanvasFrame canvas = new CanvasFrame("Web Cam 1");
    //CanvasFrame canvas2 = new CanvasFrame("Web Cam 2");
    CanvasFrame canvasStitch = new CanvasFrame("Pixel Shift Preview");
    MatVector imgs = new MatVector();
    DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();

    public PatternMatch() {
        //canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
       // canvas2.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvasStitch.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }

    public void run() {

        new File("images").mkdir();

        FrameGrabber grabber1 = new OpenCVFrameGrabber(1); // 1 for next camera
        FrameGrabber grabber2 = new OpenCVFrameGrabber(2); // 1 for next camera
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage img1;
        IplImage img2;
        int i = 0;
        try {
            grabber1.start();
            grabber2.start();

            while (true) {
                 System.out.println("Loop "+System.currentTimeMillis());
                Frame frame1 = grabber1.grab();
                Frame frame2 = grabber2.grab();

                img1 = converter.convert(frame1);
                img2 = converter.convert(frame2);

                //the grabbed frame will be flipped, re-flip to make it right
                cvFlip(img1, img1, -1);// l-r = 90_degrees_steps_anti_clockwise

                //the grabbed frame will be flipped, re-flip to make it right
                cvFlip(img2, img2, -1);// l-r = 90_degrees_steps_anti_clockwise

                
                
                //save
                //cvSaveImage("images" + File.separator + (i) + "-a1.jpg", img1);
                //cvSaveImage("images" + File.separator + (i) + "-a2.jpg", img2);
          

//                canvas.showImage(converter.convert(img1));
//                canvas2.showImage(converter.convert(img2));
//                
//                  
//                canvas.showImage(Java2DFrameUtils.toFrame(FitPolygon.getCannyBinary(Java2DFrameUtils.toBufferedImage(img1))));
//                canvas2.showImage(Java2DFrameUtils.toFrame(FitPolygon.getCannyBinary(Java2DFrameUtils.toBufferedImage(img2))));
               
                
                    BufferedImage b1=Java2DFrameUtils.toBufferedImage(img1);
                    List<ImagePOJO> templates = FitPolygon.getCannyImages(b1);
                    List<ImagePOJO> results=new ArrayList<>();
                    BufferedImage b2=Java2DFrameUtils.toBufferedImage(img2);
                   //  canvasStitch.showImage(joinBufferedImage(b1,b2));
                     
                    
                     if(true)
                         templates.parallelStream().filter(imgPOJO -> ((imgPOJO.xmax-imgPOJO.xmin)>10 || (imgPOJO.ymax-imgPOJO.ymin)>10)).forEachOrdered(imgPOJO -> {
                             try {
                                 //JLabel picLabel = new JLabel(new ImageIcon(imgPOJO.getImage()));
                                 //JOptionPane.showMessageDialog(null, picLabel, "Original", JOptionPane.PLAIN_MESSAGE, null);
                                 List<Match> matches = ExampleTemplateMatching.findMatches(b2, imgPOJO.getImage(), 1);
                                 int x = matches.get(0).x;
                                 int y = matches.get(0).y;

                                 
                                 BufferedImage result = b2.getSubimage(x, y, imgPOJO.getImage().getWidth(), imgPOJO.getImage().getHeight());

                                 descriptiveStatistics.addValue(Math.abs(imgPOJO.xmin - x));
                                 
                                 if(descriptiveStatistics.getN()>30){
                                    if(Math.abs(imgPOJO.xmin - x)>descriptiveStatistics.getPercentile(50))
                                        return;//For less than 50th %tile ignore the shift.
                                 }
                                 imgPOJO.setDistance(Math.sqrt(Math.abs(imgPOJO.xmin - x)*Math.abs(imgPOJO.xmin - x)+Math.abs(imgPOJO.ymin - y)*Math.abs(imgPOJO.ymin - y)));
                               

                                 ImagePOJO thisPojo = new ImagePOJO(result, x, y, x + imgPOJO.getImage().getWidth(), y + imgPOJO.getImage().getHeight());
                                 thisPojo.setDistance(imgPOJO.getDistance());
                                 thisPojo.setLabel(imgPOJO.getLabel());
                                 results.add(thisPojo);
                                 
                                 System.out.println("Shift>> \t" +imgPOJO.getLabel()+" "+round(imgPOJO.getDistance(),0) + "\t" + (imgPOJO.xmin - x) + "," + (imgPOJO.ymin - y));
                                 
                                 //JLabel picLabe2 = new JLabel(new ImageIcon(joinBufferedImage(imgPOJO.getImage(),result)));
                                 //  JOptionPane.showMessageDialog(null, picLabe2, "Matched", JOptionPane.PLAIN_MESSAGE, null);
                             } catch (Exception ex) {
                             }
                             //Need to form the equation properly.
                             //For xShift=171,yShift=36 => 41cm distance, Only X shift is expected as camera is kept horizontally. It may need more math work for x,y inclusion both
                             // -154,-36 =>52cm
                             // Per 17 pixel shift 11cm is depth
                             // per pixel 0.6470588235294118 cm 
                 });
             
                     BufferedImage finalImagePreview=joinBufferedImage(
                       joinBufferedImage(overlayBufferedImage(b1, FitPolygon.getCannyBinary(b1)),overlayBufferedImage(b2, FitPolygon.getCannyBinary(b2)), true),
                       joinBufferedImage(labelBufferedImage(b1,templates),labelBufferedImage(b2,results), true),false);
               canvasStitch.showImage(finalImagePreview);
               
               cvSaveImage("images" + File.separator + (i) + "-PixelShift.jpg",Java2DFrameUtils.toIplImage(finalImagePreview));
               
               Thread.sleep(INTERVAL);
                //Canny edges
                //JLabel picLabel = new JLabel(new ImageIcon(FitPolygon.getCannyBinary(Java2DFrameUtils.toBufferedImage(img1))));
                //JOptionPane.showMessageDialog(null, picLabel, "About", JOptionPane.PLAIN_MESSAGE, null);

                
                      i = i + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param img1
     * @param templates
     * @return 
     */
     public static BufferedImage labelBufferedImage(BufferedImage img1, List<ImagePOJO> templates) {

        //do some calculate first
//        int offset = 5;
//        int wid = img1.getWidth() + img2.getWidth() + offset;
//        int height = Math.max(img1.getHeight(), img2.getHeight()) + offset;
//        //create a new buffer and draw two image into the new image
//        BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img1.createGraphics();
        Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(Color.BLACK);
        
        templates.forEach(tmplate->{
            if(tmplate.getDistance()!=0.0)
            g2.drawString(tmplate.getLabel()+round(tmplate.getDistance(),2), tmplate.xmin, tmplate.ymin);
        });
        
//        g2.fillRect(0, 0, wid, height);
//        //draw image
//        g2.setColor(oldColor);
//        g2.drawImage(img1, null, 0, 0);
//        g2.drawImage(img2, null, img1.getWidth() + offset, 0);
//        g2.dispose();
        return img1;
    }
     
/**
 * 
 * @param img1
 * @param img2
 * @param horizontal
 * @return 
 */
    public static BufferedImage joinBufferedImage(BufferedImage img1, BufferedImage img2, boolean horizontal) {

        //do some calculate first
        int offset = 5;
        int wid = horizontal?img1.getWidth() + img2.getWidth() + offset: Math.max(img1.getWidth(), img2.getWidth()) + offset;
        int height = horizontal?Math.max(img1.getHeight(), img2.getHeight()) + offset:img1.getHeight() + img2.getHeight() + offset;
        //create a new buffer and draw two image into the new image
        BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);
        //draw image
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        if (horizontal)
            g2.drawImage(img2, null, img1.getWidth() + offset, 0);
        else
            g2.drawImage(img2, null, 0, img1.getHeight()+ offset);
        g2.dispose();
        return newImage;
    }
 /**
  * 
  * @param img1
  * @param img2
  * @return 
  */       
    public static BufferedImage overlayBufferedImage(BufferedImage img1, BufferedImage img2) {

        //do some calculate first
        int offset = 5;
        int wid =  Math.max(img1.getWidth(), img2.getWidth()) + offset;
        int height = Math.max(img1.getHeight(), img2.getHeight()) + offset ;
        //create a new buffer and draw two image into the new image
        BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);
        //draw image
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
        g2.drawImage(img2, null, 0, 0);
        g2.dispose();
        return newImage;
    }
 /**
  * 
  * @param value
  * @param places
  * @return 
  */   
    public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    long factor = (long) Math.pow(10, places);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;
}
    
      
    public static void main(String[] args) {
       // System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        PatternMatch gs = new PatternMatch();
        Thread th = new Thread(gs);
        th.start();
    }
}


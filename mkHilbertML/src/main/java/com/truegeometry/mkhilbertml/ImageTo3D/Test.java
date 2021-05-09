package com.truegeometry.mkhilbertml.ImageTo3D;

 
import com.truegeometry.mkhilbertml.FitPolygon;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.IplImage;

import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

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

public class Test implements Runnable {
    final int INTERVAL = 5000;///you may use interval
    CanvasFrame canvas = new CanvasFrame("Web Cam 1");
    CanvasFrame canvas2 = new CanvasFrame("Web Cam 2");
    CanvasFrame canvasStitch = new CanvasFrame("Stitched");
    MatVector imgs = new MatVector();

    public Test() {
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvas2.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
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
                Frame frame1 = grabber1.grab();
                Frame frame2 = grabber2.grab();

                img1 = converter.convert(frame1);
                img2 = converter.convert(frame2);

                //the grabbed frame will be flipped, re-flip to make it right
                cvFlip(img1, img1, -1);// l-r = 90_degrees_steps_anti_clockwise

                //the grabbed frame will be flipped, re-flip to make it right
                cvFlip(img2, img2, -1);// l-r = 90_degrees_steps_anti_clockwise

                
                
                //save
                cvSaveImage("images" + File.separator + (i) + "-a1.jpg", img1);
                cvSaveImage("images" + File.separator + (i) + "-a2.jpg", img2);
          

                canvas.showImage(converter.convert(img1));
                canvas2.showImage(converter.convert(img2));
                

                  canvas.showImage(Java2DFrameUtils.toFrame(FitPolygon.getCannyBinary(Java2DFrameUtils.toBufferedImage(img1))));
                  canvas2.showImage(Java2DFrameUtils.toFrame(FitPolygon.getCannyBinary(Java2DFrameUtils.toBufferedImage(img2))));
  

                //
                //STITCH START
                //
                
//                Mat img = imread("images" + File.separator + (i) + "-a1.jpg");
//                if (img.empty()) {
//                    System.out.println("Can't read image '" + "images" + File.separator + (i) + "-a1.jpg"+ "'");
//                }
//                imgs.resize(imgs.size() + 1);
//                imgs.put(imgs.size() - 1, img);
//                
//                 Mat imgSt2 = imread("images" + File.separator + (i) + "-a2.jpg");
//                if (img.empty()) {
//                    System.out.println("Can't read image '" + "images" + File.separator + (i) + "-a1.jpg"+ "'");
//                }
//                imgs.resize(imgs.size() + 1);
//                imgs.put(imgs.size() - 1, imgSt2);
//                
//                Mat pano = new Mat();
//                Stitcher stitcher = createStitcher();
//                int status = stitcher.stitch(imgs, pano);
//               // stitcher.
//
//                if (status != Stitcher.OK) {
//                    System.out.println("Can't stitch images, error code = " + status);
//                    System.exit(-1);
//                }
//
//                imwrite("images" + File.separator + (i) + "-Stitch.jpg", pano);
//                
//                
//                canvasStitch.showImage(converter.convert(pano));
//                
//                canvasStitch.showImage(Java2DFrameUtils.toFrame(FitPolygon.getCannyBinary(Java2DFrameUtils.toBufferedImage(converter.convert(pano)))));
//
//                System.out.println("Images stitched together to make " + "images" + File.separator + (i) + "-Stitch.jpg");
                
                //STRICT ENDS
                
                
                //Canny edges
                //JLabel picLabel = new JLabel(new ImageIcon(FitPolygon.getCannyBinary(Java2DFrameUtils.toBufferedImage(img1))));
                //JOptionPane.showMessageDialog(null, picLabel, "About", JOptionPane.PLAIN_MESSAGE, null);

                Thread.sleep(INTERVAL);
                      i = i + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


      
    public static void main(String[] args) {
       // System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        Test gs = new Test();
        Thread th = new Thread(gs);
        th.start();
    }
}


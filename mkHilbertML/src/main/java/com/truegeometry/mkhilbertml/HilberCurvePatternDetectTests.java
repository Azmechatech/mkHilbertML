/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml;

import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.feature.detect.edge.EdgeSegment;
import georegression.struct.point.Point2D_I32;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
//import net.mky.tools.FitPolygon;

/**
 *
 * @author mkfs
 */
public class HilberCurvePatternDetectTests {
 //frame 
    static JFrame f; 
      
    //lists 
    static JList b,b1,b2; 
      
    //label 
    static JLabel l1; 
    public static void main(String... args) throws IOException {
         //create a new frame  
        f = new JFrame("frame"); 
          
        //create a object 
      //  solve s=new solve(); 
        
        //create a panel 
        JPanel p =new JPanel(); 
        p.setLayout(new GridLayout(5,5));
        
        
        //Select image from JDialog
        JFileChooser fileOpener = new JFileChooser();
        fileOpener.setMultiSelectionEnabled(true);
        int action = fileOpener.showOpenDialog(null);

        BufferedImage img = null;
        if (action == JFileChooser.APPROVE_OPTION) {
             File[] files = fileOpener.getSelectedFiles();
             p.setLayout(new GridLayout(5,files.length/5+1));
             for (File file : files) {
                img = ImageIO.read(file);
                double[] result = HilbertCurvePatternDetect.getImageEquation(img, 63);

               //HilbertCurvePatternDetect.displayImage(new BufferedImage[]{HilbertCurvePatternDetect.getEquationToImage(result, 63), HilbertCurvePatternDetect.resizeImage(img, 63, 63)}, 63, " getEquationToImage");
                // HilbertCurvePatternDetect.displayImage( HilbertCurvePatternDetect.resizeImage(img, 63, 63)," Scaled to "+63);
                JButton image1 = new JButton(new ImageIcon(HilbertCurvePatternDetect.resizeImage(HilbertCurvePatternDetect.getEquationToImage(result, 63),150, 150)));
                p.add(image1);
                
                JButton image2 = new JButton(new ImageIcon(HilbertCurvePatternDetect.resizeImage(img,150, 150)));
                p.add(image2);
                
                
                System.out.println(Arrays.toString(result));
                

            }
             
             getClusterPointsInteractiveTest(img);
        }
        //getColourWheelTest();

        
        
        
          
//        //create a new label 
//        JLabel l= new JLabel("select the day of the week"); 
//  
//        //String array to store weekdays 
//        String week[]= { "Monday","Tuesday","Wednesday", 
//                         "Thursday","Friday","Saturday","Sunday"}; 
//          
//        //create list 
//        b= new JList(week); 
//          
//        //set a selected index 
//        b.setSelectedIndex(2); 
//          
//        //add list to panel 
//        p.add(b); 
   
        f.add(p); 
          
        //set the size of frame 
        f.setSize(400,400); 
           
        f.setVisible(true);

    }

    public static void getClusterPointsInteractiveTest( BufferedImage img) {
              //  img = ImageIO.read(fileOpener.getSelectedFile());

                HilbertCurvePatternDetect.displayImage(img, "getClusterPointsInteractiveTest >> baseImage");
                String test1= JOptionPane.showInputDialog("NumberOfFeatures(Intger values only)");
                int numberOfFeaures=Integer.parseInt(test1);
                List< HilbertCurveImageResult> result = HilbertCurvePatternDetect.getFeaturesInImage(img, numberOfFeaures);
                List<EdgeContour> edges= FitPolygon.getCannyEdgesXY(img);
                
                //PREPARE FOR RESULT DISPLAY
                BufferedImage BIG_IMAGE = new BufferedImage(
                        500 * result.size() / 2, 500 * result.size() / 2, //work these out
                        BufferedImage.TYPE_INT_RGB);
                Graphics g = BIG_IMAGE.getGraphics();
                g.drawImage(createGradientImage(BIG_IMAGE.getWidth(), BIG_IMAGE.getHeight(), Color.GRAY,Color.LIGHT_GRAY),10,10,null);


                int x = 0, y = 0;
                for (HilbertCurveImageResult resultImage : result) {
                    //Try to get equation of region
                     double[] imgEquation = HilbertCurvePatternDetect.getImageEquation(resultImage.getFullImage(), 63);
                    
                    //Draw edges
                    Graphics gr = resultImage.getFullImage().getGraphics();
                    for( EdgeContour edgeContour:edges){
                        for(EdgeSegment edgeSegment:edgeContour.segments)
                            for(Point2D_I32 p2d: edgeSegment.points)
                            {    
                               gr.drawOval(p2d.x, p2d.y,1, 1);
                            }
                    }

                    if (x+310 > BIG_IMAGE.getWidth()) {
                        x = 0;
                        y += 300;
                    }
                    g.drawImage(HilbertCurvePatternDetect.resizeImage(resultImage.getRegionImage(), 300, 300), x, y, null);
                    //Next image location
                    x += 310;
                    
                    
                    

                //    HilbertCurvePatternDetect.displayImage(resultImage, "getClusterPointsInteractiveTest");
                }

                 g.drawImage(HilbertCurvePatternDetect.resizeImage(img, 300, 300), x, y, null);
                 
                HilbertCurvePatternDetect.displayImage(BIG_IMAGE, "getClusterPointsInteractiveTest");
           
        
        //Display image from JDialog
    }

    public static void getColourWheelTest() {
        HilbertCurvePatternDetect.displayImage(HilbertCurvePatternDetect.getColourWheel(63), "getColourWheelTest");
    }

      public static BufferedImage createGradientImage(int width, int height, Color gradient1,
      Color gradient2) {

    BufferedImage gradientImage = createCompatibleImage(width, height);
    GradientPaint gradient = new GradientPaint(0, 0, gradient1, 0, height, gradient2, false);
    Graphics2D g2 = (Graphics2D) gradientImage.getGraphics();
    g2.setPaint(gradient);
    g2.fillRect(0, 0, width, height);
    g2.dispose();

    return gradientImage;
  }

  private static BufferedImage createCompatibleImage(int width, int height) {

    return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
        .getDefaultConfiguration().createCompatibleImage(width, height);

  }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.davidmoten.hilbert.HilbertCurve;
import org.davidmoten.hilbert.HilbertCurveRenderer;
import org.davidmoten.hilbert.HilbertCurveRenderer.Option;
import org.davidmoten.hilbert.Range;
import org.davidmoten.hilbert.SmallHilbertCurve;

/**
 *
 * @author Maneesh
 */
public class HilbertCurvePatternDetect {

    public static void main(String... args) {

        HilbertCurve c1 = HilbertCurve.bits(4).dimensions(2);
        BigInteger index = c1.index(3, 4);
        System.out.println(index);
        long[] point = c1.point(53);
        System.out.println(point[0] + " " + point[1]);

        SmallHilbertCurve c = HilbertCurve.small().bits(5).dimensions(3);
        long[] point1 = new long[]{3, 3, 3};
        long[] point2 = new long[]{8, 10, 3};
        int splitDepth = 1;
        List<Range> ranges = c.query(point1, point2, splitDepth);
        ranges.stream().forEach(System.out::println);

        HilbertCurveRenderer.renderToFile(4, 800, "target/image.png");

        HilbertCurveRenderer.renderToFile(4, 1000, "target/imageColor4.png", Option.COLORIZE, Option.LABEL);

        final File folder = new File("C:/$AVG/baseDir/Imports/Sprites/MEN/");
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                // listFilesForFolder(fileEntry);
            } else {
                if (Math.random() > .01) {
                    continue;
                }
                System.out.println(fileEntry.getName());
                Result result = getMatchingRegion(new String[]{"C:/Users/Maneesh/Desktop/face.png",
                    "C:/Users/Maneesh/Desktop/face2.png",
                    "C:/Users/Maneesh/Desktop/face3.png",
                    "C:/Users/Maneesh/Desktop/face4.png"}, fileEntry.getAbsolutePath());

                BufferedImage img = getTheImage(fileEntry.getAbsolutePath(), false);
                Graphics2D graph = img.createGraphics();

                for (int i = 0; i < result.getRectangle().length; i++) {
                    graph.setColor(Color.GREEN);
                    graph.draw(result.rectangle[i]);
                    graph.setColor(Color.WHITE);
                    graph.fill(new Rectangle(result.rectangle[i].x - 10, result.rectangle[i].y - 10, 100, 10));
                    graph.setColor(Color.BLUE);
                    graph.drawString(">>" + result.difference[i], result.rectangle[i].x, result.rectangle[i].y);

                }
                //graph.drawImage(targetScaled, rect.x, rect.y, null);
                graph.dispose();
                displayImage(img, "img");
                displayImage(cropImage(img, result.getRectangle()[result.getRectangle().length - 1]), "img");
            }

        }

        getMatchingRegion(new String[]{"C:/Users/Maneesh/Desktop/face2.png"}, "C:/Users/Maneesh/Desktop/Emiko_1513270388079.png");
        //getMatchingRegion("C:/Users/Maneesh/Desktop/SeirenFace.png","C:/Users/Maneesh/Desktop/Seiren.png");
        //SeirenFace

    }

    public static BufferedImage getMatchedRegion(String imagePattern, String TargetImage) {
        java.awt.Rectangle[] result = getMatchingRegion(new String[]{imagePattern}, TargetImage).getRectangle();
        BufferedImage img = HilbertCurvePatternDetect.getTheImage(TargetImage, false);
        //Graphics2D graph = img.createGraphics();
        //displayImage(cropImage(img, result[result.length-1]),"img");
        return cropImage(img, result[result.length - 1]);

    }

    public static BufferedImage getTheImage(String imageFullPath, boolean blackAndWhite) {
        BufferedImage img = null;
        File f = null;

        //read image
        try {
            f = new File(imageFullPath);
            img = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println(e);
        }

        // BufferedImage blackWhite = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        BufferedImage blackWhite = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = blackWhite.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return blackAndWhite ? blackWhite : img;
    }

    /**
     *
     * @param imagePattern
     * @param TargetImage
     * @return
     */
    public static Result getMatchingRegion(String[] imagePattern, String TargetImage) {

        /**
         * 1. Read pattern image 2. Read the target image 3. scale both to 2px
         * by 2px 4. With hilbert curve find close matching pixels 5. Find the
         * scale difference of pattern image and the Target image and then set
         * the bits target from 1 to scale 6. select those pixels that are
         * closest match and then keep recording the matches.
         */
        BufferedImage imgT = getTheImage(TargetImage, true);//displayImage(imgT,"imgT");

        //get image width and height
//        int width = img.getWidth();
//        int height = img.getHeight();
        // int scale = imgT.getHeight() * imgT.getWidth() / (img.getWidth() * img.getHeight());
        // scale=4;//yet to find optimum value
        int thePixelsToTraverse = 5;
        int scale = thePixelsToTraverse + 2;
//        int startOPixelToTraverse = 3;
//        int pointBestMatchedGlobal = 0;
//        int differenceGlobal = Integer.MAX_VALUE;
//        int scaleSelected = 1;
//        int scaleSelectedForPattern = 1;
        // for(int j=0;j<img.getWidth();j++)
        Rectangle result[] = new Rectangle[thePixelsToTraverse - 1];
        double differences[] = new double[thePixelsToTraverse - 1];
        Result resultObject = new Result(thePixelsToTraverse - 1);
        BufferedImage img[] = new BufferedImage[imagePattern.length];// getTheImage(imagePattern[0], true);//displayImage(img,"img");
        HashMap<String, BufferedImage> cache = new HashMap<>();
        for (int i = 0; i < imagePattern.length; i++) {
            img[i] = getTheImage(imagePattern[i], true);
        }

        for (int i = 1; i < thePixelsToTraverse; i++) {

            BufferedImage patternScaled = resizeImage(img[0], img[0].getWidth() * (i + i) / thePixelsToTraverse, img[0].getHeight() * (i + i) / thePixelsToTraverse);//displayImage(patternScaled,"patternScaled");

            BufferedImage targetScaled = imgT;//resizeImage(imgT,imgT.getWidth()*i/thePixelsToTraverse, imgT.getHeight()*i/thePixelsToTraverse);//displayImage(targetScaled,"targetScaled");

            HilbertCurve cForpattern = HilbertCurve.bits(thePixelsToTraverse).dimensions(2);
            HilbertCurve cForTarget = HilbertCurve.bits(scale).dimensions(2);

            //Very important calculation
            int pointsToTravese = (int) Math.pow(2, thePixelsToTraverse * 2);
            int pointsToTraveseC = (int) Math.pow(2, (scale) * 2);
            scale = scale + 1;
            int pointBestMatched = 0, tempMatchCount = 0;
            int difference = Integer.MAX_VALUE;
            System.out.println("Loop to run for>>" + pointsToTraveseC + "x" + pointsToTravese + " at scale>>" + ((double) (i + i) / thePixelsToTraverse));
            //for(int traverseStart=0;traverseStart<pointsToTravese-pointsToTraveseC;traverseStart++){//Limit by target size
            for (int traverseStart = 0; traverseStart < pointsToTraveseC - pointsToTravese; traverseStart = (int) (traverseStart + pointsToTravese * .3)) {//Limit by target size
                double count = pointsToTravese - 5;
                TrackingParameters trackingParameters = new TrackingParameters();
                for (int k = 0; k < imagePattern.length; k++) {
                    if (cache.containsKey(k + "_" + (i + i))) {
                        patternScaled = cache.get(k + "_" + (i + i));
                    } else {
                        patternScaled = resizeImage(img[k], img[k].getWidth() * (i + i) / thePixelsToTraverse, img[k].getHeight() * (i + i) / thePixelsToTraverse);//displayImage(patternScaled,"patternScaled");

                        cache.put(k + "_" + (i + i), patternScaled);
                    }
                    for (int traverseStartp = 0; traverseStartp < pointsToTravese; traverseStartp++) {//Compare every point

                        try {
                            //Get the XY in images to match
                            long[] pointP = cForpattern.point(traverseStartp);
                            pointP[0] = (long) (pointP[0] * patternScaled.getWidth() / Math.sqrt(pointsToTravese));
                            pointP[1] = (long) (pointP[1] * patternScaled.getHeight() / Math.sqrt(pointsToTravese));
                            long[] pointT = cForTarget.point(traverseStart);
                            pointT[0] = (long) (pointT[0] * targetScaled.getWidth() / Math.sqrt(pointsToTraveseC)) + pointP[0];
                            pointT[1] = (long) (pointT[1] * targetScaled.getHeight() / Math.sqrt(pointsToTraveseC)) + pointP[1];

                            if (pointT[0] >= targetScaled.getWidth() || pointT[1] >= targetScaled.getHeight()) {

                                break;
                            }
                            if (pointP[0] >= patternScaled.getWidth() || pointP[1] >= patternScaled.getHeight()) {
                                break;
                            }
                            //get pixel value
                            //System.out.println(pointT[0]+" /"+targetScaled.getWidth()+" "+pointT[1]+" /"+targetScaled.getHeight());
                            int p = patternScaled.getRGB((int) (long) pointP[0], (int) (long) pointP[1]);
                            int pT = targetScaled.getRGB((int) (long) pointT[0], (int) (long) pointT[1]);

                            if (!getColor(p).equals(Color.black)) { //Dont match exact black
                                if (count < traverseStartp) //Moving average method
                                {
                                    trackingParameters.maP = (trackingParameters.maP + p) / trackingParameters.count;
                                    trackingParameters.maT = (trackingParameters.maT + pT) / trackingParameters.count;
                                } else {
                                    trackingParameters.maP = (trackingParameters.maP + p);
                                    trackingParameters.maT = (trackingParameters.maT + pT);
                                }

                                if (p < Color.GRAY.getRGB()) {//Compute shiny area better
                                    if (count < traverseStartp) //Moving average method
                                    {
                                        trackingParameters.maP2 = (trackingParameters.maP2 + p) / trackingParameters.count;
                                        trackingParameters.maT2 = (trackingParameters.maT2 + pT) / trackingParameters.count;
                                    } else {
                                        trackingParameters.maP2 = (trackingParameters.maP2 + p);
                                        trackingParameters.maT2 = (trackingParameters.maT2 + pT);
                                    }
                                }
                            }

                            trackingParameters.didLoopComplete = traverseStartp == pointsToTravese - 1 ? true : false;
                            //
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                            //break;
                        }
                    }

                    //Go for min difference region
                    if (trackingParameters.didLoopComplete) {
                        //System.out.println("Epoch >>"+traverseStart+" maP>>"+maP+"\t maT>>"+maT);

                        double tempDecisionmaker = Math.sqrt(Math.pow(Math.abs(trackingParameters.maP - trackingParameters.maT), 2) + Math.pow(Math.abs(trackingParameters.maP2 - trackingParameters.maT2), 2));//=Math.abs(samePointsCountTarget-samePointsCount)
                        pointBestMatched = difference < tempDecisionmaker ? pointBestMatched : traverseStart;
                        difference = (int) (difference < tempDecisionmaker ? difference : tempDecisionmaker);

                    }
                }
                //
            }

            System.out.println("Epoch >>" + i + " pointBestMatched>>" + pointBestMatched + "\t difference>>" + difference);
            //For global compute
//            differenceGlobal = difference < differenceGlobal ? difference : differenceGlobal;
//            pointBestMatchedGlobal = difference < differenceGlobal ? pointBestMatched : pointBestMatchedGlobal;
//            scaleSelected = difference < differenceGlobal ? scale - 1 : scaleSelected;
//            scaleSelectedForPattern = difference < differenceGlobal ? (i + i) : scaleSelectedForPattern;
//            System.out.println("Epoch >>" + i + " difference>>" + difference + "\t differenceGlobal>>" + differenceGlobal);

            try {
                long[] pointT = cForTarget.point(pointBestMatched);
                //Scale up the location
                pointT[0] = (long) (pointT[0] * targetScaled.getWidth() / Math.sqrt(pointsToTraveseC));
                pointT[1] = (long) (pointT[1] * targetScaled.getHeight() / Math.sqrt(pointsToTraveseC));
//                System.out.println(pointT[0] + " " + pointT[1] + " " + patternScaled.getWidth() + " " + patternScaled.getHeight());
//                System.out.println(" Target>> " + targetScaled.getWidth() + " " + targetScaled.getHeight());

                Rectangle rect = new Rectangle((int) (long) pointT[0], (int) (long) pointT[1], patternScaled.getWidth(), patternScaled.getHeight());
                result[i - 1] = rect;
                differences[i - 1] = difference;
//                BufferedImage framedImage = new BufferedImage(targetScaled.getWidth(), targetScaled.getHeight(), targetScaled.getType());

                //For testing
//            Graphics2D graph = targetScaled.createGraphics();
//            graph.setColor(Color.GREEN);
//            graph.draw(rect);
//            //graph.drawImage(targetScaled, rect.x, rect.y, null);
//            graph.dispose();
//            displayImage(resizeImage(targetScaled,800,800), "extract");
                //displayImage(patternScaled, "patternScaled");
                //System.out.println(ImageIO.write(patternScaled, ".png", new File("patternScaled"+i+".png")));
                //ImageIO.write(targetScaled, ".png", new File("targetScaled"+i+".png"));
                //BufferedImage extract = cropImage(targetScaled, rect);
                //displayImage(extract, "extract");
                //ImageIO.write(extract, ".png", new File("cropContent"+i+".png"));
            } catch (Exception ex) {
                Logger.getLogger(HilbertCurvePatternDetect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        resultObject.setRectangle(result);
        resultObject.setDifference(differences);

        return resultObject;
    }

    public static BufferedImage resizeImage(Image image, int width, int height) {
        
        Dimension newDim=getScaledDimension(new Dimension(image.getWidth(null),image.getHeight(null)), new Dimension(width, height));
        width=newDim.width;
        height=newDim.height;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();

        // Increase quality if needed at the expense of speed
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        AffineTransform scaleTransform = AffineTransform.getScaleInstance(
                width / (double) image.getWidth(null), height / (double) image.getHeight(null));
        g.drawImage(image, scaleTransform, null);

        // Release resources
        g.dispose();

        return bufferedImage;
    }

    public static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        BufferedImage dest = src.getSubimage(rect.x, rect.y,
                src.getWidth() < rect.width + rect.x ? (rect.width - (rect.width + rect.x - src.getWidth())) : rect.width,
                src.getHeight() < rect.height + rect.y ? (rect.height - (rect.height + rect.y - src.getHeight())) : rect.height);
        return dest;
    }

    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    public static void displayImage(BufferedImage bimage, String message) {
        Icon icon = new ImageIcon(bimage);
        JLabel picLabel = new JLabel(icon);
        JOptionPane.showMessageDialog(null, picLabel, message, JOptionPane.PLAIN_MESSAGE, null);
    }
    
    
    public static void displayImage(BufferedImage[] bimage,int scaledTo, String message) {
        
        //PREPARE FOR RESULT DISPLAY
                BufferedImage BIG_IMAGE = new BufferedImage(
                        scaledTo * bimage.length , scaledTo * bimage.length , //work these out
                        BufferedImage.TYPE_INT_RGB);
                Graphics g = BIG_IMAGE.getGraphics();

                int x = 0, y = 0;
                for (BufferedImage resultImage : bimage) {
                    
        

                    g.drawImage(HilbertCurvePatternDetect.resizeImage(resultImage, scaledTo, scaledTo), x, y, null);
                    x += scaledTo;
                    if (x > resultImage.getWidth()) {
                        x = 0;
                        y += scaledTo;
                    }
                    
                    

                   // HilbertCurvePatternDetect.displayImage(resultImage, "getClusterPointsInteractiveTest");
                }

               //  g.drawImage(HilbertCurvePatternDetect.resizeImage(img, scaledTo, scaledTo), x, y, null);
                 
                 
        Icon icon = new ImageIcon(BIG_IMAGE);
        JLabel picLabel = new JLabel(icon);
        JOptionPane.showMessageDialog(null, picLabel, message, JOptionPane.PLAIN_MESSAGE, null);
    }
    
    /************************Color wheel for region mapping*********************
     * 
     * @param rad
     * @return 
     */
     public static BufferedImage getColourWheel(int rad) {
      //  int rad = 1024;
        BufferedImage img = new BufferedImage(rad, rad, BufferedImage.TYPE_INT_RGB);

        // Center Point (MIDDLE, MIDDLE)
        int centerX = img.getWidth() / 2;
        int centerY = img.getHeight() / 2;
        int radius = (img.getWidth() / 2) * (img.getWidth() / 2);

        // Red Source is (RIGHT, MIDDLE)
        int redX = img.getWidth();
        int redY = img.getHeight() / 2;
        int redRad = img.getWidth() * img.getWidth();

        // Green Source is (LEFT, MIDDLE)
        int greenX = 0;
        int greenY = img.getHeight() / 2;
        int greenRad = img.getWidth() * img.getWidth();

        // Blue Source is (MIDDLE, BOTTOM)
        int blueX = img.getWidth() / 2;
        int blueY = img.getHeight();
        int blueRad = img.getWidth() * img.getWidth();

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int a = i - centerX;
                int b = j - centerY;

                int distance = a * a + b * b;
                if (distance < radius) {
                    int rdx = i - redX;
                    int rdy = j - redY;
                    int redDist = (rdx * rdx + rdy * rdy);
                    int redVal = (int) (255 - ((redDist / (float) redRad) * 256));

                    int gdx = i - greenX;
                    int gdy = j - greenY;
                    int greenDist = (gdx * gdx + gdy * gdy);
                    int greenVal = (int) (255 - ((greenDist / (float) greenRad) * 256));

                    int bdx = i - blueX;
                    int bdy = j - blueY;
                    int blueDist = (bdx * bdx + bdy * bdy);
                    int blueVal = (int) (255 - ((blueDist / (float) blueRad) * 256));

                    Color c = new Color(redVal, greenVal, blueVal);

                    float hsbVals[] = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

                    Color highlight = Color.getHSBColor(hsbVals[0], hsbVals[1], 1);

                    img.setRGB(i, j, RGBtoHEX(highlight));
                } else {
                    img.setRGB(i, j, 0xFFFFFF);
                }
            }
        }

//        try {
//            ImageIO.write(img, "png", new File("wheel.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
        return img;
    }

    public static int RGBtoHEX(Color color) {
        String hex = Integer.toHexString(color.getRGB() & 0xffffff);
        if (hex.length() < 6) {
            if (hex.length() == 5)
                hex = "0" + hex;
            if (hex.length() == 4)
                hex = "00" + hex;
            if (hex.length() == 3)
                hex = "000" + hex;
        }
        hex = "#" + hex;
        return Integer.decode(hex);
    }

    public static Color getColor(int c) {

        int red = (c & 0x00ff0000) >> 16;
        int green = (c & 0x0000ff00) >> 8;
        int blue = c & 0x000000ff;
        //  System.out.println(red+" "+green+" "+blue);
        return new Color(red, blue, green);

    }

    public static BufferedImage getRandomImage(int width, int height) {
        //image dimension
//     int width = 640;
//     int height = 320;
        //create buffered image object img
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        //file object
        File f = null;
        //create random image pixel by pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = (int) (Math.random() * 256); //alpha
                int r = (int) (Math.random() * 256); //red
                int g = (int) (Math.random() * 256); //green
                int b = (int) (Math.random() * 256); //blue

                int p = (a << 24) | (r << 16) | (g << 8) | b; //pixel

                img.setRGB(x, y, p);
            }
        }
        //write image
//     try{
//       f = new File("D:\\Image\\Output.png");
//       ImageIO.write(img, "png", f);
//     }catch(IOException e){
//       System.out.println("Error: " + e);
//     }

        return img;
    }
    /*******************MATCH THE IMAGES EXACTLY**********************
     * 
     * @param imageOne
     * @param imageTwo
     * @param bitsToMatch
     * @return 
     */
    public static boolean match2ImagesExactly(BufferedImage imageOne, BufferedImage imageTwo, int bitsToMatch) {

        if (imageOne.getWidth() != imageTwo.getWidth() || imageOne.getHeight() != imageTwo.getHeight()) {
            return false;
        }

        HilbertCurve cForpattern = HilbertCurve.bits(bitsToMatch).dimensions(2);
        TrackingParameters trackingParameters = new TrackingParameters(); //use later
        int pointsToTravese = bitsToMatch * bitsToMatch;
        int[] errorArray = new int[pointsToTravese];//use later
        for (int traverseStartp = 0; traverseStartp < pointsToTravese; traverseStartp++) {
            //Get the XY in images to match
            long[] pointP = cForpattern.point(traverseStartp);
            pointP[0] = (long) (pointP[0] * imageOne.getWidth() / Math.sqrt(pointsToTravese));
            pointP[1] = (long) (pointP[1] * imageOne.getHeight() / Math.sqrt(pointsToTravese));
            long[] pointT = cForpattern.point(traverseStartp);
            pointT[0] = (long) (pointT[0] * imageTwo.getWidth() / Math.sqrt(pointsToTravese));
            pointT[1] = (long) (pointT[1] * imageTwo.getHeight() / Math.sqrt(pointsToTravese));

            //get pixel value
            try {
                //System.out.println(pointT[0]+" /"+targetScaled.getWidth()+" "+pointT[1]+" /"+targetScaled.getHeight());
                int p = imageOne.getRGB((int) (long) pointP[0], (int) (long) pointP[1]);
                int pT = imageTwo.getRGB((int) (long) pointT[0], (int) (long) pointT[1]);
                // errorArray[traverseStartp] =p-pT;
                if (p == pT) {
                } else {
                    return false;
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }

        return true;
    }
    
    /********************Image given as array of Hilbert Curve data
     * 
     * @param imageOne
     * @param bitsToMatch
     * @return 
     */
    public static long[][] image2HC(BufferedImage imageOne, int bitsToMatch) {

        HilbertCurve cForpattern = HilbertCurve.bits(bitsToMatch).dimensions(2);
        TrackingParameters trackingParameters = new TrackingParameters(); //use later
        int pointsToTravese = bitsToMatch * bitsToMatch;
        long[][] result = new long[pointsToTravese][2];
        for (int traverseStartp = 0; traverseStartp < pointsToTravese; traverseStartp++) {
            //Get the XY in images to match
            long[] pointP = cForpattern.point(traverseStartp);
            pointP[0] = (long) (pointP[0] * imageOne.getWidth() / Math.sqrt(pointsToTravese));
            pointP[1] = (long) (pointP[1] * imageOne.getHeight() / Math.sqrt(pointsToTravese));

            //get pixel value
            try {
                //System.out.println(pointT[0]+" /"+targetScaled.getWidth()+" "+pointT[1]+" /"+targetScaled.getHeight());
                int p = imageOne.getRGB((int) (long) pointP[0], (int) (long) pointP[1]);
                result[traverseStartp][0] = traverseStartp;
                result[traverseStartp][1] = p;
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }

        return result;
    }
    
    /************Represent the image as a polynomial function
     * 
     * @param imageOne
     * @param bitsToMatch
     * @return 
     */
    public static double[] getImageEquation(BufferedImage imageOne, int bitsToMatch) {
        long[][] image2HC = image2HC(imageOne, bitsToMatch);
//         List<List<Double>> pointlist=new ArrayList<>();
//         for(long[] row:image2HC){
//             List<Double> rowData=new ArrayList<>();
//             rowData.add(new Double(row[0]));
//             rowData.add(new Double(row[1]));
//             pointlist.add(rowData);
//         }

        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (long[] row : image2HC) {
            if(row[0]==0 && row[1]==0){continue;}
            obs.add(1,new Double(row[0]), new Double(row[1]));
           
        }

        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(4);
        final double[] coeff = fitter.fit(obs.toList());

        return coeff;

    }
    
    public static BufferedImage getEquationToImage(double[] coeffs, int bitsToMatch) {
        PolynomialFunction polynomial = new PolynomialFunction(coeffs);

        int pointsToTravese = bitsToMatch * bitsToMatch;

        HilbertCurve cForpattern = HilbertCurve.bits(bitsToMatch).dimensions(2);

        //PREPARE FOR RESULT DISPLAY
        BufferedImage BIG_IMAGE = new BufferedImage(
                bitsToMatch,  bitsToMatch, //work these out
                BufferedImage.TYPE_INT_RGB);
        Graphics g = BIG_IMAGE.getGraphics();

        for (int traverseStartp = 0; traverseStartp < pointsToTravese; traverseStartp++) {

            double p = polynomial.value(traverseStartp);

            Color c = getColor((int) p);

            long[] pointP = cForpattern.point(traverseStartp);
            g.setColor(c);
            g.drawRect((int) pointP[0], (int) pointP[1], 1, 1);

        }

        return BIG_IMAGE;

    }
    
    /******************Get the Score of the image
     * 
     * @param imageOne
     * @param imageTwo
     * @param bitsToMatch
     * @return 
     */
    public static Statistic match2ImagesScore(BufferedImage imageOne, BufferedImage imageTwo, int bitsToMatch) {
        
        Statistic result=new Statistic();

        HilbertCurve cForpattern = HilbertCurve.bits(bitsToMatch).dimensions(2);
        TrackingParameters trackingParameters = new TrackingParameters(); //use later
        int pointsToTravese = bitsToMatch * bitsToMatch;
        int[] errorArray = new int[pointsToTravese];//use later
        int matchCount=0;
        int variance=0;
        for (int traverseStartp = 0; traverseStartp < pointsToTravese; traverseStartp++) {
            //Get the XY in images to match
            long[] pointP = cForpattern.point(traverseStartp);
            pointP[0] = (long) (pointP[0] * imageOne.getWidth() / Math.sqrt(pointsToTravese));
            pointP[1] = (long) (pointP[1] * imageOne.getHeight() / Math.sqrt(pointsToTravese));
            long[] pointT = cForpattern.point(traverseStartp);
            pointT[0] = (long) (pointT[0] * imageTwo.getWidth() / Math.sqrt(pointsToTravese));
            pointT[1] = (long) (pointT[1] * imageTwo.getHeight() / Math.sqrt(pointsToTravese));
            
            //get pixel value
            try {
                //System.out.println(pointT[0]+" /"+targetScaled.getWidth()+" "+pointT[1]+" /"+targetScaled.getHeight());
                int p = imageOne.getRGB((int) (long) pointP[0], (int) (long) pointP[1]);
                int pT = imageTwo.getRGB((int) (long) pointT[0], (int) (long) pointT[1]);
                variance=variance+(p-pT)*(p-pT);
                // errorArray[traverseStartp] =p-pT;
                if (p == pT) {
                    matchCount++;
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }
        
        result.setVariance(variance/pointsToTravese);
        result.setStandardDeviation(Math.sqrt(variance/pointsToTravese));
        result.setSimilarityScore((double)matchCount/(double)pointsToTravese);

        return result;
    }
    
    /***********************Find Exactly matching pixel***************
     * 
     * @param imageOne
     * @param pixelValue
     * @return 
     */
    public static int exactMatchingRGB(BufferedImage imageOne, int pixelValue,int errorMargin) {
        int bitsToMatch = 63;//Max allowed
        HilbertCurve cForpattern = HilbertCurve.bits(bitsToMatch).dimensions(2);
        int pointsToTravese = bitsToMatch * bitsToMatch;
        for (int traverseStartp = 0; traverseStartp < pointsToTravese; traverseStartp++) {
            //Get the XY in images to match
            long[] pointP = cForpattern.point(traverseStartp);
            pointP[0] = (long) (pointP[0] * imageOne.getWidth() / Math.sqrt(pointsToTravese));
            pointP[1] = (long) (pointP[1] * imageOne.getHeight() / Math.sqrt(pointsToTravese));

            //get pixel value
            try {
                //System.out.println(pointT[0]+" /"+targetScaled.getWidth()+" "+pointT[1]+" /"+targetScaled.getHeight());
                int p = imageOne.getRGB((int) (long) pointP[0], (int) (long) pointP[1]);

                // errorArray[traverseStartp] =p-pT;
                if (p == pixelValue || (errorMargin+p>pixelValue && p-errorMargin<pixelValue) ){
               
                    return traverseStartp;
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }

        return -1;
    }
    
    /*******************Feature Extraction From Image*******************
     * Extract images based on clustering.
     * 1. Convert to Hilbert curve
     * 2. Cluster
     * 3. Re-construct image
     * @param imageOne
     * @param bitsToMatch
     * @return 
     */
    public static List<HilbertCurveImageResult> getFeaturesInImage(BufferedImage imageOne, int distinctClasses) {
        int bitsToMatch=63;
        BufferedImage colourWheelForRegionMAP=getColourWheel(1024);
        HilbertCurve cForpattern = HilbertCurve.bits(bitsToMatch).dimensions(2);
        List<HilbertCurveImageResult> result = new ArrayList<>();
        KMeans kmeans = new KMeans();
        TrackingParameters trackingParameters = new TrackingParameters(); //use later
        int pointsToTravese = bitsToMatch * bitsToMatch;
        int[] errorArray = new int[pointsToTravese];//use later
        long minX = 0;
        long maxX = pointsToTravese;
        long minY = Integer.MAX_VALUE;
        long maxY = Integer.MIN_VALUE;
        int xUnitPerHelbertPoint=imageOne.getWidth()/bitsToMatch;
        int yUnitPerHelbertPoint=imageOne.getHeight() / bitsToMatch;
        for (int traverseStartp = 0; traverseStartp < pointsToTravese; traverseStartp++) {
            //Get the XY in images to match
            long[] pointP = cForpattern.point(traverseStartp);
            pointP[0] = xUnitPerHelbertPoint/2+(long) (pointP[0] * imageOne.getWidth() / Math.sqrt(pointsToTravese));
            pointP[1] = yUnitPerHelbertPoint/2+(long) (pointP[1] * imageOne.getHeight() / Math.sqrt(pointsToTravese));

            //get pixel value
            try {
                //System.out.println(pointT[0]+" /"+targetScaled.getWidth()+" "+pointT[1]+" /"+targetScaled.getHeight());
                //Look at the select region, this can be put in parallel executor.
                int p =-1,p1=-1,p2=-1,p3=-1;
                
                for (int x = -xUnitPerHelbertPoint / 2; x < xUnitPerHelbertPoint / 2; x++)
                    for (int y = -yUnitPerHelbertPoint / 2; y < yUnitPerHelbertPoint / 2; y++) {
                        p = (p1+p2+p3+imageOne.getRGB((int) (long) pointP[0]+x, (int) (long) pointP[1]+y))/4;//Moving average
                        p3=p2;p2=p1;p1=p;
                        
                        minY = p < minY ? p : minY;
                        maxY = p > maxY ? p : maxY;
                    }
                            
                //kmeans.points.add(new Point(traverseStartp, exactMatchingRGB(colourWheelForRegionMAP, p, 1000)));
                kmeans.points.add(new Point(traverseStartp, p));

            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }

        kmeans.init(minX, maxX, minY, maxY,distinctClasses);//5 Features for now
        kmeans.calculate();

        for (Cluster cluster : kmeans.clusters) {
            BufferedImage bimg = new BufferedImage(imageOne.getWidth(), imageOne.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = bimg.createGraphics();
            graphics.setBackground(new Color(0x00FFFFFF, true));
            minX = Integer.MAX_VALUE;
            maxX = Integer.MIN_VALUE;
            minY = Integer.MAX_VALUE;
            maxY = Integer.MIN_VALUE;
            
            HilbertCurveImageResult hcir=new HilbertCurveImageResult();

            int objectsInACluster=0;
            BigInteger tempHBCNumber=BigInteger.ZERO;
            for (Point point : cluster.points) {

                long[] pointP = cForpattern.point((int) point.getX());
                pointP[0] = (long) (pointP[0] * imageOne.getWidth() / Math.sqrt(pointsToTravese));
                pointP[1] = (long) (pointP[1] * imageOne.getHeight() / Math.sqrt(pointsToTravese));

                //Find min max bounds
                minX = pointP[0] < minX ? pointP[0] : minX;
                maxX = pointP[0] > maxX ? pointP[0] : maxX;

                minY = pointP[1] < minY ? pointP[1] : minY;
                maxY = pointP[1] > maxY ? pointP[1] : maxY;
                
                //Count objects in cluster
                if(tempHBCNumber==BigInteger.ZERO){
                    tempHBCNumber=cForpattern.index(pointP);
                }else if(tempHBCNumber.add(BigInteger.ONE).longValue()<=cForpattern.index(pointP).longValue()+10 &&tempHBCNumber.add(BigInteger.ONE).longValue()>=cForpattern.index(pointP).longValue()-10) {
                    objectsInACluster++;
                    tempHBCNumber=BigInteger.ZERO;//reset the value to start counter
                }

                //get pixel value
                try {
                    //System.out.println(pointT[0]+" /"+targetScaled.getWidth()+" "+pointT[1]+" /"+targetScaled.getHeight());
                    int p = (int) point.getY();
                    
                    for(int x=-xUnitPerHelbertPoint/2;x<xUnitPerHelbertPoint/2;x++)
                        for(int y=-yUnitPerHelbertPoint/2;y<yUnitPerHelbertPoint/2;y++)
                            bimg.setRGB(x+(int) pointP[0], y+(int) pointP[1],imageOne.getRGB(x+(int) pointP[0], y+(int) pointP[1]));
                } catch (Exception ex) {
                    //ex.printStackTrace();
                }
            }
            
            System.out.println("objectsInACluster="+objectsInACluster);

            BufferedImage regiionImage=new BufferedImage((int)(maxX-minX), (int)(maxY-minY), imageOne.getType());
            
            //result.add(bimg);
            hcir.setFullImage(bimg);
            hcir.setxMin(minX);hcir.setyMin(minY);hcir.setxMax(maxX);hcir.setyMax(maxY);
            hcir.setRegionWidth((int)(maxX-minX));hcir.setRegionHeight((int)(maxY-minY));
            hcir.setCluster(cluster);
            result.add(hcir);
        }

        return result;
    }

}

class Result {

    Result() {
    }

    public Result(int size) {
    }

    public Rectangle[] getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle[] rectangle) {
        this.rectangle = rectangle;
    }

    public long[] getPointBestMatched() {
        return pointBestMatched;
    }

    public void setPointBestMatched(long[] pointBestMatched) {
        this.pointBestMatched = pointBestMatched;
    }

    public double[] getDifference() {
        return difference;
    }

    public void setDifference(double[] difference) {
        this.difference = difference;
    }
    Rectangle[] rectangle;
    long[] pointBestMatched;
    double[] difference;

}

class TrackingParameters {

    public double getMaP() {
        return maP;
    }

    public void setMaP(double maP) {
        this.maP = maP;
    }

    public double getMaP2() {
        return maP2;
    }

    public void setMaP2(double maP2) {
        this.maP2 = maP2;
    }

    public double getMaT() {
        return maT;
    }

    public void setMaT(double maT) {
        this.maT = maT;
    }

    public double getMaT2() {
        return maT2;
    }

    public void setMaT2(double maT2) {
        this.maT2 = maT2;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public boolean isDidLoopComplete() {
        return didLoopComplete;
    }

    public void setDidLoopComplete(boolean didLoopComplete) {
        this.didLoopComplete = didLoopComplete;
    }

    double maP = 0, maP2 = 0;
    double maT = 0, maT2 = 0;
    double count = 5;
    boolean didLoopComplete = false;
}

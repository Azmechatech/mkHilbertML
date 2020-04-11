/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml.functions;

import com.truegeometry.mkhilbertml.HilbertCurvePatternDetect;
import com.truegeometry.mkhilbertml.pojo.Statistic;
import com.truegeometry.mkhilbertml.solver.BasicSolver;
import java.awt.image.BufferedImage;
import org.davidmoten.hilbert.HilbertCurve;

/**
 * This class will help in finding given HUE, Saturation and Light adjustment
 * for given two images to look same.
 * @author Manoj
 */
public class DecodeColorDiff {
    
    public static void diffValues(BufferedImage sourceImage,BufferedImage destinationImage, ImageOperationInterface ioi){
        /**
         * 1. Get hilbert curve points for sourceImage
         * 2. Get  hilbert curve points for destinationImage
         * 3. Construct 2 small image
         * 4. Perform HUE, Saturation and Light adjustment till both the images are same.
         * 
         */
        int bitsToMatch=63;//64x64px image
        BufferedImage miniSourceImage=getMiniImage(sourceImage, bitsToMatch);
        BufferedImage miniDestinationImage=getMiniImage(destinationImage, bitsToMatch);
       // HilbertCurvePatternDetect.
         Statistic stat=new Statistic();
         stat.setSimilarityScore(1);//Exact match.
         BasicSolver.solve(miniSourceImage, miniDestinationImage, ioi, stat, bitsToMatch);
        
    }
    
    
    public static BufferedImage getMiniImage(BufferedImage sourceImage,int bitsToMatch){
    
        HilbertCurve cForpattern = HilbertCurve.bits(bitsToMatch).dimensions(2);
        int xUnitPerHelbertPoint=sourceImage.getWidth()/bitsToMatch;
        int yUnitPerHelbertPoint=sourceImage.getHeight() / bitsToMatch;
        int pointsToTravese = bitsToMatch * bitsToMatch;
        BufferedImage newImage=new BufferedImage(bitsToMatch, bitsToMatch, sourceImage.getType());
         for (int traverseStartp = 0; traverseStartp < pointsToTravese; traverseStartp++) {
             //Get the XY in images to match
            long[] pointP = cForpattern.point(traverseStartp);
            newImage.setRGB((int)pointP[0], (int)pointP[1], bitsToMatch);
            
            pointP[0] = xUnitPerHelbertPoint/2+(long) (pointP[0] * sourceImage.getWidth() / Math.sqrt(pointsToTravese));
            pointP[1] = yUnitPerHelbertPoint/2+(long) (pointP[1] * sourceImage.getHeight() / Math.sqrt(pointsToTravese));
            
            int rgba=sourceImage.getRGB((int) (long) pointP[0], (int) (long) pointP[1]);
            pointP = cForpattern.point(traverseStartp);
            newImage.setRGB((int)pointP[0], (int)pointP[1], rgba);
         }
         
         return newImage;
    }
}

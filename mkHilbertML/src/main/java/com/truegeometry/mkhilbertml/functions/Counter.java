/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml.functions;

import com.truegeometry.mkhilbertml.HilbertCurveImageResult;
import com.truegeometry.mkhilbertml.HilbertCurvePatternDetect;
import com.truegeometry.mkhilbertml.Point;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author Manoj
 */
public class Counter {
    
    /**
     * Checks for continuity of the Hilbert curve, if the continuity breaks, it means that feature has multiple variations. 
     * @param img
     * @param numberOfFeaures
     * @return 
     */
    public static List<HilbertCurveImageResult> populateObjectCounts(BufferedImage img,int numberOfFeaures ){
        List< HilbertCurveImageResult> result = HilbertCurvePatternDetect.getFeaturesInImage(img, numberOfFeaures);
        for (HilbertCurveImageResult hcir : result) {
            //List< HilbertCurveImageResult> subResult = HilbertCurvePatternDetect.getFeaturesInImage(hcir.getRegionImage(), 2);//only 2 class is needed, FG and BG
            double tempX = 0;
            for (Point point : hcir.getCluster().points) {
                if ((point.getX() - 1) == tempX) {//The continuity check.
                    tempX = point.getX();
                    System.out.println("tempX="+tempX +" & point.getX()="+point.getX());
                    continue;
                } else {
                    hcir.setFeatureCount(hcir.getFeatureCount() + 1);
                    tempX = point.getX();

                }

            }
        }
        
        return result;
    }
}

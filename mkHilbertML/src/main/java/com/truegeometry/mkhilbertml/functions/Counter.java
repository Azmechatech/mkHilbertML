/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml.functions;

import com.truegeometry.mkhilbertml.pojo.HilbertCurveImageResult;
import com.truegeometry.mkhilbertml.HilbertCurvePatternDetect;
import com.truegeometry.mkhilbertml.Point;
import com.truegeometry.mkhilbertml.pojo.FeatureIdentificationData;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
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
                   // System.out.println("tempX="+tempX +" & point.getX()="+point.getX());
                    continue;
                } else {
                    hcir.setFeatureCount(hcir.getFeatureCount() + 1);
                    tempX = point.getX();

                }

            }
        }
        
        return result;
    }
    
     /**
     * 
     * @param sourceImage
     * @param stopPercentage Percent at which iteration will stop
     * @return 
     */
    public static List<HilbertCurveImageResult> getPossibleFeatures(BufferedImage sourceImage, double stopPercentage) {
        double oldErrorInColor = Double.MAX_VALUE;
        double oldErrorInShape = Double.MAX_VALUE;
        List< HilbertCurveImageResult> result = null;
        for (int bitsToMatch =32; bitsToMatch < 64; bitsToMatch++) {
            boolean optimalResultAchieved = false;
            for (int numberOfFeaures = 2; numberOfFeaures < bitsToMatch; numberOfFeaures++) {
                result = HilbertCurvePatternDetect.getFeaturesInImage(sourceImage, numberOfFeaures, bitsToMatch);
                double newErrorInColor = 0;
                double newErrorInShape = 0;
                for (HilbertCurveImageResult features : result) {
                    if(features.getCluster().getPoints().size()<2) continue;//Ignore anything very small
                    newErrorInColor = (long) (newErrorInColor + features.getStatisticOfColor().getStandardDeviation());
                    newErrorInShape = (long) (newErrorInShape + features.getStatisticOfShape().getStandardDeviation());
                }
                
                double percentChangeInErrorOfColor = 100 *Math.abs(oldErrorInColor - newErrorInColor) / oldErrorInColor;
                double percentChangeInErrorOfShape = 100 *Math.abs (oldErrorInShape - newErrorInShape) / newErrorInShape;
                System.out.println("percentChangeInErrorOfColor="+percentChangeInErrorOfColor+" numberOfFeaures="+numberOfFeaures+" bitsToMatch="+bitsToMatch);

                oldErrorInColor = oldErrorInColor > newErrorInColor ? newErrorInColor : oldErrorInColor;
                oldErrorInShape = oldErrorInShape > newErrorInShape ? newErrorInShape : oldErrorInShape;

                if (percentChangeInErrorOfColor < stopPercentage) {
                    optimalResultAchieved = true;
                    break;
                }
               
            }
            if (optimalResultAchieved) {
                break;
            }
        }

        return result;
    }

    public static List<HilbertCurveImageResult> getPossibleFeatures(BufferedImage sourceImage, double stopPercentage,int depth) {
        double oldErrorInColor = Double.MAX_VALUE;
        double oldErrorInShape = Double.MAX_VALUE;
        FeatureIdentificationData featureIdentificationData = new FeatureIdentificationData();
        featureIdentificationData.setSignature2D(HilbertCurvePatternDetect.image2HC(sourceImage, 63));
        List< HilbertCurveImageResult> result = new LinkedList<>();

        boolean optimalResultAchieved = false;


        //While
        while (!optimalResultAchieved) {
            int bitsToMatch = 32+(int) (Math.random() * 31);
            int numberOfFeaures = (int) (Math.random() * bitsToMatch);
            numberOfFeaures=numberOfFeaures<2?2:numberOfFeaures;//Zero check 
            Long[] key=new Long[]{ (long)bitsToMatch,(long) numberOfFeaures};
            if( featureIdentificationData.getRewardTable().containsKey(key)) continue;// Try other combination
            List< HilbertCurveImageResult> tempResult = HilbertCurvePatternDetect.getFeaturesInImage(sourceImage, numberOfFeaures, bitsToMatch);
            double newErrorInColor = 0;
            double newErrorInShape = 0;
            for (HilbertCurveImageResult features : result) {
                if (features.getCluster().getPoints().size() < 2) {
                    continue;//Ignore anything very small
                }
                newErrorInColor = (long) (newErrorInColor + features.getStatisticOfColor().getStandardDeviation());
                newErrorInShape = (long) (newErrorInShape + features.getStatisticOfShape().getStandardDeviation());
            }

            double percentChangeInErrorOfColor = 100 * Math.abs(oldErrorInColor - newErrorInColor) / oldErrorInColor;
            double percentChangeInErrorOfShape = 100 * Math.abs(oldErrorInShape - newErrorInShape) / newErrorInShape;
            System.out.println("depth="+depth+ " featureIdentificationData.getRewardTable()="+featureIdentificationData.getRewardTable().size()+" percentChangeInErrorOfColor=" + percentChangeInErrorOfColor + " numberOfFeaures=" + numberOfFeaures + " bitsToMatch=" + bitsToMatch);

            //Store the infromation
            featureIdentificationData.getRewardTable().put(new Long[]{ (long)bitsToMatch,(long) numberOfFeaures}, percentChangeInErrorOfColor);

            oldErrorInColor = oldErrorInColor > newErrorInColor ? newErrorInColor : oldErrorInColor;
            oldErrorInShape = oldErrorInShape > newErrorInShape ? newErrorInShape : oldErrorInShape;

            if (percentChangeInErrorOfColor < stopPercentage) {
                optimalResultAchieved = true;
                result.addAll(tempResult);
                if((depth-1)>0)
                for (HilbertCurveImageResult features : result) {
                    result.addAll(getPossibleFeatures(features.getRegionImage(),  stopPercentage, depth-1));
                }
                
                
                break;
            }
        }
        //End While

        return result;
    }

}

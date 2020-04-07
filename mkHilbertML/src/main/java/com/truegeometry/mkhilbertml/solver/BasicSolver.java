/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml.solver;

import com.truegeometry.mkhilbertml.HilbertCurvePatternDetect;
import com.truegeometry.mkhilbertml.Statistic;
import com.truegeometry.mkhilbertml.functions.ImageOperationInterface;
import java.awt.image.BufferedImage;

/**
 *
 * @author Manoj
 */
public class BasicSolver {

    public static Object solve(BufferedImage sourceImage, BufferedImage destinationImage, ImageOperationInterface ioi, Statistic targetStatistic,long iterations) {
        Statistic currentStaistic = new Statistic();
        long iterationCount=0;
        while (currentStaistic.getSimilarityScore() != targetStatistic.getSimilarityScore()) //&& currentStaistic.getMedian()!=targetStatistic.getMedian()&& currentStaistic.getMode()!=targetStatistic.getMode()){
        {
            if(iterationCount>=iterations) break;
            destinationImage = ioi.applyOperation(destinationImage);
            currentStaistic = HilbertCurvePatternDetect.match2ImagesScore(sourceImage, destinationImage, 63);
            iterationCount++;
        }
        return ioi.getCurrentParameters();
    }

}
    


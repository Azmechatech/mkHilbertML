/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml;

import org.json.JSONObject;

/**
 *
 * @author mkfs
 */
public class Statistic {
    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getMode() {
        return mode;
    }

    public void setMode(double mode) {
        this.mode = mode;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }
    
        private double similarityScore;

    /**
     * Get the value of similarityScore
     *
     * @return the value of similarityScore
     */
    public double getSimilarityScore() {
        return similarityScore;
    }

    /**
     * Set the value of similarityScore
     *
     * @param similarityScore new value of similarityScore
     */
    public void setSimilarityScore(double similarityScore) {
        this.similarityScore = similarityScore;
    }

    
    double mean,median,mode,variance,standardDeviation;
    
    public JSONObject getJSONObject(){
        JSONObject jb=new JSONObject();
        jb.put("mean", mean);
        jb.put("median", median);
        jb.put("mode", mode);
        jb.put("variance", variance);
        jb.put("standardDeviation", standardDeviation);
        
        return jb;
    
    }
}

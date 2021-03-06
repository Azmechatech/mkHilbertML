/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml.pojo;

import com.truegeometry.mkhilbertml.Cluster;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author Manoj
 */
public class HilbertCurveImageResult {
    
    BufferedImage fullImage;
    public static Color bgColor=new Color(0x00FFFFFF, true);
    long featureCount=0;
    
    long xMin,yMin,xMax,yMax;
    int regionWidth,regionHeight;
    double error;
    long errorAbsolute;
    Cluster cluster;
    Statistic statisticOfColor= new Statistic();
    Statistic statisticOfShape= new Statistic();

    public BufferedImage getFullImage() {
        return fullImage;
    }

    public void setFullImage(BufferedImage fullImage) {
        this.fullImage = fullImage;
    }

    
    public BufferedImage getRegionImage() {
        
        return cropImage(fullImage);
    }


    public long getxMin() {
        return xMin;
    }

    public void setxMin(long xMin) {
        this.xMin = xMin;
    }

    public long getyMin() {
        return yMin;
    }

    public void setyMin(long yMin) {
        this.yMin = yMin;
    }

    public long getxMax() {
        return xMax;
    }

    public void setxMax(long xMax) {
        this.xMax = xMax;
    }

    public long getyMax() {
        return yMax;
    }

    public void setyMax(long yMax) {
        this.yMax = yMax;
    }

    public int getRegionWidth() {
        return regionWidth;
    }

    public void setRegionWidth(int regionWidth) {
        this.regionWidth = regionWidth;
    }

    public int getRegionHeight() {
        return regionHeight;
    }

    public void setRegionHeight(int regionHeight) {
        this.regionHeight = regionHeight;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public long getErrorAbsolute() {
        return errorAbsolute;
    }

    public void setErrorAbsolute(long errorAbsolute) {
        this.errorAbsolute = errorAbsolute;
    }

    public static Color getBgColor() {
        return bgColor;
    }

    public static void setBgColor(Color bgColor) {
        HilbertCurveImageResult.bgColor = bgColor;
    }

    public long getFeatureCount() {
        return featureCount;
    }

    public void setFeatureCount(long featureCount) {
        this.featureCount = featureCount;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Statistic getStatisticOfColor() {
        return statisticOfColor;
    }

    public Statistic getStatisticOfShape() {
        return statisticOfShape;
    }
    
    
    private BufferedImage cropImage(BufferedImage src) {
        Rectangle rect = new Rectangle((int) xMin, (int) yMin, (int) (xMax - xMin), (int) (yMax - yMin));

        try{
        BufferedImage dest = src.getSubimage(rect.x, rect.y, rect.width, rect.height);
        return dest;
        }catch (Exception ex){return fullImage;}
    }
    
    
}

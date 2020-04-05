/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author Manoj
 */
public class HilbertCurveImageResult {
    
    BufferedImage fullImage,regionImage;
    public static Color bgColor=new Color(0x00FFFFFF, true);

    long xMin,yMin,xMax,yMax;
    int regionWidth,regionHeight;
    double error;
    long errorAbsolute;

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
    
    
    private BufferedImage cropImage(BufferedImage src) {
        Rectangle rect=new Rectangle((int) xMin, (int) yMin, (int)(xMax-xMin), (int)(yMax-yMin));
        
      BufferedImage dest = src.getSubimage(rect.x, rect.y, rect.width, rect.height);
      return dest; 
   }
    
    
}

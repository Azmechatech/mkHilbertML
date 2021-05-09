/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml.ImageTo3D;

import java.awt.image.BufferedImage;

/**
 *
 * @author ryzen
 */
public class ImagePOJO {

    BufferedImage image;
    int xmin, ymin, xmax, ymax;
    double distance=0.0;
    String label;
    
    double signature;
    double eigenSignature[];

    public ImagePOJO(BufferedImage image, int xmin, int ymin, int xmax, int ymax) {
        this.image = image;
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getXmin() {
        return xmin;
    }

    public int getYmin() {
        return ymin;
    }

    public int getXmax() {
        return xmax;
    }

    public int getYmax() {
        return ymax;
    }

    public double getSignature() {
        return signature;
    }

    public void setSignature(double signature) {
        this.signature = signature;
    }

    public double[] getEigenSignature() {
        return eigenSignature;
    }

    public void setEigenSignature(double[] eigenSignature) {
        this.eigenSignature = eigenSignature;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    
}

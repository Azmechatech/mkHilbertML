/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Point {

    private double x = 0;
    private double y = 0;
    private int cluster_number = 0;

    public Point(double x, double y)
    {
        this.setX(x);
        this.setY(y);
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getX()  {
        return this.x;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void setCluster(int n) {
        this.cluster_number = n;
    }
    
    public int getCluster() {
        return this.cluster_number;
    }
    
    //Calculates the distance between two points.
    protected static double distance(Point p, Point centroid) {
        return Math.sqrt(Math.pow((centroid.getY() - p.getY()), 2) + Math.pow((centroid.getX() - p.getX()), 2));
    }
    
    //Creates random point
    protected static Point createRandomPoint(int min, int max) {
    	Random r = new Random();
    	double x = min + (max - min) * r.nextDouble();
    	double y = min + (max - min) * r.nextDouble();
    	return new Point(x,y);
    }
    
     protected static Point createRandomPoint(long minX, long maxX,long minY, long maxY) {
    	Random r = new Random();
    	double x = minX + (maxX - minX) * r.nextDouble();
    	double y = minY + (maxY - minY) * r.nextDouble();
    	return new Point(x,y);
    }
    
    public static List createRandomPoints(int min, int max, int number) {
    	List points = new ArrayList(number);
    	for(int i = 0; i < number; i++) {
    		points.add(createRandomPoint(min,max));
    	}
    	return points;
    }
    
    public String toString() {
    	return "("+x+","+y+")";
    }
}

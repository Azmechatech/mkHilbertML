/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml;

import com.truegeometry.mkhilbertml.pojo.Statistic;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.feature.detect.edge.EdgeSegment;
import boofcv.alg.feature.shapes.ShapeFittingOps;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
//import boofcv.alg.shapes.ShapeFittingOps;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.struct.ConnectRule;
import boofcv.struct.PointIndex_I32;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt32;
import boofcv.struct.image.ImageUInt8;
import com.truegeometry.mkhilbertml.ImageTo3D.ImagePOJO;
 
import georegression.struct.point.Point2D_I32;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.image.ShowImages;
import boofcv.io.UtilIO;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.ConnectRule;
 

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

public class FitPolygon {

    // Used to bias it towards more or fewer sides. larger number = fewer sides
    static double cornerPenalty = 0.25;
    // The fewest number of pixels a side can have
    static int minSide = 10;

    static ListDisplayPanel gui = new ListDisplayPanel();

    /**
     * Fits polygons to found contours around binary blobs.
     *
     * @param input
     */
    public static void fitBinaryImage(ImageFloat32 input) {

        ImageUInt8 binary = new ImageUInt8(input.width, input.height);
        BufferedImage polygon = new BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB);

        // the mean pixel value is often a reasonable threshold when creating a binary image
        double mean = ImageStatistics.mean(input);

        // create a binary image by thresholding
        ThresholdImageOps.threshold(input, binary, (float) mean, true);

        // reduce noise with some filtering
        ImageUInt8 filtered = BinaryImageOps.erode8(binary, 1, null);
        filtered = BinaryImageOps.dilate8(filtered, 1, null);

        // Find internal and external contour around each shape
        List<Contour> contours = BinaryImageOps.contour(filtered, ConnectRule.EIGHT, null);

        // Fit a polygon to each shape and draw the results
        Graphics2D g2 = polygon.createGraphics();
        g2.setStroke(new BasicStroke(2));

        for (Contour c : contours) {
            // Fit the polygon to the found external contour.  Note loop = true
            List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(c.external, true, minSide, cornerPenalty, 4);

            g2.setColor(Color.RED);
            VisualizeShapes.drawPolygon(vertexes, true, g2);

            // handle internal contours now
            g2.setColor(Color.BLUE);
            for (List<Point2D_I32> internal : c.internal) {
                vertexes = ShapeFittingOps.fitPolygon(internal, true, minSide, cornerPenalty, 4);
                VisualizeShapes.drawPolygon(vertexes, true, g2);
            }
        }

        gui.addImage(polygon, "Binary Blob Contours");
    }

    /**
     * Fits a sequence of line-segments into a sequence of points found using
     * the Canny edge detector. In this case the points are not connected in a
     * loop. The canny detector produces a more complex tree and the fitted
     * points can be a bit noisy compared to the others.
     */
    public static void fitCannyEdges(ImageFloat32 input) {

        BufferedImage displayImage = new BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB);

        // Finds edges inside the image
        CannyEdge<ImageFloat32, ImageFloat32> canny
                = FactoryEdgeDetectors.canny(2, true, true, ImageFloat32.class, ImageFloat32.class);

        canny.process(input, 0.1f, 0.3f, null);
        List<EdgeContour> contours = canny.getContours();

        Graphics2D g2 = displayImage.createGraphics();
        g2.setStroke(new BasicStroke(2));

        // used to select colors for each line
        Random rand = new Random(234);

        for (EdgeContour e : contours) {
            g2.setColor(new Color(rand.nextInt()));

            for (EdgeSegment s : e.segments) {
                // fit line segments to the point sequence.  Note that loop is false
                List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(s.points, false, minSide, cornerPenalty, 4);

                VisualizeShapes.drawPolygon(vertexes, false, g2);
            }
        }

        gui.addImage(displayImage, "Canny Trace");
    }

    /**
     * Detects contours inside the binary image generated by canny. Only the
     * external contour is relevant. Often easier to deal with than working with
     * Canny edges directly.
     */
    public static void fitCannyBinary(ImageFloat32 input) {

        BufferedImage displayImage = new BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB);
        ImageUInt8 binary = new ImageUInt8(input.width, input.height);

        // Finds edges inside the image
        CannyEdge<ImageFloat32, ImageFloat32> canny
                = FactoryEdgeDetectors.canny(2, false, true, ImageFloat32.class, ImageFloat32.class);

        canny.process(input, 0.1f, 0.3f, binary);
        ImageSInt32 output = null;
        // Only external contours are relevant
        List<Contour> contours = BinaryImageOps.contour(binary, ConnectRule.EIGHT, output);

        Graphics2D g2 = displayImage.createGraphics();
        g2.setStroke(new BasicStroke(2));

        // used to select colors for each line
        Random rand = new Random(234);

        for (Contour c : contours) {
            List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(c.external, true, minSide, cornerPenalty, 4);

            g2.setColor(new Color(rand.nextInt()));
            VisualizeShapes.drawPolygon(vertexes, true, g2);
        }

        gui.addImage(displayImage, "Canny Contour");
    }

    /**
     * 
     * @param image
     * @return 
     */
    public static BufferedImage getCannyBinary(BufferedImage image) {
        ImageFloat32 input = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
        BufferedImage displayImage = new BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB);
        ImageUInt8 binary = new ImageUInt8(input.width, input.height);

        ImageSInt32 label = new ImageSInt32(input.width, input.height);

        // Finds edges inside the image
        CannyEdge<ImageFloat32, ImageFloat32> canny
                = FactoryEdgeDetectors.canny(2, false, true, ImageFloat32.class, ImageFloat32.class);

        canny.process(input, 0.1f, 0.3f, binary);

        // Only external contours are relevant
        List<Contour> contours = BinaryImageOps.contour(binary, ConnectRule.EIGHT, label);

        Graphics2D g2 = displayImage.createGraphics();
        g2.setStroke(new BasicStroke(2));

        // used to select colors for each line
        Random rand = new Random(234);

        for (Contour c : contours) {
            List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(c.external, true, minSide, cornerPenalty, 5);

            g2.setColor(new Color(rand.nextInt()));
            VisualizeShapes.drawPolygon(vertexes, true, g2);
        }

        //gui.addImage(displayImage, "Canny Contour");
        return displayImage;
    }

    /**
     * 
     * @param image
     * @return 
     */
    public static BufferedImage getCannyEdges(BufferedImage image) {
        ImageFloat32 input = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
        BufferedImage displayImage = new BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB);

        // Finds edges inside the image
        CannyEdge<ImageFloat32, ImageFloat32> canny
                = FactoryEdgeDetectors.canny(2, true, true, ImageFloat32.class, ImageFloat32.class);

        canny.process(input, 0.1f, 0.3f, null);
        List<EdgeContour> contours = canny.getContours();

        Graphics2D g2 = displayImage.createGraphics();
        g2.setStroke(new BasicStroke(2));

        // used to select colors for each line
        Random rand = new Random(234);

        for (EdgeContour e : contours) {
            g2.setColor(new Color(rand.nextInt()));

            for (EdgeSegment s : e.segments) {
                // fit line segments to the point sequence.  Note that loop is false
                List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(s.points, false, minSide, cornerPenalty, 10);

                VisualizeShapes.drawPolygon(vertexes, false, g2);
            }
        }

        return displayImage;
    }

    /**
     * 
     * @param image
     * @return 
     */
    public static List<EdgeContour> getCannyEdgesXY(BufferedImage image) {
        ImageFloat32 input = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
        // Finds edges inside the image
        CannyEdge<ImageFloat32, ImageFloat32> canny
                = FactoryEdgeDetectors.canny(2, true, true, ImageFloat32.class, ImageFloat32.class);

        canny.process(input, 0.1f, 0.3f, null);
        List<EdgeContour> contours = canny.getContours();
        return contours;
    }
    
    /**
     * 
     * @param image
     * @return 
     */
    public static List<ImagePOJO> getCannyImages(BufferedImage image) {
        ImageFloat32 input = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
        // Finds edges inside the image
        CannyEdge<ImageFloat32, ImageFloat32> canny
                = FactoryEdgeDetectors.canny(2, true, true, ImageFloat32.class, ImageFloat32.class);

        canny.process(input, 0.1f, 0.3f, null);
        List<EdgeContour> contours = canny.getContours();

        List<ImagePOJO> result = new ArrayList<>();
        

        contours.forEach(cont -> {
            int xmin = Integer.MAX_VALUE, ymin = Integer.MAX_VALUE, xmax = Integer.MIN_VALUE, ymax = Integer.MIN_VALUE;
            List<double[]> pointsArray = new ArrayList<>();
            List<EdgeSegment> segs = cont.segments;

            for (EdgeSegment eg : segs) {
                List<Point2D_I32> points = eg.points;
                for (Point2D_I32 point : points) {
                    xmin = xmin < point.x ? xmin : point.x;
                    ymin = ymin < point.y ? ymin : point.y;
                    xmax = xmax > point.x ? xmax : point.x;
                    ymax = ymax > point.y ? ymax : point.y;
                    pointsArray.add(new double[]{point.x, point.y});
                }
            }

            try{
            ImagePOJO imagePOJO = new ImagePOJO(image.getSubimage(xmin, ymin, xmax - xmin, ymax - ymin), xmin, ymin, xmax, ymax);
            imagePOJO.setLabel("["+result.size()+"]");
            imagePOJO.setSignature(getSignature(pointsArray));
            result.add(imagePOJO);
            }catch(Exception ex){}

        });

        return result;
    }
    
    
    
    /**
     * 
     * @param edgeCountourList1
     * @param edgeCountourList2
     * @return 
     */
    public static double[][][] contourShift(List<EdgeContour> edgeCountourList1, List<EdgeContour> edgeCountourList2) {
        /**
         * 1. Need to find the closest contours by shape, 2. Estimate the
         * apparent pixel shift 3. return the pixel shift for each of the
         * matching pair of the edge contour.
         */

        Integer counterContour = 0;
        HashMap<String, Double> result1 = getSignatures(edgeCountourList1);
        HashMap<String, Double> result2 = getSignatures(edgeCountourList2);
        
        List<Set<String>> closeMatches=new ArrayList<>();
        

        return null;

    }
    
     public static double getSignature(List<double[]> pointsArray) {
        //List<double[]> pointsArray = new ArrayList<>();
//        for (EdgeSegment edgeSegment : edgeContour.segments) {
//
//            for (int i = 0; i < edgeSegment.points.size(); i++) {
//                Point2D_I32 p2d = edgeSegment.points.get(i);
//                //pointsArray[i][0] = p2d.x;
//                //pointsArray[i][1] = p2d.y;
//                pointsArray.add(new double[]{p2d.x, p2d.y});
//                // gr.drawOval(p2d.x, p2d.y,1, 1);
//            }
//        }

        double[][] pointsArrayD = new double[pointsArray.size()][2];
        //create real matrix
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(pointsArrayD);

        //create covariance matrix of points, then find eigen vectors
        //see https://stats.stackexchange.com/questions/2691/making-sense-of-principal-component-analysis-eigenvectors-eigenvalues
        Covariance covariance = new Covariance(realMatrix);
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        EigenDecomposition ed = new EigenDecomposition(covarianceMatrix);
        double[] eignValues = ed.getRealEigenvalues();
        double sumOfEV = eignValues[0] + eignValues[1];
        double signature = Math.sqrt(eignValues[0] * eignValues[0] + eignValues[1] * eignValues[1]);

        return signature;

    }
     
    /**
     * 
     * @param edgeContour
     * @return 
     */
    public static double getSignature(EdgeContour edgeContour) {
        List<double[]> pointsArray = new ArrayList<>();
        for (EdgeSegment edgeSegment : edgeContour.segments) {
            for (int i = 0; i < edgeSegment.points.size(); i++) {
                Point2D_I32 p2d = edgeSegment.points.get(i);
                pointsArray.add(new double[]{p2d.x, p2d.y});
 
            }
        }

        double[][] pointsArrayD = new double[pointsArray.size()][2];
        //create real matrix
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(pointsArrayD);

        //create covariance matrix of points, then find eigen vectors
        //see https://stats.stackexchange.com/questions/2691/making-sense-of-principal-component-analysis-eigenvectors-eigenvalues
        Covariance covariance = new Covariance(realMatrix);
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        EigenDecomposition ed = new EigenDecomposition(covarianceMatrix);
        double[] eignValues = ed.getRealEigenvalues();
        double sumOfEV = eignValues[0] + eignValues[1];
        double signature = Math.sqrt(eignValues[0] * eignValues[0] + eignValues[1] * eignValues[1]);

        return signature;

    }
    /**
     * 
     * @param edgeCountourList1
     * @return 
     */
    public static HashMap<String, Double> getSignatures(List<EdgeContour> edgeCountourList1) {

        Integer counterContour = 0;
        HashMap<String, Double> result = new HashMap<>();
        for (EdgeContour edgeContour : edgeCountourList1) {
            Integer counterSegments = 0;
            for (EdgeSegment edgeSegment : edgeContour.segments) {
                double[][] pointsArray = new double[edgeSegment.points.size()][2];
                for (int i = 0; i < edgeSegment.points.size(); i++) {
                    Point2D_I32 p2d = edgeSegment.points.get(i);
                    pointsArray[i][0] = p2d.x;
                    pointsArray[i][1] = p2d.y;
                    // gr.drawOval(p2d.x, p2d.y,1, 1);

                }

                //create real matrix
                RealMatrix realMatrix = MatrixUtils.createRealMatrix(pointsArray);

                //create covariance matrix of points, then find eigen vectors
                //see https://stats.stackexchange.com/questions/2691/making-sense-of-principal-component-analysis-eigenvectors-eigenvalues
                Covariance covariance = new Covariance(realMatrix);
                RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
                EigenDecomposition ed = new EigenDecomposition(covarianceMatrix);
                double[] eignValues = ed.getRealEigenvalues();
                double sumOfEV = eignValues[0] + eignValues[1];
                double signature = Math.sqrt(eignValues[0] * eignValues[0] + eignValues[1] * eignValues[1]);
                result.put(counterContour + "_" + counterSegments, signature);
                counterSegments++;
            }
            counterSegments++;

        }

        return result;
    }
    
    
    
    
    public static Statistic getImageStatistics( BufferedImage image ) {
                ImageFloat32 input = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
                Statistic statistic=new Statistic();
                
                statistic.setMean(ImageStatistics.mean(input));
                statistic.setVariance(ImageStatistics.variance(input,ImageStatistics.mean(input)));
                statistic.setStandardDeviation(Math.sqrt(statistic.getVariance()));

		return  statistic;
	}
    
    public static void main(String args[]) {
        // load and convert the image into a usable format
//		BufferedImage image = UtilImageIO.loadImage(UtilIO.pathExample("shapes/shapes02.png"));
//		GrayF32 input = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);
//
//		fitCannyEdges(input);
//		fitCannyBinary(input);
//		fitBinaryImage(input);
//		gui.addImage(image,"Original");
//
//		ShowImages.showWindow(gui, "Polygon from Contour", true);
    }
}

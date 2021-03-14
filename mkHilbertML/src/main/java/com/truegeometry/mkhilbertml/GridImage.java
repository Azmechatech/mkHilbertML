/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Source:  https://codereview.stackexchange.com/questions/20529/slicing-up-an-image-into-rows-and-columns-in-java
 * @author mkfs
 */
public class GridImage implements Runnable  {
     private BufferedImage image;
    private int rows, columns;
    private BufferedImage[][] smallImages;
    private int smallWidth;
    private int smallHeight;

    public GridImage(String filename, int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        try {
            image = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.smallWidth = image.getWidth() / columns;
        this.smallHeight = image.getHeight() / rows;
        smallImages = new BufferedImage[columns][rows];
    }

    public void run() {
        int count = 0;
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                smallImages[x][y] = image.getSubimage(x * smallWidth, y
                        * smallHeight, smallWidth, smallHeight);
                try {
                    ImageIO.write(smallImages[x][y], "png", new File("tile-"
                            + (count++) + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        GridImage image = new GridImage("img/card-grid-image-mass-effect.jpg",
                4, 15);
        new Thread( image).start();
    }
    
    /**
     * 
     * @param bi
     * @param rowKV k-> start y and V- end y
     * @return 
     */
    public static List<BufferedImage> splitImages(BufferedImage bi, HashMap<Integer, Integer> rowKV) {
        List<BufferedImage> result = new LinkedList<>();
        rowKV.entrySet().forEach(kv -> {
            result.add(bi.getSubimage(0, kv.getKey(), bi.getWidth(), kv.getValue() - kv.getKey()));
        });

        return result;
    }
    
    /**
     * 1. read each row
     * 2. get r,g and b values
     * 3. find std of r g and b
     * 4. the rows for which it is 0 to be considered as seprator
     * 5. Based on the seprator row r,g and b do a start and end of the 
     * image boundary.
     * @param bi
     * @return 
     */
    
    public static HashMap<Integer, Integer> rowLevelSplit(BufferedImage bi) {

         HashMap<Integer, Integer>  result=new HashMap<>();
        int count = 0;
        boolean sepratorON = false;
        int key=-1;
        for (int y = 0; y < bi.getHeight(); y++) {
            DescriptiveStatistics stats = new DescriptiveStatistics();
            for (int x = 0; x < bi.getWidth(); x++) {

                int c = bi.getRGB(x, y);
                int red = (c & 0x00ff0000) >> 16;
                int green = (c & 0x0000ff00) >> 8;
                int blue = c & 0x000000ff;

                stats.addValue(c);
            }

            double std = stats.getStandardDeviation();

            if (std == 0) {

                if (sepratorON != true) {//Entry 
                    System.out.println("std is zero! for row " + y);
                    if(key!=-1)result.put(key, y);
                }

                sepratorON = true;
            } else {
                
                 if (sepratorON != false) {//Entry 
                     key=y;
                    System.out.println("std is NOT zero! for row " + y);
                }
                 
                sepratorON = false;
            }
        }

        return result;

    }
}

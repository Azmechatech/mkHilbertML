/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml.ImageTo3D;
/*
 * Copyright (c) 2020, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 

import boofcv.alg.feature.detect.template.TemplateMatching;
import boofcv.alg.feature.detect.template.TemplateMatchingIntensity;
import boofcv.alg.misc.ImageStatistics;
import boofcv.alg.misc.PixelMath;
import boofcv.factory.feature.detect.template.FactoryTemplateMatching;
import boofcv.factory.feature.detect.template.TemplateScoreType;
import boofcv.gui.image.ShowImages;
import boofcv.gui.image.VisualizeImageData;
import boofcv.io.UtilIO;
//import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.feature.Match;
//import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageFloat32;
//import boofcv.struct.image.ImageGray;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * https://boofcv.org/index.php?title=Example_Template_Matching
 * Example of how to find objects inside an image using template matching.  Template matching works
 * well when there is little noise in the image and the object's appearance is known and static.  It can
 * also be very slow to compute, depending on the image and template size.
 *
 * @author Peter Abeles
 * Updated by azmechatech@gmail.com
 */
public class ExampleTemplateMatching {
	/**
	 * Demonstrates how to search for matches of a template inside an image
	 *
	 * @param image           Image being searched
	 * @param template        Template being looked for
	 * @param mask            Mask which determines the weight of each template pixel in the match score
	 * @param expectedMatches Number of expected matches it hopes to find
	 * @return List of match location and scores
	 */
    public  static List<Match> findMatches(ImageFloat32 image, ImageFloat32 template,
            int expectedMatches) {
        // create template matcher.
        TemplateMatching<ImageFloat32> matcher = FactoryTemplateMatching.createMatcher(TemplateScoreType.SUM_DIFF_SQ, ImageFloat32.class);

        // Find the points which match the template the best
        //matcher.setImage(image);
        matcher.setTemplate(template, expectedMatches);

        matcher.process(image);

        return matcher.getResults().toList();
    }

    public  static List<Match> findMatches(BufferedImage imagebi, BufferedImage templatebi,
            int expectedMatches) {
        ImageFloat32 image = boofcv.core.image.ConvertBufferedImage.convertFromSingle(imagebi, null, ImageFloat32.class);
        ImageFloat32 template = boofcv.core.image.ConvertBufferedImage.convertFromSingle(templatebi, null, ImageFloat32.class);
        // create template matcher.
        TemplateMatching<ImageFloat32> matcher = FactoryTemplateMatching.createMatcher(TemplateScoreType.SUM_DIFF_SQ, ImageFloat32.class);

        // Find the points which match the template the best
        //matcher.setImage(image);
        matcher.setTemplate(template, expectedMatches);

        matcher.process(image);

        return matcher.getResults().toList();
    }

	/**
	 * Computes the template match intensity image and displays the results. Brighter intensity indicates
	 * a better match to the template.
	 */
	   private static void showMatchIntensity(ImageFloat32 image, ImageFloat32 template, ImageFloat32 mask) {
        // create algorithm for computing intensity image
        TemplateMatchingIntensity<ImageFloat32> matchIntensity
                = FactoryTemplateMatching.createIntensity(TemplateScoreType.SUM_DIFF_SQ, ImageFloat32.class);

        // apply the template to the image
        //matchIntensity.setInputImage(image);
        matchIntensity.process(template, mask);

        // get the results
        ImageFloat32 intensity = matchIntensity.getIntensity();

        // White will indicate a good match and black a bad match, or the reverse
        // depending on the cost function used.
        float min = ImageStatistics.min(intensity);
        float max = ImageStatistics.max(intensity);
        float range = max - min;
        PixelMath.plus(intensity, -min, intensity);
        PixelMath.divide(intensity, range, intensity);
        PixelMath.multiply(intensity, 255.0f, intensity);

        BufferedImage output = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_BGR);
        //VisualizeImageData.grayMagnitude(new ImageGray, output, -1);
      //  ShowImages.showWindow(output, "Match Intensity", true);
    }

//    public static void main(String[] args) {
//        // Load image and templates
//        String directory = UtilIO.pathExample("template");
//
//        GrayF32 image = UtilImageIO.loadImage(directory, "desktop.png", GrayF32.class);
//        GrayF32 templateCursor = UtilImageIO.loadImage(directory, "cursor.png", GrayF32.class);
//        GrayF32 maskCursor = UtilImageIO.loadImage(directory, "cursor_mask.png", GrayF32.class);
//        GrayF32 templatePaint = UtilImageIO.loadImage(directory, "paint.png", GrayF32.class);
//
//        // create output image to show results
//        BufferedImage output = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_BGR);
//        ConvertBufferedImage.convertTo(image, output);
//        Graphics2D g2 = output.createGraphics();
//
//        BufferedImage outputtemplateCursor = new BufferedImage(templateCursor.width, templateCursor.height, BufferedImage.TYPE_INT_BGR);
//        ConvertBufferedImage.convertTo(templateCursor, outputtemplateCursor);
//
//        BufferedImage outputmaskCursor = new BufferedImage(maskCursor.width, maskCursor.height, BufferedImage.TYPE_INT_BGR);
//        ConvertBufferedImage.convertTo(maskCursor, outputmaskCursor);
//
//        // Search for the cursor in the image.  For demonstration purposes it has been pasted 3 times
//        g2.setColor(Color.RED);
//        g2.setStroke(new BasicStroke(5));
//
//        // show match intensity image for this template
//        ImageFloat32 input = boofcv.core.image.ConvertBufferedImage.convertFromSingle(output, null, ImageFloat32.class);
//        ImageFloat32 inputtemplateCursor = boofcv.core.image.ConvertBufferedImage.convertFromSingle(outputtemplateCursor, null, ImageFloat32.class);
//        ImageFloat32 inputmaskCursor = boofcv.core.image.ConvertBufferedImage.convertFromSingle(outputmaskCursor, null, ImageFloat32.class);
//
//        drawRectangles(g2, input, inputtemplateCursor, 3);
//
//        showMatchIntensity(input, inputtemplateCursor, inputmaskCursor);
//
//        // Now it's try finding the cursor without a mask.  it will get confused when the background is black
//        g2.setColor(Color.BLUE);
//        g2.setStroke(new BasicStroke(2));
//        drawRectangles(g2, input, inputtemplateCursor, 3);
//
//        // Now it searches for a specific icon for which there is only one match
////		g2.setColor(Color.ORANGE); g2.setStroke(new BasicStroke(3));
////		drawRectangles(g2, input, templatePaint, null, 1);
//        ShowImages.showWindow(output, "Found Matches", true);
//    }

    /**
     * Helper function will is finds matches and displays the results as colored
     * rectangles
     */
    private static void drawRectangles(Graphics2D g2,
            ImageFloat32 image, ImageFloat32 template,
            int expectedMatches) {
        List<Match> found = findMatches(image, template, expectedMatches);

        int r = 2;
        int w = template.width + 2 * r;
        int h = template.height + 2 * r;

        for (Match m : found) {
            System.out.printf("Match %3d %3d    score = %6.2f\n", m.x, m.y, m.score);
            // this demonstrates how to filter out false positives
            // the meaning of score will depend on the template technique
//			if( m.score < -5 )  // This line is commented out for demonstration purposes
//				continue;

            // the return point is the template's top left corner
            int x0 = m.x - r;
            int y0 = m.y - r;
            int x1 = x0 + w;
            int y1 = y0 + h;

            g2.drawLine(x0, y0, x1, y0);
            g2.drawLine(x1, y0, x1, y1);
            g2.drawLine(x1, y1, x0, y1);
            g2.drawLine(x0, y1, x0, y0);
        }
    }
}
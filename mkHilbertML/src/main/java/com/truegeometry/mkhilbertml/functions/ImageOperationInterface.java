/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml.functions;

import java.awt.image.BufferedImage;

/**
 *
 * @author Manoj
 */
public interface ImageOperationInterface   {
    BufferedImage applyOperation(BufferedImage inputImage);
    Object getCurrentParameters();
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author ryzen
 */
public class RealTimeImageTransform {
    
    public static void main(String[] args) {
        new RealTimeImageTransform();
    }

    public RealTimeImageTransform() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

        private BufferedImage img;
        private Point imgPoint = new Point(0, 0);

        public TestPane() {
            try {
                img = ImageIO.read(new File("C:\\Users\\ryzen\\Pictures\\Capture100CPU.PNG"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            MouseAdapter ma = new MouseAdapter() {

                private Point offset;

                @Override
                public void mousePressed(MouseEvent e) {
                    Rectangle bounds = getImageBounds();
                    Point mp = e.getPoint();
                    if (bounds.contains(mp)) {
                        offset = new Point();
                        offset.x = mp.x - bounds.x;
                        offset.y = mp.y - bounds.y;
                        
                        //Define split point
                    }
                    
                    
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    offset = null;
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (offset != null) {
                        Point mp = e.getPoint();
                        imgPoint.x = mp.x - offset.x;
                        imgPoint.y = mp.y - offset.y;
                        
                        //Tranform the split image
                        
                        repaint();
                    }
                }

            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        protected Rectangle getImageBounds() {
            Rectangle bounds = new Rectangle(0, 0, 0, 0);
            if (img != null) {
                bounds.setLocation(imgPoint);
                bounds.setSize(img.getWidth(), img.getHeight());
            }
            return bounds;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(img, imgPoint.x, imgPoint.y, this);
                g2d.dispose();
            }
        }
    }
    
    public void splitAndSaveImage( BufferedImage image,int atX,int atY ) throws IOException
{
    // Process image ------------------------------------------         
    int height = image.getHeight();
    int width = image.getWidth();
    boolean edgeDetected = false;
    double averageColor = 0;
    int threshold = -10;
    int rightEdge = 0;
    int leftEdge = 0;
    int middle = 0;

    // Split the image at the middle of the inside distance.
    middle = (leftEdge + rightEdge)/2;

    // Crop the image
    BufferedImage leftImage = image.getSubimage(0, 0, middle, height);

    BufferedImage rightImage = image.getSubimage(middle, 0, (width-middle), height);

    // Save the image
    // Save to file -------------------------------------------
    ImageIO.write(leftImage, "jpeg", new File("leftImage.jpeg"));

    ImageIO.write(rightImage, "jpeg", new File("rightImage.jpeg"));
}
    
    public void splitAndSaveImage( BufferedImage image ) throws IOException
{
    // Process image ------------------------------------------         
    int height = image.getHeight();
    int width = image.getWidth();
    boolean edgeDetected = false;
    double averageColor = 0;
    int threshold = -10;
    int rightEdge = 0;
    int leftEdge = 0;
    int middle = 0;

    // Scan the image and determine the edges of the blobs.
    for(int w = 0; w < width; ++w)
    {               
        for(int h = 0; h < height; ++h)
        {
            averageColor += image.getRGB(w, h);
        }

        averageColor = Math.round(averageColor/(double)height);

        if( averageColor /*!=-1*/< threshold && !edgeDetected )
        {
            // Detected the beginning of the right blob
            edgeDetected = true;
            rightEdge = w;
        }else if( averageColor >= threshold && edgeDetected )
        {
            // Detected the end of the left blob
            edgeDetected = false;
            leftEdge = leftEdge==0? w:leftEdge;
        }

        averageColor = 0;
    }

    // Split the image at the middle of the inside distance.
    middle = (leftEdge + rightEdge)/2;

    // Crop the image
    BufferedImage leftImage = image.getSubimage(0, 0, middle, height);

    BufferedImage rightImage = image.getSubimage(middle, 0, (width-middle), height);

    // Save the image
    // Save to file -------------------------------------------
    ImageIO.write(leftImage, "jpeg", new File("leftImage.jpeg"));

    ImageIO.write(rightImage, "jpeg", new File("rightImage.jpeg"));
}
}

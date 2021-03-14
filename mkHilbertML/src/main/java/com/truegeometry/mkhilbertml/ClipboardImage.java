/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml;

/**
 * Source: https://stackoverflow.com/questions/26599834/select-copy-and-paste-images
 * @author mkfs
 */
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;
public class ClipboardImage
{
    /**
     *  Retrieve an image from the system clipboard.
     *
     *  @return the image from the clipboard or null if no image is found
     */
    public static Image read()
    {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents( null );

        try
        {
            if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor))
            {
                Image image = (Image)t.getTransferData(DataFlavor.imageFlavor);
                return image;
            }
        }
        catch (Exception e) {}

        return null;
    }

    /**
     *  Place an image on the system clipboard.
     *
     *  @param  image - the image to be added to the system clipboard
     */
    public static void write(Image image)
    {
        if (image == null)
            throw new IllegalArgumentException ("Image can't be null");

        ImageTransferable transferable = new ImageTransferable( image );
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
    }

    static class ImageTransferable implements Transferable
    {
        private Image image;

        public ImageTransferable (Image image)
        {
            this.image = image;
        }

        public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException
        {
            if (isDataFlavorSupported(flavor))
            {
                return image;
            }
            else
            {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public boolean isDataFlavorSupported (DataFlavor flavor)
        {
            return flavor == DataFlavor.imageFlavor;
        }

        public DataFlavor[] getTransferDataFlavors ()
        {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }
    }

    public static void main(String[] args)
    {
        //Image image = Toolkit.getDefaultToolkit ().createImage("???.jpg");
        //ClipboardImage.write( image );
        Image clipBoardImg=ClipboardImage.read();
        javax.swing.ImageIcon icon = new javax.swing.ImageIcon( clipBoardImg );
        
        BufferedImage bi = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
// paint the Icon to the BufferedImage.
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        
        List<BufferedImage> bimgs=GridImage.splitImages(bi,  GridImage.rowLevelSplit(bi));

        javax.swing.JLabel label = new javax.swing.JLabel( icon );
        
        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.setDefaultCloseOperation( javax.swing.JFrame.EXIT_ON_CLOSE );
        
        JPanel subPanel1 = new JPanel();
subPanel1.setPreferredSize (new Dimension(400, 400));
subPanel1.setBackground (Color.cyan);
//subPanel1.add(label);
        
        frame.getContentPane().add( subPanel1 );
        
        bimgs.forEach(bimg->{
           subPanel1.add(new javax.swing.JLabel( new javax.swing.ImageIcon( bimg )));
        });
        
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
    }
}
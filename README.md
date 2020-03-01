# mkHilbertML
An ML library that combines Hilbert Curve(s) with the Classic ML algorithms like k-means clustering to match up deep learning.
Some information about Hilbert Curve can be obtained from Wikipedia link https://en.wikipedia.org/wiki/Hilbert_curve
```
Both the true Hilbert curve and its discrete approximations are useful because they give a mapping between 1D and 2D space that preserves locality fairly well.[4] This means that two data points which are close to each other in one-dimensional space are also close to each other after folding. The converse can't always be true.

Because of this locality property, the Hilbert curve is widely used in computer science. For example, the range of IP addresses used by computers can be mapped into a picture using the Hilbert curve. Code to generate the image would map from 2D to 1D to find the color of each pixel, and the Hilbert curve is sometimes used because it keeps nearby IP addresses close to each other in the picture.

A grayscale photograph can be converted to a dithered black-and-white image using thresholding, with the leftover amount from each pixel added to the next pixel along the Hilbert curve. Code to do this would map from 1D to 2D, and the Hilbert curve is sometimes used because it does not create the distracting patterns that would be visible to the eye if the order were simply left to right across each row of pixels. Hilbert curves in higher dimensions are an instance of a generalization of Gray codes, and are sometimes used for similar purposes, for similar reasons. For multidimensional databases, Hilbert order has been proposed to be used instead of Z order because it has better locality-preserving behavior. For example, Hilbert curves have been used to compress and accelerate R-tree indexes[5] (see Hilbert R-tree). They have also been used to help compress data warehouses.[6][7] 
```
## Example : Detect Comet :)
### Source Image | Fed to detect 3 Objects 
![Source Image](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_BrightComet.PNG)
### Result of object clustering using  mkHilbertML | It has done what we need it to
![Result](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_BrightComet_Result.PNG)

Following is the simple code to get started:
```java
BufferedImage img = ImageIO.read(new File("Path to image file"));
int numberOfFeaures= 3 ;// Given 3 for comet, you can vary it as needed.
List< BufferedImage> result = HilbertCurvePatternDetect.getFeaturesInImage(img, numberOfFeaures);
//Display result
HilbertCurvePatternDetect.resizeImage(resultImage, 300, 300);
```
## Example : Detect Comet :)
### Source Image
![Source Image](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_Comet1970.PNG)
### Result of object clustering using  mkHilbertML 
![Result](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_Comet1970_Result.PNG)


### This is a Java (maven) project. Application can be tested by running https://github.com/Azmechatech/mkHilbertML/blob/master/mkHilbertML/src/main/java/com/truegeometry/mkhilbertml/HilberCurvePatternDetectTests.java

## Example 1
### Source Image
![Source Image](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_BW.PNG)
### Result of object clustering using  mkHilbertML 
![Result](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_BW_result.PNG)

## Example 2
### Source Image
![Source Image](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_BW_Strip.PNG)
### Result of object clustering  mkHilbertML 
![Result](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_BW_Strip_Result.PNG)

## Example 3
### Source Image
![Source Image](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_BW_Box.PNG)
### Result of object clustering  mkHilbertML 
![Result](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_BW_Box_Result.PNG)


## Example 4
### Source Image
![Source Image](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_BW_Hand.PNG)
### Result of object clustering  mkHilbertML 
![Result](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_BW_Hand_Result.PNG)

## Example 5
### Source Image
![Source Image](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_BW_Zebra.PNG)
### Result of object clustering  mkHilbertML 
![Result](https://github.com/Azmechatech/mkHilbertML/blob/master/images/Example_BW_Zebra_Result.PNG)

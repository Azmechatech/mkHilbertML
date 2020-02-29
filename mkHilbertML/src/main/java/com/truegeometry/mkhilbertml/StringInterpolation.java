/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml;

/**
 *
 * @author mkfs
 */
public class StringInterpolation {

    public static String NUMBERSERIES = "0123456789";
    public static String CHARACTERSERIESSMALL = "abcdefghijklmnopqrstuvwxwz";
    public static String CHARACTERSERIESCAPITOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String SPECIALCHARACTERSERIES = "!@#$%^&*()_+={}|[]|:;,.<>?/";

    public static String SEARCHSPACEQUANTUM[] = {NUMBERSERIES, CHARACTERSERIESSMALL, CHARACTERSERIESCAPITOL, SPECIALCHARACTERSERIES};

    public static StringBuilder generateMatrixAndTransformToOneDimension(String numberSeries, String seprator) {
        StringBuilder oneDimensionSeries = new StringBuilder();
        for (int i = 0; i < numberSeries.length(); i++) {
            for (int j = 0; j < numberSeries.length(); j++) {
                char c1 = numberSeries.charAt(i);
                char c2 = numberSeries.charAt(j);
                oneDimensionSeries.append(c1).append(c2).append(seprator);
            }
        }
        return oneDimensionSeries;
    }

    public static String getNext(String first, String second) {

        String tempSEARCHSPACEQUANTUM = "";

        for (int i = 0; i < first.length(); i++) {
            for (int j = 0; j < second.length(); j++) {
                char c1 = first.charAt(i);
                char c2 = second.charAt(j);

                for (String series : SEARCHSPACEQUANTUM) {
                    if (series.contains(c1 + "") || series.contains(c2 + "")) {
                        if (!tempSEARCHSPACEQUANTUM.contains(series)) {
                            tempSEARCHSPACEQUANTUM = tempSEARCHSPACEQUANTUM + series;
                        }
                    }
                }

            }
        }

        //Generate search space
        String searchSpace = generateMatrixAndTransformToOneDimension(tempSEARCHSPACEQUANTUM, "").toString();

        if (searchSpace.contains(first) && searchSpace.contains(second)) {
            int stringLoc1 = searchSpace.indexOf(first);
            int stringLoc2 = searchSpace.indexOf(second);

            return searchSpace.substring(stringLoc1>stringLoc2?stringLoc2:stringLoc1, stringLoc2>stringLoc1?stringLoc2:stringLoc1);
        }
        return null;

    }
    
    public static void main(String... args){
        String first="b";
        String second="1";
        
        System.out.println(getNext( first, second));
    }
}

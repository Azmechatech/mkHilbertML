/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.truegeometry.mkhilbertml.pojo;

import com.truegeometry.mkhilbertml.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Manoj
 */
public class FeatureIdentificationData implements Serializable {

    /**
     * 1. Get image signature 2. Get set of identified conditions and the
     * reward.
     */

    private  long[][] signature2D ;
    private final HashMap<Long[], Double> rewardTable = new LinkedHashMap<>();

    public void setSignature2D(long[][] signature2D) {
        this.signature2D = signature2D;
    }

    public long[][] getSignature2D() {
        return signature2D;
    }

    public HashMap<Long[], Double> getRewardTable() {
        return rewardTable;
    }

    
}

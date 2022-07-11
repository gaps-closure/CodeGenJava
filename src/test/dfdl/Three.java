/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Mar 10, 2022
 */
package test.dfdl;

import java.io.Serializable;

public class Three extends Two_1
{
    private float threeFloat = 31.0f;
    private double threeDouble = 32.0;
    
    private float[] threeFloatArray = new float[] {33.0f, 34.0f};
    private double[] threeDoubleArray = new double[] {35.0, 36.0};
    
//    private String threeString2;
    private String threeString1 = "This is a string.";
    private Data obj1 = new Data();
//    private Data obj2 = new Data();
//    private int[] threeIntB = new int[] {37, 38};
//    private int[] threeFloatA;
    
    public Three() {
//        threeFloatA = new int[] {39, 40};
    }
}

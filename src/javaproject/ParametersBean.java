/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaproject;

public class ParametersBean {
    private int high;
    private int low;
    
    public ParametersBean(int high,int low){
        this.high = high;
        this.low = low;
    }
    
     public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }
    
     public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unitn.dsantoro.a3server;

import java.util.Random;
import javax.ejb.Stateless;

/**
 *
 * @author Daniele Santoro <daniele.santoro@studenti.unitn.it>
 */
@Stateless
public class Trade implements TradeRemote {
    
    @Override
    public float currentValue(float nominalValue) {
        System.out.println("--- Converting stock nominalValue to currentValue ---");
        System.out.println("Stock nominal value: "+ nominalValue);
        float minX = 0.95f;
        float maxX = 1.05f;
        Random rand = new Random();
        float perc = rand.nextFloat() * (maxX - minX) + minX;
	System.out.println("Percentage applied: "+ perc);
	float currentValue = nominalValue * perc;
        currentValue = round(currentValue);
	System.out.println("Stock current value: "+ currentValue);      
        return currentValue;
    }
    
    private static float round(float n){
        int nI = (int)(n*10); 
	return (float)nI/10;		
    }
}

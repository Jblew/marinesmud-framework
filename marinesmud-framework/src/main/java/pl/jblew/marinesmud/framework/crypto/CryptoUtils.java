/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.crypto;

import java.security.SecureRandom;

/**
 *
 * @author teofil
 */
public class CryptoUtils {
    private static final SecureRandom RANDOM = new SecureRandom();
    private CryptoUtils() {}
    
    public static String getRandomString() {
        return RANDOM.nextLong()+"";
    }
}

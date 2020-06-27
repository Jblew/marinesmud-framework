/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.util;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author teofil
 */
public class TrueUID {
    private static final AtomicInteger INCREMENTER = new AtomicInteger(0);
    
    private TrueUID() {}
    
    public static long obtainUniqueId() {
        byte [] idBytes = Ints.toByteArray(INCREMENTER.incrementAndGet());
        long timestamp = System.currentTimeMillis();
        byte [] bytes = Longs.toByteArray(timestamp);
        bytes[0] = idBytes[2];
        bytes[1] = idBytes[3];
        return Longs.fromByteArray(bytes);
    }
}

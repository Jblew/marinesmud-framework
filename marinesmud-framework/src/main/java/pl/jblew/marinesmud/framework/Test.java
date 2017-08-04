/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework;

/**
 *
 * @author teofil
 */
public class Test {
    public static void main(String [] args) {
        byte flag0 = 1;
        byte flag1 = 1 << 1;
        byte flag2 = 1 << 2;
        byte flag3 = 1 << 3;
        byte flag4 = 1 << 4;
        byte flag5 = 1 << 5;
        byte flag6 = 1 << 6;
        byte flag7 = Byte.MIN_VALUE;
        
        //byte flag4 = 1 << 1;
        
        byte flagset = (byte) (flag0 | flag4 | flag6 | flag7);//(byte) (flag0 & flag3);
        
        System.out.println("flag0: "+Integer.toString(flag0&0xff, 2));
        System.out.println("flag1: "+Integer.toString(flag1&0xff, 2));
        System.out.println("flag2: "+Integer.toString(flag2&0xff, 2));
        System.out.println("flag3: "+Integer.toString(flag3&0xff, 2));
        System.out.println("flag4: "+Integer.toString(flag4&0xff, 2));
        System.out.println("flag5: "+Integer.toString(flag5&0xff, 2));
        System.out.println("flag6: "+Integer.toString(flag6&0xff, 2));
        System.out.println("flag7: "+Integer.toString(flag7&0xff, 2));
        System.out.println("flagset: "+Integer.toString(flagset&0xff, 2));
        System.out.println("Flag0 set: "+((flagset & flag0) != 0));
        System.out.println("Flag1 set: "+((flagset & flag1) != 0));
        System.out.println("Flag2 set: "+((flagset & flag2) != 0));
        System.out.println("Flag3 set: "+((flagset & flag3) != 0));
        System.out.println("Flag4 set: "+((flagset & flag4) != 0));
        System.out.println("Flag5 set: "+((flagset & flag5) != 0));
        System.out.println("Flag6 set: "+((flagset & flag6) != 0));
        System.out.println("Flag7 set: "+((flagset & flag7) != 0));
        
        
    }
}

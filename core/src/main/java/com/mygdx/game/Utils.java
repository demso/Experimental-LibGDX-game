package com.mygdx.game;

public class Utils {
    public static boolean[] intToBitArray(int input){
        boolean[] bits = new boolean[32];
        for (int i = 31; i >= 0; i--) {
            bits[i] = (input & (1 << i)) != 0;
        }
        return bits;
    }

    public static boolean[] shortToBitArray(int input){
        boolean[] bits = new boolean[16];
        for (int i = 15; i >= 0; i--) {
            bits[i] = (input & (1 << i)) != 0;
        }
        return bits;
    }

    public static String intToBitString(int input){
        boolean[] bits = intToBitArray(input);
        StringBuilder sb = new StringBuilder();

        for (int i = 31; i >= 0; i--) {
            sb.append(bits[i] ? 1 : 0);
            if (i % 8 == 0)
                sb.append(" ");
        }
        return sb.toString();
    }

    public static String shortToBitString(short input){
        boolean[] bits = shortToBitArray(input);
        StringBuilder sb = new StringBuilder();

        for (int i = 15; i >= 0; i--) {
            sb.append(bits[i] ? 1 : 0);
            if (i % 8 == 0)
                sb.append(" ");
        }
        return sb.toString();
    }

    public static float round(float value, int digs) {
        return Math.round(value * digs*10) / (digs*10f);
    }
}

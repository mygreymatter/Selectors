package com.mayo.avengers;

/**
 * Created by mayo on 14/9/15.
 */
public class Avengers {
    private Avengers(){}

    private static Avengers avengers = null;

    public static Avengers getAvengers(){
        if(null == avengers)
            avengers = new Avengers();
        return avengers;
    }

    public int position = -1;
}

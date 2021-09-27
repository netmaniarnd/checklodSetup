package com.netmania.checklod;

import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void byteCheck() {
        // 240 : -16
        // 128 : -128
        System.out.println(u2b(256));
    }

    public static byte u2b(int in) {
        if(in > 127) {
            return (byte) (in - 256);
        } else {
            return (byte) in;
        }
    }
}
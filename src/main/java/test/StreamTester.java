package test;

import me.panjohnny.jip.util.AESUtil;
import me.panjohnny.jip.util.ByteUtil;
import me.panjohnny.jip.util.Bytes;

import java.io.*;
import java.util.Arrays;

public class StreamTester {
    public static void main(String[] args) throws Exception{
        byte[] bytes = new byte[(int) (2 * Math.pow(10, 9))];
        Arrays.fill(bytes, (byte) 0xFF); // Naplní pole bajtů hodnotou 0xFF

        // Write bytes to file
        try (OutputStream out = new FileOutputStream("large.txt")) {
            out.write(bytes);
        }
    }

}

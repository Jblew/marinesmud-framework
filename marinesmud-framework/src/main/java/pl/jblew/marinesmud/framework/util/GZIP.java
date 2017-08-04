/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.util;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author teofil
 */
public class GZIP {
    private GZIP() {}
    
    public static byte [] compress(byte [] in) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gos = new GZIPOutputStream(baos)) {
                gos.write(in);
            }
            return baos.toByteArray();
        }
    }
    
        public static byte [] decompress(byte [] in) throws IOException {
        try (InputStream is = new GZIPInputStream(new ByteArrayInputStream(in))) {
            return ByteStreams.toByteArray(is);
        }
    }
}

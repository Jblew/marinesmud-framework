/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.crypto.encryptors;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidParameterSpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.apache.commons.codec.binary.Base64;
import pl.jblew.marinesmud.framework.crypto.CryptoUtils;

/**
 *
 * @author teofil
 */
public interface Encryptor {
    public static final Algorithm DEFAULT_ALGORITHM = Algorithm.NO_ENCRYPTION;
    //public static byte[] EMPTY_KEY = new byte[0];

    public byte[] encrypt(byte[] in, byte[] key);

    public byte[] decrypt(byte[] in, byte[] key);

    /*public default byte[] encryptString(String in, byte[] key) {
        return encrypt(in.getBytes(StandardCharsets.UTF_8), key);
    }

    public default String decryptToString(byte[] in, byte[] key) {
        return new String(decrypt(in, key), StandardCharsets.UTF_8);
    }

    public default String encryptStringToString(String in, byte[] key) {
        String out = Base64.encodeBase64URLSafeString(encryptString(in, key));
        //System.out.println("<E>: \""+in+"\" => \""+out+"\", key="+Base64.encodeBase64URLSafeString(key));
        return out;
    }

    public default String decryptStringFromString(String in, byte[] key) {
        String out = decryptToString(Base64.decodeBase64(in), key);
        //System.out.println("<D>: \""+in+"\" => \""+out+"\", key="+Base64.encodeBase64URLSafeString(key));
        return out;
    }*/

    public static Algorithm getAlgorithm(byte code) {
        for (Algorithm a : Algorithm.values()) {
            if (a.code == code) {
                return a;
            }
        }
        throw new NoSuchAlgorithmException();
    }

    public static Algorithm getAlgorithm(byte[] in) {
        return getAlgorithm(in[0]);
    }

    public static Algorithm getAlgorithm(String in) {
        return getAlgorithm(Base64.decodeBase64(in));
    }

    /*public static final byte NO_ENCRYPTION = 0x00;
    public static final byte DEFAULT = NO_ENCRYPTION;
    
    public static byte [] encryptString(byte mode, String in) {
        return encrypt(mode, in.getBytes(StandardCharsets.UTF_8));
    }
    
    public static String decryptString(byte [] in) {
        return new String(decrypt(in), StandardCharsets.UTF_8);
    }
    
    public static byte [] encrypt(byte mode, byte [] in) {
        switch(mode){
            case NO_ENCRYPTION:
                return emptyEncrypt(in);
            default:
                throw new RuntimeException("No such encryption algorithm!");
        }
    }
    
    public static byte [] decrypt(byte [] in) {
        byte mode = in[0];
        switch(mode){
            case NO_ENCRYPTION:
                return emptyDecrypt(in);
            default:
                throw new RuntimeException("No such encryption algorithm!");
        }
    }

    private static byte[] emptyEncrypt(byte[] unencrypted) {
        byte [] out = new byte [unencrypted.length+1];
        out[0] = NO_ENCRYPTION;
        System.arraycopy(unencrypted, 0, out, 1, unencrypted.length);
        return out;
    }
    
    private static byte[] emptyDecrypt(byte[] encrypted) {
        byte [] out = new byte [encrypted.length-1];
        System.arraycopy(encrypted, 1, out, 0, encrypted.length-1);
        return out;
    }*/
    public static enum Algorithm implements Encryptor {
        NO_ENCRYPTION((byte) 0) {
            @Override
            public byte[] encrypt(byte[] unencrypted, byte[] key) {
                byte[] out = new byte[unencrypted.length + 1];
                out[0] = 0;//algorithm code
                System.arraycopy(unencrypted, 0, out, 1, unencrypted.length);
                return out;
            }

            @Override
            public byte[] decrypt(byte[] encrypted, byte[] key) {
                byte[] out = new byte[encrypted.length - 1];
                System.arraycopy(encrypted, 1, out, 0, encrypted.length - 1);
                return out;
            }
        },
        AES((byte) 10) {
            @Override
            public byte[] encrypt(byte[] unencrypted, byte[] keyBytes) {
                SecretKey key = CryptoUtils.bytesToKey(keyBytes);
                try {
                    return CryptoUtils.encryptAES(unencrypted, (byte) 10, key);
                } catch (java.security.NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidParameterSpecException | IllegalBlockSizeException | BadPaddingException ex) {
                    Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public byte[] decrypt(byte[] encrypted, byte[] keyBytes) {
                SecretKey key = CryptoUtils.bytesToKey(keyBytes);
                try {
                    return CryptoUtils.decryptAES(encrypted, key);
                } catch (java.security.NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidKeyException | InvalidParameterSpecException | IllegalBlockSizeException | BadPaddingException ex) {
                    Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex);
                }
            }
        };

        public final byte code;

        private Algorithm(byte code) {
            this.code = code;
        }

    }

    public static class Kit {
        private final Algorithm algorithm;
        private final byte[] key;

        public Kit(Algorithm algorithm, byte[] key) {
            this.algorithm = algorithm;
            this.key = key;
        }

        public Algorithm getAlgorithm() {
            return algorithm;
        }

        public byte[] getKeyBytes() {
            return key;
        }

        public byte[] encrypt(byte[] in) {
            return algorithm.encrypt(in, key);
        }

        public byte[] decrypt(byte[] in) {
            return algorithm.decrypt(in, key);
        }

        public byte[] encryptString(String in) {
            return encrypt(in.getBytes(StandardCharsets.UTF_8));
        }

        public String decryptToString(byte[] in) {
            return new String(decrypt(in), StandardCharsets.UTF_8);
        }

        public String encryptStringToString(String in) {
            String out = Base64.encodeBase64URLSafeString(encryptString(in));
            //System.out.println("<E>: \""+in+"\" => \""+out+"\", key="+Base64.encodeBase64URLSafeString(key));
            return out;
        }

        public String decryptStringFromString(String in) {
            String out = decryptToString(Base64.decodeBase64(in));
            //System.out.println("<D>: \""+in+"\" => \""+out+"\", key="+Base64.encodeBase64URLSafeString(key));
            return out;
        }
    }

    public static class NoSuchAlgorithmException extends RuntimeException {
        public NoSuchAlgorithmException() {
        }
    }
}

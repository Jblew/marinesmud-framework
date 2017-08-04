/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.crypto.encryptors;

import com.google.common.io.ByteStreams;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import pl.jblew.marinesmud.framework.util.ByteBufferInputStream;

/**
 *
 * @author teofil
 */
public final class NestedMessage {
    /*private final ByteBuffer data;
    private final Operation operation;
    private final Destination destination;

    public NestedMessage(ByteBuffer data) {
        this.data = data;
        operation = Operation.valueOfByte(data.get());
        destination = Destination.valueOfByte(data.get());
    }

    public byte[] decode(Destination targetDestination) {
        if(operation == Operation.AT_DESTINATION) {
            if(destination == targetDestination) {
                byte [] out = new byte [data.remaining()];
                data.get(out);
                return out;
            }
            else if(destination == Destination.CLIENT && targetDestination == Destination.SERVER) {
                throw new RuntimeException("Data destination went too far. Trying to get server, but already got client");
            }
            else {
                return new NestedMessage(data).decode(targetDestination);
            }
        }
        else if(operation == Operation.ENCRYPTION_AES256) {
            
        }
        else if(operation == Operation.GZIP) {
            try (DataInputStream dis = new DataInputStream(new GZIPInputStream(new ByteBufferInputStream(data)))) {
                ByteStreams.toByteArray(dis);
                return new NestedMessage(data).decode(targetDestination);
            } catch (IOException ex) {
                Logger.getLogger(NestedMessage.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            } 
        }
        else throw new UnsupportedOperationException(operation+" operation is not supported");
    }

    public static enum Operation {
        AT_DESTINATION((byte) 5), ENCRYPTION_AES256((byte) 15), GZIP((byte) 20);

        final byte code;

        private Operation(byte code) {
            this.code = code;
        }

        static Operation valueOfByte(byte code) {
            for (Operation a : Operation.values()) {
                if (a.code == code) {
                    return a;
                }
            }
            throw new MalformedDataException();
        }
    }

    public static enum Destination {
        CLIENT((byte)5), SERVER((byte)10);

        final byte code;

        private Destination(byte code) {
            this.code = code;
        }
        
        static Destination valueOfByte(byte code) {
            for (Destination a : Destination.values()) {
                if (a.code == code) {
                    return a;
                }
            }
            throw new MalformedDataException();
        }
    }*/
}

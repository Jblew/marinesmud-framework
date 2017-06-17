/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author teofil
 */
public final class JSONObjectLoader<T> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Class<? extends T> clazz;
    private final File orFile;
    private boolean preventSaving = false;

    public JSONObjectLoader(Class<? extends T> clazz, File orFile) {
        this.clazz = clazz;
        this.orFile = orFile;
    }
    
    public T loadObject(T defaultT) {
        

        if (orFile.exists() && orFile.canRead()) {
            T t = defaultT;

            try {
                t = MAPPER.readValue(orFile, clazz);
            } catch (IOException ex) {
                Logger.getLogger(JSONObjectLoader.class.getName()).log(Level.SEVERE, null, ex);
                preventSaving = true;

                backupBrokenConfig();
            }

            return t;
        } else {
            T t = defaultT;

            try {
                MAPPER.writerWithDefaultPrettyPrinter().writeValue(orFile, defaultT);
            } catch (IOException ex) {
                Logger.getLogger(JSONObjectLoader.class.getName()).log(Level.SEVERE, null, ex);
            }

            return t;
        }
    }

    public void save(T t) throws IOException {
        if (preventSaving) {
            System.out.println("Cannot save orFile("+orFile+")! (Previous broken orFile backup could not be saved!)");
        } else {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(orFile, t);
        }
    }

    private void backupBrokenConfig() {
        int bakNum = 0;
        File nextOrFileBak = new File(orFile.getAbsolutePath() + "_" + bakNum + ".bak");
        while (nextOrFileBak.exists()) {
            bakNum++;
            nextOrFileBak = new File(orFile.getAbsolutePath() + "_" + bakNum + ".bak");
        }

        try {
            Files.copy(orFile, nextOrFileBak);
            preventSaving = false;

        } catch (IOException ex) {
            Logger.getLogger(JSONObjectLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

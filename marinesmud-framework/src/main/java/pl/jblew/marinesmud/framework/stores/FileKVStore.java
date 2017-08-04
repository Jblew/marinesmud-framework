/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.stores;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.jblew.marinesmud.framework.json.JSONObjectLoader;

/**
 *
 * @author teofil
 */
public class FileKVStore {
    private final Object sync = new Object();
    private final File file;
    private final Store store;
    private final JSONObjectLoader<Store> jsonLoader;
    
    public FileKVStore(File f) {
        file = f;
        
        jsonLoader = new JSONObjectLoader<>(Store.class, file);
        
        synchronized(sync) {
            store = jsonLoader.loadObject(new Store());
        }
    }
    
    public String get(String k) {
        synchronized(sync) {
            return store.kvStore.get(k);
        }
    }
    
    public void set(String k, String v) {
        synchronized(sync) {
            store.kvStore.put(k, v);
            try {
                jsonLoader.save(store);
            } catch (IOException ex) {
                Logger.getLogger(FileKVStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static final class Store {
        private final Map<String, String> kvStore = new HashMap<>();
    }
}

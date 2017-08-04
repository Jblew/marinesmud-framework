/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author teofil
 */
public class ListenersManager<L extends Listener> {
    private final Set<L> listeners = new HashSet<>();
    private final Object lock = new Object();
    
    public void addListener(L listener) {
        synchronized(lock) {
            listeners.add(listener);
        }
    }
    
    public void removeListener(L listener) {
        synchronized(lock) {
            listeners.remove(listener);
        }
    }
    
    public void removeAllListeners() {
        synchronized(lock) {
            listeners.clear();
        }
    }
    
    public void fireEvent(Object attachment) {
        Object [] listenersArray = null;
        synchronized(lock) {
            listenersArray = listeners.toArray();
        }
        for(Object o : listenersArray) {
            L l = (L) o;
            l.actionPerformed(attachment);
        }
    }
}

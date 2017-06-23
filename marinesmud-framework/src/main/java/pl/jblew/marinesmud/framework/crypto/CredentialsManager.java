/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.crypto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author teofil
 * @param <U> – user object
 */
public class CredentialsManager<U> {
    private final long minimalLoginIntervalMs = 1500;//ms
    private final Map<String, CredentialEntry> credentials = new HashMap<>();
    private final Object sync = new Object();

    public CredentialsManager(List<UserEntry<U>> users) {
        for (UserEntry<U> ue : users) {
            synchronized (sync) {
                credentials.put(ue.username, new CredentialEntry(ue.username, ue.hash, ue.userObject));
            }
        }
    }

    public U login(String username, String password) {
        synchronized (sync) {
            if (credentials.containsKey(username)) {
                try {
                    CredentialEntry ce = credentials.get(username);
                    if (ce.verify(password)) {
                        return ce.userObject;
                    } else {
                        return null;
                    }
                } catch (PasswordStorage.CannotPerformOperationException ex) {
                    Logger.getLogger(CredentialsManager.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                } catch (PasswordStorage.InvalidHashException ex) {
                    Logger.getLogger(CredentialsManager.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public String generateHash(String password) {
        try {
            return PasswordStorage.createHash(password);
        } catch (PasswordStorage.CannotPerformOperationException ex) {
            throw new CryptographyException(password);
        }
    }

    public boolean verifyHash(String password, String hash) {
        try {
            return PasswordStorage.verifyPassword(password, hash);
        } catch (PasswordStorage.CannotPerformOperationException | PasswordStorage.InvalidHashException ex) {
            throw new CryptographyException(password);
        }
    }

    private final class CredentialEntry {
        public final String username;
        public final U userObject;
        private final String hash;
        private long lastTryTimestamp = System.currentTimeMillis();

        public CredentialEntry(String username, String hash, U userObject) {
            this.username = username;
            this.hash = hash;
            this.userObject = userObject;
        }

        public boolean verify(String password) throws PasswordStorage.CannotPerformOperationException, PasswordStorage.InvalidHashException {
            waitInterval();
            return PasswordStorage.verifyPassword(password, hash);
        }

        private void waitInterval() {
            long timeToWaitMs = minimalLoginIntervalMs - (System.currentTimeMillis() - lastTryTimestamp);
            if (timeToWaitMs > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(timeToWaitMs);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CredentialsManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            lastTryTimestamp = System.currentTimeMillis();
        }
    }

    public static class UserEntry<UU> {
        public String username;
        public String hash;
        public UU userObject;
    }
}

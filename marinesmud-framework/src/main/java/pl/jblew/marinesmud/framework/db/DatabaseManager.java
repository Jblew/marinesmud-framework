/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.db;

import com.google.common.eventbus.EventBus;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author teofil
 */
public class DatabaseManager {
    private final Object sync = new Object();
    private final File dbFile;
    private final AtomicReference<ConnectionSource> connectionSourceRef = new AtomicReference<>(null);

    public DatabaseManager(File dbFile) {
        this.dbFile = dbFile;

        loadDriver();
    }

    public void backupAndConnect() {
        backupDb(DateTimeFormatter.ofPattern("YYYY.MM.dd-HH-ii-ss").format(ZonedDateTime.now(ZoneOffset.UTC)));
        connect();
    }

    public void connect() {
        synchronized (sync) {
            try {
                ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:h2:" + dbFile.getAbsolutePath());
                connectionSourceRef.set(connectionSource);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void backupDb(String appendToFileName) {
        synchronized (sync) {
            boolean wasConnected = isConnected();
            if (wasConnected) {
                close();
            }
            try {
                File dbSourceFile = dbFile;
                File dbBackupFile = new File(dbFile.getAbsolutePath() + "." + appendToFileName + ".backup.temp");
                File dbBackupTempFile = new File(dbFile.getAbsolutePath() + "." + appendToFileName + ".backup");

                Files.copy(dbSourceFile.toPath(), dbBackupTempFile.toPath());
                if (dbBackupFile.exists()) {
                    dbBackupFile.delete();
                }
                Files.move(dbBackupTempFile.toPath(), dbBackupFile.toPath());
                Logger.getLogger(DatabaseManager.class.getName()).log(Level.INFO, "<DB BACKUP> Created DB backup in {0}", dbBackupFile);
            } catch (Exception ex) {
                Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (wasConnected) {
                connect();
            }
        }
    }

    /*
    private ConnectionSource getConnectionSource() throws SQLException {
        return connectionSourceRef.get();
    }*/

    public void close() {
        synchronized (sync) {
            if (connectionSourceRef.get() != null) {
                try {
                    connectionSourceRef.get().close();
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, "Exception while closing DB ConnectionSource", ex);
                }
                connectionSourceRef.set(null);
            }
        }
    }

    public void shutdown() {
        close();
    }

    public boolean isConnected() {
        return (connectionSourceRef.get() != null);
    }

    public void executeInSync(final Runnable r) {
        synchronized (sync) {
            r.run();
        }
    }

    private void loadDriver() {
        try {
            Class.forName("org.h2.Driver").newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public <A> Dao<A, Integer> initDao(Class<A> clazz) {
        synchronized (sync) {
            if(!isConnected()) throw new RuntimeException("Database not connected!");
            ConnectionSource connectionSource = connectionSourceRef.get();
            try {
                Dao<A, Integer> dao = DaoManager.createDao(connectionSource, clazz);
                if (!dao.isTableExists()) {
                    Logger.getLogger(Dao.class.getName()).log(Level.INFO, "Creating table for {0}", clazz.getName());
                    TableUtils.createTable(connectionSource, clazz);
                }
                dao.setAutoCommit(connectionSource.getReadWriteConnection(), true);
                return dao;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static class DBNotConnectedException extends RuntimeException {
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.util;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class SingleLineFormatter extends Formatter {

    private static SimpleDateFormat FRMT_DATE = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy.MM.dd");
    private final boolean showThread;
    private String day = "";

    public SingleLineFormatter(boolean showThread) {
        this.showThread = showThread;
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        String dayFormat = DAY_FORMAT.format(record.getMillis());
        if (!dayFormat.equals(day)) {
            sb.append("\n\n").append("<<DATE CHANGED>>: ").append(dayFormat).append("\n");
            day = dayFormat;
        }

        // format time
        sb.append(FRMT_DATE.format(record.getMillis())).append(" ");

        // thread
        if (showThread) {
            sb.append("[").append(Thread.currentThread().getName()).append("] ");
        }

        // level
        sb.append(record.getLevel().toString().toLowerCase());
        sb.append("> ");

        // package/class name, logging name
        String name = record.getLoggerName();

        if (record.getLevel().intValue() > Level.INFO.intValue()) {
            sb.append(name);
            sb.append("   ");
        }
        String msg = record.getMessage();
        Object parameters = record.getParameters();
        if (parameters != null && ((Object[]) parameters).length > 0) {
            int i = 0;
            for (Object o : (Object[]) parameters) {
                msg = msg.replace("{" + i + "}", o.toString());
                i++;
            }
        }
        sb.append(msg);

        // if there was an exception thrown, log it as well
        if (record.getThrown() != null) {
            sb.append("\n").append(printThrown(record.getThrown()));
        }

        sb.append("\n");

        return sb.toString();
    }

    private String printThrown(Throwable thrown) {
        StringBuffer sb = new StringBuffer();

        sb.append("").append(thrown.getClass().getName());
        sb.append(" - ").append(thrown.getMessage());
        sb.append("\n");

        for (StackTraceElement trace : thrown.getStackTrace()) {
            sb.append("\tat ").append(trace).append("\n");
        }

        Throwable cause = thrown.getCause();
        if (cause != null) {
            sb.append("\n").append(printThrown(cause));
        }

        return sb.toString();
    }
}

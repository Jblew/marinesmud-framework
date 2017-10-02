/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.remotecomponents;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import pl.jblew.marinesmud.framework.webserver.TemplateLoader;
import pl.jblew.marinesmud.framework.webserver.websockets.CommonEventBusWSPoster.PostToWebsocket;

/**
 *
 * @author teofil
 */
public class LogComponent extends RemoteComponent {
    private static final AtomicInteger IDINCREMENTER = new AtomicInteger(0);
        
    private final ArrayList<Entry> entries = new ArrayList<>();

    public LogComponent(RemoteComponents rc) {
        super("log", rc);
    }
    
    @Override
    public String getHtml() {
        String entriesStr = "";
        
        LocalDateTime afterMe = LocalDateTime.now().minusDays(1);
        
        for(Entry e : entries) {
            if(e.dateTime.isAfter(afterMe)) {
                entriesStr += "<div class=\"log-component-item\"><span class=\"log-component-time\">"+e.dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+"</span> "+e.text+"</div>";
            }
        }
        
        String out = new TemplateLoader(LogComponent.class).getOrLoadTemplate("log");
        out = out.replace("{{id}}", this.getId());
        out = out.replace("{{uid}}", this.obtainUID());
        out = out.replace("{{logEventClass}}", LogEvent.class.getName());
        out = out.replace("{{entries}}", entriesStr);
        return out;
    }
    
    public void post(String msg) {
        LocalDateTime dt = LocalDateTime.now();
        getEventBus().post(new LogEvent(this.getId(), dt, msg));
        
        entries.add(new Entry(dt, msg));
    }
    
    public static class Entry {
        public final LocalDateTime dateTime;
        public final String text;

        public Entry(LocalDateTime dateTime, String text) {
            this.dateTime = dateTime;
            this.text = text;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + Objects.hashCode(this.dateTime);
            hash = 97 * hash + Objects.hashCode(this.text);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Entry other = (Entry) obj;
            if (!Objects.equals(this.text, other.text)) {
                return false;
            }
            if (!Objects.equals(this.dateTime, other.dateTime)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return dateTime + ": " + text;
        }
        
        
    }
    
    public static class LogEvent implements PostToWebsocket {
        public String id;
        public String time;
        public String msg;

        public LogEvent(String id, LocalDateTime time, String msg) {
            this.id = id;
            this.time = time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.msg = msg;
        }
    }
}

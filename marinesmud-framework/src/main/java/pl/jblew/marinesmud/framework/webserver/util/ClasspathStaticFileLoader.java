/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.util;

import com.google.common.io.Resources;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import pl.jblew.marinesmud.framework.webserver.StaticFileLoader;

/**
 *
 * @author teofil
 */
public class ClasspathStaticFileLoader implements StaticFileLoader {
    private static final Map<String,String> MIMEMAP = new HashMap<>();
    static {
        MIMEMAP.put(".png", "image/png");
        MIMEMAP.put(".jpg", "image/jpeg");
        MIMEMAP.put(".jpeg", "image/jpeg");
        MIMEMAP.put(".gif", "image/gif");
        MIMEMAP.put(".js", "text/javascript");
        MIMEMAP.put(".css", "text/css");
        MIMEMAP.put(".htm", "text/html");
        MIMEMAP.put(".html", "text/html");
    }
    
    private final Class<?> relativeClass;

    public ClasspathStaticFileLoader(Class<?> relativeClass) {
        this.relativeClass = relativeClass;
    }
    
    @Override
    public byte[] loadFile(Path path) {
        String mime = getMime(path);
        if(mime == null) return "403 Forbidden".getBytes();
        
        try {
            return Resources.toByteArray(relativeClass.getResource(path.toString()));
        } catch (Exception ex) {
            Logger.getLogger(ClasspathStaticFileLoader.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public String getMime(Path path) {
        System.out.println("loading "+path+" in context of "+relativeClass);
        String fileName = path.getFileName().toString().toLowerCase();
        System.out.println("FileName="+fileName);
        for(String ext : MIMEMAP.keySet()) {
            if(fileName.endsWith(ext)) return MIMEMAP.get(ext);
        }
        return null;
    }
    
}

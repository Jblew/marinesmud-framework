/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver;

import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author teofil
 */
public final class TemplateLoader {
    private final Map<String, String> TEMPLATES = Collections.synchronizedMap(new HashMap<>());
    private final Class<?> relative;
    
    public TemplateLoader(Class<?> relative) {
        this.relative = relative;
    }
    
    public String getOrLoadTemplate(String name) {
        return this.getOrLoadTemplate(name, "html");
    }
    
    public String getOrLoadTemplate(String name, String extension) {
        String template = TEMPLATES.get(name);
        if (template != null) {
            return template;
        } else {
            try {
                URL templateUrl = relative.getResource(name + "."+extension);
                if (templateUrl == null) {
                    return "Could not find template "+name+" (relative="+relative+")";
                }
                template = Resources.toString(templateUrl, Charset.forName("UTF-8"));
                if(template != null && !template.isEmpty()) TEMPLATES.put(name, template);
                return template;
            } catch (IOException ex) {
                Logger.getLogger(TemplateLoader.class.getName()).log(Level.SEVERE, null, ex);
                return "Could not find template "+name+" (relative="+relative+")";
            }
        }
    }
}

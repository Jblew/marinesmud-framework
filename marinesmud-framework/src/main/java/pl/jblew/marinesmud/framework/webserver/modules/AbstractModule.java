/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.modules;

import pl.jblew.marinesmud.framework.webserver.TemplateLoader;
import pl.jblew.marinesmud.framework.webserver.modules.WebModule;

/**
 *
 * @author teofil
 */
public abstract class AbstractModule implements WebModule {
    public final TemplateLoader templateLoader;
    public final String defaultTemplate;
    
    public AbstractModule(Class<?> templateContext) {
        templateLoader = new TemplateLoader(templateContext);
         defaultTemplate = "main";
    }
    
    public AbstractModule(Class<?> templateContext, String defaultTemplate) {
        templateLoader = new TemplateLoader(templateContext);
        this.defaultTemplate = defaultTemplate;
    }
    
    public byte[] render(String title, String body) {
        return templateLoader.getOrLoadTemplate(defaultTemplate).replace("{{title}}", title).replace("{{body}}", body).getBytes();
    }
    
    public byte[] render(String templateName, String title, String body) {
        return templateLoader.getOrLoadTemplate(templateName).replace("{{title}}", title).replace("{{body}}", body).getBytes();
    }
}

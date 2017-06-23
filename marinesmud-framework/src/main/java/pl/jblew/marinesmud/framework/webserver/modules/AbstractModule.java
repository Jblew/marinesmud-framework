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
    
    public AbstractModule(Class<?> templateContext) {
        templateLoader = new TemplateLoader(templateContext);
    }
    
    public String render(String title, String body) {
        return templateLoader.getOrLoadTemplate("main").replace("{{title}}", title).replace("{{body}}", body);
    }
}

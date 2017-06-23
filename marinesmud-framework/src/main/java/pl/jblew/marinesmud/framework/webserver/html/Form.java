/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.html;

/**
 *
 * @author teofil
 */
public class Form extends HtmlElement {
    public Form(HtmlElement... children) {
        super(children);
    }
    
    @Override
    public String tagName() {
        return "form";
    }
    
}

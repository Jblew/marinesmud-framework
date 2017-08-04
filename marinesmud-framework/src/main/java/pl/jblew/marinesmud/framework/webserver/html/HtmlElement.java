/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.html;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author teofil
 */
public abstract class HtmlElement {
    private static final AtomicInteger idGenerator = new AtomicInteger();
    private final HtmlElement[] children;
    private final Map<String, String> attributes = new HashMap<>();
    public final String id = "elem-"+idGenerator.incrementAndGet();
    public boolean single = false;
    
    public abstract String tagName();
    
    HtmlElement(HtmlElement... children) {
        this.children = children;
    }
    HtmlElement() {
        this.children = new HtmlElement[]{};
    }
    
    @Override
    public String toString() {
        String tag = tagName();
        return "<"+tag+" "+" id=\""+id+"\" "+(single? "/" : "")+">"+(single? "" : Arrays.toString(children)+"</"+tag+">");
    }
    
}

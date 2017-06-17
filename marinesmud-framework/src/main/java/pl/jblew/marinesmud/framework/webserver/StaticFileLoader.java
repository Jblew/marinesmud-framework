/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver;

import java.nio.file.Path;

/**
 *
 * @author teofil
 */
public interface StaticFileLoader {
    public byte [] loadFile(Path path);
    public String getMime(Path path);
}

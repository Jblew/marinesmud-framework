/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.services;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author teofil
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class ServiceProvider<T extends Service> {
    public abstract T getService(ServiceManager sm);
    public abstract Class<? extends T> getServiceClass();
    public abstract boolean isLoadable(ServiceManager sm);
}

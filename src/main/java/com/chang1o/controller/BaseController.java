package com.chang1o.controller;
import com.chang1o.session.SessionManager;

public abstract class BaseController {
    
    //继承并子类访问外部类隐藏
    protected SessionManager sessionManager;

    public BaseController(){
        this.sessionManager = SessionManager.getInstance();
    }

    protected boolean checkLogin(){
        return sessionManager.checkLogin();
    }
}

package com.crt.whuseats.Net.Login;

/**
 * 登录权限错误类 (和CRT控制有关的)
 * Created at v1.0 by crt 2018-12-6
 */
public class LoginCRTPermissionException extends Exception
{
    public LoginCRTPermissionException()
    {
        super();
    }

    public LoginCRTPermissionException(String message)
    {
        super(message);
    }
}

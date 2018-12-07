package com.crt.whuseats.Net.Login;

/**
 * 登录错误类（与图书馆有关的）
 * Created at v1.0 by crt 2018-12-6
 */
public class LoginException extends Exception
{
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public LoginException(String message)
    {
        super(message);
    }

    public LoginException()
    {
        super();
    }
}

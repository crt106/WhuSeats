package com.crt.whuseats.Net;

/**
 * 基本网络异常
 * Created at v1.0 crt 1028-12-7
 */
public class NetException extends Exception
{
    public NetException(String message)
    {
        super(message);
    }

    public NetException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public NetException(Throwable cause)
    {
        super(cause);
    }
}

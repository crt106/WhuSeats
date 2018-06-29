package com.crt.whuseats.Interface;

//处理任务返回的接口
public interface onTaskResultReturn
{
     void OnTaskSucceed(Object... data);
     void OnTaskFailed(Object... data);
}

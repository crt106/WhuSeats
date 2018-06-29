package com.crt.whuseats.Task;

import android.util.Log;
import android.widget.Toast;

import com.crt.whuseats.Activity.BaseActivity;

import java.util.HashMap;

//管理所有的AsyncTask
public class TaskManager
{
    protected static HashMap<String,ResultsTask> taskHashMap=new HashMap<>();

    public static void AddTask(String Taskname,ResultsTask task)
    {
        taskHashMap.put(Taskname, task);
        Log.w("TaskManager", "成功添加Task:"+Taskname );
    }

    public static void RemoveTask(String Taskname)
    {
        try
        {
            taskHashMap.remove(Taskname);
            Log.w("TaskManager", "成功移除Task:"+Taskname );
        }
        catch (Exception e)
        {
            Log.e("TaskManager", "未成功移除Task:"+Taskname+"\n"+e.getMessage() );
        }
    }

    //取消AsyncTask
    public static void CancelTask(String Taskname)
    {
        try
        {
            ResultsTask task=taskHashMap.get(Taskname);
            task.cancel(true);
            taskHashMap.remove(Taskname);
            Log.e("TaskManager", "成功取消Task:"+Taskname+"\n");
            Toast.makeText(BaseActivity.ApplicationContext, "成功取消Task:"+Taskname, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.e("TaskManager", "在取消Task:"+Taskname+"时出错\n"+e.getMessage());
        }
    }
}

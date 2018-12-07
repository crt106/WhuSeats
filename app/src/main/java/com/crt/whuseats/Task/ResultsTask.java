package com.crt.whuseats.Task;

import android.os.AsyncTask;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.ApplicationV;
import com.crt.whuseats.Interface.onTaskResultReturn;

//实现了回调接口的ASyncTask
public abstract class ResultsTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
    protected onTaskResultReturn ResultReturn;
    protected String taskname;
    public ResultsTask(onTaskResultReturn i,String name)
    {
        super();
        ResultReturn=i;
        taskname=name;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        ApplicationV.taskManager.AddTask(taskname, this);
    }

    @Override
    protected void onPostExecute(Result result)
    {
        super.onPostExecute(result);
        ApplicationV.taskManager.RemoveTask(taskname);
    }

}


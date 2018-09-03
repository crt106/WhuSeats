package com.crt.whuseats.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.baidu.mobstat.StatService;
import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.Activity.LoginActivity;
import com.crt.whuseats.JsonHelps.JsonHelp;
import com.crt.whuseats.JsonHelps.JsonInfo_BookReturn;
import com.crt.whuseats.JsonHelps.JsonInfo_Login;
import com.crt.whuseats.JsonHelps.JsonInfo_MobileFiltrate;
import com.crt.whuseats.JsonHelps.JsonInfo_RoomLayout;
import com.crt.whuseats.JsonHelps.JsonInfo_SeatTime_End;
import com.crt.whuseats.JsonHelps.JsonInfo_SeatTime_Start;
import com.crt.whuseats.JsonHelps.JsonInfo_WebFiltrate;
import com.crt.whuseats.JsonHelps.seat;
import com.crt.whuseats.R;
import com.crt.whuseats.Task.ResultsTask;
import com.crt.whuseats.Interface.onProgressReturn;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.Utils.TimeHelp;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//实现与图书馆服务器的交互
public class NetService extends Service
{

    public NetService()
    {

    }

    //region 类字段

    //OkHttp客户端
    OkHttpClient httpClient=new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .cookieJar(new CookieJar()
            //设置cookieJar
            {
                //保存Cookies
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
                {
                    //这里的Cookies问题要提一句
                    //正常情况下访问所需要的cookies里只需要一个JSESSIONID
                    //但是图书馆服务器在有些时候会给你设置一个"remerberMe=deleteMe"
                    //这应该是shiro相关的权限设置 不是很懂 所以这里只存能够让你进行操作的那个cookies
                    //不是很懂为什么浏览器接收到这个Header之后Cookies并没有什么变化
                    // 可能浏览器是添加而不是替换 这个以后再研究
                    if(!ISLOGINPCCOOKIESGET&&cookies.get(0).name().equals("JSESSIONID"))
                    {
                        cookiestore.put(url.host(), cookies);
                        ISLOGINPCCOOKIESGET=true;
                    }
                }

                //获取保存的Cookies
                @Override
                public List<Cookie> loadForRequest(HttpUrl url)
                {
                    List<Cookie> cookies = cookiestore.get(url.host());
                    //根据查询到的cookies是否为空来返回
                    return cookies!=null? cookies:new ArrayList<Cookie>();
                }
            })
            .build();

    //TCP客户端(用于检查更新等)
    //region 套接字和IP地址
    public Socket ClientSocket=new Socket();
    //套接字输出流和输入流
    public OutputStream  Clientop;
    public InputStream   Clientin;
    String ServerIP1="120.79.7.230";//阿里云服务器ip
    String ServerIP2="10.133.1.126";//寝室crt的ip
    String ServerIP3="10.135.97.4";//图书馆crt的ip
    String ServerIP4="192.168.43.94";//热点的crt的ip
    String ServerIP5="192.168.137.1";//热点代理
    String LocalServerIP="127.0.0.1";
    //endregion
    //某些静态字段
    private static HashMap<String,List<Cookie>> cookiestore=new HashMap<>();   //储存网页登录产生的Cookies的东东
    public static String LIB_TOKEN="";  //返回的登录字段
    public static String LIB_COOKIE_JSESSIONID=""; //网页登陆的cookies内容

    public static String LIB_SEATSSTATUS=""; //指示全局的座位预约状况
    public static String LIB_MESSAGE; //指示一个返回的Message;
    public static JsonInfo_BookReturn LIB_BOOKRETURNINFO; //指示全局预约返回内容
    public static int REMOTEVERSION;  //指示远程版本号
    public static boolean ISFORCEUPDATE=false;//指示当前远程版本是不是强制更新

    //region 弃用的字段
    //开始查询一个新的座位之前的延迟
    @Deprecated
    public static int DELAY_NEWSEATS_DEFAULT=400;
    @Deprecated
    public static int DELAY_NEWSEATS=400;

    //请求开始时间之前的延迟
    @Deprecated
    public static int DELAY_BEFORESTARTTIME =850;
    @Deprecated
    public static int DELAY_BEFORESTARTTIME_DEFAULT =850;

    //请求结束时间之前的延迟
    @Deprecated
    public static int DELAY_BEFOREENDTIME =550;
    @Deprecated
    public static int DELAY_BEFOREENDTIME_DEFAULT =550;
    //endregion

    // 发送预约请求前后的延迟
    public static int DELAY_SENDBOOK =300;
    public static int DELAY_SENDBOOK_DEFAULT =300;

    //发起筛选请求之前的延迟
    public static int DELAY_BEFOREFILTRATE=1500;
    public static int DELAY_BEFOREFILTRATE_DEFAULT=1500;

    //判断PC登录是否已经获取到了Cookies
    public static boolean ISLOGINPCCOOKIESGET=false;

    //endregion

    //region 静态URL字段
    static final String LIB_URL="http://seat.lib.whu.edu.cn";
    static final String LIB_URLWITHPORT="https://seat.lib.whu.edu.cn:8443";
    static final String LIB_HOST="seat.lib.whu.edu.cn";
    static final String LIB_FREEBOOK="http://seat.lib.whu.edu.cn/rest/v2/freeBook";
    static final String CRT_HOST="http://120.79.7.230";
    static final String CRT_OSS="https://bucket-crt106.oss-cn-shenzhen.aliyuncs.com";
    static final String URL_LOGIN="/rest/auth";
    @Deprecated
    static final String URL_LOGIN_PC="/login";

    static final String URL_CAPTCHA="/simpleCaptcha/captcha";
    static final String URL_USER="/rest/v2/user";
    static final String URL_CHECKNOWRE="/rest/v2/user/reservations";
    static final String URL_FREEFILTERS="/rest/v2/free/filters";
    static final String URL_CHECKIN="/rest/v2/checkIn";
    static final String URL_LEAVE="/rest/v2/leave";
    static final String URL_STOP="/rest/v2/stop";
    //TODO 处理请求多页历史
    static final String URL_HISTORY="/rest/v2/history/1/10";
    static final String URL_CANCEL="/rest/v2/cancel";
    static final String URL_HISTORYONEVIEW="/rest/view/";
    static final String URL_CHECKHOUSESTATS="/rest/v2/room/stats2";
    static final String URL_CHECKROOMSTATS="/rest/v2/room/layoutByDate";
    static final String URL_GETSEATSTARTTIME="/rest/v2/startTimesForSeat";
    static final String URL_GETSEATENDTIME="/rest/v2/endTimesForSeat";
    static final String URL_AJAXSEARCH="/freeBook/ajaxSearch";
    static final String URL_MOBILESEARCH="/rest/v2/searchSeats";
    static final String URL_ANNOUNCE="/rest/v2/announce";
    static final String URL_UPDATE="/whuseatsapi/download.html";
    static final String URL_TOMORROWINFO_GET="/whuseatsapi/api/getinfo";
    static final String URL_TOMORROWINFO_ADD="/whuseatsapi/api/add";
    static final String URL_TOMORROWINFO_DELETE="/whuseatsapi/api/delete";
    static final String URL_PREHEAT="/whuseatsapi/api/preheat";
    static final String URL_ZHICODE="/whuseatsapi/api/alipay/zhicode";
    static final String URL_PUTZHICODE="/whuseatsapi/api/alipay/putzhicode";

    //endregion

    //与Activity绑定的Binder;
    public class NetBinder extends Binder
    {
        //以下的方法看是都是返回void 不过实际上都是有返回值的 是在实现的接口中处理的咯


        //登陆
        public void login(String user,String pwd,onTaskResultReturn r)
        {
            NetService.this.login(user, pwd,r);
        }
        //pc登录获取验证码
        @Deprecated
        public void Getcaptcha(onTaskResultReturn r)
        {
            NetService.this.Getcaptcha(r);
        }
        //pc登录
        @Deprecated
        public void LoginPc(String user,String pwd,String captcha,onTaskResultReturn r)
        {
            NetService.this.LoginPc(user,pwd ,captcha ,r );
        }
        //pc筛选座位
        @Deprecated
        public void SeatsFiltrate(String onDate,int buildingID,int roomID,String ST,String ET,onTaskResultReturn r)
        {
            NetService.this.SeatsFiltrate(onDate,buildingID,roomID,ST,ET,r);
        }
        //获取用户信息
        public void GetUserInfo(onTaskResultReturn r)
        {
            NetService.this.GetUserInfo(r);
        }
        //移动端筛选座位
        public void SeatsFiltrate_Mobile(String onDate,int buildingID,int roomID,String ST,String ET,onTaskResultReturn r)
        {
            NetService.this.SeatsFiltrate_Mobile(onDate,buildingID,roomID,ST,ET,r);
        }
        //移动端筛选座位-同步
        public String SeatsFiltrate_Mobile_Sync(String onDate,int buildingID,int roomID,String ST,String ET)
        {
            return NetService.this.SeatsFiltrate_Mobile_Sync(onDate,buildingID,roomID,ST,ET);
        }
        //检查当前预约情况
        public void CheckReservations(onTaskResultReturn r)
        {
            NetService.this.CheckReservations(r);
        }
        //签到
        public void CheckIn(onTaskResultReturn r)
        {
            NetService.this.CheckIn(r);
        }
        //暂离
        public void Leave(onTaskResultReturn r)
        {
            NetService.this.Leave(r);
        }
        //结束使用
        public void Stop(onTaskResultReturn r)
        {
            NetService.this.Stop(r);
        }
        //取消预约
        public void Cancel(String ReID,onTaskResultReturn r)
        {
            NetService.this.Cancel(ReID,r);
        }
        //取得预约历史
        public void GetHistory(onTaskResultReturn r)
        {
            NetService.this.GetHistory(r);
        }
        //取得单个的历史记录视图
        public void GetHistoryOneView(String ReID,onTaskResultReturn r)
        {
            NetService.this.GetHistoryOneView(ReID,r);
        }

        //检查图书馆总房间布局
        public void GetFilters(onTaskResultReturn r)
        {
            NetService.this.GetFilters(r);
        }
        //检查某个馆如信息学部分馆布局
        public void CheckHouseStats(onTaskResultReturn r,int id)
        {
            NetService.this.CheckHouseStats(r,id);
        }
        //检查某个具体房间的情况
        public void CheckRoomStats(onTaskResultReturn r,int roomID,String date)
        {
            NetService.this.CheckRoomStats(r,roomID,date);
        }
        //开始监听一次房间(遍历)
        @Deprecated
        public void StartListenRoomOnce(JsonInfo_RoomLayout roomInfo, String startTime, String endTime, String Date, @NonNull onTaskResultReturn r, @Nullable onProgressReturn p)
        {
            NetService.this.StartListenRoomOnce(roomInfo,startTime,endTime,Date,r,p);
        }
        //开始监听一次房间(筛选)
        @Deprecated
        public void StartListenRoomOnce(JsonInfo_WebFiltrate roomInfo, String startTime, String endTime, String Date, @NonNull onTaskResultReturn r, @Nullable onProgressReturn p)
        {
            NetService.this.StartListenRoomOnce(roomInfo,startTime,endTime,Date,r,p);
        }

        //获取座位开始时间
        public void GetSeatStartTime(int SeatID,String Date,onTaskResultReturn r)
        {
            NetService.this.GetSeatStartTime(SeatID ,Date,r);
        }
        //开始监听一次房间(移动端筛选)
        public void StartListenRoomOnce(JsonInfo_MobileFiltrate FiltrateInfo, String startTime, String endTime, String Date, onTaskResultReturn r, onProgressReturn p)
        {
            NetService.this.StartListenRoomOnce(FiltrateInfo,startTime,endTime,Date,r,p);
        }
        //同步监听一次房间(移动端筛选)
        public boolean StartListenRoomOnce_Sync(JsonInfo_MobileFiltrate FiltrateInfo, String startTime, String endTime, String Date)
        {
            return NetService.this.StartListenRoomOnce_Sync(FiltrateInfo,startTime,endTime,Date);
        }

        //发起同步预约请求
        public void FreeBookSync(int SeatID,String startTime,String endTime,String Date)
        {
            NetService.this.FreeBookSync(SeatID,startTime,endTime,Date);
        }

        //检查登陆权限
        public boolean CheckPermissionTxT(String user)
        {
            return NetService.this.CheckPermissionTxT(user);
        }

        //检查更新1 是否弹出对话框
        public boolean CheckUpdate_IsShowDialog(boolean isneedwinform)
        {
           return NetService.this.CheckUpdate_IsShowDialog(isneedwinform);
        }

        //下载更新
        public void DownLoadUpdate(onTaskResultReturn r,onProgressReturn p)
        {
            NetService.this.DownLoadUpdate(r,p);
        }

        //利用okhttp3下载更新
        public void DownLoadUpdateByHttp(onTaskResultReturn r,onProgressReturn p)
        {
            NetService.this.DownLoadUpdateByHttp(r,p );
        }

        //预热ASP.NET
        public void ASPNETpreheat()
        {
            NetService.this.ASPNETpreheat();
        }
        //获取第二天座位信息
        public void GetTomorrowInfo(onTaskResultReturn r)
        {
            NetService.this.GetTomorrowInfo(r);
        }

        //添加第二天预约信息
        public void AddTomorrowInfo(int seatID,String usernumber,onTaskResultReturn r)
        {
            NetService.this.AddTomorrowInfo(seatID,usernumber,r);
        }

        //删除第二天预约信息
        public void DeleteTomorrowInfo(int seatID,String usernumber,onTaskResultReturn r)
        {
            NetService.this.DeleteTomorrowInfo(seatID,usernumber,r);
        }

        //获取吱口令
        public String GetZhicode()
        {
            return NetService.this.GetZhicode();
        }
        //推送吱口令
        public String PutZhicode(String value)
        {
            return NetService.this.PutZhicode(value);
        }
        //清除NetService中存在的Cookies
        public void CleanCookies()
        {
            cookiestore.clear();
            ISLOGINPCCOOKIESGET=false;
        }

        //获取更新公告
        public void GetAnnounce(onTaskResultReturn r)
        {
            NetService.this.GetAnnounce(r);
        }


    }

    //region 服务绑定与解绑
    @Override
    public IBinder onBind(Intent intent)
    {
        Log.e("NetService", intent.getComponent().getClassName()+"请求绑定服务成功");
        return new NetBinder();
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        Log.e("NetService", intent.getComponent().getClassName()+"取消绑定服务");
        return super.onUnbind(intent);
    }

    //服务被创建的时候
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.e("NetService","服务创建成功");
        //实例化广播接收器
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e("NetService","服务由OnstartCommand启动");
        //如果这次接受到的Intent里面要求启动定时预约
        try
        {
            if(intent.getBooleanExtra("IsDoBook", false))
            {
                final Bitmap largeIcon = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
                //这里服务启动的时候可能是没有activity的 所以修改文件内容的时候要绕一圈
                this.getSharedPreferences("bookdata", Context.MODE_PRIVATE).edit().putBoolean("IsClockon", false).apply();
                Log.e("NetService","接收到Dobook要求");
                //region 创建一个准备抢座位的通知
                NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder=new NotificationCompat.Builder(NetService.this);
                builder.setContentTitle("当当当当~");
                builder.setContentText("看到这条消息说明APP服务还活着 正在执行座位预约~");
                builder.setSmallIcon(R.drawable.ic_launcher);
                builder.setLargeIcon(largeIcon);
                builder.setDefaults(NotificationCompat.DEFAULT_ALL);
                builder.setWhen(System.currentTimeMillis());
                Notification n=builder.build();
                manager.notify(1, n);
                //endregion
                DoBook();
            }
            return super.onStartCommand(intent, flags, startId);
        }
        catch (Exception e)
        {
            Log.e("NetonStart",e.toString() );
            return super.onStartCommand(intent, flags, startId);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    //endregion

    //region 相关实现方法

    /**
     * 移动端登陆 比如
     * http://seat.lib.whu.edu.cn/rest/auth?username={0}&password={1}
     */
    public void login(String user,String pwd,onTaskResultReturn r)
    {
        String loginurl=LIB_URL+URL_LOGIN+"?username="+user+"&password="+pwd;
        Request loginRequest=new Request.Builder()
                .get()
                .url(loginurl)
                .addHeader("Host", LIB_HOST)
                .addHeader("Connection", "Keep-Alive")
                .build();
        class LoginTask extends ResultsTask<Void,Void,Boolean>
        {
            public LoginTask(onTaskResultReturn i)
            {
                super(i,"LoginTask");
            }

            @Override
            protected Boolean doInBackground(Void... voids)
            {
                try
                {
                    Response re=httpClient.newCall(loginRequest).execute();
                    String ResponseStr=re.body().string();
                    JsonInfo_Login info= JsonHelp.GetLogin(ResponseStr);
                    //判断是否登陆成功咯~
                    if(info.status.equals("success"))
                    {
                        LIB_TOKEN=info.token;
                        Log.e("Net:Login","移动端返回Token:"+LIB_TOKEN);
                        return true;
                    }
                    else
                    {
                        Log.e("Net:Login", "移动端登陆失败");
                        return false;
                    }
                }
                catch (Exception e)
                {
                    Log.e("Net:Login", e.getMessage() );
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean)
            {
                super.onPostExecute(aBoolean);
                if(aBoolean==null||!aBoolean)
                    ResultReturn.OnTaskFailed();
                else
                    ResultReturn.OnTaskSucceed(LIB_TOKEN);

            }
        }

        //这是内部类之外的代码
        LoginTask tempTask=new LoginTask(r);
        tempTask.execute();

    }

    /**
     * 用于移动端自动登陆和重新登陆的方法 (***同步***)
     * @return 判断是否登陆成功
     */
    public boolean login_auto()
    {
        //读取已经保存的用户名和密码 注意这里在Activity已经死掉的情况下是无法调用BaseActivity的
        SharedPreferences temp=this.getSharedPreferences("pwddata", Context.MODE_PRIVATE);
        String user=temp.getString("Username","" );
        String pwd=temp.getString("Password","" );
        if(user.equals("")||pwd.equals(""))
        {
            Log.e("login_auto", "获取已经保存的用户名密码错误" );
            return false;
        }
        try
        {
            String loginurl=LIB_URL+URL_LOGIN+"?username="+user+"&password="+pwd;
            Request loginRequest=new Request.Builder()
                    .get()
                    .url(loginurl)
                    .addHeader("Host", LIB_HOST)
                    .addHeader("Connection", "Keep-Alive")
                    .build();
            Response re=httpClient.newCall(loginRequest).execute();
            String ResponseStr=re.body().string();
            JsonInfo_Login info= JsonHelp.GetLogin(ResponseStr);
            LIB_TOKEN=info.token;
            return true;
        }
        catch (Exception e)
        {
            Log.e("login_auto", e.toString() );
            return false;
        }
    }

    /**
     * Pc登录获取验证码图片
     * @param r
     */
    public void Getcaptcha(onTaskResultReturn r)
    {
        String loginurl=LIB_URL+URL_CAPTCHA;     //构建验证码登录链接
        //JSESSIONID=7CCAAB16D89854A78D3E206B08A7DD80
        Request loginrequest=new Request.Builder()
                .get()
                .url(loginurl)
                .build();

        class GetcaptchaTask extends ResultsTask<Void,Void,Object>
        {

            public GetcaptchaTask(onTaskResultReturn i)
            {
                super(i,"GetcaptchaTask");
            }

            @Override
            protected Object doInBackground(Void... voids)
            {
                Bitmap b= null;
                try
                {
                    Response re=httpClient.newCall(loginrequest).execute();
                    InputStream inputStream=re.body().byteStream();
                    b = BitmapFactory.decodeStream(inputStream);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return b;
            }

            @Override
            protected void onPostExecute(Object o)
            {
                super.onPostExecute(o);
                ResultReturn.OnTaskSucceed(o);
            }
        }
        GetcaptchaTask tempTask=new GetcaptchaTask(r);
        tempTask.execute();

    }

    /**
     *pc端登录的方法 pc登录之后 可以利用网页端的筛选功能加快查找速度
     */
    public void LoginPc(String user,String pwd,String captcha,onTaskResultReturn r)
    {
        String requestUrl = LIB_URL + "/auth/signIn";
        RequestBody postBody = new FormBody.Builder()
                .add("username", user)
                .add("password", pwd)
                .add("captcha", captcha)
                .build();
        Request loginpcRequest = new Request.Builder()
                .post(postBody)
                .url(requestUrl)
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .addHeader("Cache-Control", "max-age=0")
                .addHeader("Referer", "http://seat.lib.whu.edu.cn/login?username=" + user)
                .build();

        //创建AsyncTask
        class LoginPctask extends ResultsTask<Void, Void, Boolean>
        {
            public LoginPctask(onTaskResultReturn i)
            {
                super(i,"LoginPctask");
            }

            @Override
            public Boolean doInBackground(Void... voids)
            {
                try
                {
                    //执行操作
                    Response re = httpClient.newCall(loginpcRequest).execute();
                    String reStr = re.body().string();
                    if (reStr.length() > 10000)
                        return true;
                    else return false;

                } catch (IOException e)
                {
                    Log.e("Net:Loginpc", e.getMessage());
                    return false;
                }

            }

            @Override
            protected void onPostExecute(Boolean aBoolean)
            {
                if (aBoolean)
                    ResultReturn.OnTaskSucceed();
                else
                    ResultReturn.OnTaskFailed();
            }
        }


        LoginPctask temp = new LoginPctask(r);
        temp.execute();
    }

    /**
     * 请求用户信息
     */
    public void GetUserInfo(onTaskResultReturn r)
    {
        String url=LIB_URL+URL_USER;
        Request GetUserInforequest=new Request.Builder()
                .get()
                .addHeader("token", LIB_TOKEN)
                .url(url)
                .build();
        CommonTask tempTask=new CommonTask(r,"GetUserInfoTask",GetUserInforequest);
        tempTask.execute();
    }
    /**
     * 通过web端向服务器请求过滤后的座位信息
     * 按理说可能会返回超过一页的内容 不过既然合要求的座位都超过一页了 那还管后面干嘛 赶紧抢啊
     * 所以只请求第一页的内容
     */
    @Deprecated
    public void SeatsFiltrate(String onDate,int buildingID,int roomID,String ST,String ET,onTaskResultReturn r)//ST和ET是数字形式
    {
        String requesturl=LIB_URL+URL_AJAXSEARCH+"?"+"onDate="+onDate+"&building="+buildingID+"&room="+roomID+"&hour=null"+"&power=null&startMin="+ST+"&endMin="+ET+"&offset=0";
        Request FiltrateRequest=new Request.Builder()
                .get()
                .url(requesturl)
                .build();
        CommonTask tempTask=new CommonTask(r,"SeatsFiltrateTask",FiltrateRequest);
        tempTask.execute();
    }

    /**
     * 通过移动端向服务器请求过滤之后的座位信息
     * https://seat.lib.whu.edu.cn:8443/rest/v2/searchSeats/2018-06-08/1200/1320 HTTP/1.1
     */
    public void SeatsFiltrate_Mobile(String onDate,int buildingID,int roomID,String ST,String ET,onTaskResultReturn r)//ST和ET是数字形式
    {
        //组成一个Post请求
        String requestUrl=LIB_URL+URL_MOBILESEARCH+"/"+onDate+"/"+ST+"/"+ET;
        RequestBody FiltrateRequestBody=new FormBody.Builder()

                .add("t", "1")
                .add("roomId", Integer.toString(roomID))
                .add("buildingId", Integer.toString(buildingID))
                .add("batch","9999")
                .add("page", "1")
                .add("t2","2")
                .build();
        Request FiltrateRequest=new Request.Builder()
                .url(requestUrl)
                .addHeader("token", LIB_TOKEN)
                .post(FiltrateRequestBody)
                .build();
        CommonTask SeatsFiltrateTask=new CommonTask(r,"SeatsFiltrateTask",FiltrateRequest);
        SeatsFiltrateTask.execute();
    }

    /**
     * 通过移动端向服务器请求过滤之后的座位信息--------同步
     * https://seat.lib.whu.edu.cn:8443/rest/v2/searchSeats/2018-06-08/1200/1320 HTTP/1.1
     */
    public String SeatsFiltrate_Mobile_Sync(String onDate,int buildingID,int roomID,String ST,String ET)
    {
        //组成一个Post请求
        String requestUrl=LIB_URL+URL_MOBILESEARCH+"/"+onDate+"/"+ST+"/"+ET;
        RequestBody FiltrateRequestBody=new FormBody.Builder()

                .add("t", "1")
                .add("roomId", Integer.toString(roomID))
                .add("buildingId", Integer.toString(buildingID))
                .add("batch","9999")
                .add("page", "1")
                .add("t2","2")
                .build();
        Request FiltrateRequest=new Request.Builder()
                .url(requestUrl)
                .addHeader("token", LIB_TOKEN)
                .post(FiltrateRequestBody)
                .build();
        try
        {
            Response response=httpClient.newCall(FiltrateRequest).execute();
            return response.body().string();

        } catch (IOException e)
        {
            Log.e("SeatsFiltrate_Mobile",e.getMessage());
            return "";
        }
    }
    /**
     * 在token有效的情况下检查当前预约情况 比如
     * http://seat.lib.whu.edu.cn/rest/v2/user/reservations?token=8ZXSSXN3CC05154011
     */
    public void CheckReservations(onTaskResultReturn r)
    {
        String requestUrl=LIB_URL+URL_CHECKNOWRE+"?"+"token="+LIB_TOKEN;
        Request CheckreRequest=new Request.Builder()
                .get()
                .url(requestUrl)
                .addHeader("Host", LIB_HOST)
                .addHeader("Connection", "Keep-Alive")
                .build();

        //创建异步任务类  暂时返回String;

        CommonTask tempTask=new CommonTask(r,"CheckReservationsTask",CheckreRequest);
        tempTask.execute();

    }

    /**
     * 签到
     */
    public void CheckIn(onTaskResultReturn r)
    {
        String url=LIB_URL+URL_CHECKIN+"?token="+LIB_TOKEN;
        Request checkinrequest=new Request.Builder()
                .get()
                .url(url)
                .build();
        CommonTask tempTask=new CommonTask(r,"CheckInTask",checkinrequest);
        tempTask.execute();
    }

    /**
     * 暂离
     */
    public void Leave(onTaskResultReturn r)
    {
        String url=LIB_URL+URL_LEAVE+"?token="+LIB_TOKEN;
        Request Leaverequest=new Request.Builder()
                .get()
                .url(url)
                .build();
        CommonTask tempTask=new CommonTask(r,"LeaveTask",Leaverequest);
        tempTask.execute();
    }

    /**
     * 停止使用
     */
    public void Stop(onTaskResultReturn r)
    {
        String url=LIB_URL+URL_STOP+"?token="+LIB_TOKEN;
        Request Stoprequest=new Request.Builder()
                .get()
                .url(url)
                .build();
        CommonTask tempTask=new CommonTask(r,"StopTask",Stoprequest);
        tempTask.execute();
    }

   /**
    *取消预约
    */
    public void Cancel(String ReID,onTaskResultReturn r)
    {
        String url=LIB_URL+URL_CANCEL+"/"+ReID+"?token="+LIB_TOKEN;
        Request Cancelrequest=new Request.Builder()
                .get()
                .url(url)
                .build();
        CommonTask tempTask=new CommonTask(r,"CancelTask",Cancelrequest);
        tempTask.execute();
    }

    /**
     * 请求总的History列表信息
     */
    public void GetHistory(onTaskResultReturn r)
    {
        String url=LIB_URL+URL_HISTORY+"?token="+LIB_TOKEN;
        Request GetHistoryrequest=new Request.Builder()
                .get()
                .url(url)
                .build();
        CommonTask tempTask=new CommonTask(r,"GetHistroyTask",GetHistoryrequest);
        tempTask.execute();
    }

    /**
     * 获得单个的历史记录
     */
    public void GetHistoryOneView(String ReID,onTaskResultReturn r)
    {
        String url=LIB_URL+URL_HISTORYONEVIEW+ReID+"?token="+LIB_TOKEN;
        Request GetHistoryOnerequest=new Request.Builder()
                .get()
                .url(url)
                .build();
        CommonTask tempTask=new CommonTask(r,"GetHistroy_One_Task",GetHistoryOnerequest);
        tempTask.execute();
    }

    /**
     * 检查当前图书馆房间布局
     * http://seat.lib.whu.edu.cn/rest/v2/free/filters?token=8ZXSSXN3CC05154011
     */
    public void GetFilters(onTaskResultReturn r)
    {
        String requestUrl=LIB_URL+URL_FREEFILTERS+"?"+"token="+LIB_TOKEN;
        Request GetFiltersRequest=new Request.Builder()
                .get()
                .url(requestUrl)
                .addHeader("Host", LIB_HOST)
                .addHeader("Connection", "Keep-Alive")
                .build();

        CommonTask GetFiltersTask=new CommonTask(r,"GetFiltersTask",GetFiltersRequest);
        GetFiltersTask.execute();

    }

    /**
     * 检查图书馆某个特定的馆的布局
     * http://seat.lib.whu.edu.cn/rest/v2/room/stats2/1?token=8ZXSSXN3CC05154011
     */
    public void CheckHouseStats(onTaskResultReturn r,int HouseID)
    {
        String requestUrl=LIB_URL+URL_CHECKHOUSESTATS+"/"+HouseID+"?"+"token="+LIB_TOKEN;
        Request CheckHSRequest=new Request.Builder()
                .get()
                .url(requestUrl)
                .addHeader("Host", LIB_HOST)
                .addHeader("Connection", "Keep-Alive")
                .build();

        CommonTask CheckHouseStstsTask=new CommonTask(r,"CheckHouseStats",CheckHSRequest);
        CheckHouseStstsTask.execute();

    }


    /**
     * 检查具体房间情况
     * http://seat.lib.whu.edu.cn/rest/v2/room/layoutByDate/14/2018-05-15?token=8ZXSSXN3CC05154011
     */
    public void CheckRoomStats(onTaskResultReturn r,int roomID,String Date)
    {
        String requestUrl=LIB_URL+URL_CHECKROOMSTATS+"/"+roomID+"/"+Date+"?"+"token="+LIB_TOKEN;
        Request CheckSRSRequest=new Request.Builder()
                .get()
                .url(requestUrl)
                .addHeader("Host", LIB_HOST)
                .addHeader("Connection", "Keep-Alive")
                .build();

        CommonTask CheckHouseStstsTask=new CommonTask(r,"CheckRoomStatsTask",CheckSRSRequest);
        CheckHouseStstsTask.execute();
    }

    /***
     * 提交座位预约的请求(异步 注意频率)
     * POST http://seat.lib.whu.edu.cn/rest/v2/freeBook HTTP/1.1
     */
    public void FreeBook(int SeatID,String startTime,String endTime,String Date,onTaskResultReturn r)
    {
        //组成一个Post请求
        RequestBody bookRequestBody=new FormBody.Builder()
                .add("token", LIB_TOKEN)
                .add("startTime", startTime)
                .add("endTime", endTime)
                .add("seat",Integer.toString(SeatID) )
                .add("date", Date)
                .build();
        Request bookRequest=new Request.Builder()
                .url(LIB_FREEBOOK)
                .post(bookRequestBody)
                .build();
        CommonTask bookTask=new CommonTask(r,"FreeBookSyncTask",bookRequest);
        bookTask.execute();

    }
    //这是一个供其他方法调用的异步预定座位回调接口
    public onTaskResultReturn BookTaskReturn=new onTaskResultReturn()
    {
        @Override
        public void OnTaskSucceed(Object... data) //注意 任务成功不代表预约成功
        {
            JsonInfo_BookReturn bookReturn;
            try
            {
                String rece=(String)data[0];
                bookReturn = JsonHelp.GetBookReturn(rece);
                LIB_SEATSSTATUS=bookReturn.status; //更改全局指示字段
            }
            catch (Exception e)
            {
                Log.e("Net:BookReturn",e.getMessage() );
            }
        }

        @Override
        public void OnTaskFailed(Object... data)
        {

            //Toast.makeText(getApplicationContext(), "预约失败 请crt同学查看日志排查",Toast.LENGTH_SHORT);
        }
    };

    /**
     * 提交座位预约的请求(同步 注意在子线程中调用)
     */
    public void FreeBookSync(int SeatID,String startTime,String endTime,String Date)
    {
        if(!LIB_SEATSSTATUS.equals("success"))
        {
            RequestBody bookRequestBody = new FormBody.Builder()
                    .add("token", LIB_TOKEN)
                    .add("startTime", startTime)
                    .add("endTime", endTime)
                    .add("seat", Integer.toString(SeatID))
                    .add("date", Date)
                    .build();
            Request bookRequest = new Request.Builder()
                    .url(LIB_FREEBOOK)
                    .post(bookRequestBody)
                    .build();
            try
            {
                Response re = httpClient.newCall(bookRequest).execute();
                String reStr = re.body().string();
                JsonInfo_BookReturn bookReturn = JsonHelp.GetBookReturn(reStr);

                if (bookReturn.status.equals("success"))
                {
                    LIB_SEATSSTATUS = "success";
                    LIB_BOOKRETURNINFO = bookReturn;
                    LIB_MESSAGE=bookReturn.message;

                    //region 向百度统计发送成功数据
                    try
                    {
                        Map<String, String> attributes = new HashMap<>();
                        //读取当前学号 姓名
                        String username=getSharedPreferences("pwddata", Context.MODE_PRIVATE ).getString("Username", "");
                        String name=getSharedPreferences("pwddata", Context.MODE_PRIVATE ).getString("name", "");
                        attributes.put("学号", username);
                        attributes.put("姓名", name);
                        StatService.onEvent(this, "Event_BookSuccess", "BookSuccess", 1,attributes);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    //endregion
                }
                else
                {
                    LIB_SEATSSTATUS = bookReturn.status;
                    LIB_MESSAGE=bookReturn.message;
                }
            }
            catch (Exception e)
            {
                Log.e("Net:FreeBookSync", e.getMessage());
            }
        }
    }

    /**
     * 检查某个座位的开始时间(异步)
     * http://seat.lib.whu.edu.cn/rest/v2/startTimesForSeat/9520/2018-05-15?token=ECKITYFWD405155501
     */

    public void GetSeatStartTime(int SeatID,String Date,onTaskResultReturn r)
    {
        String requestUrl=LIB_URL+URL_GETSEATSTARTTIME+"/"+SeatID+"/"+Date+"?"+"token="+LIB_TOKEN;
        Request GetStartTimeRequest=new Request.Builder()
                .get()
                .url(requestUrl)
                .addHeader("Host", LIB_HOST)
                .addHeader("Connection", "Keep-Alive")
                .build();
        CommonTask startTimeTask=new CommonTask(r,"GetSeatStartTimeSyncTask",GetStartTimeRequest);
        startTimeTask.execute();
    }

    /**
     *检查某个座位的开始时间(同步 请在子线程执行)
     */

    public JsonInfo_SeatTime_Start GetSeatStartTimeSync(int SeatID, String Date)
    {
        String requestUrl=LIB_URL+URL_GETSEATSTARTTIME+"/"+SeatID+"/"+Date+"?"+"token="+LIB_TOKEN;
        Request GetStartTimeRequest=new Request.Builder()
                .get()
                .url(requestUrl)
                .addHeader("Host", LIB_HOST)
                .addHeader("Connection", "Keep-Alive")
                .build();
        try
        {
            Response re=httpClient.newCall(GetStartTimeRequest).execute();
            String reStr=re.body().string();
            return new JsonInfo_SeatTime_Start(reStr);

        }
        catch (Exception e)
        {
            Log.e("GetSeatsStart", e.getMessage());
            return null;
        }
    }


    /**
     * 检查某个座位的结束时间(异步)
     * http://seat.lib.whu.edu.cn/rest/v2/endTimesForSeat/9185/2018-05-15/1320?token=ECKITYFWD405155501
     */

    public void GetSeatEndTime(int SeatID,String startTime,String Date,onTaskResultReturn r)
    {
        String requestUrl=LIB_URL+URL_GETSEATENDTIME+"/"+SeatID+"/"+Date+"/"+startTime+"?"+"token="+LIB_TOKEN;
        Request GetEndTimeRequest=new Request.Builder()
                .get()
                .url(requestUrl)
                .addHeader("Host", LIB_HOST)
                .addHeader("Connection", "Keep-Alive")
                .build();
        CommonTask endTimeTask=new CommonTask(r,"GetSeatEndTimeSyncTask",GetEndTimeRequest);
        endTimeTask.execute();
    }

    /**
     * 检查某个座位结束时间(同步 请在子线程执行)
     */
    public JsonInfo_SeatTime_End GetSeatEndTimeSync(int SeatID, String startTime, String Date)
    {
        String requestUrl=LIB_URL+URL_GETSEATENDTIME+"/"+SeatID+"/"+Date+"/"+startTime+"?"+"token="+LIB_TOKEN;
        Request GetEndTimeRequest=new Request.Builder()
                .get()
                .url(requestUrl)
                .addHeader("Host", LIB_HOST)
                .addHeader("Connection", "Keep-Alive")
                .build();
        try
        {
            Response re=httpClient.newCall(GetEndTimeRequest).execute();
            String reStr=re.body().string();
            return new JsonInfo_SeatTime_End(reStr);

        }
        catch (Exception e)
        {
            Log.e("GetSeatsStart", e.getMessage());
            return null;
        }
    }


    int nowseatID=0; //给下个方法用的全局变量
    /** *********************************************************************核心方法
     *  开始监测一次某个房间的空位情况 有空就预约 没有就返回
     *  现在感觉这个方法问题比较多 频率太快导致的临时冻结账号的现象比较严重2333
     *  看样子要一定要设置间隔
     *  *********************************************************************
     */
    @Deprecated
    public void StartListenRoomOnce(JsonInfo_RoomLayout roomInfo, String startTime, String endTime, String Date, onTaskResultReturn r,onProgressReturn p)
    {

        //region 用于异步的回调 不过这里子线程内打算用同步了
        /*


        //请求结束时间回调
        onTaskResultReturn EndReturn=new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                String jsonstr=(String)data[0];
                JsonInfo_SeatTime_End temp=JsonHelp.GetSeatEndTime(jsonstr);
                int min=Integer.parseInt(temp.minTimeID);
                int max=Integer.parseInt(temp.maxTimeID);
                //如果满足条件 则开始发起预订请求
                if(endTime>min&&endTime<max)
                    FreeBook(nowseatID, startTime, endTime, Date, BookTaskReturn);
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        };

        //请求开始时间回调 因为请求开始时间成功之后要请求结束时间 所以这个回调放在后面
        onTaskResultReturn StartReturn=new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                String jsonstr=(String)data[0];
                JsonInfo_SeatTime_Start temp=JsonHelp.GetSeatStartTime(jsonstr);
                int min=Integer.parseInt(temp.minTimeID);
                int max=Integer.parseInt(temp.maxTimeID);
                //如果请求的开始时间合乎要求 则请求结束时间
                if(startTime>min&&startTime<max)
                    GetSeatEndTime(nowseatID, Integer.toString(startTime), Date, EndReturn);
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        };
        */
        //endregion

        class ListenRoomTask extends ResultsTask<Void,Integer,Void>
        {
            public ListenRoomTask(onTaskResultReturn i)
            {
                super(i,"ListenRoomTask");
            }
            int TotalSeatsNumber;
            int NowSeatNumber=0;

            @Override
            protected Void doInBackground(Void... voids)
            {
                TotalSeatsNumber=roomInfo.seatList.size();
                for (seat thisseat:roomInfo.seatList) //foreach
                {
                    NowSeatNumber++;//座位计数器自增
                    publishProgress();
                    //region 头等待
                    try
                    {
                        Log.e("NetService——Listen", "开启新座位的开头等待");
                        Thread.sleep(DELAY_NEWSEATS);
                        Log.e("NetService——Listen", "结束新座位的开头等待");
                    }
                    catch (InterruptedException e)
                    {
                        Log.e("NetService——Listen", "任务可能被中断?");
                        return null;
                    }
                    //endregion

                    //如果已经预定成功了直接跳出
                    if(LIB_SEATSSTATUS.equals("success"))
                        break;

                    /*
                     本地检查座位状态
                     这个座位状态应该有 FREE IN_USE FULL AWAY
                    */
                    if(!thisseat.seatstatus.equals("FULL"))
                    {
                        try
                        {
                            nowseatID=thisseat.seatid;

                            //region 请求开始时间等待
                            Log.e("NetService——Listen", "开启座位的请求结束时间等待");
                            Thread.sleep(DELAY_BEFORESTARTTIME);
                            Log.e("NetService——Listen", "结束等待");
                            //endregion
                            JsonInfo_SeatTime_Start startinfo=GetSeatStartTimeSync(nowseatID, Date);

                            //如果请求的开始时间合乎要求 则请求结束时间
                            if(JsonHelp.IsTimeAvaliable(startTime, startinfo.StartTimeList))
                            {

                                //region 请求结束时间等待
                                Log.e("NetService——Listen", "开启请求结束时间等待");
                                Thread.sleep(DELAY_BEFOREENDTIME);
                                Log.e("NetService——Listen", "结束等待");
                                //endregion
                                JsonInfo_SeatTime_End endinfo=GetSeatEndTimeSync(nowseatID, startTime, Date);

                                //如果请求的结束时间合乎要求 则开始预订
                                if(JsonHelp.IsTimeAvaliable(endTime, endinfo.EndTimeList))
                                {
                                    //region 预约等待
                                    Thread.sleep(DELAY_SENDBOOK);
                                    //endregion
                                    //Log.e("NetService——Listen", "找到一个合适的座位 调试中跳过预约");
                                    FreeBookSync(nowseatID, startTime, endTime, Date);
                                    //region 预约等待2
                                    Thread.sleep(DELAY_SENDBOOK);
                                    //endregion
                                }
                            }
                        }
                        catch (InterruptedException e)
                        {
                            Log.e("ListenRoomOnce", "任务可能被中断?"+e.getMessage());
                            return null;
                        }
                        catch (Exception e)
                        {
                            Log.e("ListenRoomOnce", e.getMessage());
                            continue;
                        }

                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values)
            {
                //通过回调方法报告进度
                p.OnSendProgress(TotalSeatsNumber,NowSeatNumber);
            }

            //结束任务
            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);

                if(LIB_SEATSSTATUS.equals("success"))
                    ResultReturn.OnTaskSucceed();
                else
                    ResultReturn.OnTaskFailed();
            }
        }


        new ListenRoomTask(r).execute();

    }


    /*******************************************************************核心方法
     *上一个方法的使用[筛选数据]的重载形式
     * *****************************************************************
     */
    @Deprecated
    public void StartListenRoomOnce(JsonInfo_WebFiltrate FiltrateInfo, String startTime, String endTime, String Date, onTaskResultReturn r, onProgressReturn p)
    {


        class ListenRoomTask extends ResultsTask<Void,Integer,Void>
        {
            public ListenRoomTask(onTaskResultReturn i)
            {
                super(i,"ListenRoomTask");
            }
            int TotalSeatsNumber;
            int NowSeatNumber=0;

            @Override
            protected Void doInBackground(Void... voids)
            {
                //region 头等待1
                try
                {
                    Log.e("NetService——Listen", "开启新筛选的等待");
                    Thread.sleep(DELAY_BEFOREFILTRATE);
                    Log.e("NetService——Listen", "结束新筛选的等待");
                }
                catch (InterruptedException e)
                {
                    Log.e("ListenRoomOnce_PC", "任务中断" );
                    return null;
                }
                //endregion
                TotalSeatsNumber=FiltrateInfo.SuitableSeatsList.size();
                //如果筛选结果为空直接返回
                if(TotalSeatsNumber<=0)
                    return null;

                for (String thisseat:FiltrateInfo.SuitableSeatsList) //foreach
                {
                    //把字符串形式转换为整数
                    int thisseatId=Integer.parseInt(thisseat);
                    NowSeatNumber++;//座位计数器自增
                    publishProgress();


                    //如果已经预定成功了直接跳出
                    if (LIB_SEATSSTATUS.equals("success"))
                        break;

                    try
                    {
                        FreeBookSync(thisseatId, startTime, endTime, Date);
                        //region 预约等待
                        Thread.sleep(DELAY_SENDBOOK*3);
                        //endregion
                    }
                    catch (InterruptedException e)
                    {
                        Log.e("ListenRoomOnce_PC", "任务中断" );
                        return null;
                    }
                    catch (Exception e)
                    {
                        Log.e("ListenRoomOnce_PC", e.getMessage());
                        continue;
                    }


                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values)
            {
                //通过回调方法报告进度
                p.OnSendProgress(TotalSeatsNumber,NowSeatNumber);
            }

            //结束任务
            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);

                if(LIB_SEATSSTATUS.equals("success"))
                    ResultReturn.OnTaskSucceed();
                else
                    ResultReturn.OnTaskFailed();
            }
        }


        new ListenRoomTask(r).execute();

    }

    /***
     * ******************************************************************最新操作
     * 原来移动端也是可以筛选座位的我CNM
     * ******************************************************************
     */
    public void StartListenRoomOnce(JsonInfo_MobileFiltrate FiltrateInfo, String startTime, String endTime, String Date, onTaskResultReturn r, onProgressReturn p)
    {
        class ListenRoomTask extends ResultsTask<Void,Integer,Void>
        {
            public ListenRoomTask(onTaskResultReturn i)
            {
                super(i,"ListenRoomTask");
            }
            int TotalSeatsNumber;
            int NowSeatNumber=0;

            @Override
            protected Void doInBackground(Void... voids)
            {

                //region 头等待1
                try
                {
                    Log.e("NetService——Listen", "开启新筛选的等待");
                    Thread.sleep(DELAY_BEFOREFILTRATE);
                    Log.e("NetService——Listen", "结束新筛选的等待");
                }
                catch (InterruptedException e)
                {
                    Log.e("ListenRoomOnce_PC", "任务中断" );
                    return null;
                }
                //endregion

                TotalSeatsNumber=FiltrateInfo.seatList.size();
                //如果筛选结果为空直接返回
                if(TotalSeatsNumber<=0)
                    return null;

                for (seat thisseat:FiltrateInfo.seatList) //foreach
                {
                    //把字符串形式转换为整数
                    int thisseatId=thisseat.seatid;

                    NowSeatNumber++;//座位计数器自增
                    publishProgress();


                    //如果已经预定成功了直接跳出
                    if (LIB_SEATSSTATUS.equals("success"))
                        break;

                    try
                    {
                        FreeBookSync(thisseatId, startTime, endTime, Date);
                        //region 预约等待
                        Thread.sleep(DELAY_SENDBOOK*3);
                        //endregion
                    }
                    catch (InterruptedException e)
                    {
                        Log.e("ListenRoomOnce_MF", "任务中断" );
                        return null;
                    }
                    catch (Exception e)
                    {
                        Log.e("ListenRoomOnce_MF", e.getMessage());
                        continue;
                    }


                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values)
            {
                //通过回调方法报告进度
                p.OnSendProgress(TotalSeatsNumber,NowSeatNumber);
            }

            //结束任务
            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);

                if(LIB_SEATSSTATUS.equals("success"))
                    ResultReturn.OnTaskSucceed();
                else
                    ResultReturn.OnTaskFailed();
            }
        }

        new ListenRoomTask(r).execute();
    }

    /**************************************************
     *  移动端监听 (同步操作)
     *  ************************************************
     *  @return 返回本次监听是否成功
     */
    public boolean StartListenRoomOnce_Sync(JsonInfo_MobileFiltrate FiltrateInfo, String startTime, String endTime, String Date)
    {

        //region 头等待1
        try
        {
            Log.e("NetService——Listen", "开启新筛选的等待");
            Thread.sleep(DELAY_BEFOREFILTRATE);
            Log.e("NetService——Listen", "结束新筛选的等待");
        }
        catch (InterruptedException e)
        {
            Log.e("ListenRoomOnce_PC", "任务中断" );
            return false;
        }
        //endregion

        int TotalSeatsNumber=FiltrateInfo.seatList.size();
        //如果筛选结果为空直接返回
        if(TotalSeatsNumber<=0)
            return false;

        for (seat thisseat:FiltrateInfo.seatList)
        {
            //把字符串形式转换为整数
            int thisseatId=thisseat.seatid;


            //如果已经预定成功了直接跳出
            if (LIB_SEATSSTATUS.equals("success"))
                return true;

            try
            {
                FreeBookSync(thisseatId, startTime, endTime, Date);

                if (LIB_SEATSSTATUS.equals("success"))
                    return true;

                //region 预约等待
                Thread.sleep(DELAY_SENDBOOK*3);
                //endregion
            }
            catch (InterruptedException e)
            {
                Log.e("ListenRoomOnce_MFSync", "任务中断" );
                return false;
            }
            catch (Exception e)
            {
                Log.e("ListenRoomOnce_MFSync", e.getMessage());
                continue;
            }
        }

        return false;
    }

    /*******************************************************************核心方法
     * 针对设定好的座位进行POST (同步)
     * *****************************************************************
     */
    boolean isloginsuccess; //给下个方法用的全局变量
    public void DoBook()
    {

        Thread reloginThread=new Thread(()->{
            isloginsuccess=login_auto();
        });
        while (true)
        {

            try
            {
                reloginThread.start();
                reloginThread.join();
                if (isloginsuccess)
                {
                    //读取设置好的座位信息 注意！这里的Activity可能已死！
                    int seatId=0;
                    String st="",et="";
                    if(BaseActivity.AppSetting!=null)
                    {
                        seatId = BaseActivity.AppSetting.BookSetting.getInt("ChooseSeatID", 0);
                        st = BaseActivity.AppSetting.ListenSetting.getString("startTimeValue", "");
                        et = BaseActivity.AppSetting.ListenSetting.getString("endTimeValue", "");

                        Log.e("DoBook_front", "seatId="+seatId );
                        Log.e("DoBook_front", "st="+st );
                        Log.e("DoBook_front", "et="+et );
                    }
                    else
                    {
                        SharedPreferences tempBookSetting=this.getSharedPreferences("bookdata", Context.MODE_PRIVATE);
                        SharedPreferences tempListenSetting=this.getSharedPreferences("listendata", Context.MODE_PRIVATE);

                        seatId=tempBookSetting.getInt("ChooseSeatID", 0);
                        Log.e("DoBook_back", "seatId="+seatId );
                        st = tempListenSetting.getString("startTimeValue", "");
                        Log.e("DoBook_back", "st="+st );
                        et = tempListenSetting.getString("endTimeValue", "");
                        Log.e("DoBook_back", "et="+et );
                    }

                    //构建Final中转变量*************** 注意！！！这里的字典可能没有实例化！******************
                    TimeHelp tempTimeHelp=new TimeHelp();
                    String st_final=(tempTimeHelp.Value2ID.get(st)).toString();
                    String et_final=(tempTimeHelp.Value2ID.get(et)).toString();
                    int seatId_final=seatId;
                    //开始构建线程
                    Thread bookThread=new Thread(()->{
                        //正式的预约
                        FreeBookSync(seatId_final, st_final, et_final, TimeHelp.GetTomorrowStr());
                        //测试用的预约
                        //FreeBookSync(seatId_final, st_final, et_final, TimeHelp.GetTodayStr());
                    });

                    bookThread.start();
                    bookThread.join();

                    //region 预先构建通知
                    NotificationManager manager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
                    builder.setSmallIcon(R.drawable.ic_launcher);
                    builder.setWhen(System.currentTimeMillis());
                    builder.setDefaults(NotificationCompat.DEFAULT_ALL);
                    builder.setPriority(NotificationCompat.PRIORITY_MAX);
                    //endregion

                    //一次预约执行完毕 判断结果
                    if(LIB_SEATSSTATUS.equals("success")) //执行成功
                    {
                        //构建自启动PendingIntent
                        Intent autoin=new Intent(NetService.this, LoginActivity.class);
                        autoin.putExtra("IsAutoLogin", true);
                        PendingIntent autoinPending=PendingIntent.getActivity(this,0,autoin,PendingIntent.FLAG_CANCEL_CURRENT);

                        //发送成功通知 点击之后可以进入程序
                        builder.setContentTitle("预约座位成功啦！");
                        builder.setContentText(LIB_BOOKRETURNINFO.location);
                        builder.setContentIntent(autoinPending); //点击之后进入界面
                        Notification n=builder.build();
                        manager.notify(2, n);
                        break;
                    }
                    else if(LIB_MESSAGE.contains("登录失败: 用户名或密码不正确"))
                    {
                        //这里是掉线了 尝试重新登陆
                        continue;
                    }
                    else if(LIB_MESSAGE.contains("预约失败"))
                    {
                        //发送失败通知
                        builder.setContentTitle("啊欧....");
                        builder.setContentText("好像没有抢到呢...,可能座位已经名花有主了...");
                        Notification n=builder.build();
                        manager.notify(2, n);
                        break;
                    }
                    else
                    {
                        //发送失败通知
                        builder.setContentTitle("啊欧....");
                        builder.setContentText("好像没有抢到呢...发生其他错误呢");
                        Log.e("AutoBook", LIB_MESSAGE );
                        Notification n=builder.build();
                        manager.notify(2, n);
                        break;
                    }

                }
            }
            catch (Exception e)
            {
                Log.e("DoBook",e.toString());
                break;
            }
        }

    }

    /**
     * 给服务器端的ASP.NET程序敲个闹钟
     */
    public void ASPNETpreheat()
    {
        String requestUrl=CRT_HOST+URL_TOMORROWINFO_GET+"?date="+TimeHelp.GetTomorrowStr();
        Request request=new Request.Builder()
                .get()
                .url(requestUrl)
                .build();

        CommonTask tmpTask=new CommonTask(new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                Log.e("Preheat","预热成功");
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        }, "PreheatTask", request);

        tmpTask.execute();
    }
    /**
     * 获取明天的预约情况(全部座位)
     */
    public void GetTomorrowInfo(onTaskResultReturn r)
    {
        String requestUrl=CRT_HOST+URL_TOMORROWINFO_GET+"?date="+TimeHelp.GetTomorrowStr();
        Request request=new Request.Builder()
                .get()
                .url(requestUrl)
                .build();
        CommonTask tmpTask=new CommonTask(r,"GetTomorrowInfoTask",request);
        tmpTask.execute();
    }

    /**
     * 添加第二日预约信息
     */
    public void AddTomorrowInfo(int seatID,String usernumber,onTaskResultReturn r)
    {
        String requestURl=CRT_HOST+URL_TOMORROWINFO_ADD;
        FormBody requestbody=new FormBody.Builder()
                .add("seatID", Integer.toString(seatID))
                .add("usernumber", usernumber)
                .add("date",TimeHelp.GetTomorrowStr() )
                .build();

        Request addrequest=new Request.Builder()
                .url(requestURl)
                .post(requestbody)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .build();

        CommonTask commonTask=new CommonTask(r,"Addtomorowinfotask",addrequest);
        commonTask.execute();
    }

    /**
     * 删除第二日预约信息
     */
    public void DeleteTomorrowInfo(int seatID,String usernumber,onTaskResultReturn r)
    {
        String requestURl=CRT_HOST+URL_TOMORROWINFO_DELETE;
        FormBody requestbody=new FormBody.Builder()
                .add("seatID", Integer.toString(seatID))
                .add("usernumber", usernumber)
                .add("date",TimeHelp.GetTomorrowStr())
                .build();

        Request addrequest=new Request.Builder()
                .url(requestURl)
                .post(requestbody)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .build();

        CommonTask commonTask=new CommonTask(r,"Addtomorowinfotask",addrequest);
        commonTask.execute();
    }

    /**
     * 检查登陆权限(同步,请在子线程中处理)
     */
    public boolean CheckPermissionTxT(String Username)
    {
        String requestUrl="http://120.79.7.230/WhuSeats_per.txt";
        Request CheckPeimission=new Request.Builder()
                .get()
                .url(requestUrl)
                .build();
        try
        {
            Response re=httpClient.newCall(CheckPeimission).execute();

            String reStr=re.body().string();
            String[] userArray=reStr.split("\r\n");
            List<String> userList=new ArrayList<>(Arrays.asList(userArray));

            //如果第一行控制文本为false 代表关闭登录限制 故这个方法无条件返回true 不要绕晕了
            if(userList.get(0).equals("false"))
                return true;

            if(userList.contains(Username))
                return true;
            else return false;

        }
        catch (Exception e)
        {
            Log.e("Net:CheckPermission", e.getMessage());
            return false;
        }

    }

    /**
     * 获取服务器上储存的吱口令(同步)
     * @return
     */
    public String GetZhicode()
    {
        String requestUrl=CRT_HOST+URL_ZHICODE;
        Request getzhi=new Request.Builder()
                .get()
                .url(requestUrl)
                .build();
        try
        {
            Response response=httpClient.newCall(getzhi).execute();
            return response.body().string();
        }
        catch (IOException e)
        {
            Log.e("GetZhicode:", e.getMessage());
            return "";
        }
    }

    /**
     * 刷新服务器上的吱口令(同步)
     * @return
     */
    public String PutZhicode(String value)
    {
        String requestUrl=CRT_HOST+URL_PUTZHICODE+"?value="+value;
        Request putzhi=new Request.Builder()
                .get()
                .url(requestUrl)
                .build();
        try
        {
            Response response=httpClient.newCall(putzhi).execute();
            return response.body().string();
        }
        catch (IOException e)
        {
            Log.e("PutZhicode:", e.getMessage());
            return "";
        }
    }
    /**
     * 检查更新,用于弹出对话框(同步)
     * @param IsneedWinform 这个参数用于干预本次判断要不要检测服务器上的服务端winform程序的存活情况
     * @return
     */
    public boolean CheckUpdate_IsShowDialog(boolean IsneedWinform)
    {
        //先检查当前版本 这一步用HTTP检查 如果不需要更新 则可以加快速度
        String requestUrl="http://120.79.7.230/appVersion.txt";

        Request CheckUpdate=new Request.Builder()
                .get()
                .url(requestUrl)
                .build();
        try
        {
            Response re=httpClient.newCall(CheckUpdate).execute();
            String reStr=re.body().string();
            //获取远程版本号
            JSONObject versionJobj=new JSONObject(reStr);
            REMOTEVERSION=versionJobj.getInt(BaseActivity.ApplicationContext.getPackageName());
            ISFORCEUPDATE=versionJobj.getBoolean(BaseActivity.ApplicationContext.getPackageName()+"_ISFORCE");

            //如果本地版本号等于远端 返回false 即不用更新
            if(BaseActivity.VERSIONCODE>=REMOTEVERSION)
                return false;
        }
        catch (Exception e)
        {
            Log.e("Net:CheckUpdate", e.getMessage());
            return false;
        }

        if(IsneedWinform)
        {
            //再检查与服务器winform的连接
            try
            {
                //绑定本地套接字并且连接
                ClientSocket=new Socket(ServerIP1,7777);
                Clientop=ClientSocket.getOutputStream();
                Clientin=ClientSocket.getInputStream();
            }
            catch (Exception e)
            {
                Log.e("CheckUpdate","连接到服务器失败\n"+e.getMessage());
                return false;
            }
            return true;
        }
       return true;
    }

    /**
     * 下载更新(异步)
     */
    public void DownLoadUpdate(onTaskResultReturn r,onProgressReturn p)
    {
        //任务执行匿名类
        class DownloadTask extends ResultsTask<Void,Double,File>
        {
            public DownloadTask(onTaskResultReturn i, String name)
            {
                super(i, name);
            }


            @Override
            protected File doInBackground(Void... v)
            {

                JSONObject job = new JSONObject();

                String filename = BaseActivity.PACKAGENAME + "_" + REMOTEVERSION+".apk";//拼接文件名
                File ImageFile = new File(getExternalCacheDir(), filename);
                //如果已经有了这个的完整安装包 则直接跳过下载
                if(ImageFile.exists())
                {
                    //读取保存好的文件正确的大小
                    SharedPreferences apksp=getSharedPreferences("apkdata", MODE_PRIVATE);
                    long RightapkLength=apksp.getLong(filename,0 );
                    //如果文件大小正确
                    if(ImageFile.length()==RightapkLength)
                    {
                        try
                        {
                            ClientSocket.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        return ImageFile;
                    }
                    //否则删除该文件 重新下载
                    try
                    {
                        ImageFile.delete();
                        ImageFile=new File(getExternalCacheDir(), filename);
                    }
                    catch (Exception e)
                    {
                        Log.e("DownLoadApk", e.toString() );
                    }

                }


                //正式开始下载
                try
                {
                    Clientop.flush();

                    job.put("Action", "DownloadUpdate");//动作名
                    job.put("filename", filename);

                    byte[] Jsonbyte = job.toString().getBytes();
                    //发送json数据
                    Clientop.write(Jsonbyte);

                    //接收服务器响应
                    byte[] buff = new byte[2048 * 60];

                    int resultcount = Clientin.read(buff);
                    String ReturnStr = new String(buff, 0, resultcount, "utf-8");
                    //解析一下返回的结果
                    JSONObject ReturnJson = new JSONObject(ReturnStr);
                    String returnStatus = ReturnJson.getString("status");
                    long totalFileLength = ReturnJson.getLong("FileLength");

                    //这里判断一下接收到的返回值是不是OK
                    if (!returnStatus.equals("OK"))
                    {
                        //说明没有查询到相应的文件 直接返回
                        return null;
                    }

                    //这里把该文件的大小保存到SharedPreferences中 便于之后判断文件是否存在
                    SharedPreferences.Editor apkSPeditor=getSharedPreferences("apkdata", MODE_PRIVATE).edit();
                    apkSPeditor.putLong(filename, totalFileLength).apply();


                    long recvlen = 0;
                    int len = 0;
                    //创建文件流
                    FileOutputStream fos = new FileOutputStream(ImageFile);
                    while ((len = Clientin.read(buff)) != -1)
                    {
                        fos.write(buff, 0, len);
                        recvlen += len;

                        //刷新进度
                        publishProgress(recvlen * 100.0 / totalFileLength);

                        // Log.e("Net", String.format("正在写入文件%f%%",recvlen*100.0/totalLength));

                        if (recvlen >= totalFileLength)
                            break;
                    }
                    //文件读取完毕
                    fos.close();
                    Clientin.close();
                    Log.e("Net", String.format("文件写入完毕", recvlen * 100.0 / totalFileLength));
                    //region 利用Linux命令改变文件的权限 使其能够正常安装
                    String[] command = {"chmod", "777", ImageFile.getPath() };
                    ProcessBuilder builder = new ProcessBuilder(command);
                    builder.start();
                    //endregion

                } catch (Exception e)
                {
                    e.printStackTrace();
                    Log.e("DownloadUpdate", e.toString());
                }
                return ImageFile;
            }

            @Override
            protected void onProgressUpdate(Double... values)
            {
                super.onProgressUpdate(values);
                double progress = values[0];
                p.OnSendProgress(progress);
                Log.e("Net", String.format("正在写入文件%f%%", progress));
            }

            @Override
            protected void onPostExecute(File file)
            {
                if (file == null)
                {
                    ResultReturn.OnTaskFailed();
                } else
                    ResultReturn.OnTaskSucceed(file);
            }
        }

        DownloadTask downloadTask=new DownloadTask(r,"DownLoadUpdateTask");
        downloadTask.execute();
    }

    /**
     * 利用OKHttp3 异步下载更新
     */
    public void DownLoadUpdateByHttp(onTaskResultReturn r,onProgressReturn p)
    {
        class DownloadTask extends ResultsTask<Void,Double,File>
        {
            public DownloadTask(onTaskResultReturn i, String name)
            {
                super(i, name);
            }

            @Override
            protected File doInBackground(Void... voids)
            {
                String filename = BaseActivity.PACKAGENAME + "_" + REMOTEVERSION+".apk";//拼接文件名
                File ImageFile = new File(getExternalCacheDir(), filename);

                //如果已经有了这个的完整安装包 则直接跳过下载
                if(ImageFile.exists())
                {
                    //读取保存好的文件正确的大小
                    SharedPreferences apksp = getSharedPreferences("apkdata", MODE_PRIVATE);
                    long RightapkLength = apksp.getLong(filename, 0);
                    //如果文件大小正确
                    if (ImageFile.length() == RightapkLength)
                    {
                        try
                        {
                            ClientSocket.close();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        return ImageFile;
                    }
                    else
                    {
                        try
                        {
                            ImageFile.delete();
                            ImageFile=new File(getExternalCacheDir(), filename);
                        }
                        catch (Exception e)
                        {
                            Log.e("DownLoadApk", e.toString() );
                        }
                    }
                }

                File ImageFile_Final=ImageFile; //构建的final中转量供内部类使用
                //正式开始下载请求
                String requesturl=CRT_OSS+"/apks/"+filename;
                Request downloadRequest=new Request.Builder()
                        .get()
                        .url(requesturl)
                        .build();
                try
                {
                    Response response=httpClient.newCall(downloadRequest).execute();

                    //从head里面获取文件总长度
                    long totalFileLength=Integer.parseInt(response.header("Content-Length"));

                    if(totalFileLength==0)
                    {
                        throw new IOException("获取到的远端文件长度出错");
                    }

                    //这里把该文件的大小保存到SharedPreferences中 便于之后判断文件是否存在
                    SharedPreferences.Editor apkSPeditor=getSharedPreferences("apkdata", MODE_PRIVATE).edit();
                    apkSPeditor.putLong(filename, totalFileLength).apply();


                    byte[] buff = new byte[2048 * 60];            //缓冲池子
                    Clientin=response.body().byteStream();        //构建输入流
                    long recvlen = 0;                             //已接收的文件长度
                    int len = 0;                                  //本次缓冲接收的文件长度
                    //创建输出文件流
                    FileOutputStream fos = new FileOutputStream(ImageFile_Final);
                    while ((len = Clientin.read(buff)) != -1)
                    {
                        fos.write(buff, 0, len);
                        recvlen += len;

                        //刷新进度
                        publishProgress(recvlen * 100.0 / totalFileLength);

                        // Log.e("Net", String.format("正在写入文件%f%%",recvlen*100.0/totalLength));

                        if (recvlen >= totalFileLength)
                            break;
                    }
                    //文件读取完毕
                    fos.close();
                    Clientin.close();
                    Log.e("Net", String.format("文件写入完毕", recvlen * 100.0 / totalFileLength));

                    //region 利用Linux命令改变文件的权限 使其能够正常安装
                    String[] command = {"chmod", "777", ImageFile_Final.getPath() };
                    ProcessBuilder builder = new ProcessBuilder(command);
                    builder.start();
                    //endregion

                }
                catch (IOException e)
                {
                    Log.e("Downloadhttp",e.getLocalizedMessage());
                    return null;
                }
                return ImageFile_Final;
            }

            @Override
            protected void onProgressUpdate(Double... values)
            {
                super.onProgressUpdate(values);
                double progress = values[0];
                p.OnSendProgress(progress);
                Log.e("Net", String.format("正在写入文件%f%%", progress));
            }

            @Override
            protected void onPostExecute(File file)
            {
                if (file == null)
                {
                    ResultReturn.OnTaskFailed();
                } else
                    ResultReturn.OnTaskSucceed(file);
            }
        }

        DownloadTask downloadTask=new DownloadTask(r,"DownLoadUpdateTask");
        downloadTask.execute();
    }

    /**
     * 获取图书馆公告
     */
    public void GetAnnounce(onTaskResultReturn r)
    {
        String url=LIB_URLWITHPORT+URL_ANNOUNCE;
        Request Leaverequest=new Request.Builder()
                .get()
                .addHeader("token", LIB_TOKEN)
                .url(url)
                .build();
        CommonTask tempTask=new CommonTask(r,"GetAnnounceTask",Leaverequest);
        tempTask.execute();
    }

    //endregion






    /**
     * 这是一个公共的不接受参数 返回原始的Json字符串的任务
     * ****************注意 本公共任务只是返回Json字符串 把字符串解析为相应信息不在这里**********
     */
    class CommonTask extends ResultsTask<Void,Void,String>
    {
        Request httpRequest;
        public CommonTask(onTaskResultReturn i,String taskname,Request r)
        {
            super(i,taskname);
            httpRequest=r;
        }

        @Override
        protected String doInBackground(Void... voids)
        {
            try
            {
                //执行操作
                Response re=httpClient.newCall(httpRequest).execute();
                String reStr=re.body().string();
                return reStr;
            }
            catch (IOException e)
            {
                Log.e("Net:CommonTask", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            //根据接口进行处理 这里因为是一个公共的任务 所以它无法处理任务是否正确完成
            super.onPostExecute(s);
            if(s==null)
            {
                ResultReturn.OnTaskFailed();
            }
            else
            {
                ResultReturn.OnTaskSucceed(s);
            }
        }
    }
}

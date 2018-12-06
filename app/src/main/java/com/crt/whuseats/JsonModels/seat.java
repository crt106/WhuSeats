package com.crt.whuseats.JsonModels;

/**
 * @see JsonModel_MobileFiltrate 移动端过滤类使用的数据结构 单列出来
 */
public class seat implements Comparable<seat>
{
    /**
     * 座位唯一ID
     */
    public int seatid;
    /**
     * 座位名称（编号）
     */
    public String name;
    /**
     * 该座位的名字转换为数字形式
     */
    public int seatnameInteger;
    /**
     * 该座位的类型 正常值为seat
     */
    public String seattype;
    /**
     * 座位状态 有FREE 等
     */
    public String seatstatus;
    /**
     * 是否靠窗
     */
    public boolean window;
    /**
     * 是否有电源
     */
    public boolean power;
    /**
     * 是否有电脑
     */
    public boolean computer;
    /**
     * 暂时不清楚含义
     */
    public boolean local;

    public seat(int seatid, String name, String seattype, String seatstatus, boolean window, boolean power, boolean computer, boolean local)
    {
        this.seatid = seatid;
        this.name = name;
        this.seattype = seattype;
        this.seatstatus = seatstatus;
        this.window = window;
        this.power = power;
        this.computer = computer;
        this.local = local;
        seatnameInteger=Integer.parseInt(name);
    }

    /**
     * 为了给座位排序实现的接口
     * @param o
     * @return
     */
    @Override
    public int compareTo(seat o) {
        if(this.seatnameInteger<o.seatnameInteger)
            return -1;
        else return 1;
    }
}

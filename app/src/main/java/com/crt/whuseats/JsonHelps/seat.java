package com.crt.whuseats.JsonHelps;

public class seat implements Comparable<seat>
{
    public int seatid;
    public String name;
    public int seatnameInteger; //该座位的名字转换为数字形式
    public String seattype;
    public String seatstatus;
    public boolean window;
    public boolean power;
    public boolean computer;
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

    @Override
    public int compareTo(seat o) {
        if(this.seatnameInteger<o.seatnameInteger)
            return -1;
        else return 1;
    }
}

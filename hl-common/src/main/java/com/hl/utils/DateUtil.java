package com.hl.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil
{
    /**
     * 获取字符串时间
     * @return
     */
    public static String getNowStringDateTime()
    {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(date);
    }

    /**
     * 获取当前的timestamp
     * @return
     */
    public static Timestamp getNowSqlDateTime()
    {
        Timestamp date = new Timestamp(new Date().getTime());
        return date;
    }

    /**
     * 获取相差的时间分钟数
     * @param oldTime 原来的时间
     * @param newTime 现在的时间
     * @return
     * @throws ParseException
     */
    public static long getTime(String oldTime,String newTime) throws ParseException
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long NTime = df.parse(newTime).getTime();
        //从对象中拿到时间
        long OTime = df.parse(oldTime).getTime();
        long diff = (NTime - OTime) / 1000 / 60;
        return diff;
    }

    /**
     * 传入具体日期 ，返回具体日期减少一天
     * @param date
     * @return
     * @throws ParseException
     */
    public static String subDay(String date) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = sdf.parse(date);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.DAY_OF_MONTH, -1);
        Date dt1 = rightNow.getTime();
        String reStr = sdf.format(dt1);
        return reStr;
    }

}

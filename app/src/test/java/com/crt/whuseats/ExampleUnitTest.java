package com.crt.whuseats;

import com.crt.whuseats.Model.TimeItems;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest
{
    @Test
    public void addition_isCorrect()
    {
        assertEquals(4, 2 + 2);
    }

    /**
     * 从时间ID获取时间结构
     * @param ID
     * @return
     */

    @Test
    public TimeItems.TimeStruct GetTimeStructByID(int ID)
    {
        try
        {
            return TimeItems.IDMap.get(ID);

        } catch (Exception e)
        {
            return null;
        }
    }
}
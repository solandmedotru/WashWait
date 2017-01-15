
package ru.solandme.washwait.model.pojo.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Open {

    @SerializedName("day")
    @Expose
    private int day;
    @SerializedName("time")
    @Expose
    private String time;

    /**
     * 
     * @return
     *     The day
     */
    public int getDay() {
        return day;
    }

    /**
     * 
     * @param day
     *     The day
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * 
     * @return
     *     The time
     */
    public String getTime() {
        return time;
    }

    /**
     * 
     * @param time
     *     The time
     */
    public void setTime(String time) {
        this.time = time;
    }

}

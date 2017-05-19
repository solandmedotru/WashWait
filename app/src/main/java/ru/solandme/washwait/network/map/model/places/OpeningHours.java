
package ru.solandme.washwait.network.map.model.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    private boolean openNow;
    @SerializedName("periods")
    @Expose
    private List<Period> periods = null;
    @SerializedName("weekday_text")
    @Expose
    private List<String> weekdayText = null;

    /**
     * 
     * @return
     *     The openNow
     */
    public boolean isOpenNow() {
        return openNow;
    }

    /**
     * 
     * @param openNow
     *     The open_now
     */
    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    /**
     * 
     * @return
     *     The periods
     */
    public List<Period> getPeriods() {
        return periods;
    }

    /**
     * 
     * @param periods
     *     The periods
     */
    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    /**
     * 
     * @return
     *     The weekdayText
     */
    public List<String> getWeekdayText() {
        return weekdayText;
    }

    /**
     * 
     * @param weekdayText
     *     The weekday_text
     */
    public void setWeekdayText(List<String> weekdayText) {
        this.weekdayText = weekdayText;
    }

}

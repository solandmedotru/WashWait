
package ru.solandme.washwait.POJO.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Aspect {

    @SerializedName("rating")
    @Expose
    private int rating;
    @SerializedName("type")
    @Expose
    private String type;

    /**
     * 
     * @return
     *     The rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * 
     * @param rating
     *     The rating
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

}

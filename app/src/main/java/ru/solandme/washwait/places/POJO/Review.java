
package ru.solandme.washwait.places.POJO;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("aspects")
    @Expose
    private List<Aspect> aspects = null;
    @SerializedName("author_name")
    @Expose
    private String authorName;
    @SerializedName("author_url")
    @Expose
    private String authorUrl;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("profile_photo_url")
    @Expose
    private String profilePhotoUrl;
    @SerializedName("rating")
    @Expose
    private int rating;
    @SerializedName("relative_time_description")
    @Expose
    private String relativeTimeDescription;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("time")
    @Expose
    private int time;

    /**
     * 
     * @return
     *     The aspects
     */
    public List<Aspect> getAspects() {
        return aspects;
    }

    /**
     * 
     * @param aspects
     *     The aspects
     */
    public void setAspects(List<Aspect> aspects) {
        this.aspects = aspects;
    }

    /**
     * 
     * @return
     *     The authorName
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * 
     * @param authorName
     *     The author_name
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * 
     * @return
     *     The authorUrl
     */
    public String getAuthorUrl() {
        return authorUrl;
    }

    /**
     * 
     * @param authorUrl
     *     The author_url
     */
    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    /**
     * 
     * @return
     *     The language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * 
     * @param language
     *     The language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * 
     * @return
     *     The profilePhotoUrl
     */
    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    /**
     * 
     * @param profilePhotoUrl
     *     The profile_photo_url
     */
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

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
     *     The relativeTimeDescription
     */
    public String getRelativeTimeDescription() {
        return relativeTimeDescription;
    }

    /**
     * 
     * @param relativeTimeDescription
     *     The relative_time_description
     */
    public void setRelativeTimeDescription(String relativeTimeDescription) {
        this.relativeTimeDescription = relativeTimeDescription;
    }

    /**
     * 
     * @return
     *     The text
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * @param text
     *     The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 
     * @return
     *     The time
     */
    public int getTime() {
        return time;
    }

    /**
     * 
     * @param time
     *     The time
     */
    public void setTime(int time) {
        this.time = time;
    }

}

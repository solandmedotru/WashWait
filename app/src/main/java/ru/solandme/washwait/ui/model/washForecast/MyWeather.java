package ru.solandme.washwait.ui.model.washForecast;

import android.os.Parcel;
import android.os.Parcelable;

public class MyWeather implements Parcelable {

    private long time;
    private String description;
    private float tempMin;
    private float tempMax;
    private float pressure;
    private float humidity;
    private float windSpeed;
    private float windDirection;
    private float snow;
    private float rain;

    private int imageRes;
    private float precipitation;
    private int carPicture;

    public MyWeather() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getTempMin() {
        return tempMin;
    }

    public void setTempMin(float tempMin) {
        this.tempMin = tempMin;
    }

    public float getTempMax() {
        return tempMax;
    }

    public void setTempMax(float tempMax) {
        this.tempMax = tempMax;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public float getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(float windDirection) {
        this.windDirection = windDirection;
    }

    public float getSnow() {
        return snow;
    }

    public void setSnow(float snow) {
        this.snow = snow;
    }

    public float getRain() {
        return rain;
    }

    public void setRain(float rain) {
        this.rain = rain;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public float getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(float dirtyCounter) {
        this.precipitation = dirtyCounter;
    }

    public int getCarPicture() {
        return carPicture;
    }

    public void setCarPicture(int carPicture) {
        this.carPicture = carPicture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.time);
        dest.writeString(this.description);
        dest.writeFloat(this.tempMin);
        dest.writeFloat(this.tempMax);
        dest.writeFloat(this.pressure);
        dest.writeFloat(this.humidity);
        dest.writeFloat(this.windSpeed);
        dest.writeFloat(this.windDirection);
        dest.writeFloat(this.snow);
        dest.writeFloat(this.rain);
        dest.writeInt(this.imageRes);
        dest.writeFloat(this.precipitation);
        dest.writeInt(this.carPicture);
    }

    protected MyWeather(Parcel in) {
        this.time = in.readLong();
        this.description = in.readString();
        this.tempMin = in.readFloat();
        this.tempMax = in.readFloat();
        this.pressure = in.readFloat();
        this.humidity = in.readFloat();
        this.windSpeed = in.readFloat();
        this.windDirection = in.readFloat();
        this.snow = in.readFloat();
        this.rain = in.readFloat();
        this.imageRes = in.readInt();
        this.precipitation = in.readFloat();
        this.carPicture = in.readInt();
    }

    public static final Creator<MyWeather> CREATOR = new Creator<MyWeather>() {
        @Override
        public MyWeather createFromParcel(Parcel source) {
            return new MyWeather(source);
        }

        @Override
        public MyWeather[] newArray(int size) {
            return new MyWeather[size];
        }
    };
}

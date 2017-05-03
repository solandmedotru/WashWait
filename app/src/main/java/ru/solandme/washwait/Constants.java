package ru.solandme.washwait;

public class Constants {
    public static final boolean RUN_FROM_ACTIVITY = false;
    public static final boolean RUN_FROM_BACKGROUND = true;
    public static final double DEFAULT_LONGITUDE = 37.6155600;
    public static final double DEFAULT_LATITUDE = 55.7522200;
    public static final String DEFAULT_UNITS = "metric";
    public static final String DEFAULT_FORECAST_DISTANCE = "2";
    public static final int NOTIFICATION_ID = 1981;
    public static final double DEFAULT_DIRTY_LIMIT = 0.0;
    public static final String RUN_FROM = "isRunFromBackground";
    public static final String ACTION_GET_FORECAST = "ru.solandme.washwait.action.GET_FORECAST";
    public static final String NOTIFICATION = "ru.solandme.washwait.service.receiver";
    public static final String TAG_TASK_PERIODIC = "PeriodicalMeteoWashTask";
    public static final int FIRST_DAY_POSITION = 0;
    public static final int PERIODICAL_TIMER = 43200; //21600
    public static final String TAG_ABOUT = "about";
    public static final int MA_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    public static final long FASTEST_INTERVAL = 2000; /* 2 sec */
    public static final int MINIMUM_SESSION_DURATION = 20000;
    public static final String DEFAULT_WEATHER_PROVIDER = "OpenWeatherMap";
}

package com.example.tinyweather;

/**
 * Created by MH on 2017-08-26.
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * POJO Class
 * RSS 필드에서 제공하는 모든 값을 처리할 수 있도록 설계한다.
 * (단, 12/6시간 예상 강수량과 적성량 값은 제외)
 *
 * 동네예보와 중기예보에서 제공되는 필드가 상이함에 따라
 * 모든 필드는 유효하지 않을 수 있으며, 파싱된 대상에 따라 적절하게 설정된다.
 */
public class Weather {
    Date date;
    String weather;
    String temperature;
    String maxTemperature;
    String minTemperature;
    String humidity;
    String rainfall;
    String chanceOfrain;
    String windSpeed;
    String windDirection;

    public int getForecastDay() { //예보 날짜
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public int getForecastHour() { //예보 시간
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.HOUR_OF_DAY);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date time) {
        this.date = time;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(String maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public String getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(String minTemperature) {
        this.minTemperature = minTemperature;
    }

    public String getRainfall() {
        return rainfall;
    }

    public void setRainfall(String rainfall) {
        this.rainfall = rainfall;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed.substring(0,3);
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getChanceOfrain() {
        return chanceOfrain;
    }

    public void setChanceOfrain(String chanceOfrain) {
        this.chanceOfrain = chanceOfrain;
    }

    @Override
    public String toString() {
        SimpleDateFormat formatTime = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.KOREAN);
        String timeString = formatTime.format(date);

        return String.format(Locale.getDefault(), "\n" +
                        "시간:%s\n" +
                        "날씨:%s\n" +
                        "온도:%s 최저:%s 최고:%s\n" +
                        "습도:%s\n", timeString, weather, temperature, minTemperature,
                maxTemperature, humidity);
    }
}

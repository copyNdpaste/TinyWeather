package com.example.tinyweather;

import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by MH on 2017-08-26.
 */

public class WeatherFactory {
    /**
     * 기상청 동네예보 RSS 필드 정의
     */
    public static class TownToken {
        // 시간
        public static String HOUR = "hour";
        // 오늘 부터 경과일
        public static String DAY = "day";
        // 온도
        public static String TEMP = "temp";
        // 최고온도
        public static String TEMP_MAX = "tmx";
        // 최저온도
        public static String TEMP_MIN = "tmn";
        // 하늘상태 코드
        public static String SKY_STATE = "sky";
        // 강수코드
        public static String PRECIPITATION_STATE = "pty";
        // 날씨(한국어)
        public static String WEATHER = "wfKor";
        // 풍속
        public static String WIND = "ws";
        // 풍향(한국어)
        public static String WIND_DIRECTION = "wdKor";
        // 습도
        public static String HUMIDITY = "reh";
        // 강수확률
        public static String CAHCE_OF_RAIN = "pop";
        // 강수량 6시간
        public static String RAINFALL_6HOUR ="r06";
    }
    /**
     * 기상청 중기예보 RSS 필드 정의
     */
    public static class MidTermToken {
        // 년월시분
        public static String TIME = "tmEf";
        // 날씨
        public static String WEATHER = "wf";
        // 최저온도
        public static String TEMP_MIN = "tmn";
        // 최고온도
        public static String TEMP_MAX = "tmx";
    }

    public static WeatherFactory INSTANCE = null;

    private WeatherFactory() {}

    public static WeatherFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WeatherFactory();
        }
        return INSTANCE;
    }

    public static Weather create(ForecastType type, Date publish, Element data) {
        Weather weather = new Weather();

        if (type == ForecastType.TOWN) { //예보 타입이 도시라면 (상단 파란색 화면 부분)
            Node dayNode = data.getElementsByTagName(TownToken.DAY).item(0);
            Node hourNode = data.getElementsByTagName(TownToken.HOUR).item(0);

            Log.d("CHECK", "dayNode:" + dayNode.getTextContent() + "//hourNode:" + hourNode.getTextContent());

            Calendar c = Calendar.getInstance(); //달력 인스턴스
            c.setTime(publish); //시간 배포
            c.set(Calendar.DAY_OF_MONTH, publish.getDate() + Integer.valueOf(dayNode.getTextContent())); //날짜
            c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hourNode.getTextContent())); //시간
            weather.setDate(c.getTime());

            Node tempNode = data.getElementsByTagName(TownToken.TEMP).item(0);
            weather.setTemperature(tempNode.getTextContent()); //온도

            Node humNode = data.getElementsByTagName(TownToken.HUMIDITY).item(0);
            weather.setHumidity(humNode.getTextContent()); //습도

            Node weatherNode = data.getElementsByTagName(TownToken.WEATHER).item(0);
            weather.setWeather(weatherNode.getTextContent()); //날씨

            Node windNode = data.getElementsByTagName(TownToken.WIND).item(0);
            weather.setWindSpeed(windNode.getTextContent()); //풍속

            Node rainNode = data.getElementsByTagName(TownToken.RAINFALL_6HOUR).item(0);
            weather.setRainfall(String.valueOf(Float.valueOf(rainNode.getTextContent())/6.0).substring(0,3)); //비

            Node rainChanceNode = data.getElementsByTagName(TownToken.CAHCE_OF_RAIN).item(0);
            weather.setChanceOfrain(rainChanceNode.getTextContent()); //강수확률
        }
        else if (type == ForecastType.MID_TERM){ //예보 타입이 중기 예보라면 (카드 뷰 부분)
            Node timeNode = data.getElementsByTagName(MidTermToken.TIME).item(0);
            weather.setDate(Utils.convertStringToDateType2(timeNode.getTextContent())); //날짜

            Node maxTempNode = data.getElementsByTagName(MidTermToken.TEMP_MAX).item(0);
            weather.setMaxTemperature(maxTempNode.getTextContent()); //최대 온도

            Node minTempNode = data.getElementsByTagName(MidTermToken.TEMP_MIN).item(0);
            weather.setMinTemperature(minTempNode.getTextContent()); //최저 온도

            Node weatherNode = data.getElementsByTagName(MidTermToken.WEATHER).item(0);
            weather.setWeather(weatherNode.getTextContent()); //날씨
        }

        return weather;
    }
}
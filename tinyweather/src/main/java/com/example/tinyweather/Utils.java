package com.example.tinyweather;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by MH on 2017-08-26.
 */

public class Utils {

    /**
     * 년월일시분 의 순차나열 형태의 문자열 시간을 {@link Date}로 반환
     * @param dateString
     * @return
     */
    public static Date convertStringToDateType1(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date convertStringToDateType2(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String currentStringTime() {
        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss", Locale.KOREA );
        Date currentTime = new Date ( );
        String dTime = formatter.format ( currentTime );

        return dTime;
    }

    /**
     * 날씨 별 아이콘 설정
     * @param wtString 날씨상태
     * @return
     */
    public static int weatherStringToIcon(String wtString) {
        if (wtString.equals("맑음")) {
            return R.drawable.ic_weather_sun;
        } else if (wtString.equals("구름 조금")) {
            return R.drawable.ic_weather_little_cloud;
        } else if (wtString.equals("구름 많음")) {
            return R.drawable.ic_weather_many_cloud;
        } else if (wtString.equals("흐리고 비")) {
            return R.drawable.ic_weather_cloudy_rain;
        } else if (wtString.equals("비")) {
            return R.drawable.ic_weather_rain;
        } else {
            Log.w("CHECK", "등록된 이미지가 없습니다.");
            return R.drawable.ic_sample;
        }
    }

    public static String dayToString(int dayNum) {
        String day = null;

        switch(dayNum){
            case 1:
                day = "일";
                break ;
            case 2:
                day = "월";
                break ;
            case 3:
                day = "화";
                break ;
            case 4:
                day = "수";
                break ;
            case 5:
                day = "목";
                break ;
            case 6:
                day = "금";
                break ;
            case 7:
                day = "토";
                break ;

        }
        return day;
    }
}

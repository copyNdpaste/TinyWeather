package com.example.tinyweather;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by MH on 2017-08-26.
 */

public class WeatherForecast {
    public static final String TAG = WeatherForecast.class.getSimpleName();

    private final String townUrl = "http://web.kma.go.kr/wid/queryDFSRSS.jsp?zone=";
    //    private final String termUrl = "http://web.kma.go.kr/weather/forecast/mid-term-rss3.jsp?stnId=";
    private String townCode;
    //    private String termCode;
    private String cityName;

    /**
     * 날짜별 {@link Weather} 객체 리스트를 제공
     * '기상청' 에서 제공하는 날씨정보는 일수마다 제공되는 시간별 예보의 차이가 있다.
     * 당일~모레까지는 3시간 단위의 기상정보를 제공하며, 이후는 약6일 정도는 오전/오후로 제공되고, 이후는
     * 하루 단위로 시간을 제공한다. 즉, HashMap 을 이용하여 처리하는 것이 적절하다고 판단된다.
     */
    private HashMap<Integer, HashMap<Integer, Weather>> mWeatherPerDateMap;

    // 기상청 동네예보 발생 시간
    private Date mTownPublishDate;
    // 기상청 중기예보 발생 시간
    // private Date mTermPublishDate;

    // 로딩상태 표현
    private boolean isRunning = false;

    private OnListener onListener;

    public WeatherForecast() {
        mWeatherPerDateMap = new HashMap<>();
    }

    /**
     * @param cityName 지역(시) 이름
     */
    public void execute(String cityName){
        if (!isRunning) { //로딩중이 아니라면
            if (cityName != null) //도시 이름이 null이 아니라면
                this.cityName = cityName; //도시 이름

            SupportedZone supportedZone = SupportedZone.getInstance();
            // this.termCode = supportedZone.getDoCodeFromSi(this.cityName);
            this.townCode = supportedZone.getSiCode(this.cityName); //시 코드

            //     Log.i(TAG, String.format("지역:%s 동네예보:%s 중기예보:%s 기상예보 워커 실행",
            //          this.cityName, townCode, termCode));
            // 예외처리
            // if (termCode == null || townCode == null) {
            if (townCode == null) {
                throw new NullPointerException("잘못된 지역 정보를 입력하였습니다.");
            }
            new Worker().execute();
        } else {
            Log.w(TAG, "이미 로딩 중 입니다.");
        }
    }

    public void setOnListener(OnListener onListener) {
        this.onListener = onListener;
    }

    public interface OnListener {
        void onStarted();
        void onFinished();
    }

    private void errorHandle() {
        if (mWeatherPerDateMap.size() == 0 || isRunning) {
            throw new RuntimeException("정보를 조회할 수 없습니다: 엔트리수:"
                    + mWeatherPerDateMap.size() + " 로딩중:"+isRunning );
        }
    }

    // ===================================================================================
    // 공개 API 시작점
    // ===================================================================================

    public String getCityName() {
        return this.cityName;
    } //도시 이름 얻어옴

    /**
     * 가장 가까운
     * 미래의 날씨 정보
     */

    public Weather getCurrentWeather() { //현재 날씨 메인 화면의 파란 곳의 정보
        errorHandle();

        Calendar c = Calendar.getInstance(); //달력에서 인스턴스 받아옴
        int day = c.get(Calendar.DAY_OF_MONTH); //해당 달의 날짜를 날로 지정

        HashMap<Integer, Weather> todayMap =  mWeatherPerDateMap.get(day);//오늘 날씨
        if (todayMap != null) {
            SortedSet<Integer> keys = new TreeSet<>(todayMap.keySet());
            return todayMap.get(keys.first());
        } else {
            HashMap<Integer, Weather> map =  mWeatherPerDateMap.get(day+1);//내일 날씨
            SortedSet<Integer> keys = new TreeSet<>(map.keySet());
            return map.get(keys.first());
        }
    }

    /**
     * 가장 가까운 시간(0시)의 내일 날씨 정보
     */
    public Weather getTomorrowWeather() {
        errorHandle();

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH)+1;//내일

        HashMap<Integer, Weather> map =  mWeatherPerDateMap.get(day);
        SortedSet<Integer> keys = new TreeSet<>(map.keySet());
        return map.get(keys.first());
    }

    /**
     * 입력된 날짜의 기상정보 목록
     * @param day
     * @return
     */
    public List<Weather> getWeatherList(int day) {
        List<Weather> list = new ArrayList<>();

        HashMap<Integer, Weather> map =  mWeatherPerDateMap.get(day);
        for (Integer key : map.keySet()) {
            list.add(map.get(key));
        }
        return list;
    }

    /**
     * 가장 가까운 시간(0시)의 모레 날씨 정보
     * @return
     */
    public Weather getAfterTomorrowWeather() {
        errorHandle();

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH)+2;//모레

        HashMap<Integer, Weather> map =  mWeatherPerDateMap.get(day);
        SortedSet<Integer> keys = new TreeSet<>(map.keySet());
        return map.get(keys.first());
    }

    /**
     * 기상예보
     * 내일, 모레, 이후의 모든 0시의 날씨를 반환한다.
     * @return
     */

    public List<Weather> getFutureWeathersOfZero() {
        List<Weather> weathers = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.DAY_OF_MONTH);

        // 일자별 23시의 데이터 수집
        for (Integer day : mWeatherPerDateMap.keySet()) {
            HashMap<Integer, Weather> map = mWeatherPerDateMap.get(day);

            if (today == day) continue;

            for (Integer hour : map.keySet()) {
                if (hour == 0) {
                    weathers.add(map.get(hour));
                    break;
                }
            }
        }

        // 날짜 순 정렬
        Collections.sort(weathers, new Comparator<Weather>() {
            @Override
            public int compare(Weather lhs, Weather rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });

        return weathers;
    }

    public List<Weather> getWeatherListWithThreeHour() {
        return null;
    }

    public List<Weather> getMidTermWeather() {
        return null;
    }

    // ===================================================================================
    // 공개 API 종료점
    // ===================================================================================

    /**
     * 날씨예보 엔트리 추가
     * @param weather 추가할 객체
     */
    private void addWeather(Weather weather) {
        HashMap<Integer, Weather> hourMap = mWeatherPerDateMap.get(weather.getForecastDay());
        if (hourMap == null) {
            hourMap = new HashMap<>();
        }

        hourMap.put(weather.getForecastHour(), weather);

        mWeatherPerDateMap.put(weather.getForecastDay(), hourMap);

        Log.i(TAG, String.format(Locale.getDefault(), "로컬캐시 - 일:%d 시:%d 기상예보 추가:",
                weather.getForecastDay(), weather.getForecastHour()) + weather.toString());
    }

    class Worker extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            isRunning = true;
            Log.i(TAG, "----------------------- 기상예보 데이터 로딩 시작 ---------------------------");

            // 로딩시작 상태 알림
            onListener.onStarted();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            isRunning = false;
            Log.i(TAG, "----------------------- 기상예보 데이터 로딩 완료 ---------------------------");

            // 로딩완료 상태 알림
            onListener.onFinished();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // RSS 파싱을 위한 객체 생성
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                // 동네예보 파싱
                Document townDocument = documentBuilder.parse(townUrl + townCode);
                townDocument.getDocumentElement().normalize();
                parseToCacheFromTownRSS(townDocument);

                // 중기예보 파싱
                // Document termDocument = documentBuilder.parse(termUrl + termCode);
                //       termDocument.getDocumentElement().normalize();
                //         parseToCacheFromMidTermRSS(termDocument);


                Calendar c = Calendar.getInstance();
                generateMinMaxTemperature(mWeatherPerDateMap.get(c.get(Calendar.DAY_OF_MONTH)+1)); //내일 최저 최고 온도 생성
                generateMinMaxTemperature(mWeatherPerDateMap.get(c.get(Calendar.DAY_OF_MONTH)+2)); //모레 최저 최고 온도 생성
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * 동네예보 RSS 파싱
         * 동네예보는 설정된 법정도 코드의 당일 ~ 모레까지의 시간별 기상 예보한다.
         * 동네예보는 '리'단위 까지 구분할 수 있지만, 프로젝트의 특성상 시단위로 제한한다.
         */
        private void parseToCacheFromTownRSS(Document document) {
            // 'tm' 필드 파싱
            Element publishTime = (Element)document.getElementsByTagName("tm").item(0);
            mTownPublishDate = Utils.convertStringToDateType1(publishTime.getTextContent());

            // 'data' 필드 파싱
            NodeList nodeList = document.getElementsByTagName("data");
            for (int i = 0; i < nodeList.getLength(); i++){
                // 기상예보 객체 생성 및 내부캐시 추가
                Element element = (Element)nodeList.item(i);
                addWeather(WeatherFactory.create(ForecastType.TOWN, mTownPublishDate, element));
            }
        }
/*
        private void parseToCacheFromMidTermRSS(Document document) {
            // 중기예보 - 발행시간 파싱
            Element publishTime = (Element)document.getElementsByTagName("tm").item(0);
            mTermPublishDate = Utils.convertStringToDateType1(publishTime.getTextContent());

            // 중기예보 - 지역별 정보 파싱
            NodeList nodeList = document.getElementsByTagName("location");
            for (int i = 0; i < nodeList.getLength(); i++){
                Element element = (Element)nodeList.item(i);
                //'시' 정보
                String city = element.getElementsByTagName("city").item(0).getTextContent();
                if (cityName.equals(city)) {
                    NodeList dataNodeList = element.getElementsByTagName("data");
                    for (int j = 0; j < dataNodeList.getLength(); j++) {
                        Element dataElmt = (Element)dataNodeList.item(j);
                        addWeather(WeatherFactory.create(
                                ForecastType.MID_TERM, mTermPublishDate, dataElmt));
                    }
                }
            }
        }
*/
        /**
         * 내일과 모레 날씨정보는 최저/최고 온도가 없는 상태임으로
         * 수집된 시간별 온도정보를 기준으로 값을 생성한다.
         * @param dayWeatherMap 내일 또는 모레 날씨목록
         */
        private void generateMinMaxTemperature(HashMap<Integer, Weather> dayWeatherMap) {
            if (dayWeatherMap == null || dayWeatherMap.size() < 2) {
                throw new NullPointerException("최저 최고온도 생성 오류");
            }

            List<Map.Entry<Integer, Weather>> list = new LinkedList<>(dayWeatherMap.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<Integer, Weather>>() {
                @Override
                public int compare(Map.Entry<Integer, Weather> lhs, Map.Entry<Integer, Weather> rhs) {
                    return Float.valueOf(lhs.getValue().getTemperature()).compareTo(
                            Float.valueOf(rhs.getValue().getTemperature()));
                }
            });

            Map.Entry<Integer, Weather> minEntry = list.get(0); //최저온도는 리스트의 첫번째 데이터
            Map.Entry<Integer, Weather> maxEntry = list.get(list.size()-1); //최고 온도는 리스트의 마지막 데이터

            for (Integer key : dayWeatherMap.keySet()) {
                dayWeatherMap.get(key).setMinTemperature(minEntry.getValue().getTemperature());
                dayWeatherMap.get(key).setMaxTemperature(maxEntry.getValue().getTemperature());
            }
        }
    }
}

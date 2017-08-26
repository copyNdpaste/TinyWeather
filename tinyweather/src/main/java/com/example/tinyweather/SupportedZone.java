package com.example.tinyweather;

/**
 * Created by MH on 2017-08-26.
 */

import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 기상청 기상예보 지원 가능한 지역의 조회(번호)값을 관리하는 클래스
 * Application 실행 시점에 초기화 하는 것을 권장.
 */
public class SupportedZone {
    public static final String TAG = SupportedZone.class.getSimpleName();

    /**
     * '도' / '코드' 형태
     * 예제) 경기도 - 109
     */
    //                  도   도 코드
    private HashMap<String, String> mDoZoneList;
    /**
     * '도' / '시 코드' 목록
     * 예제) 경기도 - [수원:4111000000 ...]
     */
    //                  시   시 코드
    private HashMap<String, ArrayList<Zone>> mTownZoneList;


    private HashMap<String, ArrayList<Zone>> TownList;


    private static SupportedZone instance = null;

    private SupportedZone() {
        mDoZoneList = new HashMap<>();
        mTownZoneList = new HashMap<>();

    }

    public static SupportedZone getInstance() {
        if (instance == null) { //인스턴스가 null이면
            instance = new SupportedZone(); //인스턴스에 지역 대입
        }
        return instance; //지역 반환
    }

    /**
     * supported_zone.json 파일을 이용하여 도시별 조회 코드번호를 초기화 한다.
     * @param resources
     */
    public void setup(Resources resources) {
        JsonResourceReader reader = new JsonResourceReader( //supported_zon에 있는 데이터를 Json 리더로 가져온다.
                resources, R.raw.supported_zone);

        try {
            JSONObject root = new JSONObject(reader.getJsonString()); //Json 문자열을 받아옴
            JSONArray supportedList = root.getJSONArray("supported"); // /res/raw/supported_zon에서 supported 라는 이름의 배열을 리스트로 받아옴

            for (int i = 0; i < supportedList.length(); i++) { //DB의 길이만큼
                JSONObject entry = supportedList.getJSONObject(i); //리스트의 객체 하나하나를 entry에 넣는다
                String doName = entry.getString("이름"); //이름을 도 이름으로
                String doCode = entry.getString("코드"); //코드를 도 코드로
                JSONArray array = entry.getJSONArray("도시"); //도시라는 배열을 array에

                // '도' 추가
                mDoZoneList.put(doName, doCode);

                ArrayList<Zone> towns = new ArrayList<>();
                for (int j = 0; j < array.length(); j++) {
                    JSONObject town = array.getJSONObject(j);
                    Zone zone = new Zone(town.getString("이름"), town.getString("코드"));
                    Log.d(TAG, "지원 가능한 지억:" + town.getString("이름"));
                    towns.add(zone);
                }

                // '도' 에 포함된 '시' 추가
                mTownZoneList.put(doName, towns);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * '시' 단위 동네예보 조회번호를 반환한다.
     * @param name '시' 이름
     * @return 조회번호
     */
    public String getSiCode(String name) {
        String targetCode = null;

        for (String key : mTownZoneList.keySet()) {
            for (Zone zone : mTownZoneList.get(key)) {
                if (zone.name.equals(name)) {
                    targetCode = zone.code;
                    break;
                }
            }
        }

        return targetCode;
    }

    /**
     * '시' 가 포함된 '도'의 중기예보 조회번호를 반환한다.
     * @param name '시' 이름
     * @return 조회번호
     */

    public String getDoCodeFromSi(String name) {
        String targetCode = null;

        for (String key : mTownZoneList.keySet()) {
            for (Zone zone : mTownZoneList.get(key)) {
                if (zone.name.equals(name)) {
                    targetCode = mDoZoneList.get(key);
                    break;
                }
            }
        }

        return targetCode;
    }

    /**
     * 시가 포함된 도의 이름을 구한다.
     * @param name '시' 이름
     * @return '도' '시' (경기도 수원시)
     */
    public String getFullName(String name) {
        String targetName = null;

        for (String key : mTownZoneList.keySet()) {
            for (Zone zone : mTownZoneList.get(key)) {
                if (zone.name.equals(name)) {
                    targetName = zone.name ;
                    break;
                }
            }
        }

        return targetName;
    }

    /**
     * @return 지원 가능한 지역의 모든 이름을 반환
     */
    public List<String> getSupportedZoneList() {
        List<String> supportedZoneList = new ArrayList<>();

        for (String key : mTownZoneList.keySet()) {
            for (Zone zone : mTownZoneList.get(key)) {
                //String entry = key + " " + zone.name +"시";
                supportedZoneList.add(zone.name);
            }
        }

        return supportedZoneList;
    }


    class Zone {
        String name;
        String code;

        public Zone(String name, String code) {
            this.name = name;
            this.code = code;
        }
    }
}

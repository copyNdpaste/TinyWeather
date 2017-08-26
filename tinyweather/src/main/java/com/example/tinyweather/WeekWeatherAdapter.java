package com.example.tinyweather;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by MH on 2017-08-26.
 */

class WeekWeatherAdapter extends RecyclerView.Adapter<WeekWeatherAdapter.DataHolder> {
    ArrayList<Weather> mDataSet;

    Context context;

    public class DataHolder extends RecyclerView.ViewHolder {
        ImageView mWeatherIcon;

        TextView mMonthText;
        TextView mDateText;
        TextView mDayText;
        TextView mMinTemperature;
        TextView mMaxTemperature;
        TextView mWeatherText;

        public DataHolder(View itemView) {
            super(itemView);

            mWeatherIcon = (ImageView)itemView.findViewById(R.id.weather_icon); //날씨 아이콘
            mMinTemperature = (TextView)itemView.findViewById(R.id.min_temperature_text); //최저 온도
            mMaxTemperature = (TextView)itemView.findViewById(R.id.max_temperature_text); //최고 온도
            mWeatherText = (TextView)itemView.findViewById(R.id.weather_text); //날씨 정보를 나타내는 문구

            mMonthText = (TextView)itemView.findViewById(R.id.month_text); //달
            mDateText = (TextView)itemView.findViewById(R.id.date_text); //날짜
            mDayText = (TextView)itemView.findViewById(R.id.day_text); //요일
        }
    }

    public WeekWeatherAdapter() {
        mDataSet = new ArrayList<>();
    } //주간 날씨를 배열리스트로

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.card_week_weather_view, parent, false); //주간 날씨를 카드 뷰에

        context = parent.getContext();

        return new DataHolder(rootView);
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) { //카드 뷰에 뿌려줄 정보들
        Weather weather = mDataSet.get(position);

        holder.mMinTemperature.setText(weather.getMinTemperature());
        holder.mMaxTemperature.setText(weather.getMaxTemperature());

        if (weather.getWeather() != null) { //날씨 얻어오는게 null이 아닐 시
            holder.mWeatherText.setText(weather.getWeather());
            holder.mWeatherIcon.setImageResource(Utils.weatherStringToIcon(weather.getWeather()));
        }

        // 일자정보 입력
        if (weather.getDate() != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(weather.getDate());

            holder.mMonthText.setText(String.valueOf(c.get(Calendar.MONTH)+1));
            holder.mDateText.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));

            int day = c.get(Calendar.DAY_OF_WEEK);
            holder.mDayText.setText("(" + Utils.dayToString(day) + ")");
            if (day == 7 || day == 1) {
                holder.mDayText.setTextColor(Color.RED); //토, 일 빨간색
            } else {
                holder.mDayText.setTextColor(
                        ContextCompat.getColor(context, R.color.colorAccent)); //평일 분홍색
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size()-1;//DataSet의 크기만큼 센다. 카드뷰에서 갯수가 안맞은 이유 : 데이터 셋의 크기
    }

    public void refresh(List<Weather> items) { //새로고침
        mDataSet.clear(); //DataSet을 초기화하고
        mDataSet.addAll(items); //DataSet을 다 추가
        notifyDataSetChanged();
    }
}

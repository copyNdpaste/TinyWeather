package com.example.tinyweather;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

/**
 * Created by MH on 2017-08-26.
 */

public class ZonePickDialog {

    public static OnListener onListener;

    private static ArrayList<String> mSupportedZoneName;

    public static void show(Context context, OnListener listener) {
        final SupportedZone zone = SupportedZone.getInstance();
        onListener = listener;

        mSupportedZoneName = new ArrayList<>(zone.getSupportedZoneList());

        new MaterialDialog.Builder(context)
                .title("지역을 선택하세요.")
                .items(mSupportedZoneName)
                .itemsCallback(listCallback)
                .show();
    }

    static MaterialDialog.ListCallback listCallback = new MaterialDialog.ListCallback() { //서울의 구 리스트
        @Override
        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
            onListener.onClick(mSupportedZoneName.get(which)); // 클릭된 구를 선택
        }
    };

    public interface OnListener {
        void onClick(String name);
    }
}

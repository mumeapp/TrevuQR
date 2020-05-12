package com.remu.POJO;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.Range;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.remu.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PrayerTime extends AsyncTask<Void, Void, Void> {

    private String TAG;
    private Context context;
    private String todayUrl, tomorrowURL;
    private ArrayList<HashMap<String, String>> prayerList;

    private ShimmerFrameLayout jamSholatShimmerLoad;
    private ArrayList<TextView> textViews;
    private ArrayList<LinearLayout> linearLayouts = new ArrayList<>();
    private ProgressDialog progressDialog;

    public PrayerTime(Context context, String TAG, String latitude, String longitude, ShimmerFrameLayout jamSholatShimmerLoad, ArrayList<TextView> textViews) {
        this.context = context;
        this.TAG = TAG;
        this.jamSholatShimmerLoad = jamSholatShimmerLoad;
        this.textViews = textViews;
        setTodayURL(latitude, longitude);
        setTomorrowURL(latitude, longitude);
    }

    public PrayerTime(Context context, String TAG, String latitude, String longitude, ArrayList<TextView> textViews, ArrayList<LinearLayout> linearLayouts) {
        this.context = context;
        this.TAG = TAG;
        this.textViews = textViews;
        this.linearLayouts = linearLayouts;
        setTodayURL(latitude, longitude);
        setTomorrowURL(latitude, longitude);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        if (TAG.equals("MosqueActivity")) {
            progressDialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpHandler httpHandler = new HttpHandler();

        String todayJsonStr = httpHandler.makeServiceCall(todayUrl);
        String tomorrowJsonStr = httpHandler.makeServiceCall(tomorrowURL);

        Log.d(TAG, "Response from url: " + todayJsonStr);
        Log.d(TAG, "Response from url: " + tomorrowJsonStr);

        if (todayJsonStr != null) {
            try {
                JSONObject todayTimes = new JSONObject(todayJsonStr).getJSONObject("results").getJSONArray("datetime")
                        .getJSONObject(0).getJSONObject("times");

                if (isPassed(todayTimes.getString("Isha"))) {
                    JSONObject tomorrowTimes = new JSONObject(tomorrowJsonStr).getJSONObject("results").getJSONArray("datetime")
                            .getJSONObject(0).getJSONObject("times");
                    parseJson(tomorrowTimes);
                } else {
                    parseJson(todayTimes);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        switch (TAG) {
            case "MosqueActivity":
                setNextPrayerTime(textViews.get(0));
                setNextPrayerName(textViews.get(1));
                setPrayerList(textViews.get(2), textViews.get(3), textViews.get(4), textViews.get(5), textViews.get(6));
                highlightNextPrayerTime(linearLayouts.get(getIndexNextPrayerTime()), textViews.get(getIndexNextPrayerTime() + 2));
                break;
            case "HomeFragment":
                new Handler().postDelayed(() -> {
                    setNextPrayerTime(textViews.get(0));
                    jamSholatShimmerLoad.stopShimmer();
                    jamSholatShimmerLoad.setVisibility(View.GONE);
                }, 300);
                break;
        }
    }

    private void setPrayerList(TextView fajr, TextView dhuhr, TextView asr, TextView maghrib, TextView isha) {
        fajr.setText(prayerList.get(0).get("time"));
        dhuhr.setText(prayerList.get(1).get("time"));
        asr.setText(prayerList.get(2).get("time"));
        maghrib.setText(prayerList.get(3).get("time"));
        isha.setText(prayerList.get(4).get("time"));
    }

    private void setNextPrayerName(TextView name) {
        name.setText(prayerList.get(getIndexNextPrayerTime()).get("name"));
    }

    private void setNextPrayerTime(TextView time) {
        time.setText(prayerList.get(getIndexNextPrayerTime()).get("time"));
    }

    private int getIndexNextPrayerTime() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String currentTime = df.format(c.getTime());

        int index = 0;

        for (int i = 0; i < prayerList.size() - 1; i++) {
            int rangeBottom = (Integer.parseInt(prayerList.get(i).get("time").split(":")[0]) * 60) + Integer.parseInt(prayerList.get(i).get("time").split(":")[1]);
            int rangeTop = (Integer.parseInt(prayerList.get(i + 1).get("time").split(":")[0]) * 60) + Integer.parseInt(prayerList.get(i + 1).get("time").split(":")[1]);
            int currentTimeMnt = (Integer.parseInt(currentTime.split(":")[0]) * 60) + Integer.parseInt(currentTime.split(":")[1]);

            try {
                Range<Integer> myRange = new Range<>(rangeBottom, rangeTop);
                if (myRange.contains(currentTimeMnt)) {
                    index = i + 1;
                    break;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        return index;
    }

    private void highlightNextPrayerTime(LinearLayout nextPrayerTimeLayout, TextView nextPrayerTime) {
        int[] colors = {Color.parseColor("#7EF077"), Color.parseColor("#007EF077")};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        nextPrayerTimeLayout.setBackground(gradientDrawable);

        Typeface typeface = ResourcesCompat.getFont(context, R.font.geomanistblack);
        nextPrayerTime.setTypeface(typeface);
    }


    private boolean isPassed(String isyaTimes) {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String currentTime = df.format(c.getTime());

        if (Integer.parseInt(currentTime.split(":")[0]) > Integer.parseInt(isyaTimes.split(":")[0])) {
            return true;
        } else if (Integer.parseInt(currentTime.split(":")[0]) == Integer.parseInt(isyaTimes.split(":")[0])) {
            return Integer.parseInt(currentTime.split(":")[1]) >= Integer.parseInt(isyaTimes.split(":")[1]);
        } else {
            return false;
        }
    }

    private void parseJson(JSONObject times) throws JSONException {
        prayerList = new ArrayList<HashMap<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("name", "Fajr");
                put("time", times.getString("Fajr"));
            }});
            add(new HashMap<String, String>() {{
                put("name", "Dhuhr");
                put("time", times.getString("Dhuhr"));
            }});
            add(new HashMap<String, String>() {{
                put("name", "Asr");
                put("time", times.getString("Asr"));
            }});
            add(new HashMap<String, String>() {{
                put("name", "Maghrib");
                put("time", times.getString("Maghrib"));
            }});
            add(new HashMap<String, String>() {{
                put("name", "Isha");
                put("time", times.getString("Isha"));
            }});
        }};
    }

    private void setTodayURL(String latitude, String longitude) {
        todayUrl = "https://api.pray.zone/v2/times/today.json?latitude=" + latitude + "&longitude=" + longitude + "&elevation=666";
    }

    private void setTomorrowURL(String latitude, String longitude) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String nextDate = df.format(c.getTime());

        tomorrowURL = "https://api.pray.zone/v2/times/day.json?latitude=" + latitude + "&longitude=" + longitude + "&elevation=666&date=" + nextDate;
    }

}

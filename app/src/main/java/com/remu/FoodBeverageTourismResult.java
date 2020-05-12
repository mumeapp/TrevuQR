package com.remu;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.model.LatLng;
import com.remu.POJO.HttpHandler;
import com.remu.POJO.MyComparator;
import com.remu.POJO.PlaceModel;
import com.remu.POJO.Weighting;
import com.remu.adapter.FoodBeveragesTourismResultAdapter;
import com.saber.chentianslideback.SlideBackActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoodBeverageTourismResult extends SlideBackActivity {

    private static final String TAG = "FbBnTourResult";

    private ShimmerFrameLayout resultShimmerLoad;
    private RecyclerView listCategoryResult;

    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent content = getIntent();
        changeThemeBySender(Objects.requireNonNull(content.getStringExtra("sender"), "White"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodbeveragetaourism_result);

        latitude = Double.parseDouble(getApplication().getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", null));
        longitude = Double.parseDouble(getApplication().getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", null));

        initializeUI(content);
        Animatoo.animateSlideLeft(this);

        setSlideBackDirection(SlideBackActivity.LEFT);
    }

    @Override
    protected void slideBackSuccess() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        Animatoo.animateSlideRight(this);
    }

    @Override
    protected void onPause() {
        resultShimmerLoad.stopShimmer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resultShimmerLoad.startShimmer();
    }

    private void changeThemeBySender(String sender) {
        switch (sender) {
            case "HalalFood":
                setTheme(R.style.YellowTheme);
                break;
            case "HalalBeverages":
                setTheme(R.style.DarkRedTheme);
                break;
            case "Tourism":
                setTheme(R.style.BlueTheme);
                break;
            case "white":
                break;
        }
    }

    private void initializeUI(Intent intent) {
        resultShimmerLoad = findViewById(R.id.shimmer_load_result);
        TextView categoryName = findViewById(R.id.category_name);
        categoryName.setText(capitalize(intent.getStringExtra("name")));

        CardView categoryBar = findViewById(R.id.category_name_bar);
        switch (Objects.requireNonNull(intent.getStringExtra("sender"), "White")) {
            case "HalalFood":
                categoryBar.setCardBackgroundColor(getResources().getColor(R.color.trevuYellow));
                break;
            case "HalalBeverages":
                categoryBar.setCardBackgroundColor(getResources().getColor(R.color.trevuDarkRed));
                break;
            case "Tourism":
                categoryBar.setCardBackgroundColor(getResources().getColor(R.color.trevuBlue));
                break;
            case "white":
                break;
        }

        listCategoryResult = findViewById(R.id.listCategoyResult);
        new GetCategoryResult(intent).execute();
    }

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);

        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    private class GetCategoryResult extends AsyncTask<Void, Void, Void> {

        private String url;
        private ArrayList<PlaceModel> places;
        private String senderType;

        GetCategoryResult(Intent intent) {
            places = new ArrayList<>();
            senderType = intent.getStringExtra("sender");
            setURL(senderType, intent.getStringExtra("category"));
        }

        private void setURL(String sender, String category) {
            String keyword = "";
            switch (sender) {
                case "HalalFood":
                    keyword = "food%20" + category;
                    break;
                case "HalalBeverages":
                    keyword = "beverages%20" + category;
                    break;
                case "Tourism":
                    keyword = category;
                    break;
            }
            url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude +
                    "6&rankby=distance&keyword=" + keyword + "&key=" + getString(R.string.API_KEY);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler httpHandler = new HttpHandler();

            String jsonStr = httpHandler.makeServiceCall(url);

            Log.d(TAG, url);
            Log.d(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray results = new JSONObject(jsonStr).getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject row = results.getJSONObject(i);

                        if (row.isNull("photos")) {
                            places.add(new PlaceModel(
                                    row.getString("place_id"),
                                    row.getString("name"),
                                    row.getString("vicinity"),
                                    row.getDouble("rating"),
                                    new LatLng(row.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                            row.getJSONObject("geometry").getJSONObject("location").getDouble("lng"))
                            ));
                        } else {
                            places.add(new PlaceModel(
                                    row.getString("place_id"),
                                    row.getString("name"),
                                    row.getString("vicinity"),
                                    row.getDouble("rating"),
                                    new LatLng(row.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                            row.getJSONObject("geometry").getJSONObject("location").getDouble("lng")),
                                    row.getJSONArray("photos").getJSONObject(0).getString("photo_reference")
                            ));
                        }
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            doWeighting();
            return null;
        }

        private void doWeighting() {
            Weighting weighting = new Weighting();
            ArrayList<Double> weight;

            weight = weighting.doWeighting(latitude, longitude, places);

            for (int i = 0; i < places.size(); i++) {
                places.get(i).setPlaceWeight(weight.get(i));
            }
            Collections.sort(places, new MyComparator());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            listCategoryResult.setLayoutManager(new LinearLayoutManager(FoodBeverageTourismResult.this, LinearLayoutManager.VERTICAL, false));
            FoodBeveragesTourismResultAdapter foodBeveragesTourismResultAdapter = new FoodBeveragesTourismResultAdapter(getApplication(), FoodBeverageTourismResult.this, senderType, places);
            listCategoryResult.setAdapter(foodBeveragesTourismResultAdapter);
            resultShimmerLoad.stopShimmer();
            resultShimmerLoad.setVisibility(View.GONE);
        }

    }

}

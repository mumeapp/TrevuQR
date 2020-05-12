package com.remu;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.remu.POJO.HttpHandler;
import com.remu.POJO.MyComparator;
import com.remu.POJO.PlaceModel;
import com.remu.POJO.Weighting;
import com.remu.adapter.TourismAdapter;
import com.saber.chentianslideback.SlideBackActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TourismActivity extends SlideBackActivity {

    private static final String TAG = "TourismActivity";

    private ShimmerFrameLayout tourismShimmerLoad;
    private RecyclerView listCategory, rvTour;
    private TourismAdapter tourismAdapter;
    private double latitude, longitude;
    private String userId;
    private ArrayList<PlaceModel> places;

    private EditText manualCategory;

    private NestedScrollView tourScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourism);

        latitude = Double.parseDouble(getApplication().getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", null));
        longitude = Double.parseDouble(getApplication().getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", null));

        initializeUI();
        Animatoo.animateSlideLeft(this);

        Runnable getGoogleJSON = this::getGoogleJson;

        generateListCategory();

        new GetTourPlace().execute(getGoogleJSON);

        tourScrollView = findViewById(R.id.tour_scroll);
        tourScrollView.post(() -> tourScrollView.scrollTo(0, 0));

        manualCategory.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!manualCategory.getText().toString().equals("")) {
                    Intent intent = new Intent(TourismActivity.this, FoodBeverageTourismResult.class);
                    intent.putExtra("sender", "Tourism");
                    intent.putExtra("category", changeSpace(manualCategory.getText().toString()));
                    intent.putExtra("name", manualCategory.getText().toString());
                    startActivity(intent);
                    manualCategory.setText("");
                    return true;
                } else {
                    manualCategory.setError("Please put what category you want.");
                }
            }
            return false;
        });

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
        tourismShimmerLoad.stopShimmer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manualCategory.setText("");
        tourismShimmerLoad.startShimmer();
    }

    private void parseJSON(String jsonStr, ArrayList<String> placeIds) {
        try {
            JSONArray results = new JSONObject(jsonStr).getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject row = results.getJSONObject(i);

                if (row.isNull("photos")) {
                    if (!placeIds.contains(row.getString("place_id"))) {
                        places.add(new PlaceModel(
                                row.getString("place_id"),
                                row.getString("name"),
                                row.getString("vicinity"),
                                row.getDouble("rating"),
                                new LatLng(row.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                        row.getJSONObject("geometry").getJSONObject("location").getDouble("lng"))
                        ));
                        placeIds.add(row.getString("place_id"));
                    }
                } else {
                    if (!placeIds.contains(row.getString("place_id"))) {
                        places.add(new PlaceModel(
                                row.getString("place_id"),
                                row.getString("name"),
                                row.getString("vicinity"),
                                row.getDouble("rating"),
                                new LatLng(row.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                        row.getJSONObject("geometry").getJSONObject("location").getDouble("lng")),
                                row.getJSONArray("photos").getJSONObject(0).getString("photo_reference")
                        ));
                        placeIds.add(row.getString("place_id"));
                    }
                }
            }
        } catch (final JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
        }
    }

    private void getGoogleJson() {
        HttpHandler httpHandler = new HttpHandler();

        String url1 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude +
                "6&rankby=distance&keyword=museum&key=" + getString(R.string.API_KEY);
        String url2 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude +
                "6&rankby=distance&keyword=zoo&key=" + getString(R.string.API_KEY);
        String url3 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude +
                "6&rankby=distance&keyword=theme%20park&key=" + getString(R.string.API_KEY);
        String url4 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude +
                "6&rankby=distance&keyword=waterfall&key=" + getString(R.string.API_KEY);

        ArrayList<String> arrayListJSON = new ArrayList<String>() {{
            add(httpHandler.makeServiceCall(url1));
            add(httpHandler.makeServiceCall(url2));
            add(httpHandler.makeServiceCall(url3));
            add(httpHandler.makeServiceCall(url4));
        }};

        ArrayList<String> placeIds = new ArrayList<>();

        for (String jsonStr : arrayListJSON) {
            Log.d(TAG, url1);
            Log.d(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                parseJSON(jsonStr, placeIds);
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
        }
        doWeighting();
        runOnUiThread(() -> {
            rvTour.setLayoutManager(new GridLayoutManager(TourismActivity.this, 2));
            tourismAdapter = new TourismAdapter(getApplication(), TourismActivity.this, places, new LatLng(latitude, longitude));
            rvTour.setAdapter(tourismAdapter);
            tourismShimmerLoad.stopShimmer();
            tourismShimmerLoad.setVisibility(View.GONE);
        });
    }


    private void doWeighting() {
        Weighting weighting = new Weighting();
        ArrayList<Double> weight;
        weight = weighting.doWeighting(latitude, longitude, places);

        for (int i = 0; i < places.size(); i++) {
            places.get(i).setPlaceWeight(weight.get(i));
        }

        Collections.sort(places, new MyComparator());
        if (places.size() > 20) {
            places = new ArrayList<>(places.subList(0, 20));
        }
    }

    private void initializeUI() {
        tourismShimmerLoad = findViewById(R.id.shimmer_load_recommended_tourism);
        manualCategory = findViewById(R.id.et_manual_tourism_category);
        listCategory = findViewById(R.id.listTourismCategories);
        rvTour = findViewById(R.id.listRecommendedTourism);
        userId = FirebaseAuth.getInstance().getUid();
    }

    private String changeSpace(String input) {
        String[] strings = input.split(" ");
        StringBuilder returnVal = new StringBuilder();

        for (int i = 0; i < strings.length; i++) {
            if (i + 1 != strings.length) {
                returnVal.append(strings[i]).append("%20");
            } else {
                returnVal.append(strings[i]);
            }
        }

        return returnVal.toString();
    }

    private void generateListCategory() {
        ArrayList<HashMap<String, Object>> categoryDataSet = new ArrayList<HashMap<String, Object>>() {{
            add(new HashMap<String, Object>() {{
                put("category_name", "Amusement park");
                put("keyword", "theme%20park");
                put("category_image", R.drawable.tourismcategory_amusementpark);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Aquarium");
                put("keyword", "aquarium%20attraction");
                put("category_image", R.drawable.tourismcategory_aquarium);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Beach");
                put("keyword", "beach");
                put("category_image", R.drawable.tourismcategory_beach);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Museum");
                put("keyword", "museum");
                put("category_image", R.drawable.tourismcategory_museum);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Waterfall");
                put("keyword", "waterfall");
                put("category_image", R.drawable.tourismcategory_waterfall);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Zoo");
                put("keyword", "zoo");
                put("category_image", R.drawable.tourismcategory_zoo);
            }});
        }};

        listCategory.setLayoutManager(new LinearLayoutManager(TourismActivity.this, RecyclerView.HORIZONTAL, false));
        RecyclerView.Adapter<CatergoryViewHolder> categoryAdapter = new RecyclerView.Adapter<CatergoryViewHolder>() {
            @NonNull
            @Override
            public CatergoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_category_tourfoodbeverages, parent, false);
                return new CatergoryViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull CatergoryViewHolder holder, int position) {
                holder.categoryImage.setImageDrawable(getDrawable((int) categoryDataSet.get(position).get("category_image")));
                holder.categoryName.setText((String) categoryDataSet.get(position).get("category_name"));

                holder.categoryCard.setOnClickListener((v) -> {
                    Intent intent = new Intent(TourismActivity.this, FoodBeverageTourismResult.class);
                    intent.putExtra("sender", "Tourism");
                    intent.putExtra("category", (String) categoryDataSet.get(position).get("keyword"));
                    intent.putExtra("name", (String) categoryDataSet.get(position).get("category_name"));
                    startActivity(intent);
                });
            }

            @Override
            public int getItemCount() {
                return categoryDataSet.size();
            }
        };
        listCategory.setAdapter(categoryAdapter);
    }

    private class GetTourPlace extends AsyncTask<Runnable, Void, Void> {

        GetTourPlace() {
            places = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Runnable... runnables) {
            for (Runnable task : runnables) {
                task.run();
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            tourismAdapter = new TourismAdapter(getApplication(), TourismActivity.this, places, new LatLng(latitude, longitude));
            rvTour.setAdapter(tourismAdapter);
        }
    }

    class CatergoryViewHolder extends RecyclerView.ViewHolder {

        ImageView categoryImage;
        TextView categoryName;
        CardView categoryCard;

        CatergoryViewHolder(View itemView) {
            super(itemView);

            categoryImage = itemView.findViewById(R.id.food_category_image);
            categoryName = itemView.findViewById(R.id.food_category_name);
            categoryCard = itemView.findViewById(R.id.food_category_card);
        }

    }

}

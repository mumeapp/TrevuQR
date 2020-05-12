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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.remu.POJO.HttpHandler;
import com.remu.POJO.MyComparator;
import com.remu.POJO.PlaceModel;
import com.remu.POJO.Weighting;
import com.remu.adapter.FoodBeveragesTourismResultAdapter;
import com.remu.adapter.MidnightFoodAdapter;
import com.saber.chentianslideback.SlideBackActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class HalalFoodActivity extends SlideBackActivity {

    private static final String TAG = "HalalFoodActivity";

    private double latitude, longitude;

    private LinearLayout layoutMidnight;
    private ShimmerFrameLayout shimmerLoadMidnight, shimmerLoadRecommended;
    private RecyclerView listCategory, listOpenAtNight, listRecommendedFood;

    private EditText manualCategory;
    private ArrayList<PlaceModel> places;
    private FirebaseDatabase firebaseDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halal_food);

        latitude = Double.parseDouble(getApplication().getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", null));
        longitude = Double.parseDouble(getApplication().getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", null));
        userId = FirebaseAuth.getInstance().getUid();

        initializeUI();
        Animatoo.animateSlideLeft(this);

        generateListCategory();

        Runnable getGoogleJSON = this::getGoogleJson;

        generateListOpenNight();
        new GetRecommended().execute(getGoogleJSON);

        manualCategory.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!manualCategory.getText().toString().equals("")) {
                    Intent intent = new Intent(HalalFoodActivity.this, FoodBeverageTourismResult.class);
                    intent.putExtra("sender", "HalalFood");
                    intent.putExtra("category", changeSpace(manualCategory.getText().toString()));
                    intent.putExtra("name", manualCategory.getText().toString());
                    startActivity(intent);
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
        shimmerLoadMidnight.stopShimmer();
        shimmerLoadRecommended.stopShimmer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manualCategory.setText("");
        shimmerLoadMidnight.startShimmer();
        shimmerLoadRecommended.startShimmer();
    }

    private void initializeUI() {
        shimmerLoadMidnight = findViewById(R.id.shimmer_load_midnight_food);
        shimmerLoadRecommended = findViewById(R.id.shimmer_load_recommended_food);
        layoutMidnight = findViewById(R.id.ly_food_midnight);
        listCategory = findViewById(R.id.listFoodCategory);
        listOpenAtNight = findViewById(R.id.listFoodOpenAtNight);
        listRecommendedFood = findViewById(R.id.listRecommendedFood);
        manualCategory = findViewById(R.id.et_manual_food_category);
        firebaseDatabase = FirebaseDatabase.getInstance();
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
                put("category_name", "Beef");
                put("keyword", "beef");
                put("category_image", R.drawable.foodcategory_beef);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Burgers");
                put("keyword", "burgers");
                put("category_image", R.drawable.foodcategory_burgers);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Chicken Delight");
                put("keyword", "chicken");
                put("category_image", R.drawable.foodcategory_chicken);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Chinese");
                put("keyword", "chinese");
                put("category_image", R.drawable.foodcategory_chinese);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Duck");
                put("keyword", "duck");
                put("category_image", R.drawable.foodcategory_duck);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Fried Chicken");
                put("keyword", "fried%20chicken");
                put("category_image", R.drawable.foodcategory_friedchicken);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Meatballs");
                put("keyword", "meatballs");
                put("category_image", R.drawable.foodcategory_meatballs);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Pizza and Pasta");
                put("keyword", "pizza%20pasta");
                put("category_image", R.drawable.foodcategory_pizzapasta);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Ramen");
                put("keyword", "ramen");
                put("category_image", R.drawable.foodcategory_ramen);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Seafood");
                put("keyword", "seafood");
                put("category_image", R.drawable.foodcategory_seafood);
            }});
            add(new HashMap<String, Object>() {{
                put("category_name", "Sushi");
                put("keyword", "sushi");
                put("category_image", R.drawable.foodcategory_sushi);
            }});
        }};

        listCategory.setLayoutManager(new LinearLayoutManager(HalalFoodActivity.this, RecyclerView.HORIZONTAL, false));
        RecyclerView.Adapter<CategoryViewHolder> categoryAdapter = new RecyclerView.Adapter<CategoryViewHolder>() {
            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_category_tourfoodbeverages, parent, false);
                return new CategoryViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
                holder.categoryImage.setImageDrawable(getDrawable((int) categoryDataSet.get(position).get("category_image")));
                holder.categoryName.setText((String) categoryDataSet.get(position).get("category_name"));

                holder.categoryCard.setOnClickListener((v) -> {
                    Intent intent = new Intent(HalalFoodActivity.this, FoodBeverageTourismResult.class);
                    intent.putExtra("sender", "HalalFood");
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

    private void getGoogleJson() {
        HttpHandler httpHandler = new HttpHandler();

        String url1 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude +
                "6&rankby=distance&keyword=chicken&key=" + getString(R.string.API_KEY);
        String url2 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude +
                "6&rankby=distance&keyword=seafood&key="+ getString(R.string.API_KEY);
        String url3 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude +
                "6&rankby=distance&keyword=beef&key="+ getString(R.string.API_KEY);
        String url4 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude +
                "6&rankby=distance&keyword=duck&key="+ getString(R.string.API_KEY);

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
            listRecommendedFood.setLayoutManager(new LinearLayoutManager(HalalFoodActivity.this, LinearLayoutManager.VERTICAL, false));
            FoodBeveragesTourismResultAdapter recommendedAdapter = new FoodBeveragesTourismResultAdapter(getApplication(), HalalFoodActivity.this, "HalalFood", places);
            listRecommendedFood.setAdapter(recommendedAdapter);
            shimmerLoadRecommended.stopShimmer();
            shimmerLoadRecommended.setVisibility(View.GONE);
        });
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

    private void generateListOpenNight() {
        int currentHour = Integer.parseInt(new SimpleDateFormat("HH").format(Calendar.getInstance().getTime()));

        if (currentHour < 7 || currentHour > 21) {
            layoutMidnight.setVisibility(View.VISIBLE);
            new GetMidnight().execute();
        } else {
            layoutMidnight.setVisibility(View.GONE);
        }
    }

    private class GetRecommended extends AsyncTask<Runnable, Void, Void> {

        GetRecommended() {
            places = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class GetMidnight extends AsyncTask<Void, Void, Void> {

        private ArrayList<PlaceModel> midnightPlaces;

        GetMidnight() {
            midnightPlaces = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler httpHandler = new HttpHandler();

            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude +
                    "&radius=5000&keyword=muslim%20food&opennow&key=" + getString(R.string.API_KEY);

            String jsonStr = httpHandler.makeServiceCall(url);

            Log.d(TAG, url);
            Log.d(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray results = new JSONObject(jsonStr).getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject row = results.getJSONObject(i);

                        if (row.isNull("photos")) {
                            midnightPlaces.add(new PlaceModel(
                                    row.getString("place_id"),
                                    row.getString("name"),
                                    row.getString("vicinity"),
                                    row.getDouble("rating"),
                                    new LatLng(row.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                            row.getJSONObject("geometry").getJSONObject("location").getDouble("lng"))
                            ));
                        } else {
                            midnightPlaces.add(new PlaceModel(
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            listOpenAtNight.setLayoutManager(new LinearLayoutManager(HalalFoodActivity.this, LinearLayoutManager.HORIZONTAL, false));
            MidnightFoodAdapter openAtNightAdapter = new MidnightFoodAdapter(getApplication(), HalalFoodActivity.this, midnightPlaces);
            listOpenAtNight.setAdapter(openAtNightAdapter);
            shimmerLoadMidnight.stopShimmer();
            shimmerLoadMidnight.setVisibility(View.GONE);
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView categoryImage;
        TextView categoryName;
        CardView categoryCard;

        CategoryViewHolder(View itemView) {
            super(itemView);

            categoryImage = itemView.findViewById(R.id.food_category_image);
            categoryName = itemView.findViewById(R.id.food_category_name);
            categoryCard = itemView.findViewById(R.id.food_category_card);
        }

    }

}

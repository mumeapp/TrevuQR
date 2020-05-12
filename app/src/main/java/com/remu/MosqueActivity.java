package com.remu;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alespero.expandablecardview.ExpandableCardView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.remu.POJO.HttpHandler;
import com.remu.POJO.PlaceModel;
import com.remu.POJO.PrayerTime;
import com.remu.adapter.MosqueAdapter;
import com.saber.chentianslideback.SlideBackActivity;
import com.takusemba.multisnaprecyclerview.MultiSnapHelper;
import com.takusemba.multisnaprecyclerview.SnapGravity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class MosqueActivity extends SlideBackActivity implements OnMapReadyCallback {

    private static final String TAG = "MosqueActivity";

    private String latitude, longitude;

    private ExpandableCardView jamSolat;
    private RelativeLayout someInformation;

    private ShimmerFrameLayout mosqueShimmerLoad;
    private LinearLayoutManager layoutManager;
    private RecyclerView listMasjid;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<PlaceModel> mDataSet;

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);


    GeoDataClient mGeoDataClient;
    PlaceDetectionClient mPlaceDetectionClient;
    private GoogleMap mMap;
    private LatLng latLng;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosque);

        latitude = Objects.requireNonNull(getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", null));
        longitude = Objects.requireNonNull(getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", null));

        Log.e(TAG, latitude);
        Log.e(TAG, longitude);

        //initialize ui
        initializeUI();
        Animatoo.animateSlideLeft(this);

        new GetDataMosque().execute();

        //set title for expandable card
        jamSolat.setOnExpandedListener((v, isExpanded) -> {
            if (isExpanded) {
                jamSolat.setTitle("Jadwal Sholat Hari Ini");
                someInformation.setVisibility(View.INVISIBLE);
            } else {
                jamSolat.setTitle("Jadwal Sholat Selanjutnya");
                someInformation.setVisibility(View.VISIBLE);
            }
        });

        //start the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGeoDataClient = Places.getGeoDataClient(this, null);

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        MultiSnapHelper multiSnapHelper = new MultiSnapHelper(SnapGravity.CENTER, 1, 100);
        multiSnapHelper.attachToRecyclerView(listMasjid);

        listMasjid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                setMarker("onScrollStateChanged");
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View centerView = multiSnapHelper.findSnapView(layoutManager);
                    int pos = layoutManager.getPosition(centerView);
                    Log.e("Snapped Item Position", "" + pos);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDataSet.get(pos).getPlaceLocation(), 17));
                }
            }
        });
        setSlideBackDirection(SlideBackActivity.LEFT);
    }

    @Override
    protected void slideBackSuccess() {
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        updateLocationUI();
        getDeviceLocation();

        int zoomlevel = 16;

        if (latLng != null) {
            uiSettings.setAllGesturesEnabled(true);
            uiSettings.setMapToolbarEnabled(false);
            uiSettings.setMyLocationButtonEnabled(true);
            View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 30, (int) getPixelFromDp(185) + 30);

            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomlevel));
        }

        mMap.setOnMapClickListener((view) -> {
            if (isClicked) {
                isClicked = false;
                ObjectAnimator animation = ObjectAnimator.ofFloat(listMasjid, "translationY", 0f);
                animation.setDuration(500);
                animation.start();
            } else {
                isClicked = true;
                ObjectAnimator animation = ObjectAnimator.ofFloat(listMasjid, "translationY", 500f);
                animation.setDuration(500);
                animation.start();
            }
        });
    }

    private void getLocationPermission() {
        Dexter.withActivity(MosqueActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mLocationPermissionGranted = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        mLocationPermissionGranted = false;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.toString());
        }
    }

    private void getDeviceLocation() {
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    mLastKnownLocation = task.getResult();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), 15));
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.");
                    Log.e(TAG, "Exception: %s", task.getException());
                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(mDefaultLocation, 15));
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.toString());
        }
    }

    @Override
    protected void onPause() {
        mosqueShimmerLoad.stopShimmer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mosqueShimmerLoad.startShimmer();
    }

    @Override
    public void finish() {
        super.finish();
        Animatoo.animateSlideRight(this);
    }

    private void initializeUI() {
        mosqueShimmerLoad = findViewById(R.id.shimmer_load_mosque);

        jamSolat = findViewById(R.id.jamSolat);
        CardView cardJamSolat = jamSolat.findViewById(R.id.card);
        cardJamSolat.setRadius(getPixelFromDp(10));
        cardJamSolat.setElevation(0);
        TextView captionCardSholat = cardJamSolat.findViewById(R.id.title);
        captionCardSholat.setTypeface(ResourcesCompat.getFont(this, R.font.geomanistregular));

        someInformation = findViewById(R.id.someInformation);
        listMasjid = findViewById(R.id.listMasjid);
        mDataSet = new ArrayList<>();
        latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        ArrayList<TextView> textViews = new ArrayList<TextView>() {{
            add(someInformation.findViewById(R.id.jamSolatSelanjutnya));
            add(someInformation.findViewById(R.id.solatSelanjutnya));
            add(jamSolat.findViewById(R.id.time_fajr));
            add(jamSolat.findViewById(R.id.time_dhuhr));
            add(jamSolat.findViewById(R.id.time_asr));
            add(jamSolat.findViewById(R.id.time_maghrib));
            add(jamSolat.findViewById(R.id.time_isha));
        }};
        ArrayList<LinearLayout> linearLayouts = new ArrayList<LinearLayout>() {{
            add(jamSolat.findViewById(R.id.layout_fajr));
            add(jamSolat.findViewById(R.id.layout_dhuhr));
            add(jamSolat.findViewById(R.id.layout_asr));
            add(jamSolat.findViewById(R.id.layout_maghrib));
            add(jamSolat.findViewById(R.id.layout_isha));
        }};
        new PrayerTime(this, TAG, latitude, longitude, textViews, linearLayouts).execute();
    }

    public float getPixelFromDp(float dp) {
        Resources resources = this.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    private void setMarker(String sender) {
        mMap.clear();

        switch (sender) {
            case "onScrollStateChanged":
                for (PlaceModel a : mDataSet) {
                    mMap.addMarker(new MarkerOptions()
                            .position(a.getPlaceLocation())
                            .title(a.getPlaceName())
                            .icon(bitmapDescriptorFromVector()));
                }
                break;
            case "onPostExecute":
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));

                for (PlaceModel a : mDataSet) {
                    builder.include(a.getPlaceLocation());
                    mMap.addMarker(new MarkerOptions()
                            .position(a.getPlaceLocation())
                            .title(a.getPlaceName())
                            .icon(bitmapDescriptorFromVector()));
                }

                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 210));
                break;
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector() {
        Drawable vectorDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_mosque_marker);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetDataMosque extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler httpHandler = new HttpHandler();

            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude +
                    "&rankby=distance&type=mosque&key=" + getString(R.string.API_KEY);

            String jsonStr = httpHandler.makeServiceCall(url);

            Log.d(TAG, url);
            Log.d(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray results = new JSONObject(jsonStr).getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject row = results.getJSONObject(i);

                        mDataSet.add(new PlaceModel(
                                row.getString("place_id"),
                                row.getString("name"),
                                row.getString("vicinity"),
                                row.getDouble("rating"),
                                new LatLng(row.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                        row.getJSONObject("geometry").getJSONObject("location").getDouble("lng"))
                        ));
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

            layoutManager = new LinearLayoutManager(MosqueActivity.this, LinearLayoutManager.HORIZONTAL, false);
            listMasjid.setLayoutManager(layoutManager);
            mAdapter = new MosqueAdapter(getApplication(), MosqueActivity.this, mDataSet, mMap);
            new Handler().postDelayed(() -> {
                    listMasjid.setAdapter(mAdapter);
                mosqueShimmerLoad.stopShimmer();
                mosqueShimmerLoad.setVisibility(View.GONE);
            }, 800);
            setMarker("onPostExecute");
        }
    }
}

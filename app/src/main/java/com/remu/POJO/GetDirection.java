package com.remu.POJO;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.remu.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GetDirection extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    private static final String TAG = "GetDirection";

    private Application application;
    private GoogleMap mMap;
    private String mosqueName;
    private LatLng mosqueLocation;

    public GetDirection(Application application, GoogleMap mMap, String mosqueName, LatLng mosqueLocation) {
        this.application = application;
        this.mMap = mMap;
        this.mosqueName = mosqueName;
        this.mosqueLocation = mosqueLocation;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
        HttpHandler httpHandler = new HttpHandler();

        String jsonStr = httpHandler.makeServiceCall(strings[0]);

        Log.d(TAG, "Response from url: " + jsonStr);

        List<List<HashMap<String, String>>> routes = null;

        if (jsonStr != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                DirectionsJSONParser directionsJSONParser = new DirectionsJSONParser();

                routes = directionsJSONParser.parse(jsonObject);
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }

        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
        super.onPostExecute(lists);

        ArrayList points;
        PolylineOptions lineOptions = null;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(Double.parseDouble(application.getSharedPreferences("location", MODE_PRIVATE).getString("Latitude", "-33.8523341")),
                Double.parseDouble(application.getSharedPreferences("location", MODE_PRIVATE).getString("Longitude", "151.2106085"))));
        builder.include(mosqueLocation);

        for (int i = 0; i < lists.size(); i++) {
            points = new ArrayList();
            lineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = lists.get(i);

            for (int j = 2; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

//                builder.include(position);
                points.add(position);
            }

            lineOptions.addAll(points);
            lineOptions.width(12);
            lineOptions.color(application.getResources().getColor(R.color.trevuGreen));
            lineOptions.geodesic(true);

        }

        // Drawing polyline in the Google Map for the i-th route
        mMap.clear();

        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,400));

        mMap.addMarker(new MarkerOptions()
                .position(mosqueLocation)
                .title(mosqueName)
                .icon(bitmapDescriptorFromVector(application.getApplicationContext())));
        if (lineOptions != null){
            mMap.addPolyline(lineOptions);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_mosque_marker);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}

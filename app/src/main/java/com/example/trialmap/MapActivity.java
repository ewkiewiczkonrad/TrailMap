package com.example.trialmap;

import com.example.trialmap.models.*;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.gson.Gson;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Use the LocationComponent to easily add a device location "puck" to a Mapbox map.
 */
public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {


    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private List<List<Point>> routeCoordinates= new ArrayList<>();
    private LocationComponent locationComponent;
    private boolean isInTrackingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

// This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_map);


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MapActivity.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);

                        //initRouteCoordinates();
                        parseGeoData();

// Create the LineString from the list of coordinates and then make a GeoJSON
// FeatureCollection so we can add the line to our map as a layer.
                        int index = 0;

                        for (List<Point> route: routeCoordinates
                             ) {
                            style.addSource(new GeoJsonSource("line-source" + Integer.toString(index),
                                    FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                                            LineString.fromLngLats(route)
                                    )})));


// The layer properties for our line. This is where we make the line dotted, set the
// color, etc.
                            style.addLayer(new LineLayer("linelayer"+ Integer.toString(index), "line-source"+ Integer.toString(index)).withProperties(
                                    PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                    PropertyFactory.lineWidth(5f),
                                    PropertyFactory.lineColor(Color.parseColor("#e55e5e"))

                            ));
                            index++;
                        }

                    }
                });
    }

    private void parseGeoData() {
        //plik json
        String jsonFileString = Utils.getJsonFromAssets(getApplicationContext(), "trails.geojson");
        //Log.i("data", jsonFileString);
        //parsowanie json
        Gson gson = new Gson();

        Type geoJsonType = new TypeToken<GeoJSONObject>() { }.getType();             //typ obiektu gdzie parsowac
        GeoJSONObject geoData = gson.fromJson(jsonFileString, geoJsonType);         // parsowanie do obiektu
        List<com.example.trialmap.models.Feature> features = geoData.getFeatures();
        for (com.example.trialmap.models.Feature feature: geoData.getFeatures()
             ) {
            Geometry geometry = feature.getGeometry();
            initRouteCoordinates(geometry.getCoordinates());
        }

    }




    private void initRouteCoordinates(List<List<Double>> data) {
// Create a list to store our line coordinates.

        List<Point> route = new ArrayList<Point>();
        for (List<Double> coordinates: data
        ) {

            route.add(Point.fromLngLat(coordinates.get(0), coordinates.get(1)));


        }
        routeCoordinates.add(route);

//        routeCoordinates.add(Point.fromLngLat(18.782845, 54.095981));
//        routeCoordinates.add(Point.fromLngLat(18.782061, 54.095745));
//        routeCoordinates.add(Point.fromLngLat(18.781809, 54.095824));
//        routeCoordinates.add(Point.fromLngLat(18.780849, 54.095295));
//        routeCoordinates.add(Point.fromLngLat(18.78064, 54.095087));
//        routeCoordinates.add(Point.fromLngLat(18.780473, 54.094575));
//        routeCoordinates.add(Point.fromLngLat(18.780313, 54.094059));
//        routeCoordinates.add(Point.fromLngLat(18.780361, 54.093804));
//        routeCoordinates.add(Point.fromLngLat(18.780549, 54.093527));
//        routeCoordinates.add(Point.fromLngLat(18.781064, 54.093171));
//        routeCoordinates.add(Point.fromLngLat(18.781573, 54.092816));
//        routeCoordinates.add(Point.fromLngLat(18.781562, 54.092712));
//        routeCoordinates.add(Point.fromLngLat(18.781621, 54.092659));
//        routeCoordinates.add(Point.fromLngLat(18.78152, 54.092545));
//        routeCoordinates.add(Point.fromLngLat(18.781353, 54.092083));
//        routeCoordinates.add(Point.fromLngLat(18.781187, 54.091624));
//        routeCoordinates.add(Point.fromLngLat(18.781005, 54.091501));
//        routeCoordinates.add(Point.fromLngLat(18.7802, 54.09169));
//        routeCoordinates.add(Point.fromLngLat(18.779368, 54.091888));
//        routeCoordinates.add(Point.fromLngLat(18.778537, 54.092089));
//        routeCoordinates.add(Point.fromLngLat(18.778167, 54.092187));
//        routeCoordinates.add(Point.fromLngLat(18.777469, 54.092262));
//        routeCoordinates.add(Point.fromLngLat(18.777292, 54.091872));
//        routeCoordinates.add(Point.fromLngLat(18.776992, 54.090856));
//        routeCoordinates.add(Point.fromLngLat(18.776783, 54.090403));
//        routeCoordinates.add(Point.fromLngLat(18.776574, 54.089943));
//        routeCoordinates.add(Point.fromLngLat(18.776268, 54.089314));
//        routeCoordinates.add(Point.fromLngLat(18.775957, 54.088685));
//        routeCoordinates.add(Point.fromLngLat(18.775645, 54.088056));
//        routeCoordinates.add(Point.fromLngLat(18.775329, 54.087426));
//        routeCoordinates.add(Point.fromLngLat(18.775066, 54.086841));
//        routeCoordinates.add(Point.fromLngLat(18.774798, 54.086259));
//        routeCoordinates.add(Point.fromLngLat(18.77468, 54.085784));
//        routeCoordinates.add(Point.fromLngLat(18.774562, 54.085309));
//        routeCoordinates.add(Point.fromLngLat(18.773822, 54.085422));
//        routeCoordinates.add(Point.fromLngLat(18.772813, 54.086146));
//        routeCoordinates.add(Point.fromLngLat(18.772094, 54.086479));
//        routeCoordinates.add(Point.fromLngLat(18.771585, 54.086602));
//        routeCoordinates.add(Point.fromLngLat(18.76968, 54.086863));
//        routeCoordinates.add(Point.fromLngLat(18.769455, 54.085661));
//        routeCoordinates.add(Point.fromLngLat(18.768881, 54.085752));
//        routeCoordinates.add(Point.fromLngLat(18.768345, 54.086026));
//        routeCoordinates.add(Point.fromLngLat(18.767679, 54.086489));
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);


            findViewById(R.id.back_to_camera_tracking_mode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isInTrackingMode) {
                        isInTrackingMode = true;
                        locationComponent.setCameraMode(CameraMode.TRACKING);
                        locationComponent.zoomWhileTracking(16f);
                    }
                }
            });

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
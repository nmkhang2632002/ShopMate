package com.example.shopmate.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopmate.R;
import com.example.shopmate.data.model.StoreLocation;
import com.example.shopmate.viewmodel.MapViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_LOCATION_SETTINGS = 2;
    
    private MapView mapView;
    private IMapController mapController;
    private boolean locationPermissionGranted = false;
    private MapViewModel mapViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private MyLocationNewOverlay myLocationOverlay;
    
    // UI components
    private FrameLayout loadingOverlay;
    private MaterialCardView storeInfoCard;
    private TextView storeNameTextView;
    private TextView storeAddressTextView;
    private MaterialButton directionsButton;
    private EditText searchEditText;
    private ImageButton clearSearchButton;
    
    // Store data
    private Map<Marker, StoreLocation> markerStoreMap = new HashMap<>();
    private StoreLocation selectedStore;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize OSMDroid configuration
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(requireActivity().getPackageName());

        // Initialize UI components
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        storeInfoCard = view.findViewById(R.id.storeInfoCard);
        storeNameTextView = view.findViewById(R.id.storeNameTextView);
        storeAddressTextView = view.findViewById(R.id.storeAddressTextView);
        directionsButton = view.findViewById(R.id.directionsButton);
        searchEditText = view.findViewById(R.id.searchEditText);
        clearSearchButton = view.findViewById(R.id.clearSearchButton);
        
        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        
        // Initialize map
        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        
        // Set initial map position (Vietnam)
        mapController = mapView.getController();
        mapController.setZoom(5.0);
        GeoPoint startPoint = new GeoPoint(10.762622, 106.660172); // Ho Chi Minh City
        mapController.setCenter(startPoint);

        // Check location permissions
        getLocationPermission();

        // Set up my location button
        FloatingActionButton fabMyLocation = view.findViewById(R.id.fabMyLocation);
        fabMyLocation.setOnClickListener(v -> getDeviceLocation());
        
        // Set up directions button
        directionsButton.setOnClickListener(v -> openDirectionsInExternalApp());
        
        // Set up search functionality
        setupSearch();
        
        // Initialize ViewModel
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        
        // Observe ViewModel data
        setupObservers();
        
        // Load store locations
        mapViewModel.loadStoreLocations();
    }
    
    private void setupSearch() {
        // Set up search edit text
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchLocation(searchEditText.getText().toString());
                return true;
            }
            return false;
        });
        
        // Set up clear button
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearSearchButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        
        clearSearchButton.setOnClickListener(v -> {
            searchEditText.setText("");
            clearSearchButton.setVisibility(View.GONE);
        });
    }
    
    private void searchLocation(String query) {
        if (query.isEmpty()) return;
        
        loadingOverlay.setVisibility(View.VISIBLE);
        
        // Use Geocoder to search for location
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(query, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                GeoPoint point = new GeoPoint(address.getLatitude(), address.getLongitude());
                
                // Move map to the searched location
                mapController.animateTo(point);
                mapController.setZoom(15.0);
                
                // Add a marker at the searched location
                Marker searchMarker = new Marker(mapView);
                searchMarker.setPosition(point);
                searchMarker.setTitle(address.getFeatureName() != null ? address.getFeatureName() : query);
                searchMarker.setSnippet(address.getAddressLine(0) != null ? address.getAddressLine(0) : "");
                searchMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                
                // Remove previous search markers
                List<Marker> markersToRemove = new ArrayList<>();
                for (Marker marker : markerStoreMap.keySet()) {
                    if (marker.getId() != null && marker.getId().equals("search_marker")) {
                        markersToRemove.add(marker);
                    }
                }
                
                for (Marker marker : markersToRemove) {
                    mapView.getOverlays().remove(marker);
                    markerStoreMap.remove(marker);
                }
                
                searchMarker.setId("search_marker");
                mapView.getOverlays().add(searchMarker);
                mapView.invalidate();
            } else {
                Toast.makeText(requireContext(), R.string.location_not_found, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error searching location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        
        loadingOverlay.setVisibility(View.GONE);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (myLocationOverlay != null) {
            myLocationOverlay.enableMyLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
        }
    }
    
    private void setupObservers() {
        mapViewModel.getStoreLocations().observe(getViewLifecycleOwner(), storeLocations -> {
            if (storeLocations != null && !storeLocations.isEmpty()) {
                addStoreLocations(storeLocations);
            } else {
                Toast.makeText(requireContext(), R.string.no_stores_found, Toast.LENGTH_SHORT).show();
            }
        });
        
        mapViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        mapViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            setupMyLocationOverlay();
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                setupMyLocationOverlay();
                getDeviceLocation();
            } else {
                Toast.makeText(requireContext(), R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    
    private void setupMyLocationOverlay() {
        if (mapView == null) return;
        
        // Add my location overlay with custom icon
        GpsMyLocationProvider provider = new GpsMyLocationProvider(requireContext());
        myLocationOverlay = new MyLocationNewOverlay(provider, mapView);
        myLocationOverlay.enableMyLocation();
        
        // Không thể sử dụng setPersonIcon với Drawable, bỏ dòng này
        // myLocationOverlay.setPersonIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_user_location));
        
        mapView.getOverlays().add(myLocationOverlay);
        
        // Enable built-in zoom controls
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
    }

    private void getDeviceLocation() {
        if (!locationPermissionGranted) {
            Toast.makeText(requireContext(), R.string.location_permission_required, Toast.LENGTH_LONG).show();
            return;
        }
        
        // Check if GPS is enabled
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is not enabled, prompt user to enable it
            Snackbar.make(mapView, R.string.enable_location, Snackbar.LENGTH_LONG)
                    .setAction(R.string.settings, v -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_LOCATION_SETTINGS);
                    })
                    .show();
            return;
        }
        
        try {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    // Move camera to current location
                                    GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                                    mapController.animateTo(currentLocation);
                                    mapController.setZoom(15.0);
                                } else {
                                    // If location is null, use myLocationOverlay to center map
                                    if (myLocationOverlay != null && myLocationOverlay.getMyLocation() != null) {
                                        mapController.animateTo(myLocationOverlay.getMyLocation());
                                        mapController.setZoom(15.0);
                                    }
                                }
                            }
                        });
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addStoreLocations(List<StoreLocation> storeLocations) {
        if (mapView == null) return;
        
        // Clear previous markers but keep my location overlay
        mapView.getOverlays().clear();
        if (myLocationOverlay != null) {
            mapView.getOverlays().add(myLocationOverlay);
        }
        markerStoreMap.clear();
        
        // If no stores, show message and return
        if (storeLocations.isEmpty()) {
            Toast.makeText(requireContext(), R.string.no_stores_found, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Add markers for each store
        for (StoreLocation store : storeLocations) {
            try {
                GeoPoint position = new GeoPoint(store.getLatitude(), store.getLongitude());
                Marker marker = new Marker(mapView);
                marker.setPosition(position);
                marker.setTitle(store.getStoreName());
                marker.setSnippet(store.getAddress());
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                
                // Set custom marker icon
                try {
                    marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_store_marker));
                } catch (Exception e) {
                    // Fallback to default marker if custom icon fails
                }
                
                // Set marker click listener
                marker.setOnMarkerClickListener((marker1, mapView) -> {
                    StoreLocation storeLocation = markerStoreMap.get(marker1);
                    if (storeLocation != null) {
                        selectedStore = storeLocation;
                        showStoreInfo(storeLocation);
                    }
                    return true;
                });
                
                markerStoreMap.put(marker, store);
                mapView.getOverlays().add(marker);
            } catch (Exception e) {
                // Skip this marker if there's an error
            }
        }
        
        // Zoom to show all markers
        if (!storeLocations.isEmpty()) {
            try {
                StoreLocation firstStore = storeLocations.get(0);
                GeoPoint position = new GeoPoint(firstStore.getLatitude(), firstStore.getLongitude());
                mapController.animateTo(position);
                mapController.setZoom(12.0);
            } catch (Exception e) {
                // Ignore zoom errors
            }
        }
        
        mapView.invalidate();
    }
    
    private void showStoreInfo(StoreLocation store) {
        storeNameTextView.setText(store.getStoreName());
        storeAddressTextView.setText(store.getAddress());
        storeInfoCard.setVisibility(View.VISIBLE);
    }
    
    private void openDirectionsInExternalApp() {
        if (selectedStore == null) {
            Toast.makeText(requireContext(), "Please select a store first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get current location if available
        Location currentLocation = null;
        if (locationPermissionGranted) {
            try {
                if (myLocationOverlay != null && myLocationOverlay.getMyLocation() != null) {
                    GeoPoint myLocation = myLocationOverlay.getMyLocation();
                    currentLocation = new Location("");
                    currentLocation.setLatitude(myLocation.getLatitude());
                    currentLocation.setLongitude(myLocation.getLongitude());
                } else if (ActivityCompat.checkSelfPermission(requireContext(), 
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Try to get last known location
                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            openDirectionsWithLocation(location);
                        } else {
                            // No location available, open directions without starting point
                            openDirectionsWithoutLocation();
                        }
                    }).addOnFailureListener(e -> {
                        // On failure, open directions without starting point
                        openDirectionsWithoutLocation();
                    });
                    return; // Return early as we're handling directions in the callback
                }
            } catch (Exception e) {
                // Fallback to directions without starting point
                currentLocation = null;
            }
        }
        
        if (currentLocation != null) {
            openDirectionsWithLocation(currentLocation);
        } else {
            openDirectionsWithoutLocation();
        }
    }
    
    private void openDirectionsWithLocation(Location currentLocation) {
        // Try to open in Google Maps first with current location as starting point
        try {
            // Format: "google.navigation:q=lat,lng&saddr=lat,lng" - Sử dụng google.maps thay vì google.navigation
            // để mở trang cho người dùng chọn phương tiện di chuyển
            Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1" + 
                    "&destination=" + selectedStore.getLatitude() + "," + selectedStore.getLongitude() +
                    "&origin=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() +
                    "&travelmode=driving");
            
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            
            if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
                return;
            }
        } catch (Exception e) {
            // If Google Maps intent fails, continue to fallback
        }
        
        // Fallback to browser with OpenStreetMap
        try {
            // Format: "https://www.openstreetmap.org/directions?from=lat,lng&to=lat,lng"
            Uri osmUri = Uri.parse("https://www.openstreetmap.org/directions?from=" + 
                    currentLocation.getLatitude() + "," + currentLocation.getLongitude() +
                    "&to=" + selectedStore.getLatitude() + "," + selectedStore.getLongitude());
            
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, osmUri);
            
            if (browserIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(browserIntent);
            } else {
                Toast.makeText(requireContext(), R.string.open_in_maps_error, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.open_in_maps_error, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void openDirectionsWithoutLocation() {
        // Try to open in Google Maps without starting point
        try {
            // Sử dụng URL web của Google Maps thay vì URI google.navigation
            Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1" + 
                    "&destination=" + selectedStore.getLatitude() + "," + selectedStore.getLongitude() +
                    "&travelmode=driving");
            
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            
            if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
                return;
            }
        } catch (Exception e) {
            // If Google Maps intent fails, continue to fallback
        }
        
        // Fallback to browser with OpenStreetMap
        try {
            Uri osmUri = Uri.parse("https://www.openstreetmap.org/?mlat=" + 
                    selectedStore.getLatitude() + "&mlon=" + selectedStore.getLongitude() + 
                    "&zoom=16");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, osmUri);
            
            if (browserIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(browserIntent);
            } else {
                Toast.makeText(requireContext(), R.string.open_in_maps_error, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.open_in_maps_error, Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_SETTINGS) {
            // Check if GPS is now enabled
            LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getDeviceLocation();
            }
        }
    }
} 
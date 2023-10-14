package frgp.utn.edu.ar.controllers.ui.fragments;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import frgp.utn.edu.ar.controllers.R;
import frgp.utn.edu.ar.controllers.ui.viewmodels.BuscarReporteViewModel;

public class BuscarReporteFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private BuscarReporteViewModel mViewModel;
    private ListView listaReportes;
    private GoogleMap mapaReportes;
    private Marker userMarker;
    private SearchView barraBusqueda;
    public static BuscarReporteFragment newInstance() {
        return new BuscarReporteFragment();
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        public void onMapReady(GoogleMap googleMap) {
            // Verifica si tiene permisos de ubicación
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Si no tiene permisos, se solicitan al usuario
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                // Si ya tiene permisos, accede a la ubicación
                FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
                locationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            List<Address> addresses;

                            try {
                                mapaReportes = googleMap;
                                addresses = geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1);
                                if (!addresses.isEmpty()) {
                                    userMarker = mapaReportes.addMarker(new MarkerOptions().position(currentLatLng).title(addresses.get(0).getAddressLine(0)));
                                    mapaReportes.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                                } else {
                                    userMarker = mapaReportes.addMarker(new MarkerOptions().position(currentLatLng).title("Sin título"));
                                    mapaReportes.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                                }
                            } catch (IOException e) {
                                Log.e("Error de mapa", e.toString());
                            }
                        }
                    }
                });
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buscar_reporte, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // BOTON VER REPORTE - PRUEBA
        Button bVerReporte = view.findViewById(R.id.btnVerDetalle);

        listaReportes = view.findViewById(R.id.listReportes);
        barraBusqueda = view.findViewById(R.id.busquedaReporte);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_reporte);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        bVerReporte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navegarDetalle();
            }
        });
    }

    public void navegarDetalle(){
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.detalle_reporte);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BuscarReporteViewModel.class);
        // TODO: Use the ViewModel
    }

}
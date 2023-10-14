package frgp.utn.edu.ar.controllers.ui.fragments;

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
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import frgp.utn.edu.ar.controllers.R;
import frgp.utn.edu.ar.controllers.data.model.Usuario;
import frgp.utn.edu.ar.controllers.ui.activities.VecinoActivity;
import frgp.utn.edu.ar.controllers.ui.dialogs.DenunciaReporteDialogFragment;
import frgp.utn.edu.ar.controllers.ui.dialogs.UserDetailDialogFragment;
import frgp.utn.edu.ar.controllers.ui.dialogs.ValorarReporteDialogFragment;
import frgp.utn.edu.ar.controllers.ui.viewmodels.DetalleReporteViewModel;

public class DetalleReporteFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap googlemaplocal;
    private Marker userMarker;
    private DetalleReporteViewModel mViewModel;
        public static DetalleReporteFragment newInstance() {
        return new DetalleReporteFragment();
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
                                googlemaplocal = googleMap;
                                addresses = geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1);
                                if (!addresses.isEmpty()) {
                                    userMarker = googlemaplocal.addMarker(new MarkerOptions().position(currentLatLng).title(addresses.get(0).getAddressLine(0)));
                                    googlemaplocal.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                                } else {
                                    userMarker = googlemaplocal.addMarker(new MarkerOptions().position(currentLatLng).title("Sin título"));
                                    googlemaplocal.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
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
        View view = inflater.inflate(R.layout.fragment_detalle_reporte, container, false);
        if(getActivity() instanceof VecinoActivity){
            ((VecinoActivity) getActivity()).botonmensaje.hide();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_reporte);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        Button bUsuario = view.findViewById(R.id.btnUsernameDetalle);
        bUsuario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // BOTON DETALLE DE USUARIO REPORTE
                Usuario user = new Usuario();
                user.setUsername("hvarela");
                user.setNombre("Hernán");
                user.setApellido("Varela");
                // Formatea la fecha de alta
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);  // Define el formato
                try {
                    Date fechaAlta = new Date(sdf.parse("04-09-2023").getTime());
                    user.setFecha_alta(fechaAlta);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Formatea la fecha de nacimiento
                try {
                    Date fechaNacimiento = new Date(sdf.parse("02-04-1990").getTime());
                    user.setFecha_nac(fechaNacimiento);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                user.setPuntuacion(123);
                UserDetailDialogFragment dialogFragment = UserDetailDialogFragment.newInstance(user);
                dialogFragment.show(getFragmentManager(), "user_detail_reporte");
            }
        });

        Button bSolicitarCierre = view.findViewById(R.id.btnCerrarReporte);
        bSolicitarCierre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // BOTON SOLICITAR CIERRE
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.solicitar_cierre);
            }
        });
        Button bValorar = view.findViewById(R.id.btnValorarReporte);
        bValorar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // BOTON VALORAR REPORTE
                ValorarReporteDialogFragment dialogFragment = new ValorarReporteDialogFragment();
                dialogFragment.show(getFragmentManager(), "layout_rating_reporte");
            }
        });

        Button bDenunciar = view.findViewById(R.id.btnDenunciarReporte);
        bDenunciar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // BOTON VALORAR REPORTE
                DenunciaReporteDialogFragment dialogFragment = new DenunciaReporteDialogFragment();
                dialogFragment.show(getFragmentManager(), "layout_denuciar_reporte");
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetalleReporteViewModel.class);
        // TODO: Use the ViewModel
    }

}
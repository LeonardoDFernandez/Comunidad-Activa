package frgp.utn.edu.ar.controllers.ui.fragments;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.time.Instant;
import java.util.Date;

import frgp.utn.edu.ar.controllers.data.model.EstadoReporte;
import frgp.utn.edu.ar.controllers.data.model.TipoReporte;
import frgp.utn.edu.ar.controllers.data.model.Usuario;
import frgp.utn.edu.ar.controllers.data.remote.DMAGuardarReporte;
import frgp.utn.edu.ar.controllers.data.remote.DMASpinnerTiposReporte;
import frgp.utn.edu.ar.controllers.ui.activities.VecinoActivity;
import frgp.utn.edu.ar.controllers.ui.viewmodels.NuevoReporteViewModel;
import frgp.utn.edu.ar.controllers.ui.adapters.SharedLocationViewModel;
import frgp.utn.edu.ar.controllers.R;
import frgp.utn.edu.ar.controllers.data.model.Reporte;

public class NuevoReporteFragment extends Fragment {
    private static final int CAMERA_PIC_REQUEST = 1337;
    private static final int LOCATION_PERMISSION_REQUEST = 123;
    private NuevoReporteViewModel mViewModel;
    private Bitmap imagenCapturada;
    private SharedLocationViewModel sharedLocationViewModel;
    private Spinner spinTipoReporte;
    private EditText titulo, descripcion;
    private Reporte nuevo;

    public static NuevoReporteFragment newInstance() {
        return new NuevoReporteFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nuevo_reporte, container, false);
        if(getActivity() instanceof VecinoActivity){
            ((VecinoActivity) getActivity()).botonmensaje.hide();
        }
        titulo = view.findViewById(R.id.edTituloReporte);
        descripcion = view.findViewById(R.id.edDescripcionReporte);
        spinTipoReporte = view.findViewById(R.id.spnTiposReporte);
        DMASpinnerTiposReporte dataActivityTiposReporte = new DMASpinnerTiposReporte(spinTipoReporte, getContext());
        dataActivityTiposReporte.execute();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // INICIALIZA EL sharedLocationViewModel, QUE PERMITE COMPARTIR LA UBICACION SELECCIONADA ENTRE FRAGMENTOS
        sharedLocationViewModel = new ViewModelProvider(requireActivity()).get(SharedLocationViewModel.class);

        // INICIAIZA BOTONES DEL FRAGMENTO
        Button bCamara = view.findViewById(R.id.btnCamara);
        Button bUbicacion = view.findViewById(R.id.btnUbicacion);
        Button bCrearReporte = view.findViewById(R.id.btnCrearReporte);

        // INICIALIZA NUEVO REPORTE
        nuevo = new Reporte();
        bCamara.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Verificar si tiene permiso de la cámara
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // SI TIENE PERMISOS, REDIRIGE A LA VISTA DE LA CÁMARA
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                } else {
                    // Si no tiene el permiso, se pide al usuario
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PIC_REQUEST);
                }
            }
        });
        bUbicacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Verificar si se tiene permiso de ubicación
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Navega al fragmento de ubicación
                    navigateToLocationFragment();
                } else {
                    // Si no tiene el permiso, se pide al usuario
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
                }
            }
        });
        bCrearReporte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    // Toma las coordenadas desde el ViewModel compartido con el UbicacionFragment
                    double latitude = sharedLocationViewModel.getLatitude();
                    double longitude = sharedLocationViewModel.getLongitude();

                    // Crea el objeto Reporte y carga los datos
                    nuevo = new Reporte();
                    nuevo.setLatitud(latitude);
                    nuevo.setLongitud(longitude);
                    nuevo.setTitulo(titulo.getText().toString());
                    nuevo.setDescripcion(descripcion.getText().toString());
                    nuevo.setEstado(new EstadoReporte(1,"ABIERTO"));
                    nuevo.setTipo(new TipoReporte(spinTipoReporte.getSelectedItemPosition()+1, spinTipoReporte.getSelectedItem().toString()));
                    nuevo.setImagen(imagenCapturada);
                    nuevo.setPuntaje(0);
                    nuevo.setFecha(new Date(System.currentTimeMillis()));
                    nuevo.setOwner(null); // REEMPLAZAR POR USUARIO LOGUEADO

                    if(checkFormValid(v,nuevo)){
                        DMAGuardarReporte DMAGuardar = new DMAGuardarReporte(nuevo,v.getContext());
                        DMAGuardar.execute();
                        titulo.setText("");
                        descripcion.setText("");
                        sharedLocationViewModel.setLatitude(0);
                        sharedLocationViewModel.setLongitude(0);
                        longitude = 0;
                        latitude = 0;
                        imagenCapturada = null;
                        Log.i("Existoso","Se guardo bien");
                    }else{
                        Toast.makeText(v.getContext(), "No se pudo crear el reporte", Toast.LENGTH_LONG).show();
                        Log.e("ERROR","No se guardo bien");
                    }

                }catch (Exception e){
                    Log.e("Error", e.toString());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // VALIDA QUE LA ACCION SE HAYA COMPLETADO CORRECTAMENTE
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
            // La imagen se capturó exitosamente
            Bundle extras = data.getExtras();
            imagenCapturada = (Bitmap) extras.get("data");

            // Referencia al ImageView - prueba de imagen
            ImageView imgViewFotoTomada = getView().findViewById(R.id.imgViewFotoTomada);

            // Modificación de tamaño - prueba de imagen
            Bitmap imagenRedimensionada = Bitmap.createScaledBitmap(imagenCapturada, imagenCapturada.getWidth()*2, imagenCapturada.getHeight()*2, true);

            // Configura la imagen capturada en el ImageView - prueba de imagen
            imgViewFotoTomada.setImageBitmap(imagenRedimensionada);
        }
    }

    private void navigateToLocationFragment() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.elegir_ubicacion);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NuevoReporteViewModel.class);
        // TODO: Use the ViewModel
    }

    /// VALIDACIONES DE CAMPOS

    private boolean checkFormValid(View view, Reporte nuevo) {

        if (nuevo.getTitulo().trim().isEmpty()) {
            Toast.makeText(this.getContext(), "Debes poner un título al reporte.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (nuevo.getDescripcion().trim().isEmpty()) {
            Toast.makeText(this.getContext(), "Debes dar una descripcion del problema.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (nuevo.getTipo() == null) {
            Toast.makeText(this.getContext(), "No has seleccionado un tipo de reporte.", Toast.LENGTH_LONG).show();
            return false;
        } else if (nuevo.getTipo().getTipo().isEmpty()){
            Toast.makeText(this.getContext(), "Tipo de reporte no seleccionado.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (nuevo.getLatitud() == 0 || nuevo.getLongitud() == 0) {
            Toast.makeText(this.getContext(), "No has seleccionado una ubicación.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (nuevo.getImagen() == null) {
            Toast.makeText(this.getContext(), "No has cargado una imagen.", Toast.LENGTH_LONG).show();
            return false;
        } else if (nuevo.getImagen().getWidth() < 1 || nuevo.getImagen().getHeight() < 1){
            Toast.makeText(this.getContext(), "La imagen es inválida.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
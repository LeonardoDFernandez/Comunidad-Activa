package frgp.utn.edu.ar.controllers.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import frgp.utn.edu.ar.controllers.R;
import frgp.utn.edu.ar.controllers.data.model.Proyecto;
import frgp.utn.edu.ar.controllers.data.model.Usuario;
import frgp.utn.edu.ar.controllers.data.remote.proyecto.DMASpinnerEstadosProyectos;
import frgp.utn.edu.ar.controllers.data.remote.proyecto.DMASpinnerEstadosProyectosSinDenuncia;
import frgp.utn.edu.ar.controllers.ui.viewmodels.DetalleReporteViewModel;
import frgp.utn.edu.ar.controllers.utils.SharedPreferencesService;

public class DetalleProyectoFragment extends Fragment {

    SharedPreferencesService sharedPreferences = new SharedPreferencesService();
    private DetalleReporteViewModel mViewModel;
    private TextView titulo, descripcion, estado, tipo, requerimiento, contacto, cupo, creador;
    private Spinner spEstadoDP;
    private Button btnUnirseP;
    Usuario loggedInUser = null;
    private Proyecto seleccionado;
    public static DetalleProyectoFragment newInstance() {
            return new DetalleProyectoFragment();}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_proyectos, container, false);
        titulo = view.findViewById(R.id.txtDetTituloP);
        descripcion = view.findViewById(R.id.txtDetDescP);
        estado = view.findViewById(R.id.txtDetEstadP);
        tipo = view.findViewById(R.id.txtDetTipoP);
        requerimiento = view.findViewById(R.id.txtDetReqP);
        contacto = view.findViewById(R.id.txtDetContactoP);
        cupo = view.findViewById(R.id.txtDetCupoP);
        creador = view.findViewById(R.id.txtDetUsuarioCreadorDP);
        spEstadoDP = view.findViewById(R.id.spEstadoDP);
        btnUnirseP = view.findViewById(R.id.btnUnirseDP);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seleccionado = (Proyecto) getArguments().getSerializable("proyectoactual");
        if (seleccionado != null) {
            cargarDatos();
        }
        else {
            Toast.makeText(this.getContext(), "Nulo", Toast.LENGTH_LONG).show();
        }
        spEstadoDP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    private void cargarDatos(){
        titulo.setText(seleccionado.getTitulo());
        creador.setText(seleccionado.getOwner().getUsername());
        descripcion.setText(seleccionado.getDescripcion());
        estado.setText(seleccionado.getEstado().getEstado());
        tipo.setText(seleccionado.getTipo().getTipo());
        requerimiento.setText(seleccionado.getRequerimientos());
        contacto.setText(seleccionado.getContacto());
        cupo.setText(Integer.toString(seleccionado.getCupo()));
        DMASpinnerEstadosProyectosSinDenuncia estadosP = new DMASpinnerEstadosProyectosSinDenuncia(spEstadoDP, getContext());
        estadosP.execute();
        int prueba = seleccionado.getEstado().getId();
        if(prueba!=5){
            spEstadoDP.setVisibility(View.VISIBLE);
            estado.setVisibility(View.GONE);
        }
        else{
            spEstadoDP.setVisibility(View.GONE);
            estado.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetalleReporteViewModel.class);
        // TODO: Use the ViewModel
    }

}
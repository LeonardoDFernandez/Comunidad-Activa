package frgp.utn.edu.ar.controllers.ui.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import frgp.utn.edu.ar.controllers.R;
import frgp.utn.edu.ar.controllers.ui.viewmodels.EliminarPublicacionViewModel;

public class EliminarPublicacionFragment extends Fragment {

    private EliminarPublicacionViewModel mViewModel;

    public static EliminarPublicacionFragment newInstance() {
        return new EliminarPublicacionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eliminar_publicacion, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EliminarPublicacionViewModel.class);
        // TODO: Use the ViewModel
    }

}
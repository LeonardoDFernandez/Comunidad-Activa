package frgp.utn.edu.ar.controllers.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import frgp.utn.edu.ar.controllers.R;
import frgp.utn.edu.ar.controllers.data.model.Reporte;
import frgp.utn.edu.ar.controllers.data.model.ReseniaReporte;
import frgp.utn.edu.ar.controllers.data.model.Usuario;
import frgp.utn.edu.ar.controllers.data.remote.reporte.DMAActualizarVotosReporte;
import frgp.utn.edu.ar.controllers.data.remote.reporte.DMAGuardarResenia;
import frgp.utn.edu.ar.controllers.data.remote.reporte.DMAVerificarUsuarioVoto;
import frgp.utn.edu.ar.controllers.utils.LogService;
import frgp.utn.edu.ar.controllers.utils.LogsEnum;
import frgp.utn.edu.ar.controllers.utils.NotificacionService;

public class ValorarReporteDialogFragment extends DialogFragment {
    Button btnAceptar;
    Button btnCancelar;
    RatingBar rtbValoracion;
    Reporte selectedReport = null;
    Usuario loggedInUser = null;
    LogService logService = new LogService();
    NotificacionService notificacionService = new NotificacionService();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Recupera los datos del Bundle
        Bundle args = getArguments();
        if (args != null) {
            selectedReport = (Reporte) args.getSerializable("selected_report");
            loggedInUser = (Usuario) args.getSerializable("logged_in_user");

        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.DialogTheme_transparent);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_rating_reporte, null);

        btnAceptar = dialogView.findViewById(R.id.btnConfirmarValoracion);
        btnCancelar = dialogView.findViewById(R.id.btnCancelarValoracion);
        rtbValoracion = dialogView.findViewById(R.id.rtbValoracionReporte);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la valoración del RatingBar.
                if (validacionBar()){
                    float rating = rtbValoracion.getRating();
                    float sumaDePuntos = selectedReport.getPuntaje() + rtbValoracion.getRating();
                    selectedReport.setPuntaje(sumaDePuntos);
                    int votos = selectedReport.getCant_votos() + 1;
                    selectedReport.setCant_votos(votos);

                    ReseniaReporte resenia = new ReseniaReporte();
                    resenia.setVotante(loggedInUser);
                    resenia.setReporte(selectedReport);
                    resenia.setPuntaje(rtbValoracion.getRating());

                    DMAVerificarUsuarioVoto DMAUserVoto = new DMAVerificarUsuarioVoto(getContext(),resenia);
                    DMAUserVoto.execute();

                    logService.log(loggedInUser.getId(), LogsEnum.VALORAR_REPORTE, String.format("Solicitaste el cierre del reporte %s", selectedReport.getTitulo()));
                    notificacionService.notificacion(selectedReport.getOwner().getId(), String.format("El %s %s solicito el cierre del reporte %s", loggedInUser.getTipo().getTipo(), loggedInUser.getUsername(), selectedReport.getTitulo()));

                }else{
                    Toast.makeText(getContext(), "Debes elegir un valor", Toast.LENGTH_LONG).show();
                }
                dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancelar la valoración y cerrar el diálogo.
                dismiss();
            }
        });

        builder.setView(dialogView);
        return builder.create();
    }

    private boolean validacionBar(){
        if(rtbValoracion.getRating()!=0){
            return true;
        }
        return false;
    }
}

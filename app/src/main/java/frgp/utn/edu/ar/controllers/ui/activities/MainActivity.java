package frgp.utn.edu.ar.controllers.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import frgp.utn.edu.ar.controllers.R;
import frgp.utn.edu.ar.controllers.data.model.EstadoUsuario;
import frgp.utn.edu.ar.controllers.data.model.Usuario;
import frgp.utn.edu.ar.controllers.data.repository.usuario.UsuarioRepository;
import frgp.utn.edu.ar.controllers.utils.LogService;
import frgp.utn.edu.ar.controllers.utils.LogsEnum;
import frgp.utn.edu.ar.controllers.utils.MailService;
import frgp.utn.edu.ar.controllers.utils.SharedPreferencesService;

public class MainActivity extends AppCompatActivity {
    private LogService logger = new LogService();
    private MailService mailService = new MailService();
    private UsuarioRepository usuarioRepository = new UsuarioRepository();
    private SharedPreferencesService sharedPreferences = new SharedPreferencesService();
    public Usuario usuario;
    public int loginAttemps = 0;
    public EditText etNombre, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etNombre = findViewById(R.id.etNombre);
        etPassword = findViewById(R.id.etPassword);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkActiveUser(getCurrentFocus());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkActiveUser(getCurrentFocus());
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkActiveUser(getCurrentFocus());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = super.onCreateView(name, context, attrs);
        return view;
    }

    public void iniciarSesion(View view){
        if(!isFormValid()) {
            return;
        }

        if(!isDataValid()) {
            return;
        }

        if(loginAttemps > 3) {
            Toast.makeText(this, "Exedio el numero de intentos.", Toast.LENGTH_LONG).show();
            bloqueoDesbloqueoUser("BLOQUEO");
            return;
        }
        sharedPreferences.saveUsuarioData(this, usuario);
        logger.log(usuario.getId(), LogsEnum.LOGIN, String.format("El Usuario %s inicio sesion", usuario.getUsername()));
        etNombre.setText("");
        etPassword.setText("");
        Ingresar(view);
    }

    public void IrRegistro(View view){
        Intent registro = new Intent(this, RegistroActivity.class);
        startActivity(registro);
    }

    public boolean isFormValid() {
        //CHECK FORM VACIO
        if (etNombre.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean isDataValid() {

        usuario = usuarioRepository.loginUsuario(etNombre.getText().toString(), etPassword.getText().toString());

        //CHECK EXISTENCIA USER
        if(usuario == null) {
            Toast.makeText(this, "Mail o Contraseña Incorrectos", Toast.LENGTH_LONG).show();
            loginAttemps++;
            return false;
        }

        //CHECK ESTADO USER
        if(usuario.getEstado().getEstado().equals("SUSPENDIDO")) {
            Toast.makeText(this, "Usuario suspendido. Contacte con el adminsitrador", Toast.LENGTH_LONG).show();
            return false;
        }

        if(usuario.getEstado().getEstado().equals("ELIMINADO")) {
            Toast.makeText(this, "Cuenta de usuario eliminada", Toast.LENGTH_LONG).show();
            return false;
        }

        if(usuario.getEstado().getEstado().equals("INACTIVO")) {
            Toast.makeText(this, "Su usuario todavia no fue aprobado por el administrador", Toast.LENGTH_LONG).show();
            return false;
        }

        if(usuario.getEstado().getEstado().equals("BLOQUEADO")) {
            //Check if bloqueo date is more than two days old
            if(!(usuario.getFecha_bloqueo().getTime() + 172800000 < System.currentTimeMillis())) {
                Toast.makeText(this, "Usuario bloqueado intente nuevamente mas tarde.", Toast.LENGTH_LONG).show();
                return false;
            }
            bloqueoDesbloqueoUser("DESBLOQUEO");
            return true;
        }


        return true;
    }

    public void bloqueoDesbloqueoUser(String condition){
        Usuario usuarioBloq = usuarioRepository.getUserByUserName(etNombre.getText().toString());

        switch (condition){
            case "BLOQUEO":
                if(usuarioBloq != null) {
                    usuarioBloq.setEstado(new EstadoUsuario(4, "BLOQUEADO"));
                    usuarioBloq.setFecha_bloqueo(new java.sql.Date(System.currentTimeMillis()));
                    usuarioRepository.modificarUsuario(usuarioBloq);
                    mailService.sendMail(usuarioBloq.getCorreo(), "Bloqueo de Usuario", "Su usuario ha sido bloqueado por intentos maximos de login");
                    logger.log(usuarioBloq.getId(), LogsEnum.BLOQUEO_USUARIO, String.format("Usuario %s bloqueado por intentos maximos de login", usuarioBloq.getUsername()));
                }
                Toast.makeText(this, "Usuario bloqueado intente nuevamente mas tarde.", Toast.LENGTH_LONG).show();
                loginAttemps = 0;
                break;
            case "DESBLOQUEO":
                if(usuarioBloq != null) {
                    usuarioBloq.setEstado(new EstadoUsuario(1, "ACTIVO"));
                    usuarioRepository.modificarUsuario(usuarioBloq);
                    mailService.sendMail(usuarioBloq.getCorreo(), "Desbloqueo de Usuario", "Su usuario ha sido desbloqueado");
                    logger.log(usuarioBloq.getId(), LogsEnum.DESBLOQUEO_USUARIO, String.format("Usuario %s desbloqueado", usuarioBloq.getUsername()));
                }
                loginAttemps = 0;
                break;
        }
    }

    public void IrRecuperoPass(View view){
        Intent registro = new Intent(this, RecuperoPassActivity.class);
        startActivity(registro);
    }

    public void Ingresar(View view){

        Intent registro = new Intent(this, HomeActivity.class);
        startActivity(registro);
    }

    public void IrVecino(View view){
        etNombre.setText("userVecino1");
        etPassword.setText("123456");
    }

    public void IrModerador(View view){
        etNombre.setText("mcliftonmod");
        etPassword.setText("12345678");
    }

    public void IrAdministrador(View view){
        etNombre.setText("userAdmin1");
        etPassword.setText("123456");
    }

    public void checkActiveUser(View view) {
        Usuario usuario = sharedPreferences.getUsuarioData(this);
        if(usuario != null) {
            Ingresar(view);
        }
    }
}
package com.softmicro.IO4;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class RegistrarUsuario extends AppCompatActivity implements
        View.OnClickListener {

    //Toolbar
    private Toolbar toolbar;
    private TextView lblSignIn;

    private static final String TAG = " ";
    EditText mCorreo;
    Button  mRegistrar;
    //
    String emailPass;
    //Firebase
    private FirebaseAuth mAuth;
    //Alert
    android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        //Toolbar back
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRistrar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Instancia de variables del layout.
        mCorreo = (EditText) findViewById(R.id.txt_correo);
        mRegistrar = (Button) findViewById(R.id.btn_register);
        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarRistrar);
        //Alert
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando...")
                .build();

        //Instancia de Firebase.
        mAuth = FirebaseAuth.getInstance();

        //Escuchadores
        findViewById(R.id.btn_register).setOnClickListener(this);
    }

    private void registerEmailPassword() {
        dialog.show();
        mRegistrar.setEnabled(false);
        //Declaro variables donde guardaré la información escrita por el usuario.
        String email, password;
        email = mCorreo.getText().toString();
        password = "confirmclave123";

        //Patrón para validar el email.
        Pattern patternEmail = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matherEmail = patternEmail.matcher(email);

        //Patrón para validar contraseña segura
        Pattern patternClave = Pattern
                .compile("^(?=\\w*\\d)(?=\\w*[a-z])\\S{5,}$");
        Matcher matherClave = patternClave.matcher(password);

        //Verificar que se ingresen datos
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(RegistrarUsuario.this, "Los campos no pueden estar vacíos.",
                    Toast.LENGTH_SHORT).show();
            mRegistrar.setEnabled(true);
            dialog.dismiss();
            return;
        }

        //Verificacion del correo.
        if(!matherEmail.find())
        {
            Toast.makeText(RegistrarUsuario.this, "Ingrese un correo válido.",
                    Toast.LENGTH_SHORT).show();
            mRegistrar.setEnabled(true);
            dialog.dismiss();
            return;
        }
        emailPass = email;

        //Si llega hasta aquí es porque ingresó todo correctamente.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegistrarUsuario.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Usuario creado.");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegistrarUsuario.this, "Revisa tu correo para confirmar tu cuenta.",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        Toast.makeText(RegistrarUsuario.this, "Error al registrar.",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            });
                            return;
                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Error al crear.", task.getException());
                            AlertDialog.Builder mensaje = new AlertDialog.Builder(RegistrarUsuario.this);
                            mensaje.setTitle("Correo ya Registrado, ¿Quieres Iniciar Sesión?");
                            mensaje.setCancelable(true);
                            mensaje.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(RegistrarUsuario.this, IniciarSesion.class));
                                    finish();
                                }
                            });
                            mensaje.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            mensaje.show();
                            mAuth.signOut();
                            updateUI(null);
                            return;
                        }
                    }
                });
        dialog.dismiss();
        mRegistrar.setEnabled(true);
    }
    //Función para detectar al usuario actual.
    private void updateUI(FirebaseUser user) {
        if(user != null)
        {
            startActivity(new Intent(RegistrarUsuario.this, VerificaEmail.class));
            finish();
        }
    }
    //Función que retrocede.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegistrarUsuario.this, MainActivity.class));
        finish();
    }
    //Función de los escuchadores.
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                registerEmailPassword();
                break;
           // case R.id.lblSignIn:
            //    startActivity(new Intent(RegistrarUsuario.this, IniciarSesion.class));
            //    finish();
            //    break;
            // ...
        }
    }
}

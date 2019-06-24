package com.softmicro.IO4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Provider;

import dmax.dialog.SpotsDialog;

public class InicioSesionExitoso extends AppCompatActivity implements
        View.OnClickListener {

    private GoogleSignInClient clienteGoogle;
    FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    //Alert
    AlertDialog dialog;
    //
    TextView mDatos;
    private Bitmap loadedImage;
    private String correo, uid, DisplayName, phone, photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion_exitoso);

        // Detectar usuario actual.
        mAuth = FirebaseAuth.getInstance();
        //Alert
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando...")
                .build();

        //
        mDatos = (TextView)findViewById(R.id.txt_datos);

        // Configuración de Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //Lo que va aquí está provicional.
        correo = mAuth.getCurrentUser().getEmail();
        uid = mAuth.getCurrentUser().getUid();
        if(!mAuth.getCurrentUser().getDisplayName().isEmpty())
            DisplayName = mAuth.getCurrentUser().getDisplayName();
        else DisplayName = "Vacío";
        if(!mAuth.getCurrentUser().getPhoneNumber().isEmpty())
            phone = mAuth.getCurrentUser().getPhoneNumber();
        else phone = "Vacío";
        if(mAuth.getCurrentUser().getPhotoUrl() != null) {
            photo = mAuth.getCurrentUser().getPhotoUrl().toString();
        }
        else photo = "Vacío";
        mDatos.setText("Si los datos aparecen con vacío, es porque no ingresó con Google o no poseé esa información, estas son verificaciones temporales...\n\nCorreo:" + correo + "\n UID: " + uid + "\n Display Name: " + DisplayName + "\n Phone: " + phone + "\n Photo: " + photo);

        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setEnabled(true);
    }

    private void signOut() {
        dialog.show();
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out_button:
                findViewById(R.id.sign_out_button).setEnabled(false);
                signOut();
                break;
            // ...
        }
    }

    //Para detectar cuando pulsa hacia atrás.
    @Override
    public void onBackPressed() {
        AlertDialog.Builder mensaje = new AlertDialog.Builder(this);
        mensaje.setTitle("¿Seguro que quieres salir?");
        mensaje.setCancelable(true);
        mensaje.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        mensaje.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        mensaje.show();
    }

    private void updateUI(FirebaseUser user) {
        if(user==null)
        {
            Toast.makeText(InicioSesionExitoso.this, "Sesión Cerrada.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(InicioSesionExitoso.this, MainActivity.class));
            finish();
        }
    }
}

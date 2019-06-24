package com.softmicro.IO4;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import dmax.dialog.SpotsDialog;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = "";
    private static final int RC_SIGN_IN = 9001;
    //Detectar usuario.
    private FirebaseAuth mAuth;
    //Variables de layout.
    EditText mCorreo, mClave;
    Button mRegistrar;
    TextView mRClave, mInicioSesion;
    //Alert dialog
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Alert Dialog
        //dialog = new SpotsDialog(MainActivity.this);

        //Instancia de variables del layout
        mInicioSesion = (TextView) findViewById(R.id.btn_iniciosesion);
        mRegistrar = (Button) findViewById(R.id.btn_register);
        //mRClave = (TextView) findViewById(R.id.lbl_recuperarclave);

        // Escuchador del botón de Google y demás
        findViewById(R.id.btn_iniciosesion).setOnClickListener(this);//Inicio Sesión
        findViewById(R.id.btn_register).setOnClickListener(this);
        //findViewById(R.id.lbl_recuperarclave).setOnClickListener(this);
        //Instancia usuario
        mAuth = FirebaseAuth.getInstance();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            NotificationChannel channel =
                    new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            FirebaseMessaging.getInstance().subscribeToTopic("main")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Completado";
                            if (!task.isSuccessful()) {
                                msg = "Falló";
                            }
                            Log.d(TAG, msg);
                            //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        user.reload();
        if(currentUser != null && currentUser.isEmailVerified()) {
            System.out.println("Entro a true");
            updateUI(currentUser);
        }
        else if(currentUser != null && !currentUser.isEmailVerified()) {
            System.out.println("Entro a false");
            startActivity(new Intent(MainActivity.this, VerificaEmail.class));
            finish();
            updateUI(null);
        }
        updateUI(null);
    }
    //Iniciar sesión.
    private void signIn() {
        startActivity(new Intent(MainActivity.this, IniciarSesion.class));
    }

    //Registrarse por correo.
    private void signUp() {
        AlertDialog.Builder mensaje = new AlertDialog.Builder(MainActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        mensaje.setView(inflater.inflate(R.layout.dialogpoliticas, null))
                .setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this, RegistrarUsuario.class));
                    }
                })
                .setNegativeButton("Desacuerdo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        mensaje.show();
    }
    //Detecta si el usuario ya inició sesión.
    private void updateUI(FirebaseUser user) {
        if(user!=null)
        {
            startActivity(new Intent(MainActivity.this, InicioSesionExitoso.class));
            finish();
            Toast.makeText(MainActivity.this, "Bienvenido.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    //Escuchador Botones.
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iniciosesion:
                signIn();
                break;
            case R.id.btn_register:
                signUp();
                break;
           // case R.id.lbl_recuperarclave:
           //     recoveryPass();
           //     break;
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
}

//sha1 21/6/19
//CE:9A:53:E2:73:85:EE:B9:1A:C4:32:31:66:93:AF:98:D6:E0:C1:6F

//sha1 21/6/19
//en MainActivity.java
//12:3D:FA:D9:09:8C:D1:F5:14:86:42:B9:A2:36:07:91:81:DE:D4:A8

/*
Alias: key0
MD5: 49:AF:EA:51:72:67:6C:84:02:D9:8F:32:74:4F:91:75
SHA1: 12:3D:FA:D9:09:8C:D1:F5:14:86:42:B9:A2:36:07:91:81:DE:D4:A8
SHA-256: 5C:74:90:78:F8:F3:E5:4B:3D:DC:0E:2A:7E:36:9C:F7:77:B8:FD:A0:97:E0:E8:D8:5F:2D:01:82:E1:1A:DF:0B
Valid until: miércoles 8 de junio de 2044
 */
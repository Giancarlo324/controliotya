package com.softmicro.IO4;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.reflect.Array;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class InicioSesionExitoso extends AppCompatActivity implements
        View.OnClickListener {

    private GoogleSignInClient clienteGoogle;
    FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    //
    String token, token2;
    //Alert
    android.app.AlertDialog dialog;
    //
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //
    ListView listaToken;
    //
    private static final String TAG = "";
    TextView mDatos;
    private String correo, uid, DisplayName, phone, photo;
    //Datos
    private DatabaseReference mFirebaseDatabase;


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
        listaToken = (ListView)findViewById(R.id.list_token);
        //token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        registrarToken(token);

                        // Log and toast
                        Toast.makeText(InicioSesionExitoso.this, token,
                                Toast.LENGTH_SHORT).show();
                    }
                });

        //

        // Configuración de Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        correo = mAuth.getCurrentUser().getEmail();
        uid = mAuth.getCurrentUser().getUid();

        mDatos.setText("Correo:" + correo + "\n UID: " + uid);

        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setEnabled(true);
    }

    private void registrarToken(String token) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Usuarios");

        databaseReference.child(uid).child("Email").setValue(correo);
        databaseReference.child(uid).child("Token").setValue(token);

        final ArrayList<String> tokenlist = new ArrayList<>();
        tokenlist.add(token);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tokenlist);

        listaToken.setAdapter(arrayAdapter);

        listaToken.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(InicioSesionExitoso.this, "Notificacion se envía a: ." + tokenlist.get(position),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Base
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
        dialog.dismiss();
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

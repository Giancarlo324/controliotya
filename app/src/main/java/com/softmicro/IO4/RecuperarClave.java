package com.softmicro.IO4;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class RecuperarClave extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = " ";
    EditText mCorreo;
    Button mRClave, mVolver;
    TextView txtEmail;
    FirebaseAuth auth;
    //Alert
    android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_clave);

        //Instancia de variables del layout
        mCorreo = (EditText) findViewById(R.id.txt_correo);
        mRClave = (Button) findViewById(R.id.btn_re_Clave);
        mVolver = (Button) findViewById(R.id.btn_volver);
        txtEmail = (TextView) findViewById(R.id.lbl_text_send);
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando...")
                .build();

        auth = FirebaseAuth.getInstance();

        //Escuchadores
        findViewById(R.id.btn_re_Clave).setOnClickListener(this);
        findViewById(R.id.btn_volver).setOnClickListener(this);
    }

    private void recuClave() {
        dialog.show();
        //Declaro variables donde guardaré la información escrita por el usuario.
        String email;
        email = mCorreo.getText().toString();

        //Patrón para validar el email.
        Pattern patternEmail = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matherEmail = patternEmail.matcher(email);

        //Verificacion del correo.
        if(!matherEmail.find())
        {
            Toast.makeText(RecuperarClave.this, "Ingrese un correo válido.",
                    Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }
        //Si llega hasta aquí es porque enviará el correo.
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(RecuperarClave.this, "Correo de Recuperación Enviado.",
                                    Toast.LENGTH_SHORT).show();
                            txtEmail.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }
                        else
                        {
                            dialog.dismiss();
                            Toast.makeText(RecuperarClave.this, "El Correo no está Registrado.",
                                    Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder mensaje = new AlertDialog.Builder(RecuperarClave.this);
                            mensaje.setTitle("El Correo no está Registrado, ¿Te Quieres Registrar?");
                            mensaje.setCancelable(true);
                            mensaje.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(RecuperarClave.this, MainActivity.class));
                                    finish();
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
                });
        dialog.dismiss();
    }

    private void volverInicio() {
        startActivity(new Intent(RecuperarClave.this, MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_re_Clave:
                recuClave();
                break;
            case R.id.btn_volver:
                volverInicio();
                break;
            // ...
        }
    }

    //Función que retrocede.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RecuperarClave.this, MainActivity.class));
        finish();
    }
}

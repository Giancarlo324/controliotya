package com.softmicro.IO4;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class CrearPass extends AppCompatActivity
        implements
        View.OnClickListener{

    EditText mClave, mVClave;
    Button mFinish;
    //Detefctar al usuario
    private FirebaseAuth mAuth;
    //Alert
    android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_pass);

        mClave = (EditText) findViewById(R.id.txt_password);
        mVClave = (EditText) findViewById(R.id.txt_password2);
        mFinish = (Button) findViewById(R.id.btn_finish);

        //AlertDialog
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando...")
                .build();

        //Instancia de Firebase.
        mAuth = FirebaseAuth.getInstance();

        //Escuchadores
        findViewById(R.id.btn_finish).setOnClickListener(this);
    }

    private void registerEmailPassword() {
        dialog.show();
        //Declaro variables donde guardaré la información escrita por el usuario.
        String email, password, password2;
        password = mClave.getText().toString();
        password2 = mVClave.getText().toString();

        //Patrón para validar contraseña segura
        Pattern patternClave = Pattern
                .compile("^(?=\\w*\\d)(?=\\w*[a-z])\\S{6,}$");
        Matcher matherClave = patternClave.matcher(password);

        //Verificar que se ingresen datos
        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {
            Toast.makeText(CrearPass.this, "Los campos no pueden estar vacíos.",
                    Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        //Verificación contraseña segura.
        if(!matherClave.find() || password.length() < 6 || password2.length() < 6)
        {
            Toast.makeText(CrearPass.this, "Ingrese una clave segura(Letras y números, 6 caracteres mínimo).",
                    Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        //Verificar que las contraseñas sean iguales.
        if( !password.equals(password2) || !password2.equals(password))
        {
            System.out.println("Clave1: " + password);
            System.out.println("Clave2: " + password2);
            Toast.makeText(CrearPass.this, "Las contraseñas no coinciden.",
                    Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        //Si llega hasta aquí es porque ingresó todo correctamente.
        mAuth.getCurrentUser().updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    dialog.dismiss();
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(CrearPass.this, "Correcto.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(user);
                }
                else
                {
                    dialog.dismiss();
                    mAuth.signOut();
                    updateUI(null);
                    Toast.makeText(CrearPass.this, "Ocurrió un error.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.dismiss();
    }

    //Función para detectar al usuario actual.
    private void updateUI(FirebaseUser user) {
        if(user != null)
        {
            startActivity(new Intent(CrearPass.this, MainActivity.class));
            finish();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                registerEmailPassword();
                break;
            // ...
        }
    }
}

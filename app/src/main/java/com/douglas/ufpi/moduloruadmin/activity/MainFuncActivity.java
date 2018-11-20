package com.douglas.ufpi.moduloruadmin.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.douglas.ufpi.moduloruadmin.config.ConfiguracaoFirebase;
import com.douglas.ufpi.moduloruadmin.model.Aluno;
import com.google.firebase.auth.FirebaseAuth;

import com.douglas.ufpi.moduloruadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainFuncActivity extends AppCompatActivity {

    private FirebaseAuth funcAutenticacao;
    private Button botaoDeslogar;
    private Button botaoPortaria;
    private Button botaoVenderFicha;
    private DatabaseReference reference;
    private String idAluno;
    private Aluno aluno;
    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_func);

        funcAutenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        botaoDeslogar = findViewById(R.id.botaoDeslogarFunc);
        botaoPortaria = findViewById(R.id.botaoPortaria);
        botaoVenderFicha = findViewById(R.id.botaoVenderFichas);


        botaoVenderFicha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainFuncActivity.this, VenderFichasActivity.class);
                startActivity(intent);
            }
        });

        botaoDeslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deslogarFuncionario();
            }
        });

        botaoPortaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Modo portaria ativo");
                integrator.setCameraId(0);
                integrator.initiateScan();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);


        if (result != null) {
            if (result.getContents() != null) {
                idAluno = result.getContents();
                   aceitarFicha();

                  // chamarCamera();
            } else {
                Toast.makeText(MainFuncActivity.this, "Scan cancelado!", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void chamarCamera() {

            IntentIntegrator integrator = new IntentIntegrator(activity);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Modo portaria ativo");
            integrator.setCameraId(0);
            integrator.initiateScan();

    }

    public void aceitarFicha() {

        if (idAluno == null){
            Toast.makeText(MainFuncActivity.this, "Erro ao realizar operação", Toast.LENGTH_LONG).show();
        }else{
            reference = ConfiguracaoFirebase.getFirebase().child("alunos").child(idAluno);
            aluno = new Aluno();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        aluno = dataSnapshot.getValue(Aluno.class);
                        int qtAtual = aluno.getFicha();
                        if(qtAtual <= 0){
                            Toast.makeText(MainFuncActivity.this, "Aluno não possue fichas!", Toast.LENGTH_LONG).show();
                        }else {
                            int atualizarValor = qtAtual - 1;
                            aluno.setId(idAluno);
                            aluno.setFicha(atualizarValor);
                            aluno.salvar();

                            Toast.makeText(MainFuncActivity.this, "Confirmado!", Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }

    public void deslogarFuncionario(){

        funcAutenticacao.signOut();
        Intent intent = new Intent(MainFuncActivity.this, FuncLoginActivity.class);
        startActivity(intent);
        finish();

    }


}

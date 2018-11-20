package com.douglas.ufpi.moduloruadmin.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.douglas.ufpi.moduloruadmin.R;
import com.douglas.ufpi.moduloruadmin.config.ConfiguracaoFirebase;
import com.douglas.ufpi.moduloruadmin.model.Aluno;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class VenderFichasActivity extends AppCompatActivity {

    private Aluno aluno;
    private DatabaseReference reference;
    private TextView nomeAluno;
    private TextView curso;
    private TextView fichas;
    private TextView matricula;
    private Button botaoEscanear;
    private String idAluno;
    private Button botaoVendas;
    private EditText quantidadeFichas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vender_fichas);

        setTitle("Vender Fichas");

        nomeAluno = findViewById(R.id.textNomeAluno);
        curso = findViewById(R.id.textCursoAluno);
        fichas = findViewById(R.id.textFichas);
        matricula = findViewById(R.id.textMatricula);
        botaoEscanear = findViewById(R.id.botaoEscanear);
        botaoVendas = findViewById(R.id.botaoVender);
        quantidadeFichas = findViewById(R.id.quantidadeFichas);
        final Activity activity = this;

        botaoEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scanear QR Code do aluno");
                integrator.setCameraId(0);
                integrator.initiateScan();
            }
        });

        botaoVendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idAluno == null){
                    Toast.makeText(VenderFichasActivity.this, "Para realizar uma venda é necessário escanear o código do aluno", Toast.LENGTH_LONG).show();
                }else if(quantidadeFichas.getText().toString().isEmpty()){
                    Toast.makeText(VenderFichasActivity.this, "Digite a quantidade de fichas que deseja vender", Toast.LENGTH_LONG).show();
                }else{

                    reference = ConfiguracaoFirebase.getFirebase().child("alunos").child(idAluno);
                    aluno = new Aluno();
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                aluno = dataSnapshot.getValue(Aluno.class);
                                int qtAtual = aluno.getFicha();
                                int qtAdicionar = Integer.parseInt(quantidadeFichas.getText().toString());
                                int atualizarValor = qtAtual + qtAdicionar;
                                aluno.setId(idAluno);
                                aluno.setFicha(atualizarValor);
                                aluno.salvar();
                                quantidadeFichas.setText("");
                                Toast.makeText(VenderFichasActivity.this, "Venda com sucesso!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        });

    }

    public void recuperarAluno(){

        reference = ConfiguracaoFirebase.getFirebase().child("alunos").child(idAluno);

        aluno = new Aluno();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    aluno = dataSnapshot.getValue(Aluno.class);

                    String nomeAlunoExibir = aluno.getNome().toString();
                    String cursoAluno = aluno.getCurso().toString();
                    String ficha = Integer.toString(aluno.getFicha());
                    String matriculaAluno = aluno.getMatricula();

                    nomeAluno.setText("ALUNO: "+nomeAlunoExibir.toUpperCase());
                    matricula.setText("MATRICULA: "+matriculaAluno.toUpperCase());
                    curso.setText("CURSO: "+cursoAluno.toUpperCase());
                    fichas.setText("FICHAS: "+ficha.toUpperCase());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if (result.getContents() !=  null){
                idAluno = result.getContents();
                recuperarAluno();
            }else{
                Toast.makeText(VenderFichasActivity.this, "Scan cancelado!", Toast.LENGTH_LONG).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

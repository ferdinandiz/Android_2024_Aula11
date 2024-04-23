package com.fer.aula11_camera;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageButton btnCarregar, btnCamera, btnSalvar, btnEnviar;
    Button btnTexto;
    EditText txSuperior, txInferior;
    TextView tx01, tx02;
    ImageView imagem;
    String arqImagem;
    Uri imagemUri;

    SeekBar seekBar1, seekBar2;
    int progress1 = 40, progress2 = 40;
    boolean color = false;
    int requestCode;

    String[] permissions = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = findViewById(R.id.btnCamera);
        btnCarregar = findViewById(R.id.btnCarregar);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnTexto = findViewById(R.id.btnTexto);

        tx01 = findViewById(R.id.txt01);
        tx02 = findViewById(R.id.txt02);
        txInferior = findViewById(R.id.edinferior);
        txSuperior = findViewById(R.id.edsuperior);

        imagem = findViewById(R.id.imageView);
        seekBar1 = findViewById(R.id.seekBar1);
        seekBar2 = findViewById(R.id.seekBar2);

        btnSalvar.setEnabled(false);
        btnEnviar.setEnabled(false);
        seekBar1.setMax(100);
        seekBar1.setProgress(progress1);
        seekBar2.setMax(100);
        seekBar2.setProgress(progress2);

        Intent fotoCarregada = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        Intent fotoCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent principal = getIntent();

        requestCode = principal.getIntExtra("requestCode",0);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                        Toast.makeText(MainActivity.this, "Permissão Concedida", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permissão Negada!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        if(requestCode == 1){
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE,"Nova Imagem");
            values.put(MediaStore.Images.Media.DESCRIPTION,"Imagem vinda da Camera");
            imagemUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            fotoCamera.putExtra(MediaStore.EXTRA_OUTPUT, imagemUri);
            openSomeActivityForResult(fotoCamera, requestCode);
        } else if(requestCode == 2){
            fotoCarregada.setType("*/*");
            fotoCarregada = Intent.createChooser(fotoCarregada,"Escolha o Arquivo");
            openSomeActivityForResult(fotoCarregada, requestCode);
        }

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progress1 = progress;
                tx01.setTextSize(progress1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress2 = i;
                tx02.setTextSize(progress2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        tx01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(color){
                    tx01.setTextColor(Color.BLACK);
                    tx02.setTextColor(Color.BLACK);
                }else {
                    tx01.setTextColor(Color.WHITE);
                    tx02.setTextColor(Color.WHITE);
                }
                color = !color;
            }
        });

        tx02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(color){
                    tx01.setTextColor(Color.BLACK);
                    tx02.setTextColor(Color.BLACK);
                }else {
                    tx01.setTextColor(Color.WHITE);
                    tx02.setTextColor(Color.WHITE);
                }
                color = !color;
            }
        });

        btnTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tx01.setText(txSuperior.getText().toString());
                tx02.setText(txInferior.getText().toString());
                txInferior.setText("");
                txSuperior.setText("");
                btnEnviar.setEnabled(false);
                btnSalvar.setEnabled(true);
            }
        });

    }

    private void openSomeActivityForResult(Intent fotoCamera, int requestCode) {

    }
}
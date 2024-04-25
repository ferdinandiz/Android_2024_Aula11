package com.fer.aula11_camera;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

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
            fotoCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1); // Alta Qualidade
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

        if(!temCamera()){
            btnCamera.setEnabled(false);
        }

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
        btnCarregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                i.putExtra("requestCode", 2);
                startActivity(i);
                finish();
            }
        });
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviar();
            }
        });
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    View relCenter = findViewById(R.id.relativeCenter);
                    Bitmap bitmap = screenShot(relCenter);
                    arqImagem = "AulaCamera_"+System.currentTimeMillis()+".png";
                    armazenarImagem(bitmap, arqImagem);
                    btnEnviar.setEnabled(true);
                    btnSalvar.setEnabled(false);
                } else {
                    requestPermission();
                }
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                i.putExtra("requestCode", 1);
                startActivity(i);
                finish();
            }
        });

        if(ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE},1);
        }
    }

    private boolean temCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try{
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", new Object[]{getApplicationContext().getPackageName()})));
                activityResultLauncher.launch(intent);
            } catch (Exception e){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activityResultLauncher.launch(intent);
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 30);
        }
    }

    private boolean checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        } else {
            int readCheck = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
            return readCheck == PackageManager. PERMISSION_GRANTED || writeCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void armazenarImagem(Bitmap bitmap, String arqImagem) {
        String diretorio = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AulaCamera";
        File dir = new File(diretorio);
        if(!dir.exists()){
            if(dir.mkdir()){
                Toast.makeText(this, "Pasta Criada!", Toast.LENGTH_SHORT).show();
            }
        }
        File file = new File(diretorio, arqImagem);
        if(!file.exists()) {
            file = new File(dir, arqImagem);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Salvo com sucesso", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Erro ao Salvar!", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap screenShot(View relCenter) {
        relCenter.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(relCenter.getDrawingCache());
        relCenter.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void enviar() {
        String diretorio = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AulaCamera/" + arqImagem;
        File f = new File(diretorio);
        ContentValues values = new ContentValues(2);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATA, f.getAbsolutePath());
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent,"Compartilhar usando..."));
    }

    private void openSomeActivityForResult(Intent data, int requestCode) {
        ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            if(requestCode == 2 && data != null){
                                Intent resultado = result.getData();
                                Uri pegarImagem = resultado.getData();
                                imagem.setImageURI(pegarImagem);
                                btnSalvar.setEnabled(true);
                                btnEnviar.setEnabled(false);
                            } else if (requestCode == 1 && data != null){
                                Bundle bundle = data.getExtras();
                                if(bundle != null) {
                                    imagem.setImageURI(imagemUri);
                                    btnSalvar.setEnabled(true);
                                    btnEnviar.setEnabled(false);
                                } else {
                                    Toast.makeText(MainActivity.this, "Erro! Impossível carregar imagem!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
        intentActivityResultLauncher.launch(data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length <= 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Sem Permissões Suficientes!", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Uso autorizado!", Toast.LENGTH_SHORT).show();
                }
            break;
            case 30:
                if(grantResults.length > 0){
                    boolean readPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(readPermission && writePermission){
                        Toast.makeText(this, "Permissão Concedida!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(this, "Permissão Negada!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Você negou a permissão!!", Toast.LENGTH_SHORT).show();
                }
            break;
        }
    }
}
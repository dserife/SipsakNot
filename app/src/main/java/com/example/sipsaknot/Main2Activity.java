package com.example.sipsaknot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    Button button;

    //static tanımladık çünkü, her iki activity içiinden de ulaşabilmek için.
    static SQLiteDatabase database;
    Bitmap seciliResim; //kullanıcının seçtiği resim


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView=findViewById(R.id.ivResimEkle);
        editText=findViewById(R.id.etNotYaz);
        button=findViewById(R.id.btnKaydet);

        Intent intent = getIntent();
        String info= intent.getStringExtra("info");

        if (info.equalsIgnoreCase("new")){

            Bitmap arkaplan= BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.resimekle);
            imageView.setImageBitmap(arkaplan);
            button.setVisibility(View.VISIBLE);
            editText.setText("");

        }else
            {
                String name = intent.getStringExtra("name");
                editText.setText(name);
                int position = intent.getIntExtra("position", 0);
                imageView.setImageBitmap(MainActivity.notResim.get(position));
                button.setVisibility(View.INVISIBLE);
        }

    }



    public void resimEkle (View view){

        //kullanıcı iznini almak için yazdık.
        //eğer kullanıcının izni yoksa izin al.

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }

        else //eğer kullanıcının izni varsa galeriye git.
            {
            //galeriye gidip resim seçmek için gereken kod.
            Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
        }
    }


    //kullanıcı izin verdiyse galeriye git. onRequestPermissionsResult metodu bunun için.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode ==2){

            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //galeriye gidip resim seçmek için gereken kod.
                Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==1 && resultCode==RESULT_OK && data!= null){
            Uri image =data.getData();

            try {
                seciliResim = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                imageView.setImageBitmap(seciliResim);

            }catch (IOException e){

                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //notu kaydetmek için gerekenleri yazıyoruz. ve neleri kaydedeceğimizi

    public void kaydet(View view){

        String notAdi=editText.getText().toString();

        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        seciliResim.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
        byte[] byteArray= byteArrayOutputStream.toByteArray();


        try {

            database= this.openOrCreateDatabase("SipsakNot",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS notEkle (name VARCHAR, image BLOB)");

            String sqlString= "INSERT INTO notEkle (name,image) VALUES (?,?)";
            SQLiteStatement sqLiteStatement= database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,notAdi);
            sqLiteStatement.bindBlob(2,byteArray);
            sqLiteStatement.execute();

//            database.execSQL("INSERT INTO notEkle (name,image) VALUES (?,?)");

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        Intent intent= new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);

    }
}

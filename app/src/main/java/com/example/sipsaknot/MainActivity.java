package com.example.sipsaknot;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    static ArrayList<Bitmap> notResim;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //oluşturduğumuz menüyü uygulamamızda kullanabilmemiz için yazılmış olan metot.
        //menüyü inflate ettirdik, yani onu göster dedik.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.not_ekle, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menüyü seçersek eğer neler olacak, onları belirlemek için bu metodu kullandık.

        if (item.getItemId() == R.id.not_ekle){

            //eğer benim oluşturduğum menüye tıklarsa bunlar olsun. yani yeni bir sayfaya geçecek.
            Intent intent= new Intent(getApplicationContext(),Main2Activity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=findViewById(R.id.listView);

        final ArrayList<String> notAdi = new ArrayList<String>();
        notResim = new ArrayList<Bitmap>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, notAdi);
        listView.setAdapter(arrayAdapter);

        try {

            Main2Activity.database= this.openOrCreateDatabase("SipsakNot",MODE_PRIVATE,null);
            Main2Activity.database.execSQL("CREATE TABLE IF NOT EXISTS notEkle (name VARCHAR, image BLOB)");

            Cursor cursor =Main2Activity.database.rawQuery("SELECT * FROM notEkle",null);

            int nameIx=cursor.getColumnIndex("name");
            int imageIx=cursor.getColumnIndex("image");

            cursor.moveToFirst();

            while (cursor != null){

                notAdi.add(cursor.getString(nameIx));

                byte[] byteArray= cursor.getBlob(imageIx);
                Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                notResim.add(image);

                cursor.moveToNext();

                arrayAdapter.notifyDataSetChanged();

            }

        }
        catch (Exception e){

            e.printStackTrace();
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent= new Intent(getApplicationContext(),Main2Activity.class);
                intent.putExtra("info","old");

                intent.putExtra("name",notAdi.get(position));
                intent.putExtra("position", position);
                //secilenResim=notResim.get(position);

                startActivity(intent);
            }
        });
    }
}

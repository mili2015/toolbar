package br.com.mili.toolbar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class ImageActivity extends ActionBarActivity {

    private ImageView imageView;
    private SQLiteDatabase db;
    private Uri targetUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = (ImageView) findViewById(R.id.imageView);

        db = this.openOrCreateDatabase("toolbar.db", Context.MODE_PRIVATE,  null);
        db.execSQL("drop table if exists image; ");
        db.execSQL("create table if not exists image (arquivo blob); ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void save(View view)
    {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grande);
            ByteArrayOutputStream streamJM = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, streamJM);

            byte [] img = streamJM.toByteArray();


            ContentValues values = new ContentValues();
            values.put("arquivo", img);
            long result =  db.insert("image", null, values);



            Toast.makeText(this,"save "+result,Toast.LENGTH_LONG).show();

        } catch (Exception e) {

            Log.e("mili","Erro",e);

        }
    }

    public void download(View view)
    {

        Cursor c = db.rawQuery("Select arquivo from image", new String[]{});

        boolean first = c.moveToFirst();
        Log.d("mili", " Fist "+first);


            while (!c.isAfterLast()) {

                boolean next = c.moveToNext();

                Log.d("mili", " Next "+next);

                byte[] img = c.getBlob(c.getColumnIndex("arquivo"));
                Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
                imageView.setImageBitmap(bmp);

                Toast.makeText(this, "download", Toast.LENGTH_LONG).show();
            }

    }
}

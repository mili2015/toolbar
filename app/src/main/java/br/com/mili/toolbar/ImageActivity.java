package br.com.mili.toolbar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.ByteArrayOutputStream;

public class ImageActivity extends ActionBarActivity {

    private SubsamplingScaleImageView imageView;
    private ScaleGestureDetector gestureDetector;
    private Matrix matrix = new Matrix();

    private SQLiteDatabase db;
    private Uri targetUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = (com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView) findViewById(R.id.imageView);
        //gestureDetector = new ScaleGestureDetector(this, new ScaleListener());

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
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.papel700gimp);
            ByteArrayOutputStream streamJM = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, streamJM);

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

                boolean next = first ? first : c.moveToNext();

                if(next)
                {
                    Log.d("mili", " Next "+next);

                    byte[] img = c.getBlob(c.getColumnIndex("arquivo"));
                    Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);

                    imageView.setImage(ImageSource.bitmap(bmp));
                    //tImageBitmap(bmp);
                }
                c.moveToNext();
            }

    }




   private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            Log.d("mili","onScale");
            float scaleFactor = detector.getScaleFactor();
           // scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));

            matrix.setScale(scaleFactor, scaleFactor);
           // imageView.setImageMatrix(matrix);

            Log.d("mili", "onScale -> "+scaleFactor);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            Log.d("mili","onScaleBegin");
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.d("mili","onScaleEnd");
        }
    }

}

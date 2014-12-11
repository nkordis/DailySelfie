package murachandroidworkplace.dailyselfie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by nikos on 28/11/2014.
 */
public class PhotoActivity extends Activity {

    ImageView image;
    String mCurrentPhotoPath;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);
        //TODO Display selfie in full View
        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("bitmap");
        mCurrentPhotoPath = (String)intent.getStringExtra("fileName");

        image = (ImageView)findViewById(R.id.imageView2);
        //bitmap.setHeight(56);
          image.setImageBitmap(bitmap);
       // setPic();

    }

    private void setPic() {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        int scaleFactor = 10;

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        image.setImageBitmap(bitmap);
    }
}

package murachandroidworkplace.dailyselfie;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends ListActivity {

    private static final String TAG = "Daily Selfie";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    final Context context = this;

    PhotosListAdapter mAdapter;
    String mCurrentPhotoPath;
    String mFileName;
    File photoFile;
    String timeStamp; //TODO Create Date variable or replace String timeStamp

    static PendingIntent pendingIntent;
    static AlarmManager alarmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO Delete item's clickability. Items must be clickable in the PhotosListAdapter.
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "entered onItemClick");
                PhotoRecord photoRecord = (PhotoRecord) mAdapter.getItem(position);
                Bitmap bitmap = photoRecord.getmPhotoBitmap();

                Intent photoLargeIntent = new Intent(MainActivity.this, PhotoActivity.class);
                photoLargeIntent.putExtra("bitmap", (Parcelable) bitmap);
                photoLargeIntent.putExtra("fileName", mFileName);

                startActivity(photoLargeIntent);
            }
        });

        Intent intentsOpen = new Intent(this, AlarmReceiver.class);
        intentsOpen.setAction("murachandroidworkplace.takephoto.alarm.ACTION");
        pendingIntent = PendingIntent.getBroadcast(this, 111, intentsOpen, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //fireAlarm();

        mAdapter = new PhotosListAdapter(getApplicationContext());
        mAdapter.addAllViews();
        setListAdapter(mAdapter);
    }



    /*
    Encodes the photo in the return Intent delivered to onActivityResult()
    as a small Bitmap in the extras, under the key "data". The following code retrieves this image and displays
    it in an ImageView.
    */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "entered onActivityResult");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.i(TAG, "resultCode == RESULT_OK ");


            galleryAddPic();
            Bitmap imageBitmap = setPic();



            PhotoRecord photoRecord = new PhotoRecord(imageBitmap,mCurrentPhotoPath,mFileName,timeStamp);

            mAdapter.add(photoRecord);
            Log.i(TAG, "exit onActivityResult");
        }
    }

    /*
    Invokes an intent to capture a photo
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, ex.getMessage());

            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }


        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //TODO Assign value to Date variable

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);


        File image = new File(storageDir,imageFileName+".jpg");
        //  storageDir.mkdirs();

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mFileName = image.getAbsolutePath();

        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap setPic(){



        int scaleFactor = 5;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mFileName, bmOptions);


        return bitmap;
    }

    public void fireAlarm() {
        /**
         * call broadcast receiver
         */
         final int TWO_MINUTES = 2 * 60 * 1000;
         final int ONE_DAY = 24 * 60 * 60 * 1000;


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ONE_DAY, ONE_DAY, pendingIntent);


    }

    public void stopAlarm(){
        alarmManager.cancel(pendingIntent);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {

            case R.id.camera_icon:
            Log.i(TAG, "camera's icon pressed");
            dispatchTakePictureIntent();
            return true;

            case R.id.cancel_alarm:
            stopAlarm();
            Toast.makeText(this, "Alarm canceled", Toast.LENGTH_SHORT).show();
            return true;

            case R.id.set_alarm:
            fireAlarm();
            Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show();
            return true;

            case R.id.deletallpics:
                //TODO Create a dialog asking the users if he/she wants to delete all selfies
                mAdapter.deleteAllPics();
                mAdapter.removeAllViews();
                return true;

            case R.id.about:
                //TODO Create a button to dismiss the dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog);
                dialog.setTitle(R.string.about_text_title);
                dialog.show();
                return true;

            //TODO Create option so the user can customize the alarm
        }



        return super.onOptionsItemSelected(item);
    }
}

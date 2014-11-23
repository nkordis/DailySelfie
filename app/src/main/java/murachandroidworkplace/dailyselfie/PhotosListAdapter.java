package murachandroidworkplace.dailyselfie;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;


public class PhotosListAdapter extends BaseAdapter {

    private static final String TAG = "PhotoListAdapter";

    private static class ViewHolder {
        ImageView image;
    }

    private final List<Bitmap> mItems = new ArrayList<Bitmap>();
    private final Context mContext;

    public PhotosListAdapter(Context context) {

        mContext = context;

    }

    // Returns the number of mItem
    @Override
    public int getCount() {
        return mItems.size();
    }

    // Retrieve the number of ToDoItems
    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    // Get the ID for the mItem
    // In this case it's just the position
    @Override
    public long getItemId(int position) {
        return position;
    }

    // a View for the mItem at specified position
    // using the ViewHolder pattern to make scrolling more efficient
    // See: http://developer.android.com/training/improving-layouts/smooth-scrolling.html
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Bitmap currentBitmap = mItems.get(position);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        RelativeLayout itemLayout = (RelativeLayout) convertView;
        if (convertView == null) {
            itemLayout = (RelativeLayout) inflater.inflate(R.layout.activity_main, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.image = (ImageView)itemLayout.findViewById(R.id.imageView);
            itemLayout.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder)itemLayout.getTag();
        holder.image.setImageBitmap(currentBitmap);

        return itemLayout;
    }



    // Add a ToDoItem to the adapter
    // Notify observers that the data set has changed
    public void add(Bitmap item) {

        mItems.add(item);
        notifyDataSetChanged();

        Log.i(TAG, "List view updated");


    }
}

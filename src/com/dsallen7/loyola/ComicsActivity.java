package com.dsallen7.loyola;

import java.io.File;

import com.koushikdutta.ion.Ion;
import com.dsallen7.loyola.ComicsActivity.MyAdapter;

import com.sun.syndication.feed.*;
import com.sun.syndication.feed.module.mediarss.*;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ComicsActivity extends ActionBarActivity {
    private MyAdapter mAdapter;
    Cursor mediaCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    // Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

	    // Create the text view
	    TextView textView = new TextView(this);
	    textView.setTextSize(40);
	    textView.setText(message);

	    // Set the text view as the activity layout
	    setContentView(R.layout.activity_display_message);
        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);

        setContentView(R.layout.gallery);

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi * 2;
        GridView view = (GridView) findViewById(R.id.results);
        view.setNumColumns(cols);
        mAdapter = new MyAdapter(this);
        view.setAdapter(mAdapter);

        loadMore();
	    /*
	    ImageView ionImage = (ImageView) findViewById(R.id.imageView1);
	    
	    Ion.with(ionImage).placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher).load("http://fc03.deviantart.net/fs71/f/2014/207/7/7/foreign_affairs_by_dsallen7-d7se4u6.png");
	    //http://fc03.deviantart.net/fs71/f/2014/207/7/7/foreign_affairs_by_dsallen7-d7se4u6.png
		
		setContentView(R.layout.activity_display_message);
		
		if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .add(R.id.action_bar_container, new PlaceholderFragment()).commit();
        }
		
		Intent intent = getIntent();
		String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                  Bundle savedInstanceState) {
              View rootView = inflater.inflate(R.layout.activity_display_message,
                      container, false);
              return rootView;
        }
    }

    // Adapter to populate and imageview from an url contained in the array adapter
    public class MyAdapter extends ArrayAdapter<String> {
        public MyAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // see if we need to load more to get 40, otherwise populate the adapter
            if (position > getCount() - 4)
                loadMore();

            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.google_image, null);

            // find the image view
            final ImageView iv = (ImageView) convertView.findViewById(R.id.image);

            // select the image view
            Ion.with(iv)
                    .resize(256, 256)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher)
                    .error(R.drawable.ic_launcher)
                    .load(getItem(position));

            return convertView;
        }
    }
    public void loadMore() {/*
    	SyndFeed feed =  input.build( myRSSFile );
    	List entries = feed.getEntries();
    	for( int i = 0; i < entries.size(); i++ ){
    	    System.out.println( ((SyndEntry) entries.get(i)).getModule( MediaModule.URI ) );
    	}*/
        if (mediaCursor == null) {
            mediaCursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), null, null, null, null);
        }

        int loaded = 0;
        while (mediaCursor.moveToNext() && loaded < 10) {
            // get the media type. ion can show images for both regular images AND video.
            int mediaType = mediaCursor.getInt(mediaCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
            if (mediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                && mediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                continue;
            }

            loaded++;
            String devArtUri = "http://backend.deviantart.com/rss.xml?type=deviation&q=by%3Aspyed+sort%3Atime+meta%3Aall";
            String uri = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            File file = new File(uri);
            // turn this into a file uri if necessary/possible
            if (file.exists())
                mAdapter.add(file.toURI().toString());
            else
                mAdapter.add(uri);
        }
    }
}

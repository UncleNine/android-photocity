/* Copyright 2010, Google, Inc.
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.photocitygame.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.photocity.ImageUploader;
import com.google.android.photocity.ParcelableFlag;
import com.google.photocity.Flag;
import com.google.photocity.Model;
import com.google.photocity.User;
import com.google.photocity.Zone;

public class ImageActivity extends Activity {
	public static final int TAKE_IMAGE = 0;
	public static final int UPLOAD_IMAGE = 1;
	ImageView imageView;
	Flag flag;
	Model model;
	Zone zone;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);
        imageView = (ImageView)findViewById(R.id.imageview);
        ImageButton capture = (ImageButton)findViewById(R.id.capture);
        capture.setImageResource(android.R.drawable.ic_menu_camera);
        capture.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doCapture();
			}
        });
        String url;
        flag = (ParcelableFlag)getIntent().getExtras().getParcelable(PhotoCity.FLAG);
        model = (Model)getIntent().getExtras().getParcelable(PhotoCity.MODEL);
        zone = (Zone)getIntent().getExtras().getParcelable(PhotoCity.ZONE);
        HttpClient client = new DefaultHttpClient();
        
        TableLayout scoreTable = (TableLayout)findViewById(R.id.score_table);
        
        if (flag != null) {
        	if (zone != null) {
        		model = zone.getModelById(flag.getModelId());
        	}
        	url = PhotoCity.api().getFlagImageUrl(flag);
        	for (Entry<String, Integer> entry : flag.getScores()) {
        		addScoreRow(scoreTable, entry.getKey(), entry.getValue());
        	}
        } else {
        	url = PhotoCity.api().getModelImageUrl(model);
        }
        if (model != null) {
        	setTitle(model.getName());
        } else {
        	setTitle("Details");
        }
    	HttpGet get = new HttpGet(url);
    	try {
    		HttpResponse response = client.execute(get);
    		Bitmap bm = BitmapFactory.decodeStream(response.getEntity().getContent());
    		imageView.setImageBitmap(bm);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
    private void addScoreRow(TableLayout scoreTable, String team, int score) {
    	TableRow row = new TableRow(this);
    	TextView tv = new TextView(this);
    	tv.setText(team);
    	row.addView(tv);
    	TextView scoreView = new TextView(this);
    	scoreView.setText(Integer.toString(score));
    	scoreView.setGravity(Gravity.RIGHT);
    	row.addView(scoreView);
    	scoreTable.addView(row);
    }
    
    public static final int MENU_CAMERA = 0;
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_CAMERA, 0, "Capture a new image").setIcon(android.R.drawable.ic_menu_camera);
		return true;
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_CAMERA:
			doCapture();
			return true;
		default:
			return false;
		}
	}

	protected void doCapture() {
		File f = Environment.getExternalStorageDirectory();
		File dir = new File(f, "photocity");
		if (!dir.exists()) {
			dir.mkdir();
		}
		Uri captureUri = Uri.fromFile(new File(dir, "image.jpg"));
		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		i.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
		startActivityForResult(i, TAKE_IMAGE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_IMAGE) {
			if (PasswordActivity.hasUser()) {
				uploadPhoto(PasswordActivity.getUser());
			} else {
				PasswordActivity.getUser(this, UPLOAD_IMAGE);
			}
		} else if (requestCode == UPLOAD_IMAGE) {
			uploadPhoto(PasswordActivity.getUser());
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	protected void uploadPhoto(User user) {
		if (user == null) {
			Log.e("PhotoCity", "No user!");
			Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show();
			return;
		}
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Uploading");
		File f = Environment.getExternalStorageDirectory();
		File dir = new File(f, "photocity");
		try {
			FileInputStream in = new FileInputStream(new File(dir, "image.jpg"));
			ImageUploader uploader = new ImageUploader(this, in, user.getUserId(), flag, model);
			uploader.setProgressDialog(dialog);
			new Thread(uploader).start();
		} catch (FileNotFoundException ex) {
			Log.e("PhotoCity", "Error Uploading Image", ex);
			Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show();
		}
	}
}

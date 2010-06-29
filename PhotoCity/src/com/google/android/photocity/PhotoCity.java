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
package com.google.android.photocity;

import java.io.IOException;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.photocity.PhotoCityAPI;
import com.google.photocity.UWPhotoCityApi;
import com.google.photocity.User;
import com.google.photocity.Zone;

public class PhotoCity extends MapActivity implements LocationListener {
	static final String ZONE = "zone";
	static final String FLAG = "flag";
	static final String MODEL = "model";
	static final String USER = "user";
	static final String PASSWORD = "pass";
	static final String HAS_RUN = "has_run";
	
	static final String PREFS_FILE = "PhotoCityPreferences";
	
	static final int MENU_REFRESH = 0;
	static final int MENU_MY_LOCATION = 1;
	static final int MENU_LOGIN = 2;
	static final int MENU_MY_PHOTOS = 3;
	static final int MENU_ABOUT = 4;
	static final int MENU_HELP = 5;
	
	static final int[] validZones = new int[] {1, 2, 3, 5, 9, 10, 11, 13, 14, 18, 19, 20};
	static PhotoCityAPI api = null;
	
	MapView mapView;
	ImageOverlay overlay;
	List<Zone> zones;
	
	String user;
	String password;
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zonelayout);
		mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.getController().setZoom(3);

        SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
        String user = settings.getString(USER, "");
        String password = settings.getString(PASSWORD, "");
        
        Log.e("PhotoCity", "Found: " + user + ", " + password);
        
        if (user.length() > 0) {      
        	PasswordActivity.authenticateAsync(user, password, null, new PasswordActivity.AuthenticationListener() {
        		public void authenticationFailed(User user, Exception ex) {
        			String error = "Cached login failed";
        			if (ex != null) {
        				Log.e("PhotoCity", error, ex);
        			}
        			PasswordActivity.setUser(null);
        			Log.e("PhotoCity", error);
        		}
        		public void authenticationSuccess(User user) {
        			PasswordActivity.setUser(user);
        		}
        	});
        }
        if (ZoneList.instance().needsUpdate()) {
        	reloadZonesAsync();
        } else {
        	zones = ZoneList.instance().getZoneList();
        	drawZones();
        }
        if (!hasRun()) {
        	Intent i = new Intent(this, HelpActivity.class);
        	i.putExtra(HelpActivity.URL, "file:///android_asset/howto.html");
        	startActivity(i);
        	setHasRun();
        }
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		saveUserAndPassword();
	}
	
	protected boolean hasRun() {
		SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
		return settings.getBoolean(HAS_RUN, false);
	}
	
	protected void setHasRun() {
		SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(HAS_RUN, true);
	}
	
	protected void saveUserAndPassword() {
		User user = PasswordActivity.getUser();
		SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
		SharedPreferences.Editor editor = settings.edit();
		if (user != null) {
			editor.putString(USER, user.getUsername());
			editor.putString(PASSWORD, user.getPassword());
		} else {
			editor.remove(USER);
			editor.remove(PASSWORD);
		}
		editor.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_REFRESH, 0, "Refresh").setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(0, MENU_MY_LOCATION, 0, "My Location").setIcon(android.R.drawable.ic_menu_compass);
		menu.add(0, MENU_LOGIN, 0, "Login");
		menu.add(0, MENU_MY_PHOTOS, 0, "My Photos").setIcon(android.R.drawable.ic_menu_camera);
		menu.add(0, MENU_ABOUT, 0, "About").setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, MENU_HELP, 0, "Help").setIcon(android.R.drawable.ic_menu_help);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean loggedIn = PasswordActivity.getUser() != null;
		MenuItem loginItem = menu.findItem(MENU_LOGIN);
		if (loginItem != null) {
			loginItem.setTitle(loggedIn ? "Logout" : "Login");
		}
		MenuItem photoItem = menu.findItem(MENU_MY_PHOTOS);
		if (photoItem != null) {
			photoItem.setEnabled(loggedIn);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH:
			reloadZonesAsync();
			return true;
		case MENU_MY_LOCATION:
			moveToMyLocation();
			return true;
		case MENU_LOGIN:
			if (PasswordActivity.getUser() != null) {
				PasswordActivity.clearUser();
				saveUserAndPassword();
				Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT);
			} else {
				PasswordActivity.getUser(this, MENU_LOGIN);
			}
			return true;
		case MENU_MY_PHOTOS:
			gotoPhotos(this);
			return true;
		case MENU_ABOUT:
			Intent i = new Intent(this, AboutActivity.class);
			this.startActivity(i);
		case MENU_HELP:
			Intent help = new Intent(this, HelpActivity.class);
			this.startActivity(help);
		default:
			return false;
		}
	}
	
	public static PhotoCityAPI api() {
		if (api == null) {
			api = new UWPhotoCityApi();
		}
		return api;
	}
	
	private void gotoPhotos(Context context) {
		User user = PasswordActivity.getUser();
		if (user != null) {
			gotoUrl(Uri.parse(api().userStatusUrl(user)), context);
		}
	}
		
	public static void gotoUrl(Uri url, Context context) {
		Intent i = new Intent(Intent.ACTION_VIEW, url);
		context.startActivity(i);
	}

	protected static void executeAsync(final Runnable runner, Context context) {
		final ProgressDialog dialog = new ProgressDialog(context);
		dialog.setIndeterminate(true);
		dialog.setMessage("Loading");
		dialog.setCancelable(false);
		dialog.show();
		Thread t = new Thread() {
			public void run() {
				runner.run();
				dialog.dismiss();
			}
		};
		t.start();
	}
	
	protected void reloadZonesAsync() {
		executeAsync(new Runnable() {
							public void run() {
								reloadZones();
							}
					 }, this);
	}
	
	protected void reloadZones() {
		try {
			zones = ZoneList.instance().loadZones();
			drawZones();
		} catch (IOException ex) {
			this.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(PhotoCity.this, "Failed to load data from PhotoCity.", Toast.LENGTH_LONG);
				}
			});
			Log.e("PhotoCity", "Error loading zones", ex);
		}
	}
	
	protected void drawZones() {
        TapHandler<Zone> handler = new TapHandler<Zone>() {
			public void onTap(Zone z) {
				Intent i = new Intent(PhotoCity.this, ZoneView.class);
				i.putExtra(PhotoCity.ZONE, new ParcelableZone(z));
				startActivity(i);
			}
		};
		List<Overlay> overlays = mapView.getOverlays();
		if (overlay != null) {
			overlays.remove(overlay);
		}
			
		if (zones != null && zones.size() > 0) {
			Drawable whiteFlag = getResources().getDrawable(R.drawable.flag_white);
			overlay = new ImageOverlay(whiteFlag, handler, false, this);
			for (int i = 0; i < zones.size(); ++i) {
				Zone z = zones.get(i);
				String name = z.getName();
				((TapHandler<Zone>)overlay.getTapHandler()).addElement(z);
				
				GeoPoint location = new GeoPoint(z.getLocation().getLatitudeE6(), z.getLocation().getLongitudeE6());
				overlay.addOverlayItem(new OverlayItem(location, name, name));
			}
			overlays.add(overlay);
		}
		mapView.getController().setZoom(3);
	}	
		
	protected void moveToMyLocation() {
		GeoPoint loc = LocationSource.getGeoPointLocation(this);
		
		if (loc != null) {
			mapView.getController().animateTo(loc);
			mapView.getController().setZoom(10);
		}
	}
	
	protected void onResume() {
		super.onResume();
		if (mapView != null) {
			mapView.getController().setZoom(3);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MENU_LOGIN) {
			if (data != null) {
				String msg;
				if (data.hasExtra(PasswordActivity.USER)) {
					msg = "Logged in " + data.getExtras().getString(PasswordActivity.USER);
				} else {
					msg = "Login failed.";
				}
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
				Log.e("PhotoCity", msg);
			} // otherwise, user cancelled, so do nothing.
		} else {
			Log.e("PhotoCity", "Unknown request: " + requestCode);
		}
	}
	
	protected void removeMe() {
		LocationManager locationMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		if (locationMgr == null) {
			return;
		}
		locationMgr.removeUpdates(this);
	}
	
	public void onLocationChanged(Location location) {
		GeoPoint point = new GeoPoint((int)(location.getLatitude() * 1000000),
				(int)(location.getLongitude() * 1000000));
		mapView.getController().animateTo(point);
		mapView.getController().setZoom(10);
		removeMe();
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}

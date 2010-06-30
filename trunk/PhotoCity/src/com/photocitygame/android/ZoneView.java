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

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.photocity.ImageOverlay;
import com.google.android.photocity.LocationSource;
import com.google.android.photocity.ParcelableFlag;
import com.google.android.photocity.ParcelableModel;
import com.google.android.photocity.ParcelableZone;
import com.google.android.photocity.TapHandler;
import com.google.android.photocity.ZoneList;
import com.google.photocity.Flag;
import com.google.photocity.Model;
import com.google.photocity.User;
import com.google.photocity.Zone;

public class ZoneView extends MapActivity {
	static final int MENU_ACTIVITY = 0;

	MapView mapView;
	Zone zone;
	
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
        
        Zone z = (Zone)getIntent().getExtras().getParcelable(PhotoCity.ZONE);
        zone = ZoneList.instance().getZone(z.getId());
        if (zone != null) {
        	setTitle(zone.getName());
        	GeoPoint location = new GeoPoint(zone.getLocation().getLatitudeE6(), zone.getLocation().getLongitudeE6());
        	mapView.getController().animateTo(location);
        	mapView.getController().setZoom(zone.getZoom() - 1);
        	if (zone.needsUpdate()) {
        		PhotoCity.executeAsync(new Runnable() {
        			public void run() {
        				try {
        					loadZone();
        				} catch (IOException ex) {
        					Log.e("PhotoCity", "Error updating zone info", ex);
        				}
        			}
        		}, this);
        	} else {
        		drawZone();
        	}
        } else {
        	Log.e("PhotoCity", "Can't find zone: " + z.getName() + "(" + z.getId() + ")");
        }
	}
	
	protected void loadZone() throws IOException {
		PhotoCity.api().loadInfo(zone);
		drawZone();
	}
	
	protected void drawZone() {
		List<Overlay> overlays = mapView.getOverlays();
		if (zone.getModelCount() > 0) {
			int[] resourceIds = new int[] {
					R.drawable.white, R.drawable.red, R.drawable.blue, R.drawable.yellow, R.drawable.green};
			ImageOverlay[] overlayArray = new ImageOverlay[resourceIds.length];
			for (int i = 0; i < resourceIds.length; ++i) {
				overlayArray[i] = new ImageOverlay(getResources().getDrawable(resourceIds[i]),
					new TapHandler<Model>() {
        				public void onTap(Model m) {
        					Intent i = new Intent(ZoneView.this, ImageActivity.class);
        					i.putExtra(PhotoCity.MODEL, new ParcelableModel(m));
        					i.putExtra(PhotoCity.ZONE, new ParcelableZone(zone));
        					startActivity(i);
        				}
        			});
			}
			for (int i = 0; i < zone.getModelCount(); ++i) {
				Model m = zone.getModel(i);
				int ix = m.getOwner() != null ? m.getOwner().getTeamId() : 0;
				ImageOverlay modelOverlay = overlayArray[ix];
				((TapHandler<Model>)modelOverlay.getTapHandler()).addElement(m);
				GeoPoint location = new GeoPoint(m.getLocation().getLatitudeE6(), m.getLocation().getLongitudeE6());
				modelOverlay.addOverlayItem(new OverlayItem(location, m.getName(), m.getName()));
			}
			for (int i = 0; i < overlayArray.length; ++i) {
				if (overlayArray[i].size() > 0) {
					overlays.add(overlayArray[i]);
				}
			}
		}
		if (zone.getFlagCount() > 0) {
			int[] resourceIds = new int[] {
					R.drawable.flag_white, R.drawable.flag_red, R.drawable.flag_blue, R.drawable.flag_yellow, R.drawable.flag_green};
        	ImageOverlay[] overlayArray = new ImageOverlay[resourceIds.length];
        	for (int i = 0; i < resourceIds.length; ++i) {
        		overlayArray[i] = new ImageOverlay(getResources().getDrawable(resourceIds[i]), 
        			new TapHandler<Flag>() {
        				public void onTap(Flag f) {
        					Intent i = new Intent(ZoneView.this, ImageActivity.class);
        					i.putExtra(PhotoCity.FLAG, new ParcelableFlag(f));
        					i.putExtra(PhotoCity.ZONE, new ParcelableZone(zone));
        					startActivity(i);
        				}
        			});
        	}
        	for (int i = 0; i < zone.getFlagCount(); ++i) {
        		Flag f = zone.getFlag(i);
        		String name = "Flag " + f.getId();
        		int ix = f.getWinningTeamId();
        		if (ix >= resourceIds.length) {
        			ix = 0;
        		}
        		ImageOverlay overlay = overlayArray[f.getWinningTeamId()];
				((TapHandler<Flag>)overlay.getTapHandler()).addElement(f);
				GeoPoint location = new GeoPoint(f.getLocation().getLatitudeE6(), f.getLocation().getLongitudeE6());
        		overlay.addOverlayItem(new OverlayItem(location, name, name));
        	}
        	for (int i = 0; i < overlayArray.length; ++i) {
        		if (overlayArray[i].size() > 0) {
        			overlays.add(overlayArray[i]);
        		}
        	}
        }
		mapView.getController().setZoom(zone.getZoom());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ACTIVITY, 0, "Recent Activity").setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, PhotoCity.MENU_MY_PHOTOS, 0, "My Photos").setIcon(android.R.drawable.ic_menu_camera);
		menu.add(0, PhotoCity.MENU_HELP, 0, "Help").setIcon(android.R.drawable.ic_menu_help);
		menu.add(0, PhotoCity.MENU_MY_LOCATION, 0, "My Location").setIcon(android.R.drawable.ic_menu_compass);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean loggedIn = PasswordActivity.getUser() != null;
		MenuItem photoItem = menu.findItem(PhotoCity.MENU_MY_PHOTOS);
		if (photoItem != null) {
			photoItem.setEnabled(loggedIn);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ACTIVITY:
			Uri url = Uri.parse(PhotoCity.api().getRecentActivityURL(zone));
			PhotoCity.gotoUrl(url, this);
			return true;
		case PhotoCity.MENU_MY_PHOTOS:
			User user = PasswordActivity.getUser();
			if (user != null) {
				PhotoCity.gotoUrl(Uri.parse(PhotoCity.api().userStatusUrl(user, zone)), this);
			}
			return true;
		case PhotoCity.MENU_HELP:
			Intent help = new Intent(this, HelpActivity.class);
			this.startActivity(help);
		case PhotoCity.MENU_MY_LOCATION:
			GeoPoint loc = LocationSource.getGeoPointLocation(this);
			if (loc != null) {
				mapView.getController().animateTo(loc);
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

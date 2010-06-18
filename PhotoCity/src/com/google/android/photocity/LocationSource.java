package com.google.android.photocity;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class LocationSource {
	static Location getLocation(Context context) {
		LocationManager loc = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		if (loc == null) {
			return null;
		}
		Location myLocation = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		return myLocation;
	}
	
	static GeoPoint getGeoPointLocation(Context context) {
		Location myLocation = getLocation(context);
		if (myLocation != null) {
			return new GeoPoint((int)(myLocation.getLatitude() * 1000000), (int)(myLocation.getLongitude() * 1000000));
		} else {
			return null;
		}
		
	}
}

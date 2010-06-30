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

import android.util.Log;

import com.google.photocity.Zone;
import com.photocitygame.android.PhotoCity;

public class ZoneList {
    List<Zone> zoneList;
    long lastUpdated;
    
    static ZoneList instance;
    
    private ZoneList() {
    }
    
    public static ZoneList instance() {
    	if (instance == null) {
    		instance = new ZoneList();
    	}
    	return instance;
    }
    
    public boolean needsUpdate() {
    	return ((System.currentTimeMillis() - lastUpdated) / (10 * 60 * 1000)) > 5;
    }
    
    public List<Zone> getZoneList() {
    	return zoneList;
    }
    
    public Zone getZone(int id) {
    	if (zoneList == null) {
    		try {
    			loadZones();
    		} catch (IOException ex) {
    			Log.e("PhotoCity", "Error loading zones", ex);
    		}
    	}
    	if (zoneList == null) {
    		return null;
    	}
    	for (Zone z : zoneList) {
    		if (z.getId() == id) {
    			return z;
    		}
    	}
    	return null;
    }
    
    public List<Zone> loadZones() throws IOException {
    	zoneList = PhotoCity.api().loadZones();
    	lastUpdated = System.currentTimeMillis();
    	return zoneList;
    }
}

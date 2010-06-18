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
package com.google.photocity;

import java.util.ArrayList;
import java.util.List;

public class Zone {
	protected List<Flag> flags;
	protected List<Model> models;
	protected String name;
	protected Location location;
	protected int zoom;
	protected int zoneId;
	protected long lastModified;
	protected boolean fullyLoaded;
	
	public Zone(int id) {
		flags = new ArrayList<Flag>();
		models = new ArrayList<Model>();
		zoneId = id;
		lastModified = System.currentTimeMillis();
		fullyLoaded = false;
	}
	
	public boolean needsUpdate() {
		if (!fullyLoaded) {
			return true;
		}
		int ageMinutes = (int)(System.currentTimeMillis() - lastModified) / (60 * 1000);
		return ageMinutes > 5;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location point) {
		this.location = point;
	}

	public int getZoom() {
		return zoom;
	}
	
	public void setZoom(int zoom) {
		this.zoom = zoom;
	}
	

	public int getId() {
		return zoneId;
	}
	
	public int getFlagCount() {
		return flags.size();
	}
	
	public Flag getFlag(int i) {
		return flags.get(i);
	}
	
	public int getModelCount() {
		return models.size();
	}
	
	public Model getModel(int i) {
		return models.get(i);
	}
	
	public Model getModelById(int id) {
		for (Model m : models) {
			if (m.getId() == id) {
				return m;
			}
		}
		return null;
	}
	
	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isFullyLoaded() {
		return fullyLoaded;
	}

	public void setFullyLoaded(boolean fullyLoaded) {
		this.fullyLoaded = fullyLoaded;
	}

	public void clearFlags() {
		flags.clear();
	}
	
	public void addFlag(Flag f) {
		flags.add(f);
	}
	
	public void clearModels() {
		models.clear();
	}
	
	public void addModel(Model m) {
		models.add(m);
	}
}

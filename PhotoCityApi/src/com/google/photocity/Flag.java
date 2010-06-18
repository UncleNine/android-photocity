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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Flag {
	protected Location location;
	protected int id;
	protected int winningTeamId;
	protected int modelId;

	protected boolean disputed;
	protected Map<String, Integer> teamScores;
	
	public Flag(int id, Location location, boolean disputed, int modelId, int winningTeamId) {
	  this.disputed = disputed;
	  this.id = id;
	  this.location = location;
	  this.modelId = modelId;
	  this.winningTeamId = winningTeamId;
	  this.teamScores = new HashMap<String, Integer>();
	}
	
	public void addScore(String name, int score) {
		teamScores.put(name, score);
	}
	
	public int getScore(String name) {
		if (teamScores.containsKey(name)) {
			return teamScores.get(name);
		} else {
			return 0;
		}
	}
	
	public Set<Entry<String, Integer>> getScores() {
		return teamScores.entrySet();
	}
	
	public Location getLocation() {
		return location;
	}

	public int getId() {
		return id;
	}

	public int getWinningTeamId() {
		return winningTeamId;
	}

	public int getModelId() {
		return modelId;
	}

	public boolean isDisputed() {
		return disputed;
	}

	public int describeContents() {
		return 0;
	}

	public Map<String, Integer> getTeamScores() {
		return teamScores;
	}
}

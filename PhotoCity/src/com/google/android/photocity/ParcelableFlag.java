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

import java.util.HashMap;
import java.util.Map.Entry;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.photocity.Flag;
import com.google.photocity.Location;

public class ParcelableFlag extends Flag implements Parcelable {
	public ParcelableFlag(int id, Location location, boolean disputed, int modelId, int winningTeamId) {
		super(id, location, disputed, modelId, winningTeamId);
	}
	
	public ParcelableFlag(Flag flag) {
		super(flag.getId(), flag.getLocation(), flag.isDisputed(), flag.getModelId(), flag.getWinningTeamId());
		for (Entry<String, Integer> entry : flag.getTeamScores().entrySet()) {
			addScore(entry.getKey(), entry.getValue());
		}
	}
	
	public ParcelableFlag(Parcel in) {
		super(-1, null, false, -1, -1);
		this.disputed = in.readByte() == 1;
		this.id = in.readInt();
		int lat = in.readInt();
		int lon = in.readInt();
		this.location = new Location(lat, lon);
		this.modelId = in.readInt();
		this.winningTeamId = in.readInt();
		
		this.teamScores = new HashMap<String, Integer>();
		int size = in.readInt();
		for (int i = 0; i < size; ++i) {
			String name = in.readString();
			int score = in.readInt();
			addScore(name, score);
		}
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeByte(disputed ? (byte)1 : (byte)0);
		out.writeInt(id);
		out.writeInt(location.getLatitudeE6());
		out.writeInt(location.getLongitudeE6());
		out.writeInt(modelId);
		out.writeInt(winningTeamId);
		out.writeInt(teamScores.size());
		for (Entry<String, Integer> entry : teamScores.entrySet()) {
			out.writeString(entry.getKey());
			out.writeInt(entry.getValue());
		}
	}
	
	public static final Parcelable.Creator<ParcelableFlag> CREATOR =
		new Parcelable.Creator<ParcelableFlag>() {
		public ParcelableFlag createFromParcel(Parcel in) {
			return new ParcelableFlag(in);
		}
		
		public ParcelableFlag[] newArray(int size) {
			return new ParcelableFlag[size];
		}
	};
}

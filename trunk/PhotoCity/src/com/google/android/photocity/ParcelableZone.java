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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.photocity.Flag;
import com.google.photocity.Location;
import com.google.photocity.Model;
import com.google.photocity.Zone;

public class ParcelableZone extends Zone implements Parcelable {
	public ParcelableZone(Zone z) {
		super(z.getId());
		for (int i = 0; i < z.getFlagCount(); ++i) {
			addFlag(z.getFlag(i));
		}
		for (int i = 0; i < z.getModelCount(); ++i) {
			addModel(z.getModel(i));
		}
		setName(z.getName());
		setZoom(z.getZoom());
		setLocation(z.getLocation());
	}

	public ParcelableZone(Parcel in) {
		super(in.readInt());
		Parcelable[] flagArray = in.readParcelableArray(ParcelableFlag.class.getClassLoader());
		for (int i = 0; i < flagArray.length; ++i) {
			flags.add((Flag)flagArray[i]);
		}
		Parcelable[] modelArray = in.readParcelableArray(ParcelableModel.class.getClassLoader());
		for (int i = 0; i < modelArray.length; ++i) {
			models.add((Model)modelArray[i]);
		}
		this.name = in.readString();
		this.zoom = in.readInt();
		if (in.readByte() != 0) {
			int lat = in.readInt();
			int lon = in.readInt();
			this.location = new Location(lat, lon);
		}
	}

	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flgs) {
		ParcelableFlag[] flagArray = new ParcelableFlag[flags.size()];
		for (int i = 0; i < flagArray.length; ++i) {
			flagArray[i] = new ParcelableFlag(flags.get(i));
		}
		ParcelableModel[] modelArray = new ParcelableModel[models.size()];
		for (int i = 0; i < modelArray.length; ++i) {
			modelArray[i] = new ParcelableModel(models.get(i));
		}
		
		dest.writeInt(zoneId);
		dest.writeParcelableArray(flagArray, flgs);
		dest.writeParcelableArray(modelArray, flgs);
		dest.writeString(name);
		dest.writeInt(zoom);
		if (location != null) {
			dest.writeByte((byte)1);
			dest.writeInt(location.getLatitudeE6());
			dest.writeInt(location.getLongitudeE6());
		} else {
			dest.writeByte((byte)0);
		}
	}
	
	public static final Parcelable.Creator<ParcelableZone> CREATOR =
		new Parcelable.Creator<ParcelableZone>() {
		public ParcelableZone createFromParcel(Parcel in) {
			return new ParcelableZone(in);
		}
		
		public ParcelableZone[] newArray(int size) {
			return new ParcelableZone[size];
		}
	};
}

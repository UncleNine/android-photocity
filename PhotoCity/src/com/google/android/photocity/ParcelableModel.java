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

import com.google.photocity.Location;
import com.google.photocity.Model;
import com.google.photocity.Owner;

public class ParcelableModel extends Model implements Parcelable {
	public ParcelableModel(int id, Location location, String name, Owner owner) {
		super(id, location, name, owner);
	}
	
	public ParcelableModel(Model m) {
		super(m.getId(), m.getLocation(), m.getName(), m.getOwner());
	}

	public int describeContents() {
		return 0;
	}

	public ParcelableModel(Parcel in) {
		int lat = in.readInt();
		int lon = in.readInt();
		this.location = new Location(lat, lon);
		this.name = in.readString();
		this.id = in.readInt();
		if (in.readByte() != 0) {
			this.owner = (ParcelableOwner)in.readParcelable(ParcelableOwner.class.getClassLoader());
		}
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(location.getLatitudeE6());
		dest.writeInt(location.getLongitudeE6());
		dest.writeString(name);
		dest.writeInt(id);
		if (owner != null) {
			dest.writeByte((byte)1);
			dest.writeParcelable(new ParcelableOwner(owner), flags);
		} else {
			dest.writeByte((byte)0);
		}
	}
	
	public static final Parcelable.Creator<ParcelableModel> CREATOR =
		new Parcelable.Creator<ParcelableModel>() {
		public ParcelableModel createFromParcel(Parcel in) {
			return new ParcelableModel(in);
		}
		
		public ParcelableModel[] newArray(int size) {
			return new ParcelableModel[size];
		}
	};
}

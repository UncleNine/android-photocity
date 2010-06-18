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

public class Location {
	int latE6;
	int lonE6;
	
	public Location(int latE6, int lonE6) {
		this.latE6 = latE6;
		this.lonE6 = lonE6;
	}
	
	public int getLatitudeE6() {
		return latE6;
	}
	
	public int getLongitudeE6() {
		return lonE6;
	}
}

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


public class Model {
	protected Location location;
	protected int id;
	protected String name;
	protected Owner owner;
	
	public Model() {
		this(-1, null, null, null);
	}
	
	public Model(int id, Location location, String name, Owner owner) {
	  this.id = id;
	  this.location = location;
	  this.name = name;
	  this.owner = owner;
	}
	
	public Owner getOwner() {
		return owner;
	}
	
	public Location getLocation() {
		return location;
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}

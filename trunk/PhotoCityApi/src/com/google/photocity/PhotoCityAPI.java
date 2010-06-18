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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface PhotoCityAPI {
	public void loadInfo(Zone z) throws IOException;
	public List<Zone> loadZones() throws IOException;
	public boolean authenticate(User user) throws IOException;
	public void uploadImage(InputStream image, Flag flag, Model model, int user_id) throws IOException;
	
	public String newAccountUrl();
	public String getRecentActivityURL(Zone zone);
	public String userStatusUrl(User user);
	public String userStatusUrl(User user, Zone zone);
	public String getFlagImageUrl(Flag flag);
	public String getModelImageUrl(Model model);
}

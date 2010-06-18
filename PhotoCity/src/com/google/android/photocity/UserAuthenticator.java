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

import android.app.ProgressDialog;

import com.google.photocity.User;

public class UserAuthenticator implements Runnable {
	String username;
	String password;
	PasswordActivity.AuthenticationListener listener;
	ProgressDialog dialog;
	
	public UserAuthenticator(String username, String password, PasswordActivity.AuthenticationListener listener) {
		this.username = username;
		this.password = password;
		this.listener = listener;
	}
	
	public void setProgressDialog(ProgressDialog dialog) {
		this.dialog = dialog;
	}
	
	public void run() {
		User user = new User(username, password, -1);
		Exception error = null;
		boolean success = false;
		try {
			success = PhotoCity.api().authenticate(user);
		} catch (IOException ex) {
			error = ex;
		}
    	if (dialog != null) {
    		dialog.dismiss();
    	}
    	if (!success) {
			listener.authenticationFailed(user, error);
		} else {
			listener.authenticationSuccess(user);
		}
	}
}

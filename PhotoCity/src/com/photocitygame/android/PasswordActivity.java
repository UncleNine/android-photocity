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
package com.photocitygame.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.photocity.UserAuthenticator;
import com.google.photocity.User;

public class PasswordActivity extends Activity implements OnClickListener {
	public static interface AuthenticationListener {
		public void authenticationFailed(User user, Exception ex);
		public void authenticationSuccess(User user);
	}

	public static final String USER = "user";
	public static final String PASSWORD = "password";
	public static final String FAILED = "failed";
	static User user = null;
	static Object userLock = new Object();
	
	EditText username;
	EditText password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password);
		username = (EditText)this.findViewById(R.id.user);
		password = (EditText)this.findViewById(R.id.password);
		Button ok = (Button)this.findViewById(R.id.login);
		ok.setOnClickListener(this);
		
		Button cancel = (Button)this.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Uri url = Uri.parse(PhotoCity.api().newAccountUrl());
				PhotoCity.gotoUrl(url, PasswordActivity.this);
			}
		});
	}

	public void onClick(View arg0) {
		String userName = username.getText().toString();
		String pass = password.getText().toString();
		
		authenticateAsync(userName, pass, this, new AuthenticationListener() {
			public void authenticationSuccess(User user) {
				setUser(user);
				
				Intent i = getIntent();
				i.putExtra(USER, user.getUsername());
				i.putExtra(PASSWORD, user.getPassword());
				setResult(RESULT_OK, i);
				
				Log.e("PhotoCity", "Authentication suceeded for: " + user.getUsername());
				finish();
			}
			
			public void authenticationFailed(User user, Exception ex) {
				if (ex != null) {
					Log.e("PhotoCity", "Authentication Failed.", ex);
				}
				Intent i = getIntent();
				i.putExtra(FAILED, true);
				setResult(RESULT_OK, i);
				finish();
			}
		});
	}
		
	public static void authenticateAsync(String user, String password, Context context, PasswordActivity.AuthenticationListener listener) {
		UserAuthenticator auth = new UserAuthenticator(user, password, listener);
		if (context != null) {
			ProgressDialog dialog = new ProgressDialog(context);
			dialog.setMessage("Logging In.");
			dialog.show();
			auth.setProgressDialog(dialog);
		}
		new Thread(auth).start();
		Log.e("PhotoCity", "Authentication started for: " + user);
	}
	
	public static boolean hasUser() {
		synchronized (userLock) {
			return user != null;
		}
	}
	
	public static User getUser() {
		synchronized (userLock) {
			return user;
		}
	}
	
	public static void getUser(Activity context, int requestCode) {
		Intent i = new Intent(context, PasswordActivity.class);
		context.startActivityForResult(i, requestCode);
	}
	
	public static void setUser(User user) {
		synchronized (userLock) {
			PasswordActivity.user = user;
		}
	}
	
	public static void clearUser() {
		synchronized (userLock) {
			user = null;
		}
	}
}

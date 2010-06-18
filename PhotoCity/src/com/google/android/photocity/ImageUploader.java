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
import java.io.InputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.google.photocity.Flag;
import com.google.photocity.Model;

public class ImageUploader implements Runnable {
	protected Context context;
	protected InputStream image;
	protected int user_id;
	protected Flag flag;
	protected Model model;
	protected ProgressDialog dialog;
	
	public ImageUploader(Context context, InputStream image, int user_id, Flag flag, Model model) {
		this.context = context;
		this.image = image;
		this.user_id = user_id;
		this.flag = flag;
		this.model = model;
	}
	
	public void setProgressDialog(ProgressDialog dialog) {
		this.dialog = dialog;
	}
	
	public void run() {
		try {
			PhotoCity.api().uploadImage(image, flag, model, user_id);
		} catch (IOException ex) {
			Toast.makeText(context, "Error uploading image.", Toast.LENGTH_LONG);
		}
		if (dialog != null) {
			dialog.dismiss();
		}
	}

}

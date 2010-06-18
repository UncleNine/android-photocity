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

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class ImageOverlay extends ItemizedOverlay<OverlayItem> {
	List<OverlayItem> overlayItems;
	Context context;
	TapHandler<?> handler;
	boolean nearest;
	
	public ImageOverlay(Drawable d, TapHandler<?> handler) {
		this(d, handler, true, null);
	}
	
	public ImageOverlay(Drawable d, TapHandler<?> handler, boolean nearest, Context context) {
		super(boundCenterBottom(d));
		overlayItems = new ArrayList<OverlayItem>();
		this.handler = handler;
		this.nearest = nearest;
		this.context = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlayItems.get(i);
	}

	@Override
	public int size() {
		return overlayItems.size();
	}
	
	public TapHandler<?> getTapHandler() {
		return handler;
	}
	
	public void addOverlayItem(OverlayItem item) {
		overlayItems.add(item);
		populate();
	}
	
	protected boolean closeEnough(GeoPoint l1, GeoPoint l2, MapView map) {
		double latE6Dist = Math.abs(l1.getLatitudeE6() - l2.getLatitudeE6()) / ((double)map.getLatitudeSpan());
		double lonE6Dist = Math.abs(l1.getLongitudeE6() - l2.getLongitudeE6()) / ((double)map.getLongitudeSpan());
		
		return latE6Dist < 0.05 && lonE6Dist < 0.05;
	}
	
	@Override
	public boolean onTap(GeoPoint where, MapView map) {
		if (nearest) {
			return super.onTap(where, map);
		} else {
			ArrayList<Integer> nearIndices = new ArrayList<Integer>();
			for (int i = 0; i < overlayItems.size(); ++i) {
				if (closeEnough(overlayItems.get(i).getPoint(), where, map)) {
					nearIndices.add(i);
				}
			}
			if (nearIndices.size() > 1) {
				CharSequence[] items = new CharSequence[nearIndices.size()];
				for (int i = 0; i < items.length; ++i) {
					items[i] = overlayItems.get(nearIndices.get(i)).getTitle();
				}
				DialogInterface.OnClickListener clickListener = new IndexClickListener(nearIndices, this);	
				new AlertDialog.Builder(context).setSingleChoiceItems(items, 0, clickListener)
					.setTitle("Select a Zone")
					.setPositiveButton("Select", clickListener)
					.setNegativeButton("Cancel", clickListener)
					.show();
			} else if (nearIndices.size() == 1) {
				onTap(nearIndices.get(0));
			}
		}
		return false;
	}
	
	@Override
	public boolean onTap(int index) {
		if (handler != null) {
			handler.onTap(index);
			return true;
		}
		return false;
	}
	
	private static class IndexClickListener implements DialogInterface.OnClickListener {
		List<Integer> indices;
		ImageOverlay overlay;
		int which;
		
		public IndexClickListener(List<Integer> indices, ImageOverlay overlay) {
			this.indices = indices;
			this.overlay = overlay;
		}

		public void onClick(DialogInterface dialog, int which) {
			if (which == -1) {  // Positive button
				overlay.onTap(indices.get(this.which));
			} else if (which > -1) {  // Selection in list.
				this.which = which;
			}
			// Negative button == -2
		}
	}
}

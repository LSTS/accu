/*
 /*
 * Copyright (C) 2010 Eric Harlow
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ericharlow.DragNDrop;

import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class DragNDropAdapter extends BaseAdapter implements RemoveListener, DropListener{


	public DragNDropAdapter(DragNDropListActivity dragNDropListActivity, int[] ints, int[] ints1, ArrayList<String> content) {

	}

	public abstract void onRemove(int which);

//	public void onDrop(int from, int to) {
//		String temp = mContent.get(from);
//		mContent.remove(from);
//		mContent.add(to,temp);
//	}
	public abstract void onDrop(int from, int to);
}
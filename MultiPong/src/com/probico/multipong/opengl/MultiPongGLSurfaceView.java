/*
 * Copyright (C) 2011 The Android Open Source Project
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
package com.probico.multipong.opengl;

import net.probico.pong.opengl.PongGLRenderer;
import net.probico.pong.opengl.PongGLSurfaceView;
import android.content.Context;

/**
 * A view container where OpenGL ES graphics can be drawn on screen. This view
 * can also be used to capture touch events, such as a user interacting with
 * drawn objects.
 */
public class MultiPongGLSurfaceView extends PongGLSurfaceView {

	public MultiPongGLSurfaceView(Context context) {
		super(context);
	}

	@Override
	public PongGLRenderer getPongGLRenderer(Context context) {
		return new MultiPongGLRenderer(context);
	}

}

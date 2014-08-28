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
import android.content.Context;

import com.probico.multipong.R;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class must
 * override the OpenGL ES drawing lifecycle methods:
 * <ul>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MultiPongGLRenderer extends PongGLRenderer {

	public MultiPongGLRenderer(Context context) {
		super(context);
	}

	@Override
	public int getBackgroundTextureResourceId() {
		return R.drawable.backgroundpattern_space;
	}

	@Override
	public int getPaddleTextureResourceId() {
		return R.drawable.paddle_green;
	}


}
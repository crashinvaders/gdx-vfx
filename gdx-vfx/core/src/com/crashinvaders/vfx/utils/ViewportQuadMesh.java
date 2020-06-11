/*******************************************************************************
 * Copyright 2012 bmanuel
 * Copyright 2019 metaphore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.crashinvaders.vfx.utils;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Encapsulates a fullscreen quad mesh. Geometry is aligned to the viewport corners.
 * 
 * @author bmanuel
 * @author metaphore
 */
public class ViewportQuadMesh {

	private final Mesh mesh;

	public ViewportQuadMesh() {
		this(new VertexAttribute(Usage.Position, 2, "a_position"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord0"));
	}

	public ViewportQuadMesh(VertexAttribute... vertexAttributes) {
		mesh = new Mesh(true, 4, 0, vertexAttributes);
		mesh.setVertices(verts);
	}

	public void dispose () {
		mesh.dispose();
	}

	/**
	 * Renders the quad with the specified shader program.
	 */
	public void render (ShaderProgram program) {
		mesh.render(program, GL20.GL_TRIANGLE_FAN, 0, 4);
	}

	public Mesh getMesh() {
		return mesh;
	}

	private static final int VERT_SIZE = 16;
	private static final int X1 = 0;
	private static final int Y1 = 1;
	private static final int U1 = 2;
	private static final int V1 = 3;
	private static final int X2 = 4;
	private static final int Y2 = 5;
	private static final int U2 = 6;
	private static final int V2 = 7;
	private static final int X3 = 8;
	private static final int Y3 = 9;
	private static final int U3 = 10;
	private static final int V3 = 11;
	private static final int X4 = 12;
	private static final int Y4 = 13;
	private static final int U4 = 14;
	private static final int V4 = 15;
	private static final float[] verts;
	static {
		verts = new float[VERT_SIZE];

		// Vertex coords
		verts[X1] = -1;
		verts[Y1] = -1;

		verts[X2] = 1;
		verts[Y2] = -1;

		verts[X3] = 1;
		verts[Y3] = 1;

		verts[X4] = -1;
		verts[Y4] = 1;

		// Tex coords
		verts[U1] = 0f;
		verts[V1] = 0f;

		verts[U2] = 1f;
		verts[V2] = 0f;

		verts[U3] = 1f;
		verts[V3] = 1f;

		verts[U4] = 0f;
		verts[V4] = 1f;
	}
}

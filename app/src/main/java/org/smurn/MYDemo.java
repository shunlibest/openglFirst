/*
 * Copyright 2011 Stefan C. Mueller.
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
package org.smurn;

import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;
import org.smurn.jply.util.NormalMode;
import org.smurn.jply.util.NormalizingPlyReader;
import org.smurn.jply.util.TesselationMode;
import org.smurn.jply.util.TextureMode;

import java.io.IOException;

/**
 * jPLY Demo: jPLY viewer using JWJGL.
 * 
 * This example is using state-of-the-art vertex-buffer-objects and
 * GLSL shaders instead of the immediate mode rendering often seen in 
 * examples. This makes the code a bit more complicated but its the way
 * this things are done in real-world applications.
 * 
 * The code doesn't perform proper error checking and will just fail if
 * run on a computer with an old graphics-card (or old drivers). This way
 * the code remains much more readable and simple.
 */
public class MYDemo {

    public static void main(String[] args) throws IOException {



        // Open the PLY file
        PlyReader plyReader = new PlyReaderFile(
                ClassLoader.getSystemResourceAsStream("src/main/assets/bunny.ply"));

        // Normalize the data in the PLY file to ensure that we only get
        // triangles and that the vertices have normal vectors assigned.
        plyReader = new NormalizingPlyReader(plyReader,
                TesselationMode.TRIANGLES,
                NormalMode.ADD_NORMALS_CCW,
                TextureMode.PASS_THROUGH);





    }


}

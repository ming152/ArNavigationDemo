package com.example.arnavigationdemo.opengl.mesh;

/**
 * Created by 17111862 on 2018/8/13.
 */

public class IndicatorArrow extends Mesh {
    /**
     * Create a plane with a default with and height of 1 unit.
     */
    public IndicatorArrow() {
        this(1, 1);
    }

    /**
     * Create a plane.
     *
     * @param width
     *            the width of the plane.
     * @param height
     *            the height of the plane.
     */
    public IndicatorArrow(float width, float height) {
        // Mapping coordinates for the vertices
        float textureCoordinates[] = { 0.0f, 1.0f, //
                1.0f, 1.0f, //
                0.0f, 0.0f, //
                1.0f, 0.0f, //
        };

        short[] indices = new short[] { 0, 1, 2, 3, 4, 5 };

        float[] vertices = new float[] {
                -1.0f, 0.0f, 0.0f,
                0.0f, 0.4f, 0.0f,
                1.0f, 0.0f, 0.0f,
                -0.8f, 0.0f, 0.0f,
                0.0f, 0.2f, 0.0f,
                0.8f, 0.0f, 0.0f};

        setIndices(indices);
        setVertices(vertices);
//        setTextureCoordinates(textureCoordinates);
    }
}


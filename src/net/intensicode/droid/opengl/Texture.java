package net.intensicode.droid.opengl;

import net.intensicode.util.Rectangle;

public interface Texture
    {
    int TEXTURE_MATRIX_INDEX_OF_X = 12;
    int TEXTURE_MATRIX_INDEX_OF_Y = 13;
    int TEXTURE_MATRIX_INDEX_OF_WIDTH = 0;
    int TEXTURE_MATRIX_INDEX_OF_HEIGHT = 5;

    void bind();

    void setMatrix( float[] aMatrix4x4, Rectangle aSourceRect );

    void setTextureCrop( Rectangle aRect );
    }

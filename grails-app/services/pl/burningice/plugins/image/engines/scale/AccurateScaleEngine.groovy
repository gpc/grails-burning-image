/*
Copyright (c) 2009 Pawel Gdula <pawel.gdula@burningice.pl>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package pl.burningice.plugins.image.engines.scale

import java.awt.image.renderable.ParameterBlock
import javax.media.jai.*;
import com.sun.media.jai.codec.*;

/**
 * Class allows to scale image with accourate width and height.
 * Result image will  contain exact width and height gived by user,
 * if orginal size of image not fit to user width and height, image will be scaled
 * to shortest side and cropet on center
 *
 * @author Pawel Gdula <pawel.gdula@burningice.pl>
 */
private class AccurateScaleEngine extends ApproximateScaleEngine {

    /**
     * Sometimes scale of image is lowered by 1px and when
     * there is crop action, exception is rised becouse
     * crop region not match. If wee add 1px to requested size, it will corect error
     *
     * @var int
     */
    private static final SIZE_CORRECTION = 1

    /**
     * @see ScaleEngine#scaleImage(image, width, height)
     * @overwrite
     */
    protected def scaleImage(image, width, height) {
        def scaleX = (width + SIZE_CORRECTION) / image.width
        def scaleY = (height + SIZE_CORRECTION) / image.height
        def scaledImage = resize(scaleX < scaleY ? scaleY : scaleX)(image)

        if (scaledImage.width ==  width
            && scaledImage.height == height ){
            return scaledImage
        }

        ParameterBlock cropParams = new ParameterBlock();
        cropParams.addSource(scaledImage);
        cropParams.add((float)Math.floor((scaledImage.width - width) / 2))
        cropParams.add((float)Math.floor((scaledImage.height - height) / 2))
        cropParams.add((float) width)
        cropParams.add((float) height)

        JAI.create('crop', cropParams)
    }
}
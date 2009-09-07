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

package pl.burningice.plugins.image.engines

import pl.burningice.plugins.image.engines.scale.*
import pl.burningice.plugins.image.engines.watermark.DefaultWatermarkEngine

/**
 * Object allows to build chains of action
 * It instance is pass as a parameter to closure that user define and
 * pass to execute method as aparameter
 *
 * @author pawel.gdula@burningice.pl
 */
class ActionBuilder {

    /**
     * Image that is set to manipualte
     *
     * @var ImageFile
     */
    def loadedImage

    /**
     * Name of output file
     * It is always return as a result of action
     *
     * @var String
     */
    def fileName

    /**
     * Full path to new file location
     *
     * @var String
     */
    def outputFilePath

    /**
     * Method allows to scale image with approximate width and height
     * Width and height of image will never be greater than parameters width and height
     * but it could be lover (image could not be deformed)
     *
     * @param int width
     * @param int height
     * @throws IllegalArgumentException
     * @return String Name of output file
     */
    def scaleApproximate(width, height) {
        if (!width || !height
            || width <= 0 || height <= 0){
            throw new IllegalArgumentException("Scale width = ${width}, height = ${height} is incorrent")
        }

        loadedImage = ScaleEngineFactory.produce(ScaleEngineFactory.APPROXIMATE_ENGINE)
                                        .execute(loadedImage, width, height, outputFilePath)
        fileName
    }

    /**
     * Method allows to scale image with accurate width and height
     * Widht and heigt will be always (almous ;)) equals to set parameters
     * Image will no be deformet but first scaled and next cropped on the center
     * (if it will necessary)
     *
     * @param int width
     * @param int height
     * @throws IllegalArgumentException
     * @return String Name of output file
     */
    def scaleAccurate(width, height) {
        if (!width || !height
            || width <= 0 || height <= 0){
            throw new IllegalArgumentException("Scale width = ${width}, height = ${height} is incorrent")
        }

        loadedImage = ScaleEngineFactory.produce(ScaleEngineFactory.ACCURATE_ENGINE)
                                        .execute(loadedImage, width, height, outputFilePath)
        fileName
    }

    /**
     * Method allows to add watermark to image
     * 
     * @param String watermarkPath Path to watermark image
     * @param [:] position Position on image where watermark should be placed (default [:])
     * @param float alpha Watermark alpha (default 1)
     * @throw IllegalArgumentException
     * @throw FileNotFoundException
     * @return String Name of output file
     */
    def watermark(watermarkPath, position = [:], alpha = 1f) {
        if (!watermarkPath
            || (position['left'] != null && position['right'] != null)
            || (position['top'] != null && position['bottom'] != null)){
            throw new IllegalArgumentException("Watermark watermarkPath = ${watermarkPath}, position = ${position}, alpha = ${alpha} is incorrect")
        }

        def watermarkFile = new File(watermarkPath)

        if (!watermarkFile.exists()){
            throw new FileNotFoundException("There is no ${watermarkPath} watermark file")
        }

        loadedImage = new DefaultWatermarkEngine().execute(watermarkFile, loadedImage, outputFilePath, position, alpha)

        fileName
    }
}


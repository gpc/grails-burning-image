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

package pl.burningice.plugins.image.engines.watermark

import pl.burningice.plugins.image.file.LocalImageFile
import javax.imageio.ImageIO
import java.awt.AlphaComposite
import pl.burningice.plugins.image.file.ImageFileFactory

/**
 * Base, default and only watermark engine
 *
 * @author pawel.gdula@burningice.pl
 */
class DefaultWatermarkEngine {

    /**
     * Execute watermark impose
     *
     * @param File watermarkFile Objec representing local watermark file
     * @param ImageFile loadedImage Loaded image
     * @param String outputFilePath Place where output fule should be stored
     * @param [:] position Map representing watermark location on image
     * @return ImageFile
     */
    def execute(watermarkFile, loadedImage, outputFilePath, position, alpha) {
        // this engine work onlny on local saved file
        if (!loadedImage.isLocal()) {
            loadedImage = loadedImage.asLocal(outputFilePath)
        }

        def fileToMark = ImageIO.read(loadedImage.source);
        def watermark = ImageIO.read(watermarkFile)
        def (left, top) = transfromPostionon(watermark, fileToMark, position)

        def g = fileToMark.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)alpha));
        g.drawImage(watermark, (int)left, (int)top, null);
        g.dispose();

        File outputfile = new File(outputFilePath);
        ImageIO.write(fileToMark, loadedImage.extension, outputfile);

        ImageFileFactory.produce(outputfile)
    }

    /**
     * Method transforms watermark localization parameters
     * to image coordinates
     *
     * @param Image watermark Objec representing local watermark file
     * @param Image fileToMark Loaded image
     * @param [:] position Map representing watermark location on image
     * @return [] Array where 0 index delta from left and 1 index is delta from top of image
     */
    private def transfromPostionon(watermark, fileToMark, position){
        def left, top

        if (position['left'] != null) {
            left = position['left']
        }

        if (position['top'] != null) {
            top = position['top']
        }

        if (position['right'] != null) {
            left = fileToMark.width - position['right'] - watermark.width
        }

        if (position['bottom'] != null) {
            top = fileToMark.height - position['bottom'] - watermark.height
        }

        if (!left) {
            left = (fileToMark.width - watermark.width)/2
        }

        if (!top) {
            top = (fileToMark.height - watermark.height)/2
        }

        [left, top]
    }
}


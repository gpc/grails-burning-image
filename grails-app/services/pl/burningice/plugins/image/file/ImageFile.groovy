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

package pl.burningice.plugins.image.file

/**
 * Base class for all image sources (File, MultipartFile)
 *
 * @author pawel.gdula@burningice.pl
 */
abstract class ImageFile {

    /**
     * Gif image output format
     *
     * @const String
     */
    private static final def GIF_OUTPUT_FORMAT = 'jpg'

    /**
     * Source image 
     *
     * @var File / MultipartFile
     */
    def source

    /**
     * Mapping file extension >> JAI endocer
     *
     * @var [:]
     */
    @Lazy
    def extensionEncoderMapping = [
        'jpg': 'JPEG',
        'gif': 'JPEG',
        'bmp': 'BMP',
        'png': 'PNG'
    ]

    /**
     * Returns file as JAI RenderedOp object
     *
     * @return RenderedOp
     */
    abstract def getAsJaiStream()

    /**
     * Method returns source object name
     *
     * @return String
     */
    abstract def getSourceFileName()

    /**
     * Allows to check if specified file is local
     * or is upladed by user 
     *
     * @return boolean
     */
    def isLocal() {
        this instanceof LocalImageFile
    }

    /**
     * Method returns name of file
     * If file is gif, it will replace gif extension by
     * format specified by GIF_OUTPUT_FORMAT
     *
     * @return String
     */
    def getName() {
        // this action is done to fix name of output file
        // for gif images
        // @see
        // http://java.sun.com/products/java-media/jai/forDevelopers/jaifaq.html
        // "What image file formats are supported? What limitations do these have?"
        def parts = sourceFileName.split(/\./)
        parts[-1] = extension
        parts.join('.')
    }

    /**
     * Method returns file extension
     * If there is GIF file, it will be transformed into format specified by
     * GIF_OUTPUT_FORMAT const
     *
     * @return String
     */
    def getExtension() {
        def fileExtension = sourceFileName.split(/\./)[-1].toLowerCase()

        if (fileExtension == 'gif') {
            return GIF_OUTPUT_FORMAT
        }

        fileExtension
    }

    /**
     * Method returns encoder for file
     * Encoder is mapped by file extension
     *
     * @return String
     */
    def getEncoder() {
        extensionEncoderMapping[extension]
    }
}
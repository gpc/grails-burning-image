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

package pl.burningice.plugins.image

import org.springframework.web.multipart.MultipartFile
import  pl.burningice.plugins.image.file.*
import  pl.burningice.plugins.image.engines.*

/**
 * Main entry for the plugin
 *
 * @author pawel.gdula@burningice.pl
 */
class BurningImageService {

    boolean transactional = false

    /**
     * Global setting for output direcotry
     *
     * @var String
     */
    private def resultDir

    /**
     * Object representin image to manipulate
     *
     * @var ImageFile
     */
    private def loadedImage

    /**
     * Allows to load image from spcified local source
     *
     * @throws IllegalArgumentException
     * @throws FileNotFoundException
     * @param String filePath Path to image
     * @return BurningImageService
     */
    def loadImage(String filePath) {
        if (!filePath) {
            throw new IllegalArgumentException('There is no file path specified')
        }

        def file = new File(filePath)

        if (!file.exists()) {
            throw new FileNotFoundException("There is no file ${filePath}")
        }

        loadedImage = ImageFileFactory.produce(file)
        this
    }

    /**
     * Allows to load image from uploaded file
     *
     * @throws IllegalArgumentException
     * @throws FileNotFoundException
     * @param String filePath Path to image
     * @return BurningImageService
     */
    def loadImage(MultipartFile file) {
        if (!file) {
            throw new IllegalArgumentException('There is no uploaded file')
        }

        if (file.isEmpty()) {
            throw new FileNotFoundException("Uploaded file ${file.originalFilename} is empty")
        }

        loadedImage = ImageFileFactory.produce(file)
        this
    }

    /**
     * Methods allows to set global setting for output direcotry
     *
     * @throws IllegalArgumentException
     * @throws FileNotFoundException
     * @param String resultDir Path to direcotry where images should be saved
     * @return BurningImageService
     */
    def resultDir(String resultDir){
        if (!resultDir) {
            throw new IllegalArgumentException('There is no result directory')
        }

        if (!(new File(resultDir).exists())) {
            throw new FileNotFoundException("There is no ${resultDir} directory")
        }

        if (resultDir[-1] == '/'){
            resultDir = resultDir[0..-2]
        }

        this.resultDir = resultDir
        this
    }

    /**
     * Methods execute action on image
     * It use as a output file name name of orginal image
     *
     * @param Closure chain Chain of action on image
     * @return BurningImageService
     */
    def execute (chain) {
        chain(new ActionBuilder(loadedImage:loadedImage,
                                outputFilePath: "${resultDir}/${loadedImage.name}",
                                fileName: loadedImage.name))
        this
    }

    /**
     * Methods execute action on image
     * Allows to specify output name by the user
     *
     * @param String Name of output image (without extension)
     * @param Closure chain Chain of action on image
     * @return BurningImageService
     */
    def execute (outputFileName, chain) {
        def fileName = "${outputFileName}.${loadedImage.extension}"
        chain(new ActionBuilder(loadedImage:loadedImage,
                                outputFilePath: "${resultDir}/${fileName}",
                                fileName: fileName))
        this
    }
}

package pl.burningice.plugins.image

import org.springframework.mock.web.MockMultipartFile
import pl.burningice.plugins.image.engines.*
import java.awt.Color
import java.awt.Font
import grails.test.GrailsUnitTestCase
import pl.burningice.plugins.image.test.FileUploadUtils
import grails.util.Holders as CH

/**
 *
 * @author pawel.gdula@burningice.pl
 */
@Mixin(FileUploadUtils)
class BurningImageServiceImageMagickTests extends GrailsUnitTestCase {

    protected static final def RESULT_DIR = './resources/resultImages/'

    private def burningImageService

    protected void setUp() {
        super.setUp()
        cleanUpTestDir()
        burningImageService = new BurningImageService()
        CH.config = new ConfigObject()
        CH.config.bi.renderingEngine = RenderingEngine.IMAGE_MAGICK
    }

    protected void tearDown() {
        super.tearDown()
        burningImageService = null
    }

    void testScaleApproximateMultipartFile() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('image.jpg'))

        def scaleResult, result, file

        result = burningImageService.doWith(getMultipartFile('image.jpg'), RESULT_DIR).execute {
            scaleResult = it.scaleApproximate(50, 50)
        }
        assertEquals 'image.jpg', scaleResult
        assertTrue result instanceof Worker
        assertTrue fileExists('image.jpg')
        file = getFile('image.jpg')
        assertTrue file.width <= 50
        assertTrue file.height <= 50

        cleanUpTestDir()
        assertFalse fileExists('image.jpg')

        result = burningImageService.doWith(getMultipartFile('image.jpg'), RESULT_DIR).execute {
            scaleResult = it.scaleApproximate(50, 2000)
        }
        assertEquals 'image.jpg', scaleResult
        assertTrue result instanceof Worker
        assertTrue fileExists('image.jpg')
        file = getFile('image.jpg')
        assertTrue file.width <= 50
        assertTrue file.height <= 2000

        cleanUpTestDir()
        assertFalse fileExists('image.jpg')

        result = burningImageService.doWith(getMultipartFile('image.jpg'), RESULT_DIR).execute {
            scaleResult = it.scaleApproximate(2000, 50)
        }
        assertEquals 'image.jpg', scaleResult
        assertTrue result instanceof Worker
        assertTrue fileExists('image.jpg')
        file = getFile('image.jpg')
        assertTrue file.width <= 2000
        assertTrue file.height <= 50

        cleanUpTestDir()
        assertFalse fileExists('image.jpg')

        result = burningImageService.doWith(getMultipartFile('image.jpg'), RESULT_DIR).execute {
            scaleResult = it.scaleApproximate(2000, 2000)
        }
        assertEquals 'image.jpg', scaleResult
        assertTrue result instanceof Worker
        assertTrue fileExists('image.jpg')
        file = getFile('image.jpg')
        assertTrue file.width <= 2000
        assertTrue file.height <= 2000
    }

    void testScaleApproximateWidthBiggerBothSidesSmaller() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('width_bigger.jpg'))

        def scaleResult, result, file

        result = burningImageService.doWith(getMultipartFile('width_bigger.jpg'), RESULT_DIR).execute {
            scaleResult = it.scaleApproximate(50, 50)
        }
        assertEquals 'width_bigger.jpg', scaleResult
        assertTrue result instanceof Worker
        assertTrue fileExists('width_bigger.jpg')
        file = getFile('width_bigger.jpg')
        assertTrue file.width <= 50
        assertTrue file.height <= 50
    }

    void testScaleApproximateWidthBiggerWidthSmaller() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('width_bigger.jpg'))

        def scaleResult, result, file

        result = burningImageService.doWith(getMultipartFile('width_bigger.jpg'), RESULT_DIR).execute {
            scaleResult = it.scaleApproximate(100, 1000)
        }
        assertEquals 'width_bigger.jpg', scaleResult
        assertTrue result instanceof Worker
        assertTrue fileExists('width_bigger.jpg')
        file = getFile('width_bigger.jpg')
        assertTrue file.width <= 100
        assertTrue file.height <= 1000
    }

    void testScaleApproximateWidthBiggerHeightSmaller() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('width_bigger.jpg'))

        def scaleResult, result, file

        result = burningImageService.doWith(getMultipartFile('width_bigger.jpg'), RESULT_DIR).execute {
            scaleResult = it.scaleApproximate(1000, 100)
        }
        assertEquals 'width_bigger.jpg', scaleResult
        assertTrue result instanceof Worker
        assertTrue fileExists('width_bigger.jpg')
        file = getFile('width_bigger.jpg')
        assertTrue file.width <= 1000
        assertTrue file.height <= 100
    }

    void testScaleAccurateWidthBiggerBothSidesSmaller() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('width_bigger.jpg'))

        def scaleResult, result, file

        result = burningImageService.doWith(getMultipartFile('width_bigger.jpg'), RESULT_DIR).execute {
            scaleResult = it.scaleAccurate(50, 50)
        }
        assertEquals 'width_bigger.jpg', scaleResult
        assertTrue result instanceof Worker
        assertTrue fileExists('width_bigger.jpg')
        file = getFile('width_bigger.jpg')
        assertTrue file.width == 50
        assertTrue file.height == 50
    }

    void testScaleAccurateHeightBiggerBothSidesSmaller() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('height_bigger.jpg'))

        def scaleResult, result, file

        result = burningImageService.doWith(getMultipartFile('height_bigger.jpg'), RESULT_DIR).execute {
            scaleResult = it.scaleAccurate(100, 100)
        }
        assertEquals 'height_bigger.jpg', scaleResult
        assertTrue result instanceof Worker
        assertTrue fileExists('height_bigger.jpg')
        file = getFile('height_bigger.jpg')
        assertTrue file.width == 100
        assertTrue file.height == 100
    }

    void testScaleAccurateBmp() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('image.bmp'))

        def scaleResult, result, file

        result = burningImageService.doWith(getMultipartFile('image.bmp'), RESULT_DIR).execute {
            scaleResult = it.scaleAccurate(100, 100)
        }
        assertEquals 'image.bmp', scaleResult
        assertTrue result instanceof Worker
        assertTrue fileExists('image.bmp')
        file = getFile('image.bmp')
        assertTrue file.width == 100
        assertTrue file.height == 100
    }

    void testScaleAccuratePng() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('image.png'))

        def scaleResult, result, file

        result = burningImageService.doWith(getMultipartFile('image.png'), RESULT_DIR).execute {
            scaleResult = it.scaleAccurate(100, 100)
        }
        assertEquals 'image.png', scaleResult
        assertTrue result instanceof Worker
        assertTrue fileExists('image.png')
        file = getFile('image.png')
        assertTrue file.width == 100
        assertTrue file.height == 100
    }

    void testScaleAccurateGif() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('image.gif'))

        def scaleResult, result, file

        result = burningImageService.doWith(getMultipartFile('image.gif'), RESULT_DIR).execute {
            scaleResult = it.scaleAccurate(100, 100)
        }
        assertEquals 'image.gif', scaleResult
        assertTrue result instanceof Worker
        assertTrue fileExists('image.gif')
        file = getFile('image.gif')
        assertTrue file.width == 100
        assertTrue file.height == 100
    }

    void testWatermarkJpg() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('image.jpg'))

        def scaleResult, result

        result = burningImageService.doWith(getMultipartFile('image.jpg'), RESULT_DIR).execute {
            scaleResult = it.watermark('./resources/testImages/watermark.png')
        }

        assertTrue result instanceof Worker
        assertEquals 'image.jpg', scaleResult
        assertTrue fileExists('image.jpg')
    }

    void testWatermarkBmp() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('image.bmp'))

        def scaleResult, result

        result = burningImageService.doWith(getMultipartFile('image.bmp'), RESULT_DIR).execute {
            scaleResult = it.watermark('./resources/testImages/watermark.png')
        }

        assertTrue result instanceof Worker
        assertEquals 'image.bmp', scaleResult
        assertTrue fileExists('image.bmp')
    }

    void testWatermarkGif() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('image.gif'))

        def scaleResult, result

        result = burningImageService.doWith(getMultipartFile('image.gif'), RESULT_DIR).execute {
            scaleResult = it.watermark('./resources/testImages/watermark.png')
        }

        assertTrue result instanceof Worker
        assertEquals 'image.gif', scaleResult
        assertTrue fileExists('image.gif')
    }

    void testWatermarkPng() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        assertFalse(fileExists('image.png'))

        def scaleResult, result

        result = burningImageService.doWith(getMultipartFile('image.png'), RESULT_DIR).execute {
            scaleResult = it.watermark('./resources/testImages/watermark.png')
        }

        assertTrue result instanceof Worker
        assertEquals 'image.png', scaleResult
        assertTrue fileExists('image.png')
    }

    void testCropError() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(null, null, null, null)
            }
        }

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(10, null, null, null)
            }
        }

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(0, 0, null, null)
            }
        }

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(0, 0, 10, null)
            }
        }

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(-10, 0, 10, 10)
            }
        }

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(0, -10, 10, 10)
            }
        }

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(0, 0, -10, 10)
            }
        }

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(0, 0, 0, -10)
            }
        }

        def image = getFile('image.jpg', SOURCE_DIR)

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop((image.width + 10), 0, 10, 10)
            }
        }

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(0, (image.height + 10), 10, 10)
            }
        }

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(0, 0, (image.width + 10), 10)
            }
        }

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(0, 0, 10, (image.height + 10))
            }
        }

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(image.width, 0, 10, 10)
            }
        }

        shouldFail(IllegalArgumentException){
            burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
                it.crop(0, image.height, 10, 10)
            }
        }
    }

    void testCropJpgLocalFile() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        def result, scaleResult, image

        result = burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{
            scaleResult = it.crop(0, 0, 50, 100)
        }

        assertTrue result instanceof Worker
        assertEquals 'image.jpg', scaleResult
        assertTrue fileExists('image.jpg')
        image = getFile('image.jpg')
        assertTrue image.width == 50
        assertTrue image.height == 100
    }

    void testCropBmpLocalFile() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        def result, scaleResult, image

        result = burningImageService.doWith(getFilePath('image.bmp'), RESULT_DIR).execute{
            scaleResult = it.crop(20, 30, 50, 40)
        }

        assertTrue result instanceof Worker
        assertEquals 'image.bmp', scaleResult
        assertTrue fileExists('image.bmp')
        image = getFile('image.bmp')
        assertTrue image.width == 50
        assertTrue image.height == 40
     }

    void testCropPngLocalFile() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        def result, scaleResult, image

        result = burningImageService.doWith(getFilePath('image.png'), RESULT_DIR).execute{
            scaleResult = it.crop(20, 30, 50, 40)
        }

        assertTrue result instanceof Worker
        assertEquals 'image.png', scaleResult
        assertTrue fileExists('image.png')
        image = getFile('image.png')
        assertTrue image.width == 50
        assertTrue image.height == 40
    }

    void testCropGifLocalFile() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
        def result, scaleResult, image

        result = burningImageService.doWith(getFilePath('image.gif'), RESULT_DIR).execute{
            scaleResult = it.crop(100, 100, 200, 230)
        }

        assertTrue result instanceof Worker
        assertEquals 'image.gif', scaleResult
        assertTrue fileExists('image.gif')
        image = getFile('image.gif')
        assertTrue image.width == 200
        assertTrue image.height == 230
    }

    void testTextJpgLocalFile() {
        assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)        
            def result, scaleResult, image

            result = burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{img ->
                scaleResult = img.text({
                    it.write("text one", 10, 10)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image.jpg', scaleResult
            assertTrue fileExists('image.jpg')

            cleanUpTestDir()

            result = burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{img ->
                scaleResult = img.text(Color.WHITE,{
                    it.write("text one", 10, 10)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image.jpg', scaleResult
            assertTrue fileExists('image.jpg')

            cleanUpTestDir()

            result = burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{img ->
                scaleResult = img.text(new Font('Arial', Font.PLAIN, 30),{
                    it.write("text one", 50, 50)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image.jpg', scaleResult
            assertTrue fileExists('image.jpg')

            cleanUpTestDir()

            result = burningImageService.doWith(getFilePath('image.jpg'), RESULT_DIR).execute{img ->
                scaleResult = img.text(Color.WHITE, new Font('Arial', Font.PLAIN, 30),{
                    it.write("text one", 50, 50)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image.jpg', scaleResult
            assertTrue fileExists('image.jpg')
        }

        void testTextBmpLocalFile() {
            assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
            def result, scaleResult, image

            result = burningImageService.doWith(getFilePath('image2.bmp'), RESULT_DIR).execute{img ->
                scaleResult = img.text({
                    it.write("text one", 10, 10)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image2.bmp', scaleResult
            assertTrue fileExists('image2.bmp')

            cleanUpTestDir()

            result = burningImageService.doWith(getFilePath('image2.bmp'), RESULT_DIR).execute{img ->
                scaleResult = img.text(Color.WHITE,{
                    it.write("text one", 10, 10)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image2.bmp', scaleResult
            assertTrue fileExists('image2.bmp')

            cleanUpTestDir()

            result = burningImageService.doWith(getFilePath('image2.bmp'), RESULT_DIR).execute{img ->
                scaleResult = img.text(new Font('Arial', Font.PLAIN, 30),{
                    it.write("text one", 50, 50)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image2.bmp', scaleResult
            assertTrue fileExists('image2.bmp')

            cleanUpTestDir()

            result = burningImageService.doWith(getFilePath('image2.bmp'), RESULT_DIR).execute{img ->
                scaleResult = img.text(Color.WHITE, new Font('Arial', Font.PLAIN, 30),{
                    it.write("text one", 50, 50)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image2.bmp', scaleResult
            assertTrue fileExists('image2.bmp')
    
        }

        void testTextGifLocalFile() {
            assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
            def result, scaleResult, image

            result = burningImageService.doWith(getFilePath('image.gif'), RESULT_DIR).execute{img ->
                scaleResult = img.text({
                    it.write("text one", 10, 10)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image.gif', scaleResult
            assertTrue fileExists('image.gif')

            cleanUpTestDir()

            result = burningImageService.doWith(getFilePath('image.gif'), RESULT_DIR).execute{img ->
                scaleResult = img.text(Color.WHITE,{
                    it.write("text one", 10, 10)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image.gif', scaleResult
            assertTrue fileExists('image.gif')

            cleanUpTestDir()

            result = burningImageService.doWith(getFilePath('image.gif'), RESULT_DIR).execute{img ->
                scaleResult = img.text(new Font('Arial', Font.PLAIN, 30),{
                    it.write("text one", 50, 50)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image.gif', scaleResult
            assertTrue fileExists('image.gif')

            cleanUpTestDir()

            result = burningImageService.doWith(getFilePath('image.gif'), RESULT_DIR).execute{img ->
                scaleResult = img.text(Color.WHITE, new Font('Arial', Font.PLAIN, 30),{
                    it.write("text one", 50, 50)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image.gif', scaleResult
            assertTrue fileExists('image.gif')
        }

        void testTextPngLocalFile() {
            assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
            def result, scaleResult, image

            result = burningImageService.doWith(getFilePath('image.png'), RESULT_DIR).execute{img ->
                scaleResult = img.text({
                    it.write("text one", 10, 10)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image.png', scaleResult
            assertTrue fileExists('image.png')

            cleanUpTestDir()

            result = burningImageService.doWith(getFilePath('image.png'), RESULT_DIR).execute{img ->
                scaleResult = img.text(Color.WHITE,{
                    it.write("text one", 10, 10)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image.png', scaleResult
            assertTrue fileExists('image.png')

            cleanUpTestDir()

            result = burningImageService.doWith(getFilePath('image.png'), RESULT_DIR).execute{img ->
                scaleResult = img.text(new Font('Arial', Font.PLAIN, 30),{
                    it.write("text one", 50, 50)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image.png', scaleResult
            assertTrue fileExists('image.png')

            cleanUpTestDir()

            result = burningImageService.doWith(getFilePath('image.png'), RESULT_DIR).execute{img ->
                scaleResult = img.text(Color.WHITE, new Font('Arial', Font.PLAIN, 30),{
                    it.write("text one", 50, 50)
                    it.write("text two", 100, 100)
                    it.write("text three", 200, 200)
                })
            }

            assertTrue result instanceof Worker
            assertEquals 'image.png', scaleResult
            assertTrue fileExists('image.png')
        }

        void testChainExecutions() {
            assertEquals(ConfigUtils.getEngine(), RenderingEngine.IMAGE_MAGICK)
            def result, result1, result2, image

            result = burningImageService.doWith(getFilePath('image.png'), RESULT_DIR)
            .execute('first', {img ->
                result1 =  img.scaleAccurate(100, 200)
                img.watermark('./resources/testImages/watermark.png', ['top':10, 'left': 10])
            })
            .execute('second', {img ->
                result2 = img.crop(100, 100, 500, 500)

                img.text(Color.RED, new Font('Arial', Font.PLAIN, 30)){
                    it.write("text one", 10, 10)
                }
                img.text(Color.GREEN, new Font('Arial', Font.PLAIN, 30)){
                    it.write("text two", 100, 100)
                }
                img.text(Color.BLUE, new Font('Arial', Font.PLAIN, 30)){
                    it.write("text three", 200, 200)
                }
                img.text(Color.WHITE, new Font('Arial', Font.PLAIN, 15)){
                    it.write("end end end", 100, 200)
                }

            })
            .execute('three', {img ->
                img.crop(0, 0, 600, 600)
                img.text(Color.WHITE, new Font('Arial', Font.PLAIN, 30)){
                    it.write("this is file number three", 10, 10)
                }
            })
            .execute('four', {img ->
                img.text(Color.WHITE, new Font('Arial', Font.PLAIN, 30)){
                    it.write("this is file number four", 10, 10)
                }
            })

            assertTrue result instanceof Worker
            assertEquals 'first.png', result1
            assertTrue fileExists('first.png')
            image = getFile('first.png')
            assertTrue image.width == 100
            assertTrue image.height == 200

            assertEquals 'second.png', result2
            assertTrue fileExists('second.png')
            image = getFile('second.png')
            assertTrue image.width == 500
            assertTrue image.height == 500

            assertTrue fileExists('three.png')
            image = getFile('three.png')
            assertTrue image.width == 600
            assertTrue image.height == 600

            assertTrue fileExists('four.png')
            image = getFile('four.png')
            def sourceImage = getFile('image.png', SOURCE_DIR)
            assertTrue image.width == sourceImage.width
            assertTrue image.height == sourceImage.height
        }

        void testTextLocalFile() {
            CH.config.bi.renderingEngine = RenderingEngine.JAI

            def result, scaleResult, image

            result = burningImageService.doWith(getFilePath('image.png'), RESULT_DIR).execute('jai', {img ->
                img.text(Color.RED, new Font('Arial', Font.PLAIN, 30)){
                    it.write("text one", 10, 10)
                }
                img.text(Color.GREEN, new Font('Arial', Font.PLAIN, 30)){
                    it.write("text two", 100, 100)
                }
                img.text(Color.BLUE, new Font('Arial', Font.PLAIN, 30)){
                    it.write("text three", 200, 200)
                }
                img.text(Color.WHITE, new Font('Arial', Font.PLAIN, 15)){
                    it.write("end end end", 100, 200)
                }

            })

            assertTrue result instanceof Worker
            assertTrue fileExists('jai.png')

            CH.config.bi.renderingEngine = RenderingEngine.IMAGE_MAGICK

            result = burningImageService.doWith(getFilePath('image.png'), RESULT_DIR).execute('imagemaick', {img ->
                img.text(Color.RED, new Font('Arial', Font.PLAIN, 30)){
                    it.write("text one", 10, 10)
                }
                img.text(Color.GREEN, new Font('Arial', Font.PLAIN, 30)){
                    it.write("text two", 100, 100)
                }
                img.text(Color.BLUE, new Font('Arial', Font.PLAIN, 30)){
                    it.write("text three", 200, 200)
                }
                img.text(Color.WHITE, new Font('Arial', Font.PLAIN, 15)){
                    it.write("end end end", 100, 200)
                }

            })

            assertTrue result instanceof Worker
            assertTrue fileExists('imagemaick.png')
        }

        void testTest(){
            CH.config.bi.renderingEngine = RenderingEngine.JAI
            burningImageService.doWith(getMultipartFile('image.jpg'), RESULT_DIR).execute('jai_approximate'){
                it.scaleApproximate(200, 200)
            }

            CH.config.bi.renderingEngine = RenderingEngine.IMAGE_MAGICK
            burningImageService.doWith(getMultipartFile('image.jpg'), RESULT_DIR).execute('imagemagick_approximate'){
                it.scaleApproximate(200, 200)
            }

            CH.config.bi.renderingEngine = RenderingEngine.JAI
            burningImageService.doWith(getMultipartFile('image.jpg'), RESULT_DIR).execute('jai_accurate'){
                it.scaleAccurate(200, 200)
            }

            CH.config.bi.renderingEngine = RenderingEngine.IMAGE_MAGICK
            burningImageService.doWith(getMultipartFile('image.jpg'), RESULT_DIR).execute('imagemagick_accurate'){
                it.scaleAccurate(200, 200)
            }
        }
        
    }


package pl.burningice.plugins.image

import grails.util.Holders
import pl.burningice.plugins.image.engines.scale.ScaleType
import pl.burningice.plugins.image.ast.test.TestDomain
import pl.burningice.plugins.image.ast.test.TestDbContainerDomainFirst
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import pl.burningice.plugins.image.ast.Image
import pl.burningice.plugins.image.ast.test.TestDbContainerDomainSecond
import pl.burningice.plugins.image.ast.test.TestDbContainerDomainThird
import pl.burningice.plugins.image.test.FileUploadUtils
import grails.test.GrailsUnitTestCase
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import pl.burningice.plugins.image.engines.RenderingEngine

/**
 * @author pawel.gdula@burningice.pl
 */
@Mixin(FileUploadUtils)
class ImageUploadServiceTests extends GrailsUnitTestCase implements ApplicationContextAware {

    protected static final def RESULT_DIR = './web-app/upload/'

    protected static final def WEB_APP_RESULT_DIR = './upload/'

    ImageUploadService imageUploadService

    ApplicationContext applicationContext

    protected void setUp() {
        super.setUp()
        cleanUpTestDir()
        Holders.config = new ConfigObject()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testDeleteDbContainerAndRelatedImagesWithDeleteBeforeMethodExists() {
        Holders.config.bi.TestDbContainerDomainThird = [
            images: [
                'small':[scale:[width:100, height:100, type:ScaleType.ACCURATE]],
                'medium':[scale:[width:300, height:300, type:ScaleType.ACCURATE]],
                'large':[scale:[width:800, height:600, type:ScaleType.APPROXIMATE]]
            ]
        ]

        def testDomain1 = new TestDbContainerDomainThird(namePrefix: 'prefixed-', name:'test 1', logo:getMultipartFile('image.jpg'))
        assertTrue(testDomain1.validate())
        assertNotNull(testDomain1.save(flush:true))
        imageUploadService.save(testDomain1)
        assertNotNull(testDomain1.biImage)
        assertEquals(3, testDomain1.biImage.size())
        assertNotNull(testDomain1.biImage.small)
        assertEquals('jpg', testDomain1.biImage.small.type)
        assertNotNull(testDomain1.biImage.medium)
        assertEquals('jpg', testDomain1.biImage.medium.type)
        assertNotNull(testDomain1.biImage.large)
        assertEquals('jpg', testDomain1.biImage.large.type)

        assertEquals(TestDbContainerDomainThird.count(), 1)
        assertEquals(Image.count(), 3)

        testDomain1.delete()

        assertEquals(TestDbContainerDomainThird.count(), 0)
        assertEquals(Image.count(), 0)
        // see definition of TestDbContainerDomainThird to understand this line
        assertEquals('prefixed-test 1', testDomain1.name)
    }

    void testDeleteDbContainerAndRelatedImages() {
        Holders.config.bi.TestDbContainerDomainSecond = [
            images: [
                'small':[scale:[width:100, height:100, type:ScaleType.ACCURATE]],
                'medium':[scale:[width:300, height:300, type:ScaleType.ACCURATE]],
                'large':[scale:[width:800, height:600, type:ScaleType.APPROXIMATE]]
            ]
        ]

        def testDomain1 = new TestDbContainerDomainSecond(name:'test 1', logo:getMultipartFile('image.jpg'))
        assertNotNull(testDomain1.save(flush:true))
        imageUploadService.save(testDomain1)
        assertNotNull(testDomain1.biImage)
        assertEquals(3, testDomain1.biImage.size())
        assertNotNull(testDomain1.biImage.small)
        assertEquals('jpg', testDomain1.biImage.small.type)
        assertNotNull(testDomain1.biImage.medium)
        assertEquals('jpg', testDomain1.biImage.medium.type)
        assertNotNull(testDomain1.biImage.large)
        assertEquals('jpg', testDomain1.biImage.large.type)

        assertEquals(TestDbContainerDomainSecond.count(), 1)
        assertEquals(Image.count(), 3)

        def testDomain2 = new TestDbContainerDomainSecond(name:'test 2', logo:getMultipartFile('image.png'))
        assertNotNull(testDomain2.save(flush:true))
        imageUploadService.save(testDomain2)
        assertNotNull(testDomain2.biImage)
        assertEquals(3, testDomain2.biImage.size())
        assertNotNull(testDomain2.biImage.small)
        assertEquals('png', testDomain2.biImage.small.type)
        assertNotNull(testDomain2.biImage.medium)
        assertEquals('png', testDomain2.biImage.medium.type)
        assertNotNull(testDomain2.biImage.large)
        assertEquals('png', testDomain2.biImage.large.type)

        assertEquals(TestDbContainerDomainSecond.count(), 2)
        assertEquals(Image.count(), 6)

        testDomain1.delete()

        assertEquals(TestDbContainerDomainSecond.count(), 1)
        assertEquals(Image.count(), 3)

        List<Image> testDomain2Images = testDomain2.biImage.collect {it.value} 
        Image.list().each {Image img -> assertTrue(testDomain2Images.contains(img))}

        testDomain2.delete(flush:true)

        assertEquals(TestDbContainerDomainSecond.count(), 0)
        assertEquals(Image.count(), 0)
    }

    void testDbImageDelete() {
        def testDomain, result
        // should be ok now
        Holders.config.bi.TestDbContainerDomainSecond = [
            images: [
                'small':[scale:[width:100, height:100, type:ScaleType.ACCURATE]],
                'medium':[scale:[width:300, height:300, type:ScaleType.ACCURATE]],
                'large':[scale:[width:800, height:600, type:ScaleType.APPROXIMATE]]
            ]
        ]
        testDomain = new TestDbContainerDomainSecond(name:'test', logo:getMultipartFile('image.jpg'))
        assertNotNull(testDomain.save(flush:true))
        imageUploadService.save(testDomain)
        assertEquals(Image.count(), 3)
        assertNotNull(testDomain.biImage)
        assertEquals(3, testDomain.biImage.size())
        assertNotNull(testDomain.biImage.small)
        assertEquals('jpg', testDomain.biImage.small.type)
        assertNotNull(testDomain.biImage.medium)
        assertEquals('jpg', testDomain.biImage.medium.type)
        assertNotNull(testDomain.biImage.large)
        assertEquals('jpg', testDomain.biImage.large.type)

        imageUploadService.delete(testDomain)
        assertEquals(0, Image.count())
        assertNull testDomain.biImage
    }

    void testScaleDbImageDefaultCustomFiled() {
        def testDomain, result
        Holders.config.bi.TestDbContainerDomainSecond = null
        // instance not saved and there is no image
        testDomain = new TestDbContainerDomainSecond()
        shouldFail(IllegalArgumentException){
            imageUploadService.save(testDomain)
        }
        // image uploaded but instance not saved
        testDomain = new TestDbContainerDomainSecond(name:'test', logo:getMultipartFile('image.jpg'))
        shouldFail(IllegalArgumentException){
            imageUploadService.save(testDomain)
        }
        // should fail, there is no config provided for this domain object
        testDomain = new TestDbContainerDomainSecond(name:'test')
        assertNotNull(testDomain.save(flush:true))
        testDomain.logo = getMultipartFile('image.jpg')
        shouldFail(IllegalArgumentException){
            imageUploadService.save(testDomain)
        }
        // should be ok now
        Holders.config.bi.TestDbContainerDomainSecond = [
            images: [
                'small':[scale:[width:100, height:100, type:ScaleType.ACCURATE]],
                'medium':[scale:[width:300, height:300, type:ScaleType.ACCURATE]],
                'large':[scale:[width:800, height:600, type:ScaleType.APPROXIMATE]]
            ]
        ]
        testDomain = new TestDbContainerDomainSecond(name:'test', logo:getMultipartFile('image.jpg'))
        assertNotNull(testDomain.save(flush:true))
        def version = testDomain.version
        imageUploadService.save(testDomain)

        assertEquals(version, testDomain.version)
        assertEquals(Image.count(), 3)
        assertNotNull(testDomain.biImage)
        assertEquals(3, testDomain.biImage.size())
        assertNotNull(testDomain.biImage.small)
        assertEquals('jpg', testDomain.biImage.small.type)
        assertNotNull(testDomain.biImage.medium)
        assertEquals('jpg', testDomain.biImage.medium.type)
        assertNotNull(testDomain.biImage.large)
        assertEquals('jpg', testDomain.biImage.large.type)

        testDomain.logo = getMultipartFile('image.png')
        version = testDomain.version
        imageUploadService.save(testDomain)

        assertEquals(version, testDomain.version)
        assertEquals(Image.count(), 3)
        assertNotNull(testDomain.biImage)
        assertEquals(3, testDomain.biImage.size())
        assertNotNull(testDomain.biImage.small)
        assertEquals('png', testDomain.biImage.small.type)
        assertNotNull(testDomain.biImage.medium)
        assertEquals('png', testDomain.biImage.medium.type)
        assertNotNull(testDomain.biImage.large)
        assertEquals('png', testDomain.biImage.large.type)

        testDomain.logo = getMultipartFile('image.bmp')
        version = testDomain.version
        imageUploadService.save(testDomain, true)

        assertTrue(version < testDomain.version)
        assertEquals(Image.count(), 3)
        assertNotNull(testDomain.biImage)
        assertEquals(3, testDomain.biImage.size())
        assertNotNull(testDomain.biImage.small)
        assertEquals('bmp', testDomain.biImage.small.type)
        assertNotNull(testDomain.biImage.medium)
        assertEquals('bmp', testDomain.biImage.medium.type)
        assertNotNull(testDomain.biImage.large)
        assertEquals('bmp', testDomain.biImage.large.type)

        BufferedImage smallImage = ImageIO.read(new ByteArrayInputStream(testDomain.biImage.small.data))
        println smallImage
        assertTrue smallImage.width == Holders.config.bi.TestDbContainerDomainSecond.images.small.scale.width
        assertTrue smallImage.height == Holders.config.bi.TestDbContainerDomainSecond.images.small.scale.height

        BufferedImage mediumImage = ImageIO.read(new ByteArrayInputStream(testDomain.biImage.medium.data))
        println mediumImage
        assertTrue mediumImage.width == Holders.config.bi.TestDbContainerDomainSecond.images.medium.scale.width
        assertTrue mediumImage.height == Holders.config.bi.TestDbContainerDomainSecond.images.medium.scale.height

        BufferedImage largeImage = ImageIO.read(new ByteArrayInputStream(testDomain.biImage.large.data))
        println largeImage
        assertTrue largeImage.width <= Holders.config.bi.TestDbContainerDomainSecond.images.large.scale.width
        assertTrue largeImage.height <= Holders.config.bi.TestDbContainerDomainSecond.images.large.scale.height
    }

    void testScaleDbImageDefaultFiled() {
        def testDomain, result
        Holders.config.bi.TestDbContainerDomainFirst = null
        // instance not saved and there is no image
        testDomain = new TestDbContainerDomainFirst()
        shouldFail(IllegalArgumentException){
            imageUploadService.save(testDomain)
        }
        // image uploaded but instance not saved
        testDomain = new TestDbContainerDomainFirst(image:getMultipartFile('image.jpg'))
        shouldFail(IllegalArgumentException){
            imageUploadService.save(testDomain)
        }
        // should fail, there is no config provided for this domain object
        testDomain = new TestDbContainerDomainFirst()
        assertNotNull(testDomain.save(flush:true))
        testDomain.image = getMultipartFile('image.jpg')
        shouldFail(IllegalArgumentException){
            imageUploadService.save(testDomain)
        }
        // should be ok now
        Holders.config.bi.TestDbContainerDomainFirst = [
            images: [
                'small':[scale:[width:100, height:100, type:ScaleType.ACCURATE]],
                'medium':[scale:[width:300, height:300, type:ScaleType.ACCURATE]],
                'large':[scale:[width:800, height:600, type:ScaleType.APPROXIMATE]]
            ]
        ]
        testDomain = new TestDbContainerDomainFirst(image:getMultipartFile('image.jpg'))
        assertNotNull(testDomain.save(flush:true))
        def version = testDomain.version 
        imageUploadService.save(testDomain)

        assertEquals(version, testDomain.version)
        assertEquals(Image.count(), 3)
        assertNotNull(testDomain.biImage)
        assertEquals(3, testDomain.biImage.size())
        assertNotNull(testDomain.biImage.small)
        assertEquals('jpg', testDomain.biImage.small.type)
        assertNotNull(testDomain.biImage.medium)
        assertEquals('jpg', testDomain.biImage.medium.type)
        assertNotNull(testDomain.biImage.large)
        assertEquals('jpg', testDomain.biImage.large.type)

        testDomain.image = getMultipartFile('image.png')
        version = testDomain.version
        imageUploadService.save(testDomain)

        assertEquals(version, testDomain.version)
        assertEquals(Image.count(), 3)
        assertNotNull(testDomain.biImage)
        assertEquals(3, testDomain.biImage.size())
        assertNotNull(testDomain.biImage.small)
        assertEquals('png', testDomain.biImage.small.type)
        assertNotNull(testDomain.biImage.medium)
        assertEquals('png', testDomain.biImage.medium.type)
        assertNotNull(testDomain.biImage.large)
        assertEquals('png', testDomain.biImage.large.type)

        testDomain.image = getMultipartFile('image.bmp')
        version = testDomain.version
        imageUploadService.save(testDomain, true)

        assertTrue(version < testDomain.version)
        assertEquals(Image.count(), 3)
        assertNotNull(testDomain.biImage)
        assertEquals(3, testDomain.biImage.size())
        assertNotNull(testDomain.biImage.small)
        assertEquals('bmp', testDomain.biImage.small.type)
        assertNotNull(testDomain.biImage.medium)
        assertEquals('bmp', testDomain.biImage.medium.type)
        assertNotNull(testDomain.biImage.large)
        assertEquals('bmp', testDomain.biImage.large.type)

        BufferedImage smallImage = ImageIO.read(new ByteArrayInputStream(testDomain.biImage.small.data))
        println smallImage
        assertTrue smallImage.width == Holders.config.bi.TestDbContainerDomainFirst.images.small.scale.width
        assertTrue smallImage.height == Holders.config.bi.TestDbContainerDomainFirst.images.small.scale.height

        BufferedImage mediumImage = ImageIO.read(new ByteArrayInputStream(testDomain.biImage.medium.data))
        println mediumImage
        assertTrue mediumImage.width == Holders.config.bi.TestDbContainerDomainFirst.images.medium.scale.width
        assertTrue mediumImage.height == Holders.config.bi.TestDbContainerDomainFirst.images.medium.scale.height

        BufferedImage largeImage = ImageIO.read(new ByteArrayInputStream(testDomain.biImage.large.data))
        println largeImage
        assertTrue largeImage.width <= Holders.config.bi.TestDbContainerDomainFirst.images.large.scale.width
        assertTrue largeImage.height <= Holders.config.bi.TestDbContainerDomainFirst.images.large.scale.height
    }

    void testScale() {
        def testDomain = new TestDomain(image:getMultipartFile('image.jpg'))

        shouldFail(IllegalArgumentException){
            imageUploadService.save(testDomain)
        }
        assertNull testDomain.imageExtension

        Holders.config.bi.TestDomain = [
            outputDir: WEB_APP_RESULT_DIR,
            prefix: 'prefixName',
            images: ['small':[scale:[width:100, height:100, type:ScaleType.ACCURATE]],
                     'medium':[scale:[width:300, height:300, type:ScaleType.ACCURATE]],
                     'large':[scale:[width:800, height:600, type:ScaleType.APPROXIMATE]]
            ]
        ]

        shouldFail(IllegalArgumentException){
            imageUploadService.save(testDomain)
        }
        assertNull testDomain.imageExtension

        testDomain.save(flush:true)
        def version = testDomain.version

        assertNotNull testDomain.ident()
        imageUploadService.save(testDomain)

        assertTrue testDomain.imageExtension == 'jpg'
        assertTrue testDomain.version == version

        assertTrue fileExists("prefixName-${testDomain.id}-large.jpg")
        assertTrue fileExists("prefixName-${testDomain.id}-medium.jpg")
        assertTrue fileExists("prefixName-${testDomain.id}-small.jpg")

        version = testDomain.version

        testDomain.image = getMultipartFile('image.png')
        assertNotNull testDomain.ident()
        imageUploadService.save(testDomain, true)

        assertFalse fileExists("prefixName-${testDomain.id}-large.jpg")
        assertFalse fileExists("prefixName-${testDomain.id}-medium.jpg")
        assertFalse fileExists("prefixName-${testDomain.id}-small.jpg")

        assertTrue fileExists("prefixName-${testDomain.id}-large.png")
        assertTrue fileExists("prefixName-${testDomain.id}-medium.png")
        assertTrue fileExists("prefixName-${testDomain.id}-small.png")

        assertTrue testDomain.imageExtension == 'png'
        assertTrue testDomain.version > version
    }

    void testScaleAndWatermak() {
        Holders.config.bi.TestDomain = [
            outputDir: WEB_APP_RESULT_DIR,
            prefix: null,
            images: ['large':[watermark:[sign:'images/watermark.png', offset:[top:10, left:10]]]]
        ]

        def testDomain = new TestDomain(image:getMultipartFile('image.jpg')).save(flush:true)
        assertFalse fileExists("${testDomain.ident()}-large.jpg")
        imageUploadService.save(testDomain)
        assertTrue fileExists("${testDomain.ident()}-large.jpg")
    }

    void testDelete() {
        Holders.config.bi.TestDomain = [
            outputDir: WEB_APP_RESULT_DIR,
            prefix: 'prefixName',
            images: ['small':[scale:[width:100, height:100, type:ScaleType.ACCURATE]],
                     'medium':[scale:[width:300, height:300, type:ScaleType.ACCURATE]],
                     'large':[scale:[width:800, height:600, type:ScaleType.APPROXIMATE]]
            ]
        ]

        def testDomain = new TestDomain(image:getMultipartFile('image.jpg')).save(flush:true)

        assertFalse fileExists("prefixName-${testDomain.ident()}-large.jpg")
        assertFalse fileExists("prefixName-${testDomain.ident()}-medium.jpg")
        assertFalse fileExists("prefixName-${testDomain.ident()}-small.jpg")
        
        imageUploadService.save(testDomain)

        assertTrue fileExists("prefixName-${testDomain.ident()}-large.jpg")
        assertTrue fileExists("prefixName-${testDomain.ident()}-medium.jpg")
        assertTrue fileExists("prefixName-${testDomain.ident()}-small.jpg")

        def version = testDomain.version
        imageUploadService.delete(testDomain)

        assertTrue testDomain.version == version
        assertFalse fileExists("prefixName-${testDomain.ident()}-large.jpg")
        assertFalse fileExists("prefixName-${testDomain.ident()}-medium.jpg")
        assertFalse fileExists("prefixName-${testDomain.ident()}-small.jpg")

        imageUploadService.save(testDomain)

        assertTrue fileExists("prefixName-${testDomain.ident()}-large.jpg")
        assertTrue fileExists("prefixName-${testDomain.ident()}-medium.jpg")
        assertTrue fileExists("prefixName-${testDomain.ident()}-small.jpg")

        version = testDomain.version
        imageUploadService.delete(testDomain, true)

        assertTrue testDomain.version > version
        assertFalse fileExists("prefixName-${testDomain.ident()}-large.jpg")
        assertFalse fileExists("prefixName-${testDomain.ident()}-medium.jpg")
        assertFalse fileExists("prefixName-${testDomain.ident()}-small.jpg")
    }

    void testWatermak() {
        Holders.config.bi.TestDomain = [
            outputDir: WEB_APP_RESULT_DIR,
            prefix: 'scale-and-waremark',
            images: ['large':[scale:[width:800, height:600, type:ScaleType.APPROXIMATE],
                              watermark:[sign:'images/watermark.png', offset:[top:10, left:10]]]]
        ]

        def testDomain = new TestDomain(image:getMultipartFile('image.jpg')).save(flush:true)
        assertFalse fileExists("scale-and-waremark-${testDomain.ident()}-large.jpg")
        imageUploadService.save(testDomain)
        assertTrue fileExists("scale-and-waremark-${testDomain.ident()}-large.jpg")
    }

    void testActionWraper() {
        Holders.config.bi.TestDomain = [
            outputDir: WEB_APP_RESULT_DIR,
            prefix: 'action-wraped',
            images: ['large':[scale:[width:800, height:600, type:ScaleType.APPROXIMATE],
                              watermark:[sign:'images/watermark.png', offset:[top:10, left:10]]],
                      'small':[scale:[width:300, height:300, type:ScaleType.ACCURATE]]]
        ]

        def testDomain = new TestDomain(image:getMultipartFile('image.jpg')).save(flush:true)
        def version = testDomain.version

        assertNull testDomain.imageExtension
        assertFalse fileExists("action-wraped-${testDomain.ident()}-large.jpg")
        assertFalse fileExists("action-wraped-${testDomain.ident()}-small.jpg")

        imageUploadService.save(testDomain, {image, name, action ->
            action()

            if (name == 'large'){
                image.text({it.write("Text on large image", 300, 300)})
            }

            if (name == 'small'){
                image.text({it.write("Text on small image", 10, 50)})
            }
        })

        assertTrue version == testDomain.version
        assertTrue testDomain.imageExtension == 'jpg'
        assertTrue fileExists("action-wraped-${testDomain.ident()}-large.jpg")
        assertTrue fileExists("action-wraped-${testDomain.ident()}-small.jpg")

        testDomain.image = getMultipartFile('image.png')
        
        imageUploadService.save(testDomain, true, {image, name, action ->
            action()

            if (name == 'large'){
                image.text({it.write("Text on large image", 300, 300)})
            }

            if (name == 'small'){
                image.text({it.write("Text on small image", 10, 50)})
            }
        })

        assertTrue version < testDomain.version
        assertTrue testDomain.imageExtension == 'png'

        assertTrue fileExists("action-wraped-${testDomain.ident()}-large.png")
        assertTrue fileExists("action-wraped-${testDomain.ident()}-small.png")

        assertFalse fileExists("action-wraped-${testDomain.ident()}-large.jpg")
        assertFalse fileExists("action-wraped-${testDomain.ident()}-small.jpg")
    }

    void testAbsolutePath() {
        def testDomain = new TestDomain(image:getMultipartFile('image.jpg'))

        shouldFail(IllegalArgumentException){
            imageUploadService.save(testDomain)
        }
        assertNull testDomain.imageExtension

        println getAbsolutePath(WEB_APP_RESULT_DIR)

        Holders.config.bi.TestDomain = [
            outputDir: ['path':getAbsolutePath(WEB_APP_RESULT_DIR), 'alias':'/upload/'],
            prefix: 'prefixName',
            images: ['small':[scale:[width:100, height:100, type:ScaleType.ACCURATE]],
                     'medium':[scale:[width:300, height:300, type:ScaleType.ACCURATE]],
                     'large':[scale:[width:800, height:600, type:ScaleType.APPROXIMATE]]
            ]
        ]

        shouldFail(IllegalArgumentException){
            imageUploadService.save(testDomain)
        }

        assertNull testDomain.imageExtension

        testDomain.save(flush:true)
        def version = testDomain.version

        assertNotNull testDomain.ident()
        imageUploadService.save(testDomain)

        assertTrue testDomain.imageExtension == 'jpg'
        assertTrue testDomain.version == version

        assertTrue fileExists("prefixName-${testDomain.ident()}-large.jpg")
        assertTrue fileExists("prefixName-${testDomain.ident()}-medium.jpg")
        assertTrue fileExists("prefixName-${testDomain.ident()}-small.jpg")

        version = testDomain.version

        testDomain.image = getMultipartFile('image.png')
        assertNotNull testDomain.ident()
        imageUploadService.save(testDomain, true)

        assertFalse fileExists("prefixName-${testDomain.ident()}-large.jpg")
        assertFalse fileExists("prefixName-${testDomain.ident()}-medium.jpg")
        assertFalse fileExists("prefixName-${testDomain.ident()}-small.jpg")

        assertTrue fileExists("prefixName-${testDomain.ident()}-large.png")
        assertTrue fileExists("prefixName-${testDomain.ident()}-medium.png")
        assertTrue fileExists("prefixName-${testDomain.ident()}-small.png")

        assertTrue testDomain.imageExtension == 'png'
        assertTrue testDomain.version > version
    }

    void testScaleImageMagickApproximate() {
        def testDomain = new TestDomain(image:getMultipartFile('image.jpg'))

        shouldFail(IllegalArgumentException){
            imageUploadService.save(testDomain)
        }
        assertNull testDomain.imageExtension

        Holders.config.bi = [
            renderingEngine: RenderingEngine.IMAGE_MAGICK,
            TestDomain: [
                outputDir: WEB_APP_RESULT_DIR,
                prefix: 'imageMagick',
                images: ['large':[scale:[width:800, height:600, type:ScaleType.APPROXIMATE]]
                ]
            ]
        ]

        shouldFail(IllegalArgumentException){
            imageUploadService.save(testDomain)
        }
        assertNull testDomain.imageExtension

        testDomain.save(flush:true)
        def version = testDomain.version

        assertNotNull testDomain.ident()
        imageUploadService.save(testDomain)

        assertTrue testDomain.imageExtension == 'jpg'
        assertTrue testDomain.version == version

        assertTrue fileExists("imageMagick-${testDomain.id}-large.jpg")

        version = testDomain.version

        testDomain.image = getMultipartFile('image.png')
        assertNotNull testDomain.ident()
        imageUploadService.save(testDomain, true)

        assertFalse fileExists("imageMagick-${testDomain.id}-large.jpg")
        assertTrue fileExists("imageMagick-${testDomain.id}-large.png")
        
        assertTrue testDomain.imageExtension == 'png'
        assertTrue testDomain.version > version
    }

    protected def getAbsolutePath(String uploadDir){
        applicationContext.getResource(uploadDir).getFile().toString()
    }
}

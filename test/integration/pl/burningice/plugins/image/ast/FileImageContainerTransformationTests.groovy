package pl.burningice.plugins.image.ast

import pl.burningice.plugins.image.ast.intarface.FileImageContainer
import grails.util.Holders
import pl.burningice.plugins.image.ast.test.TestDomainSecond
import pl.burningice.plugins.image.ast.test.TestDomain
import pl.burningice.plugins.image.ast.intarface.ImageContainer
import grails.test.GrailsUnitTestCase
import pl.burningice.plugins.image.test.FileUploadUtils

/**
 * @author pawel.gdula@burningice.pl
 */
@Mixin(FileUploadUtils)
class FileImageContainerTransformationTests extends GrailsUnitTestCase {

    protected static final def RESULT_DIR = './resources/resultImages/'

    protected void setUp() {
        super.setUp()
        cleanUpTestDir()
        Holders.config = new ConfigObject()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testFileImageContainerInterface() {

        println "fields: " + TestDomain.fields.name
        println "methods: " + TestDomain.methods.name
        println "interfaces: " + TestDomain.interfaces.name

        assertTrue !TestDomain.fields.name.contains('imageExtension')
        assertTrue TestDomain.methods.name.contains('getImageExtension')
        assertTrue TestDomain.methods.name.contains('setImageExtension')

        def testDomain = new TestDomain()
        assertTrue testDomain instanceof FileImageContainer
        assertTrue testDomain instanceof  ImageContainer
        assertNull testDomain.imageExtension

        testDomain = new TestDomain(imageExtension:'jpg')
        assertNotNull testDomain.imageExtension

        testDomain = new TestDomain()
        testDomain.imageExtension = 'gif'
        assertTrue testDomain.imageExtension == 'gif'
        testDomain.imageExtension = 'jpg'
        assertTrue testDomain.imageExtension == 'jpg'

        assertNotNull TestDomain.transients
        assertNotNull TestDomainSecond.transients

        println "TestDomain.transients = " + TestDomain.transients
        println "TestDomainSecond.transients = " + TestDomainSecond.transients

        assertTrue TestDomain.transients.contains('image')
        assertTrue TestDomainSecond.transients.contains('avatar')
        assertTrue TestDomainSecond.transients.contains('image')
        assertTrue TestDomainSecond.transients.contains('lastname')


        assertFalse TestDomain.fields.name.contains('image')
        assertFalse TestDomainSecond.fields.name.contains('avatar')
        assertFalse TestDomainSecond.fields.name.contains('image')

        assertTrue TestDomain.methods.name.contains('getImage')
        assertTrue TestDomain.methods.name.contains('setImage')
        assertTrue TestDomainSecond.methods.name.contains('getImage')
        assertTrue TestDomainSecond.methods.name.contains('getAvatar')
        assertTrue TestDomainSecond.methods.name.contains('setAvatar')
    }

    /**
     * It seems that there is no trancient fileds in tests ....
     * TODO: check on live
     */
    void _testTransientFields(){
        
        def testDomain = new TestDomain(image:getMultipartFile('image.jpg'))
        assertNotNull testDomain.image
        testDomain.save(flush:true)
        assertNotNull testDomain.ident()
        println "TestDomain.get(testDomain.ident()).image = " +  TestDomain.get(testDomain.ident()).image
        assertNull TestDomain.get(testDomain.ident()).image
        
        testDomain = new TestDomainSecond(avatar:getMultipartFile('image.jpg'), email:'test@test.pl', lastname:'xxxxx')
        assertNotNull testDomain.image
        assertNotNull testDomain.avatar
        
        testDomain.save(flush:true)
        assertNotNull testDomain.ident()

        assertNull TestDomainSecond.get(testDomain.ident()).lastname
        assertNull TestDomainSecond.get(testDomain.ident()).image
        assertNull TestDomainSecond.get(testDomain.ident()).avatar
    }
    
    void testFileImageContainerConstraints(){
        def testDomain = new TestDomain()
        assertFalse testDomain.hasErrors()
        assertTrue testDomain.validate()

        def testDomainSecond = new TestDomainSecond()
        assertFalse testDomainSecond.hasErrors()
        assertFalse testDomainSecond.validate()
        assertFalse testDomainSecond.errors.hasFieldErrors('imageExtension')
        assertTrue testDomainSecond.errors.hasFieldErrors('email')
    }

    void testImageConstraints(){
        def testDomain = new TestDomain()
        testDomain.validate()

        assertEquals testDomain.errors.getFieldErrors('image'), []

        Holders.config.bi.TestDomain = [
            constraints:null
        ]

        testDomain = new TestDomain()
        testDomain.validate()

        assertEquals testDomain.errors.getFieldErrors('image'), []
        
        Holders.config.bi.TestDomain = [
            constraints:[
                nullable:true
            ]
        ]

        testDomain = new TestDomain()
        testDomain.validate()

        assertEquals testDomain.errors.getFieldErrors('image'), []

        testDomain = new TestDomain(image:getEmptyMultipartFile())
        testDomain.validate()

        assertEquals testDomain.errors.getFieldErrors('image'), []

        Holders.config.bi.TestDomain = [
            constraints:[
                nullable:false
            ]
        ]
        
        testDomain = new TestDomain()
        testDomain.validate()

        assertEquals testDomain.errors.getFieldError('image').getCode(), 'nullable'

        testDomain = new TestDomain(image:getEmptyMultipartFile())
        testDomain.validate()

        assertEquals testDomain.errors.getFieldError('image').getCode(), 'nullable'

        Holders.config.bi.TestDomain = [
            constraints:[
                nullable:false,
                maxSize:50,
                contentType:['image/gif', 'image/png']
            ]
        ]

        def image = getMultipartFile('image.jpg')
        
        testDomain = new TestDomain(image:image)
        testDomain.validate()

        println testDomain.errors.getFieldErrors('image')
        assertEquals testDomain.errors.getFieldError('image').getCode(), 'maxSize.exceeded'

        Holders.config.bi.TestDomain.constraints.maxSize = image.getSize()
        testDomain.validate()
        
        println testDomain.errors.getFieldErrors('image')
        assertEquals testDomain.errors.getFieldError('image').getCode(), 'contentType.invalid'

        Holders.config.bi.TestDomain.constraints.contentType <<  image.getContentType()
        testDomain.validate()

        assertEquals testDomain.errors.getFieldErrors('image'), []

        Holders.config.bi.TestDomain.constraints.contentType = null
        testDomain.validate()

        assertEquals testDomain.errors.getFieldErrors('image'), []
    }

    void testBinding(){
        def image = getMultipartFile('image.jpg')

        def testDomain = new TestDomain(image:image)
        assertEquals testDomain.image, image

        testDomain = new TestDomain()
        testDomain.image = image
        assertEquals testDomain.image, image

        testDomain = new TestDomain()
        testDomain.properties = [image:image]
        assertEquals testDomain.image, image

        testDomain = new TestDomainSecond(avatar:image)
        assertEquals testDomain.avatar, image
        assertEquals testDomain.image, image

        testDomain = new TestDomainSecond()
        testDomain.avatar = image
        assertEquals testDomain.avatar, image
        assertEquals testDomain.image, image

        testDomain = new TestDomainSecond()
        testDomain.properties = [avatar:image]
        assertEquals testDomain.avatar, image
        assertEquals testDomain.image, image
    }
}

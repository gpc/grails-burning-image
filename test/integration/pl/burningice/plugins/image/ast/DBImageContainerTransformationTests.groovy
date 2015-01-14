package pl.burningice.plugins.image.ast

import grails.util.Holders
import pl.burningice.plugins.image.ast.intarface.DBImageContainer
import pl.burningice.plugins.image.ast.test.TestDbContainerDomainFirst
import pl.burningice.plugins.image.ast.test.TestDbContainerDomainSecond
import pl.burningice.plugins.image.ast.intarface.ImageContainer
import pl.burningice.plugins.image.ast.test.TestDbContainerDomainThird
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.orm.hibernate.cfg.HibernateMappingBuilder
import org.codehaus.groovy.grails.orm.hibernate.cfg.Mapping
import pl.burningice.plugins.image.test.FileUploadUtils
import grails.test.GrailsUnitTestCase

/**
 * @author pawel.gdula@burningice.pl
 */
@Mixin(FileUploadUtils)
class DBImageContainerTransformationTests extends GrailsUnitTestCase {

    protected static final def RESULT_DIR = './resources/resultImages/'

    protected void setUp() {
        super.setUp()
        cleanUpTestDir()
        Holders.config = new ConfigObject()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testLazyFalse(){
        def o, builder, mapping

        def validateLazyLoadingDisabled = {
            o =  GrailsClassUtils.getStaticPropertyValue(it, GrailsDomainClassProperty.MAPPING)
            builder = new HibernateMappingBuilder(it.name);
            mapping =  builder.evaluate((Closure) o)
            assertFalse mapping.getPropertyConfig('biImage').lazy
        }

        validateLazyLoadingDisabled TestDbContainerDomainFirst
        validateLazyLoadingDisabled TestDbContainerDomainSecond
        validateLazyLoadingDisabled TestDbContainerDomainThird
    }

    void testCachingEnabled(){
        def o, builder, mapping

        def validateCacheEnabled = {
            o =  GrailsClassUtils.getStaticPropertyValue(it, GrailsDomainClassProperty.MAPPING)
            builder = new HibernateMappingBuilder(it.name);
            mapping =  builder.evaluate((Closure) o)
            assertNotNull mapping.getPropertyConfig('biImage').cache
        }

        validateCacheEnabled TestDbContainerDomainFirst
        validateCacheEnabled TestDbContainerDomainSecond
        validateCacheEnabled TestDbContainerDomainThird

        o =  GrailsClassUtils.getStaticPropertyValue(TestDbContainerDomainThird, GrailsDomainClassProperty.MAPPING)
        builder = new HibernateMappingBuilder(TestDbContainerDomainThird.name);
        mapping =  builder.evaluate((Closure) o)
        assertEquals('full_name', mapping.getPropertyConfig('name').column)
    }

    void testDBImageContainerInterface() {
        def container

        println "fields: " + TestDbContainerDomainFirst.fields.name
        println "methods: " + TestDbContainerDomainFirst.methods.name
        println "interfaces: " + TestDbContainerDomainFirst.interfaces.name

        container = new TestDbContainerDomainFirst()
        assertTrue container instanceof  DBImageContainer
        assertTrue container instanceof  ImageContainer
        assertFalse TestDbContainerDomainFirst.fields.name.contains('biImage')
        assertFalse TestDbContainerDomainFirst.fields.name.contains('mapping')
        assertTrue TestDbContainerDomainSecond.methods.name.contains('getMapping')
        assertTrue TestDbContainerDomainSecond.methods.name.contains('setMapping')
        assertTrue TestDbContainerDomainFirst.methods.name.contains('getBiImage')
        assertTrue TestDbContainerDomainFirst.methods.name.contains('setBiImage')
        assertTrue TestDbContainerDomainFirst.methods.name.contains('getHasMany')
        assertTrue TestDbContainerDomainFirst.methods.name.contains('setHasMany')
        assertTrue TestDbContainerDomainFirst.methods.name.contains('beforeDelete')
        assertEquals(Image, TestDbContainerDomainFirst.hasMany?.biImage)
        
        println "fields: " + TestDbContainerDomainSecond.fields.name
        println "methods: " + TestDbContainerDomainSecond.methods.name
        println "interfaces: " + TestDbContainerDomainSecond.interfaces.name

        container = new TestDbContainerDomainSecond()
        assertTrue container instanceof  DBImageContainer
        assertFalse TestDbContainerDomainSecond.fields.name.contains('biImage')
        assertFalse TestDbContainerDomainFirst.fields.name.contains('mapping')
        assertTrue TestDbContainerDomainSecond.methods.name.contains('getBiImage')
        assertTrue TestDbContainerDomainSecond.methods.name.contains('setBiImage')
        assertTrue TestDbContainerDomainSecond.methods.name.contains('getMapping')
        assertTrue TestDbContainerDomainSecond.methods.name.contains('setMapping')
        assertTrue TestDbContainerDomainSecond.methods.name.contains('getHasMany')
        assertTrue TestDbContainerDomainSecond.methods.name.contains('setHasMany')
        assertTrue TestDbContainerDomainSecond.methods.name.contains('beforeDelete')
        assertEquals(TestDbContainerDomainSecond, TestDbContainerDomainSecond.hasMany?.children)
        assertEquals(Image, TestDbContainerDomainSecond.hasMany?.biImage)
    }

    void testFileImageContainerConstraints(){
         def testDomain = new TestDbContainerDomainFirst()
         assertFalse testDomain.hasErrors()
         assertTrue testDomain.validate()

         def testDomainSecond = new TestDbContainerDomainSecond()
         assertFalse testDomainSecond.hasErrors()
         assertFalse testDomainSecond.validate()
         assertFalse testDomainSecond.errors.hasFieldErrors('biImage')
         assertTrue testDomainSecond.errors.hasFieldErrors('name')
    }
    

    void testImageConstraints(){
        def testDomain = new TestDbContainerDomainFirst()
        testDomain.validate()

        assertEquals testDomain.errors.getFieldErrors('image'), []

        Holders.config.bi.TestDbContainerDomainFirst = [
            constraints:null
        ]

        testDomain = new TestDbContainerDomainFirst()
        testDomain.validate()

        assertEquals testDomain.errors.getFieldErrors('image'), []

        Holders.config.bi.TestDbContainerDomainFirst = [
            constraints:[
                nullable:true
            ]
        ]

        testDomain = new TestDbContainerDomainFirst()
        testDomain.validate()

        assertEquals testDomain.errors.getFieldErrors('image'), []

        testDomain = new TestDbContainerDomainFirst(image:getEmptyMultipartFile())
        testDomain.validate()

        assertEquals testDomain.errors.getFieldErrors('image'), []

        Holders.config.bi.TestDbContainerDomainFirst = [
            constraints:[
                nullable:false
            ]
        ]

        testDomain = new TestDbContainerDomainFirst()
        testDomain.validate()

        assertEquals testDomain.errors.getFieldError('image').getCode(), 'nullable'

        testDomain = new TestDbContainerDomainFirst(image:getEmptyMultipartFile())
        testDomain.validate()

        assertEquals testDomain.errors.getFieldError('image').getCode(), 'nullable'

        Holders.config.bi.TestDbContainerDomainFirst = [
            constraints:[
                nullable:false,
                maxSize:50,
                contentType:['image/gif', 'image/png']
            ]
        ]

        def image = getMultipartFile('image.jpg')

        testDomain = new TestDbContainerDomainFirst(image:image)
        testDomain.validate()

        println testDomain.errors.getFieldErrors('image')
        assertEquals testDomain.errors.getFieldError('image').getCode(), 'maxSize.exceeded'

        Holders.config.bi.TestDbContainerDomainFirst.constraints.maxSize = image.getSize()
        testDomain.validate()

        println testDomain.errors.getFieldErrors('image')
        assertEquals testDomain.errors.getFieldError('image').getCode(), 'contentType.invalid'

        Holders.config.bi.TestDbContainerDomainFirst.constraints.contentType <<  image.getContentType()
        testDomain.validate()

        assertEquals testDomain.errors.getFieldErrors('image'), []

        Holders.config.bi.TestDbContainerDomainFirst.constraints.contentType = null
        testDomain.validate()

        assertEquals testDomain.errors.getFieldErrors('image'), []
    }

    void testBinding(){
        def image = getMultipartFile('image.jpg')

        def testDomain = new TestDbContainerDomainFirst(image:image)
        assertEquals testDomain.image, image

        testDomain = new TestDbContainerDomainFirst()
        testDomain.image = image
        assertEquals testDomain.image, image

        testDomain = new TestDbContainerDomainFirst()
        testDomain.properties = [image:image]
        assertEquals testDomain.image, image

        testDomain = new TestDbContainerDomainSecond(logo:image)
        assertEquals testDomain.logo, image
        assertEquals testDomain.image, image

        testDomain = new TestDbContainerDomainSecond()
        testDomain.logo = image
        assertEquals testDomain.logo, image
        assertEquals testDomain.image, image

        testDomain = new TestDbContainerDomainSecond()
        testDomain.properties = [logo:image]
        assertEquals testDomain.logo, image
        assertEquals testDomain.image, image
    }
}
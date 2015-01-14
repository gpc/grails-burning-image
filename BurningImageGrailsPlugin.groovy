class BurningImageGrailsPlugin {
    // the plugin version
    def version = "0.5.2-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp",
        "grails-app/domain/pl/burningice/plugins/image/ast/test/**",
        "resources/**",
        "web-app/**"
    ]

    def author = "Pawel Gdula"
    def authorEmail = "pawel.gdula@burningice.pl"
    def title = "Burning Image"
    def description = """\
Image manipulation plugin that allows you to easily attach images to \
any domain class via an annotation. You can also configure the plugin \
to scale images and perform other operation."""

    // URL to the plugin's documentation
    def documentation = "http://code.google.com/p/burningimage/"

    def doWithSpring = {
        resourcePathProvider(pl.burningice.plugins.image.ResourcePathProvider)
        
        containerWorkerFactory(pl.burningice.plugins.image.container.ContainerWorkerFactory){
            resourcePathProvider = ref('resourcePathProvider')    
        }
    }
}

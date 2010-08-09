class UrlMappings {
    static mappings = {
        "/images/${imageId}-${size}.${type}" {
            controller = 'dbContainerImage'
            action = 'index'
        }

        "/$controller/$action?/$id?"{
	        constraints {
			  // apply constraints here
		    }
	    }
        "/"(view:"/index")
	    "500"(view:'/error')
    }
}

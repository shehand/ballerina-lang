import ballerina/http;
import ballerina/openapi;

listener http:Listener ep0 = new(80, config = {host: "petstore.openapi.io"});

listener http:Listener ep1 = new(443, config = {host: "petstore.swagger.io"});

@openapi:ServiceInfo {
    contract: "/resources/petstore.yaml",
    tags: [ ]
}
@http:ServiceConfig {
    basePath: "/v1"
}

service GigaClient on ep0, ep1 {
    
    @http:ResourceConfig {
        methods:["POST"],
        path:"/pets"
    }
    resource function resource_post_pets (http:Caller caller, http:Request req) returns error? {

    }

    @http:ResourceConfig {
        methods:["GET"],
        path:"/pets/{petId}"
    }
    resource function showPetById (http:Caller caller, http:Request req, string petId) returns error? {

    }

}
package it.cnr.isti.labsedc.bpmnpathextractor.RestInterface;

import it.cnr.isti.labsedc.bpmnpathextractor.BPMNParser;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.*;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/path_extractor")
public class PathExtractorResource {

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response postBPMN(String bpmnXMLString, @QueryParam("name") String name, @Context UriInfo uriInfo) {
        if (name == null) return Response.status(Status.BAD_REQUEST).build();
        int result = BPMNParser.parseXMLFromString(bpmnXMLString, name);
        if (result == 0) {
            URI uri = uriInfo.getAbsolutePathBuilder().path(name).build();
            return Response.created(uri).entity(bpmnXMLString).build();
        }
        else return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }

}
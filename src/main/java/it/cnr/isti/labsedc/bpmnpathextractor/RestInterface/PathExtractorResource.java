package it.cnr.isti.labsedc.bpmnpathextractor.RestInterface;

import it.cnr.isti.labsedc.bpmnpathextractor.BPMNFilter;
import it.cnr.isti.labsedc.bpmnpathextractor.BPMNParser;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNPath;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNProcess;
import it.cnr.isti.labsedc.bpmnpathextractor.ParametersReader;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.*;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Path("/path_extractor")
public class PathExtractorResource {

    private BPMNFilter bpmnFilter = new BPMNFilter();

    @GET
    @Path("/{bpmnName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaths(@PathParam("bpmnName") String bpmnName,
                             @DefaultValue("-1") @QueryParam("deepness") int deepness,
                             @QueryParam("poolID") List<String> poolsID,
                             @QueryParam("laneID") List<String> lanesID) {

        ArrayList<BPMNProcess> processes = bpmnFilter.extractPathsFromBPMNName(bpmnName, deepness, poolsID, lanesID);
        ParametersReader.printPaths(processes);
        return Response.ok().entity(processes.size()).build();

    }

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
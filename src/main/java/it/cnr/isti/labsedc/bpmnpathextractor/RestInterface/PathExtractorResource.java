package it.cnr.isti.labsedc.bpmnpathextractor.RestInterface;

import it.cnr.isti.labsedc.bpmnpathextractor.BPMNParser;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNPath;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.FlowObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.*;
import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Path("/path_extractor")
public class PathExtractorResource {

    @GET
    @Path("/{bpmnName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaths(@PathParam("bpmnName") String bpmnName,
                             @DefaultValue("-1") @QueryParam("deepness") int deepness,
                             @QueryParam("poolID") List<String> poolsID,
                             @QueryParam("laneID") List<String> lanesID) {

        try {

            Socket serverChannel = new Socket("localhost", 13500);
            ObjectInputStream inputStream = new ObjectInputStream(serverChannel.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(serverChannel.getOutputStream());
            outputStream.writeObject(bpmnName);
            outputStream.writeObject(deepness);
            outputStream.writeObject(poolsID.size());
            for (String poolID : poolsID)
                outputStream.writeObject(poolID);
            outputStream.writeObject(lanesID.size());
            for (String laneID : lanesID)
                outputStream.writeObject(laneID);
            outputStream.writeObject(1);
            int processesNumber = (int) inputStream.readObject();

            ArrayList<JSONProcess> processes = new ArrayList<>();

            for (int i = 0; i < processesNumber; i++) {

                String processID = (String) inputStream.readObject();
                ArrayList<BPMNPath> bpmnPaths = (ArrayList<BPMNPath>) inputStream.readObject();

                JSONProcess process = new JSONProcess(processID);

                for (BPMNPath bpmnPath : bpmnPaths) {

                    ArrayList<String> path = new ArrayList<>();

                    for (FlowObject flowObject : bpmnPath.getFlowObjects())
                        path.add(flowObject.getId());

                    process.addPath(path);

                }

                processes.add(process);

            }

            return Response.ok().entity(processes).build();

        } catch (IOException | ClassNotFoundException e) {
            return Response.serverError().build();
        }

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
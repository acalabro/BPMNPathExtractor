package it.cnr.isti.labsedc.bpmnpathextractor.RestInterface;

import it.cnr.isti.labsedc.bpmnpathextractor.BPMNFilter;
import it.cnr.isti.labsedc.bpmnpathextractor.BPMNParser;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNPath;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNProcess;

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
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
@Path("/path_extractor")
public class PathExtractorResource {

    private Socket serverChannel;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    @GET
    @Path("/{bpmnName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaths(@PathParam("bpmnName") String bpmnName,
                             @DefaultValue("-1") @QueryParam("deepness") int deepness,
                             @QueryParam("poolID") List<String> poolsID,
                             @QueryParam("laneID") List<String> lanesID) {

        HashMap<String, ArrayList<BPMNPath>> processesPaths = new HashMap<>();

        try {
            serverChannel = new Socket("localhost", 13500);
            inputStream = new ObjectInputStream(serverChannel.getInputStream());
            outputStream = new ObjectOutputStream(serverChannel.getOutputStream());
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
            for (int i = 0; i < processesNumber; i++) {
                String processID = (String) inputStream.readObject();
                ArrayList<BPMNPath> paths = (ArrayList<BPMNPath>) inputStream.readObject();
                processesPaths.put(processID, paths);
            }

        } catch (IOException | ClassNotFoundException e) {
            return Response.serverError().build();
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String processID : processesPaths.keySet()) {
            stringBuilder.append(processID).append(System.lineSeparator()).append(System.lineSeparator());
            for (BPMNPath path : processesPaths.get(processID))
                stringBuilder.append(path.toString()).append(System.lineSeparator());
        }

        return Response.ok().entity(stringBuilder.toString()).build();

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
package it.cnr.isti.labsedc.bpmnpathextractor;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNPath;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNProcess;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.Connections.Connection;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.Events.EndEvent;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.FlowObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class BPMNPathExtractor {

    public static void extractPaths(BPMNProcess process) {

        for (String startEventID : process.getStartEvents()) {
            BPMNPath path = new BPMNPath(process.getPathID());
            path.appendFlowObject(process.getFlowObject(startEventID));
            createPath(process, path);
        }

    }

    private static void createPath(BPMNProcess process, BPMNPath path) {

        LinkedList<BPMNPath> incompletePaths = new LinkedList<>();
        FlowObject currentFlowObject = process.getFlowObject(path.getLastFlowObject().getId());
        ArrayList<String> currentOutgoingConnections = currentFlowObject.getOutgoingConnections();

        while (currentOutgoingConnections.size() > 0) {

            for (int i = 1; i < currentOutgoingConnections.size(); i++) {
                String connectionID = currentOutgoingConnections.get(i);
                if (path.isPresentConnection(connectionID)) continue;
                Connection connection = process.getConnection(connectionID);

                BPMNPath incompletePath = new BPMNPath(path, process.getPathID());
                incompletePath.appendFlowObject(process.getFlowObject(connection.getTargetRef()));
                incompletePath.addConnection(connectionID, connection);
                incompletePaths.addLast(incompletePath);
            }

            String connectionID = currentOutgoingConnections.get(0);
            if (path.isPresentConnection(connectionID) && currentOutgoingConnections.size() > 1) continue;
            Connection connection = process.getConnection(connectionID);

            currentFlowObject = process.getFlowObject(connection.getTargetRef());
            path.appendFlowObject(currentFlowObject);
            path.addConnection(connectionID, connection);
            currentOutgoingConnections = currentFlowObject.getOutgoingConnections();

        }

        if (path.getLastFlowObject() instanceof EndEvent)
            process.addPath(path);

        while (!incompletePaths.isEmpty()) {
            createPath(process, incompletePaths.removeLast());
        }

    }

}

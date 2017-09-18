package it.cnr.isti.labsedc;

import it.cnr.isti.labsedc.Objects.BPMNPath;
import it.cnr.isti.labsedc.Objects.BPMNProcess;
import it.cnr.isti.labsedc.Objects.Connections.Connection;
import it.cnr.isti.labsedc.Objects.FlowObjects.Events.EndEvent;
import it.cnr.isti.labsedc.Objects.FlowObjects.FlowObject;

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
                BPMNPath incompletePath = new BPMNPath(path, process.getPathID());
                Connection connection = process.getConnection(currentOutgoingConnections.get(i));
                incompletePath.appendFlowObject(process.getFlowObject(connection.getTargetRef()));
                incompletePaths.addLast(incompletePath);
            }

            Connection connection = process.getConnection(currentOutgoingConnections.get(0));
            currentFlowObject = process.getFlowObject(connection.getTargetRef());
            path.appendFlowObject(currentFlowObject);
            currentOutgoingConnections = currentFlowObject.getOutgoingConnections();

        }

        if (path.getLastFlowObject() instanceof EndEvent)
            process.addPath(path);

        while (!incompletePaths.isEmpty()) {
            createPath(process, incompletePaths.removeLast());
        }

    }

}

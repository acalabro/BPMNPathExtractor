package it.cnr.isti.labsedc.bpmnpathextractor;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNCycle;
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
        ArrayList<String> currentOutgoingConnections = new ArrayList<>(currentFlowObject.getOutgoingConnections());

        while (currentOutgoingConnections.size() > 0) {

            String cycleConnection = null;
            BPMNCycle cycle = null;

            if (currentOutgoingConnections.size() > 1) {
                cycle = process.getCycleByRoot(currentFlowObject.getId());
                for (String connection : currentOutgoingConnections)
                    if (path.isPresentConnection(connection)) {
                        cycleConnection = connection;
                        String nextObjectID = process.getConnection(connection).getTargetRef();
                        createCycle(process, path, currentFlowObject,
                                process.getFlowObject(nextObjectID),
                                cycleConnection, process.getCycleID());
                    }
            }

            if (cycle != null) cycleConnection = cycle.getRootToFirst();
            if (cycleConnection != null) break;

            for (int i = 1; i < currentOutgoingConnections.size(); i++) {
                String connectionID = currentOutgoingConnections.get(i);
                Connection connection = process.getConnection(connectionID);
                BPMNPath incompletePath = new BPMNPath(path, process.getPathID());
                incompletePath.appendFlowObject(process.getFlowObject(connection.getTargetRef()));
                incompletePath.addConnection(connectionID, connection);
                incompletePaths.addLast(incompletePath);
            }

            String connectionID = currentOutgoingConnections.get(0);
            Connection connection = process.getConnection(connectionID);
            currentFlowObject = process.getFlowObject(connection.getTargetRef());
            path.appendFlowObject(currentFlowObject);
            path.addConnection(connectionID, connection);
            currentOutgoingConnections = new ArrayList<>(currentFlowObject.getOutgoingConnections());

        }

        if (path.getLastFlowObject() instanceof EndEvent)
            process.addPath(path);

        while (!incompletePaths.isEmpty()) {
            createPath(process, incompletePaths.removeLast());
        }

    }

    private static void createCycle(BPMNProcess process, BPMNPath path,
                                    FlowObject rootObject, FlowObject firstCycleObject,
                                    String rootToFirst, int cycleID) {

        BPMNCycle cycle = new BPMNCycle(cycleID, rootObject, rootToFirst);
        boolean inCycle = false;

        for (FlowObject flowObject : path.getFlowObjects()) {
            if (cycle.isPresentFlowObject(flowObject)) return;
            if (flowObject.equals(firstCycleObject) && !inCycle) inCycle = true;
            if (inCycle) cycle.appendFlowObject(flowObject);
            if (flowObject.equals(rootObject) && inCycle) break;
        }

        process.addCycle(cycle);

    }

}

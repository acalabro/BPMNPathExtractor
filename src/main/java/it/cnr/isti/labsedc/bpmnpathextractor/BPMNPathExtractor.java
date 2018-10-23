package it.cnr.isti.labsedc.bpmnpathextractor;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.*;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.Connections.Connection;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.Activities.SubProcess;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.Events.EndEvent;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.FlowObject;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.Gateways.Gateway;

import java.util.ArrayList;
import java.util.LinkedList;

public class BPMNPathExtractor {

    private ExtractionType extractionType;

    public BPMNPathExtractor() { extractionType = ExtractionType.TYPE_ALFA; }

    public void extractPaths(BPMNProcess process) {

        switch (extractionType) {
            case TYPE_ALFA:
                for (String startEventID : process.getStartEvents()) {
                    BPMNPath path = new BPMNPath(process.getPathID());
                    path.appendFlowObject(process.getFlowObject(startEventID));
                    createPath(process, path);
                    ArrayList<BPMNPath> paths = new ArrayList<>(process.getPaths());
                    for (BPMNPath pathToExplode : paths)
                        explodePathWithCycles(process, pathToExplode, process.getCycles(), 0);
                }
                break;
		    default:
                throw new UnsupportedOperationException("Extraction type not supported.");
        }

    }

    private void createPath(BPMNProcess process, BPMNPath path) {

        LinkedList<BPMNPath> incompletePaths = new LinkedList<>();
        FlowObject currentFlowObject = process.getFlowObject(path.getLastFlowObject().getId());
        ArrayList<String> currentOutgoingConnections = new ArrayList<>(currentFlowObject.getOutgoingConnections());

        while (currentOutgoingConnections.size() > 0) {

            boolean cycleConnection = false;
            ArrayList<BPMNCycle> cycles = null;

            if (currentOutgoingConnections.size() > 1) {
                cycles = process.getCyclesByRoot(currentFlowObject.getId());
                for (String connection : currentOutgoingConnections)
                    if (path.isPresentConnection(connection)) {
                        cycleConnection = true;
                        String nextObjectID = process.getConnection(connection).getTargetRef();
                        createCycle(process, path, currentFlowObject,
                                process.getFlowObject(nextObjectID),
                                connection, process.getCycleID());
                    }
            }

            if (cycleConnection || (cycles != null && cycles.size() > 0)) break;

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

    private void createCycle(BPMNProcess process, BPMNPath path,
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

    private void explodePathWithCycles(BPMNProcess process, BPMNPath path, ArrayList<BPMNCycle> cycles, int index) {

        LinkedList<FlowObject> flowObjects = path.getFlowObjects();
        for (int i = index; i < flowObjects.size(); i++) {
            FlowObject flowObject = flowObjects.get(i);
            for (BPMNCycle cycle : cycles) {
                if (cycle.getRootObject().equals(flowObject)) {
                    BPMNPath incompletePath = new BPMNPath(path, process.getPathID());
                    LinkedList<FlowObject> cycleObjects = cycle.getFlowObjects();
                    for (int j = 0; j < cycleObjects.size(); j++)
                        incompletePath.addFlowObject(i + j + 1, cycleObjects.get(j));
                    process.addPath(incompletePath);
                    ArrayList<BPMNCycle> newCycles = new ArrayList<>(cycles);
                    newCycles.remove(cycle);
                    explodePathWithCycles(process, incompletePath, newCycles, i + 1);
                }
            }
        }

    }

    public void explodeProcessesWithSubProcesses(ArrayList<BPMNProcess> processes) {
        for (BPMNProcess process : processes) {
            if (process.getDeepness() == 0) {
                ArrayList<BPMNPath> paths = new ArrayList<>(process.getPaths());
                for (BPMNPath path : paths) {
                    process.removePath(path);
                    explodePathWithSubProcesses(processes, process, path, 0);
                }
            }
        }

    }

    private void explodePathWithSubProcesses(ArrayList<BPMNProcess> processes, BPMNProcess process, BPMNPath path, int index) {

        LinkedList<FlowObject> flowObjects = path.getFlowObjects();
        for (int i = index; i < flowObjects.size(); i++) {
            FlowObject flowObject = flowObjects.get(i);
            if (flowObject instanceof SubProcess) {
                BPMNProcess subProcess = null;
                for (BPMNProcess tempProcess : processes) {
                    if (tempProcess.getDeepness() > 0 && tempProcess.getParentObject().equals(flowObject)) {
                        subProcess = tempProcess;
                        break;
                    }
                }
                if (subProcess != null) {
                    for (BPMNPath subProcessPath : subProcess.getPaths()) {
                        BPMNPath incompletePath = new BPMNPath(path, process.getPathID());
                        LinkedList<FlowObject> subProcessObjects = subProcessPath.getFlowObjects();
                        for (int j = 0; j < subProcessObjects.size(); j++)
                            incompletePath.addFlowObject(i + j + 1, subProcessObjects.get(j));
                        explodePathWithSubProcesses(processes, process, incompletePath, i + 1);
                    }
                    return;
                }
            }
        }
        process.addPath(path);

    }

    public void setExtractionType(ExtractionType extractionType) { this.extractionType = extractionType; }
}

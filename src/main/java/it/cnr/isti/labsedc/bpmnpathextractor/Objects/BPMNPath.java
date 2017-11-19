package it.cnr.isti.labsedc.bpmnpathextractor.Objects;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.Connections.Connection;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.FlowObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class BPMNPath implements Serializable {

    private final int id;
    private LinkedList<FlowObject> flowObjects;
    private HashMap<String, Connection> connections;

    public BPMNPath(int id) {
        this.id = id;
        flowObjects = new LinkedList<>();
        connections = new HashMap<>();
    }

    public BPMNPath(BPMNPath path, int id) {
        this.id = id;
        flowObjects = new LinkedList<>(path.getFlowObjects());
        connections = new HashMap<>(path.getConnections());
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ID: ").append(id).append(System.lineSeparator())
                .append("Path Elements: ").append(System.lineSeparator());

        for (FlowObject flowObject : flowObjects) {
            if (flowObject.getName() != null) stringBuilder.append(flowObject.getName());
            else stringBuilder.append(flowObject.getId());
            stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString();

    }

    public int getId() { return id; }

    public void appendFlowObject(FlowObject flowObject) { flowObjects.addLast(flowObject); }
    public void addFlowObject(int index, FlowObject flowObject) { flowObjects.add(index, flowObject); }

    public LinkedList<FlowObject> getFlowObjects() { return flowObjects; }
    public FlowObject getFirstFlowObject() { return flowObjects.getFirst(); }
    public FlowObject getLastFlowObject() { return flowObjects.getLast(); }
    public boolean isPresentFlowObject(FlowObject flowObject) { return flowObjects.contains(flowObject); }

    public HashMap<String, Connection> getConnections() { return connections; }
    public void addConnection(String key, Connection connection) { connections.put(key, connection); }
    public boolean isPresentConnection(String connectionID) { return connections.containsKey(connectionID); }

}

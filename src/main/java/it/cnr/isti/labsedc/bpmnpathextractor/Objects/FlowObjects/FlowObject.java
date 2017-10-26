package it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNObject;

import java.util.ArrayList;

public abstract class FlowObject implements BPMNObject {

    protected final String id;
    protected final String name;
    private ArrayList<String> incomingConnections;
    private ArrayList<String> outgoingConnections;
    private ArrayList<String> parentLanes;

    public FlowObject(String id, String name) {
        this.id = id;
        this.name = name;
        incomingConnections = new ArrayList<>();
        outgoingConnections = new ArrayList<>();
        parentLanes = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Incoming Connections: ").append(System.lineSeparator());

        for (String incomingConnection : incomingConnections)
            stringBuilder.append(incomingConnection).append(System.lineSeparator());
        stringBuilder.append("Outgoing Connections: ").append(System.lineSeparator());

        for (String outgoingConnection : outgoingConnections)
            stringBuilder.append(outgoingConnection).append(System.lineSeparator());
        stringBuilder.append("Parent Lanes: ").append(System.lineSeparator());

        for (String laneID : parentLanes)
            stringBuilder.append(laneID).append(System.lineSeparator());
        return stringBuilder.toString();
    }

    public ArrayList<String> getIncomingConnections() { return incomingConnections; }
    public ArrayList<String> getOutgoingConnections() { return outgoingConnections; }
    public ArrayList<String> getParentLanes() { return parentLanes; }

    public void addIncomingConnection(String connectionID) { incomingConnections.add(connectionID); }
    public void addOutgoingConnection(String connectionID) { outgoingConnections.add(connectionID); }
    public void addParentLane(String laneID) { parentLanes.add(laneID); }

    public String getId() { return id; }
    public String getName() { return name; }

    public boolean hasParentLane(String laneID) { return parentLanes.contains(laneID); }

}

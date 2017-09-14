package Objects.FlowObjects;

import Objects.BPMNObject;

import java.util.ArrayList;

public class FlowObject extends BPMNObject {

    private ArrayList<String> incomingConnections;
    private ArrayList<String> outgoingConnections;

    public FlowObject(String id, String name) {
        super(id, name);
        incomingConnections = new ArrayList<>();
        outgoingConnections = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Incoming Connections: ").append(System.lineSeparator());
        for (String incomingConnection : incomingConnections) {
            stringBuilder.append(incomingConnection).append(System.lineSeparator());
        }
        stringBuilder.append("Outgoing Connections: ").append(System.lineSeparator());
        for (String outgoingConnection : outgoingConnections) {
            stringBuilder.append(outgoingConnection).append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    public void addIncomingConnection(String connectionID) { incomingConnections.add(connectionID); }
    public void addOutgoingConnection(String connectionID) { outgoingConnections.add(connectionID); }

}

package it.cnr.isti.labsedc.Objects;

import it.cnr.isti.labsedc.Objects.FlowObjects.FlowObject;

import java.util.LinkedList;

public class BPMNPath {

    private final int id;
    private LinkedList<FlowObject> flowObjects;

    public BPMNPath(int id) {
        this.id = id;
        flowObjects = new LinkedList<>();
    }

    public BPMNPath(BPMNPath path, int id) {
        this.id = id;
        flowObjects = new LinkedList<>();
        for (FlowObject flowObject : path.getFlowObjects()) flowObjects.addLast(flowObject);
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

    public LinkedList<FlowObject> getFlowObjects() { return flowObjects; }
    public FlowObject getFirstFlowObject() { return flowObjects.getFirst(); }
    public FlowObject getLastFlowObject() { return flowObjects.getLast(); }

}

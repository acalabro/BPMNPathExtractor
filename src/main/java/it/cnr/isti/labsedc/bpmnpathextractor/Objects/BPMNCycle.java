package it.cnr.isti.labsedc.bpmnpathextractor.Objects;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.FlowObject;

import java.util.LinkedList;

public class BPMNCycle {

    private final int id;
    private final FlowObject rootObject;
    private LinkedList<FlowObject> flowObjects;

    public BPMNCycle(int id, FlowObject rootObject) {
        this.id = id;
        this.rootObject = rootObject;
        flowObjects = new LinkedList<>();
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ID: ").append(id).append(System.lineSeparator())
                .append("RootObject: ").append(rootObject.getName()).append(System.lineSeparator())
                .append("Cycle Elements: ").append(System.lineSeparator());

        for (FlowObject flowObject : flowObjects) {
            if (flowObject.getName() != null) stringBuilder.append(flowObject.getName());
            else stringBuilder.append(flowObject.getId());
            stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString();

    }

    public int getId() { return id; }
    public FlowObject getRootObjectID() { return rootObject; }
    public LinkedList<FlowObject> getFlowObjects() { return flowObjects; }
    public boolean isPresentFlowObject(FlowObject flowObject) { return flowObjects.contains(flowObject); }

    public void appendFlowObject(FlowObject flowObject) { flowObjects.addLast(flowObject); }

}

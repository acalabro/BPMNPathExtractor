package it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicActivities;

import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicFlowObject;

public class GraphicActivity extends GraphicFlowObject {

    private final ActivityType activityType;

    public GraphicActivity(String id, String name, ActivityType activityType) {
        super(id, name);
        this.activityType = activityType;
    }

    @Override
    public String toString() {
        return "ID: " + id + System.lineSeparator() +
                "Name: " + name + System.lineSeparator() +
                "Type: " + activityType + System.lineSeparator() +
                super.toString();
    }

    public ActivityType getActivityType() { return activityType; }

}

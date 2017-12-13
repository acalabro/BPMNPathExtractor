package it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicEvents;

import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicFlowObject;

public class GraphicEvent extends GraphicFlowObject {

    private final EventType eventType;

    public GraphicEvent(String id, String name, EventType eventType) {
        super(id, name);
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "ID: " + id + System.lineSeparator() +
                "Name: " + name + System.lineSeparator() +
                "Type: " + eventType + System.lineSeparator() +
                super.toString();
    }

    public EventType getEventType() { return eventType; }

}

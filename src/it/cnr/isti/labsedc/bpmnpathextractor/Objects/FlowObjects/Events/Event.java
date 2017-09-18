package it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.Events;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.FlowObject;

public class Event extends FlowObject {

    private final EventType eventType;

    public Event(String id, String name, EventType eventType) {
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

package it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects;

import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.BPMNGraphicObject;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class GraphicFlowObject implements BPMNGraphicObject {

    protected final String id;
    protected final String name;
    private int posX;
    private int posY;
    private int width;
    private int height;
    private ArrayList<String> incomingConnections;
    private ArrayList<String> outgoingConnections;
    private ArrayList<String> parentLanes;
    private HashMap<String, String> properties;

    public GraphicFlowObject(String id, String name) {
        this.id = id;
        this.name = name;
        incomingConnections = new ArrayList<>();
        outgoingConnections = new ArrayList<>();
        parentLanes = new ArrayList<>();
        properties = new HashMap<>();
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

        if (!parentLanes.isEmpty()) {
            stringBuilder.append("Parent Lanes: ").append(System.lineSeparator());

            for (String laneID : parentLanes)
                stringBuilder.append(laneID).append(System.lineSeparator());
        }

        stringBuilder.append("x: ").append(posX).append(", y: ").append(posY).append(System.lineSeparator())
                .append("width: ").append(width).append(", height ").append(height).append(System.lineSeparator());

        return stringBuilder.toString();
    }

    public void addIncomingConnection(String connectionID) { incomingConnections.add(connectionID); }
    public void addOutgoingConnection(String connectionID) { outgoingConnections.add(connectionID); }
    public void addParentLane(String laneID) { parentLanes.add(laneID); }
    public void addProperty(String key, String value) { properties.put(key, value); }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public ArrayList<String> getParentLanes() { return parentLanes; }
    public HashMap<String, String> getProperties() { return properties; }

    public void setPosX(int posX) { this.posX = posX; }
    public void setPosY(int posY) { this.posY = posY; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }

    public void setParentLanes(ArrayList<String> parentLanes) { this.parentLanes = new ArrayList<>(parentLanes); }
    public void setProperties(HashMap<String, String> properties) { this.properties = properties; }
}
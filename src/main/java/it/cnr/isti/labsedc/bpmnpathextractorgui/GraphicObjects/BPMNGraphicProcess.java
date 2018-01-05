package it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects;

import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicConnections.GraphicConnection;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicFlowObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BPMNGraphicProcess {

    private final String id;
    private final String name;
    private final String parentProcessID;
    private final GraphicFlowObject parentObject;
    private final boolean executable;
    private final int deepness;
    private int posX;
    private int posY;
    private int width;
    private int height;
    private String poolID;
    private String poolName;
    private ArrayList<String> startEvents;
    private HashMap<String, GraphicFlowObject> flowObjects;
    private HashMap<String, GraphicConnection> connections;
    private HashMap<String, GraphicConnection> messageFlows;
    private ArrayList<String> innerLanes;
    private ArrayList<LaneCoordinate> lanesCoordinates;

    public BPMNGraphicProcess(String id, String name, String parentProcessID, GraphicFlowObject parentObject, boolean executable, int deepness) {
        this.id = id;
        this.name = name;
        this.parentProcessID = parentProcessID;
        this.parentObject = parentObject;
        this.executable = executable;
        this.deepness = deepness;
        startEvents = new ArrayList<>();
        flowObjects = new HashMap<>();
        connections = new HashMap<>();
        messageFlows = new HashMap<>();
        innerLanes = new ArrayList<>();
        lanesCoordinates = new ArrayList<>();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return  false;
        if (getClass() != obj.getClass()) return false;
        BPMNGraphicProcess process = (BPMNGraphicProcess) obj;
        return this.id.equals(process.id);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ID: ").append(id).append(System.lineSeparator()).
                append("Name: ").append(name).append(System.lineSeparator()).
                append("Is Executable: ").append(executable).append(System.lineSeparator()).
                append(System.lineSeparator());
        for (String key : flowObjects.keySet())
            stringBuilder.append(flowObjects.get(key).toString()).append(System.lineSeparator());
        for (String key : connections.keySet())
            stringBuilder.append(connections.get(key).toString()).append(System.lineSeparator());
        return stringBuilder.toString();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getParentProcessID() { return parentProcessID; }
    public GraphicFlowObject getParentObject() { return parentObject; }
    public boolean isExecutable() { return executable; }
    public int getDeepness() { return deepness; }
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getHeight() { return height; }
    public int getWidth() { return width; }

    public String getPoolID() { return poolID; }
    public String getPoolName() { return poolName; }

    public ArrayList<String> getStartEvents() { return startEvents; }
    public HashMap<String, GraphicFlowObject> getFlowObjects() { return flowObjects; }
    public HashMap<String, GraphicConnection> getConnections() { return connections; }
    public HashMap<String, GraphicConnection> getMessageFlows() { return messageFlows; }
    public ArrayList<String> getInnerLanes() { return innerLanes; }
    public ArrayList<LaneCoordinate> getLanesCoordinates() { return lanesCoordinates; }

    public GraphicFlowObject getFlowObject(String id) { return flowObjects.get(id); }
    public GraphicConnection getConnection(String id) { return connections.get(id); }
    public GraphicConnection getMessageFlow(String id) { return messageFlows.get(id); }

    public boolean isPresentLane(String laneID) { return innerLanes.contains(laneID); }

    public void setPosX(int posX) { this.posX = posX; }
    public void setPosY(int posY) { this.posY = posY; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }

    public void setPoolID(String poolID) { this.poolID = poolID; }
    public void setPoolName(String poolName) { this.poolName = poolName; }

    public void setInnerLanes(ArrayList<String> innerLanes) { this.innerLanes = new ArrayList<>(innerLanes); }

    public void addStartEvent(String id) { startEvents.add(id); }
    public void addFlowObject(String key, GraphicFlowObject flowObject) { flowObjects.put(key, flowObject); }
    public void addConnection(String key, GraphicConnection connection) { connections.put(key, connection); }
    public void addMessageFlow(String key, GraphicConnection messageFlow) { messageFlows.put(key, messageFlow); }
    public void addInnerLane(String laneID) { innerLanes.add(laneID); }

    public void addLaneCoordinate(String laneID, int posX, int posY, int width, int height) {
        lanesCoordinates.add(new LaneCoordinate(laneID, posX, posY, width, height));
    }

}

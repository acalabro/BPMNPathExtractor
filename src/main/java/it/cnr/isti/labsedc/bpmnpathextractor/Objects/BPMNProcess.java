package it.cnr.isti.labsedc.bpmnpathextractor.Objects;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.Connections.Connection;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.FlowObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BPMNProcess {

    private final String id;
    private final String name;
    private final String parentProcessID;
    private final FlowObject parentObject;
    private final boolean executable;
    private final int deepness;
    private String poolID;
    private String poolName;
    private ArrayList<String> startEvents;
    private HashMap<String, FlowObject> flowObjects;
    private HashMap<String, Connection> connections;
    private ArrayList<String> innerLanes;
    private ArrayList<BPMNPath> paths;
    private ArrayList<BPMNPath> filteredPaths;
    private ArrayList<BPMNCycle> cycles;
    private int pathID;
    private int cycleID;

    public BPMNProcess(String id, String name, String parentProcessID, FlowObject parentObject, boolean executable, int deepness) {
        this.id = id;
        this.name = name;
        this.parentProcessID = parentProcessID;
        this.parentObject = parentObject;
        this.executable = executable;
        this.deepness = deepness;
        startEvents = new ArrayList<>();
        flowObjects = new HashMap<>();
        connections = new HashMap<>();
        innerLanes = new ArrayList<>();
        paths = new ArrayList<>();
        filteredPaths = new ArrayList<>();
        cycles = new ArrayList<>();
        pathID = 0;
        cycleID = 0;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return  false;
        if (getClass() != obj.getClass()) return false;
        BPMNProcess process = (BPMNProcess) obj;
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

    public int getPathID() { return pathID++; }
    public int getCycleID() { return cycleID++; }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getParentProcessID() { return parentProcessID; }
    public FlowObject getParentObject() { return parentObject; }
    public boolean isExecutable() { return executable; }
    public int getDeepness() { return deepness; }
    public String getPoolID() { return poolID; }
    public String getPoolName() { return poolName; }

    public ArrayList<String> getStartEvents() { return startEvents; }
    public HashMap<String, FlowObject> getFlowObjects() { return flowObjects; }
    public HashMap<String, Connection> getConnections() { return connections; }
    public ArrayList<String> getInnerLanes() { return innerLanes; }

    public ArrayList<BPMNPath> getPaths() { return paths; }
    public ArrayList<BPMNPath> getFilteredPaths() { return filteredPaths; }
    public ArrayList<BPMNCycle> getCycles() { return cycles; }

    public FlowObject getFlowObject(String id) { return flowObjects.get(id); }
    public Connection getConnection(String id) { return connections.get(id); }

    public ArrayList<BPMNCycle> getCyclesByRoot(String id) {
        ArrayList<BPMNCycle> rootCycles = new ArrayList<>();
        for (BPMNCycle cycle : cycles)
            if (cycle.getRootObject().getId().equals(id))
                rootCycles.add(cycle);
        return rootCycles;
    }

    public boolean isPresentLane(String laneID) { return innerLanes.contains(laneID); }

    public void setPoolID(String poolID) { this.poolID = poolID; }
    public void setPoolName(String poolName) { this.poolName = poolName; }

    public void setFilteredPaths(ArrayList<BPMNPath> filteredPaths) { this.filteredPaths = new ArrayList<>(filteredPaths); }
    public void setInnerLanes(ArrayList<String> innerLanes) { this.innerLanes = new ArrayList<>(innerLanes); }

    public void addStartEvent(String id) { startEvents.add(id); }
    public void addFlowObject(String key, FlowObject flowObject) { flowObjects.put(key, flowObject); }
    public void addConnection(String key, Connection connection) { connections.put(key, connection); }
    public void addInnerLane(String laneID) { innerLanes.add(laneID); }
    public void addPath(BPMNPath path) { paths.add(path); }
    public void addFilteredPath(BPMNPath path) { filteredPaths.add(path); }
    public void addCycle(BPMNCycle cycle) { cycles.add(cycle); }

    public void removePath(BPMNPath path) { paths.remove(path); }

}

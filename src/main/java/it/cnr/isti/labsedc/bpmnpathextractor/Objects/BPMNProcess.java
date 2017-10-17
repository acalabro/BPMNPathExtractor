package it.cnr.isti.labsedc.bpmnpathextractor.Objects;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.Connections.Connection;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.FlowObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BPMNProcess {

    private final String id;
    private final String name;
    private final boolean executable;
    private ArrayList<String> startEvents;
    private HashMap<String, FlowObject> flowObjects;
    private HashMap<String, Connection> connections;
    private ArrayList<BPMNPath> paths;
    private ArrayList<BPMNCycle> cycles;
    private int pathID;
    private int cycleID;

    public BPMNProcess(String id, String name, boolean executable) {
        this.id = id;
        this.name = name;
        this.executable = executable;
        startEvents = new ArrayList<>();
        flowObjects = new HashMap<>();
        connections = new HashMap<>();
        paths = new ArrayList<>();
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
    public boolean isExecutable() { return executable; }

    public ArrayList<String> getStartEvents() { return startEvents; }
    public HashMap<String, FlowObject> getFlowObjects() { return flowObjects; }
    public HashMap<String, Connection> getConnections() { return connections; }
    public ArrayList<BPMNPath> getPaths() { return paths; }
    public ArrayList<BPMNCycle> getCycles() { return cycles; }

    public FlowObject getFlowObject(String id) { return flowObjects.get(id); }
    public Connection getConnection(String id) { return connections.get(id); }
    public BPMNCycle getCycleByRoot(String id) {
        for (BPMNCycle cycle : cycles) if (cycle.getRootObject().getId().equals(id)) return cycle;
        return null;
    }

    public void addStartEvent(String id) { startEvents.add(id); }
    public void addFlowObject(String key, FlowObject flowObject) { flowObjects.put(key, flowObject); }
    public void addConnection(String key, Connection connection) { connections.put(key, connection); }
    public void addPath(BPMNPath path) { paths.add(path); }
    public void addCycle(BPMNCycle cycle) { cycles.add(cycle); }


}

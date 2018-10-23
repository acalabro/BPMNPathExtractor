package it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicConnections;

import java.util.ArrayList;

public class GraphicConnection {

    protected final String id;
    protected final String name;
    private ConnectionType connectionType;
    private String sourceRef;
    private String targetRef;
    private ArrayList<Coordinate> waypoints;

    public GraphicConnection(String id, String name, String sourceRef, String targetRef, ConnectionType connectionType) {
        this.id = id;
        this.name = name;
        this.sourceRef = sourceRef;
        this.targetRef = targetRef;
        this.connectionType = connectionType;
        this.waypoints = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ID: ").append(id).append(System.lineSeparator())
                .append("Name: ").append(name).append(System.lineSeparator())
                .append("Type: ").append(connectionType).append(System.lineSeparator())
                .append("Source Ref: ").append(sourceRef).append(System.lineSeparator())
                .append("Target Ref: ").append(targetRef).append(System.lineSeparator());

        for (Coordinate waypoint : waypoints)
            stringBuilder.append("Waypoint: ").append("x: ").append(waypoint.getX())
                    .append(", y: ").append(waypoint.getY()).append(System.lineSeparator());

        return stringBuilder.toString();

    }

    public String getId() { return id; }
    public String getName() { return name; }
    public ArrayList<Coordinate> getWaypoints() { return waypoints; }
    public ConnectionType getConnectionType() { return connectionType; }
    public String getSourceRef() { return sourceRef; }
    public String getTargetRef() { return targetRef; }

    public void addWaypoint(int x, int y) { waypoints.add(new Coordinate(x, y)); }

}

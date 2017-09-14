package it.cnr.isti.labsedc.Objects.Connections;

import it.cnr.isti.labsedc.Objects.BPMNObject;

public class Connection extends BPMNObject {

    private ConnectionType connectionType;
    private String sourceRef;
    private String targetRef;

    public Connection(String id, String name, String sourceRef, String targetRef, ConnectionType connectionType) {
        super(id, name);
        this.sourceRef = sourceRef;
        this.targetRef = targetRef;
        this.connectionType = connectionType;
    }

    @Override
    public String toString() {
        return "Id: " + id + System.lineSeparator() +
                "Name: " + name + System.lineSeparator() +
                "Type: " + connectionType + System.lineSeparator() +
                "Source Ref: " + sourceRef + System.lineSeparator() +
                "Target Ref: " + targetRef + System.lineSeparator();
    }

    public ConnectionType getConnectionType() { return connectionType; }
    public String getSourceRef() { return sourceRef; }
    public String getTargetRef() { return targetRef; }
}

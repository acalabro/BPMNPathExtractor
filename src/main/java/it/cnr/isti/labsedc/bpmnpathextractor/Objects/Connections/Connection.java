package it.cnr.isti.labsedc.bpmnpathextractor.Objects.Connections;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNObject;

import java.io.Serializable;

public class Connection implements BPMNObject, Serializable {

	private static final long serialVersionUID = -7281283768153909867L;
	protected final String id;
    protected final String name;
    private ConnectionType connectionType;
    private String sourceRef;
    private String targetRef;

    public Connection(String id, String name, String sourceRef, String targetRef, ConnectionType connectionType) {
        this.id = id;
        this.name = name;
        this.sourceRef = sourceRef;
        this.targetRef = targetRef;
        this.connectionType = connectionType;
    }

    @Override
    public String toString() {
        return "ID: " + id + System.lineSeparator() +
                "Name: " + name + System.lineSeparator() +
                "Type: " + connectionType + System.lineSeparator() +
                "Source Ref: " + sourceRef + System.lineSeparator() +
                "Target Ref: " + targetRef + System.lineSeparator();
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public ConnectionType getConnectionType() { return connectionType; }
    public String getSourceRef() { return sourceRef; }
    public String getTargetRef() { return targetRef; }
}

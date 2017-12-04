package it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.Gateways;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.FlowObject;

public class Gateway extends FlowObject {

	private static final long serialVersionUID = -6461878604582675966L;
	private final GatewayType gatewayType;

    public Gateway(String id, String name, GatewayType gatewayType) {
        super(id, name);
        this.gatewayType = gatewayType;
    }

    @Override
    public String toString() {
        return "ID: " + id + System.lineSeparator() +
                "Name: " + name + System.lineSeparator() +
                "Type: " + gatewayType + System.lineSeparator() +
                super.toString();
    }

    public GatewayType getGatewayType() { return gatewayType; }
}

package it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicGateways;

import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicFlowObject;

public class GraphicGateway extends GraphicFlowObject {

    private final GatewayType gatewayType;

    public GraphicGateway(String id, String name, GatewayType gatewayType) {
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

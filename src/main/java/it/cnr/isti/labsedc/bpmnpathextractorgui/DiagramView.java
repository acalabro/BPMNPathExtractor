package it.cnr.isti.labsedc.bpmnpathextractorgui;

import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.BPMNGraphicProcess;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicConnections.Coordinate;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicConnections.GraphicConnection;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicActivities.GraphicActivity;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicEvents.GraphicEndEvent;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicEvents.GraphicIntermediateEvent;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicEvents.GraphicStartEvent;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicFlowObject;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicGateways.GraphicGateway;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.diagram.*;
import org.primefaces.model.diagram.connector.FlowChartConnector;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.ArrowOverlay;
import org.primefaces.model.diagram.overlay.LabelOverlay;

import java.util.HashMap;

public class DiagramView {

    private DefaultDiagramModel model;
    private FileUploadView fileUploadView;

    public void drawDiagram(FileUploadEvent event) {

        fileUploadView.handleFileUpload(event);
        model = new DefaultDiagramModel();
        FlowChartConnector connector = new FlowChartConnector();
        connector.setPaintStyle("{strokeStyle:'#000000',lineWidth:2}");
        model.setDefaultConnector(connector);
        model.setMaxConnections(-1);

        for (BPMNGraphicProcess process : fileUploadView.getProcesses()) {

            HashMap<String, GraphicFlowObject> flowObjects = process.getFlowObjects();
            for (String key : flowObjects.keySet()) {
                GraphicFlowObject flowObject = flowObjects.get(key);
                Element element = new Element(flowObject.getName(), flowObject.getPosX() + "pt", flowObject.getPosY() + 50 + "pt");
                if (flowObject instanceof GraphicActivity)
                    element.setStyleClass("activity");
                else if (flowObject instanceof GraphicGateway) {
                    element.setData(null);
                    element.setStyleClass("gateway");
                }
                else if (flowObject instanceof GraphicStartEvent)
                    element.setStyleClass("start-event");
                else if (flowObject instanceof GraphicIntermediateEvent)
                    element.setStyleClass("intermediate-event");
                else if (flowObject instanceof GraphicEndEvent)
                    element.setStyleClass("end-event");
                element.setDraggable(false);
                element.setId(flowObject.getId());
                element.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
                element.addEndPoint(new BlankEndPoint(EndPointAnchor.RIGHT));
                element.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM));
                element.addEndPoint(new BlankEndPoint(EndPointAnchor.LEFT));
                model.addElement(element);
            }

            HashMap<String, GraphicConnection> connections = process.getConnections();
            for (String key : connections.keySet()) {
                GraphicConnection connection = connections.get(key);
                String sourceRef = connection.getSourceRef();
                String targetRef = connection.getTargetRef();
                Element sourceElement = model.findElement(sourceRef);
                Element targetElement = model.findElement(targetRef);
                GraphicFlowObject sourceObject = flowObjects.get(sourceRef);
                GraphicFlowObject targetObject = flowObjects.get(targetRef);

                int sourceElementAnchor = findAnchorPosition(sourceObject, connection.getWaypoints().get(0));
                int targetElementAnchor = findAnchorPosition(targetObject, connection.getWaypoints().get(connection.getWaypoints().size() - 1));

                model.connect(createConnection(sourceElement.getEndPoints().get(sourceElementAnchor), targetElement.getEndPoints().get(targetElementAnchor)));
            }

        }

    }

    private Connection createConnection(EndPoint from, EndPoint to) {
        Connection connection = new Connection(from, to);
        connection.getOverlays().add(new ArrowOverlay(10, 10, 1, 1));

        // if(label != null) connection.getOverlays().add(new LabelOverlay(label));

        return connection;
    }

    private int findAnchorPosition(GraphicFlowObject flowObject, Coordinate coordinate) {

        int flowObjectPosX = flowObject.getPosX();
        int flowObjectPosY = flowObject.getPosY();
        int flowObjectWidth = flowObject.getWidth();
        int flowObjectHeight = flowObject.getHeight();
        int connectionPosX = coordinate.getX();
        int connectionPosY = coordinate.getY();

        if (connectionPosY == flowObjectPosY) return 0;
        else if (connectionPosX == flowObjectPosX + flowObjectWidth) return 1;
        else if (connectionPosY == flowObjectPosY + flowObjectHeight) return 2;
        else if (connectionPosX == flowObjectPosX) return 3;

        return 0;

    }

    public DiagramModel getModel() { return model; }

    public FileUploadView getFileUploadView() { return fileUploadView; }
    public void setFileUploadView(FileUploadView fileUploadView) { this.fileUploadView = fileUploadView; }
}
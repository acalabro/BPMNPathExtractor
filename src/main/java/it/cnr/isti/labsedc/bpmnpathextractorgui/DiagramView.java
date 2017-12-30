package it.cnr.isti.labsedc.bpmnpathextractorgui;

import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.BPMNGraphicProcess;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicConnections.Coordinate;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicConnections.GraphicConnection;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicActivities.GraphicActivity;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicActivities.GraphicSubProcess;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicEvents.GraphicEndEvent;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicEvents.GraphicIntermediateEvent;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicEvents.GraphicStartEvent;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicFlowObject;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicGateways.GraphicGateway;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.diagram.*;
import org.primefaces.model.diagram.connector.FlowChartConnector;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.ArrowOverlay;

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
                Element element = null;
                if (flowObject instanceof GraphicActivity) {
                    if (flowObject instanceof GraphicSubProcess) {
                        element = new Element(flowObject.getName(), flowObject.getPosX() - 5 +  "pt", flowObject.getPosY() + 50 + "pt");
                        RequestContext.getCurrentInstance().execute("createClass(" +
                                "\"." + flowObject.getId() + "\", \"" +
                                "border: 1.5pt solid black; " +
                                "width: " + flowObject.getWidth() + "pt; " +
                                "height: " + flowObject.getHeight() + "pt; " +
                                "text-align: center; " +
                                "border-radius: 10pt; " +
                                "\")");
                        element.setStyleClass(flowObject.getId());
                    }
                    else {
                        element = new Element(flowObject.getName(), flowObject.getPosX() + 10 + "pt", flowObject.getPosY() + 65 + "pt");
                        element.setStyleClass("activity");
                    }
                }
                else if (flowObject instanceof GraphicGateway) {
                    element = new Element(null, flowObject.getPosX() + "pt", flowObject.getPosY() + 50 + "pt");
                    element.setStyleClass("gateway");
                }
                else if (flowObject instanceof GraphicStartEvent) {
                    element = new Element(flowObject.getName(), flowObject.getPosX() - 7 + "pt", flowObject.getPosY() + 43 + "pt");
                    element.setStyleClass("start-event");
                }
                else if (flowObject instanceof GraphicIntermediateEvent) {
                    element = new Element(flowObject.getName(), flowObject.getPosX() - 7 + "pt", flowObject.getPosY() + 43 + "pt");
                    element.setStyleClass("intermediate-event");
                }
                else if (flowObject instanceof GraphicEndEvent) {
                    element = new Element(flowObject.getName(), flowObject.getPosX() - 7 + "pt", flowObject.getPosY() + 43 + "pt");
                    element.setStyleClass("end-event");
                }

                if (element != null) {
                    element.setDraggable(false);
                    element.setId(flowObject.getId());
                    element.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
                    element.addEndPoint(new BlankEndPoint(EndPointAnchor.RIGHT));
                    element.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM));
                    element.addEndPoint(new BlankEndPoint(EndPointAnchor.LEFT));
                    model.addElement(element);
                }

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

        if (Math.abs(connectionPosY - flowObjectPosY) < 3) return 0;
        else if (Math.abs(connectionPosX - flowObjectPosX - flowObjectWidth) < 3) return 1;
        else if (Math.abs(connectionPosY - flowObjectPosY - flowObjectHeight) < 3) return 2;
        else if (Math.abs(connectionPosX - flowObjectPosX) < 3) return 3;

        System.out.println("PosX: " + flowObjectPosX + "PosY: " + flowObjectPosY);
        System.out.println("Width: " + flowObjectWidth + "Height: " + flowObjectHeight);
        System.out.println("ConnX: " + connectionPosX + "ConnY: " + connectionPosY);

        return 0;

    }

    public DiagramModel getModel() { return model; }
    public FileUploadView getFileUploadView() { return fileUploadView; }

    public void setFileUploadView(FileUploadView fileUploadView) { this.fileUploadView = fileUploadView; }
}
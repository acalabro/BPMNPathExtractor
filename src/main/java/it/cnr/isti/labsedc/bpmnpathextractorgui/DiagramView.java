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
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.LaneCoordinate;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.diagram.*;
import org.primefaces.model.diagram.connector.Connector;
import org.primefaces.model.diagram.connector.FlowChartConnector;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.ArrowOverlay;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import java.util.ArrayList;
import java.util.HashMap;

public class DiagramView {

    private DefaultDiagramModel model;
    private FileUploadView fileUploadView;
    private CheckBoxView checkBoxView;
    private boolean showActionButtons = true;
    private ArrayList<Element> diagramElements;

    public void createModel(FileUploadEvent event) {

        showActionButtons = false;
        diagramElements = new ArrayList<>();
        fileUploadView.handleFileUpload(event);
        checkBoxView.clearInformation();
        checkBoxView.setName(event.getFile().getFileName());
        model = new DefaultDiagramModel();
        model.setMaxConnections(-1);
        int maxDeepness = 0;
        for (BPMNGraphicProcess process : fileUploadView.getProcesses()) {
            if (process.getDeepness() > maxDeepness) maxDeepness = process.getDeepness();
            if (process.getPoolID() != null) checkBoxView.addPool(process.getPoolID());
            Element processElement = new Element(process.getPoolName(), process.getPosX() + 10 + "pt", process.getPosY() + 50 + "pt");
            RequestContext.getCurrentInstance().execute(createScriptArgument(process.getPoolID(), process.getWidth(), process.getHeight(), StyleType.POOL));
            processElement.setStyleClass(process.getPoolID());
            processElement.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
            processElement.setDraggable(false);
            diagramElements.add(processElement);
            for (LaneCoordinate laneCoordinate : process.getLanesCoordinates()) {
                if (process.getPoolID() != null) checkBoxView.addLane(process.getPoolID(), laneCoordinate.getLaneID());
                Element laneElement = new Element(null, laneCoordinate.getPosX() + 10 + "pt", laneCoordinate.getPosY() + 50 + "pt");
                RequestContext.getCurrentInstance().execute(createScriptArgument(laneCoordinate.getLaneID(), laneCoordinate.getWidth(), laneCoordinate.getHeight(), StyleType.POOL));
                laneElement.setStyleClass(laneCoordinate.getLaneID());
                laneElement.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
                laneElement.setDraggable(false);
                diagramElements.add(laneElement);
            }
            HashMap<String, GraphicFlowObject> flowObjects = process.getFlowObjects();
            for (String key : flowObjects.keySet()) {
                GraphicFlowObject flowObject = flowObjects.get(key);
                Element element = null;
                if (flowObject instanceof GraphicActivity) {
                    if (flowObject instanceof GraphicSubProcess) {
                        element = new Element(flowObject.getName(), flowObject.getPosX() - 5 + "pt", flowObject.getPosY() + 50 + "pt");
                        RequestContext.getCurrentInstance().execute(createScriptArgument(flowObject.getId(), flowObject.getWidth(), flowObject.getHeight(), StyleType.SUB_PROCESS));
                        element.setStyleClass(flowObject.getId());
                    } else {
                        element = new Element(flowObject.getName(), flowObject.getPosX() + 10 + "pt", flowObject.getPosY() + 65 + "pt");
                        element.setStyleClass("activity");
                    }
                } else if (flowObject instanceof GraphicGateway) {
                    element = new Element(null, flowObject.getPosX() + "pt", flowObject.getPosY() + 50 + "pt");
                    element.setStyleClass("gateway");
                } else if (flowObject instanceof GraphicStartEvent) {
                    element = new Element(flowObject.getName(), flowObject.getPosX() - 7 + "pt", flowObject.getPosY() + 43 + "pt");
                    element.setStyleClass("start-event");
                } else if (flowObject instanceof GraphicIntermediateEvent) {
                    element = new Element(flowObject.getName(), flowObject.getPosX() - 7 + "pt", flowObject.getPosY() + 43 + "pt");
                    element.setStyleClass("intermediate-event");
                } else if (flowObject instanceof GraphicEndEvent) {
                    element = new Element(flowObject.getName(), flowObject.getPosX() - 7 + "pt", flowObject.getPosY() + 43 + "pt");
                    element.setStyleClass("end-event");
                } if (element != null) {
                    element.setDraggable(false);
                    element.setId(flowObject.getId());
                    element.addEndPoint(new BlankEndPoint(EndPointAnchor.TOP));
                    element.addEndPoint(new BlankEndPoint(EndPointAnchor.RIGHT));
                    element.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM));
                    element.addEndPoint(new BlankEndPoint(EndPointAnchor.LEFT));
                    diagramElements.add(element);
                }

            }

        }

        checkBoxView.setMaxDeepness(maxDeepness);
        checkBoxView.fillLanesList();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Upload of " + event.getFile().getFileName() + " complete."));

    }

    public void drawDiagram(ArrayList<String> pathElements) {

        for (Element element : diagramElements)
            model.addElement(element);

        FlowChartConnector connectionConnector = new FlowChartConnector();
        FlowChartConnector selectedConnectionConnector = new FlowChartConnector();
        connectionConnector.setPaintStyle("{strokeStyle:'#000000',lineWidth:2}");
        selectedConnectionConnector.setPaintStyle("{strokeStyle:'#FF0000',lineWidth:2}");

        for (BPMNGraphicProcess process : fileUploadView.getProcesses()) {

            HashMap<String, GraphicFlowObject> flowObjects = process.getFlowObjects();
            HashMap<String, GraphicConnection> connections = process.getConnections();
            for (String key : connections.keySet()) {
                GraphicConnection flow = connections.get(key);
                String sourceRef = flow.getSourceRef();
                String targetRef = flow.getTargetRef();
                Element sourceElement = model.findElement(sourceRef);
                Element targetElement = model.findElement(targetRef);
                GraphicFlowObject sourceObject = flowObjects.get(sourceRef);
                GraphicFlowObject targetObject = flowObjects.get(targetRef);

                int sourceElementAnchor = findAnchorPosition(sourceObject, flow.getWaypoints().get(0));
                int targetElementAnchor = findAnchorPosition(targetObject, flow.getWaypoints().get(flow.getWaypoints().size() - 1));

                Connection connection = createConnection(sourceElement.getEndPoints().get(sourceElementAnchor), targetElement.getEndPoints().get(targetElementAnchor));
                if (pathElements != null && pathElements.contains(sourceRef) && pathElements.contains(targetRef))
                    connection.setConnector(selectedConnectionConnector);
                else
                    connection.setConnector(connectionConnector);
                model.connect(connection);

            }

            HashMap<String, GraphicConnection> messageFlows = process.getMessageFlows();
            Connector messageFlowConnector = new FlowChartConnector();
            messageFlowConnector.setPaintStyle("{strokeStyle:'#000000',lineWidth:2,dashstyle: \"2\"}");
            for (String key : messageFlows.keySet()) {
                GraphicConnection messageFlow = messageFlows.get(key);
                String sourceRef = messageFlow.getSourceRef();
                String targetRef = messageFlow.getTargetRef();
                Element sourceElement = model.findElement(sourceRef);
                Element targetElement = model.findElement(targetRef);
                GraphicFlowObject sourceObject = flowObjects.get(sourceRef);
                GraphicFlowObject targetObject = null;

                for (BPMNGraphicProcess bpmnGraphicProcess : fileUploadView.getProcesses())
                    if ((targetObject = bpmnGraphicProcess.getFlowObject(targetRef)) != null) break;

                if (targetObject == null) continue;

                int sourceElementAnchor = findAnchorPosition(sourceObject, messageFlow.getWaypoints().get(0));
                int targetElementAnchor = findAnchorPosition(targetObject, messageFlow.getWaypoints().get(messageFlow.getWaypoints().size() - 1));

                Connection connection = createConnection(sourceElement.getEndPoints().get(sourceElementAnchor), targetElement.getEndPoints().get(targetElementAnchor));
                connection.setConnector(messageFlowConnector);
                model.connect(connection);

            }

        }

    }

    private Connection createConnection(EndPoint from, EndPoint to) {
        Connection connection = new Connection(from, to);
        connection.getOverlays().add(new ArrowOverlay(10, 10, 1, 1));

        return connection;
    }

    private int findAnchorPosition(GraphicFlowObject flowObject, Coordinate coordinate) {

        int anchorPosition = 0;

        int flowObjectPosX = flowObject.getPosX();
        int flowObjectPosY = flowObject.getPosY();
        int flowObjectWidth = flowObject.getWidth();
        int flowObjectHeight = flowObject.getHeight();
        int connectionPosX = coordinate.getX();
        int connectionPosY = coordinate.getY();

        if (Math.abs(connectionPosY - flowObjectPosY) < 3) anchorPosition = 0;
        else if (Math.abs(connectionPosX - flowObjectPosX - flowObjectWidth) < 3) anchorPosition = 1;
        else if (Math.abs(connectionPosY - flowObjectPosY - flowObjectHeight) < 3) anchorPosition = 2;
        else if (Math.abs(connectionPosX - flowObjectPosX) < 3) anchorPosition = 3;

        return anchorPosition;

    }

    private String createScriptArgument(String id, int width, int height, StyleType type) {
        if (type == StyleType.POOL)
            return "createClass(" +
                    "\"." + id + "\", \"" +
                    "border: 1.5pt solid black; " +
                    "width: " + width + "pt; " +
                    "height: " + height + "pt; " +
                    "text-align: center; " +
                    "\")";
        else if (type == StyleType.SUB_PROCESS)
            return "createClass(" +
                    "\"." + id + "\", \"" +
                    "border: 1.5pt solid black; " +
                    "width: " + width + "pt; " +
                    "height: " + height + "pt; " +
                    "text-align: center; " +
                    "border-radius: 10pt; " +
                    "\")";
        else if (type == StyleType.SELECTED_SUB_PROCESS)
            return "createClass(" +
                    "\"." + id + "\", \"" +
                    "border: 1.5pt solid red; " +
                    "width: " + width + "pt; " +
                    "height: " + height + "pt; " +
                    "text-align: center; " +
                    "border-radius: 10pt; " +
                    "\")";
        return null;
    }

    public void markSelectedPath() {

        ArrayList<String> pathElements = checkBoxView.getPath(checkBoxView.getSelectedPath());

        ArrayList<BPMNGraphicProcess> processes = fileUploadView.getProcesses();

        for (BPMNGraphicProcess process : processes) {
            HashMap<String, GraphicFlowObject> flowObjects = process.getFlowObjects();
            for (String key : flowObjects.keySet()) {
                GraphicFlowObject flowObject = flowObjects.get(key);
                Element element = model.findElement(flowObject.getId());
                if (pathElements.contains(flowObject.getId())) {
                    if (flowObject instanceof GraphicActivity)
                        if (flowObject instanceof GraphicSubProcess) {
                            RequestContext.getCurrentInstance().execute(createScriptArgument(flowObject.getId(), flowObject.getWidth(), flowObject.getHeight(), StyleType.SELECTED_SUB_PROCESS));
                            element.setStyleClass(flowObject.getId());
                        } else element.setStyleClass("selected-activity");
                    else if (flowObject instanceof GraphicGateway)
                        element.setStyleClass("selected-gateway");
                    else if (flowObject instanceof GraphicStartEvent)
                        element.setStyleClass("selected-start-event");
                    else if (flowObject instanceof GraphicIntermediateEvent)
                        element.setStyleClass("selected-intermediate-event");
                    else if (flowObject instanceof GraphicEndEvent)
                        element.setStyleClass("selected-end-event");
                } else {
                    if (flowObject instanceof GraphicActivity)
                        if (flowObject instanceof GraphicSubProcess) {
                            RequestContext.getCurrentInstance().execute(createScriptArgument(flowObject.getId(), flowObject.getWidth(), flowObject.getHeight(), StyleType.SUB_PROCESS));
                            element.setStyleClass(flowObject.getId());
                        } else element.setStyleClass("activity");
                    else if (flowObject instanceof GraphicGateway)
                        element.setStyleClass("gateway");
                    else if (flowObject instanceof GraphicStartEvent)
                        element.setStyleClass("start-event");
                    else if (flowObject instanceof GraphicIntermediateEvent)
                        element.setStyleClass("intermediate-event");
                    else if (flowObject instanceof GraphicEndEvent)
                        element.setStyleClass("end-event");
                }
            }
        }

        drawDiagram(pathElements);

    }

    public DiagramModel getModel() { return model; }

    public boolean getShowActionButtons() { return showActionButtons; }

    public void setShowActionButtons(boolean showActionButtons) { this.showActionButtons = showActionButtons; }

    public void setFileUploadView(FileUploadView fileUploadView) { this.fileUploadView = fileUploadView; }
    public void setCheckBoxView(CheckBoxView checkBoxView) { this.checkBoxView = checkBoxView; }

}
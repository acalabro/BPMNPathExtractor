package it.cnr.isti.labsedc.bpmnpathextractorgui;

import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.BPMNGraphicProcess;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicConnections.*;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicActivities.*;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicEvents.*;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicFlowObject;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicGateways.*;
import org.primefaces.model.UploadedFile;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class BPMNGraphicParser {

    private static BPMNProperties properties = new BPMNProperties();

    public static Document createDocument(UploadedFile bpmnFile) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(bpmnFile.getInputstream());
            document.getDocumentElement().normalize();
            return document;
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<BPMNGraphicProcess> parseProcessList(Document bpmnDocument) {

        ArrayList<BPMNGraphicProcess> processes = new ArrayList<>();
        NodeList processesNodes = bpmnDocument.getElementsByTagNameNS(properties.getProperty("bpmnDefaultNamespace"), "process");

        for (int i = 0; i < processesNodes.getLength(); i++) {
            processes.add(parseProcess(processesNodes.item(i), processes, null, null,0));
        }

        addInformationAboutLanesInSubProcesses(processes);
        addInformationAboutPools(bpmnDocument, processes);
        addGraphicalInformations(bpmnDocument, processes);

        return processes;

    }

    private static BPMNGraphicProcess parseProcess(Node processNode, ArrayList<BPMNGraphicProcess> processes, String parentProcessID, GraphicFlowObject parentObject, int deepness) {

        BPMNGraphicProcess process;
        Node laneSet = null;
        String processID = getAttributeValue(processNode, "id");
        String processName = getAttributeValue(processNode, "name");
        String processExecutable = getAttributeValue(processNode, "isExecutable");

        process = new BPMNGraphicProcess(processID, processName, parentProcessID, parentObject, Boolean.parseBoolean(processExecutable), deepness);

        NodeList processChildNodes = processNode.getChildNodes();
        for (int j = 0; j < processChildNodes.getLength(); j++) {
            Node childNode = processChildNodes.item(j);
            String nodeID = getAttributeValue(childNode, "id");
            String localName = childNode.getLocalName();
            if (localName == null) continue;
            switch (localName) {
                case "laneSet":
                    laneSet = childNode;
                    break;
                case "startEvent":
                    process.addFlowObject(nodeID, parseEvent(childNode, EventType.START_EVENT));
                    process.addStartEvent(nodeID);
                    break;
                case "intermediateThrowEvent":
                    process.addFlowObject(nodeID, parseEvent(childNode, EventType.INTERMEDIATE_THROW_EVENT));
                    break;
                case "intermediateCatchEvent":
                    process.addFlowObject(nodeID, parseEvent(childNode, EventType.INTERMEDIATE_CATCH_EVENT));
                    break;
                case "endEvent":
                    process.addFlowObject(nodeID, parseEvent(childNode, EventType.END_EVENT));
                    break;
                case "task":
                    process.addFlowObject(nodeID, parseActivity(childNode, ActivityType.TASK));
                    break;
                case "sendTask":
                    process.addFlowObject(nodeID, parseActivity(childNode, ActivityType.SEND_TASK));
                    break;
                case "receiveTask":
                    process.addFlowObject(nodeID, parseActivity(childNode, ActivityType.RECEIVE_TASK));
                    break;
                case "userTask":
                    process.addFlowObject(nodeID, parseActivity(childNode, ActivityType.USER_TASK));
                    break;
                case "manualTask":
                    process.addFlowObject(nodeID, parseActivity(childNode, ActivityType.MANUAL_TASK));
                    break;
                case "businessRuleTask":
                    process.addFlowObject(nodeID, parseActivity(childNode, ActivityType.BUSINESS_RULE_TASK));
                    break;
                case "serviceTask":
                    process.addFlowObject(nodeID, parseActivity(childNode, ActivityType.SERVICE_TASK));
                    break;
                case "scriptTask":
                    process.addFlowObject(nodeID, parseActivity(childNode, ActivityType.SCRIPT_TASK));
                    break;
                case "callActivity":
                    process.addFlowObject(nodeID, parseActivity(childNode, ActivityType.CALL_ACTIVITY));
                    break;
                case "subProcess":
                    GraphicFlowObject subProcess = parseActivity(childNode, ActivityType.SUB_PROCESS);
                    processes.add(parseProcess(childNode, processes, processID, subProcess, deepness + 1));
                    process.addFlowObject(nodeID, subProcess);
                    break;
                case "exclusiveGateway":
                    process.addFlowObject(nodeID, parseGateway(childNode, GatewayType.EXCLUSIVE_GATEWAY));
                    break;
                case "inclusiveGateway":
                    process.addFlowObject(nodeID, parseGateway(childNode, GatewayType.INCLUSIVE_GATEWAY));
                    break;
                case "complexGateway":
                    process.addFlowObject(nodeID, parseGateway(childNode, GatewayType.COMPLEX_GATEWAY));
                    break;
                case "eventBasedGateway":
                    process.addFlowObject(nodeID, parseGateway(childNode, GatewayType.EVENT_BASED_GATEWAY));
                    break;
                case "sequenceFlow":
                    process.addConnection(nodeID, parseConnection(childNode, ConnectionType.SEQUENCE_FLOW));
                    break;
                case "messageFlow":
                    process.addConnection(nodeID, parseConnection(childNode, ConnectionType.MESSAGE_FLOW));
                    break;
                case "association":
                    process.addConnection(nodeID, parseConnection(childNode, ConnectionType.ASSOCIATION));
                    break;
            }
        }

        addInformationAboutLanes(process, laneSet);

        return process;
    }

    private static GraphicEvent parseEvent(Node eventNode, EventType eventType) {

        GraphicEvent event;
        String eventID = getAttributeValue(eventNode, "id");
        String eventName = getAttributeValue(eventNode, "name");

        switch (eventType) {
            case START_EVENT:
                event = new GraphicStartEvent(eventID, eventName, eventType);
                break;
            case END_EVENT:
                event = new GraphicEndEvent(eventID, eventName, eventType);
                break;
            default:
                event = new GraphicIntermediateEvent(eventID, eventName, eventType);
                break;
        }

        addConnectionsToObject(event, eventNode);

        return event;

    }

    private static GraphicActivity parseActivity(Node activityNode, ActivityType activityType) {

        GraphicActivity activity;
        String activityID = getAttributeValue(activityNode, "id");
        String activityName = getAttributeValue(activityNode, "name");

        switch (activityType) {
            case SUB_PROCESS:
                activity = new GraphicSubProcess(activityID, activityName, activityType);
                break;
            default:
                activity = new GraphicTask(activityID, activityName, activityType);
                break;
        }

        addConnectionsToObject(activity, activityNode);

        return activity;

    }

    private static GraphicGateway parseGateway(Node gatewayNode, GatewayType gatewayType) {

        GraphicGateway gateway;
        String gatewayID = getAttributeValue(gatewayNode, "id");
        String gatewayName = getAttributeValue(gatewayNode, "name");

        gateway = new GraphicGateway(gatewayID, gatewayName, gatewayType);

        addConnectionsToObject(gateway, gatewayNode);

        return gateway;

    }

    private static GraphicConnection parseConnection(Node connectionNode, ConnectionType connectionType) {

        GraphicConnection connection;
        String sourceRef = getAttributeValue(connectionNode, "sourceRef");
        String targetRef = getAttributeValue(connectionNode, "targetRef");
        String connectionID = getAttributeValue(connectionNode, "id");
        String connectionName = getAttributeValue(connectionNode, "name");

        connection = new GraphicConnection(connectionID, connectionName, sourceRef, targetRef, connectionType);

        return connection;

    }

    private static void addConnectionsToObject(GraphicFlowObject object, Node objectNode) {

        NodeList childNodes = objectNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node eventChildNode = childNodes.item(i);
            String localName = eventChildNode.getLocalName();
            if (localName != null) {
                switch (localName) {
                    case "outgoing":
                        object.addOutgoingConnection(eventChildNode.getTextContent());
                        break;
                    case "incoming":
                        object.addIncomingConnection(eventChildNode.getTextContent());
                        break;
                }
            }
        }

    }

    private static void addInformationAboutLanes(BPMNGraphicProcess process, Node laneSet) {

        if (laneSet == null) return;
        NodeList childNodes = laneSet.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            String localName = childNode.getLocalName();
            if (localName == null) continue;
            if (childNode.getLocalName().equals("lane")) {
                String laneID = getAttributeValue(childNode, "id");
                if (!process.isPresentLane(laneID)) process.addInnerLane(laneID);
                NodeList flowNodes = childNode.getChildNodes();

                for (int j = 0; j < flowNodes.getLength(); j++) {
                    Node flowNode = flowNodes.item(j);
                    String nodeLocalName = flowNode.getLocalName();
                    if (nodeLocalName == null) continue;
                    if (nodeLocalName.equals("flowNodeRef")) {
                        GraphicFlowObject flowObject = process.getFlowObject(flowNode.getTextContent());
                        flowObject.addParentLane(laneID);
                    } else if (nodeLocalName.equals("childLaneSet")) {
                        addInformationAboutLanes(process, flowNode);
                    }
                }
            }
        }
    }

    private static void addInformationAboutLanesInSubProcesses(ArrayList<BPMNGraphicProcess> processes) {

        int deepness = 1;
        boolean isPresentDeepness = true;

        while (isPresentDeepness) {
            isPresentDeepness = false;
            for (BPMNGraphicProcess subProcess : processes) {
                if (subProcess.getDeepness() == deepness) {
                    isPresentDeepness = true;
                    HashMap<String, GraphicFlowObject> flowObjects = subProcess.getFlowObjects();
                    GraphicFlowObject parentObject = subProcess.getParentObject();
                    subProcess.setInnerLanes(parentObject.getParentLanes());
                    for (String flowObjectID : flowObjects.keySet()) {
                        flowObjects.get(flowObjectID).setParentLanes(parentObject.getParentLanes());
                    }
                }
            }
            deepness++;
        }

    }

    private static void addInformationAboutPools(Document document, ArrayList<BPMNGraphicProcess> processes) {

        BPMNProperties properties = new BPMNProperties();

        NodeList collaborationNodes = document.getElementsByTagNameNS(properties.getProperty("bpmnDefaultNamespace"), "collaboration");
        for (int i = 0; i < collaborationNodes.getLength(); i++) {
            Node collaborationNode = collaborationNodes.item(i);
            NodeList participants = collaborationNode.getChildNodes();

            for (int j = 0; j < participants.getLength(); j++) {
                Node participant = participants.item(j);
                String localName = participant.getLocalName();
                if (localName == null) continue;
                if (localName.equals("participant")) {
                    String poolID = getAttributeValue(participant, "id");
                    String poolName = getAttributeValue(participant, "name");
                    String processRef = getAttributeValue(participant, "processRef");

                    for (BPMNGraphicProcess process : processes) {
                        if (process.getId().equals(processRef)) {
                            process.setPoolID(poolID);
                            process.setPoolName(poolName);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void addGraphicalInformations(Document document, ArrayList<BPMNGraphicProcess> processes) {

        BPMNProperties properties = new BPMNProperties();

        Node diagramNode = document.getElementsByTagNameNS(properties.getProperty("bpmnDiagramNamespace"), "BPMNDiagram").item(0);
        NodeList diagramChildNodes = diagramNode.getChildNodes();

        for (int i = 0; i < diagramChildNodes.getLength(); i++) {
            Node planeNode = diagramChildNodes.item(i);
            if (Objects.equals(planeNode.getLocalName(), "BPMNPlane")) {
                NodeList planeChildNodes = planeNode.getChildNodes();
                for (int j = 0; j < planeChildNodes.getLength(); j++) {
                    Node childNode = planeChildNodes.item(j);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        switch (childNode.getLocalName()) {
                            case "BPMNShape":
                                String flowObjectID = getAttributeValue(childNode, "bpmnElement");
                                GraphicFlowObject flowObject = findFlowObject(processes, flowObjectID);
                                NodeList boundsList = childNode.getChildNodes();
                                if (flowObject != null) {
                                    for (int t = 0; t < boundsList.getLength(); t++) {
                                        Node boundsNode = boundsList.item(t);
                                        if (Objects.equals(boundsNode.getLocalName(), "Bounds")) {
                                            String x = getAttributeValue(boundsNode, "x");
                                            String y = getAttributeValue(boundsNode, "y");
                                            String width = getAttributeValue(boundsNode, "width");
                                            String height = getAttributeValue(boundsNode, "height");
                                            if (x != null) flowObject.setPosX(Math.round(Float.parseFloat(x)));
                                            if (y != null) flowObject.setPosY(Math.round(Float.parseFloat(y)));
                                            if (width != null) flowObject.setWidth(Math.round(Float.parseFloat(width)));
                                            if (height != null) flowObject.setHeight(Math.round(Float.parseFloat(height)));
                                        }
                                    }
                                }
                                break;
                            case "BPMNEdge":
                                String connectionID = getAttributeValue(childNode, "bpmnElement");
                                GraphicConnection connection = findConnection(processes, connectionID);
                                NodeList waypointsList = childNode.getChildNodes();
                                if (connection != null) {
                                    for (int t = 0; t < waypointsList.getLength(); t++) {
                                        Node waypointNode = waypointsList.item(t);
                                        if (Objects.equals(waypointNode.getLocalName(), "waypoint")) {
                                            String x = getAttributeValue(waypointNode, "x");
                                            String y = getAttributeValue(waypointNode, "y");
                                            if (x != null && y != null)
                                                connection.addWaypoint(Integer.parseInt(x), Integer.parseInt(y));
                                        }

                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }

    }

    private static String getAttributeValue(Node node, String attributeName) {
        NamedNodeMap attributes = node.getAttributes();
        Node attributeNode;
        if (attributes != null) {
            attributeNode = attributes.getNamedItem(attributeName);
            if (attributeNode != null)
                return attributeNode.getNodeValue();
        }
        return null;
    }

    private static GraphicFlowObject findFlowObject(ArrayList<BPMNGraphicProcess> processes, String id) {

        for (BPMNGraphicProcess process : processes) {
            GraphicFlowObject flowObject = process.getFlowObject(id);
            if (flowObject != null) return flowObject;
        }

        return null;

    }

    private static GraphicConnection findConnection(ArrayList<BPMNGraphicProcess> processes, String id) {

        for (BPMNGraphicProcess process : processes) {
            GraphicConnection connection = process.getConnection(id);
            if (connection != null) return connection;
        }

        return null;

    }

}

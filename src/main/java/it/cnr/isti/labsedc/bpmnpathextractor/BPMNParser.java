package it.cnr.isti.labsedc.bpmnpathextractor;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.Connections.*;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.Activities.*;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.Events.*;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.FlowObject;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.Gateways.*;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNProcess;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

public class BPMNParser {

    private static BPMNProperties properties = new BPMNProperties();

    @Nullable
    public static Document parseXMLFromPath(String path) {
        File xmlFile = new File(path);
        if (!xmlFile.exists()) return null;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(path));
            document.getDocumentElement().normalize();
            return document;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int parseXMLFromString(String bpmnXMLString, String bpmnName) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(bpmnXMLString)));
            document.getDocumentElement().normalize();
            return saveDocumentToFile(document, bpmnName);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static int saveDocumentToFile(Document document, String bpmnName) {

        String folderPath = properties.getProperty("dbFolderPath") + "/" + bpmnName;
        File dbFolder = new File(folderPath);

        boolean success;

        if (dbFolder.exists() && dbFolder.isDirectory()) {
            success = dbFolder.delete();
            if (!success) return 1;
        }

        success = dbFolder.mkdir();
        if (!success) return 1;

        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(new File(folderPath + "/" + bpmnName +  ".xml")));
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return 0;

    }

    public static ArrayList<BPMNProcess> parseProcessesList(Document document) {

        BPMNProperties properties = new BPMNProperties();

        ArrayList<BPMNProcess> processes = new ArrayList<>();
        NodeList processesNodes = document.getElementsByTagNameNS(properties.getProperty("defaultNamespace"), "process");

        for (int i = 0; i < processesNodes.getLength(); i++) {
            processes.add(parseProcess(processesNodes.item(i), processes, null, null,0));
        }

        addInformationAboutLanesInSubProcesses(processes);
        addInformationAboutPools(document, processes);

        return processes;

    }

    private static BPMNProcess parseProcess(Node processNode, ArrayList<BPMNProcess> processes, String parentProcessID, FlowObject parentObject, int deepness) {

        BPMNProcess process;
        Node laneSet = null;
        String processID = getAttributeValue(processNode, "id");
        String processName = getAttributeValue(processNode, "name");
        String processExecutable = getAttributeValue(processNode, "isExecutable");

        process = new BPMNProcess(processID, processName, parentProcessID, parentObject, Boolean.parseBoolean(processExecutable), deepness);

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
                    FlowObject subProcess = parseActivity(childNode, ActivityType.SUB_PROCESS);
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

    private static Event parseEvent(Node eventNode, EventType eventType) {

        Event event;
        String eventID = getAttributeValue(eventNode, "id");
        String eventName = getAttributeValue(eventNode, "name");

        switch (eventType) {
            case START_EVENT:
                event = new StartEvent(eventID, eventName, eventType);
                break;
            case END_EVENT:
                event = new EndEvent(eventID, eventName, eventType);
                break;
            default:
                event = new IntermediateEvent(eventID, eventName, eventType);
                break;
        }

        addConnectionsToObject(event, eventNode);

        return event;

    }

    private static Activity parseActivity(Node activityNode, ActivityType activityType) {

        Activity activity;
        String activityID = getAttributeValue(activityNode, "id");
        String activityName = getAttributeValue(activityNode, "name");

        switch (activityType) {
            case SUB_PROCESS:
                activity = new SubProcess(activityID, activityName, activityType);
                break;
            default:
                activity = new Task(activityID, activityName, activityType);
                break;
        }

        addConnectionsToObject(activity, activityNode);

        return activity;

    }

    private static Gateway parseGateway(Node gatewayNode, GatewayType gatewayType) {

        Gateway gateway;
        String gatewayID = getAttributeValue(gatewayNode, "id");
        String gatewayName = getAttributeValue(gatewayNode, "name");

        gateway = new Gateway(gatewayID, gatewayName, gatewayType);

        addConnectionsToObject(gateway, gatewayNode);

        return gateway;

    }

    private static Connection parseConnection(Node connectionNode, ConnectionType connectionType) {

        Connection connection;
        String sourceRef = getAttributeValue(connectionNode, "sourceRef");
        String targetRef = getAttributeValue(connectionNode, "targetRef");
        String connectionID = getAttributeValue(connectionNode, "id");
        String connectionName = getAttributeValue(connectionNode, "name");

        connection = new Connection(connectionID, connectionName, sourceRef, targetRef, connectionType);

        return connection;

    }

    private static void addConnectionsToObject(FlowObject object, Node objectNode) {

        NodeList childNodes = objectNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node eventChildNode = childNodes.item(i);
            String localName = eventChildNode.getLocalName();
            if (localName == null) continue;
            switch (localName) {
                case "incoming":
                    object.addIncomingConnection(eventChildNode.getTextContent());
                    break;
                case "outgoing":
                    object.addOutgoingConnection(eventChildNode.getTextContent());
                    break;
            }
        }

    }

    private static void addInformationAboutLanes(BPMNProcess process, Node laneSet) {

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
                        FlowObject flowObject = process.getFlowObject(flowNode.getTextContent());
                        flowObject.addParentLane(laneID);
                    } else if (nodeLocalName.equals("childLaneSet")) {
                        addInformationAboutLanes(process, flowNode);
                    }
                }
            }
        }
    }

    private static void addInformationAboutLanesInSubProcesses(ArrayList<BPMNProcess> processes) {

        int deepness = 1;
        boolean isPresentDeepness = true;

        while (isPresentDeepness) {
            isPresentDeepness = false;
            for (BPMNProcess subProcess : processes) {
                if (subProcess.getDeepness() == deepness) {
                    isPresentDeepness = true;
                    HashMap<String, FlowObject> flowObjects = subProcess.getFlowObjects();
                    FlowObject parentObject = subProcess.getParentObject();
                    subProcess.setInnerLanes(parentObject.getParentLanes());
                    for (String flowObjectID : flowObjects.keySet()) {
                        flowObjects.get(flowObjectID).setParentLanes(parentObject.getParentLanes());
                    }
                }
            }
            deepness++;
        }

    }

    private static void addInformationAboutPools(Document document, ArrayList<BPMNProcess> processes) {

        BPMNProperties properties = new BPMNProperties();

        NodeList collaborationNodes = document.getElementsByTagNameNS(properties.getProperty("defaultNamespace"), "collaboration");
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

                    for (BPMNProcess process : processes) {
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

    @Nullable
    private static String getAttributeValue(Node node, String attributeName) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) return null;
        Node attributeNode = attributes.getNamedItem(attributeName);
        if (attributeNode == null) return null;
        return attributeNode.getNodeValue();
    }

}

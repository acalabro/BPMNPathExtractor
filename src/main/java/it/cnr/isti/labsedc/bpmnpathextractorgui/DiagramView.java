package it.cnr.isti.labsedc.bpmnpathextractorgui;

import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.BPMNGraphicProcess;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicActivities.GraphicActivity;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicEvents.GraphicEndEvent;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicEvents.GraphicIntermediateEvent;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicEvents.GraphicStartEvent;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicFlowObject;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicGateways.GraphicGateway;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.diagram.*;
import org.primefaces.model.diagram.connector.FlowChartConnector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class DiagramView {

    private DefaultDiagramModel model;
    private FileUploadView fileUploadView;

    public void drawDiagram(FileUploadEvent event) {

        fileUploadView.handleFileUpload(event);
        model = new DefaultDiagramModel();
        FlowChartConnector connector = new FlowChartConnector();
        connector.setPaintStyle("{strokeStyle:'#000000',lineWidth:1}");
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
                model.addElement(element);
            }
        }

    }

    public DiagramModel getModel() { return model; }

    public FileUploadView getFileUploadView() { return fileUploadView; }
    public void setFileUploadView(FileUploadView fileUploadView) { this.fileUploadView = fileUploadView; }
}
package it.cnr.isti.labsedc.bpmnpathextractorgui;

import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.BPMNGraphicProcess;
import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.GraphicFlowObjects.GraphicFlowObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.diagram.*;
import org.primefaces.model.diagram.connector.FlowChartConnector;

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
                model.addElement(element);
            }
        }

    }

    public DiagramModel getModel() { return model; }

    public FileUploadView getFileUploadView() { return fileUploadView; }
    public void setFileUploadView(FileUploadView fileUploadView) { this.fileUploadView = fileUploadView; }
}
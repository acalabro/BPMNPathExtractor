package it.cnr.isti.labsedc.bpmnpathextractorgui;

import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.BPMNGraphicProcess;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.w3c.dom.Document;

import java.util.ArrayList;

public class FileUploadView {

    private UploadedFile bpmnFile;

    private ArrayList<BPMNGraphicProcess> processes;

    public void handleFileUpload(FileUploadEvent event) {
        bpmnFile = event.getFile();
        Document bpmnDocument = BPMNGraphicParser.createDocument(bpmnFile);
        if (bpmnDocument != null) {
            processes = BPMNGraphicParser.parseProcessList(bpmnDocument);
        }
    }

    public UploadedFile getBpmnFile() { return bpmnFile; }
    public ArrayList<BPMNGraphicProcess> getProcesses() { return processes; }

}

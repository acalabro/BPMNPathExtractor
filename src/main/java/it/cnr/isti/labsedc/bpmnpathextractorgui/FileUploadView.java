package it.cnr.isti.labsedc.bpmnpathextractorgui;

import it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects.BPMNGraphicProcess;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.w3c.dom.Document;

import java.util.ArrayList;

public class FileUploadView {

    private UploadedFile bpmnFile;

    public void handleFileUpload(FileUploadEvent event) {
        bpmnFile = event.getFile();
        ArrayList<BPMNGraphicProcess> processes;
        Document bpmnDocument = BPMNGraphicParser.createDocument(bpmnFile);
        if (bpmnDocument != null) {
            processes = BPMNGraphicParser.parseProcessList(bpmnDocument);
            for (BPMNGraphicProcess process : processes)
                System.out.println(process);
        }

    }

}

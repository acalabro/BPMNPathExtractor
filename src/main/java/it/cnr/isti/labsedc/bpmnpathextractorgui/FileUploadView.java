package it.cnr.isti.labsedc.bpmnpathextractorgui;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

public class FileUploadView {

    private UploadedFile bpmnFile;

    public void handleFileUpload(FileUploadEvent event) {
        bpmnFile = event.getFile();
        System.out.println(bpmnFile.getFileName());
    }

}

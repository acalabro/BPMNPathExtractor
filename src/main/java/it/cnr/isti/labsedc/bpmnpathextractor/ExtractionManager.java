package it.cnr.isti.labsedc.bpmnpathextractor;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ExtractionManager implements Callable {

    private BPMNFilter filter;
    private String bpmnPath;
    private int deepness;
    private List<String> poolsID;
    private List<String> lanesID;
    private int pathType;

    public ExtractionManager(String bpmnPath, int deepness,
                             List<String> poolsID, List<String> lanesID, int pathType) {
        this.filter = new BPMNFilter();
        this.bpmnPath = bpmnPath;
        this.deepness = deepness;
        this.poolsID = poolsID;
        this.lanesID = lanesID;
        this.pathType = pathType;
    }

    @Override
    public ArrayList<BPMNProcess> call() {
        if (pathType == 0) return filter.extractPathsFromBPMNPath(bpmnPath, deepness, poolsID, lanesID);
        else return filter.extractPathsFromBPMNName(bpmnPath, deepness, poolsID, lanesID);
    }

}

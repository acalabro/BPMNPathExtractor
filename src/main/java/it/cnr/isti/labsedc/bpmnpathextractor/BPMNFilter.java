package it.cnr.isti.labsedc.bpmnpathextractor;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNPath;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNProcess;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.FlowObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BPMNFilter {

    private HashMap<String, Document> documentCache;
    private BPMNProperties properties;
    private BPMNPathExtractor extractor = new BPMNPathExtractor();

    public BPMNFilter() {
        documentCache = new HashMap<>();
        properties = new BPMNProperties();
        extractor.setExtractionType(ExtractionType.TYPE_ALFA);
    }

    public ArrayList<BPMNProcess> extractPathsFromBPMNName(String bpmnName, int deepness, List<String> poolsID, List<String> lanesID) {

        Document bpmnDocument = documentCache.get(bpmnName);

        if (bpmnDocument == null)
            bpmnDocument = BPMNParser.parseXMLFromPath(properties.getProperty("dbFolderPath") + "/" + bpmnName + "/" + bpmnName + ".xml");

        if (bpmnDocument != null) {
            ArrayList<BPMNProcess> processes = BPMNParser.parseProcessesList(bpmnDocument);
            if (deepness > 0) processes = filterByDeepness(processes, deepness);
            if (poolsID.size() > 0) processes = filterByPool(processes, poolsID);
            for (BPMNProcess process : processes) {
                extractor.extractPaths(process);
                extractor.explodeProcessesWithSubProcesses(processes, deepness);
            }
            if (lanesID.size() > 0) filterByLane(processes, lanesID);
            else
                for (BPMNProcess process : processes)
                    process.setFilteredPaths(process.getPaths());
            return processes;
        }

        return null;

    }

    private ArrayList<BPMNProcess> filterByDeepness(ArrayList<BPMNProcess> processes, int deepness) {
        ArrayList<BPMNProcess> processesToAnalyze = new ArrayList<>();
        for (BPMNProcess process : processes)
            if (process.getDeepness() <= deepness)
                processesToAnalyze.add(process);
        return processesToAnalyze;
    }

    private ArrayList<BPMNProcess> filterByPool(ArrayList<BPMNProcess> processes, List<String> poolsID) {
        ArrayList<BPMNProcess> processesToAnalyze = new ArrayList<>();
        for (String poolID : poolsID) {
            for (BPMNProcess process : processes) {
                if (process.getPoolID() != null && process.getPoolID().equals(poolID) && !processesToAnalyze.contains(process)) {
                    processesToAnalyze.add(process);
                    break;
                }
            }
        }
        return processesToAnalyze;
    }

    private void filterByLane(ArrayList<BPMNProcess> processes, List<String> lanesID) {
        for (String laneID : lanesID) {
            for (BPMNProcess process : processes) {
                if (process.isPresentLane(laneID)) {
                    for (BPMNPath path : process.getPaths()) {
                        BPMNPath filteredPath = new BPMNPath(process.getPathID());
                        for (FlowObject flowObject : path.getFlowObjects()) {
                            if (flowObject.hasParentLane(laneID)) {
                                filteredPath.appendFlowObject(flowObject);
                            }
                        }
                        if (filteredPath.getFlowObjects().size() > 0) process.addFilteredPath(filteredPath);
                    }
                }
            }
        }
    }

    public void addDocumentToCache(String name, Document document) { documentCache.put(name, document); }
    public Document getDocumentFromCache(String name) { return documentCache.get(name); }

}

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
    private BPMNPathExtractor extractor;

    public BPMNFilter() {
        documentCache = new HashMap<>();
        properties = new BPMNProperties();
        extractor = new BPMNPathExtractor();
        extractor.setExtractionType(ExtractionType.TYPE_ALFA);
    }

    public ArrayList<BPMNProcess> extractPathsFromBPMNName(String bpmnName, int deepness, List<String> poolsID, List<String> lanesID) {

        Document bpmnDocument = documentCache.get(bpmnName);

        if (bpmnDocument == null)
            bpmnDocument = BPMNParser.parseXMLFromPath(properties.getProperty("dbFolderPath") + "/" + bpmnName + "/" + bpmnName + ".xml");

        if (bpmnDocument != null)
            return extractPathsFromBPMNDocument(bpmnDocument, deepness, poolsID, lanesID);

        return null;

    }

    public ArrayList<BPMNProcess> extractPathsFromBPMNPath(String bpmnPath, int deepness, List<String> poolsID, List<String> lanesID) {

        Document bpmnDocument = BPMNParser.parseXMLFromPath(bpmnPath);

        if (bpmnDocument != null)
            return extractPathsFromBPMNDocument(bpmnDocument, deepness, poolsID, lanesID);

        return null;

    }

    private ArrayList<BPMNProcess> extractPathsFromBPMNDocument(Document bpmnDocument, int deepness, List<String> poolsID, List<String> lanesID) {

        ArrayList<BPMNProcess> processes = BPMNParser.parseProcessesList(bpmnDocument);
        if (poolsID.size() > 0) processes = filterByPool(processes, poolsID);
        if (deepness > -1) processes = filterByDeepness(processes, deepness);
        for (BPMNProcess process : processes)
            extractor.extractPaths(process);
        extractor.explodeProcessesWithSubProcesses(processes);

        if (lanesID.size() > 0) filterByLane(processes, lanesID);
        return processes;

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
                    addChildrenRecursively(processesToAnalyze, process);
                    break;
                }
            }
        }

        return processesToAnalyze;
    }

    private void addChildrenRecursively(ArrayList<BPMNProcess> processesToAnalyze, BPMNProcess process) {
        for (BPMNProcess childProcess : process.getChildrenProcesses()) {
            processesToAnalyze.add(childProcess);
            addChildrenRecursively(processesToAnalyze, childProcess);
        }
    }

    private void filterByLane(ArrayList<BPMNProcess> processes, List<String> lanesID) {
        for (BPMNProcess process : processes) {
            ArrayList<BPMNPath> filteredPaths = new ArrayList<>();
            for (BPMNPath path : process.getPaths()) {
                BPMNPath filteredPath = new BPMNPath(process.getPathID());
                for (FlowObject flowObject : path.getFlowObjects())
                    if (flowObject.isAChild(lanesID)) filteredPath.appendFlowObject(flowObject);
                if (filteredPath.getFlowObjects().size() > 0) filteredPaths.add(filteredPath);
            }
            process.setPaths(filteredPaths);
        }
    }

    public void addDocumentToCache(String name, Document document) { documentCache.put(name, document); }
    public Document getDocumentFromCache(String name) { return documentCache.get(name); }

}

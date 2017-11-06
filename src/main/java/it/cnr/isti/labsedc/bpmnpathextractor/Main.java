package it.cnr.isti.labsedc.bpmnpathextractor;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNPath;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNProcess;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.FlowObjects.FlowObject;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ArrayList<BPMNProcess> processes;
        ArrayList<BPMNProcess> processesToAnalyze = new ArrayList<>();
        BPMNPathExtractor extractor = new BPMNPathExtractor();
        extractor.setExtractionType(ExtractionType.TYPE_ALFA);

        if (args.length != 1) {
            System.err.println("Wrong args number");
            System.exit(1);
        }

        Document document = BPMNParser.parseXML(args[0]);

        if (document == null) {
            System.err.println("Error parsing xml");
            System.exit(1);
        }

        processes = BPMNParser.parseProcessesList(document);

        Scanner scanner = new Scanner(System.in);
        String response = null;
        int deepness = -1;

        while (response == null || !(response.equals("y") || response.equals("Y") || response.equals("n") || response.equals("N"))) {
            System.out.println("Do selective extraction by deepness? (y/n)");
            response = scanner.nextLine();
        }

        if (response.equals("y") || response.equals("Y")) {
            System.out.println("Deepness (From 0 to n)");
            String deepnessString = scanner.nextLine();
            deepness = Integer.parseInt(deepnessString);
            for (BPMNProcess process : processes) {
                if (process.getDeepness() <= deepness) processesToAnalyze.add(process);
            }
        } else {
            processesToAnalyze = new ArrayList<>(processes);
        }

        response = null;

        while (response == null || !(response.equals("y") || response.equals("Y") || response.equals("n") || response.equals("N"))) {
            System.out.println("Do selective extraction by pool? (y/n)");
            response = scanner.nextLine();
        }

        if (response.equals("y") || response.equals("Y")) {
            System.out.println("ID of pools to analyze, one for each row ('q' to go to next step): ");

            while (true) {
                String poolID = scanner.nextLine();
                if (poolID.equals("q")) break;
                boolean found = false;
                for (BPMNProcess process : processesToAnalyze) {
                    if (process.getPoolID() != null && process.getPoolID().equals(poolID) && !processesToAnalyze.contains(process)) {
                        processesToAnalyze.add(process);
                        if (process.getPoolName() != null)
                            System.out.println("Pool: " + process.getPoolName() + " added");
                        else
                            System.out.println("Pool: " + process.getPoolID() + " added.");
                        found = true;
                    }
                }
                if (!found) System.out.println("Wrong pool ID.");
            }
        }

        for (BPMNProcess process : processesToAnalyze) {
            extractor.extractPaths(process);
            extractor.explodeProcessesWithSubProcesses(processesToAnalyze, deepness);
        }

        response = null;

        while (response == null || !(response.equals("y") || response.equals("Y") || response.equals("n") || response.equals("N"))) {
            System.out.println("Do selective extraction by lane? (y/n)");
            response = scanner.nextLine();
        }

        if (response.equals("y") || response.equals("Y")) {
            System.out.println("ID of lanes to analyze, one for each row ('q' to go to next step): ");

            while (true) {
                String laneID = scanner.nextLine();
                if (laneID.equals("q")) break;
                boolean found = false;

                for (BPMNProcess process : processesToAnalyze) {
                    if (process.isPresentLane(laneID)) {
                        found = true;
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

                if (found) System.out.println("Lane " + laneID + " added");
                else System.out.println("Wrong lane ID.");
            }

        } else {
            for (BPMNProcess process : processes) {
                process.setFilteredPaths(process.getPaths());
            }
        }

        for (BPMNProcess process : processesToAnalyze) {
            System.out.println("Paths: " + process.getId() + System.lineSeparator());
            for (BPMNPath path : process.getFilteredPaths())
                System.out.println(path);
        }

    }

}

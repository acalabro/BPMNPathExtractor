package it.cnr.isti.labsedc.bpmnpathextractor;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNCycle;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNPath;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNProcess;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ArrayList<BPMNProcess> processes;
        ArrayList<BPMNProcess> processesToAnalyze = new ArrayList<>();

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

        while (response == null || !(response.equals("y") || response.equals("Y") || response.equals("n") || response.equals("N"))) {
            System.out.println("Do selective extraction by pool or lane? (y/n)");
            response = scanner.nextLine();
        }

        if (response.equals("y") || response.equals("Y")) {
            System.out.println("ID of pools to analyze, one for each row ('q' to go to next step): ");

            while (true) {
                String poolID = scanner.nextLine();
                if (poolID.equals("q")) break;
                boolean found = false;
                for (BPMNProcess process : processes) {
                    if (process.getPoolID().equals(poolID) && !processesToAnalyze.contains(process)) {
                        processesToAnalyze.add(process);
                        System.out.println("Pool: " + process.getPoolID() + " added.");
                        found = true;
                    }
                }
                if (!found) System.out.println("Wrong pool ID.");
            }
        } else {
            processesToAnalyze = processes;
        }

        for (BPMNProcess process : processesToAnalyze) {
            BPMNPathExtractor.extractPaths(process);
            System.out.println("Paths: " + process.getPoolID() + System.lineSeparator());
            for (BPMNPath path : process.getPaths())
                System.out.println(path);
            System.out.println("Cycles: " + process.getPoolID() + System.lineSeparator());
            for (BPMNCycle cycle : process.getCycles())
                System.out.println(cycle);
        }

    }

}

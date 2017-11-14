package it.cnr.isti.labsedc.bpmnpathextractor;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNPath;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ParametersReader {

    public static int getDeepness() {

        int deepness = -1;
        String response = null;
        Scanner scanner = new Scanner(System.in);

        while (response == null || !(response.equals("y") || response.equals("Y") || response.equals("n") || response.equals("N"))) {
            System.out.println("Do selective extraction by deepness? (y/n)");
            response = scanner.nextLine();
        }

        if (response.equals("y") || response.equals("Y")) {
            System.out.println("Deepness (From 0 to n)");
            String deepnessString = scanner.nextLine();
            deepness = Integer.parseInt(deepnessString);
        }

        return deepness;

    }

    public static List<String> getPoolsID() {

        ArrayList<String> poolsID = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        String response = null;

        while (response == null || !(response.equals("y") || response.equals("Y") || response.equals("n") || response.equals("N"))) {
            System.out.println("Do selective extraction by pool? (y/n)");
            response = scanner.nextLine();
        }

        if (response.equals("y") || response.equals("Y")) {
            System.out.println("ID of pools to analyze, one for each row ('q' to go to next step): ");

            while (true) {
                String poolID = scanner.nextLine();
                if (poolID.equals("q")) break;
                poolsID.add(poolID);
            }

        }

        return poolsID;

    }

    public static List<String> getLanesID() {

        ArrayList<String> lanesID = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        String response = null;

        while (response == null || !(response.equals("y") || response.equals("Y") || response.equals("n") || response.equals("N"))) {
            System.out.println("Do selective extraction by lane? (y/n)");
            response = scanner.nextLine();
        }

        if (response.equals("y") || response.equals("Y")) {
            System.out.println("ID of lanes to analyze, one for each row ('q' to go to next step): ");

            while (true) {
                String laneID = scanner.nextLine();
                if (laneID.equals("q")) break;
                lanesID.add(laneID);
            }

        }

        return lanesID;

    }

    public static void printPaths(ArrayList<BPMNProcess> processes) {
        for (BPMNProcess process : processes) {
            if (process.getDeepness() == 0) {
                System.out.println("Paths: " + process.getId() + System.lineSeparator());
                for (BPMNPath path : process.getPaths())
                    System.out.println(path);
            }
        }
    }

}

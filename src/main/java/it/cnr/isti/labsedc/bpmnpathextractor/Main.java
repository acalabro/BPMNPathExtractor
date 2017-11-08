package it.cnr.isti.labsedc.bpmnpathextractor;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        BPMNFilter filter = new BPMNFilter();

        if (args.length != 1) {
            System.err.println("Wrong args number");
            System.exit(1);
        }

        int deepness = ParametersReader.getDeepness();
        List<String> poolsID = ParametersReader.getPoolsID();
        List<String> lanesID = ParametersReader.getLanesID();

        ParametersReader.printPaths(filter.extractPathsFromBPMNPath(args[0], deepness, poolsID, lanesID));

    }

}

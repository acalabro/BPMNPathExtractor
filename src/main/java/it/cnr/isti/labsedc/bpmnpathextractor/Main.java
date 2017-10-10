package it.cnr.isti.labsedc.bpmnpathextractor;

import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNPath;
import it.cnr.isti.labsedc.bpmnpathextractor.Objects.BPMNProcess;
import org.w3c.dom.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        ArrayList<BPMNProcess> processes;

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

        for (BPMNProcess process : processes) {
            BPMNPathExtractor.extractPaths(process);
            for (BPMNPath path : process.getPaths())
                System.out.println(path);
        }

    }

}

package it.cnr.isti.labsedc.bpmnpathextractor.RestInterface;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class JSONProcess {

    private String id;
    private List<List<String>> paths;

    public JSONProcess() {}

    public JSONProcess(String id) {
        this.id = id;
        paths = new ArrayList<>();
    }

    public String getId() { return id; }
    public List<List<String>> getPaths() { return paths; }

    public void setId(String id) { this.id = id; }
    public void setPaths(List<List<String>> paths) { this.paths = paths; }

    public void addPath(List<String> path) { paths.add(path); }

}

package it.cnr.isti.labsedc.bpmnpathextractorgui;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class CheckBoxView {

    private String name;

    private int maxDeepness;
    private int selectedDeepness;

    private String[] selectedPools;
    private String[] selectedLanes;

    private List<String> pools;
    private List<String> lanes;

    @PostConstruct
    public void init() {
        pools = new ArrayList<>();
        lanes = new ArrayList<>();
    }

    public List<String> completeDeepnessList(String query) {

        List<String> results = new ArrayList<>();

        for (int i = 0; i <= maxDeepness; i++)
            results.add(query + i);

        return results;

    }

    public void clearInformation() {
        maxDeepness = 0;
        selectedDeepness = 0;
        selectedPools = null;
        selectedLanes = null;
        pools = new ArrayList<>();
        lanes = new ArrayList<>();
    }

    public void sendExtractionRequest() {
        System.out.println(selectedDeepness);
        for (String pool : selectedPools)
        System.out.println(pool);
        for (String lane : selectedLanes)
        System.out.println(lane);
    }

    public String getName() { return name; }

    public int getMaxDeepness() { return maxDeepness; }
    public int getSelectedDeepness() { return selectedDeepness; }

    public List<String> getPools() { return pools; }
    public List<String> getLanes() { return lanes; }

    public String[] getSelectedPools() { return selectedPools; }
    public String[] getSelectedLanes() { return selectedLanes; }

    public void setName(String name) { this.name = name; }

    public void setMaxDeepness(int maxDeepness) { this.maxDeepness = maxDeepness; }
    public void setSelectedDeepness(int selectedDeepness) { this.selectedDeepness = selectedDeepness; }

    public void setSelectedPools(String[] selectedPools) { this.selectedPools = selectedPools; }
    public void setSelectedLanes(String[] selectedLanes) { this.selectedLanes = selectedLanes; }

    public void addPool(String pool) { pools.add(pool); }
    public void addLane(String lane) { lanes.add(lane); }

}

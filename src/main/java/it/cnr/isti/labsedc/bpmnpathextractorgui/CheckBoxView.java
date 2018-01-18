package it.cnr.isti.labsedc.bpmnpathextractorgui;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CheckBoxView {

    private String name;

    private int maxDeepness;
    private int selectedDeepness;

    private String[] selectedPools;
    private String[] selectedLanes;
    private String selectedPath;

    private List<String> pools;
    private List<String> lanes;
    private List<String> pathsList;

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
        selectedPath = null;
        pools = new ArrayList<>();
        lanes = new ArrayList<>();
        pathsList = new ArrayList<>();
    }

    public void sendExtractionRequest() {

        StringBuilder requestURL = new StringBuilder("http://localhost:8080/bpmn-path-extractor/bpmn_api/path_extractor/");
        requestURL.append(name);
        requestURL.append("?deepness=").append(selectedDeepness);
        for (String pool : selectedPools)
            requestURL.append("&poolID=").append(pool);
        for (String lane : selectedLanes)
            requestURL.append("&laneID=").append(lane);

        try {
            URL url = new URL(requestURL.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() < 200 || connection.getResponseCode() > 299)
                throw new RuntimeException(connection.getResponseMessage());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder requestResult = new StringBuilder();
            String output;

            while ((output = reader.readLine()) != null)
                requestResult.append(output);

            JSONArray processes = new JSONArray(requestResult.toString());

            for (int i = 0; i < processes.length(); i++) {

                JSONObject process = processes.getJSONObject(i);
                String processID = process.getString("id");
                JSONArray paths = process.getJSONArray("paths");

                for (int j = 0; j < paths.length(); j++)
                    pathsList.add(processID + ": Path " + j);

            }

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() { return name; }

    public int getMaxDeepness() { return maxDeepness; }
    public int getSelectedDeepness() { return selectedDeepness; }

    public List<String> getPools() { return pools; }
    public List<String> getLanes() { return lanes; }
    public List<String> getPathsList() { return pathsList; }

    public String[] getSelectedPools() { return selectedPools; }
    public String[] getSelectedLanes() { return selectedLanes; }
    public String getSelectedPath() { return selectedPath; }

    public void setName(String name) { this.name = name; }

    public void setMaxDeepness(int maxDeepness) { this.maxDeepness = maxDeepness; }
    public void setSelectedDeepness(int selectedDeepness) { this.selectedDeepness = selectedDeepness; }

    public void setSelectedPools(String[] selectedPools) { this.selectedPools = selectedPools; }
    public void setSelectedLanes(String[] selectedLanes) { this.selectedLanes = selectedLanes; }
    public void setSelectedPath(String selectedPath) { this.selectedPath = selectedPath; }

    public void addPool(String pool) { pools.add(pool); }
    public void addLane(String lane) { lanes.add(lane); }
    public void setPathsList(List<String> pathsList) { this.pathsList = pathsList; }

}

package it.cnr.isti.labsedc.bpmnpathextractorgui.GraphicObjects;

public class LaneCoordinate {

    private final String laneID;
    private final int posX;
    private final int posY;
    private final int width;
    private final int height;

    public LaneCoordinate(String laneID, int posX, int posY, int width, int height) {
        this.laneID = laneID;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public String getLaneID() { return laneID; }
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

}

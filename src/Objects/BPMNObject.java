package Objects;

public abstract class BPMNObject {

    protected final String id;
    protected final String name;

    public BPMNObject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

}

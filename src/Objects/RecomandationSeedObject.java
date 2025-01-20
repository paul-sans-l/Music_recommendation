package Objects;

public class RecomandationSeedObject {
    int afterFilteringSize;
    int afterRelinkingSize;
    String href;
    String id;
    int initialPoolSize;
    String type;

    public RecomandationSeedObject(int afterFilteringSize, int afterRelinkingSize, String href, String id, int initialPoolSize, String type) {
        this.afterFilteringSize = afterFilteringSize;
        this.afterRelinkingSize = afterRelinkingSize;
        this.href = href;
        this.id = id;
        this.initialPoolSize = initialPoolSize;
        this.type = type;
    }

    public int getAfterFilteringSize() {
        return afterFilteringSize;
    }

    public int getAfterRelinkingSize() {
        return afterRelinkingSize;
    }

    public String getHref() {
        return href;
    }

    public String getId() {
        return id;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public String getType() {
        return type;
    }
}

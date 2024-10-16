public class Website {
    private int id;
    private String url;
    private String displayText;

    public Website(int id, String url, String displayText) {
        this.id = id;
        this.url = url;
        this.displayText = displayText;
    }

    public Website(String url, String displayText) {
        this.url = url;
        this.displayText = displayText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }
}

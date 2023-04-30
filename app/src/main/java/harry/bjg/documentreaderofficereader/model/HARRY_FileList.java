package harry.bjg.documentreaderofficereader.model;

public class HARRY_FileList {

    private String title;
    private String path;

    public HARRY_FileList(String title, String path) {
        this.title = title;
        this.path = path;
    }

    public String getTitle() {
        return this.title;
    }

    public String getPath() {
        return this.path;
    }
}

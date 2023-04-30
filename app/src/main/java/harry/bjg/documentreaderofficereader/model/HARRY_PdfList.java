package harry.bjg.documentreaderofficereader.model;

public class HARRY_PdfList {

    private String title;
    private String path;

    public HARRY_PdfList(String title, String path) {
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

package harry.bjg.documentreaderofficereader.model;

import java.util.ArrayList;

public class HARRY_ImageFolderData {
    String folder;
    ArrayList<HARRY_ImageData> path;

    public HARRY_ImageFolderData(String folder, ArrayList<HARRY_ImageData> path) {
        this.folder = folder;
        this.path = path;
    }

    public String getfolder() {
        return folder;
    }

    public void setfolder(String folder) {
        this.folder = folder;
    }

    public ArrayList<HARRY_ImageData> getPath() {
        return path;
    }

    public void setPath(ArrayList<HARRY_ImageData> path) {
        this.path = path;
    }
}

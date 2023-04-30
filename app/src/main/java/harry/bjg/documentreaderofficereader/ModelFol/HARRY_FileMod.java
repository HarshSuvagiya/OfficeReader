package harry.bjg.documentreaderofficereader.ModelFol;

import java.io.Serializable;

public class HARRY_FileMod implements Serializable {

    String name;
    String path;
    String ext;
    String size;
    long sizel;

    public HARRY_FileMod(String name, String path, String ext, String size, long sizel) {
        this.name = name;
        this.path = path;
        this.ext = ext;
        this.size = size;
        this.sizel = sizel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getSizel() {
        return sizel;
    }

    public void setSizel(long sizel) {
        this.sizel = sizel;
    }
}

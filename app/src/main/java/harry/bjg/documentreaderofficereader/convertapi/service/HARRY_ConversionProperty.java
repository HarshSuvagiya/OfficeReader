package harry.bjg.documentreaderofficereader.convertapi.service;

import java.io.File;

public class HARRY_ConversionProperty {
    private File inputFile;
    private String inputFormat;
    private String outputFormat;

    public static class ConversionPropertyBuilder {
        public File inputFile;
        public String inputFormat;
        public String outputFormat;

        public ConversionPropertyBuilder inputFile(File inputFile2) {
            this.inputFile = inputFile2;
            return this;
        }

        public ConversionPropertyBuilder inputFormat(String inputFormat2) {
            this.inputFormat = inputFormat2;
            return this;
        }

        public ConversionPropertyBuilder outputFormat(String outputFormat2) {
            this.outputFormat = outputFormat2;
            return this;
        }

        public HARRY_ConversionProperty build() {
            return new HARRY_ConversionProperty(this);
        }
    }

    public File getInputFile() {
        return this.inputFile;
    }

    public void setInputFile(File inputFile2) {
        this.inputFile = inputFile2;
    }

    public String getInputFormat() {
        return this.inputFormat;
    }

    public void setInputFormat(String inputFormat2) {
        this.inputFormat = inputFormat2;
    }

    public String getOutputFormat() {
        return this.outputFormat;
    }

    public void setOutputFormat(String outputFormat2) {
        this.outputFormat = outputFormat2;
    }

    public HARRY_ConversionProperty(ConversionPropertyBuilder builder) {
        this.inputFile = builder.inputFile;
        this.inputFormat = builder.inputFormat;
        this.outputFormat = builder.outputFormat;
    }
}

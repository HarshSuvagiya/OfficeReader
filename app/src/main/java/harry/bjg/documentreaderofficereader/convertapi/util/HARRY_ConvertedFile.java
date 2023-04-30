package harry.bjg.documentreaderofficereader.convertapi.util;

import java.io.File;
import java.util.Date;

public class HARRY_ConvertedFile {
    public static final String DOC = "doc";
    public static final String DOCX = "docx";
    public static final String DOCM = "docm";
    public static final int HOST_CLOUD_CONVERT = 0;
    public static final int HOST_ZAMZAR = 1;
    public static int MAX_RETRY = 10;
    public static final String ODT = "odt";
    public static final String PDF = "pdf";
    public static final String PPT = "ppt";
    public static final String PPTX = "pptx";
    public static final String PPTM = "pptm";
    public static final String RTF = "rtf";
    public static final int STATUS_COMPLETE = 4;
    public static final int STATUS_CONVERTING = 2;
    public static final int STATUS_DOWNLOADING = 3;
    public static final int STATUS_ERROR = 5;
    public static final int STATUS_STARTING = 0;
    public static final int STATUS_UPLOADING = 1;
    public static final String TXT = "txt";
    public static final String XLS = "xls";
    public static final String XLSX = "xlsx";
    public static final String XLSM = "xlsm";
    public static final String JPG = "jpg";
    public static final String HTML = "html";
    private int downloadPercentage;
    private String downloadUrl;
    private int host;

    private Long f1070id;
    private String inputFormat;
    private Date lastModified;
    private String outputFormat;
    private String outputName;
    private String outputPath;
    private String processID;
    private String processUrl;
    private int retryCount;
    private String sourcePath;
    private int status;
    private int uploadPercentage;
    private String uploadUrl;

    public HARRY_ConvertedFile(Long id, String sourcePath2, String outputPath2, Date lastModified2, int status2, int host2, String processID2, String processUrl2, String uploadUrl2, String downloadUrl2, String inputFormat2, String outputName2, String outputFormat2, int uploadPercentage2, int downloadPercentage2, int retryCount2) {
        this.f1070id = id;
        this.sourcePath = sourcePath2;
        this.outputPath = outputPath2;
        this.lastModified = lastModified2;
        this.status = status2;
        this.host = host2;
        this.processID = processID2;
        this.processUrl = processUrl2;
        this.uploadUrl = uploadUrl2;
        this.downloadUrl = downloadUrl2;
        this.inputFormat = inputFormat2;
        this.outputName = outputName2;
        this.outputFormat = outputFormat2;
        this.uploadPercentage = uploadPercentage2;
        this.downloadPercentage = downloadPercentage2;
        this.retryCount = retryCount2;
    }

    public HARRY_ConvertedFile(String sourcePath2, String outputFormat2, String outputName2) {
        this.sourcePath = sourcePath2;
        this.outputFormat = outputFormat2;
        this.lastModified = new Date();
        this.status = 0;
        this.outputName = outputName2;
        this.retryCount = 0;
    }

    public Long getId() {
        return this.f1070id;
    }

    public void setId(Long id) {
        this.f1070id = id;
    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(Date lastModified2) {
        this.lastModified = lastModified2;
    }

    public String getSourcePath() {
        return this.sourcePath;
    }

    public void setSourcePath(String sourcePath2) {
        this.sourcePath = sourcePath2;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status2) {
        this.status = status2;
    }

    public boolean hasError() {
        return this.status == 5;
    }

    public boolean isCompleted() {
        return this.status == 4;
    }

    public String getProcessID() {
        return this.processID;
    }

    public void setProcessID(String processID2) {
        this.processID = processID2;
    }

    public String getProcessUrl() {
        return this.processUrl;
    }

    public void setProcessUrl(String processUrl2) {
        this.processUrl = processUrl2;
    }

    public int getUploadPercentage() {
        return this.uploadPercentage;
    }

    public void setUploadPercentage(int uploadPercentage2) {
        this.uploadPercentage = uploadPercentage2;
    }

    public int getDownloadPercentage() {
        return this.downloadPercentage;
    }

    public void setDownloadPercentage(int downloadPercentage2) {
        this.downloadPercentage = downloadPercentage2;
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

    public String getUploadUrl() {
        return this.uploadUrl;
    }

    public void setUploadUrl(String uploadUrl2) {
        this.uploadUrl = uploadUrl2;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl2) {
        this.downloadUrl = downloadUrl2;
    }

    public File getSourceFile() {
        return new File(this.sourcePath);
    }

    public File getOutputFile() {
        if (hasError() || this.outputPath == null) {
            return null;
        }
        return new File(this.outputPath);
    }

    public String getOutputFileName() {
        return this.outputName + "." + this.outputFormat;
    }

    public String getOutputPath() {
        return this.outputPath;
    }

    public void setOutputPath(String outputPath2) {
        this.outputPath = outputPath2;
    }

    public long getOutputFileSize() {
        return getOutputFile().length();
    }

    public void reset() {
        this.status = 0;
        this.processUrl = "";
        this.uploadUrl = "";
        this.downloadUrl = "";
        this.uploadPercentage = 0;
        this.downloadPercentage = 0;
    }

    public int getHost() {
        return this.host;
    }

    public void setHost(int host2) {
        this.host = host2;
    }

    public boolean isPDFFile(String format) {
        return format.equals(PDF);
    }

    public boolean isWordFile(String format) {
        return format.equals(DOC) || format.equals(DOCX);
    }

    public boolean isPowerPointFile(String format) {
        return format.equals(PPT) || format.equals(PPTX);
    }

    public boolean isExcelFile(String format) {
        return format.equals(XLS) || format.equals(XLSX);
    }

    public boolean isTextFile(String format) {
        return format.equals(TXT);
    }

    public static boolean isFormatValid(String format) {
        return format.equals(PDF) || format.equals(DOC) || format.equals(DOCX) || format.equals(DOCM) || format.equals(PPT) || format.equals(PPTX) || format.equals(PPTM) || format.equals(XLS) || format.equals(XLSX) || format.equals(XLSM) || format.equals(TXT) || format.equals(ODT) || format.equals(RTF);
    }

    public String getOutputName() {
        return this.outputName;
    }

    public void setOutputName(String outputName2) {
        this.outputName = outputName2;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void setRetryCount(int retryCount2) {
        this.retryCount = retryCount2;
    }

    public void addRetryCount() {
        this.retryCount++;
        if (this.retryCount >= MAX_RETRY) {
            this.retryCount = MAX_RETRY;
        }
    }
}

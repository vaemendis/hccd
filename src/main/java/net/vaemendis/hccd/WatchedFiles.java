package net.vaemendis.hccd;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class WatchedFiles {

    private File htmlFile;
    private File cssFile;
    private File csvFile;
    private File parentDir;

    public WatchedFiles(File htmlFile) {
        this.htmlFile = htmlFile;
        parentDir = htmlFile.getParentFile();
        String root = FilenameUtils.getBaseName(htmlFile.getName());
        cssFile = new File(parentDir, root + ".css");
        csvFile = new File(parentDir, root + ".csv");
    }

    public File getHtmlFile() {
        return htmlFile;
    }

    public File getCssFile() {
        return cssFile;
    }

    public File getCsvFile() {
        return csvFile;
    }

    public File getParentDir() {
        return parentDir;
    }

    @Override
    public String toString(){
        return htmlFile.getName() + ", " +
                cssFile.getName() + ", " +
                csvFile.getName() + " in " +
                parentDir.getPath();
    }
}

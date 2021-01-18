package com.dags.lockedmeofficial.operations;

import java.io.IOException;

public interface Operations {
    void showFilesInDirectory();
    boolean searchForFile(String filePathStr);
    String deleteFile(String filePathStr) throws IOException;
    String addFile(String filePathString);
}

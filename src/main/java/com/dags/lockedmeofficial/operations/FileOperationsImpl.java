package com.dags.lockedmeofficial.operations;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
    A subclass of Operations.FileOperations that provides alternative functionality.
    Essentially: This is a different interpretation on some of the instructions.
 */
public class FileOperationsImpl extends FileOperations {

    public FileOperationsImpl(String DIRECTORY_PATH) {
        super(DIRECTORY_PATH);
    }

    /*
        This implementation only checks for files that exist in the ROOT FOLDER
     */
    @Override
    public boolean searchForFile(String filePathStr) {

        Path path = Paths.get(filePathStr);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(this.getDirectoryPath()),
                p -> p.toFile().isFile() && Files.isRegularFile(Paths.get(p.toString()))
                        && p.getFileName().toString().equals(path.getFileName().toString()))) {

            List<Path> pathsList = new ArrayList<>();
            directoryStream.forEach(pathsList::add);

            pathsList.forEach(System.out::println);

            return !pathsList.isEmpty();

        } catch (IOException e) {
            System.out.println("Error while navigating directory!");
            return false;
        }
    }

    /*
        This implementation sorts the files in Java's lexicographical, case sensitive ascending order, and only
        shows file names and NOT the full path of the file.

        NOTE: These are all of the files belonging to the directory, not just those in the "root folder".
     */

    @Override
    public void showFilesInDirectory() {

        List<String> filePathsArrayList = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(this.getDirectoryPath()))) {
            filePathsArrayList = paths.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.out.println("Error while walking directory!");
        }

        if (filePathsArrayList.isEmpty()) {
            System.out.println("No files in directory: " + this.getDirectoryPath());
        }else {
            //printing ONLY the filenames
            filePathsArrayList.forEach(System.out::println);
            System.out.println();
        }

        /*
            Below is just an example of how to only print the files of the root directory.
         */

//        List<File> files = Arrays.asList(new File(this.getDirectoryPath()).listFiles());
//        files.stream().filter(file -> !file.isDirectory())
//                .map(File::getName)
//                .sorted()
//                .forEach(System.out::println);
    }

}
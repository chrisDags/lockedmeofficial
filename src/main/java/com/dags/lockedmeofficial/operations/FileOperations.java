package com.dags.lockedmeofficial.operations;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileOperations implements Operations {

    private final String DIRECTORY_PATH;

    // The constructor injects the directory path and creates the directory
    public FileOperations(String DIRECTORY_PATH) {
        this.DIRECTORY_PATH = DIRECTORY_PATH;
        this.createDirectory();
    }


    /*
       FROM THE INSTRUCTIONS:
       "The root directory can be either empty or contain few files or folders in it."

        MY INTERPRETATION:
        Since our directory can contain existing folders and files, in addition to the fact that a user can add files
        and folders to this directory outside of the program WHILE it is running, we must navigate through our
        entire directory to gather all of the files.

     */
    @Override
    public void showFilesInDirectory() {

        // Grab all of the files in the directory and collect them in a list.
            /*
              FROM THE INSTRUCTIONS:
              "The first option should return the current file names in ascending order.
              The root directory can be either empty or contain few files or folders in it"

              MY INTERPRETATION:
              I opted for a case-insensitive ascending order. The lexicographical natural order sort is case-sensitive,
              which didn't seem correct based on my interpretation of the instructions.
              However, I have made a separate implementation that uses Java's natural lexicographical ordering
              (upper-case letters come before lower-case) in Operations.FileOperationsImpl.
            */
        try (Stream<Path> paths = Files.walk(Paths.get(DIRECTORY_PATH))) {
            List<String> pathsList = paths.filter(Files::isRegularFile)
                    .map(Path::toString)
                    // sort the file paths case-insensitively in alphabetical order by file name.
                    .sorted(Comparator.comparing(s -> s.toLowerCase().substring(s.lastIndexOf(System.lineSeparator()) + 1)))
                    .collect(Collectors.toList());

            if (pathsList.isEmpty()) {
                System.out.println("No files in directory: " + DIRECTORY_PATH);
            }
            else {
            /*
                I opted to print the full path of the files. The instructions did not specify whether or not
                we should ONLY include the filenames and not the full path of the file.
             */
                pathsList.forEach(System.out::println);

            /*
                This line below can be uses in place of 'filePathsArrayList.forEach(System.out::println)' in order
                to only print the file names. This implementation is included in Operations.FileOperationsImpl.
             */
                ///pathsList.forEach(s -> System.out.println(Paths.get(s).getFileName()));
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
        FROM THE INSTRUCTIONS:
        "Delete a user specified file from the existing directory list"
        - "You can add the case sensitivity on the file name in order to ensure that the right file is deleted from the directory list"
        - "Return a message if FNF (File not found)"

        MY INTERPRETATION:
        The user must be able to delete and search any file inside our directory, not just from a single folder.

        For example, the user should be able to delete or search: C:\RootFolder\UserMadeFolder\UserMadeFile.txt
        because 'UserMadeFolder' is a folder in our directory that contains 'UserMadeFile.txt'.

        As for the case sensitivity, my interpretation is:
            - user searches/deletes: FOO.png when the file in folder is named foo.png = ERROR
            - user searches/deletes: foo.PNG when the file in folder is named foo.png = ERROR
            - user searches/deletes: foo.png when the file in folder is named foo.png = SUCCESS

     */

    @Override
    public String deleteFile(String filePathStr) throws IOException {

        try {
            Path dirPath = Paths.get(DIRECTORY_PATH);
            Path path = Paths.get(filePathStr);

            File file = new File(filePathStr);
            File dirPathFile = new File(dirPath.toString());
            File fileFromFilePathStr = new File(path.toString());

            if(!file.isFile() || !(file.getCanonicalFile().getName().equals(path.getFileName().toString()))){
                return "File not found in directory: " + DIRECTORY_PATH;
            }

            if (file.getCanonicalFile().toString().startsWith(dirPathFile.getCanonicalPath()) &&
                    file.getCanonicalFile().toString().equals(fileFromFilePathStr.getCanonicalFile().toString())) {
                Files.delete(path);
                return "File deleted: " + filePathStr;
            }
        } catch (InvalidPathException e) {
            return "Filepath is invalid.";
        }

        return "File does not exist in the directory: " + DIRECTORY_PATH;
    }
    /*
        FROM THE INSTRUCTIONS:
        "Search a user specified file from the main directory"
            - "You can add the case sensitivity on the file name to retrieve the correct file"
            - "Display the result upon successful operation"
            - "Display the result upon unsuccessful operation"

        MY INTERPRETATION:
        The user can search for any file that exists in the directory. However, this might not be what is meant by
        "main directory", so I have provided an alternative solution in Operations.FileOperationsImpl that only searches
        in the root folder.

     */
    @Override
    public boolean searchForFile(String filePathStr) {

        try {

            File file = new File(filePathStr);
            File dirPathFile = new File(DIRECTORY_PATH);

            Path path = Paths.get(file.getCanonicalFile().toString());

            if (file.getCanonicalFile().toString().startsWith(dirPathFile.getCanonicalPath()) &&
                    // It is assumed that the files are all regular files since the instructions did not specify.
                    Files.isRegularFile(path) && file.getName().equals(path.getFileName().toString()))
                return true;

        } catch (InvalidPathException | IOException e) {
            System.out.println("Path is invalid.");
            return false;
        }
        return false;
    }

    /*
        FROM THE INSTRUCTIONS:
            - "Add a file to the existing directory list"
            - "You can ignore the case sensitivity of the file names"

        MY INTERPRETATION:
        Our instructor defined 'adding' as copying a file from a separate location on the user's computer to the
        new directory we have made.

        The instructions did not specify where exactly we should be adding these files inside our directory,
        so it is assumed that I need to be adding to the root of the directory.

     */
    @Override
    public String addFile(String filePathString) {

        try {
            if (!Paths.get(filePathString).toFile().isFile()) {
                return "The file you want to add does not exist.";

            }
            /*
                Creating a File object and then calling file.getCanonicalFile().toString() in the Path argument
                allows the user to use forward slashes or backslashes when inputting a filepath.

                For example:
                    user inputs -> C:\MyFolder\MyFile.txt == VALID
                    user inputs -> C:/MyFolder/MyFile.txt == VALID
             */
            File file = new File(filePathString);
            Path path = Paths.get(file.getCanonicalFile().toString());

            String newFilePathStr = DIRECTORY_PATH +File.separator + path.getFileName();

            int count = 0;
            while (Files.exists(Paths.get(newFilePathStr))) {
                count++;
                newFilePathStr = DIRECTORY_PATH + File.separator + count + "_" + path.getFileName();
            }

            Files.copy(path, Paths.get(newFilePathStr));

        } catch (IOException e) {
            return "Unable to copy file to path: " + DIRECTORY_PATH;
        } catch (InvalidPathException e) {
            return "File Invalid: contains illegal characters.";
        }

        return "Successfully added file from: " + filePathString + "\nTo the folder: " + DIRECTORY_PATH + "\n";
    }


    public void createDirectory() {
        Path path = Paths.get(DIRECTORY_PATH);
        boolean preExistingDirectory = false;

        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            preExistingDirectory = true;
        }

        /*
            FROM THE INSTRUCTIONS: "The root directory can be either empty or contain few files or folders in it."

            To account for a previously made directory: If the directory already exists, simply let the user know and
            continue using the directory.
         */
        if (preExistingDirectory)
            System.out.println("\nUSING EXISTING DIRECTORY: " + DIRECTORY_PATH);
        else
            System.out.println("\nUSING NEW DIRECTORY: " + DIRECTORY_PATH);

    }

    // getter for the directory path, useful for subclasses
    public String getDirectoryPath(){
        return DIRECTORY_PATH;
    }


}
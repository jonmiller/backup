package com.jonamiller.backup;

import com.github.fracpete.processoutput4j.output.ConsoleOutputProcessOutput;
import com.github.fracpete.rsync4j.RSync;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Backup {

  private static final String BACKUP_DRIVE = "F:/";
  private static final String BACKUP_FOLDER_NAME = "Backup";

  private static final String HOME_DIR = "C:/Users/Jon";
  private static final List<String> HOME_DIR_SUBPATHS =
      List.of(
          ".IdeaIC2019.3",
          "Archived",
          "Documents/Assorted",
          "Documents/Finances",
          "Documents/Other",
          "Documents/School",
          "Music",
          "Pictures",
          "Programs",
          "Projects",
          "SSH Keys",
          "Ubiquiti UniFi",
          "Videos");

  private static final String SECOND_DRIVE = "E:/";
  private static final List<String> SECOND_DRIVE_SUBPATHS =
      List.of(
          "backup.tc",
          "Ubuntu-SSN");
  private static final String SECOND_DRIVE_BACKUP_FOLDER_NAME = "Second";

  public static void main(String[] args) {
    try {
      Backup backup = new Backup();
      backup.execute();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void execute() throws IOException {
    Path backupFolder = Paths.get(BACKUP_DRIVE, BACKUP_FOLDER_NAME);
    deleteDirectoryIfExists(backupFolder);
    Files.createDirectory(backupFolder);

    Path homeDir = Paths.get(HOME_DIR);
    for (String subpath : HOME_DIR_SUBPATHS) {
      Path sourcePath = homeDir.resolve(subpath);
      Path destinationPath = Files.createDirectory(backupFolder.resolve(subpath));
      Files.walk(sourcePath).forEach(source ->
          copy(source, destinationPath.resolve(sourcePath.relativize(source))));
    }

    Path secondDrive = Paths.get(SECOND_DRIVE);
    Path secondDriveBackupFolder =
        Files.createDirectory(backupFolder.resolve(SECOND_DRIVE_BACKUP_FOLDER_NAME));

    for (String subpath : SECOND_DRIVE_SUBPATHS) {
      Path sourcePath = secondDrive.resolve(subpath);
      Path destinationPath = Files.createDirectory(secondDriveBackupFolder.resolve(subpath));
      Files.walk(sourcePath).forEach(source ->
          copy(source, destinationPath.resolve(sourcePath.relativize(source))));
    }
  }

  private void copy(Path source, Path dest) {
    try {
      Files.copy(source, dest, REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void deleteDirectoryIfExists(Path path) throws IOException {
    if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
      try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
        for (Path entry : entries) {
          deleteDirectoryIfExists(entry);
        }
      }
    }
    Files.deleteIfExists(path);
  }
}

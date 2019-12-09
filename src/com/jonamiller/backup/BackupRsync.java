package com.jonamiller.backup;

import com.github.fracpete.processoutput4j.output.ConsoleOutputProcessOutput;
import com.github.fracpete.rsync4j.RSync;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class BackupRsync {

  private static final String BACKUP_DRIVE = "F:/";
  private static final String BACKUP_FOLDER_NAME = "Backup";

  private static final String HOME_DIR = "C:/Users/Jon";
  private static final String HOME_DIR_BACKUP_FOLDER_NAME = "Home";
  private static final List<String> HOME_DIR_SUBPATHS =
      List.of(
          ".IdeaIC2019.3",
          "Archived",
          "Documents",
          "Music",
          "Pictures",
          "Programs",
          "Projects",
          "SSH Keys",
          "Ubiquiti UniFi",
          "Videos");

  private static final String SECOND_DRIVE = "E:/";
  private static final String SECOND_DRIVE_BACKUP_FOLDER_NAME = "Second";
  private static final List<String> SECOND_DRIVE_SUBPATHS =
      List.of(
          "backup.tc",
          "Ubuntu-SSN");

  public static void main(String[] args) {
    try {
      boolean delete = Arrays.asList(args).contains("--delete");
      BackupRsync backup = new BackupRsync();
      backup.execute(delete);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void execute(boolean delete) throws Exception {
    Path backupFolder = Paths.get(BACKUP_DRIVE, BACKUP_FOLDER_NAME);
    if (delete) {
      deleteDirectoryIfExists(backupFolder);
    }
    if (!Files.exists(backupFolder)) {
      Files.createDirectory(backupFolder);
    }

    Path homeDir = Paths.get(HOME_DIR);
    Path homeDirBackupFolder = backupFolder.resolve(HOME_DIR_BACKUP_FOLDER_NAME);
    if (!Files.exists(homeDirBackupFolder)) {
      Files.createDirectory(homeDirBackupFolder);
    }

    RSync rsync = new RSync()
        .archive(true)
        .sources(HOME_DIR_SUBPATHS.stream().map(
            (subpath) -> homeDir.resolve(subpath).toString()).collect(toList()))
        .destination(homeDirBackupFolder.toString());
    ConsoleOutputProcessOutput output = new ConsoleOutputProcessOutput();
    output.monitor(rsync.builder());

    Path secondDrive = Paths.get(SECOND_DRIVE);
    Path secondDriveBackupFolder = backupFolder.resolve(SECOND_DRIVE_BACKUP_FOLDER_NAME);
    if (!Files.exists(secondDriveBackupFolder)) {
      Files.createDirectory(secondDriveBackupFolder);
    }

    RSync secondRsync = new RSync()
        .archive(true)
        .sources(SECOND_DRIVE_SUBPATHS.stream().map(
            (subpath) -> secondDrive.resolve(subpath).toString()).collect(toList()))
        .destination(secondDriveBackupFolder.toString());
    ConsoleOutputProcessOutput secondOutput = new ConsoleOutputProcessOutput();
    secondOutput.monitor(secondRsync.builder());
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



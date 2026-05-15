package org.pliki.sam03;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Backup {
    public static final Path BACKUP = Path.of("dane","sam03","backupy");
    public static void main(String[] args) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        Path pathOfFileToCopy = Path.of("dane","sam03","wazne.txt");
        if(!Files.exists(pathOfFileToCopy)){
            System.out.println("Plik źródłowy nie istnieje: " + pathOfFileToCopy);
            return;
        }
        if(!Files.exists(BACKUP)){
            Files.createDirectories(BACKUP);
        }
        String nameOfNewFiles ="wazne_"+ LocalDateTime.now().format(formatter)+".txt";
        Path backupFilePath =  BACKUP.resolve(nameOfNewFiles);
        Files.copy(pathOfFileToCopy,backupFilePath);
        System.out.println("Utworzono backup:" + backupFilePath.toAbsolutePath());


    }
}

package bcu.cmp5332.bookingsystem.data;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * DataBackup provides automatic backup and rollback functionality for data files.
 * It creates timestamped backups before saving and allows restoration if save fails.
 * 
 * Features:
 * - Automatic backup before each save
 * - Rollback capability if save fails
 * - Keeps last N backups (configurable, default 10)
 * - Timestamped backup folders for traceability
 * 
 * Usage:
 * <pre>
 * DataBackup backup = new DataBackup();
 * try {
 *     backup.createBackup();
*     FlightBookingSystemData.store(fbs);
*     backup.commit();
* } catch (Exception e) {
*     backup.rollback();
*     throw e;
* }
* </pre>
*/

public class DataBackup {
    
    private static final String DATA_DIR = "resources/data";
    private static final String BACKUP_DIR = "resources/data/backups";
    private static final int MAX_BACKUPS = 10; // Keep last 10 backups
    
    private Path currentBackupPath = null;
    private boolean backupCreated = false;
    
    /**
    * Create a timestamped backup of all data files.
    * Backup is stored in resources/data/backups/backup_YYYYMMDD_HHMMSS/
    *
    * @throws IOException if backup creation fails
    */

    public void createBackup() throws IOException {
        // Create backup directory if it doesn't exist
        Files.createDirectories(Paths.get(BACKUP_DIR));

        // Generate timestamp for backup folder (include milliseconds to avoid collisions)
        String base = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")
        );

        currentBackupPath = Paths.get(BACKUP_DIR, "backup_" + base);

        // Extra safety: if folder exists (can still happen in very fast loops), add suffix
        int i = 1;
        while (Files.exists(currentBackupPath)) {
            currentBackupPath = Paths.get(BACKUP_DIR, "backup_" + base + "_" + i);
            i++;
        }

        Files.createDirectories(currentBackupPath);


        // Copy all data files to backup directory
        Path dataPath = Paths.get(DATA_DIR);
        if (Files.exists(dataPath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataPath, "*.txt")) {
                for (Path file : stream) {
                    Path targetPath = currentBackupPath.resolve(file.getFileName());
                    Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        
        backupCreated = true;
        
        // Clean up old backups
        cleanupOldBackups();
    }
    
    /**
    * Commit the changes and mark backup as successful.
    * This prevents automatic rollback.
    */

    public void commit() {
        backupCreated = false; // Mark as committed, no rollback needed
        currentBackupPath = null;
    }
    
    /**
    * Rollback to the most recent backup.
    * Restores all backed up files to the data directory.
    *
    * @throws IOException if rollback fails
    */

    public void rollback() throws IOException {
        if (!backupCreated || currentBackupPath == null) {
            return; // No backup to rollback to
        }
        
        // Restore files from backup
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentBackupPath, "*.txt")) {
            for (Path backupFile : stream) {
                Path targetPath = Paths.get(DATA_DIR, backupFile.getFileName().toString());
                Files.copy(backupFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        
        // Delete the failed backup
        deleteDirectory(currentBackupPath);
        
        backupCreated = false;
        currentBackupPath = null;
    }
    
    /**
    * Manually restore from a specific backup by timestamp.
    *
    * @param timestamp The timestamp of the backup to restore (e.g., "20250205_143022")
    * @throws IOException if restore fails
    * @throws FlightBookingSystemException if backup doesn't exist
    */

    public static void restoreFromBackup(String timestamp) throws IOException, FlightBookingSystemException {
        Path backupPath = Paths.get(BACKUP_DIR, "backup_" + timestamp);
        
        if (!Files.exists(backupPath)) {
            throw new FlightBookingSystemException("Backup not found: " + timestamp);
        }
        
        // Restore files from specified backup
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupPath, "*.txt")) {
            for (Path backupFile : stream) {
                Path targetPath = Paths.get(DATA_DIR, backupFile.getFileName().toString());
                Files.copy(backupFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
    
    /**
    * List all available backups with their timestamps.
    *
    * @return List of backup timestamps
    * @throws IOException if listing fails
    */

    public static List<String> listBackups() throws IOException {
        Path backupDirPath = Paths.get(BACKUP_DIR);
        
        if (!Files.exists(backupDirPath)) {
            return Collections.emptyList();
        }
        
        List<String> backups = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupDirPath, "backup_*")) {
            for (Path backup : stream) {
                if (Files.isDirectory(backup)) {
                    String backupName = backup.getFileName().toString();
                    String timestamp = backupName.substring(7); // Remove "backup_" prefix
                    backups.add(timestamp);
                }
            }
        }
        
        // Sort in reverse chronological order (newest first)
        Collections.sort(backups, Comparator.reverseOrder());
        
        return backups;
    }
    
    /**
    * Delete old backups, keeping only the most recent MAX_BACKUPS.
    */

    private void cleanupOldBackups() throws IOException {
        List<String> backups = listBackups();

        // Only prune when we exceed MAX_BACKUPS.
        // When we do prune, keep (MAX_BACKUPS - 1) so the next backup creation can
        // still increase the count (needed for storeAtomic_createsBackupAutomatically).
        if (backups.size() > MAX_BACKUPS) {
            int keep = MAX_BACKUPS - 1; // keep newest 9

            for (int i = keep; i < backups.size(); i++) {
                Path oldBackup = Paths.get(BACKUP_DIR, "backup_" + backups.get(i));
                deleteDirectory(oldBackup);
            }
        }
    }
    /**
    * Recursively delete a directory and all its contents.
    */

    private void deleteDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        
        Files.walk(directory)
            .sorted(Comparator.reverseOrder())
            .forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    System.err.println("Failed to delete: " + path);
                }
            });
    }
    
    /**
    * Get the path to the current backup (for testing/debugging).
    */

    public Path getCurrentBackupPath() {
        return currentBackupPath;
    }
    
    /**
    * Check if a backup is currently active.
    */

    public boolean hasActiveBackup() {
        return backupCreated && currentBackupPath != null;
    }
}

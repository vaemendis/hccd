package net.vaemendis.hccd;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileWatcher {

    private WatchService watchService;
    private Timer timer;
    private WatchedFiles watchedFiles;
    private UserConfiguration config;

    public FileWatcher() throws IOException {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkFiles();
                } catch (Exception e) {
                    ErrorDialog.show(null, e);
                }
            }
        }, 0, 500);
    }

    public void watch(File watched) throws IOException {
        resetWatchService();

        watchedFiles = new WatchedFiles(watched);
        Path watchedDir = watchedFiles.getParentDir().toPath();
        watchedDir.register(watchService, ENTRY_MODIFY);
        Hccd.log("Watching " + watchedFiles.toString());
        CardGenerator.generateCards(watchedFiles, config);
    }

    private void resetWatchService() throws IOException {
        if (watchService != null) {
            watchService.close();
        }
        watchService = FileSystems.getDefault().newWatchService();
    }


    private void checkFiles() throws IOException {
        if (watchService != null) {
            WatchKey key = watchService.poll();
            if (key != null) {
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    final WatchEvent.Kind<?> kind = watchEvent.kind();
                    if (StandardWatchEventKinds.ENTRY_MODIFY == kind &&
                            isWatched((Path)watchEvent.context())) {
                        Path p = (Path) watchEvent.context();
                        Hccd.log("File modified: " + p.getFileName().toString());
                        CardGenerator.generateCards(watchedFiles, config);
                    }
                }
                key.reset();
            }
        }
    }

    private boolean isWatched(Path path) {
        String fileName = path.getFileName().toString();
        return watchedFiles.getCsvFile().getName().equals(fileName) ||
                watchedFiles.getCssFile().getName().equals(fileName) ||
                watchedFiles.getHtmlFile().getName().equals(fileName);
    }

    public void setConfiguration(UserConfiguration config) {
        this.config = config;
    }

    public void generateCardSheet() throws IOException {
        if (watchedFiles != null) {
            CardGenerator.generateCards(watchedFiles, config);
        }
    }
}

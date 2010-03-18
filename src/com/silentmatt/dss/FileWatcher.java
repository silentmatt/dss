package com.silentmatt.dss;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks a list of files for changes.
 *
 * @author Matthew Crumley
 */
public class FileWatcher {
    private final List<File> files;
    private final List<Long> lastModifiedList;
    private final Set<File> primaryFiles;

    /**
     * Constructs a FileWatcher that watches a list of Files.
     *
     * The given files will be the primary files.
     *
     * @param files The {@link File} to be watched.
     */
    public FileWatcher(List<File> files) {
        this.files = new ArrayList<File>(files);
        this.lastModifiedList = new ArrayList<Long>(files.size());
        this.primaryFiles = new HashSet<File>(files);

        for (File f : files) {
            lastModifiedList.add(f.lastModified());
        }
    }

    /**
     * Adds a file to the list of files to watch.
     *
     * If the file is already being watched, this has no affect.
     *
     * @param file The file to watch.
     *
     * @return true if the file was added (was not already in the list).
     */
    public boolean addFile(File file) {
        for (File f : files) {
            if (f.equals(file)) {
                // Already in the list
                return false;
            }
        }
        files.add(file);
        lastModifiedList.add(file.lastModified());
        return true;
    }

    /**
     * Ignore any past changes to the specified file on the next iteration.
     *
     * If the file changes after you call ignoreChanges, but before the next call
     * to {@link #filesChanged()}, it <em>will</em> be included.
     *
     * If the file is not being watched, nothing changes.
     * 
     * @param file The file to ignore.
     */
    public void ignoreChanges(File file) {
        int index = files.indexOf(file);
        if (index != -1) {
            lastModifiedList.set(index, file.lastModified());
        }
    }

    /**
     * Gets the "primary files".
     *
     * @return The a copy of the set of primary files to watch.
     */
    public Set<File> getPrimaryFiles() {
        return new HashSet<File>(primaryFiles);
    }

    /**
     * Checks for any changed files.
     *
     * If a file other than the primary does not exist, it will be removed from
     * the file list.
     *
     * @return true if any of the files in the list have changed.
     */
    public boolean filesChanged() {
        boolean changed = false;

        for (int i = 0; i < files.size(); i++) {
            File newFile = files.get(i);

            long lastModified = newFile.lastModified();
            if (lastModified == 0) {
                if (!primaryFiles.contains(newFile)) {
                    int index = files.indexOf(newFile);
                    files.remove(index);
                    lastModifiedList.remove(index);
                }
                changed = true;
            }
            else if (lastModified > lastModifiedList.get(i)) {
                lastModifiedList.set(i, newFile.lastModified());
                changed = true;
            }
        }

        return changed;
    }
}

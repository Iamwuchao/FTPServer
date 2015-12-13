 package util;

public enum CopyOption {
	 /**
     * Replace an existing file if it exists.
     */
    REPLACE_EXISTING,
    /**
     * Copy attributes to the new file.
     */
    COPY_ATTRIBUTES,
    /**
     * Move the file as an atomic file system operation.
     */
    ATOMIC_MOVE;
}

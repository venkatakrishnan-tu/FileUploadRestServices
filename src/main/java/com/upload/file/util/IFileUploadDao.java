package com.upload.file.util;

import java.util.Date;
import java.util.List;

 

/**
 * Data access object to insert, find and load FileUploads
 * 
 * @author Poornima
 */
public interface IFileUploadDao {

    /**
     * Inserts a document in the data store.
     * 
     * @param file 
     */
    void insert(FileUpload file);
    
    /**
     * Find files in the data store matching the given parameter.
     * A list of file meta data is returned which does not include the file data.
     * Use load and the id from the meta data to get the file.
     * Returns an empty list if no file was found.
     * 
     * @param authorName The name of a author, may be null
     * @param date The date of a document, may be null
     * @return A list of file meta data
     */
    List<FileUploadMetadata> findByAuthorNameDate(String authorName, Date date);
    
    /**
     * Returns the file from the data store with the given id.
     * The file and meta data is returned.
     * Returns null if no file was found.
     * 
     * @param uuid The id of the document
     * @return File including file and meta data
     */
    FileUpload load(String uuid);
    
}

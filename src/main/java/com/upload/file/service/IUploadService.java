package com.upload.file.service;

import java.util.Date;
import java.util.List;

import com.upload.file.util.FileUpload;
import com.upload.file.util.FileUploadMetadata;

/**
 * A service to save, find and get files from an file system. 
 *  
 */
public interface IUploadService {
    
    /**
     * Saves a document in the archive.
     * 
     * @param document A document
     * @return FileUploadMetadata The meta data of the saved document
     */
    FileUploadMetadata save(FileUpload document);
    
    /**
     * Finds document in the archive matching the given parameter.
     * A list of document meta data which does not include the file data.
     * Use getFileUploadFile and the id from the meta data to get the file.
     * Returns an empty list if no document was found.
     * 
     * @param authorName The name of a person, may be null
     * @param date The date of a document, may be null
     * @return A list of document meta data
     */
    List<FileUploadMetadata> findFileUploads(String personName, Date date);
    
    
    /**
     * Returns the document file from the archive with the given id.
     * Returns null if no document was found.
     * 
     * @param id The id of a document
     * @return A document file
     */
    byte[] getFileUploadFile(String id);
}
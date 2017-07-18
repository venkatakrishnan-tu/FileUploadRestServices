package com.upload.file.util;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

/**
 * A document from the file system managed by IUploadService.
 * 
 * @author Poornima
 */
public class FileUpload extends FileUploadMetadata implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private byte[] fileData;
    
    public FileUpload( byte[] fileData, String fileName, Date documentDate, String authorName) {
        super(fileName, documentDate, authorName);
        this.fileData = fileData;
    }

    public FileUpload(Properties properties) {
        super(properties);
    }
    
    public FileUpload(FileUploadMetadata metadata) {
        super(metadata.getUuid(), metadata.getFileName(), metadata.getAuthorName(), metadata.getUploadDate());
    }

    public byte[] getFileData() {
        return fileData;
    }
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
    
    public FileUploadMetadata getMetadata() {
        return new FileUploadMetadata(getUuid(), getFileName(), getAuthorName(), getUploadDate());
    }
    
}

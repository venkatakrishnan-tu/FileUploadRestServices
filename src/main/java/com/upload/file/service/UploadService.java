package com.upload.file.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upload.file.util.FileUpload;
import com.upload.file.util.FileUploadMetadata;
import com.upload.file.util.IFileUploadDao;

/**
 * A service to save, find and get files from a file system. 
 * 
 * @author Poornima
 */
@Service("uploadService")
public class UploadService implements IUploadService, Serializable {

    private static final long serialVersionUID = 3L;
    
    @Autowired
    private IFileUploadDao FileUploadDao;

    /**
     * Saves a document in the file system.
     */
    @Override
    public FileUploadMetadata save(FileUpload document) {
        getFileUploadDao().insert(document); 
        return document.getMetadata();
    }
    
    /**
     * Finds document in the file system
     */
    @Override
    public List<FileUploadMetadata> findFileUploads(String authorName, Date date) {
        return getFileUploadDao().findByAuthorNameDate(authorName, date);
    }
    
    /**
     * Returns the document file from the file system
     */
    @Override
    public byte[] getFileUploadFile(String id) {
        FileUpload document = getFileUploadDao().load(id);
        if(document!=null) {
            return document.getFileData();
        } else {
            return null;
        }
    }


    public IFileUploadDao getFileUploadDao() {
        return FileUploadDao;
    }

    public void setFileUploadDao(IFileUploadDao documentDao) {
        FileUploadDao = documentDao;
    }


}

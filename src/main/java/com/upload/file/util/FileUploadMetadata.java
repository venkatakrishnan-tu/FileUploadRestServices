package com.upload.file.util;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * Meta data of a document from an archive managed by IUploadService.
 * 
 * @author Poornima
 */
public class FileUploadMetadata implements Serializable {
    
    static final long serialVersionUID = 2L;

    private static final Logger LOG = Logger.getLogger(FileUploadMetadata.class);
    
    public static final String PROP_UUID = "uuid";
    public static final String PROP_AUTHOR = "author-name";
    public static final String PROP_FILE_NAME = "file-name";
    public static final String PROP_UPLOAD_DATE = "document-date";
    
    public static final String DATE_FORMAT_PATTERN = "mm-dd-yyyy";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    
    protected String uuid;
    protected String fileName;
    protected String authorName;
    protected Date uploadDate;

    
    public FileUploadMetadata() {
        super();
    }

    public FileUploadMetadata(String fileName, Date uploadDate, String authorName) {
        this(UUID.randomUUID().toString(), fileName, authorName,uploadDate);
    }
    
    public FileUploadMetadata(String uuid, String fileName, String authorName, Date uploadDate) {
        super();
        this.uuid = uuid;
        this.fileName = fileName;
        this.uploadDate = uploadDate;
        this.authorName = authorName;
    }
    
    public FileUploadMetadata(Properties properties) {
        this(properties.getProperty(PROP_UUID),
             properties.getProperty(PROP_FILE_NAME),
             properties.getProperty(PROP_AUTHOR),
             null);
        String dateString = properties.getProperty(PROP_UPLOAD_DATE);
        if(dateString!=null) {
            try {
                this.uploadDate = DATE_FORMAT.parse(dateString);
            } catch (ParseException e) {
                LOG.error("Error while parsing date string: " + dateString + ", format is: mm-dd-yyyy" , e);
            }
        }    
    }

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public Date getUploadDate() {
        return uploadDate;
    }
    public void setuploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public Properties createProperties() {
        Properties props = new Properties();
        props.setProperty(PROP_UUID, getUuid());
        props.setProperty(PROP_FILE_NAME, getFileName());
        props.setProperty(PROP_AUTHOR, getAuthorName());
        props.setProperty(PROP_UPLOAD_DATE, DATE_FORMAT.format(getUploadDate()));
        return props;
    }
    
    
}

package com.upload.file.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
 
import org.springframework.stereotype.Service;

/**
 * Data access object to insert, find and load files.
 * 
 * FileSystemDocumentDao saves files in the file system.  
 * For each file a folder is created. The folder contains the file
 * and a properties files with the meta data of the file.
 * Each file in the file system has a Universally Unique Identifier (UUID).
 * The name of the files folder is the UUID of the file.
 * 
 * @author Poornima
 */
@Service("fileDao")
public class FileSystemDocumentDao implements IFileUploadDao {

    private static final Logger LOG = Logger.getLogger(FileSystemDocumentDao.class);
    
    public static final String DIRECTORY = "uploads";
    public static final String META_DATA_FILE_NAME = "metadata.properties";
    
    @PostConstruct
    public void init() {
        createDirectory(DIRECTORY);
    }
    
    /**
     * Inserts a file to the file system by creating a folder with the UUID
     * of the file. In the folder the file is saved and a properties file
     * with the meta data of the file. 
     * 
     */
    @Override
    public void insert(FileUpload file) {
        try {
            createDirectory(file);
            saveFileData(file);
            saveMetaData(file);
        } catch (IOException e) {
            String message = "Error while inserting file";
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }
    
    /**
     * Find files in the data store matching the given parameter.
     * To find a file all file meta data sets are iterated to check if they match
     * the parameter.
     * 
     */
    @Override
    public List<FileUploadMetadata> findByAuthorNameDate(String personName, Date date) {
        try {
            return findInFileSystem(personName,date);
        } catch (IOException e) {
            String message = "Error while finding file, person name: " + personName + ", date:" + date;
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }
    
    /**
     * Returns the file from the data store with the given UUID.
     * 
     */
    @Override
    public FileUpload load(String uuid) {
        try {
            return loadFromFileSystem(uuid);
        } catch (IOException e) {
            String message = "Error while loading file with id: " + uuid;
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
        
    }
    

    private List<FileUploadMetadata> findInFileSystem(String personName, Date date) throws IOException  {
        List<String> uuidList = getUuidList();
        List<FileUploadMetadata> metadataList = new ArrayList<FileUploadMetadata>(uuidList.size());
        for (String uuid : uuidList) {
            FileUploadMetadata metadata = loadMetadataFromFileSystem(uuid);
            if(isMatched(metadata, personName, date)) {
                metadataList.add(metadata);
            }
        }
        return metadataList;
    }

    private boolean isMatched(FileUploadMetadata metadata, String authorName, Date date) {
        if(metadata==null) {
            return false;
        }
        boolean match = true;
        if(authorName!=null) {
            match = (authorName.equals(metadata.getAuthorName()));
        }
        if(date!=null) {
            match = (date.equals(metadata.getUploadDate()));
        }
        if(authorName!=null && date!=null) {
            match = (authorName.equals(metadata.getAuthorName()) && date.equals(metadata.getUploadDate()));
        }
        return match;
    }

    private FileUploadMetadata loadMetadataFromFileSystem(String uuid) throws IOException {
        FileUploadMetadata file = null;
        String dirPath = getDirectoryPath(uuid);
        File file1 = new File(dirPath);
        if(file1.exists()) {
            Properties properties = readProperties(uuid);
            file = new FileUploadMetadata(properties);
        } 
        return file;
    }
    
    private FileUpload loadFromFileSystem(String uuid) throws IOException {
       FileUploadMetadata metadata = loadMetadataFromFileSystem(uuid);
       if(metadata==null) {
           return null;
       }
       Path path = Paths.get(getFilePath(metadata));
       FileUpload file = new FileUpload(metadata);
       file.setFileData(Files.readAllBytes(path));
       return file;
    }

    private String getFilePath(FileUploadMetadata metadata) {
        String dirPath = getDirectoryPath(metadata.getUuid());
        StringBuilder sb = new StringBuilder();
        sb.append(dirPath).append(File.separator).append(metadata.getFileName());
        return sb.toString();
    }
    
    private void saveFileData(FileUpload file) throws IOException {
        String path = getDirectoryPath(file);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(new File(path), file.getFileName())));
        stream.write(file.getFileData());
        stream.close();
    }
    
    public void saveMetaData(FileUpload file) throws IOException {
            String path = getDirectoryPath(file);
            Properties props = file.createProperties();
            File f = new File(new File(path), META_DATA_FILE_NAME);
            OutputStream out = new FileOutputStream( f );
            props.store(out, "FileUpload meta data");       
    }
    
    private List<String> getUuidList() {
        File file = new File(DIRECTORY);
        String[] directories = file.list(new FilenameFilter() {
          @Override
          public boolean accept(File current, String name) {
            return new File(current, name).isDirectory();
          }
        });
        return Arrays.asList(directories);
    }
    
    private Properties readProperties(String uuid) throws IOException {
        Properties prop = new Properties();
        InputStream input = null;     
        try {
            input = new FileInputStream(new File(getDirectoryPath(uuid),META_DATA_FILE_NAME));
            prop.load(input);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }
    
    private String createDirectory(FileUpload file) {
        String path = getDirectoryPath(file);
        createDirectory(path);
        return path;
    }

    private String getDirectoryPath(FileUpload file) {
       return getDirectoryPath(file.getUuid());
    }
    
    private String getDirectoryPath(String uuid) {
        StringBuilder sb = new StringBuilder();
        sb.append(DIRECTORY).append(File.separator).append(uuid);
        String path = sb.toString();
        return path;
    }

    private void createDirectory(String path) {
        File file = new File(path);
        file.mkdirs();
    }

}

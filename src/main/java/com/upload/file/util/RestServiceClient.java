package com.upload.file.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.upload.file.service.IUploadService;

/**
 * @author Poornima
 *
 */
public class RestServiceClient implements IUploadService {

    private static final Logger LOG = Logger.getLogger(RestServiceClient.class);
    
    String protocol = "http";
    String hostname = "localhost";
    Integer port = 8080;
    String baseUrl = "rest";
    
    RestTemplate restTemplate;
    
    @Override
    public FileUploadMetadata save(FileUpload document) {
        try {          
            return doSave(document);
        } catch (RuntimeException e) {
            LOG.error("Error while uploading file", e);
            throw e;
        } catch (IOException e) {
            LOG.error("Error while uploading file", e);
            throw new RuntimeException("Error while uploading file", e);
        }

    }

    private FileUploadMetadata doSave(FileUpload document) throws IOException, FileNotFoundException {
        String tempFilePath = writeFileUploadToTempFile(document);
        MultiValueMap<String, Object> parts = createMultipartFileParam(tempFilePath);
        String dateString = FileUploadMetadata.DATE_FORMAT.format(document.getFileData());
        FileUploadMetadata fileUploadMetadata = getRestTemplate().postForObject(getServiceUrl() + "/upload?author={name}&date={date}", 
                parts, 
                FileUploadMetadata.class,
                document.getAuthorName(), 
                dateString);
        return fileUploadMetadata;
    }

    @Override
    public byte[] getFileUploadFile(String id) {
        return getRestTemplate().getForObject(getServiceUrl() +  "/file/{id}", byte[].class, id);
    }

    @Override
    public List<FileUploadMetadata> findFileUploads(String authorName, Date date) {
        String dateString = null;
        if(date!=null) {           
            dateString = FileUploadMetadata.DATE_FORMAT.format(date);
        }
        FileUploadMetadata[] result = getRestTemplate().getForObject(getServiceUrl() +  "files?author={name}&date={date}", FileUploadMetadata[].class, authorName, dateString);
        return Arrays.asList(result);
    }
    
    private MultiValueMap<String, Object> createMultipartFileParam(String tempFilePath) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();           
        parts.add("file", new FileSystemResource(tempFilePath));
        return parts;
    }

    private String writeFileUploadToTempFile(FileUpload document) throws IOException, FileNotFoundException {
        Path path;       
        path = Files.createTempDirectory(document.getUuid());  
        String tempDirPath = path.toString();
        File file = new File(tempDirPath,document.getFileName());
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(document.getFileData());    
        fo.close();
        return file.getPath();
    }
    
    public String getServiceUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(getProtocol()).append("://");
        sb.append(getHostname());
        if(getPort()!=null) {
            sb.append(":").append(getPort());
        }
        sb.append("/").append(getBaseUrl()).append("/");
        return sb.toString();
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public RestTemplate getRestTemplate() {
        if(restTemplate==null) {
            restTemplate = createRestTemplate(); 
        }
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private RestTemplate createRestTemplate() {
        restTemplate = new RestTemplate();
        return restTemplate;
    }

}

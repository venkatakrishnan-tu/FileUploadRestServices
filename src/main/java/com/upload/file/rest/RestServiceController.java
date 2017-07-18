package com.upload.file.rest;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.upload.file.service.IUploadService;
import com.upload.file.util.FileUpload;
import com.upload.file.util.FileUploadMetadata;

@Controller
@RequestMapping(value = "/rest")
public class RestServiceController {

	
private static final Logger LOG = Logger.getLogger(RestServiceController.class);
    
    @Autowired
    IUploadService uploadService;

    /**
     * Adds a file to the file system.
     * 
     * Url: /rest/upload?file={file}&author={author}&date={date} [POST]
     * 
     * @param file A file posted in a multipart request
     * @param author The name of the uploading author
     * @param date The date of the file
     * @return The meta data of the added file
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody FileUploadMetadata handleFileUpload(
            @RequestParam(value="file", required=true) MultipartFile fileReceived ,
            @RequestParam(value="author", required=false) String author,
            @RequestParam(value="date", required=false) @DateTimeFormat(pattern="mm-dd-yyyy") Date date) {
        
        try {
            FileUpload file = new FileUpload(fileReceived.getBytes(), fileReceived.getOriginalFilename(), date, author );
            getUploadService().save(file);
            return file.getMetadata();
        } catch (RuntimeException e) {
            LOG.error("Error while uploading.", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Error while uploading.", e);
            throw new RuntimeException(e);
        }      
    }
    
    /**
     * Finds file in the file system. Returns a list of files meta data 
     * which does not include the file data. Use getFileUpload to get the file.
     * Returns an empty list if no file was found.
     * 
     * Url: /rest/uploads?author={author}&date={date} [GET]
     * 
     * @param author The name of the uploading author
     * @param date The date of the file
     * @return A list of file meta data
     */
    @RequestMapping(value = "/files", method = RequestMethod.GET)
    public HttpEntity<List<FileUploadMetadata>> findFileUpload(
            @RequestParam(value="author", required=false) String author,
            @RequestParam(value="date", required=false) @DateTimeFormat(pattern="mm-dd-yyyy") Date date) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<List<FileUploadMetadata>>(getUploadService().findFileUploads(author,date), httpHeaders,HttpStatus.OK);
    }
    
    
    /**
     * Returns the file from the file system with the given UUID.
     * 
     * Url:  [GET]
     * 
     * @param id The UUID of a file
     * @return The file
     */
    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public HttpEntity<byte[]> getFileUpload(@RequestParam(value="id", required=true) String id) {         
        // send it back to the client
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<byte[]>(getUploadService().getFileUploadFile(id), httpHeaders,HttpStatus.OK);
        
    }

 
    /**
     * Returns the document file from the archive with the given UUID.
     * 
     * Url: /archive/document/{id} [GET]
     * 
     * @param id The UUID of a document
     * @return The document file
     */
    @RequestMapping(value = "/file/{id}", method = RequestMethod.GET)
    public HttpEntity<byte[]> getDocument(@PathVariable String id) {         
        // send it back to the client
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<byte[]>(getUploadService().getFileUploadFile(id), httpHeaders, HttpStatus.OK);
    }
   
    	
    public IUploadService getUploadService() {
        return uploadService;
    }

    public void setUploadService(IUploadService uploadService) {
        this.uploadService = uploadService;
    }

}

package com.example.TaskManagement.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {
    public String upoadFile(String path, MultipartFile file) throws IOException {
        String fileName=file.getOriginalFilename();
        if(fileName==null||fileName.isEmpty()){
            throw new FileNotFoundException("no file");
        }
        String cleanFileName=fileName.replaceAll("[^a-z-A-Z0-9\\.\\-_]","_");
        String uniqueName= UUID.randomUUID()+"_"+cleanFileName;
        String filePath=path + File.separator+uniqueName;
        File savePath=new File(path);
        if(!savePath.exists()){
            savePath.mkdirs();
        }
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        return uniqueName;
    }

    public InputStream getResource(String path,String fileName)throws FileNotFoundException{
        String filePath=path+File.separator+fileName;
        return new FileInputStream(filePath);
    }
}

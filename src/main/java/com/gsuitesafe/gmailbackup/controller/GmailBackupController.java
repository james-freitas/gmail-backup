package com.gsuitesafe.gmailbackup.controller;

import com.gsuitesafe.gmailbackup.dto.CreatedBackupResponse;
import com.gsuitesafe.gmailbackup.dto.InitiatedBackupResponse;
import com.gsuitesafe.gmailbackup.service.GmailBackupService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class GmailBackupController {

    private final GmailBackupService service;

    public GmailBackupController(GmailBackupService service) {
        this.service = service;
    }

    @PostMapping("/backups")
    public ResponseEntity<CreatedBackupResponse> createBackup() {
        final CreatedBackupResponse backupResponse = service.createGmailBackup();
        return new ResponseEntity<>(backupResponse, HttpStatus.OK);
    }

    @GetMapping("/backups")
    public ResponseEntity<List<InitiatedBackupResponse>> listInitiatedBackups() {
        List<InitiatedBackupResponse> list = service.getInitiatedGmailBackupList();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/exports/{backupId}", produces="application/zip")
    public void downloadFullBackup(
            @PathVariable String backupId,
            HttpServletResponse response
    ) throws IOException {

        // Setting headers
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"test.zip\"");

        generateZipFile(response);
    }

    @GetMapping(value = "/exports/{backupId}/{label}", produces="application/zip")
    public void downloadBackupByLabel(
            @PathVariable String backupId,
            @PathVariable String label,
            HttpServletResponse response
    ) throws IOException {

        // Setting headers
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"test.zip\"");

        generateZipFile(response);
    }

    private void generateZipFile(HttpServletResponse response) throws IOException {
        // create a list to add files to be zipped
        ArrayList<File> files = new ArrayList<>(2);
        files.add(new File("README.md"));

        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

        // package files
        for (File file : files) {
            //new zip entry and copying inputstream with file to zipOutputStream, after all closing streams
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fileInputStream = new FileInputStream(file);

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
            zipOutputStream.closeEntry();
        }

        zipOutputStream.close();
    }
}

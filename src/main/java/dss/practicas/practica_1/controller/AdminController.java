package dss.practicas.practica_1.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dss.practicas.practica_1.service.DatabaseExportService;

@Controller
@RequestMapping ("/admin")
public class AdminController {

    private final DatabaseExportService exportService;

    public AdminController(DatabaseExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping
    public String adminPanel() {
        return "admin";
    }

    @GetMapping ("/export")
    public ResponseEntity<byte[]> exportSQL() {
        byte[] sqlData = exportService.exportDatabaseToSql();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"products.sql\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(sqlData);
    }
    
}

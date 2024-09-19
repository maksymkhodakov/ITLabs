package mo.khodakov.webs.mvc;

import lombok.extern.slf4j.Slf4j;
import mo.khodakov.gui.database.Database;
import mo.khodakov.gui.database.DatabaseReader;
import mo.khodakov.gui.database.Result;
import mo.khodakov.webs.rest.exceptions.ApiException;
import mo.khodakov.webs.rest.exceptions.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static mo.khodakov.webs.rest.api.RestDatabaseController.DOWNLOAD_DEFAULT_FILENAME;

@Slf4j
@Controller
public class DatabaseController {
    private Database database;

    private Path tempDir;

    public DatabaseController() {
        try {
            // Initialize temporary directory
            tempDir = Files.createTempDirectory("tempUploads");
            log.info("Temporary directory created at: " + tempDir.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @GetMapping("/")
    public String queryForm(@RequestParam(name = "name", required = false, defaultValue = "guest") String name,
                            Model model) {
        model.addAttribute("name", name);
        return "database";
    }

    @GetMapping("/query")
    public String queryContent(@RequestParam(name = "name", required = false, defaultValue = "guest") String name,
                               Model model) {
        model.addAttribute("name", name);
        return "queries";
    }

    @PostMapping(value = "/database", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Result query(@RequestParam(name = "query") String query) {
        log.info(query);
        return database.query(query);
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }
        try {
            // Generate a temporary file location
            Path tempFilePath = Files.createTempFile(tempDir, "uploaded_", "_" + file.getOriginalFilename());

            // Save the file in the temporary directory
            Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("File uploaded to: " + tempFilePath);
            this.database = new DatabaseReader(tempFilePath.toFile().getAbsolutePath()).read();
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully: " + tempFilePath.toString());
        } catch (IOException e) {
            log.error("File upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    @PostMapping("/downloadFile")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("fileName") String fileName) throws IOException {
        if (fileName == null || fileName.equalsIgnoreCase("null")) {
            if (database == null) {
                throw new ApiException(ErrorCode.NO_ACTIVE_DATABASE);
            }
            return ResponseEntity.ok()
                    .header("content-disposition", "attachment; filename=" + DOWNLOAD_DEFAULT_FILENAME)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(database.download());
        } else {
            Path file = tempDir.resolve(fileName);

            if (!Files.exists(file)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            try (InputStream in = new FileInputStream(file.toFile())) {
                byte[] fileContent = in.readAllBytes();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", fileName);

                return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
            }
        }
    }
}

package mo.khodakov.webs.mvc;

import lombok.extern.slf4j.Slf4j;
import mo.khodakov.gui.database.Database;
import mo.khodakov.gui.database.DatabaseReader;
import mo.khodakov.gui.database.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Controller
public class DatabaseController {
    private Database database;

    private Path tempDir;

    public DatabaseController() {
        try {
            database = new DatabaseReader("/Users/maksymkhodakov/Downloads/KhodakovITLab/webs/src/main/resources/init.json").read();
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

            log.info("File uploaded to: " + tempFilePath.toString());
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully: " + tempFilePath.toString());
        } catch (IOException e) {
            log.error("File upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    @PostMapping("/downloadFile")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("fileName") String fileName) throws IOException {
        Path file = tempDir.resolve(fileName);

        if (!Files.exists(file)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        InputStream in = new FileInputStream(file.toFile());
        byte[] fileContent = in.readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
}

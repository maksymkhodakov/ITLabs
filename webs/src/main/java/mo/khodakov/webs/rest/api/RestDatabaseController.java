package mo.khodakov.webs.rest.api;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import mo.khodakov.gui.database.Database;
import mo.khodakov.gui.database.DatabaseReader;
import mo.khodakov.webs.rest.exceptions.ApiException;
import mo.khodakov.webs.rest.exceptions.ErrorCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/api")
public class RestDatabaseController {
    public static final String DOWNLOAD_DEFAULT_FILENAME = "DB.json";
    private Database database;

    @PostMapping("/database/create")
    @Operation(description = "Method for initial create DB upload")
    public ResponseEntity<Void> createDatabase(@RequestParam String path) {
        if (path == null) {
            throw new ApiException(ErrorCode.PATH_NOT_FOUND);
        }

        if (!path.endsWith(".json")) {
            path += ".json";
        }

        File newDatabaseFile = new File(path);

        if (!newDatabaseFile.exists()) {
            try {
                newDatabaseFile.createNewFile();
                // Ініціалізація нової бази даних та запис її в файл.
                this.database = new Database(path);
                this.database.save();
                this.database = new DatabaseReader(path).read();
                return ResponseEntity.ok().build();
            } catch (IOException e) {
                throw new ApiException(ErrorCode.FILE_CREATION_FAILED);
            }
        } else {
            throw new ApiException(ErrorCode.FILE_ALREADY_EXISTS);
        }
    }

    @PostMapping("/database/upload")
    @Operation(description = "Method for initial existing DB upload")
    public ResponseEntity<Collection<String>> uploadDatabase(@RequestParam String path) {
        try {
            database = new DatabaseReader(path).read();
        } catch (FileNotFoundException e) {
            throw new ApiException(ErrorCode.FILE_NOT_FOUND);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new ApiException(ErrorCode.INVALID_JSON);
        }
        return ResponseEntity.ok(database.getTableNames());
    }

    @PostMapping("/database/download")
    @Operation(description = "Method for downloading database state")
    public ResponseEntity<byte[]> downloadDatabase() throws ApiException {
        if (database == null) {
            throw new ApiException(ErrorCode.NO_ACTIVE_DATABASE);
        }
        return ResponseEntity.ok()
                .header("content-disposition", "attachment; filename=" + DOWNLOAD_DEFAULT_FILENAME)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(database.download());
    }
}

package mo.khodakov.webs.rest.api;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import io.swagger.v3.oas.annotations.Operation;
import mo.khodakov.gui.database.Database;
import mo.khodakov.gui.database.DatabaseReader;
import mo.khodakov.webs.rest.exceptions.ApiException;
import mo.khodakov.webs.rest.exceptions.ErrorCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.Collection;


@RestController
@RequestMapping("/api")
public class RestDatabaseController {
    public static final String DOWNLOAD_DEFAULT_FILENAME = "DB.json";
    private Database database;

    @PostMapping("/database/upload")
    @Operation(description = "Method for initial DB upload")
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

package mo.khodakov.webs.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import mo.khodakov.gui.database.Database;
import mo.khodakov.gui.database.DatabaseReader;
import mo.khodakov.gui.database.Result;
import mo.khodakov.webs.rest.exceptions.ApiException;
import mo.khodakov.webs.rest.exceptions.ErrorCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequestMapping("/api")
public class DatabaseController {
    public static final String DEFAULT_SCHEMA = "init.json";
    public static final String DOWNLOAD_FILENAME = "DB.json";
    private Database database;

    @PostMapping("/database/upload")
    @Operation(description = "Method for initial DB upload")
    public ResponseEntity<Collection<String>> uploadDatabase(@RequestParam String path) {
        try {
            database = new DatabaseReader(path).read();
        } catch (Exception e) {
            e.printStackTrace();
            database = new Database(DEFAULT_SCHEMA);
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
                .header("content-disposition", "attachment; filename=" + DOWNLOAD_FILENAME)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(database.download());
    }

    @GetMapping(value = "/database/tables")
    public ResponseEntity<Result> tables() throws ApiException {
        if (database == null) {
            throw new ApiException(ErrorCode.NO_ACTIVE_DATABASE);
        }
        return ResponseEntity.ok(database.query("list tables"));
    }

    @DeleteMapping(value = "/database/tables/delete/{tableName}")
    public ResponseEntity<Result> dropTable(@PathVariable String tableName) throws ApiException {
        if (database == null) {
            throw new ApiException(ErrorCode.NO_ACTIVE_DATABASE);
        }
        return ResponseEntity.ok(database.query(String.format("remove table %s", tableName)));
    }

    @PostMapping(value = "/database/tables/create/{tableName}/{columns}")
    public ResponseEntity<Result> createTable(@PathVariable String columns,
                                              @PathVariable String tableName) throws ApiException {
        if (database == null) {
            throw new ApiException(ErrorCode.NO_ACTIVE_DATABASE);
        }
        return ResponseEntity.ok(database.query(String.format("create table %s (%s)", tableName, columns)));
    }

    @GetMapping(value = "/database/{tableName}/select/{columns}/{condition}")
    public ResponseEntity<Result> selectCondition(@PathVariable String columns,
                                                  @PathVariable String tableName,
                                                  @PathVariable String condition) throws ApiException {
        if (database == null) {
            throw new ApiException(ErrorCode.NO_ACTIVE_DATABASE);
        }
        return ResponseEntity.ok(database.query(String.format("select %s from %s where %s", columns, tableName, condition)));
    }

    @GetMapping(value = "/database/{tableName}/select/{columns}")
    public ResponseEntity<Result> select(@PathVariable String columns,
                                         @PathVariable String tableName) throws ApiException {
        if (database == null) {
            throw new ApiException(ErrorCode.NO_ACTIVE_DATABASE);
        }
        return ResponseEntity.ok(database.query(String.format("select %s from %s", columns, tableName)));
    }

    @PostMapping(value = "/database/{tableName}/insert/{columns}/{values}")
    public ResponseEntity<Result> insert(@PathVariable String columns,
                                         @PathVariable String tableName,
                                         @PathVariable String values) throws ApiException {
        if (database == null) {
            throw new ApiException(ErrorCode.NO_ACTIVE_DATABASE);
        }
        return ResponseEntity.ok(database.query(String.format("insert into %s (%s) values (%s)", tableName, columns, values)));
    }

    @DeleteMapping(value = "/database/{tableName}/delete/{condition}")
    public ResponseEntity<Result> delete(@PathVariable String tableName,
                                         @PathVariable String condition) throws ApiException {
        if (database == null) {
            throw new ApiException(ErrorCode.NO_ACTIVE_DATABASE);
        }
        return ResponseEntity.ok(database.query(String.format("delete from %s where %s", tableName, condition)));
    }

    @GetMapping(value = "/database/{tableLeftName}/combine/{tableRightName}")
    public ResponseEntity<Result> combine(@PathVariable String tableLeftName,
                                          @PathVariable String tableRightName) throws ApiException {
        if (database == null) {
            throw new ApiException(ErrorCode.NO_ACTIVE_DATABASE);
        }
        return ResponseEntity.ok(database.query(String.format("combine %s with %s", tableLeftName, tableRightName)));
    }


    @GetMapping(value = "/database/{tableLeftName}/subtract/{tableRightName}")
    public ResponseEntity<Result> subtract(@PathVariable String tableLeftName,
                                           @PathVariable String tableRightName) throws ApiException {
        if (database == null) {
            throw new ApiException(ErrorCode.NO_ACTIVE_DATABASE);
        }
        return ResponseEntity.ok(database.query(String.format("subtract %s from %s", tableLeftName, tableRightName)));
    }
}

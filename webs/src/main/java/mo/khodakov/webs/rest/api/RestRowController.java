package mo.khodakov.webs.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import mo.khodakov.gui.database.Result;
import mo.khodakov.webs.rest.service.DatabaseService;
import mo.khodakov.webs.rest.exceptions.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RestRowController {
    private final DatabaseService databaseService;

    @GetMapping(value = "/rows/{tableName}/select/{columns}/{condition}")
    @Operation(description = "Select by conditions from DB")
    public ResponseEntity<Result> selectCondition(@PathVariable String columns,
                                                  @PathVariable String tableName,
                                                  @PathVariable String condition) throws ApiException {
        final var database = databaseService.getDatabase();
        return ResponseEntity.ok(database.query(String.format("select %s from %s where %s", columns, tableName, condition)));
    }

    @GetMapping(value = "/rows/{tableName}/select/{columns}")
    @Operation(description = "Select from DB without conditions")
    public ResponseEntity<Result> select(@PathVariable String columns,
                                         @PathVariable String tableName) throws ApiException {
        final var database = databaseService.getDatabase();
        return ResponseEntity.ok(database.query(String.format("select %s from %s", columns, tableName)));
    }

    @PostMapping(value = "/rows/{tableName}/insert/{columns}/{values}")
    @Operation(description = "Insert into table")
    public ResponseEntity<Result> insert(@PathVariable String columns,
                                         @PathVariable String tableName,
                                         @PathVariable String values) throws ApiException {
        final var database = databaseService.getDatabase();
        return ResponseEntity.ok(database.query(String.format("insert into %s (%s) values (%s)", tableName, columns, values)));
    }

    @DeleteMapping(value = "/rows/{tableName}/delete/{condition}")
    @Operation(description = "Delete from DB by condition")
    public ResponseEntity<Result> delete(@PathVariable String tableName,
                                         @PathVariable String condition) throws ApiException {
        final var database = databaseService.getDatabase();
        return ResponseEntity.ok(database.query(String.format("delete from %s where %s", tableName, condition)));
    }
}

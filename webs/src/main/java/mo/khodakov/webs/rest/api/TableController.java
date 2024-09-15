package mo.khodakov.webs.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import mo.khodakov.gui.database.Result;
import mo.khodakov.webs.rest.api.service.DatabaseService;
import mo.khodakov.webs.rest.exceptions.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TableController {
    private final DatabaseService databaseService;

    @GetMapping(value = "/tables")
    @Operation(description = "Retrieve tables from DB")
    public ResponseEntity<Result> tables() throws ApiException {
        final var database = databaseService.getDatabase();
        return ResponseEntity.ok(database.query("list tables"));
    }

    @DeleteMapping(value = "/tables/delete/{tableName}")
    @Operation(description = "Drop table in DB")
    public ResponseEntity<Result> dropTable(@PathVariable String tableName) throws ApiException {
        final var database = databaseService.getDatabase();
        return ResponseEntity.ok(database.query(String.format("remove table %s", tableName)));
    }

    @PostMapping(value = "/tables/create/{tableName}/{columns}")
    @Operation(description = "Create table in DB")
    public ResponseEntity<Result> createTable(@PathVariable String columns,
                                              @PathVariable String tableName) throws ApiException {
        final var database = databaseService.getDatabase();
        return ResponseEntity.ok(database.query(String.format("create table %s (%s)", tableName, columns)));
    }

    @PostMapping(value = "/tables/{tableLeftName}/combine/{tableRightName}")
    @Operation(description = "Combine tables in DB")
    public ResponseEntity<Result> combine(@PathVariable String tableLeftName,
                                          @PathVariable String tableRightName) throws ApiException {
        final var database = databaseService.getDatabase();
        return ResponseEntity.ok(database.query(String.format("combine %s with %s", tableLeftName, tableRightName)));
    }


    @PostMapping(value = "/tables/{tableLeftName}/subtract/{tableRightName}")
    @Operation(description = "Subtract tables in DB")
    public ResponseEntity<Result> subtract(@PathVariable String tableLeftName,
                                           @PathVariable String tableRightName) throws ApiException {
        final var database = databaseService.getDatabase();
        return ResponseEntity.ok(database.query(String.format("subtract %s from %s", tableLeftName, tableRightName)));
    }
}

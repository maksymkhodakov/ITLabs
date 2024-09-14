package mo.khodakov.webs.rest;

import mo.khodakov.gui.database.Database;
import mo.khodakov.gui.database.DatabaseReader;
import mo.khodakov.gui.database.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class DatabaseController {

    private Database database;

    public DatabaseController() {
        try {
            database = new DatabaseReader("/Users/maksymkhodakov/Downloads/KhodakovITLab/webs/src/main/resources/init.json").read();
        } catch (Exception e) {
            database = new Database("init.json");
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/database/tables")
    public ResponseEntity<Result> tables() {
        return ResponseEntity.ok(database.query("list tables"));
    }

    @DeleteMapping(value = "/database/tables/delete/{tableName}")
    public ResponseEntity<Result> dropTable(@PathVariable String tableName) {
        return ResponseEntity.ok(database.query(String.format("remove table %s", tableName)));
    }

    @PostMapping(value = "/database/tables/create/{tableName}/{columns}")
    public ResponseEntity<Result> createTable(@PathVariable String columns,
                                              @PathVariable String tableName) {
        return ResponseEntity.ok(database.query(String.format("create table %s (%s)", tableName, columns)));
    }

    @GetMapping(value = "/database/{tableName}/select/{columns}/{condition}")
    public ResponseEntity<Result> selectCondition(@PathVariable String columns,
                                                  @PathVariable String tableName,
                                                  @PathVariable String condition) {
        return ResponseEntity.ok(database.query(String.format("select %s from %s where %s", columns, tableName, condition)));
    }

    @GetMapping(value = "/database/{tableName}/select/{columns}")
    public ResponseEntity<Result> select(@PathVariable String columns,
                                         @PathVariable String tableName) {
        return ResponseEntity.ok(database.query(String.format("select %s from %s", columns, tableName)));
    }

    @PostMapping(value = "/database/{tableName}/insert/{columns}/{values}")
    public ResponseEntity<Result> insert(@PathVariable String columns,
                                         @PathVariable String tableName,
                                         @PathVariable String values) {
        return ResponseEntity.ok(database.query(String.format("insert into %s (%s) values (%s)", tableName, columns, values)));
    }

    @DeleteMapping(value = "/database/{tableName}/delete/{condition}")
    public ResponseEntity<Result> delete(@PathVariable String tableName,
                                         @PathVariable String condition) {
        return ResponseEntity.ok(database.query(String.format("delete from %s where %s", tableName, condition)));
    }

    @GetMapping(value = "/database/{tableLeftName}/combine/{tableRightName}")
    public ResponseEntity<Result> combine(@PathVariable String tableLeftName,
                                          @PathVariable String tableRightName) {
        return ResponseEntity.ok(database.query(String.format("combine %s with %s", tableLeftName, tableRightName)));
    }


    @GetMapping(value = "/database/{tableLeftName}/subtract/{tableRightName}")
    public ResponseEntity<Result> subtract(@PathVariable String tableLeftName,
                                           @PathVariable String tableRightName) {
        return ResponseEntity.ok(database.query(String.format("subtract %s from %s", tableLeftName, tableRightName)));
    }
}

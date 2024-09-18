package mo.khodakov.webs.mvc;

import lombok.RequiredArgsConstructor;
import mo.khodakov.gui.database.Result;
import mo.khodakov.webs.rest.exceptions.ApiException;
import mo.khodakov.webs.rest.service.DatabaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mvc")
@RequiredArgsConstructor
public class TableController {
    public static final String REDIRECT_MVC_TABLES = "redirect:/mvc/tables";
    public static final String RESULT = "result";
    private final DatabaseService databaseService;

    @GetMapping("/tables")
    public String listTables(Model model) throws ApiException {
        final var database = databaseService.getDatabase();
        Result result = database.query("list tables");
        model.addAttribute("tables", result);
        return "tables";
    }

    @PostMapping("/tables/create")
    public String createTable(@RequestParam String tableName,
                              @RequestParam String columns,
                              Model model) throws ApiException {
        final var database = databaseService.getDatabase();
        Result result = database.query(String.format("create table %s (%s)", tableName, columns));
        model.addAttribute(RESULT, result);
        return REDIRECT_MVC_TABLES;
    }

    @PostMapping("/tables/delete")
    public String dropTable(@RequestParam String tableName,
                            Model model) throws ApiException {
        final var database = databaseService.getDatabase();
        Result result = database.query(String.format("remove table %s", tableName));
        model.addAttribute(RESULT, result);
        return REDIRECT_MVC_TABLES;
    }

    @PostMapping("/tables/combine")
    public String combineTables(@RequestParam String tableLeftName,
                                @RequestParam String tableRightName,
                                Model model) throws ApiException {
        final var database = databaseService.getDatabase();
        Result result = database.query(String.format("combine %s with %s", tableLeftName, tableRightName));
        model.addAttribute(RESULT, result);
        return REDIRECT_MVC_TABLES;
    }

    @PostMapping("/tables/subtract")
    public String subtractTables(@RequestParam String tableLeftName,
                                 @RequestParam String tableRightName,
                                 Model model) throws ApiException {
        final var database = databaseService.getDatabase();
        Result result = database.query(String.format("subtract %s from %s", tableLeftName, tableRightName));
        model.addAttribute(RESULT, result);
        return REDIRECT_MVC_TABLES;
    }
}

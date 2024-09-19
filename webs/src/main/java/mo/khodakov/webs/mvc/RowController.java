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
@RequestMapping("/mvc/rows")
@RequiredArgsConstructor
public class RowController {
    private final DatabaseService databaseService;
    public static final String REDIRECT_ROWS = "redirect:/mvc/rows";
    public static final String ROWS_VIEW = "rows";
    public static final String RESULT = "result";

    @GetMapping
    public String getRowsPage(Model model) {
        // Initial page load for rows.html
        return ROWS_VIEW;
    }

    @GetMapping("/select")
    public String selectRows(@RequestParam String columns,
                             @RequestParam String tableName,
                             @RequestParam(required = false) String condition,
                             Model model) throws ApiException {
        // Construct the SQL query based on the condition parameter
        String query = (condition == null || condition.isEmpty())
                ? String.format("select %s from %s", columns, tableName)
                : String.format("select %s from %s where %s", columns, tableName, condition);

        // Execute the query using the database service
        final var database = databaseService.getDatabase();
        Result result = database.query(query);

        // Add the result to the model to be displayed on the rows.html page
        model.addAttribute("rows", result);
        return ROWS_VIEW;
    }

    @PostMapping("/insert")
    public String insertRow(@RequestParam String columns,
                            @RequestParam String tableName,
                            @RequestParam String values,
                            Model model) throws ApiException {
        // Construct the SQL query for inserting a new row
        String query = String.format("insert into %s (%s) values (%s)", tableName, columns, values);

        // Execute the query using the database service
        final var database = databaseService.getDatabase();
        Result result = database.query(query);

        // Add the result to the model and redirect to display updated rows
        model.addAttribute(RESULT, result);
        return String.format("redirect:/mvc/rows/select?columns=%s&tableName=%s", columns, tableName);
    }

    @PostMapping("/delete")
    public String deleteRow(@RequestParam String tableName,
                            @RequestParam String condition,
                            Model model) throws ApiException {
        // Construct the SQL query for deleting rows based on the condition
        String query = String.format("delete from %s where %s", tableName, condition);

        // Execute the query using the database service
        final var database = databaseService.getDatabase();
        Result result = database.query(query);

        // Add the result to the model and redirect back to the rows page
        model.addAttribute(RESULT, result);
        return REDIRECT_ROWS;
    }
}

package mo.khodakov.webs.mvc;

import mo.khodakov.gui.database.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/mvc/rows")
public class RowController {

    public static final String TABLE_NAME = "&tableName=";
    private final RestTemplate restTemplate;

    @Value("${api.rows.base-url}")  // Inject the base URL from application properties
    private String restApiUrl;

    public RowController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping
    public String getRowsPage(Model model) {
        return "rows";
    }

    @GetMapping("/select")
    public String selectRows(@RequestParam String columns,
                             @RequestParam String tableName,
                             @RequestParam(required = false) String condition,
                             Model model) {
        String url = (condition == null || condition.isEmpty())
                ? restApiUrl + "/select?columns=" + columns + TABLE_NAME + tableName
                : restApiUrl + "/select-by-conditions?columns=" + columns + TABLE_NAME + tableName + "&condition=" + condition;

        Result result = restTemplate.getForObject(url, Result.class);
        model.addAttribute("rows", result);
        return "rows";  // Thymeleaf template "rows.html"
    }

    @PostMapping("/insert")
    public String insertRow(@RequestParam String columns,
                            @RequestParam String tableName,
                            @RequestParam String values,
                            Model model) {
        String url = restApiUrl + "/insert?columns=" + columns + TABLE_NAME + tableName + "&values=" + values;
        Result result = restTemplate.postForObject(url, null, Result.class);
        model.addAttribute("result", result);
        return "redirect:/mvc/rows/select?columns=" + columns + TABLE_NAME + tableName;  // Redirect to view rows
    }

    @PostMapping("/delete")
    public String deleteRow(@RequestParam String tableName,
                            @RequestParam String condition,
                            Model model) {
        String url = restApiUrl + "/delete?tableName=" + tableName + "&condition=" + condition;
        restTemplate.delete(url);
        model.addAttribute("message", "Row deleted successfully");
        return "redirect:/mvc/rows";
    }
}

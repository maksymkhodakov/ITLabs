package mo.khodakov.webs.mvc;

import lombok.extern.slf4j.Slf4j;
import mo.khodakov.gui.database.Database;
import mo.khodakov.gui.database.DatabaseReader;
import mo.khodakov.gui.database.Result;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class DatabaseController {
    private Database database;

    // TODO remove predefined file path
    public DatabaseController() {
        try {
            database = new DatabaseReader("/Users/maksymkhodakov/Downloads/KhodakovITLab/webs/src/main/resources/init.json").read();
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
}

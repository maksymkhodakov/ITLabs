package mo.khodakov.gui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * we need this class to import classes to Webs module
 */
@SpringBootApplication
public class GuiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuiApplication.class, args);
    }

}

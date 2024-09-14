package mo.khodakov.webs;

import mo.khodakov.gui.GuiApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(GuiApplication.class)
@SpringBootApplication
public class WebsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsApplication.class, args);
    }

}

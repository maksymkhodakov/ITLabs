package mo.khodakov.gui.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class DatabaseReader {
    private String filePath;

    public DatabaseReader(String filePath) {
        this.filePath = filePath;
    }

    public Database read() throws FileNotFoundException, JsonSyntaxException, JsonIOException {
        FileReader reader = new FileReader(filePath);
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(reader, Database.class);
    }
}

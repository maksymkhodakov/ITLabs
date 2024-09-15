package mo.khodakov.webs.rest.service.impl;

import mo.khodakov.gui.database.Database;
import mo.khodakov.gui.database.DatabaseReader;
import mo.khodakov.webs.rest.service.DatabaseService;
import mo.khodakov.webs.rest.exceptions.ApiException;
import mo.khodakov.webs.rest.exceptions.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class DatabaseServiceImpl implements DatabaseService {
    @Override
    public Database getDatabase() {
        var database = DatabaseReader.getDatabase();
        if (database == null) {
            throw new ApiException(ErrorCode.NO_ACTIVE_DATABASE);
        }
        return database;
    }
}

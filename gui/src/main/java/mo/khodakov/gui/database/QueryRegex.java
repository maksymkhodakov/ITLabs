package mo.khodakov.gui.database;

public enum QueryRegex {
    // DELETE FROM Tablename WHERE column1=value1,column2=value2,column3=value3
    DELETE_ROWS("^\\s*DELETE\\s+FROM\\s+([^\\s]+)(?:\\s+WHERE\\s+(([^\\s]+),?\\s*)*)\\s*$"),
    // INSERT INTO <tablename> (column1, column2, ...) VALUES (value1, value2, ...)
    INSERT_ROW("^\\s*INSERT\\s+INTO\\s+([^\\s]+)\\s+\\(([^\\)]+)\\)\\s+VALUES\\s*\\(([^\\)]+)\\)s*$"),
    // UPDATE <tablename> SET column1=value1, column2=value2, .. [WHERE column1.1=value1.1, column1.2=value1.2]
    UPDATE_ROWS("^\\s*UPDATE\\s+([^\\s]+)\\s+SET\\s*([^\\s]+)(?:\\s+WHERE\\s+([^\\s]+))?\\s*$"),
    // SELECT (column1, column2,...) FROM <tablename> [WHERE column1=value1, column2=value2,...]
    SELECT_ROWS("^\\s*SELECT\\s+(.+)\\s+FROM\\s+([^\\s]+)(?:\\s+WHERE\\s+([^\\s]+))?\\s*$"),

    // CREATE TABLE Tablename (TYPE1 column1, TYPE2 column2)
    CREATE_TABLE("^\\s*CREATE\\s+TABLE\\s+([^\\s]+)\\s+\\(([^\\)]+)\\)\\s*$"),
    // REMOVE TABLE Tablename
    REMOVE_TABLE("^\\s*REMOVE\\s+TABLE\\s+([^\\s]+)\\s*$"),
    // LIST TABLES
    LIST_TABLES("^\\s*(LIST\\s+TABLES)\\s*$"),

    // COMBINE <tablename1> WITH <tablename2>
    COMBINE("^\\s*COMBINE\\s+([^\\s]+)\\s+WITH\\s+([^\\s]+)\\s*$"),

    // SUBTRACT <tablename1> FROM <tablename2>
    SUBTRACT("^\\s*SUBTRACT\\s+([^\\s]+)\\s+FROM\\s+([^\\s]+)\\s*$");

    private String regex;
    public final String SUFFIX = "/i";

    QueryRegex(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex + SUFFIX;
    }
}

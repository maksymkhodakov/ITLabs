package mo.khodakov.gui.database;

public class Column {
    public enum Type {
        INT,
        FLOAT,
        CHAR,
        STR,
        EMAIL,
        ENUM,
        MONEY,
        MONEY_INV,
        DATE,
        DATE_INV
    }

    private Type type;
    private String name;
    private boolean nullAllowed = true;

    public Column(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNullAllowed() {
        return nullAllowed;
    }
}

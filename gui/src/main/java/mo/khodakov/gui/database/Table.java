package mo.khodakov.gui.database;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {
    private final String name;
    private List<Row> rows;
    private Map<String, Column> columns;

    Table(String name, Collection<Column> columns) {
        this.name = name;
        this.rows = new ArrayList<>();
        this.columns = new HashMap<>();
        for (Column col : columns) {
            this.columns.put(col.getName(), col);
        }
    }

    public void insert(Map<Column, String> values) throws Exception {
        ArrayList<Element> elements = new ArrayList<>();
        getColumns().forEach(column -> elements.add(new Element(values.get(column), column.getName())));

        Row row = new Row(elements);
        row.validate(this);

        rows.add(row);
    }

    public void update(Map<Column, String> values, Predicate<Row> predicate) throws Exception {
        for (Map.Entry<Column, String> entry : values.entrySet()) {
            new Element(entry.getValue(), entry.getKey().getName()).validate(this);
        }

        for (Row row : rows) {
            if (!predicate.test(row)) continue;

            for (Map.Entry<Column, String> entry : values.entrySet()) {
                row.getElement(entry.getKey()).setValue(entry.getValue());
            }
        }
    }

    public void delete(Predicate<Row> predicate) throws Exception {
        rows.removeIf(predicate);
    }

    public Collection<Row> select(Collection<Column> columns, Predicate<Row> predicate) throws Exception {
        if (columns.isEmpty()) throw new Exception("Columns collection is not allowed to be empty in a select query");

        ArrayList<Row> result = new ArrayList<>();

        for (Row row : rows) {
            if (!predicate.test(row)) continue;

            result.add(new Row(columns.stream().map(row::getElement).collect(Collectors.toCollection(ArrayList::new))));
        }

        return result;
    }

    public String getName() {
        return name;
    }

    public Collection<Row> getRows() {
        return rows;
    }

    public Column getColumn(String name) throws Exception {
        if (!columns.containsKey(name))
            throw new Exception(String.format("A column with the name '%s' doesn't exist", name));
        return columns.get(name);
    }

    public Collection<Column> getColumns() {
        return columns.values();
    }

    public Collection<Row> combine(Table rightTable) {
        Collection<Column> rightTableColumns = rightTable.getColumns();
        Collection<Column> leftTableColumns = this.getColumns();
        Collection<Column> sharedColumns = new ArrayList<>();
        for (Column col : leftTableColumns) {
            for (Column col1 : rightTableColumns) {
                if (col.getName().equals(col1.getName()) && col.getType() == col1.getType()) {
                    sharedColumns.add(col);
                }
            }
        }
        return sharedColumns.size() != rightTableColumns.size() || sharedColumns.size() != leftTableColumns.size() ? new ArrayList<>() :
                Stream.concat(this.rows.stream(), rightTable.rows.stream()).toList();
    }

    public Collection<Row> subtract(Table rightTable) {
        Collection<Column> rightTableColumns = rightTable.getColumns();
        Collection<Column> leftTableColumns = this.getColumns();
        Collection<Column> sharedColumns = new ArrayList<>();
        List<Row> result_Left_from_Right = new ArrayList<>(this.rows);
        List<Row> result_Right_from_left = new ArrayList<>(rightTable.rows);
        for (Column col : leftTableColumns) {
            for (Column col1 : rightTableColumns) {
                if (col.getName().equals(col1.getName()) && col.getType() == col1.getType()) {
                    sharedColumns.add(col);
                }
            }
        }

        for (Row leftRow : this.rows) {
            for (Row rightRow : rightTable.rows) {
                if (equalRows(leftRow, rightRow, sharedColumns)) {
                    result_Left_from_Right = removeRow(leftRow, result_Left_from_Right);
                    result_Right_from_left = removeRow(rightRow, result_Right_from_left);
                    break;
                }
            }
        }
        return result_Left_from_Right;
    }

    public boolean findColumn(String colName, Collection<Column> arrayOfColumns) {
        for (Column c : arrayOfColumns) {
            if (c.getName().equals(colName)) {
                return true;
            }
        }
        return false;
    }

    public boolean equalRows(Row left, Row right, Collection<Column> sharedCol) {
        boolean deleteRow = false;
        for (Element left_element : left.getElements()) {
            if (findColumn(left_element.getColumn(), sharedCol)) {
                for (Element right_element : right.getElements()) {
                    if (right_element.getColumn().equals(left_element.getColumn()) && right_element.getValue().equals(left_element.getValue())) {
                        deleteRow = true;
                    } else if (right_element.getColumn().equals(left_element.getColumn()) && !right_element.getValue().equals(left_element.getValue())) {
                        deleteRow = false;
                        break;
                    }

                }
                if (!deleteRow) {
                    return false;
                }
            }

        }
        return deleteRow;
    }

    public List<Row> removeRow(Row row, List<Row> result) {
        for (Row r : result) {
            if (r.getElementsAll().equals(row.getElementsAll())) {
                result.remove(r);
                break;
            }
        }
        return result;
    }
}

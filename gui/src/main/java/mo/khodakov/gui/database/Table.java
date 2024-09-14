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
        Collection<Column> rightTable_col = rightTable.getColumns();
        Collection<Column> leftTable_col = this.getColumns();
        Collection<Column> shared_col = new ArrayList<>();
        for (Column col : leftTable_col) {
            for (Column col1 : rightTable_col) {
                if (col.getName().equals(col1.getName()) && col.getType() == col1.getType()) {
                    shared_col.add(col);
                }
            }
        }
        return shared_col.size() != rightTable_col.size() || shared_col.size() != leftTable_col.size() ? new ArrayList<>() :
                Stream.concat(this.rows.stream(), rightTable.rows.stream()).toList();
    }

    public Collection<Row> subtract(Table rightTable) {
        Collection<Column> rightTable_col = rightTable.getColumns();
        Collection<Column> leftTable_col = this.getColumns();
        Collection<Column> shared_col = new ArrayList<>();
        List<Row> result_Left_from_Right = new ArrayList<>(this.rows);
        List<Row> result_Right_from_left = new ArrayList<>(rightTable.rows);
        for (Column col : leftTable_col) {
            for (Column col1 : rightTable_col) {
                {
                    if (col.getName().equals(col1.getName()) && col.getType() == col1.getType()) {
                        shared_col.add(col);
                    }
                }
            }
        }

        for (Row leftRow : this.rows) {
            for (Row rightRow : rightTable.rows) {
                if (equal_rows(leftRow, rightRow, shared_col)) {
                    result_Left_from_Right = remove_row(leftRow, result_Left_from_Right);
                    result_Right_from_left = remove_row(rightRow, result_Right_from_left);
                    break;
                }
            }
        }
        return result_Left_from_Right;
    }

    public boolean find_col(String col_name, Collection<Column> arr_col) {
        for (Column c : arr_col) {
            if (c.getName().equals(col_name)) {
                return true;
            }
        }
        return false;
    }

    public boolean equal_rows(Row left, Row right, Collection<Column> shared_col) {
        boolean delete_row = false;
        for (Element left_element : left.getElements()) {
            if (find_col(left_element.getColumn(), shared_col)) {
                for (Element right_element : right.getElements()) {
                    if (right_element.getColumn().equals(left_element.getColumn()) && right_element.getValue().equals(left_element.getValue())) {
                        delete_row = true;
                    } else if (right_element.getColumn().equals(left_element.getColumn()) && !right_element.getValue().equals(left_element.getValue())) {
                        delete_row = false;
                        break;
                    }

                }
                if (!delete_row) {
                    return false;
                }
            }

        }
        return delete_row;
    }

    public List<Row> remove_row(Row row, List<Row> result) {
        for (Row r : result) {
            if (r.getElementsAll().equals(row.getElementsAll())) {
                result.remove(r);
                break;
            }
        }
        return result;
    }
}

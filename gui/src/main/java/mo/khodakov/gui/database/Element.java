package mo.khodakov.gui.database;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class Element implements Serializable {
    public static final BigDecimal MAX_MONEY = new BigDecimal("10000000000000.00");

    private String value;
    private String column;

    Element(String value, String column) {
        this.value = value;
        this.column = column;
    }

    @JsonIgnore
    public Integer getAsInteger() {
        return parseInt(value);
    }

    @JsonIgnore
    public Float getAsFloat() {
        return Float.parseFloat(value);
    }

    @JsonIgnore
    public char getAsCharacter() throws Exception {
        if (value.length() != 1) throw new Exception("Invalid character value");
        return value.charAt(0);
    }

    @JsonIgnore
    public String getAsString() {
        return value;
    }

    @JsonIgnore
    public String getAsEmail() throws Exception {
        Pattern regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
        Matcher regMatcher = regexPattern.matcher(value);
        if (regMatcher.matches()) {
            return value;
        } else {
            throw new Exception("Invalid email value");
        }
    }

    @JsonIgnore
    public CustomEnum getAsEnum() throws Exception {
        Pattern regexPattern = Pattern.compile("^\\s*\\{(\\s*\\w* *= *\\d*\\s*;?)*?\\s*\\}$");
        Matcher regMatcher = regexPattern.matcher(value);
        if (regMatcher.matches()) {
            String str = value;
            str = str.replaceAll("[{}]", "");
            ArrayList<String> arr = Arrays.stream(str.split(";")).map(String::trim).collect(Collectors.toCollection(ArrayList::new));
            ArrayList<String> keys = new ArrayList<>();
            ArrayList<Integer> values = new ArrayList<>();
            for (String a : arr) {
                String[] pair = a.split("=");
                keys.add(pair[0]);
                values.add(parseInt(pair[1]));
            }
            CustomEnum customEnum = new CustomEnum(keys, values);
            return customEnum;
        } else {
            throw new Exception("Invalid email value");
        }
    }

    @JsonIgnore
    public Date getAsDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(value);
        } catch (Exception e) {
            throw new Exception("Invalid date");
        }
    }

    @JsonIgnore
    public List<Date> getAsDateInv() throws Exception {
        List<Date> dateList = new ArrayList<>();
        String[] dateValues = value.split(",");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (String dateStr : dateValues) {
            try {
                dateList.add(sdf.parse(dateStr.trim()));
            } catch (Exception e) {
                throw new Exception("Invalid date interval value");
            }
        }

        return dateList;
    }

    @JsonIgnore
    public BigDecimal getAsMoney() throws Exception {
        // Regular expression for validating a monetary format (e.g., $1,000.00)
        String moneyRegex = "^\\$\\d{1,3}(,\\d{3})*(\\.\\d{2})?$";

        // Check if the value matches the monetary format
        if (!value.matches(moneyRegex)) {
            throw new Exception("Invalid money format");
        }

        // Remove the dollar sign and commas
        String numericValue = value.replaceAll("[\\$,]", "");

        // Convert the string to BigDecimal
        BigDecimal money = new BigDecimal(numericValue);

        // Check if the value exceeds the maximum allowed amount
        if (money.compareTo(MAX_MONEY) > 0) {
            throw new Exception("Value exceeds maximum allowed amount of $10,000,000,000,000.00");
        }

        return money;
    }

    @JsonIgnore
    public List<BigDecimal> getAsMoneyInv() throws Exception {
        List<BigDecimal> moneyList = new ArrayList<>();

        // Split the interval by semicolon
        String[] moneyValues = value.split(";");

        // Regular expression for validating a monetary format (e.g., $1,000.00)
        String moneyRegex = "^\\$\\d{1,3}(,\\d{3})*(\\.\\d{2})?$";

        for (String moneyStr : moneyValues) {
            moneyStr = moneyStr.trim();

            // Check if the value matches the monetary format
            if (!moneyStr.matches(moneyRegex)) {
                throw new Exception("Invalid money format in interval");
            }

            // Remove the dollar sign and commas
            String numericValue = moneyStr.replaceAll("[\\$,]", "");

            // Convert the string to BigDecimal
            BigDecimal money = new BigDecimal(numericValue);

            // Check if the value exceeds the maximum allowed amount
            if (money.compareTo(MAX_MONEY) > 0) {
                throw new Exception("Value exceeds maximum allowed amount of $10,000,000,000,000.00");
            }

            // Add the valid money value to the list
            moneyList.add(money);
        }

        return moneyList;
    }

    public String getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }

    void validate(Table table) throws Exception {
        Column column = table.getColumn(this.column);

        if (value == null) {
            if (column.isNullAllowed()) return;
            throw new Exception("Null value is not allowed");
        }

        try {
            switch (column.getType()) {
                case INT:
                    getAsInteger();
                    break;
                case FLOAT:
                    getAsFloat();
                    break;
                case CHAR:
                    getAsCharacter();
                    break;
                case STR:
                    getAsString();
                    break;
                case EMAIL:
                    getAsEmail();
                    break;
                case ENUM:
                    getAsEnum();
                    break;
                case MONEY:
                    getAsMoney();
                    break;
                case MONEY_INV:
                    getAsMoneyInv();
                    break;
                case DATE:
                    getAsDate();
                    break;
                case DATE_INV:
                    getAsDateInv();
                    break;
            }
        } catch (Exception e) {
            throw new Exception(String.format("Invalid element value '%s': %s", value, e.getMessage()));
        }
    }

    public boolean equals(String other) {
        if (other == null) return value == null;
        if (value == null) return other.equals("null");
        return value.equals(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Element element = (Element) o;
        return Objects.equals(value, element.value) &&
                Objects.equals(column, element.column);
    }

}

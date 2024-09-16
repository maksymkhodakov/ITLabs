package mo.khodakov.webs;

import mo.khodakov.gui.database.Database;
import mo.khodakov.gui.database.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class DatabaseTest {

    @Test
    void createTable() {
        Database database = new Database(null);
        assertSame(Result.Status.OK, database.query("create table test1 (INT id, STR name)").getStatus());
        assertSame(Result.Status.OK, database.query("create table test2 (INT id, STR name)").getStatus());

        Result result = database.query("list tables");
        assertSame(Result.Status.OK, result.getStatus());
        assertSame(2, result.getRows().size());
    }

    @Test
    void email() {
        Database database = new Database(null);
        assertSame(Result.Status.OK, database.query("create table test1 (EMAIL email)").getStatus());

        assertSame(Result.Status.OK, database.query("insert into test1 (email) values(darianna697@gmail.com)").getStatus());
        assertSame(Result.Status.FAIL, database.query("insert into test1 (email) values(darianna697)").getStatus());
    }

    @Test
    void money() {
        Database database = new Database(null);
        assertSame(Result.Status.OK, database.query("create table test2 (MONEY money)").getStatus());

        assertSame(Result.Status.FAIL, database.query("insert into test2 (money) values(100000)").getStatus());
        assertSame(Result.Status.FAIL, database.query("insert into test2 (money) values(100000)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into test2 (money) values($100,000.00)").getStatus());
    }

    @Test
    void moneyInv() {
        Database database = new Database(null);
        assertSame(Result.Status.OK, database.query("create table test3 (MONEY_INV inv)").getStatus());

        assertSame(Result.Status.FAIL, database.query("insert into test3 (inv) values($100000.00;$10,000.00)").getStatus());
        assertSame(Result.Status.FAIL, database.query("insert into test3 (inv) values(100000.00;$10,000.00)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into test3 (inv) values($100,000.00;$10.000.00)").getStatus());
    }

    @Test
    void date() {
        Database database = new Database(null);
        assertSame(Result.Status.OK, database.query("create table test4 (DATE date)").getStatus());

        assertSame(Result.Status.FAIL, database.query("insert into test4 (date) values(2000-2-aaa1)").getStatus());
        assertSame(Result.Status.FAIL, database.query("insert into test4 (date) values(10.000.00)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into test4 (date) values(2022-02-02)").getStatus());
    }

    @Test
    void dateInv() {
        Database database = new Database(null);
        assertSame(Result.Status.OK, database.query("create table test5 (DATE_INV inv)").getStatus());

        assertSame(Result.Status.FAIL, database.query("insert into test5 (inv) values(10.000.00)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into test5 (inv) values(2000-02-01)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into test5 (inv) values(2022-02-02;2022-02-03)").getStatus());
    }

    @Test
    void combineOperation() {
        Database database = new Database(null);
        assertSame(Result.Status.OK, database.query("create table white_cats (INT id, STR name)").getStatus());
        assertSame(Result.Status.OK, database.query("create table black_cats (INT id, STR name)").getStatus());

        assertSame(Result.Status.OK, database.query("insert into white_cats  (id, name) values(1, cat1)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into white_cats  (id, name) values(3, cat2)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into white_cats  (id, name) values(4, cat4)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into white_cats  (id, name) values(6, cat8)").getStatus());

        assertSame(Result.Status.OK, database.query("insert into black_cats  (id, name) values(3, cat2)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into black_cats  (id, name) values(4, cat4)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into black_cats  (id, name) values(6, cat15)").getStatus());

        Result result = database.query("COMBINE white_cats WITH black_cats");
        assertSame(Result.Status.OK, result.getStatus());
        assertSame(7, result.getRows().size());

        assertSame(Result.Status.OK, database.query("create table white_cats_2 (INT id, STR name, MONEY price)").getStatus());
        assertSame(Result.Status.OK, database.query("create table black_cats_2 (INT id, STR name, MONEY_INV price_interval)").getStatus());

        result = database.query("COMBINE white_cats_2 WITH black_cats_2");
        assertSame(Result.Status.OK, result.getStatus());
        assertSame(0, result.getRows().size());

        assertSame(Result.Status.OK, database.query("create table white_cats_3 (INT id, STR name, MONEY price)").getStatus());
        assertSame(Result.Status.OK, database.query("create table black_cats_3 (INT id, STR name)").getStatus());

        result = database.query("COMBINE white_cats_3 WITH black_cats_3");
        assertSame(Result.Status.OK, result.getStatus());
        assertSame(0, result.getRows().size());
    }

    @Test
    void subtractOperation() {
        Database database = new Database(null);
        assertSame(Result.Status.OK, database.query("create table all_cats (INT id, STR name, FLOAT weight)").getStatus());
        assertSame(Result.Status.OK, database.query("create table black_cats (INT id, STR name)").getStatus());

        assertSame(Result.Status.OK, database.query("insert into all_cats  (id, name, weight) values(1, cat1, 2.3)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into all_cats  (id, name, weight) values(3, cat2, 5.1)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into all_cats  (id, name, weight) values(4, cat4, 3.0)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into all_cats  (id, name, weight) values(6, cat8, 2.2)").getStatus());

        assertSame(Result.Status.OK, database.query("insert into black_cats  (id, name) values(3, cat2)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into black_cats  (id, name) values(4, cat4)").getStatus());
        assertSame(Result.Status.OK, database.query("insert into black_cats  (id, name) values(6, cat15)").getStatus());

        Result result = database.query("SUBTRACT all_cats FROM black_cats");
        assertSame(Result.Status.OK, result.getStatus());
        assertSame(2, result.getRows().size());

        result = database.query("SUBTRACT black_cats FROM all_cats");
        assertSame(Result.Status.OK, result.getStatus());
        assertSame(1, result.getRows().size());
    }
}

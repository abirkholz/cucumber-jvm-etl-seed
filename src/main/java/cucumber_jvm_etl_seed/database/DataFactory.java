package cucumber_jvm_etl_seed.database;

import io.codearte.jfairy.Fairy;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;

public class DataFactory {



    public static DateTime randomDateTime() {
        return Fairy.create().dateProducer().randomDateBetweenYearAndNow(2016);
    }

    public static Integer randomInteger(int min, int max) {
        return Fairy.create().baseProducer().randomBetween(min, max);
    }

    public static String randomString(int characterCount) {
        return Fairy.create().textProducer().randomString(characterCount);
    }

    public static String generateRowFromTableSchema(String tableName, List schema) {
        Fairy fairy = Fairy.create();
        String columnNames = "";
        String columnValues = "";

        for (int i = 0; i < schema.size(); i++) {
            HashMap<String, String> column = (HashMap<String, String>) schema.get(i);
            String name = column.get("column_name");
            String type = column.get("column_type");
            Integer size = column.get("column_size").isEmpty() ? 50 : Integer.valueOf(column.get("column_size"));

            if (i != 0) {
                columnNames += ",";
                columnValues += ",";
            }

            switch (type) {
                case "serial":
                    columnValues += null;
                case "varchar":
                    columnValues += "'"+fairy.textProducer().randomString(size)+"'";
                    break;
                case "text":
                    columnValues += "'"+fairy.textProducer().paragraph()+"'";
                    break;
                case "int2":
                case "int4":
                case "int8":
                    columnValues += fairy.baseProducer().randomBetween(size - 1, size);
                    break;
                case "bool":
                    columnValues += fairy.baseProducer().trueOrFalse();
                    break;
                case "timestamp":
                    columnValues += fairy.dateProducer().randomDateBetweenYearAndNow(2016);
                    break;
                default:
                    columnValues += "'"+fairy.textProducer().latinSentence()+"'";
                    break;
            }
        }

        return String.format("INSERT INTO %s (%s) VALUES (%s)\n/", tableName, columnNames, columnValues);
    }

}

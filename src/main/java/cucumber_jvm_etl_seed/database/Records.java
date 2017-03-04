package cucumber_jvm_etl_seed.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Records {
    private static List<Map<String, Object>> queryRecords = new ArrayList<>();

    public static void addQueryRecord(String table, String type, ResultSet resultSet, Integer resultCount, List resultKeys) {
        Map<String,Object> record = new HashMap<>();
        record.put("table", table);
        record.put("type", type);
        record.put("resultSet", resultSet);
        record.put("resultCount", resultCount);
        record.put("resultKeys", resultKeys);
        queryRecords.add(record);
    }

    public static List<Map<String, Object>> getQueryRecords() {
        return queryRecords;
    }

    public static List<Map<String, Object>> getQueryRecordsByTableAndType (String table, String type) {
        List<Map<String, Object>> records = new ArrayList<>();

        for (Map record: queryRecords) {
            String rTable = record.get("table").toString();
            String rType = record.get("type").toString();

            if (rTable.equals(table) && rType.equals(type)) {
               records.add(record);
            }
        }

        return records;
    }

    public static void clearQueryRecords() {
        queryRecords.clear();
    }

    public static List<ResultSet> getRowsFromQueryRecords(String tableName, String queryType, List<Map<String, Object>> queryRecords) throws SQLException {
        List<ResultSet> results = new ArrayList<>();
        List<Map<String, Object>> qRecords = getQueryRecordsByTableAndType(tableName, queryType);

        for (Map record: qRecords) {
            if(queryType.equals("insert")) {
                ResultSet rs = (ResultSet) record.get("resultSet");
                String query = "SELECT * FROM "+tableName+" WHERE id=" +rs.getInt(1);
                ResultSet result = Query.select(query);
                results.add(result);
            }
            else if (queryType.equals("update")) {
                List rs = (List) record.get("resultKeys");
                String query = "SELECT * FROM "+tableName+" WHERE id=" +rs.get(0);
                ResultSet result = Query.select(query);
                results.add(result);
            }
            else if (queryType.equals("delete")) {
                if (record.get("resultSet") != null) {
                    ResultSet rs = (ResultSet) record.get("resultSet");
                    results.add(rs);
                }
            }
        }

        return results;
    }

    public static List<List<Map<String, String>>> formatRows(List<ResultSet> results) throws SQLException {
        List<List<Map<String, String>>> rows = new ArrayList<>();

        for (ResultSet result: results) {
            rows.add(formatRow(result));
        }

        return rows;
    }

    public static List<Map<String, String>> formatRow(ResultSet result) throws SQLException {
        List<Map<String, String>> record = new ArrayList<>();
        int columnCount = result.getMetaData().getColumnCount();

        while(result.next())
        {
            for (int i=0; i < columnCount; i++)
            {
                Map<String, String> dd = new HashMap<>();
                dd.put("columnName", result.getMetaData().getColumnLabel(i + 1));
                dd.put("columnType", result.getMetaData().getColumnTypeName(i + 1));
                dd.put("columnValue", result.getString(i + 1));
                record.add(dd);
            }
        }

        return record;
    }
}

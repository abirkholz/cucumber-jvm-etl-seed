package cucumber_jvm_etl_seed.database;

import java.sql.*;
import java.util.*;

public class Metadata {

    public static List<Map<String, String>> getMutualTableColumns(String sourceTable, String targetTable) throws SQLException {
        List<Map<String, String>> sourceColumns = getTableColumnMetadata(sourceTable);
        List<Map<String, String>> targetColumns = getTableColumnMetadata(targetTable);

        //Identify the columns that sourceTable shares with targetTable
        sourceColumns.retainAll(targetColumns);

        return sourceColumns;
    }

    public static List<Map<String, String>> getTableColumnMetadata(String tableName) throws SQLException {
        DatabaseMetaData md = getDatabaseMetadata();
        ResultSet rs = md.getColumns(null, null, tableName, null);
        List<Map<String, String>> cols = new ArrayList<>();

        while (rs.next()) {
            Map<String, String> col = new HashMap<>();
            col.put("columnName", rs.getString("COLUMN_NAME"));
            col.put("columnType", rs.getString("TYPE_NAME"));
            col.put("columnSize", String.valueOf(rs.getInt("COLUMN_SIZE")));
            cols.add(col);
        }

        return cols;
    }

    private static DatabaseMetaData getDatabaseMetadata() throws SQLException {
        return Connect.getConnection().getMetaData();
    }


}

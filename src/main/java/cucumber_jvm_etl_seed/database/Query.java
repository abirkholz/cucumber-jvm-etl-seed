package cucumber_jvm_etl_seed.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Query {

    public static ResultSet select(String sql) throws SQLException {
        Statement st = Connect.getConnection().createStatement();
        ResultSet rs = st.executeQuery(sql);
        return rs != null ? rs : null;
    }

    public static ResultSet insert(String sql) throws SQLException {
        PreparedStatement ps = Connect.getConnection().prepareStatement(sql, new String[] { "id" });
        ps.executeUpdate();
        ResultSet gk = ps.getGeneratedKeys();
        return gk != null && gk.next() ? gk : null;
    }

    public static int updateReturnCount(String sql) throws SQLException {
        PreparedStatement ps = Connect.getConnection().prepareStatement(sql);
        ps.executeUpdate();
        return ps.getUpdateCount();
    }

    public static List<Integer> updateReturnKeys(String sql, String table) throws SQLException {
        PreparedStatement ps = Connect.getConnection().prepareStatement(sql);
        ps.executeUpdate();
        return getAffectedPrimaryKeys(sql, table);
    }

    public static ResultSet updateReturnResultSet(String sql) throws SQLException {
        PreparedStatement ps = Connect.getConnection().prepareStatement(sql);
        ps.executeUpdate();
        ResultSet rs = ps.getResultSet();
        return rs != null && rs.next() ? rs : null;
    }

    public static ResultSet deleteReturnResultSet(String sql, String table) throws SQLException {
        ResultSet rsSelect = select("SELECT * FROM "+table+" WHERE "+sql.split( "WHERE")[1]);

        PreparedStatement psDelete = Connect.getConnection().prepareStatement(sql);
        psDelete.executeUpdate();
        return rsSelect;
    }

    public static ResultSet callStoredProcedure(String procedure) throws SQLException {
        CallableStatement cs;
        String proc = Procedures.getProcedure(procedure);
        cs = Connect.getConnection().prepareCall(proc);
        ResultSet rs = cs.executeQuery();
        return rs != null ? rs : null;
    }

    public static int truncateTable(String tableName) throws SQLException {
        Statement st = Connect.getConnection().createStatement();
        int result = st.executeUpdate("TRUNCATE TABLE " + tableName);
        return result != 0 ? result : 0;
    }

    public static String getQueryValueForColumnType(String type, String value) {
        switch (type) {
            case "varchar":
            case "nvarchar":
            case "text":
            case "datetime":
                return "'" + value +"'";
            case "int":
            case "tinyint":
            case "bigint":
                return value;
            default:
                return null;
        }
    }

    private static List<Integer> getAffectedPrimaryKeys(String sql, String table) throws SQLException {
        List<Integer> primaryKeys = new ArrayList<>();
        List<List<Map<String, String>>> rows = getAffectedRows(sql, table);

        for (List<Map<String, String>> row : rows) {
            for (Map column: row ) {
                if(column.get("columnName").equals("id")) {
                    primaryKeys.add(Integer.parseInt(column.get("columnValue").toString()));
                }
            }
        }

        return primaryKeys;
    }

    private static List<List<Map<String, String>>> getAffectedRows(String sql, String table) throws SQLException {
        String sqlString = "SELECT * FROM " + table +" WHERE "+ sql.split( "WHERE")[1];
        PreparedStatement ps = Connect.getConnection().prepareStatement(sqlString);
        ResultSet rs = ps.executeQuery();
        List<ResultSet> rows = new ArrayList<>();
        rows.add(rs);
        List<List<Map<String, String>>> records = Records.formatRows(rows);
        return records;
    }


}


package cucumber_jvm_etl_seed.database;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import cucumber_jvm_etl_seed.database.Query;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utility {

    public static List<String> createSelectForTargetValidation(String targetTable, List<List<Map<String, String>>> sourceRecords, List<Map<String, String>> mutualColumns) {
        List<String> selectQueries = new ArrayList<>();

        for (List<Map<String, String>> sRecord : sourceRecords) {
            List<Map<String,String>> mutualRecords = new ArrayList<>();

            for (Map<String, String> rCol: sRecord) {
                String rColName = rCol.get("columnName");
                for (Map<String,String> mCol: mutualColumns) {
                    if(rColName.equals(mCol.get("columnName")) && !rColName.equals("id")) {
                        mutualRecords.add(rCol);
                        break;
                    }
                }
            }
            selectQueries.add(createTargetTableSelectQuery(targetTable, mutualRecords));
        }

        return selectQueries;
    }

    private static String createTargetTableSelectQuery(String targetTable, List<Map<String, String>> records) {

        if (records.size() == 0) { return null; }

        String sql = "SELECT * FROM "+ targetTable +" WHERE ";

        for (Map<String, String> record: records) {
            String name = record.get("columnName");
            String type = record.get("columnType");
            String value = record.get("columnValue");
            String whereValue = Query.getQueryValueForColumnType(type, value);

            // Check if last
            if (!(records.indexOf(record) == (records.size() -1))) {
                sql += name +"="+whereValue+" AND ";
            }
            else {
                sql += name +"="+whereValue;
            }
        }

        return sql;
    }

    public static List getQueryStringsFromXml(String queryType, String tableName, Document xmlDoc) throws XPathExpressionException {

        XPath xpath = XPathFactory.newInstance().newXPath();
        String expr = "//root/table[@name='"+ tableName +"']/queries/child::*";
        NodeList nodes = (NodeList) xpath.evaluate(expr, xmlDoc, XPathConstants.NODESET);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if(node.getNodeName().equals(queryType)) {
                String key = node.getAttributes().getNamedItem("key").getTextContent();
                result.add(node.getTextContent());
            }
        }

        return result;
    }
}

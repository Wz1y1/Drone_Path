package uk.ac.ed.inf;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Database {
    //this read the specific column of given date
    public static List<String> readOrder(Date date, String label) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:derby://localhost:9876/derbyDB");
        Statement statement = conn.createStatement();
        final String orderQuery = "select * from orders where deliveryDate=(?)";
        PreparedStatement psOrderQuery = null;
        try {
            psOrderQuery = conn.prepareStatement(orderQuery);
            psOrderQuery.setDate(1, (java.sql.Date) date);
        } catch (SQLException e){
            e.printStackTrace();
        }
        List<String> orderList = new ArrayList<>();
        ResultSet rs = psOrderQuery.executeQuery();
        while (rs.next()) {
            String order = rs.getString(label);
            orderList.add(order);
        }
        return orderList;
    }
    // Read from table orderDetail, given orderID, output Item
    public static List<String> readOrderDetail(String orderID,String Item) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:derby://localhost:9876/derbyDB");
        Statement statement = conn.createStatement();
        final String orderQuery = "select * from orderDetails where orderNo=(?)";
        PreparedStatement psOrderQuery = null;
        try {
            psOrderQuery = conn.prepareStatement(orderQuery);
            psOrderQuery.setString(1, orderID);
        } catch (SQLException e){
            e.printStackTrace();
        }
        List<String> itemList = new ArrayList<>();
        ResultSet rs = psOrderQuery.executeQuery();
        while (rs.next()) {
            String order = rs.getString(Item);
            itemList.add(order);
        }
        return itemList;
    }

    public void createTableFlightPath() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:derby://localhost:9876/derbyDB");
        Statement statement = conn.createStatement();
        DatabaseMetaData databaseMetadata = conn.getMetaData();
        ResultSet resultSet =
                databaseMetadata.getTables(null, null, "FLIGHTPATH", null);
        if (resultSet.next()) {
            statement.execute("drop table flightpath");
            System.out.println("drop the table");
        }
        statement.execute(
                "create table flightpath(" +
                        "orderNo char(8)," +
                        "fromLongitude double, " +
                        "fromLatitude double," +
                        "angle Integer," +
                        "toLongitude double," +
                        "toLatitude double)");
        System.out.println("Create successfully");
    }

    public void createTableDeliveries() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:derby://localhost:9876/derbyDB");
        Statement statement = conn.createStatement();
        DatabaseMetaData databaseMetadata = conn.getMetaData();
        ResultSet resultSet =
                databaseMetadata.getTables(null, null, "DELIVERIES", null);
        if (resultSet.next()) {
            statement.execute("drop table deliveries");
            System.out.println("drop the table");
        }
        statement.execute(
                "create table deliveries(orderNo char(8),\n" +
                        "deliveredTo varchar(19),\n" +
                        "costInPence int)");
        System.out.println("Create successfully");
    }
}



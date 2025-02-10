package database;

import model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDAO {

    public Order createOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, product_id, quantity, total_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, order.getUserId());
            pstmt.setInt(2, order.getProductId());
            pstmt.setInt(3, order.getQuantity());
            pstmt.setDouble(4, order.getTotalPrice());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setOrderId(generatedKeys.getInt(1));
                    }
                } catch (SQLException e) {
                    System.out.println("Creating order failed, no ID obtained." + e.getMessage());
                    return null;
                }
                return order;
            } else {
                System.out.println("Creating order failed, no rows affected.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
            return null;
        }
    }


    public Order getOrderById(int orderId) {
        String sql = "SELECT order_id, user_id, product_id, quantity, total_price FROM orders WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setProductId(rs.getInt("product_id"));
                    order.setQuantity(rs.getInt("quantity"));
                    order.setTotalPrice(rs.getDouble("total_price"));
                    return order;
                } else {
                    System.out.println("Order not found with ID: " + orderId);
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order by ID: " + e.getMessage());
            return null;
        }
    }
}

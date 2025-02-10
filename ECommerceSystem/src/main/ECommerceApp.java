package main;

import model.Order;
import model.Product;
import model.Review;
import model.User;
import database.OrderDAO;
import database.ProductDAO;
import database.ReviewDAO;
import database.UserDAO;

import java.util.List;
import java.util.Scanner;

public class ECommerceApp {

    private static User currentUser = null;
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDAO userDAO = new UserDAO();
    private static final ProductDAO productDAO = new ProductDAO();
    private static final OrderDAO orderDAO = new OrderDAO();
    private static final ReviewDAO reviewDAO = new ReviewDAO();

    public static void main(String[] args) {
        displayMainMenu();
    }

    private static void displayMainMenu() {
        while (true) {
            System.out.println("\nWelcome to the E-Commerce System!");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    System.out.println("Exiting... Thank you!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void registerUser() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        System.out.print("Confirm your password: ");
        String confirmPassword = scanner.nextLine();

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.");
            return;
        }

        System.out.print("Enter your mobile number: ");
        String mobileNumber = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        User newUser = new User();
        newUser.setName(name);
        newUser.setPassword(password);
        newUser.setMobileNumber(mobileNumber);
        newUser.setEmail(email);

        User registeredUser = userDAO.registerUser(newUser);

        if (registeredUser != null) {
            System.out.println("Registration successful! You can now login.");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    private static void loginUser() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        currentUser = userDAO.loginUser(email, password);

        if (currentUser != null) {
            System.out.println("Login successful! Welcome, " + currentUser.getName() + ".");
            displayUserMenu();
        } else {
            System.out.println("Login failed. Invalid credentials.");
        }
    }

    private static void displayUserMenu() {
        while (currentUser != null) {
            System.out.println("\nUser Menu:");
            System.out.println("1. View Products");
            System.out.println("2. Place Order");
            System.out.println("3. View Reviews");
            System.out.println("4. Add a Review");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewProducts();
                    break;
                case 2:
                    placeOrder();
                    break;
                case 3:
                    viewReviews();
                    break;
                case 4:
                    addReview();
                    break;
                case 5:
                    logout();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void viewProducts() {
        List<Product> products = productDAO.getAllProducts();

        if (products == null || products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.println("\nAvailable Products:");
        for (Product product : products) {
            System.out.println(product.getProductId() + ". " + product.getName() + " - $" + product.getPrice() + " (Stock: " + product.getStock() + ")");

            // Display reviews for each product
            List<Review> reviews = reviewDAO.getReviewsByProductId(product.getProductId());
            if (reviews != null && !reviews.isEmpty()) {
                System.out.println("   Reviews:");
                for (Review review : reviews) {
                    System.out.println("   - Rating: " + review.getRating() + "/5, Comment: \"" + review.getComment() + "\"");
                }
            } else {
                System.out.println("   No reviews yet.");
            }
        }
    }

    private static void placeOrder() {
        System.out.print("Enter the product ID to order: ");
        int productId = scanner.nextInt();
        System.out.print("Enter the quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Product product = productDAO.getProductById(productId);

        if (product == null) {
            System.out.println("Invalid product ID.");
            return;
        }

        if (product.getStock() < quantity) {
            System.out.println("Not enough stock available for this product.");
            return;
        }

        double totalPrice = product.getPrice() * quantity;

        Order order = new Order();
        order.setUserId(currentUser.getUserId());
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);

        Order createdOrder = orderDAO.createOrder(order);

        if (createdOrder != null) {
            // Update product stock
            int newStock = product.getStock() - quantity;
            boolean stockUpdated = productDAO.updateProductStock(productId, newStock);

            if (stockUpdated) {
                System.out.println("Order placed successfully!");
                System.out.println("Your order details:");
                System.out.println("- Product: " + product.getName());
                System.out.println("- Quantity: " + quantity);
                System.out.println("- Total Price: $" + totalPrice);
            } else {
                System.out.println("Order placed successfully, but stock update failed.");
            }
        } else {
            System.out.println("Failed to place order.");
        }
    }

    private static void viewReviews() {
        System.out.println("Select a product to view reviews:");
        List<Product> products = productDAO.getAllProducts();

        if (products == null || products.isEmpty()) {
            System.out.println("No products available to review.");
            return;
        }

        for (Product product : products) {
            System.out.println(product.getProductId() + ". " + product.getName());
        }

        System.out.print("Enter your choice: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Product selectedProduct = productDAO.getProductById(productId);
        if (selectedProduct == null) {
            System.out.println("Invalid product ID.");
            return;
        }

        List<Review> reviews = reviewDAO.getReviewsByProductId(productId);

        if (reviews == null || reviews.isEmpty()) {
            System.out.println("No reviews available for " + selectedProduct.getName() + ".");
            return;
        }

        System.out.println("\nReviews for " + selectedProduct.getName() + ":");
        for (Review review : reviews) {
            System.out.println("- Rating: " + review.getRating() + "/5, Comment: \"" + review.getComment() + "\"");
        }
    }

    private static void addReview() {
        System.out.print("Enter the product ID to review: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Product product = productDAO.getProductById(productId);
        if (product == null) {
            System.out.println("Invalid product ID.");
            return;
        }

        System.out.print("Enter your rating (1-5): ");
        int rating = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (rating < 1 || rating > 5) {
            System.out.println("Invalid rating. Please enter a value between 1 and 5.");
            return;
        }

        System.out.print("Enter your comment: ");
        String comment = scanner.nextLine();

        Review review = new Review();
        review.setProductId(productId);
        review.setUserId(currentUser.getUserId());
        review.setRating(rating);
        review.setComment(comment);

        Review addedReview = reviewDAO.addReview(review);

        if (addedReview != null) {
            System.out.println("Review added successfully!");
        } else {
            System.out.println("Failed to add review.");
        }
    }


    private static void logout() {
        currentUser = null;
        System.out.println("Logging out... Thank you for using the E-Commerce System");
    }
}

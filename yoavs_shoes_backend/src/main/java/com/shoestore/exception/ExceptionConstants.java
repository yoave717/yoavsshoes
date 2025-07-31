package com.shoestore.exception;

/**
 * Constants for exception messages and error codes
 */
public class ExceptionConstants {

    // User-related errors
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email is already in use";
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String USER_NOT_ACTIVE = "User account is not active";

    // Product-related errors
    public static final String PRODUCT_NOT_FOUND = "Product not found";
    public static final String BRAND_NOT_FOUND = "Brand not found";
    public static final String CATEGORY_NOT_FOUND = "Category not found";
    public static final String SHOE_MODEL_NOT_FOUND = "Shoe model not found";
    public static final String INVALID_PRODUCT_DATA = "Invalid product data";
    public static final String DUPLICATE_SKU = "SKU already exists";

    // Inventory-related errors
    public static final String INSUFFICIENT_STOCK = "Insufficient stock";
    public static final String SIZE_NOT_AVAILABLE = "Size not available";
    public static final String PRODUCT_OUT_OF_STOCK = "Product is out of stock";
    public static final String INVALID_QUANTITY = "Invalid quantity";

    // Order-related errors
    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String ORDER_CANNOT_BE_MODIFIED = "Order cannot be modified";
    public static final String ORDER_ALREADY_CANCELLED = "Order is already cancelled";
    public static final String ORDER_ALREADY_SHIPPED = "Order is already shipped";
    public static final String INVALID_ORDER_STATUS = "Invalid order status";
    public static final String EMPTY_CART = "Shopping cart is empty";

    // Address-related errors
    public static final String ADDRESS_NOT_FOUND = "Address not found";
    public static final String INVALID_ADDRESS = "Invalid address information";
    public static final String DEFAULT_ADDRESS_REQUIRED = "Default address is required";

    // Authentication/Authorization errors
    public static final String ACCESS_DENIED = "Access denied";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
    public static final String TOKEN_EXPIRED = "Token has expired";
    public static final String INVALID_TOKEN = "Invalid token";
    public static final String ADMIN_ACCESS_REQUIRED = "Administrator access required";

    // File-related errors
    public static final String FILE_UPLOAD_FAILED = "File upload failed";
    public static final String INVALID_FILE_TYPE = "Invalid file type";
    public static final String FILE_TOO_LARGE = "File size exceeds maximum limit";
    public static final String FILE_NOT_FOUND = "File not found";

    // Validation errors
    public static final String INVALID_EMAIL_FORMAT = "Invalid email format";
    public static final String INVALID_PASSWORD_FORMAT = "Invalid password format";
    public static final String REQUIRED_FIELD_MISSING = "Required field is missing";
    public static final String INVALID_FIELD_LENGTH = "Field length is invalid";
    public static final String INVALID_FIELD_VALUE = "Field value is invalid";

    // Payment-related errors
    public static final String PAYMENT_FAILED = "Payment processing failed";
    public static final String INVALID_PAYMENT_METHOD = "Invalid payment method";
    public static final String PAYMENT_ALREADY_PROCESSED = "Payment already processed";

    // General errors
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String SERVICE_UNAVAILABLE = "Service temporarily unavailable";
    public static final String BAD_REQUEST = "Bad request";
    public static final String METHOD_NOT_ALLOWED = "HTTP method not allowed";
    public static final String UNSUPPORTED_MEDIA_TYPE = "Unsupported media type";

    // Success messages
    public static final String USER_CREATED_SUCCESSFULLY = "User created successfully";
    public static final String USER_UPDATED_SUCCESSFULLY = "User updated successfully";
    public static final String PRODUCT_CREATED_SUCCESSFULLY = "Product created successfully";
    public static final String PRODUCT_UPDATED_SUCCESSFULLY = "Product updated successfully";
    public static final String ORDER_PLACED_SUCCESSFULLY = "Order placed successfully";
    public static final String ORDER_UPDATED_SUCCESSFULLY = "Order updated successfully";
    public static final String LOGIN_SUCCESSFUL = "Login successful";
    public static final String LOGOUT_SUCCESSFUL = "Logout successful";

    // Private constructor to prevent instantiation
    private ExceptionConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
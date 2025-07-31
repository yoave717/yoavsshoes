## JsonView Usage Guide

This guide explains how to use Jackson `@JsonView` annotations to provide different levels of detail in your API responses.

### Overview

We have implemented three levels of detail using JsonView:

1. **Summary View** (`Views.Summary.class`) - Basic information only, relations show only IDs
2. **Detailed View** (`Views.Detailed.class`) - Includes all summary fields plus complete relation details
3. **Admin View** (`Views.Admin.class`) - Includes all detailed fields plus sensitive/audit information

### JsonView Classes

```java
public class Views {
    public static class Summary {}
    public static class Detailed extends Summary {}
    public static class Admin extends Detailed {}
}
```

### How to Use in Controllers

To use JsonView in your controllers, add the `@JsonView` annotation to your endpoint methods:

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // Summary view - returns basic order info
    @GetMapping("/summary")
    @JsonView(Views.Summary.class)
    public ResponseEntity<List<OrderDto>> getOrdersSummary() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // Detailed view - returns complete order info with relations
    @GetMapping("/detailed")
    @JsonView(Views.Detailed.class)
    public ResponseEntity<List<OrderDto>> getOrdersDetailed() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // Admin view - includes audit fields
    @GetMapping("/admin")
    @JsonView(Views.Admin.class)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDto>> getOrdersAdmin() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}
```

### DTO Field Annotations

Each DTO field is annotated with the appropriate view level:

#### OrderDto Example:
```java
public class OrderDto {
    @JsonView(Views.Summary.class)
    private Long id;
    
    @JsonView(Views.Summary.class)
    private String orderNumber;
    
    @JsonView(Views.Summary.class)
    private OrderStatus status;
    
    @JsonView(Views.Summary.class)
    private BigDecimal totalAmount;
    
    @JsonView(Views.Summary.class)
    private LocalDateTime orderDate;
    
    @JsonView(Views.Detailed.class)
    private OrderUserInfoDto user;
    
    @JsonView(Views.Detailed.class)
    private List<OrderItemDto> orderItems;
    
    @JsonView(Views.Admin.class)
    private LocalDateTime createdAt;
    
    @JsonView(Views.Admin.class)
    private LocalDateTime updatedAt;
}
```

### Response Examples

#### Summary View Response:
```json
{
  "id": 1,
  "orderNumber": "ORD-2024-001",
  "status": "CONFIRMED",
  "totalAmount": 299.99,
  "orderDate": "2024-01-15T10:30:00"
}
```

#### Detailed View Response:
```json
{
  "id": 1,
  "orderNumber": "ORD-2024-001",
  "status": "CONFIRMED",
  "totalAmount": 299.99,
  "orderDate": "2024-01-15T10:30:00",
  "user": {
    "id": 123,
    "fullName": "John Doe",
    "email": "john@example.com"
  },
  "orderItems": [
    {
      "id": 1,
      "size": "9",
      "quantity": 1,
      "unitPrice": 299.99,
      "shoeModel": {
        "id": 45,
        "modelName": "Air Max 90",
        "color": "White/Black"
      }
    }
  ]
}
```

#### Admin View Response:
```json
{
  "id": 1,
  "orderNumber": "ORD-2024-001",
  "status": "CONFIRMED",
  "totalAmount": 299.99,
  "orderDate": "2024-01-15T10:30:00",
  "user": { /* full user details */ },
  "orderItems": [ /* full order items */ ],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### Updated DTOs with JsonView

The following DTOs have been updated with JsonView annotations:

1. **OrderDto** - Order information with summary/detailed/admin views
2. **OrderUserInfoDto** - User information in orders (ID in summary, full details in detailed)
3. **OrderItemDto** - Order items (basic info in summary, full shoe model in detailed)
4. **OrderAddressInfoDto** - Address information (ID in summary, full address in detailed)
5. **ShoeModelResponse** - Shoe model data (basic fields in summary, relations in detailed)
6. **ShoeInfo** - Shoe information (basic fields in summary, brand/category in detailed)
7. **BrandInfoDto** - Brand information (ID in summary, name/logo in detailed)
8. **CategoryInfo** - Category information (ID in summary, name in detailed)
9. **UserAddressResponse** - User addresses (basic address in summary, full details in detailed)

### Best Practices

1. **Use Summary View for Lists** - When displaying lists of items, use summary view to reduce payload size
2. **Use Detailed View for Single Items** - When showing a single item's details, use detailed view
3. **Use Admin View Sparingly** - Only for administrative functions that need audit information
4. **Consistent Field Placement** - Always put IDs in Summary, relations in Detailed, audit fields in Admin
5. **Security Considerations** - Use `@PreAuthorize` with Admin view to ensure proper access control

### Migration Strategy

To migrate existing endpoints:

1. **Identify the current response level** - Is it basic info or detailed?
2. **Add JsonView annotation** - Use `@JsonView(Views.Summary.class)` or `@JsonView(Views.Detailed.class)`
3. **Create alternative endpoints** - Add `/summary` and `/detailed` variants if needed
4. **Update frontend** - Modify frontend calls to use appropriate endpoints
5. **Deprecate old endpoints** - Gradually phase out endpoints without view control

### Example Controller Modifications

```java
// Before - single endpoint with all data
@GetMapping
public ResponseEntity<List<OrderDto>> getOrders() {
    return ResponseEntity.ok(orderService.getAllOrders());
}

// After - multiple endpoints with different detail levels
@GetMapping("/summary")
@JsonView(Views.Summary.class)
public ResponseEntity<List<OrderDto>> getOrdersSummary() {
    return ResponseEntity.ok(orderService.getAllOrders());
}

@GetMapping("/detailed")
@JsonView(Views.Detailed.class)
public ResponseEntity<List<OrderDto>> getOrdersDetailed() {
    return ResponseEntity.ok(orderService.getAllOrders());
}
```

This approach allows you to use a single DTO class for each entity while providing different levels of detail based on the client's needs, reducing both the number of DTO classes and network payload sizes.

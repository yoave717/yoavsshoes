package com.shoestore.dto.view;

/**
 * Jackson JsonView definitions for controlling serialization depth
 */
public class Views {
    
    /**
     * Summary view - includes basic data members and only IDs for relations
     */
    public static class Summary {}
    
    /**
     * Detailed view - includes all relations with full data
     */
    public static class Detailed extends Summary {}
    
    /**
     * Admin view - includes all data including sensitive information
     */
    public static class Admin extends Detailed {}
    
}

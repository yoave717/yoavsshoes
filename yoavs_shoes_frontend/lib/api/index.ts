
// Re-export all API functions from a central location
export * from './auth';
export * from './shoes';
export * from './products';
export * from './address';
export * from './orders';
export * from './brands';
export * from './categories'

// Export the API client
export { default as apiClient } from './client';


// export as object for deconstruction

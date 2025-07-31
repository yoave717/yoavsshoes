
// Re-export all API functions from a central location
export * from './auth';
export * from './shoes';
export * from './products';
export * from './address';
export * from './orders';

// Export the API client
export { default as apiClient } from './client';


// export as object for deconstruction

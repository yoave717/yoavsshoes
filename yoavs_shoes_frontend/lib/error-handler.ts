export interface ApiError {
  message: string;
  status?: number;
  code?: string;
}

export const handleApiError = (error: unknown): string => {
  // Type guard to check if error is an axios error
  if (typeof error === 'object' && error !== null && 'response' in error) {
    const axiosError = error as {
      response: { status: number; data?: { message?: string; error?: string } };
    };
    // Server responded with error status
    const status = axiosError.response.status;
    const message =
      axiosError.response.data?.message ||
      axiosError.response.data?.error ||
      'An error occurred';

    switch (status) {
      case 400:
        return `Bad Request: ${message}`;
      case 401:
        return 'Unauthorized: Please check your credentials';
      case 403:
        return 'Forbidden: You do not have permission to perform this action';
      case 404:
        return 'Not Found: The requested resource was not found';
      case 500:
        return 'Server Error: Please try again later';
      default:
        return message;
    }
  } else if (
    typeof error === 'object' &&
    error !== null &&
    'request' in error
  ) {
    // Network error
    return 'Network Error: Please check your internet connection';
  } else if (error instanceof Error) {
    // Other error
    return error.message;
  } else {
    return 'An unexpected error occurred';
  }
};

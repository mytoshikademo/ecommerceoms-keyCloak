import apiClient from "./apiClient";

/**
 * POST /api/v1/products/{productId}/reviews (CUSTOMER only)
 * body: { rating: 1-5, comment, metadata? }
 * NOTE: unlike every other endpoint, the response is a bare ReviewResponse,
 * NOT wrapped in the { jsonId, success, message, data } APIResponse envelope.
 * response.data -> ReviewResponse { id, productId, keycloakUserId, rating, comment, metadata, createdAt }
 */
export function createReview(productId, reviewRequest) {
  return apiClient.post(`/products/${productId}/reviews`, reviewRequest);
}

/**
 * GET /api/v1/products/{productId}/reviews?page&size (public, no auth)
 * response.data -> bare page object, NOT wrapped in the APIResponse
 * envelope: { totalElements, totalPages, size, number, content: ReviewResponse[] }
 */
export function getProductReviews(productId, { page, size }) {
  return apiClient.get(`/products/reviews`, { params: { productId, page, size } });
}
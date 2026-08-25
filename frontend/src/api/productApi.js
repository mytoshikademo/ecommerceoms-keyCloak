import apiClient from "./apiClient";

/**
 * GET /api/v1/products (public, no auth)
 * params: page, size, sortBy, direction, keyword?, minPrice?, maxPrice?, category?
 * response.data.data -> Page<ProductResponse> { id, name, price, availableQuantity, category }
 * NOTE: list items do NOT include description — use getProductById for that.
 */
export function getProducts(params) {
  return apiClient.get("/products", { params });
}

/**
 * GET /api/v1/products/{id} (public, no auth)
 * response.data.data -> ProductDetailsResponse { id, name, price, description, availableQuantity, category }
 */
export function getProductById(id) {
  return apiClient.get(`/products/${id}`);
}

/**
 * POST /api/v1/admin/products (ADMIN only)
 * IMPORTANT: this endpoint expects the request wrapped in an APIRequest envelope:
 *   { data: { name, description, price, availableQuantity } }
 * unlike updateProduct below, which takes the plain object. This asymmetry is
 * in the actual backend code, not a frontend inconsistency.
 * response.data.data -> ProductResponse
 */
export function createProduct(productRequest) {
  return apiClient.post("/admin/products", { data: productRequest });
}

/**
 * PUT /api/v1/admin/products/{id} (ADMIN only)
 * body: plain ProductRequest { name, description, price, availableQuantity } — NOT wrapped.
 * response.data.data -> ProductResponse
 */
export function updateProduct(id, productRequest) {
  return apiClient.put(`/admin/products/${id}`, productRequest);
}

/**
 * DELETE /api/v1/admin/products/{id} (ADMIN only) — soft delete.
 * response.data.data is null; response.data.message has the confirmation text.
 */
export function deleteProduct(id) {
  return apiClient.delete(`/admin/products/${id}`);
}

/**
 * PATCH /api/v1/admin/products/{id} (ADMIN only) — restore a soft-deleted product.
 * response.data.data -> ProductResponse
 */
export function restoreProduct(id) {
  return apiClient.patch(`/admin/products/${id}`);
}

/**
 * GET /api/v1/admin/products/deleted (ADMIN only)
 * response.data.data -> ProductResponse[]
 */
export function getDeletedProducts() {
  return apiClient.get("/admin/products/deleted");
}
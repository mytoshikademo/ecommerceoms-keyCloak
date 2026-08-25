import apiClient from "./apiClient";

/**
 * POST /api/v1/users/register
 * body: { name, email, password }
 * response.data.data -> UserResponse { id, name, email, role[] }
 */
export function registerUser(payload) {
  return apiClient.post("/users/register", payload);
}

/**
 * POST /api/v1/auth/login
 * body: { email, password }
 * response.data.data -> LoginResponse { accessToken, refreshToken, tokenType, expiresIn }
 */
export function loginUser(payload) {
  return apiClient.post("/auth/login", payload);
}

/**
 * GET /api/v1/users/me
 * Requires bearer token. Backend-side addition (not in original controller scan) —
 * returns the logged-in user's own profile without needing to know their internal ID.
 * response.data.data -> UserResponse { id, name, email, role[] }
 */
export function getMyProfile() {
  return apiClient.get("/users/me");
}

/**
 * GET /api/v1/users/{id}
 * Requires bearer token. Backend enforces self-or-ADMIN only;
 * a mismatch throws a custom AccessDeniedException that currently
 * surfaces as a generic 500, not a 403 — handle accordingly at call sites.
 * response.data.data -> UserResponse { id, name, email, role[] }
 */
export function getUserById(id) {
  return apiClient.get(`/users/${id}`);
}

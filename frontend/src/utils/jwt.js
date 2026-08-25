import { getAccessToken } from "./tokenStorage.js";

/**
 * Decodes the `sub` claim (the Keycloak user ID) from the currently stored
 * access token, without any verification — this is only ever used to match
 * "is this review mine" for display purposes, never for anything
 * security-relevant (the backend remains the sole authority on identity).
 * Returns null on any failure (missing token, malformed JWT, etc.) rather
 * than throwing, since this is a best-effort UI nicety.
 */
export function getCurrentUserKeycloakId() {
    const token = getAccessToken();
    if (!token) return null;

    try {
        const payloadSegment = token.split(".")[1];
        const base64 = payloadSegment.replace(/-/g, "+").replace(/_/g, "/");
        const json = atob(base64);
        const payload = JSON.parse(json);
        return payload.sub ?? null;
    } catch {
        return null;
    }
}
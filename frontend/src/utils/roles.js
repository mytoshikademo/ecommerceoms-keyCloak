/**
 * Normalizes a role string so comparisons work whether the backend sends
 * plain values ("ADMIN") or Spring-Security-style authorities
 * ("ROLE_ADMIN"), and regardless of case.
 */
function normalizeRole(role) {
    return String(role ?? "")
        .replace(/^ROLE_/i, "")
        .toUpperCase();
}

/**
 * True if any of `userRoles` matches any of `requiredRoles`, after
 * normalizing both sides.
 */
export function hasAnyRole(userRoles, requiredRoles) {
    const normalizedUserRoles = (userRoles ?? []).map(normalizeRole);
    const normalizedRequired = (requiredRoles ?? []).map(normalizeRole);
    return normalizedUserRoles.some((r) => normalizedRequired.includes(r));
}
// This app authenticates via a direct password grant (frontend -> our
// backend -> Keycloak's token endpoint) and never redirects to Keycloak's
// hosted login UI for that flow. Keycloak's "send password reset email"
// action, however, has no public unauthenticated REST endpoint — it's only
// reachable via Keycloak's own hosted page, or the Admin REST API (which
// requires admin credentials and must stay server-side). Since a backend
// change isn't wanted here, the only zero-backend option is to send the
// browser to Keycloak's own reset-credentials page, prefilled with the
// email the user typed. Keycloak sends the email and owns the entire
// reset UI/flow from that point on.
const KEYCLOAK_URL = import.meta.env.VITE_KEYCLOAK_URL;
const KEYCLOAK_REALM = import.meta.env.VITE_KEYCLOAK_REALM;
const KEYCLOAK_CLIENT_ID = import.meta.env.VITE_KEYCLOAK_CLIENT_ID;

/**
 * Builds the URL for Keycloak's hosted "forgot password" page.
 * `redirectUri` must be registered under this client's Valid Redirect URIs
 * in the Keycloak admin console, or Keycloak will refuse the request.
 */
export function buildKeycloakResetPasswordUrl(email, redirectUri) {
    if (!KEYCLOAK_URL || !KEYCLOAK_REALM || !KEYCLOAK_CLIENT_ID) {
        // eslint-disable-next-line no-console
        console.error(
            "Keycloak env vars are not set. Check VITE_KEYCLOAK_URL, VITE_KEYCLOAK_REALM, VITE_KEYCLOAK_CLIENT_ID in .env."
        );
    }

    const params = new URLSearchParams({
        client_id: KEYCLOAK_CLIENT_ID ?? "",
        redirect_uri: redirectUri,
    });

    if (email) {
        // Prefills the email/username field on Keycloak's page, when supported
        // by the realm's login theme. Harmless no-op if unsupported.
        params.set("login_hint", email);
    }

    return `${KEYCLOAK_URL}/realms/${KEYCLOAK_REALM}/login-actions/reset-credentials?${params.toString()}`;
}
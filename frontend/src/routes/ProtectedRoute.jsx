import { useEffect } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { useToast } from "../context/ToastContext.jsx";
import { LoadingState } from "../components/ui/StatusStates.jsx";

/**
 * Route guard. Wrap any element that requires:
 * - being logged in (default), and/or
 * - having one of `roles` (e.g. roles={["ADMIN"]}).
 *
 * Frontend authorization here is UX only — the backend remains the
 * authoritative enforcement point for every request.
 */
function ProtectedRoute({ children, roles }) {
    const { isAuthenticated, loading, user } = useAuth();
    const { showToast } = useToast();
    const location = useLocation();

    const hasRequiredRole =
        !roles || (user?.role ?? []).some((r) => roles.includes(r));

    useEffect(() => {
        if (!loading && isAuthenticated && !hasRequiredRole) {
            showToast("You don't have permission to view that page.", "error");
        }
    }, [loading, isAuthenticated, hasRequiredRole, showToast]);

    // Session restore (checking an existing token) still in progress —
    // don't redirect yet, or a valid logged-in user gets briefly bounced.
    if (loading) {
        return <LoadingState label="Checking your session..." />;
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    if (!hasRequiredRole) {
        return <Navigate to="/" replace />;
    }

    return children;
}

export default ProtectedRoute;
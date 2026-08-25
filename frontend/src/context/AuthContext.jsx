import { createContext, useContext, useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser, registerUser, getMyProfile } from "../api/authApi";
import { useToast } from "./ToastContext.jsx";
import { getAccessToken, setTokens, clearTokens } from "../utils/tokenStorage.js";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null); // UserResponse { id, name, email, role[] }
    const [loading, setLoading] = useState(true); // true while checking an existing session on load
    const navigate = useNavigate();
    const { showToast } = useToast();

    const clearSession = useCallback(() => {
        clearTokens();
        setUser(null);
    }, []);

    // On mount: if a token exists from a previous visit, fetch the profile to
    // restore the session; otherwise finish loading immediately.
    useEffect(() => {
        const token = getAccessToken();
        if (!token) {
            setLoading(false);
            return;
        }
        getMyProfile()
            .then((res) => setUser(res.data.data))
            .catch(() => clearSession())
            .finally(() => setLoading(false));
    }, [clearSession]);

    // React to 401s from any API call (dispatched by apiClient's interceptor).
    useEffect(() => {
        function handleUnauthorized() {
            const hadSession = !!getAccessToken();
            clearSession();
            if (hadSession) {
                showToast("Your session has expired. Please log in again.", "error");
                navigate("/login");
            }
        }
        window.addEventListener("auth:unauthorized", handleUnauthorized);
        return () => window.removeEventListener("auth:unauthorized", handleUnauthorized);
    }, [clearSession, navigate, showToast]);

    async function login(email, password, rememberMe = true) {
        let loginRes;
        try {
            loginRes = await loginUser({ email, password });
        } catch {
            // The backend's Keycloak-backed login path doesn't currently
            // distinguish bad credentials from other failures at the HTTP layer,
            // so this is the one specific, accurate message for this call site.
            throw new Error("Invalid email or password.");
        }

        const { accessToken, refreshToken } = loginRes.data.data;
        setTokens(accessToken, refreshToken, rememberMe);

        try {
            const profileRes = await getMyProfile();
            setUser(profileRes.data.data);
        } catch {
            clearSession();
            throw new Error("Logged in, but couldn't load your profile. Please try again.");
        }
    }

    async function register({ name, email, password }) {
        try {
            await registerUser({ name, email, password });
        } catch (err) {
            const status = err.response?.status;
            if (status === 409) {
                throw new Error("An account with this email already exists.");
            }
            if (status === 400) {
                // Backend returns a clean, pre-joined validation message for 400s.
                throw new Error(err.response?.data?.message || "Please check your details and try again.");
            }
            throw new Error("Registration failed. Please try again.");
        }
    }

    function logout() {
        clearSession();
        navigate("/login");
    }

    const value = {
        user,
        loading,
        isAuthenticated: !!user,
        login,
        register,
        logout,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth must be used within AuthProvider");
    return ctx;
}
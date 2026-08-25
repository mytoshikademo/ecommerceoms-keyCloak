import { useEffect, useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { useToast } from "../context/ToastContext.jsx";

function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [rememberMe, setRememberMe] = useState(true);
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const { login } = useAuth();
    const { showToast } = useToast();
    const navigate = useNavigate();
    const location = useLocation();

    // Landing here after Keycloak's hosted password-reset flow: this is the
    // page we registered as Keycloak's redirect_uri (see ForgotPasswordPage).
    // We don't perform an OAuth code exchange here — that would need backend
    // + client secret involvement, which was explicitly out of scope. This
    // just recognizes the return trip (via the query params Keycloak appends)
    // and gives a clean landing + a clean URL.
    useEffect(() => {
        const params = new URLSearchParams(location.search);
        if (params.has("code") || params.has("session_state")) {
            showToast("You can now log in with your new password.", "success");
            navigate("/login", { replace: true });
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setSubmitting(true);
        try {
            await login(email, password, rememberMe);
            showToast("Welcome back!", "success");
            const redirectTo = location.state?.from?.pathname ?? "/";
            navigate(redirectTo, { replace: true });
        } catch (err) {
            setError(err.message);
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <div className="min-h-screen bg-bg text-text-primary flex items-center justify-center px-4">
            <div className="bg-surface border border-border rounded-lg p-8 max-w-sm w-full">
                <h1 className="text-xl font-semibold mb-1">Sign in</h1>
                <p className="text-text-secondary text-sm mb-6">
                    Welcome back to the OMS dashboard.
                </p>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label htmlFor="email" className="block text-sm font-medium mb-1">
                            Email
                        </label>
                        <input
                            id="email"
                            type="email"
                            required
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="w-full rounded-md border border-border bg-bg text-text-primary text-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
                            placeholder="you@example.com"
                        />
                    </div>

                    <div>
                        <div className="flex items-center justify-between mb-1">
                            <label htmlFor="password" className="block text-sm font-medium">
                                Password
                            </label>
                            <Link
                                to="/forgot-password"
                                className="text-xs text-accent hover:text-accent-hover"
                            >
                                Forgot password?
                            </Link>
                        </div>
                        <input
                            id="password"
                            type="password"
                            required
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full rounded-md border border-border bg-bg text-text-primary text-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
                            placeholder="••••••••"
                        />
                    </div>

                    {error && (
                        <p className="text-danger text-sm" role="alert">
                            {error}
                        </p>
                    )}

                    <label className="flex items-center gap-2 text-sm text-text-secondary cursor-pointer select-none">
                        <input
                            type="checkbox"
                            checked={rememberMe}
                            onChange={(e) => setRememberMe(e.target.checked)}
                            className="w-4 h-4 rounded border-border text-accent focus:ring-accent focus:ring-offset-0 accent-accent"
                        />
                        Remember me
                    </label>

                    <button
                        type="submit"
                        disabled={submitting}
                        className="w-full bg-accent hover:bg-accent-hover disabled:opacity-60 text-white text-sm font-medium py-2 rounded-md transition-colors"
                    >
                        {submitting ? "Signing in..." : "Sign in"}
                    </button>
                </form>

                <p className="text-text-secondary text-sm mt-5 text-center">
                    Don't have an account?{" "}
                    <Link to="/register" className="text-accent hover:text-accent-hover">
                        Register
                    </Link>
                </p>
            </div>
        </div>
    );
}

export default LoginPage;
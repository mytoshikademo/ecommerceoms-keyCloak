import { useState } from "react";
import { Link } from "react-router-dom";
import { buildKeycloakResetPasswordUrl } from "../utils/keycloakAuth.js";

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function ForgotPasswordPage() {
    const [email, setEmail] = useState("");
    const [error, setError] = useState("");
    const [redirecting, setRedirecting] = useState(false);

    function handleSubmit(e) {
        e.preventDefault();
        setError("");

        if (!email.trim()) {
            setError("Please enter your email address.");
            return;
        }
        if (!EMAIL_PATTERN.test(email.trim())) {
            setError("Please enter a valid email address.");
            return;
        }

        setRedirecting(true);
        const redirectUri = `${window.location.origin}/login`;
        const resetUrl = buildKeycloakResetPasswordUrl(email.trim(), redirectUri);

        // Full browser navigation, not an API call — Keycloak's own hosted
        // page takes over from here, sends the email, and handles the reset.
        window.location.href = resetUrl;
    }

    return (
        <div className="min-h-screen bg-bg text-text-primary flex items-center justify-center px-4">
            <div className="bg-surface border border-border rounded-lg p-8 max-w-sm w-full">
                <h1 className="text-xl font-semibold mb-1">Reset your password</h1>
                <p className="text-text-secondary text-sm mb-6">
                    Enter your email and we'll take you to a secure page to reset your
                    password.
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

                    {error && (
                        <p className="text-danger text-sm" role="alert">
                            {error}
                        </p>
                    )}

                    {redirecting && (
                        <p className="text-text-secondary text-sm" role="status">
                            Redirecting you to a secure page to complete the reset...
                        </p>
                    )}

                    <button
                        type="submit"
                        disabled={redirecting}
                        className="w-full bg-accent hover:bg-accent-hover disabled:opacity-60 text-white text-sm font-medium py-2 rounded-md transition-colors"
                    >
                        {redirecting ? "Redirecting..." : "Send Reset Link"}
                    </button>
                </form>

                <p className="text-text-secondary text-sm mt-5 text-center">
                    Remembered your password?{" "}
                    <Link to="/login" className="text-accent hover:text-accent-hover">
                        Back to sign in
                    </Link>
                </p>
            </div>
        </div>
    );
}

export default ForgotPasswordPage;
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { useToast } from "../context/ToastContext.jsx";

// Mirrors the backend's UserRequest @Pattern constraint so obviously invalid
// passwords are caught before a round trip: min 8 chars, 2+ digits, one
// uppercase, one lowercase, one special char from @#$%^&+=
const PASSWORD_PATTERN = /^(?=.*[0-9]{2,})(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=]).{8,}$/;

function RegisterPage() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const { register } = useAuth();
    const { showToast } = useToast();
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");

        if (!PASSWORD_PATTERN.test(password)) {
            setError(
                "Password must be at least 8 characters and include an uppercase letter, a lowercase letter, 2 numbers, and a special character (@#$%^&+=)."
            );
            return;
        }

        setSubmitting(true);
        try {
            await register({ name, email, password });
            showToast("Account created. Please sign in.", "success");
            navigate("/login");
        } catch (err) {
            setError(err.message);
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <div className="min-h-screen bg-bg text-text-primary flex items-center justify-center px-4">
            <div className="bg-surface border border-border rounded-lg p-8 max-w-sm w-full">
                <h1 className="text-xl font-semibold mb-1">Create an account</h1>
                <p className="text-text-secondary text-sm mb-6">
                    Register to start shopping and reviewing products.
                </p>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label htmlFor="name" className="block text-sm font-medium mb-1">
                            Full name
                        </label>
                        <input
                            id="name"
                            type="text"
                            required
                            minLength={2}
                            maxLength={100}
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            className="w-full rounded-md border border-border bg-bg text-text-primary text-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
                            placeholder="Jane Doe"
                        />
                    </div>

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
                        <label htmlFor="password" className="block text-sm font-medium mb-1">
                            Password
                        </label>
                        <input
                            id="password"
                            type="password"
                            required
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full rounded-md border border-border bg-bg text-text-primary text-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
                            placeholder="••••••••"
                        />
                        <p className="text-text-secondary text-xs mt-1">
                            8+ characters, uppercase, lowercase, 2 numbers, and a special character (@#$%^&+=).
                        </p>
                    </div>

                    {error && (
                        <p className="text-danger text-sm" role="alert">
                            {error}
                        </p>
                    )}

                    <button
                        type="submit"
                        disabled={submitting}
                        className="w-full bg-accent hover:bg-accent-hover disabled:opacity-60 text-white text-sm font-medium py-2 rounded-md transition-colors"
                    >
                        {submitting ? "Creating account..." : "Create account"}
                    </button>
                </form>

                <p className="text-text-secondary text-sm mt-5 text-center">
                    Already have an account?{" "}
                    <Link to="/login" className="text-accent hover:text-accent-hover">
                        Sign in
                    </Link>
                </p>
            </div>
        </div>
    );
}

export default RegisterPage;

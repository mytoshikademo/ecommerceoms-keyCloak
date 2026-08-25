import { Moon, Sun, LogOut } from "lucide-react";
import { useTheme } from "../../context/ThemeContext.jsx";
import { useAuth } from "../../context/AuthContext.jsx";

function Header() {
    const { theme, toggleTheme } = useTheme();
    const { user, logout } = useAuth();

    const initials = user?.name
        ? user.name
            .split(" ")
            .map((part) => part[0])
            .join("")
            .slice(0, 2)
            .toUpperCase()
        : "?";

    return (
        <header className="h-14 border-b border-border bg-surface flex items-center justify-between px-4 shrink-0">
            <div className="text-sm text-text-secondary">
                {/* Breadcrumb placeholder — will reflect the active route in a later phase */}
                Dashboard
            </div>

            <div className="flex items-center gap-3">
                <button
                    type="button"
                    onClick={toggleTheme}
                    aria-label={theme === "dark" ? "Switch to light theme" : "Switch to dark theme"}
                    className="w-8 h-8 flex items-center justify-center rounded-md border border-border text-text-secondary hover:text-accent-hover hover:border-accent-hover transition-colors"
                >
                    {theme === "dark" ? <Sun size={16} /> : <Moon size={16} />}
                </button>

                {user ? (
                    <div className="flex items-center gap-2">
                        <div
                            className="w-8 h-8 rounded-full bg-bg border border-border flex items-center justify-center text-xs font-medium text-text-primary"
                            title={user.email}
                        >
                            {initials}
                        </div>
                        <button
                            type="button"
                            onClick={logout}
                            aria-label="Log out"
                            className="w-8 h-8 flex items-center justify-center rounded-md border border-border text-text-secondary hover:text-danger hover:border-danger transition-colors"
                        >
                            <LogOut size={16} />
                        </button>
                    </div>
                ) : (
                    <div className="w-8 h-8 rounded-full bg-bg border border-border flex items-center justify-center text-xs font-medium text-text-primary">
                        ?
                    </div>
                )}
            </div>
        </header>
    );
}

export default Header;
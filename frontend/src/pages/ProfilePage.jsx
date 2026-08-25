import { LogOut, Mail, ShieldCheck, User as UserIcon } from "lucide-react";
import { useAuth } from "../context/AuthContext.jsx";

function ProfilePage() {
    const { user, logout } = useAuth();

    // ProtectedRoute guarantees isAuthenticated (and therefore `user`) is set
    // before this renders, but guard defensively in case of a race on logout.
    if (!user) return null;

    const initials = user.name
        ? user.name
            .split(" ")
            .map((part) => part[0])
            .join("")
            .slice(0, 2)
            .toUpperCase()
        : "?";

    return (
        <div className="max-w-md">
            <h1 className="text-lg font-semibold text-text-primary mb-4">My Profile</h1>

            <div className="bg-surface border border-border rounded-lg p-6">
                <div className="flex items-center gap-3 mb-5">
                    <div className="w-12 h-12 rounded-full bg-bg border border-border flex items-center justify-center text-sm font-medium text-text-primary">
                        {initials}
                    </div>
                    <div>
                        <p className="text-text-primary font-medium">{user.name}</p>
                        <div className="flex gap-1.5 mt-1">
                            {user.userId && (
                                <span className="inline-flex items-center gap-1 text-xs font-medium px-2 py-0.5 rounded-full bg-bg border border-border text-text-secondary">
                  <ShieldCheck size={11} />
                                    {user.userId}
                </span>
                            )}
                        </div>
                    </div>
                </div>

                <dl className="space-y-3 text-sm border-t border-border pt-4">
                    <div className="flex items-center gap-2">
                        <UserIcon size={14} className="text-text-secondary shrink-0" />
                        <dt className="text-text-secondary w-16 shrink-0">Name</dt>
                        <dd className="text-text-primary">{user.name}</dd>
                    </div>
                    <div className="flex items-center gap-2">
                        <Mail size={14} className="text-text-secondary shrink-0" />
                        <dt className="text-text-secondary w-16 shrink-0">Email</dt>
                        <dd className="text-text-primary">{user.email}</dd>
                    </div>
                </dl>

                <button
                    type="button"
                    onClick={logout}
                    className="mt-6 inline-flex items-center gap-1.5 text-sm font-medium text-danger hover:opacity-80 transition-opacity"
                >
                    <LogOut size={14} />
                    Log out
                </button>
            </div>
        </div>
    );
}

export default ProfilePage;
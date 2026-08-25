import { NavLink } from "react-router-dom";
import {
    Package,
    User,
    ShieldCheck,
    Trash2,
    ShoppingCart,
    CreditCard,
    ClipboardList,
    Wallet,
} from "lucide-react";
import { useAuth } from "../../context/AuthContext.jsx";
import { hasAnyRole } from "../../utils/roles.js";
import Tooltip from "../ui/Tooltip.jsx";

// `end: true` on every item is required: React Router's NavLink does prefix
// matching by default, so "/admin/products" would otherwise also match
// "/admin/products/deleted" and stay active on that page too.
const NAV_ITEMS = [
    { to: "/", label: "Products", icon: Package, end: true },
    { to: "/profile", label: "My Profile", icon: User, end: true },
    {
        to: "/admin/products",
        label: "Manage Products",
        icon: ShieldCheck,
        end: true,
        roles: ["ADMIN"],
    },
    {
        to: "/admin/products/deleted",
        label: "Deleted Products",
        icon: Trash2,
        end: true,
        roles: ["ADMIN"],
    },
];

// No backend API exists for any of these yet (see claude.md "Missing
// Backend Features"). They render visibly disabled, make no network
// requests, and cannot be clicked into anywhere — only a "Not Implemented"
// tooltip on hover/focus.
const DISABLED_NAV_ITEMS = [
    { label: "Cart", icon: ShoppingCart },
    { label: "Checkout", icon: CreditCard },
    { label: "Orders", icon: ClipboardList },
    { label: "Payments", icon: Wallet },
];

function Sidebar() {
    const { user } = useAuth();

    const visibleItems = NAV_ITEMS.filter(
        (item) => !item.roles || hasAnyRole(user?.role, item.roles)
    );

    return (
        <aside className="w-56 shrink-0 border-r border-border bg-surface flex flex-col">
            <div className="h-14 flex items-center px-4 border-b border-border">
        <span className="font-semibold text-text-primary tracking-tight">
          OMS
        </span>
            </div>

            <nav className="flex-1 py-3">
                {visibleItems.map(({ to, label, icon: Icon, end }) => (
                    <NavLink
                        key={to}
                        to={to}
                        end={end}
                        className={({ isActive }) =>
                            `group w-full flex items-center gap-2.5 px-4 py-2 text-sm border-l-2 transition-colors ${
                                isActive
                                    ? "border-l-accent bg-active"
                                    : "border-l-transparent hover:bg-bg"
                            }`
                        }
                    >
                        {({ isActive }) => (
                            <>
                                <Icon
                                    size={16}
                                    className={
                                        isActive
                                            ? "text-accent dark:text-accent-light"
                                            : "text-text-secondary group-hover:text-text-primary"
                                    }
                                />
                                <span
                                    className={
                                        isActive
                                            ? "text-text-primary dark:text-white font-medium"
                                            : "text-text-secondary group-hover:text-text-primary"
                                    }
                                >
                  {label}
                </span>
                            </>
                        )}
                    </NavLink>
                ))}

                <p className="px-4 pt-4 pb-1.5 text-[10px] font-semibold tracking-wide uppercase text-text-secondary">
                    Coming Soon
                </p>
                {DISABLED_NAV_ITEMS.map(({ label, icon: Icon }) => (
                    <Tooltip key={label} label="Not implemented" side="right" className="w-full">
                        <div
                            tabIndex={0}
                            role="button"
                            aria-disabled="false"
                            className="w-full flex items-center gap-2.5 px-4 py-2 text-sm border-l-2 border-l-transparent text-text-secondary opacity-50 cursor-not-allowed select-none"
                        >
                            <Icon size={16} />
                            <span>{label}</span>
                        </div>
                    </Tooltip>
                ))}
            </nav>
        </aside>
    );
}

export default Sidebar;
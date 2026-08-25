import { Routes, Route } from "react-router-dom";
import AppShell from "../components/layout/AppShell.jsx";
import PlaceholderPage from "../pages/PlaceholderPage.jsx";
import LoginPage from "../pages/LoginPage.jsx";
import RegisterPage from "../pages/RegisterPage.jsx";
import ForgotPasswordPage from "../pages/ForgotPasswordPage.jsx";
import ProductListPage from "../pages/ProductListPage.jsx";
import ProductDetailsPage from "../pages/ProductDetailsPage.jsx";
import AdminProductsPage from "../pages/AdminProductsPage.jsx";
import AdminDeletedProductsPage from "../pages/AdminDeletedProductsPage.jsx";
import ProfilePage from "../pages/ProfilePage.jsx";
import ProtectedRoute from "./ProtectedRoute.jsx";

/**
 * Product browsing ("/", "/products/:id") is public, matching the backend's
 * permitAll rules — guests can view the catalogue without logging in.
 * "/profile" requires any authenticated user; the admin routes require the
 * ADMIN role. Auth pages (login/register) and 404 render standalone,
 * outside AppShell.
 */
function AppRoutes() {
    return (
        <Routes>
            <Route element={<AppShell />}>
                <Route path="/" element={<ProductListPage />} />
                <Route path="/products/:id" element={<ProductDetailsPage />} />
                <Route
                    path="/profile"
                    element={
                        <ProtectedRoute>
                            <ProfilePage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/products"
                    element={
                        <ProtectedRoute roles={["ADMIN"]}>
                            <AdminProductsPage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/products/deleted"
                    element={
                        <ProtectedRoute roles={["ADMIN"]}>
                            <AdminDeletedProductsPage />
                        </ProtectedRoute>
                    }
                />
            </Route>

            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
            <Route
                path="*"
                element={<PlaceholderPage title="404 — Page Not Found" inShell={false} />}
            />
        </Routes>
    );
}

export default AppRoutes;
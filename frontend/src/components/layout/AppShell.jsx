import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar.jsx";
import Header from "./Header.jsx";

/**
 * Layout route wrapper for all "dashboard" pages (catalogue, product details,
 * profile, admin). Auth pages (login/register) render outside this shell —
 * see AppRoutes.jsx.
 */
function AppShell() {
  return (
    <div className="h-screen flex bg-bg text-text-primary">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0">
        <Header />
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default AppShell;

import { useEffect, useState } from "react";
import { RotateCcw } from "lucide-react";
import { getDeletedProducts, restoreProduct } from "../api/productApi";
import { LoadingState, EmptyState, ErrorState } from "../components/ui/StatusStates.jsx";
import { useToast } from "../context/ToastContext.jsx";

const currencyFormatter = new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
});

function AdminDeletedProductsPage() {
    const { showToast } = useToast();
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [refreshCount, setRefreshCount] = useState(0);
    const [restoringId, setRestoringId] = useState(null);

    useEffect(() => {
        let cancelled = false;
        setLoading(true);
        setError("");

        getDeletedProducts()
            .then((res) => {
                if (!cancelled) setProducts(res.data.data ?? []);
            })
            .catch(() => {
                if (!cancelled) setError("Couldn't load deleted products. Please try again.");
            })
            .finally(() => {
                if (!cancelled) setLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [refreshCount]);

    async function handleRestore(product) {
        setRestoringId(product.id);
        try {
            await restoreProduct(product.id);
            showToast(`"${product.name}" restored.`, "success");
            setRefreshCount((c) => c + 1);
        } catch (err) {
            const status = err.response?.status;
            showToast(
                status === 409
                    ? "This product is already active."
                    : "Couldn't restore the product. Please try again.",
                "error"
            );
        } finally {
            setRestoringId(null);
        }
    }

    return (
        <div>
            <h1 className="text-lg font-semibold text-text-primary mb-4">Deleted Products</h1>

            {loading && <LoadingState label="Loading deleted products..." />}

            {!loading && error && (
                <ErrorState message={error} onRetry={() => setRefreshCount((c) => c + 1)} />
            )}

            {!loading && !error && products.length === 0 && (
                <EmptyState
                    title="No deleted products"
                    description="Products you soft-delete from Manage Products will show up here."
                />
            )}

            {!loading && !error && products.length > 0 && (
                <div className="bg-surface border border-border rounded-lg overflow-hidden">
                    <table className="w-full text-sm">
                        <thead>
                        <tr className="border-b border-border text-left text-text-secondary text-xs">
                            <th className="px-4 py-2.5 font-medium">Name</th>
                            <th className="px-4 py-2.5 font-medium">Price</th>
                            <th className="px-4 py-2.5 font-medium">Quantity</th>
                            <th className="px-4 py-2.5 font-medium text-right">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {products.map((product) => (
                            <tr
                                key={product.id}
                                className="border-b border-border last:border-b-0 border-l-[3px] border-l-danger"
                            >
                                <td className="px-4 py-2.5 text-text-primary">{product.name}</td>
                                <td className="px-4 py-2.5 font-mono text-text-primary">
                                    {currencyFormatter.format(product.price)}
                                </td>
                                <td className="px-4 py-2.5 text-text-secondary">
                                    {product.availableQuantity}
                                </td>
                                <td className="px-4 py-2.5">
                                    <div className="flex justify-end">
                                        <button
                                            type="button"
                                            onClick={() => handleRestore(product)}
                                            disabled={restoringId === product.id}
                                            className="inline-flex items-center gap-1.5 text-xs font-medium border border-border rounded-md px-2.5 py-1.5 text-text-secondary hover:text-success hover:border-success disabled:opacity-60 transition-colors"
                                        >
                                            <RotateCcw size={13} />
                                            {restoringId === product.id ? "Restoring..." : "Restore"}
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

export default AdminDeletedProductsPage;
import { useEffect, useState } from "react";
import { Plus, Pencil, Trash2 } from "lucide-react";
import { getProducts, deleteProduct } from "../api/productApi";
import SearchFilterBar from "../components/ui/SearchFilterBar.jsx";
import Pagination from "../components/ui/Pagination.jsx";
import ConfirmDialog from "../components/ui/ConfirmDialog.jsx";
import ProductFormModal from "../components/products/ProductFormModal.jsx";
import { LoadingState, EmptyState, ErrorState } from "../components/ui/StatusStates.jsx";
import { useToast } from "../context/ToastContext.jsx";

const PAGE_SIZE = 10;
const DEBOUNCE_MS = 400;

const currencyFormatter = new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
});

function AdminProductsPage() {
    const { showToast } = useToast();

    const [keywordInput, setKeywordInput] = useState("");
    const [minPriceInput, setMinPriceInput] = useState("");
    const [maxPriceInput, setMaxPriceInput] = useState("");
    const [keyword, setKeyword] = useState("");
    const [minPrice, setMinPrice] = useState("");
    const [maxPrice, setMaxPrice] = useState("");
    const [sortBy, setSortBy] = useState("name");
    const [direction, setDirection] = useState("asc");
    const [page, setPage] = useState(0);

    const [products, setProducts] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [refreshCount, setRefreshCount] = useState(0);

    const [formOpen, setFormOpen] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const [deleteTarget, setDeleteTarget] = useState(null);
    const [deleting, setDeleting] = useState(false);

    useEffect(() => {
        const timer = setTimeout(() => {
            setKeyword(keywordInput.trim());
            setMinPrice(minPriceInput);
            setMaxPrice(maxPriceInput);
            setPage(0);
        }, DEBOUNCE_MS);
        return () => clearTimeout(timer);
    }, [keywordInput, minPriceInput, maxPriceInput]);

    useEffect(() => {
        setPage(0);
    }, [sortBy, direction]);

    useEffect(() => {
        let cancelled = false;
        setLoading(true);
        setError("");

        getProducts({
            page,
            size: PAGE_SIZE,
            sortBy,
            direction,
            keyword: keyword || undefined,
            minPrice: minPrice !== "" ? Number(minPrice) : undefined,
            maxPrice: maxPrice !== "" ? Number(maxPrice) : undefined,
        })
            .then((res) => {
                if (cancelled) return;
                const pageData = res.data.data;
                setProducts(pageData.content ?? []);
                setTotalPages(pageData.totalPages ?? 0);
            })
            .catch(() => {
                if (!cancelled) setError("Couldn't load products. Please try again.");
            })
            .finally(() => {
                if (!cancelled) setLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [page, sortBy, direction, keyword, minPrice, maxPrice, refreshCount]);

    function refresh() {
        setRefreshCount((c) => c + 1);
    }

    function handleFormSuccess() {
        setFormOpen(false);
        showToast(editingProduct ? "Product updated." : "Product created.", "success");
        setEditingProduct(null);
        refresh();
    }

    async function handleConfirmDelete() {
        if (!deleteTarget) return;
        setDeleting(true);
        try {
            await deleteProduct(deleteTarget.id);
            showToast("Product deleted.", "success");
            setDeleteTarget(null);
            refresh();
        } catch {
            showToast("Couldn't delete the product. Please try again.", "error");
        } finally {
            setDeleting(false);
        }
    }

    return (
        <div>
            <div className="flex items-center justify-between mb-4">
                <h1 className="text-lg font-semibold text-text-primary">Manage Products</h1>
                <button
                    type="button"
                    onClick={() => {
                        setEditingProduct(null);
                        setFormOpen(true);
                    }}
                    className="inline-flex items-center gap-1.5 bg-accent hover:bg-accent-hover text-white text-sm font-medium px-3 py-1.5 rounded-md transition-colors"
                >
                    <Plus size={16} />
                    Add Product
                </button>
            </div>

            <SearchFilterBar
                keyword={keywordInput}
                onKeywordChange={setKeywordInput}
                minPrice={minPriceInput}
                onMinPriceChange={setMinPriceInput}
                maxPrice={maxPriceInput}
                onMaxPriceChange={setMaxPriceInput}
                sortBy={sortBy}
                onSortByChange={setSortBy}
                direction={direction}
                onDirectionToggle={() => setDirection((p) => (p === "asc" ? "desc" : "asc"))}
            />

            {loading && <LoadingState label="Loading products..." />}

            {!loading && error && (
                <ErrorState message={error} onRetry={refresh} />
            )}

            {!loading && !error && products.length === 0 && (
                <EmptyState
                    title="No products found"
                    description="Try adjusting your search, or add a new product."
                />
            )}

            {!loading && !error && products.length > 0 && (
                <>
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
                            {products.map((product) => {
                                const inStock = product.availableQuantity > 0;
                                return (
                                    <tr
                                        key={product.id}
                                        className={`border-b border-border last:border-b-0 border-l-[3px] ${
                                            inStock ? "border-l-success" : "border-l-danger"
                                        }`}
                                    >
                                        <td className="px-4 py-2.5 text-text-primary">{product.name}</td>
                                        <td className="px-4 py-2.5 font-mono text-text-primary">
                                            {currencyFormatter.format(product.price)}
                                        </td>
                                        <td className="px-4 py-2.5 text-text-secondary">
                                            {product.availableQuantity}
                                        </td>
                                        <td className="px-4 py-2.5">
                                            <div className="flex justify-end gap-1.5">
                                                <button
                                                    type="button"
                                                    onClick={() => {
                                                        setEditingProduct(product);
                                                        setFormOpen(true);
                                                    }}
                                                    aria-label={`Edit ${product.name}`}
                                                    className="w-7 h-7 flex items-center justify-center rounded-md border border-border text-text-secondary hover:text-accent hover:border-accent transition-colors"
                                                >
                                                    <Pencil size={14} />
                                                </button>
                                                <button
                                                    type="button"
                                                    onClick={() => setDeleteTarget(product)}
                                                    aria-label={`Delete ${product.name}`}
                                                    className="w-7 h-7 flex items-center justify-center rounded-md border border-border text-text-secondary hover:text-danger hover:border-danger transition-colors"
                                                >
                                                    <Trash2 size={14} />
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                );
                            })}
                            </tbody>
                        </table>
                    </div>
                    <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
                </>
            )}

            <ProductFormModal
                open={formOpen}
                product={editingProduct}
                onClose={() => {
                    setFormOpen(false);
                    setEditingProduct(null);
                }}
                onSuccess={handleFormSuccess}
            />

            <ConfirmDialog
                open={!!deleteTarget}
                title="Delete this product?"
                description={
                    deleteTarget
                        ? `"${deleteTarget.name}" will be soft-deleted and hidden from the catalogue. This can be undone from Deleted Products.`
                        : ""
                }
                confirmLabel={deleting ? "Deleting..." : "Delete"}
                danger
                onConfirm={handleConfirmDelete}
                onCancel={() => setDeleteTarget(null)}
            />
        </div>
    );
}

export default AdminProductsPage;
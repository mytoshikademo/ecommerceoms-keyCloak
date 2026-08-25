import { useEffect, useState } from "react";
import { getProducts } from "../../api/productApi";
import ProductCard from "./ProductCard.jsx";
import { LoadingState, ErrorState } from "../ui/StatusStates.jsx";

const RELATED_SIZE = 6;

/**
 * Self-contained, same pattern as ReviewList: fetches independently of the
 * main product fetch, so it never blocks or interferes with the primary
 * Product Details content. Reuses the existing getProducts + ProductCard —
 * no new API endpoint, no new card component.
 */
function RelatedProducts({ category, excludeProductId }) {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        if (!category) {
            setLoading(false);
            setProducts([]);
            return;
        }

        let cancelled = false;
        setLoading(true);
        setError("");

        getProducts({ page: 0, size: RELATED_SIZE, category })
            .then((res) => {
                if (cancelled) return;
                const content = res.data?.data?.content ?? [];
                setProducts(content.filter((p) => p.id !== excludeProductId));
            })
            .catch(() => {
                if (!cancelled) setError("Couldn't load related products.");
            })
            .finally(() => {
                if (!cancelled) setLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [category, excludeProductId]);

    if (!category) return null;

    return (
        <div>
            <h2 className="text-sm font-semibold text-text-primary mb-3">
                Related Products
            </h2>

            {loading && <LoadingState label="Loading related products..." />}

            {!loading && error && <ErrorState message={error} />}

            {!loading && !error && products.length === 0 && (
                <p className="text-text-secondary text-xs">No related products found.</p>
            )}

            {!loading && !error && products.length > 0 && (
                <div className="flex flex-col gap-3">
                    {products.map((product) => (
                        <ProductCard key={product.id} product={product} />
                    ))}
                </div>
            )}
        </div>
    );
}

export default RelatedProducts;
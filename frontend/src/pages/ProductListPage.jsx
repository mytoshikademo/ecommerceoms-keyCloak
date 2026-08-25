import { useEffect, useState } from "react";
import { getProducts } from "../api/productApi";
import ProductCard from "../components/products/ProductCard.jsx";
import SearchFilterBar from "../components/ui/SearchFilterBar.jsx";
import Pagination from "../components/ui/Pagination.jsx";
import { LoadingState, EmptyState, ErrorState } from "../components/ui/StatusStates.jsx";

const PAGE_SIZE = 12;
const DEBOUNCE_MS = 400;

function ProductListPage() {
    // Immediate input values (typed every keystroke)
    const [keywordInput, setKeywordInput] = useState("");
    const [minPriceInput, setMinPriceInput] = useState("");
    const [maxPriceInput, setMaxPriceInput] = useState("");

    // Debounced/applied values that actually drive the fetch
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
    const [retryCount, setRetryCount] = useState(0);

    // Debounce keyword/price typing before it becomes part of the applied filters.
    useEffect(() => {
        const timer = setTimeout(() => {
            setKeyword(keywordInput.trim());
            setMinPrice(minPriceInput);
            setMaxPrice(maxPriceInput);
            setPage(0); // any filter change resets to the first page
        }, DEBOUNCE_MS);
        return () => clearTimeout(timer);
    }, [keywordInput, minPriceInput, maxPriceInput]);

    // Sort changes reset to page 0 too; page changes on their own don't reset page.
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
                if (cancelled) return;
                setError("Couldn't load products. Please try again.");
            })
            .finally(() => {
                if (!cancelled) setLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [page, sortBy, direction, keyword, minPrice, maxPrice, retryCount]);

    function handleDirectionToggle() {
        setDirection((prev) => (prev === "asc" ? "desc" : "asc"));
    }

    return (
        <div>
            <h1 className="text-lg font-semibold text-text-primary mb-4">
                Product Catalogue
            </h1>

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
                onDirectionToggle={handleDirectionToggle}
            />

            {loading && <LoadingState label="Loading products..." />}

            {!loading && error && (
                <ErrorState message={error} onRetry={() => setRetryCount((c) => c + 1)} />
            )}

            {!loading && !error && products.length === 0 && (
                <EmptyState
                    title="No products found"
                    description="Try adjusting your search or filters."
                />
            )}

            {!loading && !error && products.length > 0 && (
                <>
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                        {products.map((product) => (
                            <ProductCard key={product.id} product={product} />
                        ))}
                    </div>
                    <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
                </>
            )}
        </div>
    );
}

export default ProductListPage;
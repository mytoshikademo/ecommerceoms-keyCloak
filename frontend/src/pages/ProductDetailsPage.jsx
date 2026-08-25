import { useEffect, useState } from "react";
import { useParams, Link, useLocation } from "react-router-dom";
import { ArrowLeft, PenSquare } from "lucide-react";
import { getProductById } from "../api/productApi";
import { useAuth } from "../context/AuthContext.jsx";
import { hasAnyRole } from "../utils/roles.js";
import { LoadingState, ErrorState } from "../components/ui/StatusStates.jsx";
import ReviewForm from "../components/products/ReviewForm.jsx";
import ReviewList from "../components/products/ReviewList.jsx";
import RelatedProducts from "../components/products/RelatedProducts.jsx";

const currencyFormatter = new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
});

function ProductDetailsPage() {
    const { id } = useParams();
    const location = useLocation();
    const { isAuthenticated, user } = useAuth();

    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [showReviewForm, setShowReviewForm] = useState(false);

    useEffect(() => {
        let cancelled = false;
        setLoading(true);
        setError("");
        setProduct(null);

        getProductById(id)
            .then((res) => {
                if (!cancelled) setProduct(res.data.data);
            })
            .catch((err) => {
                if (cancelled) return;
                const status = err.response?.status;
                if (status === 404) {
                    setError("This product doesn't exist.");
                } else if (status === 410) {
                    setError("This product has been removed and is no longer available.");
                } else {
                    setError("Couldn't load this product. Please try again.");
                }
            })
            .finally(() => {
                if (!cancelled) setLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [id]);

    const canReview = isAuthenticated && hasAnyRole(user?.role, ["CUSTOMER"]);

    if (loading) return <LoadingState label="Loading product..." />;

    if (error) {
        return (
            <div>
                <ErrorState message={error} />
                <div className="flex justify-center">
                    <Link to="/" className="text-accent hover:text-accent-hover text-sm">
                        Back to catalogue
                    </Link>
                </div>
            </div>
        );
    }

    if (!product) return null;

    const inStock = product.availableQuantity > 0;

    return (
        <div className="max-w-6xl">
            <Link
                to="/"
                className="inline-flex items-center gap-1.5 text-text-secondary hover:text-text-primary text-sm mb-4"
            >
                <ArrowLeft size={14} />
                Back to catalogue
            </Link>

            <div className="flex flex-col lg:flex-row gap-6 items-start">
                <div className="flex-1 min-w-0 w-full">
                    <div
                        className={`bg-surface border border-border border-l-[3px] rounded-lg p-6 ${
                            inStock ? "border-l-success" : "border-l-danger"
                        }`}
                    >
                        <h1 className="text-xl font-semibold text-text-primary mb-2">
                            {product.name}
                        </h1>
                        <p className="font-mono text-2xl text-text-primary mb-3">
                            {currencyFormatter.format(product.price)}
                        </p>
                        <p
                            className={`text-sm font-medium mb-4 ${
                                inStock ? "text-success" : "text-danger"
                            }`}
                        >
                            {inStock ? `${product.availableQuantity} in stock` : "Out of stock"}
                        </p>
                        <p className="text-text-secondary text-sm leading-relaxed">
                            {product.description}
                        </p>
                    </div>

                    <div className="mt-6">
                        <h2 className="text-sm font-semibold text-text-primary mb-2">Reviews</h2>

                        {canReview && (
                            <>
                                <button
                                    type="button"
                                    onClick={() => setShowReviewForm((v) => !v)}
                                    className="inline-flex items-center gap-1.5 bg-accent hover:bg-accent-hover text-white text-sm font-medium px-3 py-1.5 rounded-md transition-colors"
                                >
                                    <PenSquare size={14} />
                                    Write a Review
                                </button>
                                {showReviewForm && <ReviewForm productId={product.id} />}
                            </>
                        )}

                        {!isAuthenticated && (
                            <p className="text-text-secondary text-sm">
                                <Link
                                    to="/login"
                                    state={{ from: location }}
                                    className="text-accent hover:text-accent-hover"
                                >
                                    Log in
                                </Link>{" "}
                                to write a review.
                            </p>
                        )}

                        {isAuthenticated && !canReview && (
                            <p className="text-text-secondary text-sm">
                                Only customer accounts can write reviews.
                            </p>
                        )}

                        <ReviewList productId={product.id} />
                    </div>
                </div>

                <aside className="w-full lg:w-72 shrink-0">
                    <RelatedProducts category={product.category} excludeProductId={product.id} />
                </aside>
            </div>
        </div>
    );
}

export default ProductDetailsPage;
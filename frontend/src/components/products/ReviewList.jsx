import { useEffect, useState } from "react";
import { Star, Loader2, AlertTriangle, ChevronLeft, ChevronRight } from "lucide-react";
import { getProductReviews } from "../../api/reviewApi";

const PAGE_SIZE = 4;

const dateFormatter = new Intl.DateTimeFormat("en-US", {
    dateStyle: "medium",
});

function StarRow({ rating }) {
    return (
        <div className="flex items-center gap-0.5 shrink-0">
            {[1, 2, 3, 4, 5].map((n) => (
                <Star
                    key={n}
                    size={12}
                    className={n <= rating ? "fill-accent text-accent" : "text-border"}
                />
            ))}
        </div>
    );
}

/**
 * Self-contained: owns its own paging state and fetches independently of
 * the rest of Product Details, so changing review pages never touches the
 * product fetch or the write-a-review flow above it.
 *
 * The backend returns totalPages directly, so Previous/Next enablement is
 * a straight comparison against page/totalPages — no guessing needed.
 *
 * Reviewer names: the backend only exposes keycloakUserId, and there's no
 * endpoint to resolve other users' names from that ID. If a review's
 * keycloakUserId matches the currently logged-in viewer (decoded from
 * their own JWT), we show their real name from AuthContext; otherwise we
 * show a neutral "Customer" label. The raw UUID is never displayed.
 */
function ReviewList({ productId }) {
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        setPage(0);
    }, [productId]);

    useEffect(() => {
        let cancelled = false;
        setLoading(true);
        setError("");

        getProductReviews(productId, { page, size: PAGE_SIZE })
            .then((res) => {
                if (cancelled) return;
                setReviews(res.data?.content ?? []);
                setTotalPages(res.data?.totalPages ?? 0);
            })
            .catch(() => {
                if (!cancelled) setError("Couldn't load reviews.");
            })
            .finally(() => {
                if (!cancelled) setLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [productId, page]);

    const hasPrevious = page > 0;
    const hasNext = page + 1 < totalPages;

    function handlePrevious() {
        if (hasPrevious && !loading) setPage((p) => p - 1);
    }

    function handleNext() {
        if (hasNext && !loading) setPage((p) => p + 1);
    }


    return (
        <div className="mt-4">
            {loading && (
                <div className="flex items-center gap-2 text-text-secondary text-xs py-3">
                    <Loader2 size={13} className="animate-spin" />
                    Loading reviews...
                </div>
            )}

            {!loading && error && (
                <div className="flex items-center gap-2 text-danger text-xs py-3">
                    <AlertTriangle size={13} />
                    {error}
                </div>
            )}

            {!loading && !error && reviews.length === 0 && (
                <p className="text-text-secondary text-xs py-3">No reviews yet.</p>
            )}

            {!loading && !error && reviews.length > 0 && (
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    {reviews.map((review) => (
                        <div
                            key={review.id}
                            className="bg-surface border border-border rounded-lg p-3"
                        >
                            <div className="flex items-start justify-between gap-2">
                                <StarRow rating={review.rating} />
                                <div className="text-right shrink-0">
                                    <p className="text-xs text-text-secondary">
                                        {dateFormatter.format(new Date(review.createdAt))}
                                    </p>
                                    <p className="text-xs text-text-primary font-medium">
                                        {review.reviewerName || "Customer"}
                                    </p>
                                </div>
                            </div>
                            <p className="text-sm text-text-primary mt-2">{review.comment}</p>
                        </div>
                    ))}
                </div>
            )}

            {!loading && !error && totalPages > 0 && (
                <div className="flex items-center justify-center gap-3 pt-3">
                    <button
                        type="button"
                        onClick={handlePrevious}
                        disabled={!hasPrevious}
                        className="inline-flex items-center gap-1 text-xs text-text-secondary hover:text-text-primary disabled:opacity-40 disabled:hover:text-text-secondary transition-colors"
                    >
                        <ChevronLeft size={13} />
                        Previous
                    </button>
                    <span className="text-xs text-text-secondary font-mono">
            Page {page + 1}
          </span>
                    <button
                        type="button"
                        onClick={handleNext}
                        disabled={!hasNext}
                        className="inline-flex items-center gap-1 text-xs text-text-secondary hover:text-text-primary disabled:opacity-40 disabled:hover:text-text-secondary transition-colors"
                    >
                        Next
                        <ChevronRight size={13} />
                    </button>
                </div>
            )}
        </div>
    );
}

export default ReviewList;
import { useState } from "react";
import { Star } from "lucide-react";
import { createReview } from "../../api/reviewApi";

/**
 * There's no GET-reviews endpoint on the backend, so once a review is
 * submitted there's no way to fetch it back later — the confirmation shown
 * here is built from the POST response itself, not re-fetched. Backend also
 * rejects a second review for the same product (409), so this intentionally
 * doesn't offer a way to submit again after success in the same session.
 */
function ReviewForm({ productId }) {
    const [rating, setRating] = useState(0);
    const [hoverRating, setHoverRating] = useState(0);
    const [comment, setComment] = useState("");
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [submitted, setSubmitted] = useState(null); // holds the ReviewResponse on success

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");

        if (rating < 1 || rating > 5) {
            setError("Please select a rating from 1 to 5.");
            return;
        }
        if (!comment.trim()) {
            setError("Please enter a comment.");
            return;
        }

        setSubmitting(true);
        try {
            const res = await createReview(productId, { rating, comment: comment.trim() });
            setSubmitted(res.data);
        } catch (err) {
            const status = err.response?.status;
            if (status === 409) {
                setError("You've already reviewed this product.");
            } else if (status === 404) {
                setError("This product no longer exists.");
            } else if (status === 400) {
                setError(err.response?.data?.message || "Please check your review and try again.");
            } else {
                setError("Couldn't submit your review. Please try again.");
            }
        } finally {
            setSubmitting(false);
        }
    }

    if (submitted) {
        return (
            <div className="mt-3 bg-surface border border-border border-l-[3px] border-l-success rounded-lg p-4">
                <div className="flex items-center gap-1 mb-1.5">
                    {[1, 2, 3, 4, 5].map((n) => (
                        <Star
                            key={n}
                            size={14}
                            className={n <= submitted.rating ? "fill-accent text-accent" : "text-border"}
                        />
                    ))}
                </div>
                <p className="text-text-primary text-sm">{submitted.comment}</p>
                <p className="text-text-secondary text-xs mt-2">Thanks for your review!</p>
            </div>
        );
    }

    return (
        <form onSubmit={handleSubmit} className="mt-3 bg-surface border border-border rounded-lg p-4 space-y-3">
            <div>
                <span className="block text-xs text-text-secondary mb-1.5">Rating</span>
                <div className="flex gap-1" onMouseLeave={() => setHoverRating(0)}>
                    {[1, 2, 3, 4, 5].map((n) => (
                        <button
                            key={n}
                            type="button"
                            onClick={() => setRating(n)}
                            onMouseEnter={() => setHoverRating(n)}
                            aria-label={`${n} star${n > 1 ? "s" : ""}`}
                            className="p-0.5"
                        >
                            <Star
                                size={20}
                                className={
                                    n <= (hoverRating || rating)
                                        ? "fill-accent text-accent"
                                        : "text-border hover:text-accent-hover"
                                }
                            />
                        </button>
                    ))}
                </div>
            </div>

            <div>
                <label htmlFor="comment" className="block text-xs text-text-secondary mb-1">
                    Comment
                </label>
                <textarea
                    id="comment"
                    rows={3}
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                    placeholder="What did you think of this product?"
                    className="w-full rounded-md border border-border bg-bg text-text-primary text-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent resize-none"
                />
            </div>

            {error && (
                <p className="text-danger text-sm" role="alert">
                    {error}
                </p>
            )}

            <button
                type="submit"
                disabled={submitting}
                className="bg-accent hover:bg-accent-hover disabled:opacity-60 text-white text-sm font-medium px-3 py-1.5 rounded-md transition-colors"
            >
                {submitting ? "Submitting..." : "Submit Review"}
            </button>
        </form>
    );
}

export default ReviewForm;
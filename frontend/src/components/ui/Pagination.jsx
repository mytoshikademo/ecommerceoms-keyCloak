import { ChevronLeft, ChevronRight } from "lucide-react";

/**
 * `page` is 0-based (matches the backend's `number` field) so callers can
 * pass it straight through to the API params without conversion.
 */
function Pagination({ page, totalPages, onPageChange }) {
    if (totalPages <= 1) return null;

    return (
        <div className="flex items-center justify-center gap-3 mt-6">
            <button
                type="button"
                onClick={() => onPageChange(page - 1)}
                disabled={page === 0}
                aria-label="Previous page"
                className="w-8 h-8 flex items-center justify-center rounded-md border border-border text-text-secondary hover:text-text-primary hover:border-accent disabled:opacity-40 disabled:hover:border-border disabled:hover:text-text-secondary transition-colors"
            >
                <ChevronLeft size={16} />
            </button>

            <span className="text-sm text-text-secondary font-mono">
        Page {page + 1} of {totalPages}
      </span>

            <button
                type="button"
                onClick={() => onPageChange(page + 1)}
                disabled={page >= totalPages - 1}
                aria-label="Next page"
                className="w-8 h-8 flex items-center justify-center rounded-md border border-border text-text-secondary hover:text-text-primary hover:border-accent disabled:opacity-40 disabled:hover:border-border disabled:hover:text-text-secondary transition-colors"
            >
                <ChevronRight size={16} />
            </button>
        </div>
    );
}

export default Pagination;
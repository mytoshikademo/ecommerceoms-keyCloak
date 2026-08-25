import { Search, ArrowUpDown } from "lucide-react";

const SORT_OPTIONS = [
    { value: "name", label: "Name" },
    { value: "price", label: "Price" },
    { value: "availableQuantity", label: "Availability" },
    { value: "id", label: "Newest" },
];

/**
 * Controlled filter bar. Parent owns debouncing (keyword/price typing) and
 * immediate application (sort/direction) — this component is presentation
 * only, so it can be reused wherever a filterable list shows up later.
 */
function SearchFilterBar({
                             keyword,
                             onKeywordChange,
                             minPrice,
                             onMinPriceChange,
                             maxPrice,
                             onMaxPriceChange,
                             sortBy,
                             onSortByChange,
                             direction,
                             onDirectionToggle,
                         }) {
    return (
        <div className="flex flex-wrap items-end gap-3 mb-6">
            <div className="flex-1 min-w-[200px]">
                <label htmlFor="keyword" className="block text-xs text-text-secondary mb-1">
                    Search
                </label>
                <div className="relative">
                    <Search
                        size={14}
                        className="absolute left-2.5 top-1/2 -translate-y-1/2 text-text-secondary"
                    />
                    <input
                        id="keyword"
                        type="text"
                        value={keyword}
                        onChange={(e) => onKeywordChange(e.target.value)}
                        placeholder="Search products..."
                        className="w-full rounded-md border border-border bg-surface text-text-primary text-sm pl-8 pr-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
                    />
                </div>
            </div>

            <div className="w-24">
                <label htmlFor="minPrice" className="block text-xs text-text-secondary mb-1">
                    Min price
                </label>
                <input
                    id="minPrice"
                    type="number"
                    min="0"
                    value={minPrice}
                    onChange={(e) => onMinPriceChange(e.target.value)}
                    placeholder="0"
                    className="w-full rounded-md border border-border bg-surface text-text-primary text-sm px-2 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
                />
            </div>

            <div className="w-24">
                <label htmlFor="maxPrice" className="block text-xs text-text-secondary mb-1">
                    Max price
                </label>
                <input
                    id="maxPrice"
                    type="number"
                    min="0"
                    value={maxPrice}
                    onChange={(e) => onMaxPriceChange(e.target.value)}
                    placeholder="Any"
                    className="w-full rounded-md border border-border bg-surface text-text-primary text-sm px-2 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
                />
            </div>

            <div className="w-40">
                <label htmlFor="sortBy" className="block text-xs text-text-secondary mb-1">
                    Sort by
                </label>
                <select
                    id="sortBy"
                    value={sortBy}
                    onChange={(e) => onSortByChange(e.target.value)}
                    className="w-full rounded-md border border-border bg-surface text-text-primary text-sm px-2 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
                >
                    {SORT_OPTIONS.map((opt) => (
                        <option key={opt.value} value={opt.value}>
                            {opt.label}
                        </option>
                    ))}
                </select>
            </div>

            <button
                type="button"
                onClick={onDirectionToggle}
                aria-label={direction === "asc" ? "Sort descending" : "Sort ascending"}
                title={direction === "asc" ? "Ascending" : "Descending"}
                className="w-9 h-9 flex items-center justify-center rounded-md border border-border text-text-secondary hover:text-text-primary hover:border-accent transition-colors"
            >
                <ArrowUpDown size={16} className={direction === "desc" ? "rotate-180" : ""} />
            </button>
        </div>
    );
}

export default SearchFilterBar;
import { Link } from "react-router-dom";

const currencyFormatter = new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
});

/**
 * Left border color follows the app-wide "state stripe" convention:
 * green = in stock, red = out of stock. Same pattern used for the active
 * sidebar item and will extend to admin product tables/status badges later.
 */
function ProductCard({ product }) {
    const inStock = product.availableQuantity > 0;

    return (
        <Link
            to={`/products/${product.id}`}
            className={`block bg-surface border border-border border-l-[3px] rounded-lg p-4 hover:border-accent transition-colors ${
                inStock ? "border-l-success" : "border-l-danger"
            }`}
        >
            <h3 className="font-medium text-text-primary mb-1 truncate">
                {product.name}
            </h3>
            <p className="font-mono text-lg text-text-primary mb-2">
                {currencyFormatter.format(product.price)}
            </p>
            <p
                className={`text-xs font-medium ${
                    inStock ? "text-success" : "text-danger"
                }`}
            >
                {inStock ? `${product.availableQuantity} in stock` : "Out of stock"}
            </p>
        </Link>
    );
}

export default ProductCard;
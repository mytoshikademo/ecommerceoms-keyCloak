import { useEffect, useState } from "react";
import { createProduct, updateProduct } from "../../api/productApi";

const EMPTY_FORM = { name: "", description: "", price: "", availableQuantity: "", category: "" };

/**
 * Single modal used for both create (product=null) and edit (product set).
 * Client-side validation mirrors the backend's ProductRequest constraints:
 * name/description non-blank, price positive, availableQuantity >= 0.
 */
function ProductFormModal({ open, product, onClose, onSuccess }) {
    const [form, setForm] = useState(EMPTY_FORM);
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    const isEdit = !!product;

    useEffect(() => {
        if (open) {
            setForm(
                product
                    ? {
                        name: product.name ?? "",
                        description: product.description ?? "",
                        price: product.price ?? "",
                        availableQuantity: product.availableQuantity ?? "",
                        category: product.category ?? "",
                    }
                    : EMPTY_FORM
            );
            setError("");
        }
    }, [open, product]);

    if (!open) return null;

    function validate() {
        if (!form.name.trim()) return "Name is required.";
        if (!form.description.trim()) return "Description is required.";
        const price = Number(form.price);
        if (form.price === "" || Number.isNaN(price) || price <= 0) {
            return "Price must be a positive number.";
        }
        const qty = Number(form.availableQuantity);
        if (
            form.availableQuantity === "" ||
            !Number.isInteger(qty) ||
            qty < 0
        ) {
            return "Available quantity must be a whole number, 0 or more.";
        }
        if (!form.category.trim()) return "Category is required.";
        return "";
    }

    async function handleSubmit(e) {
        e.preventDefault();
        const validationError = validate();
        if (validationError) {
            setError(validationError);
            return;
        }

        const payload = {
            name: form.name.trim(),
            description: form.description.trim(),
            price: Number(form.price),
            availableQuantity: Number(form.availableQuantity),
            category: form.category.trim(),
        };

        setSubmitting(true);
        setError("");
        try {
            if (isEdit) {
                await updateProduct(product.id, payload);
            } else {
                await createProduct(payload);
            }
            onSuccess();
        } catch (err) {
            const status = err.response?.status;
            if (status === 409) {
                setError("A product with this name already exists.");
            } else if (status === 400) {
                setError(err.response?.data?.message || "Please check the values and try again.");
            } else if (status === 410) {
                setError("This product has been deleted and can no longer be edited.");
            } else {
                setError(
                    isEdit
                        ? "Couldn't update the product. Please try again."
                        : "Couldn't create the product. Please try again."
                );
            }
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <div
            className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4"
            role="dialog"
            aria-modal="true"
            aria-labelledby="product-form-title"
        >
            <div className="bg-surface border border-border rounded-lg shadow-xl max-w-md w-full p-6">
                <h2 id="product-form-title" className="text-base font-semibold text-text-primary mb-4">
                    {isEdit ? "Edit Product" : "Add Product"}
                </h2>

                <form onSubmit={handleSubmit} className="space-y-3">
                    <div>
                        <label htmlFor="name" className="block text-xs text-text-secondary mb-1">
                            Name
                        </label>
                        <input
                            id="name"
                            type="text"
                            value={form.name}
                            onChange={(e) => setForm({ ...form, name: e.target.value })}
                            className="w-full rounded-md border border-border bg-bg text-text-primary text-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
                        />
                    </div>

                    <div>
                        <label htmlFor="description" className="block text-xs text-text-secondary mb-1">
                            Description
                        </label>
                        <textarea
                            id="description"
                            rows={3}
                            value={form.description}
                            onChange={(e) => setForm({ ...form, description: e.target.value })}
                            className="w-full rounded-md border border-border bg-bg text-text-primary text-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent resize-none"
                        />
                    </div>

                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label htmlFor="price" className="block text-xs text-text-secondary mb-1">
                                Price
                            </label>
                            <input
                                id="price"
                                type="number"
                                min="0"
                                step="0.01"
                                value={form.price}
                                onChange={(e) => setForm({ ...form, price: e.target.value })}
                                className="w-full rounded-md border border-border bg-bg text-text-primary text-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
                            />
                        </div>
                        <div>
                            <label htmlFor="availableQuantity" className="block text-xs text-text-secondary mb-1">
                                Quantity
                            </label>
                            <input
                                id="availableQuantity"
                                type="number"
                                min="0"
                                step="1"
                                value={form.availableQuantity}
                                onChange={(e) => setForm({ ...form, availableQuantity: e.target.value })}
                                className="w-full rounded-md border border-border bg-bg text-text-primary text-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
                            />
                        </div>
                        <div>
                            <label htmlFor="category" className="block text-xs text-text-secondary mb-1">
                                Category
                            </label>
                            <input
                                id="category"
                                type="text"
                                value={form.category}
                                onChange={(e) => setForm({ ...form, category: e.target.value })}
                                className="w-full rounded-md border border-border bg-bg text-text-primary text-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
                            />
                        </div>
                    </div>

                    {error && (
                        <p className="text-danger text-sm" role="alert">
                            {error}
                        </p>
                    )}

                    <div className="flex justify-end gap-2 pt-2">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-3 py-1.5 rounded-md text-sm font-medium text-text-primary border border-border hover:bg-bg transition-colors"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={submitting}
                            className="px-3 py-1.5 rounded-md text-sm font-medium text-white bg-accent hover:bg-accent-hover disabled:opacity-60 transition-colors"
                        >
                            {submitting ? "Saving..." : isEdit ? "Save Changes" : "Create Product"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ProductFormModal;
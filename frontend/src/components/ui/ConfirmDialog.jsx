import { useEffect, useRef } from "react";

/**
 * Generic confirmation modal. Controlled via `open`; caller owns the state
 * that opens it. Used for destructive actions like delete-product later on.
 */
function ConfirmDialog({
  open,
  title,
  description,
  confirmLabel = "Confirm",
  cancelLabel = "Cancel",
  danger = false,
  onConfirm,
  onCancel,
}) {
  const confirmBtnRef = useRef(null);

  useEffect(() => {
    if (open) confirmBtnRef.current?.focus();
  }, [open]);

  useEffect(() => {
    function handleKey(e) {
      if (e.key === "Escape") onCancel?.();
    }
    if (open) document.addEventListener("keydown", handleKey);
    return () => document.removeEventListener("keydown", handleKey);
  }, [open, onCancel]);

  if (!open) return null;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4"
      role="dialog"
      aria-modal="true"
      aria-labelledby="confirm-dialog-title"
    >
      <div className="bg-surface border border-border rounded-lg shadow-xl max-w-sm w-full p-5">
        <h2
          id="confirm-dialog-title"
          className="text-base font-semibold text-text-primary mb-1.5"
        >
          {title}
        </h2>
        {description && (
          <p className="text-sm text-text-secondary mb-5">{description}</p>
        )}
        <div className="flex justify-end gap-2">
          <button
            type="button"
            onClick={onCancel}
            className="px-3 py-1.5 rounded-md text-sm font-medium text-text-primary border border-border hover:bg-bg transition-colors"
          >
            {cancelLabel}
          </button>
          <button
            ref={confirmBtnRef}
            type="button"
            onClick={onConfirm}
            className={`px-3 py-1.5 rounded-md text-sm font-medium text-white transition-colors ${
              danger
                ? "bg-danger hover:opacity-90"
                : "bg-accent hover:bg-accent-hover"
            }`}
          >
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}

export default ConfirmDialog;

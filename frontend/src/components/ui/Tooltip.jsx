import { cloneElement, useId, useState } from "react";

/**
 * Lightweight tooltip. Wrap a single element child; pass `label` for the
 * hover/focus text. `className` is applied to the tooltip's own wrapper
 * span (e.g. "w-full" so a full-width disabled row keeps its width instead
 * of shrinking to fit content, since the wrapper defaults to inline-flex).
 * Used generally, and for "Not Implemented" hints on disabled
 * future-feature nav items/buttons.
 *
 * Usage: <Tooltip label="Not implemented"><button>Cart</button></Tooltip>
 */
function Tooltip({ label, children, side = "top", className = "" }) {
  const [visible, setVisible] = useState(false);
  const id = useId();

  const sideClasses = {
    top: "bottom-full left-1/2 -translate-x-1/2 mb-1.5",
    bottom: "top-full left-1/2 -translate-x-1/2 mt-1.5",
    right: "left-full top-1/2 -translate-y-1/2 ml-1.5",
  };

  const trigger = cloneElement(children, {
    "aria-describedby": id,
    onMouseEnter: () => setVisible(true),
    onMouseLeave: () => setVisible(false),
    onFocus: () => setVisible(true),
    onBlur: () => setVisible(false),
  });

  return (
      <span className={`relative inline-flex ${className}`}>
      {trigger}
        {visible && (
            <span
                id={id}
                role="tooltip"
                className={`absolute z-50 whitespace-nowrap rounded-md bg-surface border border-border text-text-primary text-xs px-2 py-1 shadow-lg pointer-events-none ${sideClasses[side]}`}
            >
          {label}
        </span>
        )}
    </span>
  );
}

export default Tooltip;
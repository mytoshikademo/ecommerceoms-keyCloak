import { Loader2, Inbox, AlertTriangle } from "lucide-react";

export function LoadingState({ label = "Loading..." }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-text-secondary">
      <Loader2 size={28} className="animate-spin mb-2" />
      <span className="text-sm">{label}</span>
    </div>
  );
}

export function EmptyState({
  title = "Nothing here yet",
  description,
  icon: Icon = Inbox,
}) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center px-4">
      <Icon size={32} className="text-text-secondary mb-3" />
      <h3 className="text-text-primary font-medium mb-1">{title}</h3>
      {description && (
        <p className="text-text-secondary text-sm max-w-sm">{description}</p>
      )}
    </div>
  );
}

/**
 * Generic error state. `message` should be the specific, context-appropriate
 * text decided at the call site (per the "specific, not generic error
 * messages" rule) — this component only handles presentation, and falls
 * back to a generic line only when no message is supplied at all.
 */
export function ErrorState({ message = "Something went wrong. Please try again.", onRetry }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center px-4">
      <AlertTriangle size={32} className="text-danger mb-3" />
      <p className="text-text-primary text-sm max-w-sm mb-4">{message}</p>
      {onRetry && (
        <button
          type="button"
          onClick={onRetry}
          className="px-3 py-1.5 rounded-md text-sm font-medium bg-accent hover:bg-accent-hover text-white transition-colors"
        >
          Retry
        </button>
      )}
    </div>
  );
}

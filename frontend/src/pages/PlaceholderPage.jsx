/**
 * Temporary stub for pages not yet built. Used two ways:
 * - inShell=true (default): rendered inside AppShell's <main>, which already
 *   provides the themed background/padding — so this only renders a card.
 * - inShell=false: used for standalone routes (login/register/404) that sit
 *   outside AppShell and need their own full-screen themed background.
 */
function PlaceholderPage({ title, inShell = true }) {
  const card = (
    <div className="bg-surface border border-border rounded-lg p-8 max-w-md w-full text-center">
      <h1 className="text-xl font-semibold mb-2">{title}</h1>
      <p className="text-text-secondary text-sm mb-4">
        This page will be built in a later phase.
      </p>
      <button
        type="button"
        className="bg-accent hover:bg-accent-hover text-white text-sm font-medium px-4 py-2 rounded-md transition-colors"
      >
        Sample button
      </button>
    </div>
  );

  if (inShell) {
    return <div className="flex items-center justify-center min-h-[60vh]">{card}</div>;
  }

  return (
    <div className="min-h-screen bg-bg text-text-primary flex items-center justify-center px-4">
      {card}
    </div>
  );
}

export default PlaceholderPage;

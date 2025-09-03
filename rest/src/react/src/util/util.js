export function capitalizeFirstLetter(str) {
  if (str.length === 0) {
    return ""; // Handle empty strings
  }
  return str.charAt(0).toUpperCase() + str.slice(1);
}

export function formatTime(date, locale = navigator.language) {
  const d = new Date(date);
  const diff = Math.floor((Date.now() - d) / 1000);

  if (diff < 60) return `${diff} second${diff !== 1 ? "s" : ""}`;
  if (diff < 3600) {
    const mins = Math.floor(diff / 60);
    return `${mins} minute${mins !== 1 ? "s" : ""}`;
  }
  if (diff < 86400) {
    const hours = Math.floor(diff / 3600);
    return `${hours} hour${hours !== 1 ? "s" : ""}`;
  }
  if (diff < 604800) {
    const days = Math.floor(diff / 86400);
    return `${days} day${days !== 1 ? "s" : ""}`;
  }

  return d.toLocaleDateString(locale);
}

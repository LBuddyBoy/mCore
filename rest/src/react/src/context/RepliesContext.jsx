import { createContext, useContext, useState } from "react";

const ReplyContext = createContext(null);

export default function ReplyProvider({ children }) {
  const [editing, setEditing] = useState(null);

  const exports = {
    editing,
    setEditing,
  };

  return (
    <ReplyContext.Provider value={exports}>{children}</ReplyContext.Provider>
  );
}

export function useReply() {
  const context = useContext(ReplyContext);

  if (!context) {
    throw new Error("useReply must be used within a ReplyProvider");
  }

  return context;
}

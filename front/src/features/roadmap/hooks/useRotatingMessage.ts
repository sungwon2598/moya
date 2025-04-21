import { useState, useEffect } from "react";

export function useRotatingMessage<T>(
  messages: T[],
  intervalMs: number = 5000
) {
  const [currentIndex, setCurrentIndex] = useState<number>(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentIndex((prev) => {
        const newIndex = prev + 1;
        return newIndex >= messages.length ? 0 : newIndex;
      });
    }, intervalMs);

    return () => clearInterval(interval);
  }, [messages.length, intervalMs]);

  return {
    currentMessage: messages[currentIndex],
    currentIndex,
  };
}

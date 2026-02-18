import { Suspense } from "react";
import SessionWrapper from "../../components/SessionWrapper";

export default function StartSession() {
  return (
    <Suspense fallback={
      <main className="min-h-screen bg-gray-50 flex items-center justify-center">
        <p className="text-amber-600 text-lg font-semibold">Loading session...</p>
      </main>
    }>
      <SessionWrapper />
    </Suspense>
  );
}

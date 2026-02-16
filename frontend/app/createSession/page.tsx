import { Suspense } from "react";
import SessionWrapper from "../../components/SessionWrapper";

export default function StartSession() {
  return (
    <Suspense fallback={
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-amber-600 text-lg font-semibold">Loading session...</div>
      </div>
    }>
      <SessionWrapper />
    </Suspense>
  );
}

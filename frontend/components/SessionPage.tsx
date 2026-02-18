import SessionView from "./SessionView";
import { useSearchParams } from "next/navigation";

export default function SessionPage() {
  const searchParams = useSearchParams();
  const id = searchParams.get("id");

  if (!id) {
    return (
      <main className="min-h-screen bg-gray-50 flex items-center justify-center">
        <p className="text-red-600 text-lg font-semibold">
          No session ID provided.
        </p>
      </main>
    );
  }

  return <SessionView sessionId={id} />;
}
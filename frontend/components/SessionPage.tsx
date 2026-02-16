import SessionView from "./SessionView";
import { useSearchParams } from "next/navigation";

export default function SessionPage() {
  const searchParams = useSearchParams();
  const id = searchParams.get("id");

  if (!id) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-red-600 text-lg font-semibold">
          No session ID provided.
        </div>
      </div>
    );
  }

  return <SessionView sessionId={id} />;
}
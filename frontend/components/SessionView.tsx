"use client";
import { useQuery, useMutation } from "@apollo/client/react";
import { GET_SESSION } from "@/lib/graphql/queries";
import { CLAIM_ITEM } from "@/lib/graphql/mutations";

interface SessionData {
  getSessionById: {
    id: string;
    partySize: number;
    items: string;
    users: string;
  };
}

interface Item {
  name: string;
  price: number;
  claimedBy: string;
}

interface SessionViewProps {
  sessionId: string;
}

export default function SessionView({ sessionId }: SessionViewProps) {
  const { data, loading, error } = useQuery<SessionData>(GET_SESSION, {
    variables: { id: sessionId },
  });
  const [claimItem] = useMutation(CLAIM_ITEM);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-amber-600 text-lg font-semibold">
          Loading session...
        </div>
      </div>
    );
  }

  if (error || !data?.getSessionById) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-red-600 text-lg font-semibold">
          {error ? `Error: ${error.message}` : "Session not found."}
        </div>
      </div>
    );
  }

  const session = data.getSessionById;
  const items: Record<string, Item> = session.items
    ? JSON.parse(session.items)
    : {};
  const users: Record<string, string> = session.users
    ? JSON.parse(session.users)
    : {};

  const currentUserId = Object.keys(users)[0] || "";

  const handleClaim = async (itemId: string) => {
    try {
      await claimItem({
        variables: { sessionId, itemId, userId: currentUserId },
        refetchQueries: [{ query: GET_SESSION, variables: { id: sessionId } }],
      });
    } catch (err) {
      console.error("Failed to claim item:", err);
    }
  };

  const handleUnclaim = async (itemId: string) => {
    try {
      await claimItem({
        variables: { sessionId, itemId, userId: "" },
        refetchQueries: [{ query: GET_SESSION, variables: { id: sessionId } }],
      });
    } catch (err) {
      console.error("Failed to unclaim item:", err);
    }
  };

  return (
    <main className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-4">
      <article className="w-full max-w-md bg-white rounded-3xl shadow-xl p-8 border border-gray-100">
        <header className="text-center mb-6">
          <h1 className="text-3xl font-bold text-gray-900 tracking-tight">
            Split the Bill
          </h1>
          <p className="text-gray-500 mt-2">
            Session #{session.id} &middot; {session.partySize} people
          </p>
        </header>

        {Object.keys(users).length > 0 && (
          <section className="mb-6">
            <h2 className="text-sm font-medium text-gray-500 mb-2">People</h2>
            <div className="flex flex-wrap gap-2">
              {Object.values(users).map((name) => (
                <span
                  key={name}
                  className="px-3 py-1 bg-amber-100 text-amber-800 rounded-full text-sm font-medium"
                >
                  {name}
                </span>
              ))}
            </div>
          </section>
        )}

        <section>
          <h2 className="text-sm font-medium text-gray-500 mb-3">Items</h2>
          {Object.keys(items).length === 0 ? (
            <p className="text-gray-400 text-center py-4">No items yet.</p>
          ) : (
            <ul className="space-y-3">
              {Object.entries(items).map(([key, item]) => (
                <li
                  key={key}
                  className="flex items-center justify-between p-4 rounded-2xl border-2 border-gray-100"
                >
                  <label>
                    <p className="font-semibold text-gray-900">{item.name}</p>
                    <p className="text-sm text-gray-500">
                      ${Number(item.price).toFixed(2)}
                    </p>
                  </label>
                  {item.claimedBy ? (
                    item.claimedBy === currentUserId ? (
                      <button
                        onClick={() => handleUnclaim(key)}
                        className="px-4 py-2 bg-gray-200 text-gray-600 font-bold rounded-xl text-sm transition-all hover:bg-gray-300"
                      >
                        Unclaim
                      </button>
                    ) : (
                      <span className="px-3 py-1 bg-gray-100 text-gray-500 rounded-full text-sm">
                        {users[item.claimedBy] || item.claimedBy}
                      </span>
                    )
                  ) : (
                    <button
                      onClick={() => handleClaim(key)}
                      className="px-4 py-2 bg-amber-600 text-white font-bold rounded-xl text-sm transition-all hover:bg-amber-700"
                    >
                      Claim
                    </button>
                  )}
                </li>
              ))}
            </ul>
          )}
        </section>
      </article>
    </main>
  );
}

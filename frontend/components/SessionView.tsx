"use client";

import { useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import { useQuery, useMutation } from "@apollo/client/react";
import { GET_SESSION } from "@/lib/graphql/queries";
import { CLAIM_ITEM } from "@/lib/graphql/mutations";

interface SessionData {
  getSessionById: {
    id: string;
    partySize: number;
    items: string;
    users: string;
    parsingStatus: string;
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
  const abortControllerRef = useRef<AbortController>(new AbortController());

  // create a fresh controller on mount, abort on unmount
  useEffect(() => {
    abortControllerRef.current = new AbortController();
    return () => abortControllerRef.current.abort();
  }, []);

  const getFetchContext = () => ({
    fetchOptions: { signal: abortControllerRef.current.signal },
  });

  const { data, loading, error } = useQuery<SessionData>(GET_SESSION, {
    variables: { id: sessionId },
    context: getFetchContext(),
  });
  const [claimItem] = useMutation(CLAIM_ITEM);
  const [parsingStatus, setParsingStatus] = useState<string | null>(null);
  const [streamedItems, setStreamedItems] = useState<Record<
    string,
    Item
  > | null>(null);
  const [streamedUsers, setStreamedUsers] = useState<Record<
    string,
    string
  > | null>(null);
  const [copied, setCopied] = useState(false);
  const [claimError, setClaimError] = useState<string | null>(null);
  const [claimingItemId, setClaimingItemId] = useState<string | null>(null);

  const joinSessionUrl = `http://localhost:3000/join?sessionId=${sessionId}`;

  const copyLinkToClipboard = async (): Promise<void> => {
    try {
      await navigator.clipboard.writeText(joinSessionUrl);
      console.log("Text copied to clipboard");
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error("Failed to copy: ", err);
    }
  };
  useEffect(() => {
    const client = new Client({
      brokerURL: "ws://localhost:8000/ws",
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("websocket connected, sessionId: ", sessionId);

        client.subscribe(`/topic/session/${sessionId}`, (message) => {
          const data = JSON.parse(message.body);
          console.log(" websocket message:", data);

          data.status === "PARSING" && setParsingStatus("PARSING");
          data.status === "ACTIVE" && setParsingStatus("ACTIVE");
          data.status === "ACTIVE" && setStreamedItems(data.items);
          data.status === "FAILURE" && setParsingStatus("FAILURE");
          // new status for when a user has joined --> update users
          data.status === "USER_JOINED" && setStreamedUsers(data.users);
          // update items when another user claims/unclaims
          data.status === "ITEM_CLAIMED" && setStreamedItems(data.items);
        });
      },
    });

    client.activate();
    return () => {
      client.deactivate();
    };
  }, [sessionId]);

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

  const items: Record<string, Item> =
    streamedItems ?? (session.items ? JSON.parse(session.items) : {});
  const users: Record<string, string> =
    streamedUsers ?? (session.users ? JSON.parse(session.users) : {});

  const currentUserId =
    (typeof window !== "undefined"
      ? localStorage.getItem(`userId_${sessionId}`)
      : null) ||
    Object.keys(users)[0] ||
    "";

  const optimisticUpdate = (itemId: string, userId: string) => {
    const updated = { ...items };
    updated[itemId] = { ...updated[itemId], claimedBy: userId };
    setStreamedItems(updated);
  };

  const handleClaim = async (itemId: string) => {
    if (claimingItemId) return;
    setClaimingItemId(itemId);
    setClaimError(null);
    const previous = { ...items };
    optimisticUpdate(itemId, currentUserId);
    try {
      await claimItem({
        variables: { sessionId, itemId, userId: currentUserId },
        context: getFetchContext(),
      });
    } catch (err) {
      setStreamedItems(previous);
      const apolloErr = err as { graphQLErrors?: { message: string }[] };
      const message = apolloErr?.graphQLErrors?.[0]?.message;
      setClaimError(message ?? "Failed to claim item.");
      setTimeout(() => setClaimError(null), 3000);
    } finally {
      setClaimingItemId(null);
    }
  };

  const handleUnclaim = async (itemId: string) => {
    if (claimingItemId) return;
    setClaimingItemId(itemId);
    const previous = { ...items };
    optimisticUpdate(itemId, "");
    try {
      await claimItem({
        variables: { sessionId, itemId, userId: "" },
        context: getFetchContext(),
      });
    } catch (err) {
      setStreamedItems(previous);
      console.error("Failed to unclaim item:", err);
    } finally {
      setClaimingItemId(null);
    }
  };

  return (
    <main className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-4">
      <article className="w-full max-w-md bg-white rounded-3xl shadow-xl p-8 border border-gray-100">
        <p className="text-gray-500 mt-2">Share this link</p>
        <button
          onClick={copyLinkToClipboard}
          className="px-4 py-2 bg-amber-600 text-white rounded-xl text-sm font-bold"
        >
          {copied ? "Copied!" : "Copy Link"}
        </button>
      </article>
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

          {claimError && (
            <div className="mb-3 px-4 py-2 bg-red-100 text-red-700 rounded-xl text-sm font-medium">
              {claimError}
            </div>
          )}

          {parsingStatus === "PARSING" && (
            <div className="space-y-3">
              {[1, 2, 3, 4].map((i) => (
                <div
                  key={i}
                  className="h-3 bg-black/10 rounded animate-pulse w-full"
                />
              ))}
            </div>
          )}

          {parsingStatus === "FAILURE" && (
            <p className="text-gray-400 text-center py-4">
              Could not parse receipt. Please try again.
            </p>
          )}

          {parsingStatus !== "PARSING" && parsingStatus !== "FAILURE" && (
            <>
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
                        <p className="font-semibold text-gray-900">
                          {item.name}
                        </p>
                        <p className="text-sm text-gray-500">
                          ${Number(item.price).toFixed(2)}
                        </p>
                      </label>
                      {item.claimedBy ? (
                        item.claimedBy === currentUserId ? (
                          <button
                            onClick={() => handleUnclaim(key)}
                            disabled={claimingItemId === key}
                            className="px-4 py-2 bg-gray-200 text-gray-600 font-bold rounded-xl text-sm transition-all hover:bg-gray-300 disabled:opacity-50"
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
                          disabled={claimingItemId === key}
                          className="px-4 py-2 bg-amber-600 text-white font-bold rounded-xl text-sm transition-all hover:bg-amber-700 disabled:opacity-50"
                        >
                          Claim
                        </button>
                      )}
                    </li>
                  ))}
                </ul>
              )}
            </>
          )}
        </section>
      </article>
    </main>
  );
}

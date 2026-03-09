"use client";

import { useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { useMutation } from "@apollo/client/react";
import { ADD_USER_TO_SESSION } from "@/lib/graphql/mutations";

interface AddUserData {
  addUserToSession: {
    newUserId: string;
    session: {
      id: string;
    };
  };
}

export default function JoinSessionForm() {
  const router = useRouter();
  const sessionId = useSearchParams().get("sessionId");

  const [name, setName] = useState("");
  const [addUser, { loading }] =
    useMutation<AddUserData>(ADD_USER_TO_SESSION);

  const handleJoin = async () => {
    try {
      const { data } = await addUser({
        variables: {
            sessionId,
            name,
        },
      });
    
      const userId = data?.addUserToSession.newUserId;
        if (userId) {
          localStorage.setItem(`userId_${sessionId}`, userId);
        }
        router.push(`/createSession?id=${sessionId}`);
    } catch (err) {
      console.error("Failed to add user:", err);
    }
  };

   if (!sessionId) {
    return (
      <main className="min-h-screen bg-gray-50 flex items-center justify-center">
        <p> No session ID provided</p>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-sm bg-white rounded-3xl shadow-xl p-8">
        <h1 className="text-2xl font-bold text-center mb-6 text-gray-900">
          Join Session
        </h1>
        <input
          type="text"
          placeholder="Please enter your name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="w-full border border-gray-200 rounded-xl px-4 py-3 mb-4 text-gray-900"
        />
        <button
          onClick={handleJoin}
          disabled={loading || !name.trim()}
          type="button"
          className="w-full bg-amber-600 text-white font-bold py-4 rounded-2xl transition-all disabled:opacity-50"
        >
          {loading ? "Joining..." : "Join Session"}
        </button>
      </div>
    </main>
  );
}
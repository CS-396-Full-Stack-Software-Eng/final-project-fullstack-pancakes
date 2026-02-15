"use client";
import { useSearchParams } from "next/navigation";
import { ApolloProvider } from "@apollo/client/react";
import ApolloClient from "../lib/ApolloClient";
import SessionView from "../../components/SessionView";
import { Suspense } from "react";

function SessionPage() {
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

export default function StartSession() {
  return (
    <ApolloProvider client={ApolloClient}>
      <Suspense>
        <SessionPage />
      </Suspense>
    </ApolloProvider>
  );
}

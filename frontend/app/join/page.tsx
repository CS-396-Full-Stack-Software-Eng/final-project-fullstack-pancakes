"use client";

import { ApolloProvider } from "@apollo/client/react";
import JoinSessionForm from "@/components/JoinSessionForm";
import apolloClient from "../lib/ApolloClient";

export default function JoinSessionPage() {
  return (
    <ApolloProvider client={apolloClient}>
      <JoinSessionForm />
    </ApolloProvider>
  );
}
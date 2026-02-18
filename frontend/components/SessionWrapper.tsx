"use client";
import { ApolloProvider } from "@apollo/client/react";
import ApolloClient from "../app/lib/ApolloClient";
import SessionPage from "./SessionPage";

export default function SessionWrapper() {
  return (
    <ApolloProvider client={ApolloClient}>
      <SessionPage />
    </ApolloProvider>
  );
}

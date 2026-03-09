"use client";
import Landing from "../components/Landing";
import { ApolloProvider } from "@apollo/client/react";
import ApolloClient from "./lib/ApolloClient";

export default function Home() {
  return (
    <>
      <ApolloProvider client={ApolloClient}>
        <Landing />
      </ApolloProvider>
    </>
  );
}

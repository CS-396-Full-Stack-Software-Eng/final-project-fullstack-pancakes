import { ApolloClient, InMemoryCache, HttpLink } from "@apollo/client";

const cache = new InMemoryCache();
const link = new HttpLink({uri: "http://localhost:8000/graphql"});

const apolloClient = new ApolloClient({
  link: link,
  cache: cache
})

export default apolloClient;


import { gql } from "@apollo/client";

export const GET_SESSION = gql`
  query GetSession($id: ID!) {
    getSessionById(id: $id) {
      id
      partySize
      items
      users
      parsingStatus
    }
  }
`;

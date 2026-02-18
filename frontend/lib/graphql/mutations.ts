import { gql } from "@apollo/client";

export const CLAIM_ITEM = gql`
  mutation ClaimItem($sessionId: ID!, $itemId: ID!, $userId: ID!) {
    claimItem(sessionId: $sessionId, itemId: $itemId, userId: $userId) {
      id
      items
      users
    }
  }
`;

export const UPLOAD_RECEIPT = gql`
  mutation UploadReceipt($image: String!, $partySize: Int!, $leaderName: String!) {
    uploadReceipt(image: $image, partySize: $partySize, leaderName: $leaderName) {
      id
      partySize
    }
  }
`;
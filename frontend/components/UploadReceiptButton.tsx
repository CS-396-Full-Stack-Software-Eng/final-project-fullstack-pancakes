import { gql } from "@apollo/client";
import { useMutation } from "@apollo/client/react";
import Link from "next/link";

const CREATE_SESSION = gql`
  mutation CreateSession($partySize: Int!) {
    createSession(partySize: $partySize) {
      id
      partySize
    }
  }
`;

interface StartSessionProps {
  partySize: string;
}

export default function UploadReceiptButton({ partySize }: StartSessionProps) {
  const [startSession, { loading, error, data }] = useMutation(CREATE_SESSION);
  const handleClick = async () => {
    try {
      await startSession({
        variables: {
          partySize: parseInt(partySize, 10),
        },
      });
    } catch (err) {
      console.log(err);
    }
  };
  return (
    <Link href={`/createSession`}>
      <button
        onClick={handleClick}
        type="button"
        className="w-full bg-amber-600 text-white font-bold py-4 rounded-2xl transition-all"
      >
        {loading ? "Processing..." : "Process Receipt"}{" "}
      </button>
    </Link>
  );
}

import { gql } from "@apollo/client";
import { useMutation } from "@apollo/client/react";
import { useRouter } from "next/navigation";

const UPLOAD_RECEIPT = gql`
  mutation UploadReceipt($image: String!, $partySize: Int!, $leaderName: String!) {
    uploadReceipt(image: $image, partySize: $partySize, leaderName: $leaderName) {
      id
      partySize
    }
  }
`;

interface UploadReceiptData {
  uploadReceipt: {
    id: string;
    partySize: number;
  };
}

interface StartSessionProps {
  partySize: string;
  leaderName: string;
}

export default function UploadReceiptButton({ partySize, leaderName }: StartSessionProps) {
  const router = useRouter();
  const [uploadReceipt, { loading }] = useMutation<UploadReceiptData>(UPLOAD_RECEIPT);

  const handleClick = async () => {
    try {
      const { data } = await uploadReceipt({
        variables: {
          image: "fake-receipt-placeholder",
          partySize: parseInt(partySize, 10),
          leaderName,
        },
      });
      if (data?.uploadReceipt?.id) {
        router.push(`/createSession?id=${data.uploadReceipt.id}`);
      }
    } catch (err) {
      console.error("Failed to create session:", err);
    }
  };

  return (
    <button
      onClick={handleClick}
      disabled={loading}
      type="button"
      className="w-full bg-amber-600 text-white font-bold py-4 rounded-2xl transition-all disabled:opacity-50"
    >
      {loading ? "Processing..." : "Process Receipt"}
    </button>
  );
}

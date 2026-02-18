"use client";
import UploadReceiptButton from "./UploadReceiptButton";
import { useState } from "react";

export default function Landing() {
  const [partySize, setPartySize] = useState("");
  const [leaderName, setLeaderName] = useState("");

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-md bg-white rounded-3xl shadow-xl p-8 border border-gray-100">
        <section className="text-center mb-8">
          <header className="text-3xl font-bold text-gray-900 tracking-tight">
            Split the Bill
          </header>
          <p className="text-gray-500 mt-2">
            Upload your receipt to get started.
          </p>
        </section>

        <form className="space-y-6">
          <section>
            <label
              className="block text-sm font-medium text-gray-700 mb-2"
              htmlFor="party_size"
            >
              How many people?
            </label>
            <input
              className="w-full text-gray-700 px-4 py-3 rounded-xl border-2 border-gray-100 focus:border-amber-500 focus:ring-2 focus:ring-amber-200 outline-none transition-all text-xl font-semibold text-center"
              type="number"
              id="party_size"
              name="party_size"
              placeholder="e.g. 4"
              min="1"
              value={partySize}
              onChange={(e) => setPartySize(e.target.value)}
            />
          </section>

          <section>
            <label
              className="block text-sm font-medium text-gray-700 mb-2"
              htmlFor="leader_name"
            >
              Your Name
            </label>
            <input
              className="w-full text-gray-700 px-4 py-3 rounded-xl border-2 border-gray-100 focus:border-amber-500 focus:ring-2 focus:ring-amber-200 outline-none transition-all text-xl font-semibold text-center"
              type="text"
              id="leader_name"
              name="leader_name"
              placeholder="e.g. Alex"
              value={leaderName}
              onChange={(e) => setLeaderName(e.target.value)}
            />
          </section>

          <section>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Receipt Photo
            </label>
            <label className="flex flex-col items-center justify-center w-full h-40 border-2 border-dashed border-gray-300 rounded-2xl cursor-pointer bg-gray-50 hover:bg-amber-50 hover:border-amber-300 transition-all">
              <div className="flex flex-col items-center justify-center pt-5 pb-6">
                <svg
                  className="w-10 h-10 mb-3 text-amber-500"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
                </svg>
                <p className="text-sm text-gray-600">
                  <span className="font-semibold text-amber-600">
                    Click to upload
                  </span>
                </p>
              </div>
              <input
                type="file"
                className="hidden"
                accept="image/*"
                capture="environment"
              />
            </label>
          </section>
          <UploadReceiptButton partySize={partySize} leaderName={leaderName} />
        </form>
      </div>
    </div>
  );
}

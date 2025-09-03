import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router";
import { getAccountReplies } from "../../api/api";
import { useState } from "react";
import ReplyItem from "../forums/component/ReplyItem";
import "./style/AccountRepliesPage.css";
import Button from "../../component/Button";
import ReplyProvider from "../../context/RepliesContext";
import useTitle from "../../hooks/useTitle";

export default function AccountRepliesPage() {
  const { id } = useParams();
  const [page, setPage] = useState(0);
  const query = useQuery({
    queryKey: ["accountReplies_" + id, page],
    queryFn: () => getAccountReplies({ accountId: id, page }),
  });

  useTitle(query.data?.page.totalElements + " Messages");

  if (query.isPending || query.isError) return <></>;

  const replies = query.data.content;
  const pagination = query.data.page;

  console.log(query.data);

  return (
    <ReplyProvider>
      <div className="accountRepliesContainer">
        <header>
          <h1>Messages</h1>
        </header>
        <div className="accountReplyItems">
          {replies.map((reply) => {
            return (
              <ReplyItem
                key={reply.id}
                reply={reply}
                showView={true}
                style={{
                  backgroundColor: "var(--bg)",
                }}
              >
                <Button>View Post</Button>
              </ReplyItem>
            );
          })}
        </div>
        <div className="accountRepliesControls">
          <Button
            onClick={() => setPage((prev) => prev - 1)}
            disabled={page == 0}
          >
            Previous Page
          </Button>
          <p>Page {page + 1}</p>
          <Button
            onClick={() => setPage((prev) => prev + 1)}
            disabled={page >= pagination.totalPages - 1}
          >
            Next Page
          </Button>
        </div>
      </div>
    </ReplyProvider>
  );
}

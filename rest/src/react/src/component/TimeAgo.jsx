import { formatTime } from "../util/util";

export default function TimeAgo({ timeStamp }) {
  return <time>{formatTime(timeStamp) + " ago"}</time>;
}

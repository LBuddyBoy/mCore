import "./style/Button.css";

export default function Button({
  children,
  bgColor = "var(--primary)",
  ...props
}) {
  return (
    <button
      id="customButton"
      className={
        "customButton" + (props.className ? " " + props.className : "")
      }
      style={{
        backgroundColor: bgColor,
      }}
      {...props}
    >
      {children}
    </button>
  );
}

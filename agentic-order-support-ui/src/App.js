import React, { useState, useRef, useEffect, useCallback } from 'react';

const BASE_URL = process.env.REACT_APP_BASE_URL || 'http://localhost:8080';

function generateId(prefix) {
  const base = prefix === 'user'
    ? '550e8400-e29b-41d4-a716-44665544'
    : '660e8400-e29b-41d4-a716-44665544';
  const micro = (performance.now() * 1000 | 0).toString().padStart(4, '0').slice(-4);
  return base + micro;
}

function extractMessage(raw) {
  // Strip everything before the last sentence (after all FunctionCall/FunctionResponse blocks)
  // Strategy: find the last occurrence of a pattern that ends a function block and take what's after, to fetch the relevant response of the user
  const functionResponsePattern = /FunctionResponse\{[^}]*\}[^}]*\}/g;
  let lastIndex = 0;
  let match;
  while ((match = functionResponsePattern.exec(raw)) !== null) {
    lastIndex = match.index + match[0].length;
  }
  if (lastIndex > 0) {
    return raw.slice(lastIndex).trim();
  }
  return raw.trim();
}

const TypingIndicator = () => (
  <div style={styles.typingWrapper}>
    <div style={styles.botAvatar}>AI</div>
    <div style={styles.typingBubble}>
      <span style={{ ...styles.dot, animationDelay: '0ms' }} />
      <span style={{ ...styles.dot, animationDelay: '160ms' }} />
      <span style={{ ...styles.dot, animationDelay: '320ms' }} />
    </div>
  </div>
);

const Message = ({ msg }) => {
  const isUser = msg.role === 'user';
  return (
    <div style={{ ...styles.messageRow, justifyContent: isUser ? 'flex-end' : 'flex-start' }}>
      {!isUser && <div style={styles.botAvatar}>AI</div>}
      <div style={isUser ? styles.userBubble : styles.botBubble}>
        <p style={styles.messageText}>{msg.content}</p>
        <span style={styles.timestamp}>{msg.time}</span>
      </div>
      {isUser && <div style={styles.userAvatar}>You</div>}
    </div>
  );
};

export default function App() {
  const [userId] = useState(() => generateId('user'));
  const [sessionId] = useState(() => generateId('session'));
  const [messages, setMessages] = useState([
    {
      id: 'welcome',
      role: 'bot',
      content: 'Hello! I\'m your Order Support assistant. How may I help you today?',
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const bottomRef = useRef(null);
  const inputRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, loading]);

  const sendMessage = useCallback(async () => {
    const text = input.trim();
    if (!text || loading) return;

    const userMsg = {
      id: Date.now(),
      role: 'user',
      content: text,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };
    setMessages(prev => [...prev, userMsg]);
    setInput('');
    setLoading(true);

    try {
      const res = await fetch(`${BASE_URL}/agent`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId, sessionId, input: text }),
      });

      if (!res.ok) throw new Error(`Server error: ${res.status}`);
      const data = await res.json();
      const clean = extractMessage(data.response || '');

      setMessages(prev => [...prev, {
        id: Date.now() + 1,
        role: 'bot',
        content: clean || 'I received your message but had no response.',
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      }]);
    } catch (err) {
      setMessages(prev => [...prev, {
        id: Date.now() + 1,
        role: 'bot',
        content: `Sorry, I couldn't connect to the server. (${err.message})`,
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        error: true,
      }]);
    } finally {
      setLoading(false);
      inputRef.current?.focus();
    }
  }, [input, loading, userId, sessionId]);

  const handleKey = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  return (
    <div style={styles.root}>
      <style>{keyframes}</style>
      {/* Sidebar */}
      <aside style={styles.sidebar}>
        <div style={styles.logo}>
          <div style={styles.logoIcon}>✦</div>
          <span style={styles.logoText}>Assistant</span>
        </div>
        <div style={styles.sessionInfo}>
          <p style={styles.sessionLabel}>Session Info</p>
          <div style={styles.sessionRow}>
            <span style={styles.sessionKey}>User ID</span>
            <code style={styles.sessionVal}>{userId.slice(-8)}</code>
          </div>
          <div style={styles.sessionRow}>
            <span style={styles.sessionKey}>Session ID</span>
            <code style={styles.sessionVal}>{sessionId.slice(-8)}</code>
          </div>
          <div style={styles.sessionRow}>
            <span style={styles.sessionKey}>Status</span>
            <span style={{ ...styles.sessionVal, ...styles.statusDot }}>
              <span style={styles.dot2} />
              Active
            </span>
          </div>
        </div>
        <div style={styles.sidebarFooter}>
          <p style={styles.footerNote}>New IDs generated on every page load</p>
        </div>
      </aside>

      {/* Main chat */}
      <main style={styles.main}>
        <header style={styles.header}>
          <div>
            <h1 style={styles.headerTitle}>Order Support AI Agent</h1>
            <p style={styles.headerSub}>Powered by your Google ADK V1, Java and Gemini agent</p>
          </div>
          <div style={styles.headerEndpoint}>
            <span style={styles.endpointLabel}>Endpoint</span>
            <code style={styles.endpointUrl}>{BASE_URL}/agent</code>
          </div>
        </header>

        <div style={styles.messageArea}>
          {messages.map(msg => <Message key={msg.id} msg={msg} />)}
          {loading && <TypingIndicator />}
          <div ref={bottomRef} />
        </div>

        <div style={styles.inputArea}>
          <div style={styles.inputWrapper}>
            <textarea
              ref={inputRef}
              value={input}
              onChange={e => setInput(e.target.value)}
              onKeyDown={handleKey}
              placeholder="Type a message… (Enter to send)"
              style={styles.textarea}
              rows={1}
              disabled={loading}
            />
            <button
              onClick={sendMessage}
              disabled={!input.trim() || loading}
              style={{
                ...styles.sendBtn,
                ...((!input.trim() || loading) ? styles.sendBtnDisabled : {}),
              }}
            >
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <line x1="22" y1="2" x2="11" y2="13" />
                <polygon points="22 2 15 22 11 13 2 9 22 2" />
              </svg>
            </button>
          </div>
          <p style={styles.hint}>Shift+Enter for new line · Enter to send</p>
        </div>
      </main>
    </div>
  );
}

const keyframes = `
  @keyframes bounce {
    0%, 80%, 100% { transform: translateY(0); opacity: 0.4; }
    40% { transform: translateY(-6px); opacity: 1; }
  }
  @keyframes fadeSlideIn {
    from { opacity: 0; transform: translateY(8px); }
    to { opacity: 1; transform: translateY(0); }
  }
  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.4; }
  }
`;

const styles = {
  root: {
    display: 'flex',
    height: '100vh',
    fontFamily: "'DM Sans', sans-serif",
    background: '#0d0d0d',
    color: '#f0ede8',
    overflow: 'hidden',
  },
  sidebar: {
    width: '220px',
    minWidth: '220px',
    background: '#161616',
    borderRight: '1px solid #222',
    display: 'flex',
    flexDirection: 'column',
    padding: '24px 16px',
    gap: '32px',
  },
  logo: {
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
  },
  logoIcon: {
    fontSize: '20px',
    color: '#c8b8a2',
  },
  logoText: {
    fontSize: '15px',
    fontWeight: '500',
    letterSpacing: '0.04em',
    color: '#f0ede8',
  },
  sessionInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '10px',
  },
  sessionLabel: {
    fontSize: '10px',
    textTransform: 'uppercase',
    letterSpacing: '0.12em',
    color: '#555',
    margin: 0,
    marginBottom: '4px',
  },
  sessionRow: {
    display: 'flex',
    flexDirection: 'column',
    gap: '2px',
  },
  sessionKey: {
    fontSize: '11px',
    color: '#555',
  },
  sessionVal: {
    fontSize: '11px',
    color: '#998877',
    fontFamily: "'DM Mono', monospace",
    background: '#1e1e1e',
    padding: '2px 6px',
    borderRadius: '4px',
    border: '1px solid #2a2a2a',
    display: 'inline-block',
    marginTop: '1px',
  },
  statusDot: {
    display: 'flex',
    alignItems: 'center',
    gap: '5px',
    background: 'transparent',
    padding: 0,
    border: 'none',
    color: '#4d9e6e',
  },
  dot2: {
    display: 'inline-block',
    width: '6px',
    height: '6px',
    borderRadius: '50%',
    background: '#4d9e6e',
    animation: 'pulse 2s infinite',
  },
  sidebarFooter: {
    marginTop: 'auto',
  },
  footerNote: {
    fontSize: '10px',
    color: '#3a3a3a',
    lineHeight: '1.5',
    margin: 0,
  },
  main: {
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
    overflow: 'hidden',
  },
  header: {
    padding: '20px 28px',
    borderBottom: '1px solid #1e1e1e',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    background: '#0d0d0d',
  },
  headerTitle: {
    margin: 0,
    fontSize: '16px',
    fontWeight: '500',
    color: '#f0ede8',
  },
  headerSub: {
    margin: '2px 0 0',
    fontSize: '12px',
    color: '#444',
  },
  headerEndpoint: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-end',
    gap: '2px',
  },
  endpointLabel: {
    fontSize: '10px',
    color: '#444',
    textTransform: 'uppercase',
    letterSpacing: '0.1em',
  },
  endpointUrl: {
    fontSize: '11px',
    color: '#665544',
    fontFamily: "'DM Mono', monospace",
  },
  messageArea: {
    flex: 1,
    overflowY: 'auto',
    padding: '24px 28px',
    display: 'flex',
    flexDirection: 'column',
    gap: '16px',
  },
  messageRow: {
    display: 'flex',
    alignItems: 'flex-end',
    gap: '10px',
    animation: 'fadeSlideIn 0.25s ease',
  },
  botAvatar: {
    minWidth: '30px',
    height: '30px',
    borderRadius: '8px',
    background: '#1e1c1a',
    border: '1px solid #2e2c2a',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '9px',
    fontWeight: '500',
    color: '#c8b8a2',
    letterSpacing: '0.05em',
  },
  userAvatar: {
    minWidth: '30px',
    height: '30px',
    borderRadius: '8px',
    background: '#1a1e1c',
    border: '1px solid #2a2e2c',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '9px',
    fontWeight: '500',
    color: '#a2c8b8',
    letterSpacing: '0.05em',
  },
  botBubble: {
    background: '#161614',
    border: '1px solid #222',
    borderRadius: '12px 12px 12px 2px',
    padding: '12px 16px',
    maxWidth: '65%',
  },
  userBubble: {
    background: '#1a2820',
    border: '1px solid #2a3a32',
    borderRadius: '12px 12px 2px 12px',
    padding: '12px 16px',
    maxWidth: '65%',
  },
  messageText: {
    margin: 0,
    fontSize: '14px',
    lineHeight: '1.6',
    color: '#d8d4ce',
    whiteSpace: 'pre-wrap',
    wordBreak: 'break-word',
  },
  timestamp: {
    display: 'block',
    marginTop: '6px',
    fontSize: '10px',
    color: '#3a3a3a',
    textAlign: 'right',
  },
  typingWrapper: {
    display: 'flex',
    alignItems: 'flex-end',
    gap: '10px',
    animation: 'fadeSlideIn 0.2s ease',
  },
  typingBubble: {
    background: '#161614',
    border: '1px solid #222',
    borderRadius: '12px 12px 12px 2px',
    padding: '14px 18px',
    display: 'flex',
    gap: '5px',
    alignItems: 'center',
  },
  dot: {
    display: 'inline-block',
    width: '5px',
    height: '5px',
    borderRadius: '50%',
    background: '#555',
    animation: 'bounce 1.2s infinite',
  },
  inputArea: {
    padding: '16px 28px 20px',
    borderTop: '1px solid #1a1a1a',
  },
  inputWrapper: {
    display: 'flex',
    alignItems: 'flex-end',
    gap: '10px',
    background: '#161616',
    border: '1px solid #282828',
    borderRadius: '12px',
    padding: '10px 12px',
    transition: 'border-color 0.2s',
  },
  textarea: {
    flex: 1,
    background: 'transparent',
    border: 'none',
    outline: 'none',
    resize: 'none',
    fontFamily: "'DM Sans', sans-serif",
    fontSize: '14px',
    color: '#e0ddd8',
    lineHeight: '1.5',
    padding: 0,
    maxHeight: '120px',
    overflowY: 'auto',
  },
  sendBtn: {
    background: '#c8b8a2',
    border: 'none',
    borderRadius: '8px',
    width: '36px',
    height: '36px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    cursor: 'pointer',
    color: '#0d0d0d',
    flexShrink: 0,
    transition: 'background 0.15s, transform 0.1s',
  },
  sendBtnDisabled: {
    background: '#2a2a2a',
    color: '#444',
    cursor: 'not-allowed',
  },
  hint: {
    margin: '8px 0 0',
    fontSize: '11px',
    color: '#2e2e2e',
    textAlign: 'center',
  },
};

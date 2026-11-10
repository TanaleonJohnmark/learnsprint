import { useCallback, useEffect, useState } from 'react';
import { api } from './api.js';
import { GROUPS, RESOURCES, findResource } from './resources.js';
import Overview from './Overview.jsx';
import ResourcePage from './ResourcePage.jsx';

const readRoute = () => window.location.hash.replace(/^#/, '') || 'overview';

export default function App() {
  const [route, setRoute] = useState(readRoute);
  const [counts, setCounts] = useState(null);
  const [server, setServer] = useState('checking'); // checking | up | down
  const [toasts, setToasts] = useState([]);
  const [menuOpen, setMenuOpen] = useState(false);

  // The address after the # decides which screen is shown.
  useEffect(() => {
    const onHashChange = () => {
      setRoute(readRoute());
      setMenuOpen(false);
      document.getElementById('main')?.focus();
    };
    window.addEventListener('hashchange', onHashChange);
    return () => window.removeEventListener('hashchange', onHashChange);
  }, []);

  const refreshCounts = useCallback(async () => {
    try {
      setCounts(await api.counts());
      setServer('up');
    } catch (error) {
      setServer(error.status === 0 ? 'down' : 'up');
    }
  }, []);

  useEffect(() => {
    refreshCounts();
  }, [refreshCounts, route]);

  const notify = useCallback((message) => {
    const id = `${Date.now()}-${Math.random()}`;
    setToasts((current) => [...current, { id, message }]);
    window.setTimeout(() => {
      setToasts((current) => current.filter((toast) => toast.id !== id));
    }, 3600);
  }, []);

  const resource = findResource(route);

  return (
    <div className="shell">
      <a className="skip" href="#main">
        Skip to the content
      </a>

      <aside className="side">
        <div className="side-top">
          <a className="brand" href="#overview">
            LearnSprint
          </a>
          <button
            type="button"
            className="menu-btn"
            aria-expanded={menuOpen}
            aria-controls="side-nav"
            onClick={() => setMenuOpen((open) => !open)}
          >
            {menuOpen ? 'Close' : 'Menu'}
          </button>
        </div>

        <nav id="side-nav" className={menuOpen ? 'nav nav-open' : 'nav'} aria-label="Sections">
          <ul>
            <li>
              <a href="#overview" aria-current={!resource ? 'page' : undefined}>
                <span>Overview</span>
              </a>
            </li>
          </ul>
          {GROUPS.map((group) => (
            <div key={group}>
              <h2>{group}</h2>
              <ul>
                {RESOURCES.filter((r) => r.group === group).map((r) => (
                  <li key={r.path}>
                    <a href={`#${r.path}`} aria-current={route === r.path ? 'page' : undefined}>
                      <span>{r.plural}</span>
                      {counts && <span className="nav-count">{counts[r.countKey] ?? 0}</span>}
                    </a>
                  </li>
                ))}
              </ul>
            </div>
          ))}

          <p className={`server server-${server}`}>
            <span className="server-dot" aria-hidden="true" />
            {server === 'up' && 'Connected to the server'}
            {server === 'down' && 'Server not answering'}
            {server === 'checking' && 'Checking the server'}
          </p>
        </nav>
      </aside>

      <main id="main" tabIndex={-1}>
        {resource ? (
          <ResourcePage key={resource.path} resource={resource} notify={notify} onChanged={refreshCounts} />
        ) : (
          <Overview counts={counts} server={server} onRetry={refreshCounts} />
        )}
      </main>

      <div className="toasts" role="status" aria-live="polite">
        {toasts.map((toast) => (
          <p key={toast.id} className="toast">
            {toast.message}
          </p>
        ))}
      </div>
    </div>
  );
}

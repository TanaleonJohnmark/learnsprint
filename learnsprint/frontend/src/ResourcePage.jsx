import { useCallback, useEffect, useMemo, useState } from 'react';
import { api } from './api.js';
import { capitalize } from './resources.js';
import RecordForm from './RecordForm.jsx';
import ConfirmDelete from './ConfirmDelete.jsx';

// One screen that lists, adds, edits and deletes the records of one entity.
export default function ResourcePage({ resource, notify, onChanged }) {
  const [rows, setRows] = useState([]);
  const [state, setState] = useState('loading'); // loading | ready | error
  const [errorMessage, setErrorMessage] = useState('');
  const [query, setQuery] = useState('');
  const [form, setForm] = useState(null); // null, or { record } where record is null for a new one
  const [toDelete, setToDelete] = useState(null);

  const load = useCallback(async () => {
    try {
      setRows(await api.list(resource.path));
      setState('ready');
    } catch (error) {
      setErrorMessage(error.message);
      setState('error');
    }
  }, [resource.path]);

  useEffect(() => {
    load();
  }, [load]);

  const visible = useMemo(() => {
    const needle = query.trim().toLowerCase();
    if (!needle) return rows;
    return rows.filter((row) =>
      resource.columns.some((column) => String(column.value(row) ?? '').toLowerCase().includes(needle))
    );
  }, [rows, query, resource]);

  const label = capitalize(resource.singular);

  const afterSave = () => {
    setForm(null);
    notify(`${label} saved`);
    load();
    onChanged();
  };

  const afterDelete = () => {
    setToDelete(null);
    notify(`${label} deleted`);
    load();
    onChanged();
  };

  const countLine =
    state !== 'ready'
      ? ''
      : query.trim()
        ? `${visible.length} of ${rows.length} shown`
        : `${rows.length} in the database`;

  return (
    <>
      <header className="page-head">
        <div>
          <h1>{resource.plural}</h1>
          <p className="page-count" aria-live="polite">
            {countLine}
          </p>
        </div>
        <div className="page-tools">
          <label className="search">
            <span className="visually-hidden">Search {resource.plural.toLowerCase()}</span>
            <svg width="16" height="16" viewBox="0 0 16 16" aria-hidden="true">
              <circle cx="7" cy="7" r="4.5" fill="none" stroke="currentColor" strokeWidth="1.6" />
              <path d="M10.5 10.5L14 14" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" />
            </svg>
            <input
              type="search"
              placeholder="Search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
            />
          </label>
          <button type="button" className="btn btn-primary" onClick={() => setForm({ record: null })}>
            Add {resource.singular}
          </button>
        </div>
      </header>

      {state === 'loading' && <p className="notice">Loading {resource.plural.toLowerCase()}</p>}

      {state === 'error' && (
        <div className="notice notice-problem" role="alert">
          <p>{errorMessage}</p>
          <button
            type="button"
            className="btn"
            onClick={() => {
              setState('loading');
              load();
            }}
          >
            Try again
          </button>
        </div>
      )}

      {state === 'ready' && rows.length === 0 && (
        <div className="notice">
          <p>No {resource.plural.toLowerCase()} yet.</p>
          <button type="button" className="btn btn-primary" onClick={() => setForm({ record: null })}>
            Add the first {resource.singular}
          </button>
        </div>
      )}

      {state === 'ready' && rows.length > 0 && visible.length === 0 && (
        <div className="notice">
          <p>Nothing matches “{query.trim()}”.</p>
          <button type="button" className="btn" onClick={() => setQuery('')}>
            Clear the search
          </button>
        </div>
      )}

      {state === 'ready' && visible.length > 0 && (
        <div className="table-wrap">
          <table>
            <caption className="visually-hidden">{resource.plural}</caption>
            <thead>
              <tr>
                {resource.columns.map((column) => (
                  <th key={column.label} scope="col" className={column.align === 'right' ? 'num' : undefined}>
                    {column.label}
                  </th>
                ))}
                <th scope="col" className="actions-col">
                  <span className="visually-hidden">Actions</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {visible.map((row) => (
                <tr key={row[resource.idKey]}>
                  {resource.columns.map((column, index) => (
                    <Cell key={column.label} column={column} row={row} lead={index === 0} />
                  ))}
                  <td className="actions-col">
                    <button
                      type="button"
                      className="row-btn"
                      onClick={() => setForm({ record: row })}
                      aria-label={`Edit ${resource.title(row)}`}
                    >
                      Edit
                    </button>
                    <button
                      type="button"
                      className="row-btn row-btn-danger"
                      onClick={() => setToDelete(row)}
                      aria-label={`Delete ${resource.title(row)}`}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {form && (
        <RecordForm resource={resource} record={form.record} onClose={() => setForm(null)} onSaved={afterSave} />
      )}

      {toDelete && (
        <ConfirmDelete
          resource={resource}
          record={toDelete}
          onClose={() => setToDelete(null)}
          onDeleted={afterDelete}
        />
      )}
    </>
  );
}

function Cell({ column, row, lead }) {
  const value = column.value(row);
  const blank = value === null || value === undefined || value === '';

  if (column.kind === 'sync') {
    return (
      <td>
        <span className={value === 'Synced' ? 'pill pill-synced' : 'pill pill-waiting'}>{value}</span>
      </td>
    );
  }

  const className = [column.align === 'right' ? 'num' : '', lead ? 'lead' : ''].join(' ').trim() || undefined;
  return <td className={className}>{blank ? <span className="blank">None</span> : value}</td>;
}

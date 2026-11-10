import { useEffect, useRef, useState } from 'react';
import { api } from './api.js';

export default function ConfirmDelete({ resource, record, onClose, onDeleted }) {
  const dialogRef = useRef(null);
  const [working, setWorking] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const dialog = dialogRef.current;
    if (dialog && !dialog.open) dialog.showModal();
    dialog?.querySelector('[data-cancel]')?.focus();
  }, []);

  const close = () => dialogRef.current?.close();

  const remove = async () => {
    setWorking(true);
    setError('');
    try {
      await api.remove(resource.path, record[resource.idKey]);
      onDeleted();
    } catch (failure) {
      // 409 means other rows still point at this one (a foreign key).
      setError(
        failure.status === 409
          ? `This ${resource.singular} is still used by other records. Delete those records first, then try again.`
          : failure.message
      );
      setWorking(false);
    }
  };

  return (
    <dialog
      ref={dialogRef}
      className="confirm"
      aria-labelledby="confirm-title"
      onClose={onClose}
      onClick={(event) => {
        if (event.target === event.currentTarget) close();
      }}
    >
      <div className="confirm-inner">
        <h2 id="confirm-title">Delete this {resource.singular}?</h2>
        <p className="confirm-name">{resource.title(record)}</p>
        <p>It is removed from the database and can't be brought back.</p>
        {error && (
          <p className="alert" role="alert">
            {error}
          </p>
        )}
        <div className="confirm-actions">
          <button type="button" className="btn" data-cancel onClick={close}>
            Keep it
          </button>
          <button type="button" className="btn btn-danger" onClick={remove} disabled={working}>
            {working ? 'Deleting' : `Delete ${resource.singular}`}
          </button>
        </div>
      </div>
    </dialog>
  );
}

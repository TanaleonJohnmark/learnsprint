import { useEffect, useRef, useState } from 'react';
import { api } from './api.js';
import { today } from './format.js';
import { capitalize, findResource } from './resources.js';

// ---- turning a record into form values, and form values back into JSON

function initialValues(resource, record) {
  const values = {};
  for (const field of resource.fields) {
    const current = record ? record[field.name] : undefined;
    if (field.type === 'relation') {
      const target = findResource(field.to);
      values[field.name] = current ? String(current[target.idKey]) : '';
    } else if (field.type === 'boolean') {
      values[field.name] = Boolean(current);
    } else if (field.type === 'password') {
      values[field.name] = '';
    } else if (field.type === 'select') {
      values[field.name] = current ?? field.options[0];
    } else if (field.type === 'date') {
      values[field.name] = current ?? (!record && field.defaultToday ? today() : '');
    } else {
      values[field.name] = current === null || current === undefined ? '' : String(current);
    }
  }
  return values;
}

function validate(resource, values, isEdit) {
  const errors = {};
  for (const field of resource.fields) {
    const value = values[field.name];
    const empty = typeof value === 'string' && value.trim() === '';

    if (field.type === 'relation') {
      if (field.required && empty) errors[field.name] = 'Choose one from the list.';
      continue;
    }
    if (field.type === 'password') {
      if (field.requiredOnCreate && !isEdit && empty) errors[field.name] = 'Password is required.';
      continue;
    }
    if (field.required && empty) {
      errors[field.name] = `${field.label} is required.`;
      continue;
    }
    if (field.type === 'email' && !empty && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim())) {
      errors[field.name] = 'Enter an email address like name@cit.edu.';
    }
    if (field.type === 'number' && !empty) {
      const number = Number(value);
      if (!Number.isInteger(number)) {
        errors[field.name] = 'Use a whole number.';
      } else if (field.min !== undefined && number < field.min) {
        errors[field.name] = `Use ${field.min} or more.`;
      } else if (field.max !== undefined && number > field.max) {
        errors[field.name] = `Use ${field.max} or less.`;
      }
    }
  }
  return errors;
}

function toPayload(resource, values, isEdit) {
  const payload = {};
  for (const field of resource.fields) {
    const value = values[field.name];
    if (field.type === 'relation') {
      const target = findResource(field.to);
      // The backend links records by id: { "course": { "courseId": 1 } }
      payload[field.name] = value === '' ? null : { [target.idKey]: Number(value) };
    } else if (field.type === 'boolean') {
      payload[field.name] = value;
    } else if (field.type === 'number') {
      payload[field.name] = value === '' ? null : Number(value);
    } else if (field.type === 'password') {
      if (!(isEdit && value === '')) payload[field.name] = value;
    } else {
      const text = typeof value === 'string' ? value.trim() : value;
      payload[field.name] = text === '' ? null : text;
    }
  }
  return payload;
}

// ---- the panel

export default function RecordForm({ resource, record, onClose, onSaved }) {
  const isEdit = Boolean(record);
  const dialogRef = useRef(null);
  const [values, setValues] = useState(() => initialValues(resource, record));
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [saving, setSaving] = useState(false);
  const [options, setOptions] = useState({});
  const [optionsReady, setOptionsReady] = useState(false);

  // Open as a modal dialog: the browser then traps focus and handles Escape.
  useEffect(() => {
    const dialog = dialogRef.current;
    if (dialog && !dialog.open) dialog.showModal();
    dialog?.querySelector('[data-first-field]')?.focus();
  }, []);

  // Load the choices of every dropdown that points at another table.
  useEffect(() => {
    let cancelled = false;
    const relations = resource.fields.filter((f) => f.type === 'relation');
    Promise.all(relations.map((f) => api.list(findResource(f.to).path)))
      .then((lists) => {
        if (cancelled) return;
        const loaded = {};
        relations.forEach((f, i) => {
          loaded[f.name] = lists[i];
        });
        setOptions(loaded);
        setOptionsReady(true);
      })
      .catch((error) => {
        if (!cancelled) setServerError(error.message);
      });
    return () => {
      cancelled = true;
    };
  }, [resource]);

  const close = () => dialogRef.current?.close();

  const setValue = (name, value) => {
    setValues((previous) => ({ ...previous, [name]: value }));
    setErrors((previous) => {
      if (!previous[name]) return previous;
      const next = { ...previous };
      delete next[name];
      return next;
    });
  };

  const submit = async (event) => {
    event.preventDefault();
    const found = validate(resource, values, isEdit);
    setErrors(found);
    const firstInvalid = resource.fields.find((f) => found[f.name]);
    if (firstInvalid) {
      document.getElementById(`field-${firstInvalid.name}`)?.focus();
      return;
    }
    setSaving(true);
    setServerError('');
    try {
      const payload = toPayload(resource, values, isEdit);
      if (isEdit) {
        await api.update(resource.path, record[resource.idKey], payload);
      } else {
        await api.create(resource.path, payload);
      }
      onSaved();
    } catch (error) {
      setServerError(error.message);
      setSaving(false);
    }
  };

  const heading = `${isEdit ? 'Edit' : 'Add'} ${resource.singular}`;

  return (
    <dialog
      ref={dialogRef}
      className="drawer"
      aria-labelledby="record-form-title"
      onClose={onClose}
      onClick={(event) => {
        if (event.target === event.currentTarget) close();
      }}
    >
      <form className="drawer-form" onSubmit={submit} noValidate>
        <header className="drawer-head">
          <h2 id="record-form-title">{heading}</h2>
          <button type="button" className="icon-btn" onClick={close} aria-label="Close without saving">
            <svg width="18" height="18" viewBox="0 0 18 18" aria-hidden="true">
              <path d="M4 4l10 10M14 4L4 14" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
            </svg>
          </button>
        </header>

        <div className="drawer-body">
          {serverError && (
            <p className="alert" role="alert">
              {serverError}
            </p>
          )}

          {resource.fields.map((field, index) => (
            <Field
              key={field.name}
              field={field}
              value={values[field.name]}
              error={errors[field.name]}
              isEdit={isEdit}
              first={index === 0}
              choices={options[field.name]}
              choicesReady={optionsReady}
              onChange={(value) => setValue(field.name, value)}
            />
          ))}
        </div>

        <footer className="drawer-foot">
          <button type="button" className="btn" onClick={close}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? 'Saving' : `Save ${resource.singular}`}
          </button>
        </footer>
      </form>
    </dialog>
  );
}

function Field({ field, value, error, isEdit, first, choices, choicesReady, onChange }) {
  const id = `field-${field.name}`;
  const help = isEdit && field.editHelp ? field.editHelp : field.help;
  const required = field.required || (field.requiredOnCreate && !isEdit);
  const describedBy = [error ? `${id}-error` : null, help ? `${id}-help` : null].filter(Boolean).join(' ') || undefined;
  const shared = {
    id,
    'aria-invalid': error ? true : undefined,
    'aria-describedby': describedBy,
    'data-first-field': first ? '' : undefined,
  };

  if (field.type === 'boolean') {
    return (
      <div className="field">
        <label className="check">
          <input type="checkbox" {...shared} checked={value} onChange={(e) => onChange(e.target.checked)} />
          <span>{field.label}</span>
        </label>
        {help && (
          <p className="field-help" id={`${id}-help`}>
            {help}
          </p>
        )}
      </div>
    );
  }

  let control;
  if (field.type === 'relation') {
    const target = findResource(field.to);
    const list = choices ?? [];
    const nothingToChoose = choicesReady && list.length === 0;
    control = (
      <>
        <select {...shared} value={value} disabled={!choicesReady} onChange={(e) => onChange(e.target.value)}>
          <option value="">{choicesReady ? 'Choose one' : 'Loading'}</option>
          {list.map((item) => (
            <option key={item[target.idKey]} value={String(item[target.idKey])}>
              {target.title(item)}
            </option>
          ))}
        </select>
        {nothingToChoose && (
          <p className="field-help">
            There is nothing to choose yet. Add one under {target.plural} first.
          </p>
        )}
      </>
    );
  } else if (field.type === 'select') {
    control = (
      <select {...shared} value={value} onChange={(e) => onChange(e.target.value)}>
        {field.options.map((option) => (
          <option key={option} value={option}>
            {capitalize(option.toLowerCase().replace('_', ' '))}
          </option>
        ))}
      </select>
    );
  } else if (field.type === 'textarea') {
    control = (
      <textarea {...shared} rows={field.rows ?? 4} value={value} onChange={(e) => onChange(e.target.value)} />
    );
  } else {
    control = (
      <input
        {...shared}
        type={field.type}
        value={value}
        min={field.min}
        max={field.max}
        inputMode={field.type === 'number' ? 'numeric' : undefined}
        autoComplete={field.type === 'password' ? 'new-password' : 'off'}
        onChange={(e) => onChange(e.target.value)}
      />
    );
  }

  return (
    <div className="field">
      <label htmlFor={id}>
        {field.label}
        {!required && <span className="optional"> (optional)</span>}
      </label>
      {control}
      {help && (
        <p className="field-help" id={`${id}-help`}>
          {help}
        </p>
      )}
      {error && (
        <p className="field-error" id={`${id}-error`}>
          {error}
        </p>
      )}
    </div>
  );
}

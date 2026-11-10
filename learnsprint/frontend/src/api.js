// Every request goes to /api on the same address the page came from.
// In development, vite.config.js forwards /api to Spring Boot on port 8080.
const BASE = '/api';

const UNREACHABLE =
  'The server is not answering. Start LearnsprintApplication and MySQL, then try again.';

export class ApiError extends Error {
  constructor(message, status) {
    super(message);
    this.status = status;
  }
}

async function request(path, options = {}) {
  let response;
  try {
    response = await fetch(`${BASE}/${path}`, {
      headers: { 'Content-Type': 'application/json' },
      ...options,
    });
  } catch {
    throw new ApiError(UNREACHABLE, 0);
  }

  const text = await response.text();
  let data = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = null;
    }
  }

  if (!response.ok) {
    // The backend answers errors as { "error": "..." }.
    if (data && typeof data.error === 'string' && response.status < 500) {
      throw new ApiError(data.error, response.status);
    }
    if (response.status >= 500 && !data) {
      throw new ApiError(UNREACHABLE, 0);
    }
    throw new ApiError(`The server answered with an error (status ${response.status}).`, response.status);
  }
  return data;
}

export const api = {
  list: (path) => request(path),
  create: (path, body) => request(path, { method: 'POST', body: JSON.stringify(body) }),
  update: (path, id, body) => request(`${path}/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  remove: (path, id) => request(`${path}/${id}`, { method: 'DELETE' }),
  counts: () => request('dashboard'),
};

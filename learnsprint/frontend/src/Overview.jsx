import { useEffect, useState } from 'react';
import { api } from './api.js';
import { formatDate, fullName } from './format.js';

// The path a piece of course content follows, in the order the ERD nests it.
const CHAIN = [
  { path: 'courses', key: 'courses', label: 'Courses', note: 'each taught by one faculty member' },
  { path: 'tracks', key: 'tracks', label: 'Tracks', note: 'split a course into topics' },
  { path: 'lessons', key: 'lessons', label: 'Lessons', note: 'five to ten minutes each' },
  { path: 'quizzes', key: 'quizzes', label: 'Quizzes', note: 'one for a lesson' },
  { path: 'questions', key: 'questions', label: 'Questions', note: 'answered inside a quiz' },
];

const TALLY = [
  { path: 'students', key: 'students', label: 'Students' },
  { path: 'faculty', key: 'faculty', label: 'Faculty' },
  { path: 'admins', key: 'admins', label: 'Admins' },
  { path: 'enrollments', key: 'enrollments', label: 'Enrollments' },
  { path: 'lesson-progress', key: 'lessonProgress', label: 'Lesson progress records' },
  { path: 'quiz-attempts', key: 'quizAttempts', label: 'Quiz attempts' },
  { path: 'badges', key: 'badges', label: 'Badges' },
  { path: 'student-badges', key: 'studentBadges', label: 'Badges awarded' },
];

export default function Overview({ counts, server, onRetry }) {
  const [waiting, setWaiting] = useState(null);

  useEffect(() => {
    if (server !== 'up') return undefined;
    let cancelled = false;
    Promise.all([api.list('lesson-progress'), api.list('quiz-attempts')])
      .then(([progress, attempts]) => {
        if (cancelled) return;
        const items = [
          ...progress
            .filter((p) => !p.isSynced)
            .map((p) => ({
              id: `p${p.progressId}`,
              text: `${fullName(p.student)} ${p.status === 'COMPLETED' ? 'finished' : 'started'} ${p.lesson?.title ?? 'a lesson'}`,
              date: p.dateCompleted,
            })),
          ...attempts
            .filter((a) => !a.isSynced)
            .map((a) => ({
              id: `a${a.attemptId}`,
              text: `${fullName(a.student)} scored ${a.score ?? 0} on ${a.quiz?.title ?? 'a quiz'}`,
              date: a.dateTaken,
            })),
        ];
        setWaiting(items);
      })
      .catch(() => {
        if (!cancelled) setWaiting([]);
      });
    return () => {
      cancelled = true;
    };
  }, [server, counts]);

  if (server === 'down') {
    return (
      <>
        <header className="page-head">
          <div>
            <h1>Overview</h1>
          </div>
        </header>
        <div className="notice notice-problem" role="alert">
          <p>
            <strong>The server is not answering.</strong> This page reads its data from the Spring Boot
            backend.
          </p>
          <ol>
            <li>Start MySQL in the XAMPP Control Panel.</li>
            <li>Run LearnsprintApplication in IntelliJ and wait for “Started LearnsprintApplication”.</li>
            <li>Come back here and try again.</li>
          </ol>
          <button type="button" className="btn btn-primary" onClick={onRetry}>
            Try again
          </button>
        </div>
      </>
    );
  }

  const show = (key) => (counts ? counts[key] ?? 0 : '');

  return (
    <>
      <header className="page-head">
        <div>
          <h1>Overview</h1>
          <p className="page-count">Read live from the LearnSprint database</p>
        </div>
      </header>

      <section aria-labelledby="chain-title" className="block">
        <h2 id="chain-title">How course content is built</h2>
        <ol className="chain">
          {CHAIN.map((step) => (
            <li key={step.path}>
              <a href={`#${step.path}`}>
                <span className="chain-count">{show(step.key)}</span>
                <span className="chain-label">{step.label}</span>
                <span className="chain-note">{step.note}</span>
              </a>
            </li>
          ))}
        </ol>
      </section>

      <div className="split">
        <section aria-labelledby="waiting-title" className="panel panel-waiting">
          <div className="panel-head">
            <h2 id="waiting-title">Waiting to sync</h2>
            {waiting && <span className="panel-total">{waiting.length}</span>}
          </div>
          <p className="panel-lede">
            Lessons and quizzes finished offline that have not reached the server yet.
          </p>
          {!waiting && <p className="quiet">Loading</p>}
          {waiting && waiting.length === 0 && <p className="quiet">Nothing is waiting. Every record has synced.</p>}
          {waiting && waiting.length > 0 && (
            <ul className="waiting-list">
              {waiting.map((item) => (
                <li key={item.id}>
                  <span>{item.text}</span>
                  <span className="waiting-date">{item.date ? formatDate(item.date) : 'No date yet'}</span>
                </li>
              ))}
            </ul>
          )}
          <p className="panel-links">
            <a href="#lesson-progress">Open lesson progress</a>
            <a href="#quiz-attempts">Open quiz attempts</a>
          </p>
        </section>

        <section aria-labelledby="tally-title" className="panel">
          <div className="panel-head">
            <h2 id="tally-title">People and activity</h2>
          </div>
          <ul className="tally">
            {TALLY.map((row) => (
              <li key={row.path}>
                <a href={`#${row.path}`}>
                  <span>{row.label}</span>
                  <span className="tally-count">{show(row.key)}</span>
                </a>
              </li>
            ))}
          </ul>
        </section>
      </div>
    </>
  );
}

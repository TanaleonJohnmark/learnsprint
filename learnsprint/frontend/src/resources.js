import { formatDate, fullName } from './format.js';

// One entry per entity of the ERD. The table, the form and the navigation are
// all built from this description, so adding a field here is enough to show it.
//
//   path     - the backend endpoint: /api/{path}
//   idKey    - the primary key property in the JSON
//   countKey - the matching key in GET /api/dashboard
//   title    - how one record is named in dropdowns and in the delete dialog
//   fields   - the inputs of the add/edit form
//   columns  - the columns of the table

const personFields = [
  { name: 'fname', label: 'First name', type: 'text', required: true },
  { name: 'lname', label: 'Last name', type: 'text', required: true },
  { name: 'email', label: 'Email', type: 'email', required: true },
  {
    name: 'password',
    label: 'Password',
    type: 'password',
    requiredOnCreate: true,
    editHelp: 'Leave empty to keep the current password.',
  },
];

const syncColumn = { label: 'Sync', kind: 'sync', value: (r) => (r.isSynced ? 'Synced' : 'Waiting') };

const syncField = {
  name: 'isSynced',
  label: 'Already synced to the server',
  type: 'boolean',
  help: 'Leave unticked for work done offline that is still waiting to upload.',
};

export const RESOURCES = [
  // ------------------------------------------------------------ People
  {
    path: 'students',
    idKey: 'userId',
    countKey: 'students',
    group: 'People',
    plural: 'Students',
    singular: 'student',
    title: fullName,
    fields: [
      ...personFields,
      { name: 'schoolId', label: 'School ID', type: 'text' },
      { name: 'program', label: 'Program', type: 'text' },
      { name: 'yearLevel', label: 'Year level', type: 'number', min: 1, max: 6 },
      { name: 'totalPoints', label: 'Total points', type: 'number', min: 0 },
      { name: 'currentStreak', label: 'Current streak (days)', type: 'number', min: 0 },
    ],
    columns: [
      { label: 'Name', value: fullName },
      { label: 'School ID', value: (r) => r.schoolId },
      { label: 'Program', value: (r) => r.program },
      { label: 'Year', value: (r) => r.yearLevel, align: 'right' },
      { label: 'Points', value: (r) => r.totalPoints, align: 'right' },
      { label: 'Streak', value: (r) => r.currentStreak, align: 'right' },
    ],
  },
  {
    path: 'faculty',
    idKey: 'userId',
    countKey: 'faculty',
    group: 'People',
    plural: 'Faculty',
    singular: 'faculty member',
    title: fullName,
    fields: [
      ...personFields,
      { name: 'employeeNo', label: 'Employee number', type: 'text' },
      { name: 'department', label: 'Department', type: 'text' },
    ],
    columns: [
      { label: 'Name', value: fullName },
      { label: 'Employee number', value: (r) => r.employeeNo },
      { label: 'Department', value: (r) => r.department },
      { label: 'Email', value: (r) => r.email },
    ],
  },
  {
    path: 'admins',
    idKey: 'userId',
    countKey: 'admins',
    group: 'People',
    plural: 'Admins',
    singular: 'admin',
    title: fullName,
    fields: [
      ...personFields,
      { name: 'adminLevel', label: 'Admin level', type: 'select', options: ['SUPER', 'STAFF'] },
    ],
    columns: [
      { label: 'Name', value: fullName },
      { label: 'Email', value: (r) => r.email },
      { label: 'Level', value: (r) => r.adminLevel },
    ],
  },

  // ------------------------------------------------------------ Course content
  {
    path: 'courses',
    idKey: 'courseId',
    countKey: 'courses',
    group: 'Course content',
    plural: 'Courses',
    singular: 'course',
    title: (r) => (r ? `${r.courseCode} ${r.title}` : ''),
    fields: [
      { name: 'courseCode', label: 'Course code', type: 'text', required: true },
      { name: 'title', label: 'Title', type: 'text', required: true },
      { name: 'description', label: 'Description', type: 'textarea' },
      { name: 'faculty', label: 'Taught by', type: 'relation', to: 'faculty', required: true },
    ],
    columns: [
      { label: 'Code', value: (r) => r.courseCode },
      { label: 'Title', value: (r) => r.title },
      { label: 'Taught by', value: (r) => fullName(r.faculty) },
    ],
  },
  {
    path: 'tracks',
    idKey: 'trackId',
    countKey: 'tracks',
    group: 'Course content',
    plural: 'Tracks',
    singular: 'track',
    title: (r) => (r ? `${r.title} (${r.course?.courseCode ?? 'no course'})` : ''),
    fields: [
      { name: 'title', label: 'Title', type: 'text', required: true },
      { name: 'course', label: 'Course', type: 'relation', to: 'courses', required: true },
      { name: 'sequenceNo', label: 'Order in the course', type: 'number', min: 1 },
    ],
    columns: [
      { label: 'Title', value: (r) => r.title },
      { label: 'Course', value: (r) => r.course?.courseCode },
      { label: 'Order', value: (r) => r.sequenceNo, align: 'right' },
    ],
  },
  {
    path: 'lessons',
    idKey: 'lessonId',
    countKey: 'lessons',
    group: 'Course content',
    plural: 'Lessons',
    singular: 'lesson',
    title: (r) => (r ? r.title : ''),
    fields: [
      { name: 'title', label: 'Title', type: 'text', required: true },
      { name: 'track', label: 'Track', type: 'relation', to: 'tracks', required: true },
      { name: 'sequenceNo', label: 'Order in the track', type: 'number', min: 1 },
      { name: 'durationMin', label: 'Reading time (minutes)', type: 'number', min: 1 },
      { name: 'content', label: 'Lesson content', type: 'textarea', rows: 7 },
    ],
    columns: [
      { label: 'Title', value: (r) => r.title },
      { label: 'Track', value: (r) => r.track?.title },
      { label: 'Order', value: (r) => r.sequenceNo, align: 'right' },
      { label: 'Minutes', value: (r) => r.durationMin, align: 'right' },
    ],
  },
  {
    path: 'quizzes',
    idKey: 'quizId',
    countKey: 'quizzes',
    group: 'Course content',
    plural: 'Quizzes',
    singular: 'quiz',
    title: (r) => (r ? r.title : ''),
    fields: [
      { name: 'title', label: 'Title', type: 'text', required: true },
      {
        name: 'lesson',
        label: 'Lesson',
        type: 'relation',
        to: 'lessons',
        required: true,
        help: 'A lesson can have only one quiz.',
      },
      { name: 'passingScore', label: 'Passing score', type: 'number', min: 0 },
    ],
    columns: [
      { label: 'Title', value: (r) => r.title },
      { label: 'Lesson', value: (r) => r.lesson?.title },
      { label: 'Passing score', value: (r) => r.passingScore, align: 'right' },
    ],
  },
  {
    path: 'questions',
    idKey: 'questionId',
    countKey: 'questions',
    group: 'Course content',
    plural: 'Questions',
    singular: 'question',
    title: (r) => (r ? r.questionText : ''),
    fields: [
      { name: 'questionText', label: 'Question', type: 'textarea', rows: 3, required: true },
      { name: 'quiz', label: 'Quiz', type: 'relation', to: 'quizzes', required: true },
      { name: 'choices', label: 'Choices', type: 'text', help: 'Separate the choices with a vertical bar, for example: GET | POST | PUT' },
      { name: 'correctAnswer', label: 'Correct answer', type: 'text' },
    ],
    columns: [
      { label: 'Question', value: (r) => r.questionText },
      { label: 'Correct answer', value: (r) => r.correctAnswer },
      { label: 'Quiz', value: (r) => r.quiz?.title },
    ],
  },

  // ------------------------------------------------------------ Learner activity
  {
    path: 'enrollments',
    idKey: 'enrollmentId',
    countKey: 'enrollments',
    group: 'Learner activity',
    plural: 'Enrollments',
    singular: 'enrollment',
    title: (r) => (r ? `${fullName(r.student)} in ${r.course?.courseCode ?? ''}` : ''),
    fields: [
      { name: 'student', label: 'Student', type: 'relation', to: 'students', required: true },
      { name: 'course', label: 'Course', type: 'relation', to: 'courses', required: true },
      { name: 'dateEnrolled', label: 'Date enrolled', type: 'date', defaultToday: true },
    ],
    columns: [
      { label: 'Student', value: (r) => fullName(r.student) },
      { label: 'Course', value: (r) => r.course?.courseCode },
      { label: 'Enrolled', value: (r) => formatDate(r.dateEnrolled) },
    ],
  },
  {
    path: 'lesson-progress',
    idKey: 'progressId',
    countKey: 'lessonProgress',
    group: 'Learner activity',
    plural: 'Lesson progress',
    singular: 'progress record',
    title: (r) => (r ? `${fullName(r.student)} on ${r.lesson?.title ?? ''}` : ''),
    fields: [
      { name: 'student', label: 'Student', type: 'relation', to: 'students', required: true },
      { name: 'lesson', label: 'Lesson', type: 'relation', to: 'lessons', required: true },
      { name: 'status', label: 'Status', type: 'select', options: ['IN_PROGRESS', 'COMPLETED'] },
      { name: 'dateCompleted', label: 'Date completed', type: 'date' },
      syncField,
    ],
    columns: [
      { label: 'Student', value: (r) => fullName(r.student) },
      { label: 'Lesson', value: (r) => r.lesson?.title },
      { label: 'Status', value: (r) => (r.status === 'COMPLETED' ? 'Completed' : 'In progress') },
      { label: 'Completed', value: (r) => formatDate(r.dateCompleted) },
      syncColumn,
    ],
  },
  {
    path: 'quiz-attempts',
    idKey: 'attemptId',
    countKey: 'quizAttempts',
    group: 'Learner activity',
    plural: 'Quiz attempts',
    singular: 'quiz attempt',
    title: (r) => (r ? `${fullName(r.student)} on ${r.quiz?.title ?? ''}` : ''),
    fields: [
      { name: 'student', label: 'Student', type: 'relation', to: 'students', required: true },
      { name: 'quiz', label: 'Quiz', type: 'relation', to: 'quizzes', required: true },
      { name: 'score', label: 'Score', type: 'number', min: 0 },
      { name: 'dateTaken', label: 'Date taken', type: 'date', defaultToday: true },
      syncField,
    ],
    columns: [
      { label: 'Student', value: (r) => fullName(r.student) },
      { label: 'Quiz', value: (r) => r.quiz?.title },
      { label: 'Score', value: (r) => r.score, align: 'right' },
      { label: 'Taken', value: (r) => formatDate(r.dateTaken) },
      syncColumn,
    ],
  },

  // ------------------------------------------------------------ Rewards
  {
    path: 'badges',
    idKey: 'badgeId',
    countKey: 'badges',
    group: 'Rewards',
    plural: 'Badges',
    singular: 'badge',
    title: (r) => (r ? r.name : ''),
    fields: [
      { name: 'name', label: 'Name', type: 'text', required: true },
      { name: 'description', label: 'Description', type: 'textarea', rows: 3 },
      { name: 'criteria', label: 'How it is earned', type: 'text' },
    ],
    columns: [
      { label: 'Name', value: (r) => r.name },
      { label: 'Description', value: (r) => r.description },
      { label: 'How it is earned', value: (r) => r.criteria },
    ],
  },
  {
    path: 'student-badges',
    idKey: 'studentBadgeId',
    countKey: 'studentBadges',
    group: 'Rewards',
    plural: 'Badges awarded',
    singular: 'badge award',
    title: (r) => (r ? `${r.badge?.name ?? ''} for ${fullName(r.student)}` : ''),
    fields: [
      { name: 'student', label: 'Student', type: 'relation', to: 'students', required: true },
      { name: 'badge', label: 'Badge', type: 'relation', to: 'badges', required: true },
      { name: 'dateEarned', label: 'Date earned', type: 'date', defaultToday: true },
      { name: 'verificationCode', label: 'Verification code', type: 'text' },
    ],
    columns: [
      { label: 'Student', value: (r) => fullName(r.student) },
      { label: 'Badge', value: (r) => r.badge?.name },
      { label: 'Earned', value: (r) => formatDate(r.dateEarned) },
      { label: 'Verification code', value: (r) => r.verificationCode },
    ],
  },
];

export const GROUPS = ['People', 'Course content', 'Learner activity', 'Rewards'];

export const findResource = (path) => RESOURCES.find((r) => r.path === path);

export const capitalize = (text) => text.charAt(0).toUpperCase() + text.slice(1);

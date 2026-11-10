# LearnSprint

CSIT321, Section G5, Software Project Increment 1
Team LearnSprint: Johnmark C. Tanaleon, Vince Esgana, Carlos Enicuela

A web application with a Spring Boot backend and a React frontend. Increment 1
delivers create, read, update and delete for every core entity of the ERD.

| Item | Value |
|---|---|
| Group | `com.csit321g5.learnsprint` |
| Artifact | `learnsprint` |
| Backend | Spring Boot 3.3.5, Spring Data JPA, Java 17 or newer |
| Frontend | React 18 with Vite, in the `frontend` folder |
| Database | MySQL from XAMPP, schema `learnsprint_db` |

## Run it

1. Start **MySQL** in the XAMPP Control Panel.
2. Open this `learnsprint` folder in IntelliJ IDEA and wait for Maven to finish.
3. Run `LearnsprintApplication`.
4. Wait for `Started LearnsprintApplication` in the console.
5. Open **http://localhost:8080** in the browser.

The database, its tables and the sample data are created on the first run.
The React app is already built into `src/main/resources/static`, so Spring Boot
serves it and Node.js is not needed for these five steps.

## Run the React app in development mode (optional)

Use this to show or change the React source. Keep the backend running, then in
a terminal:

```
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173**. Requests to `/api` are forwarded to the backend
on port 8080 (see `frontend/vite.config.js`).

After changing the React code, `npm run build` writes the new version into
`src/main/resources/static`. Restart the backend to serve it on port 8080.

## Backend structure

```
src/main/java/com/csit321g5/learnsprint
├── LearnsprintApplication.java   starts the application
├── entity        one class per ERD entity; each maps to a table
├── repository    one interface per entity; extends JpaRepository
├── service       the rules: validation, linking records, create/read/update/delete
├── controller    the REST endpoints under /api
├── exception     the two custom exceptions and the handler that turns them into JSON
└── config        CORS settings and the sample data seeder
```

How one request travels:

```
React or Postman  ->  Controller  ->  Service  ->  Repository  ->  MySQL
   (HTTP + JSON)      (the URL)      (the rules)   (the queries)
```

- A **controller** only receives the request and returns the answer.
- A **service** checks the required values, swaps a linked id for the real row,
  and decides what is saved. Keeping the rules here means they are written once.
- A **repository** extends `JpaRepository`, which already provides `save`,
  `findAll`, `findById`, `existsById`, `deleteById` and `count`. No SQL is written.
- `GlobalExceptionHandler` answers every failure as `{"error": "..."}`:
  404 for an id that does not exist, 400 for a missing required value,
  409 when the database refuses a change (a duplicate, or a row still in use).

### The User supertype

`User` is an abstract entity using JOINED inheritance, which makes the database
match the ERD: the shared attributes are in `app_user`, and `student`, `faculty`
and `admin` each have their own table sharing the same primary key.

The specialization is disjoint and total: an account is exactly one of the
three. The discriminator is the `role` column of `app_user`, filled with
`STUDENT`, `FACULTY` or `ADMIN`. The table is named `app_user` because `user`
is a reserved word in MySQL.

### Relationships

A child holds a foreign key to its parent with `@ManyToOne` and `@JoinColumn`,
for example `Course.faculty` is the column `faculty_id`. `Quiz.lesson` is
`@OneToOne` because the ERD allows one quiz for a lesson. A request links a
record by sending the parent's id, and the service loads the real parent row.

## Endpoints

Every entity has the same five operations:

| Operation | Request |
|---|---|
| Create | `POST /api/{resource}` |
| Read all | `GET /api/{resource}` |
| Read one | `GET /api/{resource}/{id}` |
| Update | `PUT /api/{resource}/{id}` |
| Delete | `DELETE /api/{resource}/{id}` |

| Entity | Resource |
|---|---|
| Student | `/api/students` |
| Faculty | `/api/faculty` |
| Admin | `/api/admins` |
| Course | `/api/courses` |
| Track | `/api/tracks` |
| Lesson | `/api/lessons` |
| Quiz | `/api/quizzes` |
| Question | `/api/questions` |
| Enrollment | `/api/enrollments` |
| LessonProgress | `/api/lesson-progress` |
| QuizAttempt | `/api/quiz-attempts` |
| Badge | `/api/badges` |
| StudentBadge | `/api/student-badges` |

`GET /api/dashboard` returns the number of rows in every table for the overview page.

Example, creating a course taught by the faculty member with id 2:

```
POST http://localhost:8080/api/courses
Content-Type: application/json

{
  "courseCode": "IT365",
  "title": "Data Analytics 1",
  "description": "Introduction to analytics tooling.",
  "faculty": { "userId": 2 }
}
```

More ready-to-send requests are in `requests.http`.

## Known limits of Increment 1

- There is no login yet. Passwords are stored as plain text and are never sent
  back in a response; hashing and role-based access are planned for the next increment.
- The offline download and synchronization feature is not built yet. The
  `isSynced` column it depends on already exists on `lesson_progress` and `quiz_attempt`.

## If something goes wrong

| What you see | What to do |
|---|---|
| `Communications link failure` | MySQL is not running. Start it in XAMPP and run again. |
| `Port 8080 was already in use` | Another copy is still running. Stop it with the red square in IntelliJ. |
| `Access denied for user 'root'` | Your MySQL has a password. Put it after `spring.datasource.password=`. |
| Red imports in the code | Maven has not finished. Open the Maven panel and press Reload. |
| The page says the server is not answering | The backend stopped. Run `LearnsprintApplication` again. |

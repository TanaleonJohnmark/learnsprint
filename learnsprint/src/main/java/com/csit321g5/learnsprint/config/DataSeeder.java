package com.csit321g5.learnsprint.config;

import com.csit321g5.learnsprint.entity.*;
import com.csit321g5.learnsprint.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Fills an empty database with sample records the first time the application
 * starts, so no table is empty during checking. It does nothing once the
 * student table has rows. Turn it off with learnsprint.seed=false.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AdminRepository adminRepository;
    private final CourseRepository courseRepository;
    private final TrackRepository trackRepository;
    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final BadgeRepository badgeRepository;
    private final StudentBadgeRepository studentBadgeRepository;

    @Value("${learnsprint.seed:true}")
    private boolean seedEnabled;

    public DataSeeder(StudentRepository studentRepository,
                      FacultyRepository facultyRepository,
                      AdminRepository adminRepository,
                      CourseRepository courseRepository,
                      TrackRepository trackRepository,
                      LessonRepository lessonRepository,
                      QuizRepository quizRepository,
                      QuestionRepository questionRepository,
                      EnrollmentRepository enrollmentRepository,
                      LessonProgressRepository lessonProgressRepository,
                      QuizAttemptRepository quizAttemptRepository,
                      BadgeRepository badgeRepository,
                      StudentBadgeRepository studentBadgeRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.adminRepository = adminRepository;
        this.courseRepository = courseRepository;
        this.trackRepository = trackRepository;
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.badgeRepository = badgeRepository;
        this.studentBadgeRepository = studentBadgeRepository;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled || studentRepository.count() > 0) {
            return;
        }
        try {
            seed();
            System.out.println("LearnSprint sample data created.");
        } catch (RuntimeException ex) {
            // A seeding problem must never stop the application from starting.
            System.out.println("LearnSprint sample data was not created: " + ex.getMessage());
        }
    }

    private void seed() {
        LocalDate today = LocalDate.now();

        // ---- accounts: one admin, two faculty, six students
        Admin admin = new Admin();
        admin.setFname("System");
        admin.setLname("Administrator");
        admin.setEmail("admin@cit.edu");
        admin.setPassword("admin123");
        admin.setAdminLevel("SUPER");
        adminRepository.save(admin);

        Faculty santos = faculty("Maria", "Santos", "maria.santos@cit.edu", "EMP-0001", "Information Technology");
        Faculty delaCruz = faculty("Ramon", "Dela Cruz", "ramon.delacruz@cit.edu", "EMP-0002", "Computer Science");

        Student johnmark = student("Johnmark", "Tanaleon", "johnmark.tanaleon@cit.edu", "22-0001-101", 3, 310, 5);
        Student vince = student("Vince", "Esgana", "vince.esgana@cit.edu", "22-0001-102", 3, 190, 2);
        Student carlos = student("Carlos", "Enicuela", "carlos.enicuela@cit.edu", "22-0001-103", 3, 240, 7);
        Student ana = student("Ana", "Reyes", "ana.reyes@cit.edu", "23-0002-201", 2, 120, 1);
        Student miguel = student("Miguel", "Bautista", "miguel.bautista@cit.edu", "23-0002-202", 2, 60, 0);
        Student liza = student("Liza", "Fernandez", "liza.fernandez@cit.edu", "21-0003-301", 4, 420, 9);

        // ---- content: course > track > lesson > quiz > question
        Course csit321 = course("CSIT321", "Applications Development and Emerging Technology",
                "Building a full application with a Spring backend and a React frontend.", santos);
        Course csit340 = course("CSIT340", "Front-end Development using ReactJS",
                "Components, state and data fetching in React.", delaCruz);

        Track restApis = track(csit321, 1, "REST APIs");
        Track springData = track(csit321, 2, "Spring Data and MySQL");
        Track reactBasics = track(csit340, 1, "React fundamentals");

        Lesson endpoint = lesson(restApis, 1, "What a REST endpoint is", 6,
                "An endpoint is one address the server listens on, paired with a method. "
                        + "The path names a resource and the method says what to do with it.");
        Lesson verbs = lesson(restApis, 2, "HTTP verbs in practice", 8,
                "GET reads and changes nothing. POST creates. PUT replaces a resource in full, "
                        + "PATCH edits only the fields sent, and DELETE removes it.");
        Lesson statusCodes = lesson(restApis, 3, "Status codes that matter", 5,
                "200 means success, 201 means created, 400 means the request was wrong, "
                        + "404 means the resource does not exist and 409 means a conflict.");
        lesson(restApis, 4, "Writing a controller", 9,
                "A controller is a class marked @RestController. It reads the request, "
                        + "calls a service and returns the result. Rules belong in the service.");
        Lesson entities = lesson(springData, 1, "Entities and tables", 7,
                "A class marked @Entity maps to one table. Each field becomes a column and "
                        + "the field marked @Id becomes the primary key.");
        lesson(springData, 2, "Repositories without SQL", 6,
                "Extending JpaRepository provides save, findById, findAll and deleteById "
                        + "without writing any SQL.");
        lesson(springData, 3, "Connecting to XAMPP MySQL", 5,
                "The connection is set in application.properties: the JDBC url, the user root "
                        + "and an empty password, which is the XAMPP default.");
        Lesson components = lesson(reactBasics, 1, "Components and props", 7,
                "A component is a function that returns markup. Props are the inputs "
                        + "a parent passes down to it.");
        lesson(reactBasics, 2, "State with useState", 8,
                "State is data a component remembers between renders. Calling the setter "
                        + "asks React to render the component again.");
        lesson(reactBasics, 3, "Fetching data with useEffect", 9,
                "useEffect runs code after rendering, which is where a component "
                        + "requests data from an API.");

        Quiz endpointQuiz = quiz(endpoint, "REST endpoint check", 1);
        question(endpointQuiz, "Which path names a resource correctly?",
                "/api/getAllItems | /api/items | /api/item/fetch", "/api/items");
        question(endpointQuiz, "An endpoint is made up of which two things?",
                "A path and a method | A path and a password | A method and a port", "A path and a method");

        Quiz verbsQuiz = quiz(verbs, "HTTP verbs check", 2);
        question(verbsQuiz, "Which method replaces an existing resource in full?",
                "GET | POST | PUT | PATCH", "PUT");
        question(verbsQuiz, "Which method changes nothing on the server?",
                "DELETE | GET | POST", "GET");
        question(verbsQuiz, "What does PATCH do differently from PUT?",
                "Deletes the resource | Edits only the fields sent | Returns no body",
                "Edits only the fields sent");

        Quiz statusQuiz = quiz(statusCodes, "Status codes check", 1);
        question(statusQuiz, "A request succeeded and created a record. Which code?",
                "200 | 201 | 204", "201");
        question(statusQuiz, "The requested id does not exist. Which code?",
                "400 | 401 | 404", "404");

        Quiz entitiesQuiz = quiz(entities, "Entities check", 1);
        question(entitiesQuiz, "Which annotation maps a class to a table?",
                "@Column | @Entity | @Id", "@Entity");

        Quiz componentsQuiz = quiz(components, "Components check", 1);
        question(componentsQuiz, "What are props?",
                "Data a component remembers | Inputs passed from a parent | A kind of hook",
                "Inputs passed from a parent");

        // ---- activity: enrollments, lesson progress and quiz attempts
        for (Student s : new Student[]{johnmark, vince, carlos, ana, miguel, liza}) {
            enroll(s, csit321, today.minusDays(30));
        }
        enroll(johnmark, csit340, today.minusDays(28));
        enroll(ana, csit340, today.minusDays(28));
        enroll(liza, csit340, today.minusDays(27));

        progress(johnmark, endpoint, "COMPLETED", today.minusDays(4), true);
        progress(johnmark, verbs, "COMPLETED", today.minusDays(3), true);
        progress(johnmark, statusCodes, "COMPLETED", today.minusDays(1), false);
        progress(vince, endpoint, "COMPLETED", today.minusDays(5), true);
        progress(vince, verbs, "IN_PROGRESS", null, false);
        progress(carlos, endpoint, "COMPLETED", today.minusDays(6), true);
        progress(carlos, verbs, "COMPLETED", today.minusDays(2), true);
        progress(carlos, entities, "COMPLETED", today, false);
        progress(ana, components, "COMPLETED", today.minusDays(2), true);
        progress(liza, endpoint, "COMPLETED", today.minusDays(9), true);
        progress(liza, verbs, "COMPLETED", today.minusDays(8), true);
        progress(liza, statusCodes, "COMPLETED", today.minusDays(7), true);

        attempt(johnmark, endpointQuiz, 2, today.minusDays(4), true);
        attempt(johnmark, verbsQuiz, 3, today.minusDays(3), true);
        attempt(johnmark, statusQuiz, 1, today.minusDays(1), false);
        attempt(vince, endpointQuiz, 1, today.minusDays(5), true);
        attempt(carlos, verbsQuiz, 2, today.minusDays(2), true);
        attempt(carlos, entitiesQuiz, 1, today, false);
        attempt(ana, componentsQuiz, 1, today.minusDays(2), true);
        attempt(liza, verbsQuiz, 3, today.minusDays(8), true);

        // ---- rewards
        Badge firstLesson = badge("First lesson", "Finished a first lesson.", "1 lesson completed");
        Badge weekStreak = badge("7-day streak", "Studied on seven days in a row.", "Current streak of 7 or more");
        // Not earned by anyone yet, so the Badges screen also shows an unclaimed award.
        badge("REST APIs track", "Finished every lesson in the REST APIs track.",
                "All lessons in the track completed");
        Badge perfectQuiz = badge("Perfect quiz", "Answered every question of a quiz correctly.",
                "Full score on any quiz");

        award(johnmark, firstLesson, today.minusDays(4), "LS-1A2B-01");
        award(johnmark, perfectQuiz, today.minusDays(3), "LS-1A2B-02");
        award(carlos, firstLesson, today.minusDays(6), "LS-3C4D-01");
        award(carlos, weekStreak, today, "LS-3C4D-02");
        award(liza, firstLesson, today.minusDays(9), "LS-5E6F-01");
        award(liza, weekStreak, today.minusDays(2), "LS-5E6F-02");
        award(liza, perfectQuiz, today.minusDays(8), "LS-5E6F-03");
        award(vince, firstLesson, today.minusDays(5), "LS-7G8H-01");
    }

    private Faculty faculty(String fname, String lname, String email, String employeeNo, String department) {
        Faculty f = new Faculty();
        f.setFname(fname);
        f.setLname(lname);
        f.setEmail(email);
        f.setPassword("faculty123");
        f.setEmployeeNo(employeeNo);
        f.setDepartment(department);
        return facultyRepository.save(f);
    }

    private Student student(String fname, String lname, String email, String schoolId,
                            int yearLevel, int points, int streak) {
        Student s = new Student();
        s.setFname(fname);
        s.setLname(lname);
        s.setEmail(email);
        s.setPassword("student123");
        s.setSchoolId(schoolId);
        s.setProgram("BS Information Technology");
        s.setYearLevel(yearLevel);
        s.setTotalPoints(points);
        s.setCurrentStreak(streak);
        return studentRepository.save(s);
    }

    private Course course(String code, String title, String description, Faculty owner) {
        Course c = new Course();
        c.setCourseCode(code);
        c.setTitle(title);
        c.setDescription(description);
        c.setFaculty(owner);
        return courseRepository.save(c);
    }

    private Track track(Course course, int sequenceNo, String title) {
        Track t = new Track();
        t.setCourse(course);
        t.setSequenceNo(sequenceNo);
        t.setTitle(title);
        return trackRepository.save(t);
    }

    private Lesson lesson(Track track, int sequenceNo, String title, int minutes, String content) {
        Lesson l = new Lesson();
        l.setTrack(track);
        l.setSequenceNo(sequenceNo);
        l.setTitle(title);
        l.setDurationMin(minutes);
        l.setContent(content);
        return lessonRepository.save(l);
    }

    private Quiz quiz(Lesson lesson, String title, int passingScore) {
        Quiz q = new Quiz();
        q.setLesson(lesson);
        q.setTitle(title);
        q.setPassingScore(passingScore);
        return quizRepository.save(q);
    }

    private void question(Quiz quiz, String text, String choices, String correctAnswer) {
        Question q = new Question();
        q.setQuiz(quiz);
        q.setQuestionText(text);
        q.setChoices(choices);
        q.setCorrectAnswer(correctAnswer);
        questionRepository.save(q);
    }

    private void enroll(Student student, Course course, LocalDate date) {
        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setCourse(course);
        e.setDateEnrolled(date);
        enrollmentRepository.save(e);
    }

    private void progress(Student student, Lesson lesson, String status, LocalDate completed, boolean synced) {
        LessonProgress p = new LessonProgress();
        p.setStudent(student);
        p.setLesson(lesson);
        p.setStatus(status);
        p.setDateCompleted(completed);
        p.setIsSynced(synced);
        lessonProgressRepository.save(p);
    }

    private void attempt(Student student, Quiz quiz, int score, LocalDate taken, boolean synced) {
        QuizAttempt a = new QuizAttempt();
        a.setStudent(student);
        a.setQuiz(quiz);
        a.setScore(score);
        a.setDateTaken(taken);
        a.setIsSynced(synced);
        quizAttemptRepository.save(a);
    }

    private Badge badge(String name, String description, String criteria) {
        Badge b = new Badge();
        b.setName(name);
        b.setDescription(description);
        b.setCriteria(criteria);
        return badgeRepository.save(b);
    }

    private void award(Student student, Badge badge, LocalDate earned, String code) {
        StudentBadge sb = new StudentBadge();
        sb.setStudent(student);
        sb.setBadge(badge);
        sb.setDateEarned(earned);
        sb.setVerificationCode(code);
        studentBadgeRepository.save(sb);
    }
}

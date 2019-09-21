import atexit
import os
import sqlite3
import sys


def parse_student(line_arr):
    line_arr = line_arr[1:]
    line_arr = [x.strip() for x in line_arr]
    repo.students.insert(Student(*line_arr))


def parse_classroom(line_arr):
    line_arr = line_arr[1:]
    line_arr = [x.strip() for x in line_arr]
    repo.classrooms.insert(Classroom(line_arr[0], line_arr[1], 0, 0))


def parse_course(line_arr):
    line_arr = line_arr[1:]
    line_arr = [x.strip() for x in line_arr]
    repo.courses.insert(Course(*line_arr))


def parse_data_type(line_arr):
    if line_arr[0] == 'S':
        parse_student(line_arr)
    elif line_arr[0] == 'C':
        parse_course(line_arr)
    elif line_arr[0] == 'R':
        parse_classroom(line_arr)
    else:
        print ('Wrong input values')


def parse_config(config_path):
    with open(config_path) as configFile:
        for line in configFile:
            line.strip()  # removes spaces from begining and end
            if line.__contains__('\n'):
                line = line[:len(line) - 1]
            line_arr = line.split(',')
            parse_data_type(line_arr)


# Data Transfer Objects:
class Student:
    def __init__(self, grade, count):
        self.grade = grade
        self.count = count

    def __str__(self):
        return "('{}', {})".format(self.grade, self.count)


class Course:
    def __init__(self, id, course_name, student, number_of_students, class_id, course_length):
        self.id = id
        self.course_name = course_name
        self.student = student
        self.number_of_students = number_of_students
        self.class_id = class_id
        self.course_length = course_length

    def __str__(self):
        return "({}, '{}', '{}', {}, {}, {})".format(self.id, self.course_name, self.student,
                                                 self.number_of_students, self.class_id,
                                                 self.course_length)


class Classroom:
    def __init__(self, id, location, current_course_id, current_course_time_left):
        self.id = id
        self.location = location
        self.current_course_id = current_course_id
        self.current_course_time_left = current_course_time_left

    def __str__(self):
        return "({}, '{}', {}, {})".format(self.id, self.location, self.current_course_id,
                                         self.current_course_time_left)


class ClassroomsWithCourses:
    def __init__(self, class_id, location, current_course_id, current_course_time_left, course_id, course_name, student,
                 number_of_students_in_course, classroom_id, course_length):
        self.class_id = class_id
        self.location = location
        self.current_course_id = current_course_id
        self.current_course_time_left = current_course_time_left
        self.course_id = course_id
        self.course_name = course_name
        self.student = student
        self.number_of_students = number_of_students_in_course
        self.classroom_id = classroom_id
        self.course_length = course_length


# Data Access Objects:
# All of these are meant to be singletons
class _Students:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, student):
        self._conn.execute("""
               INSERT INTO students (grade, count) VALUES (?, ?)
           """, [student.grade, student.count])

    def delete(self, student):
        self._conn.execute("""
                       DELETE FROM students WHERE grade = (?)
                   """, [student.grade])

    def update(self, grade, count):
        self._conn.execute("""
                   UPDATE students SET count = (?) WHERE grade = (?) 
               """, [count, grade])

    def find(self, grade):
        c = self._conn.cursor()
        c.execute("SELECT * FROM students WHERE grade = ?", [grade])
        return Student(*c.fetchone())

    def find_all(self):
        c = self._conn.cursor()
        all = c.execute("SELECT * FROM students").fetchall()
        return [Student(*row) for row in all]


class _Courses:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, course):
        self._conn.execute("""
                INSERT INTO courses (id, course_name, student, number_of_students, class_id, course_length) VALUES (?, ?, ?, ?, ?, ?)
        """, [course.id, course.course_name, course.student, course.number_of_students, course.class_id,
              course.course_length])

    def delete(self, id):
        self._conn.execute("""
                       DELETE FROM courses WHERE id = (?)
                   """, [id])

    def find_all(self):
        c = self._conn.cursor()
        all = c.execute("SELECT * FROM courses").fetchall()
        return [Course(*row) for row in all]


class _Classrooms:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, classroom):
        self._conn.execute("""
            INSERT INTO classrooms (id, location, current_course_id, current_course_time_left) VALUES (?, ?, ?, ?)
        """, [classroom.id, classroom.location, classroom.current_course_id, classroom.current_course_time_left])

    def delete(self, classroom):
        self._conn.execute("""
                       DELETE FROM classrooms WHERE id = (?)
                   """, [classroom.id])

    def update(self, class_id, course_id, time_left):
        self._conn.execute("""
                   UPDATE classrooms SET current_course_id = (?), current_course_time_left = (?) WHERE classrooms.id = (?) 
               """, [course_id, time_left, class_id])

    def find(self, course_id):
        c = self._conn.cursor()
        c.execute("SELECT * FROM classrooms WHERE id = ?", [course_id])
        return Classroom(*c.fetchone())

    def find_all(self):
        c = self._conn.cursor()
        all = c.execute("SELECT * FROM classrooms").fetchall()
        return [Classroom(*row) for row in all]


# The Repository
class _Repository:
    def __init__(self):
        self._conn = sqlite3.connect('schedule.db')
        self.students = _Students(self._conn)
        self.courses = _Courses(self._conn)
        self.classrooms = _Classrooms(self._conn)
        if _dbExists:
            return
        self.create_tables()

    def _close(self):
        self._conn.commit()
        self._conn.close()

    def create_tables(self):
        self._conn.executescript("""

                CREATE TABLE classrooms (
                    id                          INTEGER     PRIMARY KEY,
                    location                    TEXT        NOT NULL,
                    current_course_id           INTEGER     NOT NULL,
                    current_course_time_left    INTEGER     NOT NULL
                  );

                CREATE TABLE courses (
                    id                  INTEGER     PRIMARY KEY,
                    course_name         TEXT        NOT NULL,
                    student             TEXT        NOT NULL,
                    number_of_students  INTEGER     NOT NULL,
                    class_id            INTEGER     REFERENCES  classrooms(id),
                    course_length       INTEGER     NOT NULL
                );

                CREATE TABLE students (
                    grade   TEXT        PRIMARY KEY,
                    count   INTEGER     NOT NULL
                );

            """)

    def get_classes_with_courses(self):
        c = self._conn.cursor()
        all = c.execute("""
                SELECT *
                FROM classrooms
                JOIN courses ON classrooms.current_course_id = courses.id
                WHERE classrooms.current_course_id != 0
            """).fetchall()

        return [ClassroomsWithCourses(*row) for row in all]


# the repository singleton
_dbExists = os.path.isfile('schedule.db')
repo = _Repository()
atexit.register(repo._close)


def print_table(list_of_tuples):
    for item in list_of_tuples:
        print(item)


def main(args):
    if _dbExists:
        return
    parse_config(args[1])
    print('courses')
    print_table(repo.courses.find_all())
    print('classrooms')
    print_table(repo.classrooms.find_all())
    print('students')
    print_table(repo.students.find_all())


if __name__ == '__main__':
    main(sys.argv)

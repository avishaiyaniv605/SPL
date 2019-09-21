import sqlite3
from create_db import repo


def main():
    should_terminate = False
    curr_iteration = 0

    while not should_terminate:

        should_terminate = True
        # print running classes
        for courseWithClassroom in repo.get_classes_with_courses():
            if courseWithClassroom.current_course_id != 0:
                if courseWithClassroom.current_course_time_left > 1:
                    print("({}) {}: occupied by {}".format(curr_iteration,
                                                           courseWithClassroom.location,
                                                           courseWithClassroom.course_name))
                    should_terminate = False
                    # update time left
                    repo.classrooms.update(courseWithClassroom.class_id,
                                           courseWithClassroom.current_course_id,
                                           courseWithClassroom.current_course_time_left - 1)
                elif courseWithClassroom.current_course_time_left == 1:
                    repo.courses.delete(courseWithClassroom.current_course_id)
                    # print class is done
                    print("({}) {}: {} is done".format(curr_iteration,
                                                       courseWithClassroom.location,
                                                       courseWithClassroom.course_name))
                    # update class is free
                    repo.classrooms.update(courseWithClassroom.class_id, 0, 0)
                    for course in repo.courses.find_all():
                        classroom = repo.classrooms.find(course.class_id)
                        # if class can be started, start it and update tables
                        if classroom.current_course_id == 0:
                            repo.classrooms.update(classroom.id, course.id, course.course_length)
                            student_count = repo.students.find(course.student).count
                            new_student_count = student_count - course.number_of_students
                            if new_student_count < 0:
                                new_student_count = 0
                            repo.students.update(course.student, new_student_count)
                            print("({}) {}: {} is schedule to start".format(curr_iteration,
                                                                            classroom.location,
                                                                            course.course_name))
                            should_terminate = False
        for course in repo.courses.find_all():
            classroom = repo.classrooms.find(course.class_id)
            # if class can be started, start it and update tables
            if classroom.current_course_id == 0:
                repo.classrooms.update(classroom.id, course.id, course.course_length)
                student_count = repo.students.find(course.student).count
                new_student_count = student_count - course.number_of_students
                if new_student_count < 0:
                    new_student_count = 0
                repo.students.update(course.student, new_student_count)
                print("({}) {}: {} is schedule to start".format(curr_iteration,
                                                                classroom.location,
                                                                course.course_name))
                should_terminate = False

        # tables print
        print('courses')
        print_table(repo.courses.find_all())
        print('classrooms')
        print_table(repo.classrooms.find_all())
        print('students')
        print_table(repo.students.find_all())
        curr_iteration = curr_iteration + 1
    # if courses table is empty and all classrooms are empty, terminate


#   if repo.find(classroom with course id) and repo.find(courses) is empty
#    should_terminate = True


def print_table(list_of_tuples):
    for item in list_of_tuples:
        print(item)


if __name__ == '__main__':
    main()

// 通用响应格式
export interface ApiResponse<T> {
    success: boolean
    message?: string
    data: T
}

// 课程信息
export interface CourseSchedule {
    id?: string
    courseId: string
    courseName: string
    courseType: string
    teacherId: string
    teacherName: string
    classroomId: string
    classroomName: string
    buildingId: string
    academicYear: string
    semester: number
    weekday: number
    startTime?: string
    endTime?: string
    startSection?: number
    endSection?: number
    weekNumber?: number
    credits?: number
    description?: string
    totalStudents?: number
    maxStudents?: number
    weekNumbers?: number[]
    weekNumberDisplay?: string
}

// 课程详细信息
export interface CourseDetail {
    courseId: string
    courseName: string
    courseType: string
    teacherId: string
    teacherName: string
    classroomId: string
    classroomName: string
    buildingId: string
    academicYear: string
    semester: number
    weekday: number
    startSection: number
    endSection: number
    weekNumber?: number
}

// 专业信息
export interface Major {
    majorId: string
    majorName: string
    departmentId: string
}

// 班级信息
export interface Class {
    classId: string
    className: string
    majorId: string
    startYear: number
}

// 学生课表查询参数
export interface StudentTimetableQuery {
    classId: string
    academicYear: string
    semester: number
    weekNumber?: number
}

// 教师课表查询参数
export interface TeacherTimetableQuery {
    teacherId: string
    academicYear: string
    semester: number
    weekNumber?: number
}

// 选项数据
export interface OptionItem {
    label: string
    value: string
}

export interface Teacher {
    id: string
    name: string
    code: string  // 工号
    title?: string
    departmentId?: string
    office?: string
    gender?: string
}

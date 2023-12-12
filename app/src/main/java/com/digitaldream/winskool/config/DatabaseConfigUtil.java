package com.digitaldream.winskool.config;

import com.digitaldream.winskool.models.AssessmentModel;
import com.digitaldream.winskool.models.ClassNameTable;
import com.digitaldream.winskool.models.CommentTable;
import com.digitaldream.winskool.models.CourseOutlineTable;
import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.models.Exam;
import com.digitaldream.winskool.models.ExamQuestions;
import com.digitaldream.winskool.models.ExamType;
import com.digitaldream.winskool.models.FormClassModel;
import com.digitaldream.winskool.models.GeneralSettingModel;
import com.digitaldream.winskool.models.GradeModel;
import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.models.NewsTable;
import com.digitaldream.winskool.models.StaffTableUtil;
import com.digitaldream.winskool.models.StudentCourses;
import com.digitaldream.winskool.models.StudentResultDownloadTable;
import com.digitaldream.winskool.models.StudentTable;
import com.digitaldream.winskool.models.TeacherCourseModel;
import com.digitaldream.winskool.models.TeacherCourseModelCopy;
import com.digitaldream.winskool.models.TeachersTable;
import com.digitaldream.winskool.models.VideoTable;
import com.digitaldream.winskool.models.VideoUtilTable;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[]{StudentTable.class,
            TeachersTable.class, ClassNameTable.class, LevelTable.class,
            NewsTable.class, CourseTable.class,
            StudentResultDownloadTable.class, StudentCourses.class,
            VideoTable.class, GradeModel.class, GeneralSettingModel.class,
            VideoUtilTable.class
            , AssessmentModel.class, Exam.class, ExamQuestions.class,
            ExamType.class, StaffTableUtil.class, FormClassModel.class,
            TeacherCourseModel.class, TeacherCourseModelCopy.class,
            CourseOutlineTable.class, CommentTable.class};

    public static void main(String[] args) throws IOException, SQLException {
        writeConfigFile("ormlite_config", classes);
    }
}

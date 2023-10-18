package com.digitaldream.linkskool.config;

import com.digitaldream.linkskool.models.AssessmentModel;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.CommentTable;
import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.models.Exam;
import com.digitaldream.linkskool.models.ExamQuestions;
import com.digitaldream.linkskool.models.ExamType;
import com.digitaldream.linkskool.models.FormClassModel;
import com.digitaldream.linkskool.models.GeneralSettingModel;
import com.digitaldream.linkskool.models.GradeModel;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.models.NewsTable;
import com.digitaldream.linkskool.models.StaffTableUtil;
import com.digitaldream.linkskool.models.StudentCourses;
import com.digitaldream.linkskool.models.StudentResultDownloadTable;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.models.TeacherCourseModel;
import com.digitaldream.linkskool.models.TeacherCourseModelCopy;
import com.digitaldream.linkskool.models.TeachersTable;
import com.digitaldream.linkskool.models.VideoTable;
import com.digitaldream.linkskool.models.VideoUtilTable;
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

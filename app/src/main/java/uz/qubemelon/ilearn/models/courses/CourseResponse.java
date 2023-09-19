package uz.qubemelon.ilearn.models.courses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import uz.qubemelon.ilearn.models.courses.chapters.GrammarChapterList;
import uz.qubemelon.ilearn.models.courses.chapters.VocabularyChapterList;
import uz.qubemelon.ilearn.models.courses.exercises.ExerciseList;
import uz.qubemelon.ilearn.models.courses.lessons.LessonList;
import uz.qubemelon.ilearn.models.courses.questions.QuestionList;
import uz.qubemelon.ilearn.models.dictionaries.DictionaryList;
import uz.qubemelon.ilearn.models.games.FivesGameScore;
import uz.qubemelon.ilearn.models.user.LeaderboardList;
import uz.qubemelon.ilearn.models.user.Profile;

public class CourseResponse{

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("total_point")
    private int total_point;

    @SerializedName("dictionary_list")
    private List<DictionaryList> dictionary_list;

    @SerializedName("leader_board_list")
    private List<LeaderboardList> leader_board_list;

    @SerializedName("fives_game")
    private FivesGameScore fives_game;

    @SerializedName("profile")
    private Profile profile;

    @SerializedName("vocabulary_chapter_list")
    private List<VocabularyChapterList> vocabulary_chapter_list;

    @SerializedName("grammar_chapter_list")
    private List<GrammarChapterList> grammar_chapter_list;

    @SerializedName("lessons_list")
    private List<LessonList> lesson_list;

    @SerializedName("exercise_list")
    private List<ExerciseList> exercise_list;

    @SerializedName("question_list")
    private List<QuestionList> question_list;

    @SerializedName("user_available_point")
    private int user_available_point;

    @SerializedName("total_question")
    private int total_question;

    @SerializedName("total_exercise")
    private int total_exercise;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getTotalPoint() {
        return total_point;
    }

    public List<DictionaryList> getDictionaryList() {
        return dictionary_list;
    }

    public List<LeaderboardList> getLeaderboardList() {
        return leader_board_list;
    }

    public FivesGameScore getFivesGame() {
        return fives_game;
    }

    public Profile getProfile() {
        return profile;
    }

    public List<VocabularyChapterList> getVocabularyChapterList() {
        return vocabulary_chapter_list;
    }

    public List<GrammarChapterList> getGrammarChapterList() {
        return grammar_chapter_list;
    }

    public List<LessonList> getLessonList() {
        return lesson_list;
    }

    public List<ExerciseList> getExerciseList() {
        return exercise_list;
    }

    public List<QuestionList> getQuestionList() {
        return question_list;
    }

    public int getUserAvailablePoint() {
        return user_available_point;
    }

    public int getTotalQuestion() {
        return total_question;
    }

    public int getTotalExercise() {
        return total_exercise;
    }
}

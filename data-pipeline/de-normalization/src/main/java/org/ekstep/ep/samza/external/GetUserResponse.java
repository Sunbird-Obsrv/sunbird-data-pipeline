package org.ekstep.ep.samza.external;

import java.util.Map;

public class GetUserResponse {
    public static final String SUCCESS_RESPONSE_STATUS = "successful";
    private String id;
    private String ver;
    private String ts;
    private Map<String, Object> params;
    private GetUserResult result;

    public boolean successful() {
        return params != null && SUCCESS_RESPONSE_STATUS.equals(params.get("status"));
    }

    public ProfileDto profile() {
        return result.profile;
    }

    public LearnerDto learner() {
        return result.learner;
    }

    private class GetUserResult {
        private LearnerDto learner;
        private ProfileDto profile;

        @Override
        public String toString() {
            return "GetUserResult{" +
                    "learner=" + learner +
                    ", profile=" + profile +
                    '}';
        }
    }

    public class LearnerDto {
        private long id;
        private String uid;

        @Override
        public String toString() {
            return "LearnerDto{" +
                    "id=" + id +
                    ", uid='" + uid + '\'' +
                    '}';
        }
    }

    public class ProfileDto {
        private long id;
        private String uid;
        private String handle;
        private String gender;
        private int age;
        private String standard;
        private String language;
        private int yearOfBirth;
        private int day;
        private int month;
        private boolean isGroupUser;

        public String handle() {
            return handle;
        }

        public String gender() {
            return gender;
        }

        public int age() {
            return age;
        }

        public String standard() {
            return standard;
        }

        public String language() {
            return language;
        }

        public int yearOfBirth() {
            return yearOfBirth;
        }

        public int day() {
            return day;
        }

        public int month() {
            return month;
        }

        public boolean isGroupUser() {
            return isGroupUser;
        }

        @Override
        public String toString() {
            return "ProfileDto{" +
                    "id=" + id +
                    ", uid='" + uid + '\'' +
                    ", handle='" + handle + '\'' +
                    ", gender='" + gender + '\'' +
                    ", age=" + age +
                    ", standard='" + standard + '\'' +
                    ", language='" + language + '\'' +
                    ", yearOfBirth=" + yearOfBirth +
                    ", day=" + day +
                    ", month=" + month +
                    ", isGroupUser=" + isGroupUser +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GetUserResponse{" +
                "id='" + id + '\'' +
                ", ver='" + ver + '\'' +
                ", ts='" + ts + '\'' +
                ", params=" + params +
                ", result=" + result +
                '}';
    }
}

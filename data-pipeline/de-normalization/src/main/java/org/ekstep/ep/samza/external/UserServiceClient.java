package org.ekstep.ep.samza.external;

import com.google.gson.Gson;
import okhttp3.*;
import org.ekstep.ep.samza.Child;
import org.ekstep.ep.samza.logger.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import static java.text.MessageFormat.format;

public class UserServiceClient implements UserService {
    static Logger LOGGER = new Logger(UserServiceClient.class);

    private static final String HANDLE = "handle";
    private static final String STANDARD = "standard";
    private static final String GENDER = "gender";
    private static final String YEAR_OF_BIRTH = "year_of_birth";
    private static final String BOARD = "board";
    private static final String MEDIUM = "medium";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private final String IS_GROUP_USER = "is_group_user";
    private String userServiceEndpoint;
    private final OkHttpClient httpClient;

    public UserServiceClient(String userServiceEndpoint) {
        this.userServiceEndpoint = userServiceEndpoint;
        httpClient = new OkHttpClient();
    }

    @Override
    public Child getUserFor(Child child, Date timeOfEvent, String eventId) throws IOException {
        Request request = new Request.Builder()
                .url(userServiceEndpoint + child.getUid())
                .post(RequestBody.create(JSON_MEDIA_TYPE, new Gson().toJson(GetUserRequest.create())))
                .build();
        Response response = httpClient.newCall(request).execute();
        GetUserResponse getUserResponse = new Gson().fromJson(response.body().string(), GetUserResponse.class);

        HashMap<String, Object> childData = new HashMap<String, Object>();
        if (!getUserResponse.successful()) {
            LOGGER.error(eventId, "USER SERVICE FAILED. RESPONSE: {}",getUserResponse);
            child.populate(childData, timeOfEvent, eventId);
            return child;
        }

        if (getUserResponse.profile() != null) {
            LOGGER.info(eventId, "PROFILE FOUND");
            childData.put(HANDLE, getUserResponse.profile().handle());
            childData.put(STANDARD, getUserResponse.profile().standard());
            String genderValue = getUserResponse.profile().gender() == null
                    ? "Not known"
                    : getUserResponse.profile().gender();
            String board_value = getUserResponse.profile().board() == null
                    ? "Not known"
                    : getUserResponse.profile().board();
            String medium_value = getUserResponse.profile().medium() == null
                    ? "Not known"
                    : getUserResponse.profile().medium();
            childData.put(GENDER, genderValue);
            childData.put(BOARD, board_value);
            childData.put(MEDIUM, medium_value);
            childData.put(YEAR_OF_BIRTH, getUserResponse.profile().yearOfBirth());
            childData.put(IS_GROUP_USER, getUserResponse.profile().isGroupUser());
            child.populate(childData, timeOfEvent, eventId);
            return child;
        }

        if (getUserResponse.learner() != null) {
            LOGGER.info(eventId, "PROFILE NOT FOUND, BUT LEARNER FOUND");
            childData.put(HANDLE, null);
            childData.put(STANDARD, 0);
            childData.put(GENDER, "Not known");
            childData.put(BOARD, "Not known");
            childData.put(MEDIUM, "Not known");
            childData.put(YEAR_OF_BIRTH, null);
            childData.put(IS_GROUP_USER, false);
        }
        child.populate(childData, timeOfEvent, eventId);
        return child;
    }
}

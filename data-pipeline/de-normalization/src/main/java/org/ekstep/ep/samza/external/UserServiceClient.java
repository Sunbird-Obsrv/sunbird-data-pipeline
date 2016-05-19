package org.ekstep.ep.samza.external;

import com.google.gson.Gson;
import okhttp3.*;
import org.ekstep.ep.samza.Child;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;

public class UserServiceClient implements UserService {
    private static final String HANDLE = "handle";
    private static final String STANDARD = "standard";
    private static final String GENDER = "gender";
    private static final String YEAR_OF_BIRTH = "year_of_birth";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private final String IS_GROUP_USER = "is_group_user";
    private String userServiceEndpoint;
    private final OkHttpClient httpClient;

    public UserServiceClient(String userServiceEndpoint) {
        this.userServiceEndpoint = userServiceEndpoint;
        httpClient = new OkHttpClient();
    }

    @Override
    public Child getUserFor(Child child, java.util.Date timeOfEvent) throws IOException {
        Request request = new Request.Builder()
                .url(userServiceEndpoint + child.getUid())
                .post(RequestBody.create(JSON_MEDIA_TYPE, new Gson().toJson(GetUserRequest.create())))
                .build();
        Response response = httpClient.newCall(request).execute();
        GetUserResponse getUserResponse = new Gson().fromJson(response.body().string(), GetUserResponse.class);

        HashMap<String, Object> childData = new HashMap<String, Object>();
        if (!getUserResponse.successful()) {
            System.err.println(MessageFormat.format("User service call failed for uid:  {0}, Response: {1}",
                    child.getUid(), getUserResponse));
            child.populate(childData, timeOfEvent);
            return child;
        }

        if (getUserResponse.profile() != null) {
            System.out.println("Found profile for uid: " + child.getUid());
            childData.put(HANDLE, getUserResponse.profile().handle());
            childData.put(STANDARD, getUserResponse.profile().standard());
            String genderValue = getUserResponse.profile().gender() == null
                    ? "Not known"
                    : getUserResponse.profile().gender();
            childData.put(GENDER, genderValue);
            childData.put(YEAR_OF_BIRTH, getUserResponse.profile().yearOfBirth());
            childData.put(IS_GROUP_USER, getUserResponse.profile().isGroupUser());
            child.populate(childData, timeOfEvent);
            return child;
        }

        if (getUserResponse.learner() != null) {
            System.out.println("Found learner for uid: " + child.getUid());
            childData.put(HANDLE, null);
            childData.put(STANDARD, 0);
            childData.put(GENDER, "Not known");
            childData.put(YEAR_OF_BIRTH, null);
            childData.put(IS_GROUP_USER, false);
        }
        child.populate(childData, timeOfEvent);
        return child;
    }
}

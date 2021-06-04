package org.ekstep.ep.samza.fixtures;

public class TelemetryV3 {
    /**
     * Valid event with all proper data type
     */
    public static final String VALID_EVENT = "{\"actor\":{\"type\":\"User\",\"id\":\"4c4530df-0d4f-42a5-bd91-0366716c8c24\"},\"edata\":{\"id\":\"content-detail\",\"type\":\"OTHER\",\"pageid\":\"content-detail\",\"subtype\":\"detail\",\"extra\":{\"values\":[{\"isDownloaded\":true,\"isUpdateAvailable\":false}]}},\"eid\":\"INTERACT\",\"ver\":\"3.0\",\"ets\":1551344686294,\"context\":{\"pdata\":{\"ver\":\"2.0.localstaging-debug\",\"pid\":\"sunbird.app\",\"id\":\"staging.diksha.app\"},\"channel\":\"01231711180382208027\",\"env\":\"home\",\"did\":\"6c61348dc9841f27c96f4887b64ee1f777d74c38\",\"sid\":\"cef2d0be-83fc-4988-8ad9-1b72399e6d3a\",\"cdata\":[]},\"mid\":\"3318a611-50fa-4ae9-9167-7b4390a62b9f\",\"object\":{\"id\":\"do_21228031946955980819\",\"type\":\"Worksheet\",\"version\":\"1.0\",\"rollup\":{\"l4\":\"do_21270636501657190413450\",\"l1\":\"do_21270636097196032013440\",\"l2\":\"do_21270636501655552013444\",\"l3\":\"do_21270636501657190413448\"}},\"tags\":[],\"syncts\":1551344699388,\"@timestamp\":\"2019-02-28T09:04:59.388Z\",\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":true,\"user_location_retrieved\":false,\"content_data_retrieved\":true,\"user_data_retrieved\":true,\"device_data_retrieved\":true},\"type\":\"events\",\"ts\":\"2019-02-28T09:04:46.294+0000\",\"devicedata\":{\"statecustomcode\":\"\",\"country\":\"\",\"city\":\"\",\"countrycode\":\"\",\"state\":\"\",\"statecode\":\"\",\"districtcustom\":\"\",\"statecustomname\":\"\",\"uaspec\":{},\"firstaccess\":1545920698694},\"userdata\":{\"district\":\"\",\"state\":\"\",\"subject\":[\"English\"],\"grade\":[\"KG\",\"Class 1\",\"Class 2\",\"Class 3\",\"Class 4\",\"Class 5\",\"Class 6\",\"Class 7\",\"Class 8\",\"Class 9\",\"Class 10\",\"Class 11\",\"Class 12\",\"Other\"],\"language\":[\"English\",\"Gujarati\",\"Hindi\"]},\"contentdata\":{\"pkgversion\":1,\"language\":[\"Assamese\"],\"lastpublishedon\":1499851249497,\"contenttype\":\"Resource\",\"lastupdatedon\":1499851152176,\"framework\":\"NCF\",\"name\":\"Test review process\",\"mimetype\":\"application/vnd.ekstep.ecml-archive\",\"objecttype\":\"Content\",\"mediatype\":\"content\",\"status\":\"Live\"}}";
    /**
     * Passing Invalid event having wrong `pkgVersion` type
     */
    public static final String INVALID_EVENT = "{\"actor\":{\"type\":\"User\",\"id\":\"4c4530df-0d4f-42a5-bd91-0366716c8c24\"},\"edata\":{\"id\":\"content-detail\",\"type\":\"OTHER\",\"pageid\":\"content-detail\",\"subtype\":\"detail\",\"extra\":{\"values\":[{\"isDownloaded\":true,\"isUpdateAvailable\":false}]}},\"eid\":\"INTERACT\",\"ver\":\"3.0\",\"ets\":1551344686294,\"context\":{\"pdata\":{\"ver\":\"2.0.localstaging-debug\",\"pid\":\"sunbird.app\",\"id\":\"staging.diksha.app\"},\"channel\":\"01231711180382208027\",\"env\":\"home\",\"did\":\"6c61348dc9841f27c96f4887b64ee1f777d74c38\",\"sid\":\"cef2d0be-83fc-4988-8ad9-1b72399e6d3a\",\"cdata\":[]},\"mid\":\"3318a611-50fa-4ae9-9167-7b4390a62b9f\",\"object\":{\"id\":\"do_21228031946955980819\",\"type\":\"Worksheet\",\"version\":\"1.0\",\"rollup\":{\"l4\":\"do_21270636501657190413450\",\"l1\":\"do_21270636097196032013440\",\"l2\":\"do_21270636501655552013444\",\"l3\":\"do_21270636501657190413448\"}},\"tags\":[],\"syncts\":1551344699388,\"@timestamp\":\"2019-02-28T09:04:59.388Z\",\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":true,\"user_location_retrieved\":false,\"content_data_retrieved\":true,\"user_data_retrieved\":true,\"device_data_retrieved\":true},\"type\":\"events\",\"ts\":\"2019-02-28T09:04:46.294+0000\",\"devicedata\":{\"statecustomcode\":\"\",\"country\":\"\",\"city\":\"\",\"countrycode\":\"\",\"state\":\"\",\"statecode\":\"\",\"districtcustom\":\"\",\"statecustomname\":\"\",\"uaspec\":{},\"firstaccess\":1545920698694},\"userdata\":{\"district\":\"\",\"state\":\"\",\"subject\":[\"English\"],\"grade\":[\"KG\",\"Class 1\",\"Class 2\",\"Class 3\",\"Class 4\",\"Class 5\",\"Class 6\",\"Class 7\",\"Class 8\",\"Class 9\",\"Class 10\",\"Class 11\",\"Class 12\",\"Other\"],\"language\":[\"English\",\"Gujarati\",\"Hindi\"]},\"contentdata\":{\"pkgversion\":\"1\",\"language\":[\"Assamese\"],\"lastpublishedon\":1499851249497,\"contenttype\":\"Resource\",\"lastupdatedon\":1499851152176,\"framework\":\"NCF\",\"name\":\"Test review process\",\"mimetype\":\"application/vnd.ekstep.ecml-archive\",\"objecttype\":\"Content\",\"mediatype\":\"content\",\"status\":\"Live\"}}";

    /**
     * Passing Invalid event having wrong `gradelist` data type
     */
    public static final String INVALID_USERDATA = "{\"eid\":\"SEARCH\",\"ets\":1551332806642,\"ver\":\"3.0\",\"mid\":\"LP.1551332806642.922fb715-b4ed-421c-bae4-5ec48fd4d255\",\"actor\":{\"id\":\"org.ekstep.learning.platform\",\"type\":\"System\"},\"context\":{\"channel\":\"in.ekstep\",\"pdata\":{\"id\":\"staging.ntp.learning.platform\",\"pid\":\"search-service\",\"ver\":\"1.0\"},\"env\":\"search\"},\"edata\":{\"size\":259168,\"query\":\"\",\"filters\":{},\"sort\":{},\"type\":\"all\",\"topn\":[{\"identifier\":\"mh_fm_1_topic_science_usefulandharmfulmicrobes_harmfulmicroorganismsclostridiumandothers\"},{\"identifier\":\"mh_fm_1_topic_science_classificationofplants_subkingdomangiosperms\"},{\"identifier\":\"do_21254796078943436811896\"},{\"identifier\":\"do_21253024920136089612518\"},{\"identifier\":\"do_2125457233644748801997\"}]},\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":false},\"type\":\"events\",\"syncts\":1551341370427,\"@timestamp\":\"2019-02-28T08:09:30.427Z\",\"ts\":\"2019-02-28T05:46:46.642+0000\",\"devicedata\":{\"statecustomcode\":\"\",\"country\":\"\",\"city\":\"\",\"countrycode\":\"\",\"state\":\"\",\"statecode\":\"\",\"districtcustom\":\"\",\"statecustomname\":\"\"},\"dialcodedata\":{\"channel\":\"\",\"batchcode\":\"\",\"publisher\":\"\",\"generatedon\":43568797685,\"publishedon\":508978490534,\"status\":\"\",\"objecttype\":\"\"},\"userdata\":{\"gradelist\":{},\"languagelist\":[],\"subjectlist\":[],\"usertype\":\"\",\"state\":\"\",\"district\":\"\"}}";

    /**
     * Passing Invalid Device data having wrong `firstAccess` data type
     */
    public static final String INVALID_DEVICEDATA_CASE_1 = "{\"actor\":{\"type\":\"User\",\"id\":\"4c4530df-0d4f-42a5-bd91-0366716c8c24\"},\"edata\":{\"id\":\"content-detail\",\"type\":\"OTHER\",\"pageid\":\"content-detail\",\"subtype\":\"detail\",\"extra\":{\"values\":[{\"isDownloaded\":true,\"isUpdateAvailable\":false}]}},\"eid\":\"INTERACT\",\"ver\":\"3.0\",\"ets\":1551344686294,\"context\":{\"pdata\":{\"ver\":\"2.0.localstaging-debug\",\"pid\":\"sunbird.app\",\"id\":\"staging.diksha.app\"},\"channel\":\"01231711180382208027\",\"env\":\"home\",\"did\":\"6c61348dc9841f27c96f4887b64ee1f777d74c38\",\"sid\":\"cef2d0be-83fc-4988-8ad9-1b72399e6d3a\",\"cdata\":[]},\"mid\":\"3318a611-50fa-4ae9-9167-7b4390a62b9f\",\"object\":{\"id\":\"do_21228031946955980819\",\"type\":\"Worksheet\",\"version\":\"1.0\",\"rollup\":{\"l4\":\"do_21270636501657190413450\",\"l1\":\"do_21270636097196032013440\",\"l2\":\"do_21270636501655552013444\",\"l3\":\"do_21270636501657190413448\"}},\"tags\":[],\"syncts\":1551344699388,\"@timestamp\":\"2019-02-28T09:04:59.388Z\",\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":true,\"user_location_retrieved\":false,\"content_data_retrieved\":true,\"user_data_retrieved\":true,\"device_data_retrieved\":true},\"type\":\"events\",\"ts\":\"2019-02-28T09:04:46.294+0000\",\"devicedata\":{\"statecustomcode\":\"KA\",\"country\":\"India\",\"city\":\"Banglore\",\"countrycode\":\"IND\",\"state\":\"Karnataka\",\"statecode\":\"\",\"districtcustom\":\"\",\"statecustomname\":\"\",\"uaspec\":{},\"firstaccess\":\"1545920698694\"},\"userdata\":{\"district\":\"\",\"state\":\"\",\"subject\":[\"English\"],\"grade\":[\"KG\",\"Class 1\",\"Class 2\",\"Class 3\",\"Class 4\",\"Class 5\",\"Class 6\",\"Class 7\",\"Class 8\",\"Class 9\",\"Class 10\",\"Class 11\",\"Class 12\",\"Other\"],\"language\":[\"English\",\"Gujarati\",\"Hindi\"]},\"contentdata\":{\"pkgversion\":1,\"language\":[\"Assamese\"],\"lastpublishedon\":1499851249497,\"contenttype\":\"Resource\",\"lastupdatedon\":1499851152176,\"framework\":\"NCF\",\"name\":\"Test review process\",\"mimetype\":\"application/vnd.ekstep.ecml-archive\",\"objecttype\":\"Content\",\"mediatype\":\"content\",\"status\":\"Live\"}}";

    /**
     * Passing Invalid Device data having wrong `uaspec` data type
     */
    public static final String INVALID_DEVICEDATA_CASE_2 = "{\"actor\":{\"type\":\"User\",\"id\":\"4c4530df-0d4f-42a5-bd91-0366716c8c24\"},\"edata\":{\"id\":\"content-detail\",\"type\":\"OTHER\",\"pageid\":\"content-detail\",\"subtype\":\"detail\",\"extra\":{\"values\":[{\"isDownloaded\":true,\"isUpdateAvailable\":false}]}},\"eid\":\"INTERACT\",\"ver\":\"3.0\",\"ets\":1551344686294,\"context\":{\"pdata\":{\"ver\":\"2.0.localstaging-debug\",\"pid\":\"sunbird.app\",\"id\":\"staging.diksha.app\"},\"channel\":\"01231711180382208027\",\"env\":\"home\",\"did\":\"6c61348dc9841f27c96f4887b64ee1f777d74c38\",\"sid\":\"cef2d0be-83fc-4988-8ad9-1b72399e6d3a\",\"cdata\":[]},\"mid\":\"3318a611-50fa-4ae9-9167-7b4390a62b9f\",\"object\":{\"id\":\"do_21228031946955980819\",\"type\":\"Worksheet\",\"version\":\"1.0\",\"rollup\":{\"l4\":\"do_21270636501657190413450\",\"l1\":\"do_21270636097196032013440\",\"l2\":\"do_21270636501655552013444\",\"l3\":\"do_21270636501657190413448\"}},\"tags\":[],\"syncts\":1551344699388,\"@timestamp\":\"2019-02-28T09:04:59.388Z\",\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":true,\"user_location_retrieved\":false,\"content_data_retrieved\":true,\"user_data_retrieved\":true,\"device_data_retrieved\":true},\"type\":\"events\",\"ts\":\"2019-02-28T09:04:46.294+0000\",\"devicedata\":{\"statecustomcode\":\"KA\",\"country\":\"India\",\"city\":\"Banglore\",\"countrycode\":\"IND\",\"state\":\"Karnataka\",\"statecode\":\"\",\"districtcustom\":\"\",\"statecustomname\":\"\",\"uaspec\":[],\"firstaccess\":1545920698694},\"userdata\":{\"district\":\"\",\"state\":\"\",\"subject\":[\"English\"],\"grade\":[\"KG\",\"Class 1\",\"Class 2\",\"Class 3\",\"Class 4\",\"Class 5\",\"Class 6\",\"Class 7\",\"Class 8\",\"Class 9\",\"Class 10\",\"Class 11\",\"Class 12\",\"Other\"],\"language\":[\"English\",\"Gujarati\",\"Hindi\"]},\"contentdata\":{\"pkgversion\":1,\"language\":[\"Assamese\"],\"lastpublishedon\":1499851249497,\"contenttype\":\"Resource\",\"lastupdatedon\":1499851152176,\"framework\":\"NCF\",\"name\":\"Test review process\",\"mimetype\":\"application/vnd.ekstep.ecml-archive\",\"objecttype\":\"Content\",\"mediatype\":\"content\",\"status\":\"Live\"}}";

    /**
     * Passing Invalid Device data having wrong `country` data type and missing few fields
     */
    public static final String INVALID_DEVICEDATA_CASE_3 = "{\"actor\":{\"type\":\"User\",\"id\":\"4c4530df-0d4f-42a5-bd91-0366716c8c24\"},\"edata\":{\"id\":\"content-detail\",\"type\":\"OTHER\",\"pageid\":\"content-detail\",\"subtype\":\"detail\",\"extra\":{\"values\":[{\"isDownloaded\":true,\"isUpdateAvailable\":false}]}},\"eid\":\"INTERACT\",\"ver\":\"3.0\",\"ets\":1551344686294,\"context\":{\"pdata\":{\"ver\":\"2.0.localstaging-debug\",\"pid\":\"sunbird.app\",\"id\":\"staging.diksha.app\"},\"channel\":\"01231711180382208027\",\"env\":\"home\",\"did\":\"6c61348dc9841f27c96f4887b64ee1f777d74c38\",\"sid\":\"cef2d0be-83fc-4988-8ad9-1b72399e6d3a\",\"cdata\":[]},\"mid\":\"3318a611-50fa-4ae9-9167-7b4390a62b9f\",\"object\":{\"id\":\"do_21228031946955980819\",\"type\":\"Worksheet\",\"version\":\"1.0\",\"rollup\":{\"l4\":\"do_21270636501657190413450\",\"l1\":\"do_21270636097196032013440\",\"l2\":\"do_21270636501655552013444\",\"l3\":\"do_21270636501657190413448\"}},\"tags\":[],\"syncts\":1551344699388,\"@timestamp\":\"2019-02-28T09:04:59.388Z\",\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":true,\"user_location_retrieved\":false,\"content_data_retrieved\":true,\"user_data_retrieved\":true,\"device_data_retrieved\":true},\"type\":\"events\",\"ts\":\"2019-02-28T09:04:46.294+0000\",\"devicedata\":{\"statecustomcode\":\"KA\",\"country\":958743,\"city\":\"Banglore\",\"countrycode\":\"IND\",\"statecode\":\"\",\"districtcustom\":\"\",\"uaspec\":[],\"firstaccess\":1545920698694},\"userdata\":{\"district\":\"\",\"state\":\"\",\"subject\":[\"English\"],\"grade\":[\"KG\",\"Class 1\",\"Class 2\",\"Class 3\",\"Class 4\",\"Class 5\",\"Class 6\",\"Class 7\",\"Class 8\",\"Class 9\",\"Class 10\",\"Class 11\",\"Class 12\",\"Other\"],\"language\":[\"English\",\"Gujarati\",\"Hindi\"]},\"contentdata\":{\"pkgversion\":1,\"language\":[\"Assamese\"],\"lastpublishedon\":1499851249497,\"contenttype\":\"Resource\",\"lastupdatedon\":1499851152176,\"framework\":\"NCF\",\"name\":\"Test review process\",\"mimetype\":\"application/vnd.ekstep.ecml-archive\",\"objecttype\":\"Content\",\"mediatype\":\"content\",\"status\":\"Live\"}}";

    /**
     * Passing Invalid Device data having wrong `firstAccess` data type to float format
     */
    public static final String INVALID_DEVICEDATA_CASE_4 = "{\"actor\":{\"type\":\"User\",\"id\":\"4c4530df-0d4f-42a5-bd91-0366716c8c24\"},\"edata\":{\"id\":\"content-detail\",\"type\":\"OTHER\",\"pageid\":\"content-detail\",\"subtype\":\"detail\",\"extra\":{\"values\":[{\"isDownloaded\":true,\"isUpdateAvailable\":false}]}},\"eid\":\"INTERACT\",\"ver\":\"3.0\",\"ets\":1551344686294,\"context\":{\"pdata\":{\"ver\":\"2.0.localstaging-debug\",\"pid\":\"sunbird.app\",\"id\":\"staging.diksha.app\"},\"channel\":\"01231711180382208027\",\"env\":\"home\",\"did\":\"6c61348dc9841f27c96f4887b64ee1f777d74c38\",\"sid\":\"cef2d0be-83fc-4988-8ad9-1b72399e6d3a\",\"cdata\":[]},\"mid\":\"3318a611-50fa-4ae9-9167-7b4390a62b9f\",\"object\":{\"id\":\"do_21228031946955980819\",\"type\":\"Worksheet\",\"version\":\"1.0\",\"rollup\":{\"l4\":\"do_21270636501657190413450\",\"l1\":\"do_21270636097196032013440\",\"l2\":\"do_21270636501655552013444\",\"l3\":\"do_21270636501657190413448\"}},\"tags\":[],\"syncts\":1551344699388,\"@timestamp\":\"2019-02-28T09:04:59.388Z\",\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":true,\"user_location_retrieved\":false,\"content_data_retrieved\":true,\"user_data_retrieved\":true,\"device_data_retrieved\":true},\"type\":\"events\",\"ts\":\"2019-02-28T09:04:46.294+0000\",\"devicedata\":{\"statecustomcode\":\"\",\"country\":\"\",\"city\":\"\",\"countrycode\":\"\",\"state\":\"\",\"statecode\":\"\",\"districtcustom\":\"\",\"statecustomname\":\"\",\"uaspec\":{},\"firstaccess\":12},\"userdata\":{\"district\":\"\",\"state\":\"\",\"subject\":[\"English\"],\"grade\":[\"KG\",\"Class 1\",\"Class 2\",\"Class 3\",\"Class 4\",\"Class 5\",\"Class 6\",\"Class 7\",\"Class 8\",\"Class 9\",\"Class 10\",\"Class 11\",\"Class 12\",\"Other\"],\"language\":[\"English\",\"Gujarati\",\"Hindi\"]},\"contentdata\":{\"pkgversion\":1,\"language\":[\"Assamese\"],\"lastpublishedon\":1499851249497,\"contenttype\":\"Resource\",\"lastupdatedon\":1499851152176,\"framework\":\"NCF\",\"name\":\"Test review process\",\"mimetype\":\"application/vnd.ekstep.ecml-archive\",\"objecttype\":\"Content\",\"mediatype\":\"content\",\"status\":\"Live\"}}";


    /**
     * Passing Invalid Event Having wrong data type format in Languages field
     */
    public static final String INVALID_CONTENTDATA = "{\"actor\":{\"type\":\"User\",\"id\":\"4c4530df-0d4f-42a5-bd91-0366716c8c24\"},\"edata\":{\"id\":\"content-detail\",\"type\":\"OTHER\",\"pageid\":\"content-detail\",\"subtype\":\"detail\",\"extra\":{\"values\":[{\"isDownloaded\":true,\"isUpdateAvailable\":false}]}},\"eid\":\"INTERACT\",\"ver\":\"3.0\",\"ets\":1551344686294,\"context\":{\"pdata\":{\"ver\":\"2.0.localstaging-debug\",\"pid\":\"sunbird.app\",\"id\":\"staging.diksha.app\"},\"channel\":\"01231711180382208027\",\"env\":\"home\",\"did\":\"6c61348dc9841f27c96f4887b64ee1f777d74c38\",\"sid\":\"cef2d0be-83fc-4988-8ad9-1b72399e6d3a\",\"cdata\":[]},\"mid\":\"3318a611-50fa-4ae9-9167-7b4390a62b9f\",\"object\":{\"id\":\"do_21228031946955980819\",\"type\":\"Worksheet\",\"version\":\"1.0\",\"rollup\":{\"l4\":\"do_21270636501657190413450\",\"l1\":\"do_21270636097196032013440\",\"l2\":\"do_21270636501655552013444\",\"l3\":\"do_21270636501657190413448\"}},\"tags\":[],\"syncts\":1551344699388,\"@timestamp\":\"2019-02-28T09:04:59.388Z\",\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":true,\"user_location_retrieved\":false,\"content_data_retrieved\":true,\"user_data_retrieved\":true,\"device_data_retrieved\":true},\"type\":\"events\",\"ts\":\"2019-02-28T09:04:46.294+0000\",\"devicedata\":{\"statecustomcode\":\"\",\"country\":\"\",\"city\":\"\",\"countrycode\":\"\",\"state\":\"\",\"statecode\":\"\",\"districtcustom\":\"\",\"statecustomname\":\"\",\"uaspec\":{},\"firstaccess\":125465324536475},\"userdata\":{\"district\":\"\",\"state\":\"\",\"subject\":[\"English\"],\"grade\":[\"KG\",\"Class 1\",\"Class 2\",\"Class 3\",\"Class 4\",\"Class 5\",\"Class 6\",\"Class 7\",\"Class 8\",\"Class 9\",\"Class 10\",\"Class 11\",\"Class 12\",\"Other\"],\"language\":[\"English\",\"Gujarati\",\"Hindi\"]},\"contentdata\":{\"pkgversion\":1,\"language\":{},\"lastpublishedon\":1499851249497,\"contenttype\":\"Resource\",\"lastupdatedon\":1499851152176,\"framework\":\"NCF\",\"name\":\"Test review process\",\"mimetype\":\"application/vnd.ekstep.ecml-archive\",\"objecttype\":\"Content\",\"mediatype\":\"content\",\"status\":\"Live\"}}";

    /**
     *
     */
    public static final String INVALID_DIALCODEDATA ="{\"eid\":\"SEARCH\",\"ets\":1551332806642,\"ver\":\"3.0\",\"mid\":\"LP.1551332806642.922fb715-b4ed-421c-bae4-5ec48fd4d255\",\"actor\":{\"id\":\"org.ekstep.learning.platform\",\"type\":\"System\"},\"context\":{\"channel\":\"in.ekstep\",\"pdata\":{\"id\":\"staging.ntp.learning.platform\",\"pid\":\"search-service\",\"ver\":\"1.0\"},\"env\":\"search\"},\"edata\":{\"size\":259168,\"query\":\"\",\"filters\":{},\"sort\":{},\"type\":\"all\",\"topn\":[{\"identifier\":\"mh_fm_1_topic_science_usefulandharmfulmicrobes_harmfulmicroorganismsclostridiumandothers\"},{\"identifier\":\"mh_fm_1_topic_science_classificationofplants_subkingdomangiosperms\"},{\"identifier\":\"do_21254796078943436811896\"},{\"identifier\":\"do_21253024920136089612518\"},{\"identifier\":\"do_2125457233644748801997\"}]},\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":false},\"type\":\"events\",\"syncts\":1551341370427,\"@timestamp\":\"2019-02-28T08:09:30.427Z\",\"ts\":\"2019-02-28T05:46:46.642+0000\",\"devicedata\":{\"statecustomcode\":\"\",\"country\":\"\",\"city\":\"\",\"countrycode\":\"\",\"state\":\"\",\"statecode\":\"\",\"districtcustom\":\"\",\"statecustomname\":\"\"},\"dialcodedata\":{\"channel\":\"\",\"batchcode\":\"\",\"publisher\":\"\",\"generatedon\":43568797685,\"publishedon\":508978490534,\"objecttype\":1}}";

    /**
     * case sensitive dialcode key validation
     *
     */
    public static final String INVALID_DIALCODE_KEY ="{\"eid\":\"SEARCH\",\"ets\":1551977394372,\"ver\":\"3.0\",\"mid\":\"LP.1551977394372.d18d0bdf-9fbe-42ff-ac2c-3cb7306462a6\",\"actor\":{\"id\":\"org.ekstep.learning.platform\",\"type\":\"System\"},\"context\":{\"channel\":\"505c7c48ac6dc1edc9b08f21db5a571d\",\"pdata\":{\"id\":\"prod.ntp.learning.platform\",\"pid\":\"search-service\",\"ver\":\"1.0\"},\"env\":\"search\",\"did\":\"530f35ec69f30432bd5e87eee4e7d9a82da0e129\"},\"edata\":{\"size\":14,\"query\":\"\",\"filters\":{\"channels\":\"yes\",\"dialCodes\":\"\",\"objectType\":[\"Content\",\"ContentImage\"],\"contentType\":[\"Resource\"],\"status\":[\"Live\"],\"compatibilityLevel\":{\"min\":1,\"max\":4},\"channel\":{\"ne\":[\"0124433024890224640\",\"0124446042259128320\",\"0124487522476933120\",\"0125840271570288640\",\"0124453662635048969\"]},\"framework\":{},\"mimeType\":{},\"resourceType\":{}},\"sort\":{\"me_averageRating\":\"desc\"},\"type\":\"content\",\"topn\":[{\"identifier\":\"do_31251340904692121624142\"},{\"identifier\":\"do_31257347965634969621957\"},{\"identifier\":\"do_312580373603835904112177\"},{\"identifier\":\"do_312580362281156608111821\"},{\"identifier\":\"do_312580363689975808211635\"}]},\"cdata\":[{\"id\":\"prod.diksha.app\",\"type\":\"appId\"}],\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":true},\"type\":\"events\",\"syncts\":1551977394569,\"@timestamp\":\"2019-03-07T16:49:54.569Z\",\"ts\":\"2019-03-07T16:49:54.372+0000\",\"devicedata\":{\"country\":\"India\",\"city\":\"Coimbatore\",\"countrycode\":\"IN\",\"state\":\"Tamil Nadu\",\"statecode\":\"TN\"}}";

    /**
     *
     */
    public static final String VALID_DIALCODETYPE ="{\"eid\":\"SEARCH\",\"ets\":1551332806642,\"ver\":\"3.0\",\"mid\":\"LP.1551332806642.922fb715-b4ed-421c-bae4-5ec48fd4d255\",\"actor\":{\"id\":\"org.ekstep.learning.platform\",\"type\":\"System\"},\"context\":{\"channel\":\"in.ekstep\",\"pdata\":{\"id\":\"staging.ntp.learning.platform\",\"pid\":\"search-service\",\"ver\":\"1.0\"},\"env\":\"search\"},\"edata\":{\"size\":259168,\"query\":\"\",\"filters\":{},\"sort\":{},\"type\":\"all\",\"topn\":[{\"identifier\":\"mh_fm_1_topic_science_usefulandharmfulmicrobes_harmfulmicroorganismsclostridiumandothers\"},{\"identifier\":\"mh_fm_1_topic_science_classificationofplants_subkingdomangiosperms\"},{\"identifier\":\"do_21254796078943436811896\"},{\"identifier\":\"do_21253024920136089612518\"},{\"identifier\":\"do_2125457233644748801997\"}]},\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":false},\"type\":\"events\",\"syncts\":1551341370427,\"@timestamp\":\"2019-02-28T08:09:30.427Z\",\"ts\":\"2019-02-28T05:46:46.642+0000\",\"devicedata\":{\"statecustomcode\":\"\",\"country\":\"\",\"city\":\"\",\"countrycode\":\"\",\"state\":\"\",\"statecode\":\"\",\"districtcustom\":\"\",\"statecustomname\":\"\"},\"dialcodedata\":[{\"channel\":\"\",\"batchcode\":\"\",\"publisher\":\"\",\"generatedon\":43568797685,\"publishedon\":508978490534,\"status\":\"\",\"objecttype\":\"\"},{\"channel\":\"\",\"batchcode\":\"\",\"publisher\":\"\",\"generatedon\":76854768347,\"publishedon\":508978490534,\"status\":\"\",\"objecttype\":\"\"}]}";

    /**
     * When array of object is having in the dialcodedata and which is having invalid property in one of the object in the array. ex: generatedon
     */
    public static final String INVALID_DIALCODETYPE_CASE_1 ="{\"eid\":\"SEARCH\",\"ets\":1551332806642,\"ver\":\"3.0\",\"mid\":\"LP.1551332806642.922fb715-b4ed-421c-bae4-5ec48fd4d255\",\"actor\":{\"id\":\"org.ekstep.learning.platform\",\"type\":\"System\"},\"context\":{\"channel\":\"in.ekstep\",\"pdata\":{\"id\":\"staging.ntp.learning.platform\",\"pid\":\"search-service\",\"ver\":\"1.0\"},\"env\":\"search\"},\"edata\":{\"size\":259168,\"query\":\"\",\"filters\":{},\"sort\":{},\"type\":\"all\",\"topn\":[{\"identifier\":\"mh_fm_1_topic_science_usefulandharmfulmicrobes_harmfulmicroorganismsclostridiumandothers\"},{\"identifier\":\"mh_fm_1_topic_science_classificationofplants_subkingdomangiosperms\"},{\"identifier\":\"do_21254796078943436811896\"},{\"identifier\":\"do_21253024920136089612518\"},{\"identifier\":\"do_2125457233644748801997\"}]},\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":false},\"type\":\"events\",\"syncts\":1551341370427,\"@timestamp\":\"2019-02-28T08:09:30.427Z\",\"ts\":\"2019-02-28T05:46:46.642+0000\",\"devicedata\":{\"statecustomcode\":\"\",\"country\":\"\",\"city\":\"\",\"countrycode\":\"\",\"state\":\"\",\"statecode\":\"\",\"districtcustom\":\"\",\"statecustomname\":\"\"},\"dialcodedata\":[{\"channel\":\"\",\"batchcode\":\"\",\"publisher\":\"\",\"generatedon\":43568797685,\"publishedon\":508978490534,\"status\":\"\",\"objecttype\":\"\"},{\"channel\":\"\",\"batchcode\":\"\",\"publisher\":\"\",\"generatedon\":\"43568797685\",\"publishedon\":508978490534,\"status\":true,\"objecttype\":\"\"}]}";

    /**
     * When dialcodedata object is having invalid properties
     */
    public static final String INVALID_DIALCODETYPE_CASE_2 ="{\"eid\":\"SEARCH\",\"ets\":1551332806642,\"ver\":\"3.0\",\"mid\":\"LP.1551332806642.922fb715-b4ed-421c-bae4-5ec48fd4d255\",\"actor\":{\"id\":\"org.ekstep.learning.platform\",\"type\":\"System\"},\"context\":{\"channel\":\"in.ekstep\",\"pdata\":{\"id\":\"staging.ntp.learning.platform\",\"pid\":\"search-service\",\"ver\":\"1.0\"},\"env\":\"search\"},\"edata\":{\"size\":259168,\"query\":\"\",\"filters\":{},\"sort\":{},\"type\":\"all\",\"topn\":[{\"identifier\":\"mh_fm_1_topic_science_usefulandharmfulmicrobes_harmfulmicroorganismsclostridiumandothers\"},{\"identifier\":\"mh_fm_1_topic_science_classificationofplants_subkingdomangiosperms\"},{\"identifier\":\"do_21254796078943436811896\"},{\"identifier\":\"do_21253024920136089612518\"},{\"identifier\":\"do_2125457233644748801997\"}]},\"flags\":{\"tv_processed\":true,\"dd_processed\":true,\"device_location_retrieved\":false},\"type\":\"events\",\"syncts\":1551341370427,\"@timestamp\":\"2019-02-28T08:09:30.427Z\",\"ts\":\"2019-02-28T05:46:46.642+0000\",\"devicedata\":{\"statecustomcode\":\"\",\"country\":\"\",\"city\":\"\",\"countrycode\":\"\",\"state\":\"\",\"statecode\":\"\",\"districtcustom\":\"\",\"statecustomname\":\"\"},\"dialcodedata\":{\"channel\":1234534534,\"batchcode\":\"\",\"publisher\":\"\",\"generatedon\":\"43568797685\",\"publishedon\":508978490534,\"status\":true,\"objecttype\":\"\"}}";

    /**
     *
     */
    public static final String VALID_LOG_EVENT = "{\"eid\":\"LOG\",\"ets\":1561717784892,\"ver\":\"3.0\",\"mid\":\"LP.1561717784892.1345e38d-f799-403f-93d9-1db71011627f\",\"actor\":{\"id\":\"org.ekstep.learning.platform\",\"type\":\"System\"},\"context\":{\"channel\":\"505c7c48ac6dc1edc9b08f21db5a571d\",\"pdata\":{\"id\":\"prod.diksha.app\",\"pid\":\"learning-service\",\"ver\":\"1.0\"},\"env\":\"content\",\"did\":\"beea4401850780ee685120597a6428ab14ff47a2\"},\"edata\":{\"level\":\"INFO\",\"type\":\"api_access\",\"message\":\"\",\"params\":[{\"duration\":5},{\"protocol\":\"HTTP/1.1\"},{\"size\":10350},{\"method\":\"GET\"},{\"rid\":\"ekstep.content.find\"},{\"uip\":\"11.4.0.37\"},{\"url\":\"/learning-service/content/v3/read/do_31274885743485747215761\"},{\"status\":200}]},\"syncts\":1561717784892,\"flags\":{\"tv_processed\":true,\"dd_processed\":true},\"type\":\"events\",\"@timestamp\":\"2019-06-28T10:29:44.892Z\"}";

    public static final String VALID_USER_DECLARED_AND_DERIVED_LOCATION_EVENT = "{\"actor\":{\"type\":\"User\",\"id\":\"393407b1-66b1-4c86-9080-b2bce9842886\"},\"eid\":\"INTERACT\",\"edata\":{\"id\":\"ContentDetail\",\"pageid\":\"ContentDetail\",\"type\":\"TOUCH\",\"subtype\":\"ContentDownload-Initiate\"},\"ver\":\"3.0\",\"ets\":1.54157454518E12,\"context\":{\"pdata\":{\"ver\":\"2.1.8\",\"pid\":\"sunbird.app\",\"id\":\"prod.diksha.app\"},\"channel\":\"0123221617357783046602\",\"env\":\"sdk\",\"did\":\"68dfc64a7751ad47617ac1a4e0531fb761ebea6f\",\"cdata\":[{\"type\":\"qr\",\"id\":\"K4KCXE\"},{\"type\":\"API\",\"id\":\"f3ac6610-d218-11e8-b2bb-1598ac1fcb99\"}],\"sid\":\"70ea93d0-e521-4030-934f-276e7194c225\"},\"mid\":\"e6a3bcd3-eb78-457b-8fc0-4acc94642ebf\",\"object\":{\"id\":\"do_31249561779090227216256\",\"type\":\"Content\",\"version\":\"\"},\"tags\":[],\"syncts\":1.539846605341E12,\"@timestamp\":\"2018-10-18T07:10:05.341Z\",\"derivedlocationdata\":{\"district\":\"Bengaluru\",\"state\":\"Karnataka\",\"from\":\"user-profile\"},\"devicedata\":{\"statecustomcode\":\"KA-Custom\",\"country\":\"India\",\"iso3166statecode\":\"IN-KA\",\"city\":\"Bangalore\",\"countrycode\":\"IN\",\"statecode\":\"KA\",\"devicespec\":{\"os\":\"Android 6.0\",\"make\":\"Motorola XT1706\"},\"districtcustom\":\"Banglore-Custom\",\"firstaccess\":1559484698000,\"uaspec\":{\"agent\":\"Mozilla\",\"ver\":\"5.0\",\"system\":\"iPad\",\"raw\":\"Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)\",\"platform\":\"AppleWebKit/531.21.10\"},\"state\":\"Karnataka\",\"statecustomname\":\"Karnatak-Custom\",\"userdeclared\":{\"district\":\"Bangalore\",\"state\":\"Karnataka\"}},\"flags\":{\"device_location_retrieved\":true,\"device_profile_retrieved\":true}}";

    public static final String INVALID_USER_DECLARED_LOCATION_EVENT = "{\"actor\":{\"type\":\"User\",\"id\":\"393407b1-66b1-4c86-9080-b2bce9842886\"},\"eid\":\"INTERACT\",\"edata\":{\"id\":\"ContentDetail\",\"pageid\":\"ContentDetail\",\"type\":\"TOUCH\",\"subtype\":\"ContentDownload-Initiate\"},\"ver\":\"3.0\",\"ets\":1.54157454518E12,\"context\":{\"pdata\":{\"ver\":\"2.1.8\",\"pid\":\"sunbird.app\",\"id\":\"prod.diksha.app\"},\"channel\":\"0123221617357783046602\",\"env\":\"sdk\",\"did\":\"68dfc64a7751ad47617ac1a4e0531fb761ebea6f\",\"cdata\":[{\"type\":\"qr\",\"id\":\"K4KCXE\"},{\"type\":\"API\",\"id\":\"f3ac6610-d218-11e8-b2bb-1598ac1fcb99\"}],\"sid\":\"70ea93d0-e521-4030-934f-276e7194c225\"},\"mid\":\"e6a3bcd3-eb78-457b-8fc0-4acc94642ebf\",\"object\":{\"id\":\"do_31249561779090227216256\",\"type\":\"Content\",\"version\":\"\"},\"tags\":[],\"syncts\":1.539846605341E12,\"@timestamp\":\"2018-10-18T07:10:05.341Z\",\"derivedlocationdata\":{\"district\":\"Bengaluru\",\"state\":\"Karnataka\",\"from\":\"user-profile\"},\"devicedata\":{\"statecustomcode\":\"KA-Custom\",\"country\":\"India\",\"iso3166statecode\":\"IN-KA\",\"city\":\"Bangalore\",\"countrycode\":\"IN\",\"statecode\":\"KA\",\"devicespec\":{\"os\":\"Android 6.0\",\"make\":\"Motorola XT1706\"},\"districtcustom\":\"Banglore-Custom\",\"firstaccess\":1559484698000,\"uaspec\":{\"agent\":\"Mozilla\",\"ver\":\"5.0\",\"system\":\"iPad\",\"raw\":\"Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)\",\"platform\":\"AppleWebKit/531.21.10\"},\"state\":\"Karnataka\",\"statecustomname\":\"Karnatak-Custom\",\"userdeclared\":{\"district\":12,\"state\":\"Karnataka\"}},\"flags\":{\"device_location_retrieved\":true,\"device_profile_retrieved\":true}}";

    public static final String IVALID_DERIVED_LOCATION_EVENT = "{\"actor\":{\"type\":\"User\",\"id\":\"393407b1-66b1-4c86-9080-b2bce9842886\"},\"eid\":\"INTERACT\",\"edata\":{\"id\":\"ContentDetail\",\"pageid\":\"ContentDetail\",\"type\":\"TOUCH\",\"subtype\":\"ContentDownload-Initiate\"},\"ver\":\"3.0\",\"ets\":1.54157454518E12,\"context\":{\"pdata\":{\"ver\":\"2.1.8\",\"pid\":\"sunbird.app\",\"id\":\"prod.diksha.app\"},\"channel\":\"0123221617357783046602\",\"env\":\"sdk\",\"did\":\"68dfc64a7751ad47617ac1a4e0531fb761ebea6f\",\"cdata\":[{\"type\":\"qr\",\"id\":\"K4KCXE\"},{\"type\":\"API\",\"id\":\"f3ac6610-d218-11e8-b2bb-1598ac1fcb99\"}],\"sid\":\"70ea93d0-e521-4030-934f-276e7194c225\"},\"mid\":\"e6a3bcd3-eb78-457b-8fc0-4acc94642ebf\",\"object\":{\"id\":\"do_31249561779090227216256\",\"type\":\"Content\",\"version\":\"\"},\"tags\":[],\"syncts\":1.539846605341E12,\"@timestamp\":\"2018-10-18T07:10:05.341Z\",\"derivedlocationdata\":{\"district\":\"Bengaluru\",\"state\":1234,\"from\":\"user-profile\"},\"devicedata\":{\"statecustomcode\":\"KA-Custom\",\"country\":\"India\",\"iso3166statecode\":\"IN-KA\",\"city\":\"Bangalore\",\"countrycode\":\"IN\",\"statecode\":\"KA\",\"devicespec\":{\"os\":\"Android 6.0\",\"make\":\"Motorola XT1706\"},\"districtcustom\":\"Banglore-Custom\",\"firstaccess\":1559484698000,\"uaspec\":{\"agent\":\"Mozilla\",\"ver\":\"5.0\",\"system\":\"iPad\",\"raw\":\"Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)\",\"platform\":\"AppleWebKit/531.21.10\"},\"state\":\"Karnataka\",\"statecustomname\":\"Karnatak-Custom\",\"userdeclared\":{\"district\":\"Bangalore\",\"state\":\"Karnataka\"}},\"flags\":{\"device_location_retrieved\":true,\"device_profile_retrieved\":true}}";

    public static final String INVALID_JSON  = "{\"eid\":\"LOG\",\"ets\":1570817279146,\"ver\":\"3.0\",\"mid\":\"LOG:2a210813fac274656f194f9807ad8abf\",\"actor\":{\"id\":\"1\",\"type\":\"service\"},\"context\":{\"channel\":\"505c7c48ac6dc1edc9b08f21db5a571d\",\"pdata\":{\"id\":\"prod.diksha.content-service\",\"ver\":\"1.0\",\"pid\":\"sunbird-content-service\"},\"env\":\"content\",\"sid\":\"\",\"did\":\"1e465bed2138b8f2764d946492293e2b2f628789\",\"cdata\":[],\"rollup\":{}},\"object\":{\"id\":\"do_312776645813747712111638\",\"type\":\"content\",\"ver\":\"\",\"rollup\":{}},\"tags\":,\"edata\":{\"type\":\"api_access\",\"level\":\"INFO\",\"message\":\"successful\",\"params\":[{\"rid\":\"0ece8640-ec52-11e9-a3d6-697ee5684e40\"},{\"title\":\"Content read api\"},{\"category\":\"contentread\"},{\"url\":\"content/read\"},{\"method\":\"GET\"}]},\"syncts\":1570817431399,\"@timestamp\":\"2019-10-11T18:10:31.399Z\",\"flags\":{\"tv_processed\":true,\"dd_processed\":true},\"type\":\"events\"}";

}

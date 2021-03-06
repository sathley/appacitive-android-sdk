package com.appacitive.java;

import com.appacitive.core.*;
import com.appacitive.core.exceptions.AppacitiveException;
import com.appacitive.core.exceptions.ValidationException;
import com.appacitive.core.infra.ErrorCodes;
import com.appacitive.core.infra.SystemDefinedPropertiesHelper;
import com.appacitive.core.model.Callback;
import com.appacitive.core.model.ConnectedObjectsResponse;
import com.appacitive.core.model.Environment;
import com.appacitive.core.model.PagedList;
import com.appacitive.core.query.*;
import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import org.junit.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by sathley.
 */
//@Ignore
public class ObjectTest {

    @BeforeClass
    public static void oneTimeSetUp() {
        AppacitiveContextBase.initialize(Keys.masterKey, Environment.sandbox, new JavaPlatform());
    }

    private AtomicBoolean somethingHappened = new AtomicBoolean(false);

    @AfterClass
    public static void oneTimeTearDown() {

        // one-time cleanup code
    }

    @Before
    public void beforeTest() {
        Awaitility.reset();
        somethingHappened.set(false);
    }

    @After
    public void afterTest() {
    }

    private String getRandomString() {
        return UUID.randomUUID().toString();
    }

    @Test
    public void createFullObjectTest() throws ValidationException, ParseException {

        AppacitiveObject newObject = new AppacitiveObject("object");
        newObject.setIntProperty("intfield", 100);
        newObject.setDoubleProperty("decimalfield", 20.251100);
        newObject.setBoolProperty("boolfield", true);
        newObject.setStringProperty("stringfield", "hello world");
        newObject.setStringProperty("textfield", "Objects represent your data stored inside the Appacitive platform. Every object is mapped to the type that you create via the designer in your management console. If we were to use conventional databases as a metaphor, then a type would correspond to a table and an object would correspond to one row inside that table. object api allows you to store, retrieve and manage all the data that you store inside Appacitive. You can retrieve individual records or lists of records based on a specific filter criteria.");
        final Date date = new Date();
        final String nowAsISODate = convertDateToString(date);
        final String nowAsISOTime = convertTimeToString(date);
        final String nowAsISODateTime = convertDateTimeToString(date);
        newObject.setStringProperty("datefield", nowAsISODate);
        newObject.setStringProperty("timefield", nowAsISOTime);
        newObject.setStringProperty("datetimefield", nowAsISODateTime);
        newObject.setStringProperty("geofield", "10.11, 20.22");
        newObject.setPropertyAsMultiValued("multifield", new ArrayList<String>() {{
            add("val1");
            add("500");
            add("false");
        }});

        newObject.addTag("t1");
        List<String> tags = new ArrayList<String>();
        tags.add("t2");
        tags.add("t3");

        newObject.addTags(tags);

        newObject.setAttribute("a1", "v1");
        newObject.setAttribute("a2", "v2");
        newObject.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                assert (result.getId() > 0);
                assert (result.getPropertyAsInt("intfield") == 100);
                assert (result.getPropertyAsDouble("decimalfield") == 20.2511d);
                assert (result.getPropertyAsBoolean("boolfield"));

//                assert (convertDateToString(result.getPropertyAsDate("datefield")).equals(nowAsISODate));
//                    assert (convertTimeToString(result.getPropertyAsTime("timefield")).equals(nowAsISOTime));
                assert (convertDateTimeToString(result.getPropertyAsDateTime("datetimefield")).equals(nowAsISODateTime));

                assert (result.getPropertyAsGeo("geofield")[0] == 10.11d);
                assert (result.getPropertyAsGeo("geofield")[1] == 20.22d);
                assert (result.getPropertyAsMultiValuedString("multifield").size() == 3);
                assert ((result.getPropertyAsMultiValuedString("multifield").get(0).equals("val1")));
                assert ((result.getPropertyAsMultiValuedString("multifield").get(1).equals("500")));
                assert ((result.getPropertyAsMultiValuedString("multifield").get(2).equals("false")));
                somethingHappened.set(true);
            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().untilTrue(somethingHappened);
    }

    @Test
    public void dateTimePropertiesTest() throws ValidationException {
        AppacitiveObject object = new AppacitiveObject("object");
        Date now = new Date();
//        object.setDateProperty("datefield", now);
//        object.setTimeProperty("timefield", now);
        object.setDateTimeProperty("datetimefield", now);

        final String origDateStr = convertDateToString(now);
//        final String origTimeStr = convertTimeToString(now);
        final String origDatetimeStr = convertDateTimeToString(now);

        object.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                Date date = null;
                Date time = null;
                Date datetime = null;

//                date = result.getPropertyAsDate("datefield");
//                    time = result.getPropertyAsTime("timefield");
                datetime = result.getPropertyAsDateTime("datetimefield");


                final String dateStr = convertDateToString(date);
//                final String timeStr = convertTimeToString(time);
                final String datetimeStr = convertDateTimeToString(datetime);

                assert dateStr.equals(origDateStr);
//                assert timeStr.equals(origTimeStr);
                assert datetimeStr.equals(origDatetimeStr);

                somethingHappened.set(true);
            }
        });

        await().untilTrue(somethingHappened);
    }

    @Test
    public void multiLingualObjectCreateTest() throws ValidationException {
        AppacitiveObject newObject = new AppacitiveObject("object");
        final String randomString1 = "以下便是有关此问题的所有信息";
        final String randomString3 = "વિત ક્વિલપૅડ રોમિંગ યૂ વિલ બે ઍબલ તો રાઇટ ઇન ગુજરાતી ઓન ફસ્ેબૂક";
        final String randomString2 = "ä»¥ä¸ä¾¿æ¯æå³æ­¤é®é¢çææä¿¡æ¯";
//        newObject.setStringProperty("stringfield", randomString1);
        newObject.setStringProperty("textfield", randomString3);
        newObject.createInBackground(new Callback<AppacitiveObject>() {
            public void success(AppacitiveObject result) {
                Assert.assertTrue(result.getId() > 0);
                assert (result.getPropertyAsString("textfield").toString().equals(randomString2));
                assert (result.getPropertyAsString("stringfield").toString().equals(randomString1));
                somethingHappened.set(true);
            }

            public void failure(AppacitiveObject result, AppacitiveException e) {
                Assert.fail(e.getMessage());
            }
        });
        await().untilTrue(somethingHappened);
    }

    private AppacitiveObject getRandomObject() {
        AppacitiveObject newObject = new AppacitiveObject("object");
        newObject.setIntProperty("intfield", 100);
        newObject.setDoubleProperty("decimalfield", 20.251100);
        newObject.setBoolProperty("boolfield", true);
        newObject.setStringProperty("stringfield", "hello world");
        newObject.setStringProperty("textfield", "Objects represent your data stored inside the Appacitive platform. Every object is mapped to the type that you create via the designer in your management console. If we were to use conventional databases as a metaphor, then a type would correspond to a table and an object would correspond to one row inside that table. object api allows you to store, retrieve and manage all the data that you store inside Appacitive. You can retrieve individual records or lists of records based on a specific filter criteria.");
        final Date date = new Date();
        final String nowAsISODate = convertDateToString(date);
        final String nowAsISOTime = convertTimeToString(date);
        final String nowAsISODateTime = convertDateTimeToString(date);
        newObject.setStringProperty("datefield", nowAsISODate);
        newObject.setStringProperty("timefield", nowAsISOTime);
        newObject.setStringProperty("datetimefield", nowAsISODateTime);
        newObject.setStringProperty("geofield", "10.11, 20.22");
        newObject.setPropertyAsMultiValued("multifield", new ArrayList<String>() {{
            add("val1");
            add("500");
            add("false");
        }});

        newObject.addTag("t1");
        List<String> tags = new ArrayList<String>();
        tags.add("t2");
        tags.add("t3");

        newObject.addTags(tags);

        newObject.setAttribute("a1", "v1");
        newObject.setAttribute("a2", "v2");
        return newObject;
    }

    @Test
    public void updateObjectTest() throws Exception {
        getRandomObject().createInBackground(new Callback<AppacitiveObject>() {
            public void success(AppacitiveObject result) {
                result.setIntProperty("intfield", 200);
                result.setDoubleProperty("decimalfield", 40.50200);
                result.setBoolProperty("boolfield", false);
                result.setStringProperty("stringfield", "hello world again !!");
                final Date date = new Date();
                final String nowAsISODate = convertDateToString(date);
                final String nowAsISOTime = convertTimeToString(date);
                final String nowAsISODateTime = convertDateTimeToString(date);
                result.setStringProperty("datefield", nowAsISODate);
                result.setStringProperty("timefield", nowAsISOTime);
                result.setStringProperty("datetimefield", nowAsISODateTime);
                result.setStringProperty("geofield", "15.55, 33.88");
                result.setPropertyAsMultiValued("multifield", new ArrayList<String>() {{
                    add("val2");
                    add("800");
                    add("true");
                }});
                result.updateInBackground(false, new Callback<AppacitiveObject>() {
                    @Override
                    public void success(AppacitiveObject result) {
                        assert (result.getRevision() == 2);
                        assert (result.getPropertyAsInt("intfield") == 200);
                        assert (result.getPropertyAsDouble("decimalfield") == 40.502d);
                        assert (!result.getPropertyAsBoolean("boolfield"));
                        assert ((result.getPropertyAsString("datefield")).equals(nowAsISODate));
                        assert ((result.getPropertyAsString("timefield")).equals(nowAsISOTime));
                        assert ((result.getPropertyAsString("datetimefield")).equals(nowAsISODateTime));
                        assert (result.getPropertyAsGeo("geofield")[0] == 15.55d);
                        assert (result.getPropertyAsGeo("geofield")[1] == 33.88d);
                        assert (result.getPropertyAsMultiValuedString("multifield").size() == 3);
                        assert ((result.getPropertyAsMultiValuedString("multifield").get(0).equals("val2")));
                        assert ((result.getPropertyAsMultiValuedString("multifield").get(1).equals("800")));
                        assert ((result.getPropertyAsMultiValuedString("multifield").get(2).equals("true")));
                        somethingHappened.set(true);
                    }

                    @Override
                    public void failure(AppacitiveObject result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });

        await().untilTrue(somethingHappened);
    }

//    @Test
//    public void updateObjectPropertyAsNull() throws Exception {
//        AppacitiveObject appacitiveObject = getRandomObject();
//        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
//            @Override
//            public void success(AppacitiveObject result) {
//                result.setIntProperty("intfield", null);
//                result.setDoubleProperty("decimalfield", null);
//                result.setBoolProperty("boolfield", null);
//                result.setStringProperty("stringfield", null);
//                result.setStringProperty("textfield", null);
//                result.setDateProperty("datefield", null);
//                result.setTimeProperty("timefield", null);
//                result.setDateTimeProperty("datetimefield", null);
//                result.setGeoProperty("geofield", null);
//                result.setPropertyAsMultiValuedString("multifield", null);
//                result.updateInBackground(false, new Callback<AppacitiveObject>() {
//                    @Override
//                    public void success(AppacitiveObject result) {
//                        assert result.getRevision() == 2;
//                    }
//
//                    @Override
//                    public void failure(AppacitiveObject result, Exception e) {
//                        Assert.fail(e.getMessage());
//                    }
//                });
//            }
//        });
//    }

    public String convertDateToString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String result;
        synchronized (df) {
            result = df.format(date);
        }
        return result;
    }

    public String convertTimeToString(Date date) {
        DateFormat tf = new SimpleDateFormat("HH:mm:ss.SSSSSSS");
        String result;
        synchronized (tf) {
            result = tf.format(date);
        }
        return result;
    }

    public String convertDateTimeToString(Date date) {
        DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'");
        String result;
        synchronized (dtf) {
            result = dtf.format(date);
        }
        return result;
    }

    @Test
    public void updateEmptyObjectTest() throws ValidationException, ParseException {
        AppacitiveObject appacitiveObject = new AppacitiveObject("object");
        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                result.setIntProperty("intfield", 200);
                result.setDoubleProperty("decimalfield", 40.50200);
                result.setBoolProperty("boolfield", false);
                result.setStringProperty("stringfield", "hello world again !!");
                final Date date = new Date();

                final String nowAsISODate = convertDateToString(date);
                final String nowAsISOTime = convertTimeToString(date);
                final String nowAsISODateTime = convertDateTimeToString(date);
                result.setStringProperty("datefield", nowAsISODate);
                result.setStringProperty("timefield", nowAsISOTime);
                result.setStringProperty("datetimefield", nowAsISODateTime);
                result.setStringProperty("geofield", "15.55, 33.88");
                result.setPropertyAsMultiValued("multifield", new ArrayList<String>() {{
                    add("val2");
                    add("800");
                    add("true");
                }});

                result.updateInBackground(false, new Callback<AppacitiveObject>() {
                    @Override
                    public void success(AppacitiveObject result) {
                        assert (result.getRevision() == 2);
                        assert (result.getPropertyAsInt("intfield") == 200);
                        assert (result.getPropertyAsDouble("decimalfield") == 40.502d);
                        assert (!result.getPropertyAsBoolean("boolfield"));


                        assert ((result.getPropertyAsString("datefield")).equals(nowAsISODate));
                        assert ((result.getPropertyAsString("timefield")).equals(nowAsISOTime));
                        assert ((result.getPropertyAsString("datetimefield")).equals(nowAsISODateTime));

                        assert (result.getPropertyAsGeo("geofield")[0] == 15.55d);
                        assert (result.getPropertyAsGeo("geofield")[1] == 33.88d);
                        assert (result.getPropertyAsMultiValuedString("multifield").size() == 3);
                        assert ((result.getPropertyAsMultiValuedString("multifield").get(0).equals("val2")));
                        assert ((result.getPropertyAsMultiValuedString("multifield").get(1).equals("800")));
                        assert ((result.getPropertyAsMultiValuedString("multifield").get(2).equals("true")));
                        somethingHappened.set(true);
                    }

                    @Override
                    public void failure(AppacitiveObject result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }
        });

        await().untilTrue(somethingHappened);
    }

    @Test
    public void updateWithValidRevisionTest() throws ValidationException {
        AppacitiveObject appacitiveObject = new AppacitiveObject("object");
        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                result.addTag("t1");
                result.addTag("t2");
                result.setAttribute("a1", "v1");
                result.setAttribute("a2", "v2");
                result.updateInBackground(true, new Callback<AppacitiveObject>() {
                    @Override
                    public void success(AppacitiveObject result) {
                        assert result.getRevision() == 2;
                        somethingHappened.set(true);
                    }

                    @Override
                    public void failure(AppacitiveObject result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }
        });

        await().untilTrue(somethingHappened);
    }

    @Test
    public void updateWithInvalidRevisionTest() throws Exception {
        AppacitiveObject appacitiveObject = new AppacitiveObject("object");

        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(final AppacitiveObject origResult) {
                final long id = origResult.getId();
                AppacitiveObject.getInBackground("object", id, null, new Callback<AppacitiveObject>() {
                    @Override
                    public void success(AppacitiveObject returnOrigResult) {
                        returnOrigResult.updateInBackground(true, new Callback<AppacitiveObject>() {
                            @Override
                            public void success(AppacitiveObject result) {
                                origResult.updateInBackground(true, new Callback<AppacitiveObject>() {
                                    @Override
                                    public void success(AppacitiveObject result) {
                                        assert false;
                                    }

                                    @Override
                                    public void failure(AppacitiveObject result, Exception e) {
                                        AppacitiveException ae = (AppacitiveException) e;
                                        assert ae.getCode().equals(ErrorCodes.INCORRECT_REVISION);
                                        somethingHappened.set(true);
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().untilTrue(somethingHappened);

    }

//    @Test
//    public void updateExistingNullValuesToNullTest() throws ValidationException {
//        AppacitiveObject appacitiveObject = new AppacitiveObject("object");
//        appacitiveObject.setIntProperty("intfield", 0);
//        appacitiveObject.setProperty("decimalfield", null);
//        appacitiveObject.setProperty("boolfield", null);
//        appacitiveObject.setProperty("stringfield", null);
//        appacitiveObject.setProperty("textfield", null);
//        appacitiveObject.setProperty("datefield", null);
//        appacitiveObject.setProperty("timefield", null);
//        appacitiveObject.setProperty("datetimefield", null);
//        appacitiveObject.setProperty("geofield", null);
//        appacitiveObject.setProperty("multifield", null);
//        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
//            @Override
//            public void success(AppacitiveObject result) {
//                result.setProperty("intfield", null);
//                result.setProperty("decimalfield", null);
//                result.setProperty("boolfield", null);
//                result.setProperty("stringfield", null);
//                result.setProperty("textfield", null);
//                result.setProperty("datefield", null);
//                result.setProperty("timefield", null);
//                result.setProperty("datetimefield", null);
//                result.setProperty("geofield", null);
//                result.setProperty("multifield", null);
//                result.updateInBackground(false, new Callback<AppacitiveObject>() {
//                    @Override
//                    public void success(AppacitiveObject result) {
//                        assert result.getRevision() == 2;
//                    }
//
//                    @Override
//                    public void failure(AppacitiveObject result, Exception e) {
//                        Assert.fail(e.getMessage());
//                    }
//                });
//            }
//        });
//    }

    @Test
    public void updateAttributesTest() throws ValidationException {
        AppacitiveObject appacitiveObject = new AppacitiveObject("object");
        appacitiveObject.setAttribute("a1", "vx");
        appacitiveObject.setAttribute("a2", "v2");
        appacitiveObject.setAttribute("a3", "v3");
        appacitiveObject.setAttribute("a1", "v1");

        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                assert result.getAllAttributes().containsKey("a1");
                assert result.getAllAttributes().containsKey("a2");
                assert result.getAllAttributes().containsKey("a3");
                assert result.getAttribute("a1").equals("v1");

                result.removeAttribute("a1");
                result.removeAttribute("a2");
                result.setAttribute("a3", "vv3");

                result.updateInBackground(false, new Callback<AppacitiveObject>() {
                    @Override
                    public void success(AppacitiveObject result) {
                        Map<String, String> attributes = result.getAllAttributes();
                        assert attributes.containsKey("a1") == false;
                        assert attributes.containsKey("a2") == false;
                        assert attributes.containsKey("a3");
                        assert attributes.get("a3").equals("vv3");

                        result.removeAttribute("non_existent_attr");
                        result.updateInBackground(false, new Callback<AppacitiveObject>() {
                            @Override
                            public void failure(AppacitiveObject result, Exception e) {
                                assert false;
                            }

                            @Override
                            public void success(AppacitiveObject result) {
                                assert true;
                                somethingHappened.set(true);
                            }
                        });
                    }

                    @Override
                    public void failure(AppacitiveObject result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });

            }
        });
        await().untilTrue(somethingHappened);
    }

    @Test
    public void updateTagsTest() throws ValidationException {
        AppacitiveObject appacitiveObject = new AppacitiveObject("object");
        appacitiveObject.addTag("t1");
        appacitiveObject.addTag("t2");
        appacitiveObject.addTag("t3");
        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                List<String> tags = result.getAllTags();
                assert tags.size() == 3;
                assert tags.contains("t1");
                assert tags.contains("t2");
                assert tags.contains("t3");

                result.addTag("t4");
                result.addTag("t5");

                result.removeTag("t3");
                result.removeTag("t6");
                result.updateInBackground(false, new Callback<AppacitiveObject>() {
                    @Override
                    public void success(AppacitiveObject result) {
                        List<String> tags = result.getAllTags();
                        assert tags.size() == 4;
                        assert result.tagExists("t3") == false;
                        assert result.tagExists("t6") == false;
                        somethingHappened.set(true);
                    }

                    @Override
                    public void failure(AppacitiveObject result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().untilTrue(somethingHappened);
    }

    @Test
    public void multiGetObjectsTest() throws IOException, ValidationException, InterruptedException {
        final AtomicInteger createdObjectsCount = new AtomicInteger(0);
        final ArrayList<Long> ids = new ArrayList<Long>();
        for (int i = 0; i < 3; i++) {
            AppacitiveObject appacitiveObject = new AppacitiveObject("object");
            appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
                @Override
                public void success(AppacitiveObject result) {
                    ids.add(result.getId());
                    createdObjectsCount.incrementAndGet();
                }
            });
        }
        await().untilAtomic(createdObjectsCount, equalTo(3));

        AppacitiveObject.multiGetInBackground("object", ids, null, new Callback<List<AppacitiveObject>>() {
            @Override
            public void success(List<AppacitiveObject> result) {
                assert result.size() == 3;
                somethingHappened.set(true);
            }

            @Override
            public void failure(List<AppacitiveObject> result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().untilTrue(somethingHappened);
    }

    @Test
    public void fieldsTest() throws ValidationException {
        AppacitiveObject appacitiveObject = getRandomObject();
        appacitiveObject.setAttribute("aa", "vv");
        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                List<String> fields = new ArrayList<String>();
                fields.add("intfield");
                fields.add("geofield");
                fields.add(SystemDefinedPropertiesHelper.attributes);
                fields.add(SystemDefinedPropertiesHelper.lastModifiedBy);
                fields.add(SystemDefinedPropertiesHelper.utcDateCreated);
                AppacitiveObject.getInBackground("object", result.getId(), fields, new Callback<AppacitiveObject>() {
                    @Override
                    public void success(AppacitiveObject result) {
                        assert result.getPropertyAsString("intfield") != null;
                        assert result.getPropertyAsString("geofield") != null;

                        assert result.getPropertyAsString("stringfield") == null;
                        assert result.getPropertyAsString("textfield") == null;

                        assert result.getAllTags().size() == 0;
                        assert result.getAllAttributes().size() != 0;
                        somethingHappened.set(true);
                    }

                    @Override
                    public void failure(AppacitiveObject result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().untilTrue(somethingHappened);
    }

    @Test
    public void deleteObjectTest() throws ValidationException {
        AppacitiveObject object = new AppacitiveObject("object");
        object.createInBackground(new Callback<AppacitiveObject>() {
            public void success(AppacitiveObject result) {
                final long id = result.getId();
                result.deleteInBackground(false, new Callback<Void>() {
                    @Override
                    public void success(Void result) {
                        AppacitiveObject.getInBackground("object", id, null, new Callback<AppacitiveObject>() {
                            @Override
                            public void success(AppacitiveObject result) {
                                assert false;
                            }

                            @Override
                            public void failure(AppacitiveObject result, Exception e) {
                                AppacitiveException ae = (AppacitiveException) e;
                                assert ae.getCode().equals(ErrorCodes.NOT_FOUND);
                                somethingHappened.set(true);
                            }
                        });
                    }

                    @Override
                    public void failure(Void result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().atMost(Duration.TEN_SECONDS).untilTrue(somethingHappened);
    }

    @Test
    public void multiDeleteObjectTest() throws ValidationException, InterruptedException {
        final AtomicInteger createCount = new AtomicInteger(0);
        final List<Long> ids = new ArrayList<Long>();
        for (int i = 0; i < 3; i++) {
            AppacitiveObject appacitiveObject = new AppacitiveObject("object");
            appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
                @Override
                public void success(AppacitiveObject result) {
                    ids.add(result.getId());
                    createCount.incrementAndGet();
                }

                @Override
                public void failure(AppacitiveObject result, Exception e) {
                    Assert.fail(e.getMessage());
                }
            });
        }
        await().atMost(Duration.TEN_SECONDS).untilAtomic(createCount, equalTo(3));

        final AtomicInteger deleteCount = new AtomicInteger(3);
        AppacitiveObject.bulkDeleteInBackground("object", ids, new Callback<Void>() {
            @Override
            public void success(Void result) {
                for (long id : ids) {
                    AppacitiveObject.getInBackground("object", id, null, new Callback<AppacitiveObject>() {
                        @Override
                        public void success(AppacitiveObject result) {
                            assert false;
                        }

                        @Override
                        public void failure(AppacitiveObject result, Exception e) {
                            assert true;
                            deleteCount.decrementAndGet();
                        }
                    });
                }
            }

            @Override
            public void failure(Void result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().atMost(Duration.TEN_SECONDS).untilAtomic(deleteCount, equalTo(0));
    }

    @Test
    public void findObjectsTest() throws ValidationException {
        AppacitiveObject appacitiveObject = new AppacitiveObject("object");
        final AppacitiveQuery query = new AppacitiveQuery();
        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                AppacitiveObject.findInBackground("object", query, null, new Callback<PagedList<AppacitiveObject>>() {
                    @Override
                    public void success(PagedList<AppacitiveObject> result) {
                        assert result.results.size() > 0;
                        somethingHappened.set(true);
                    }

                    @Override
                    public void failure(PagedList<AppacitiveObject> result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().atMost(Duration.TEN_SECONDS).untilTrue(somethingHappened);
    }

    @Test
    public void findObjectsTestWithPagination() throws ValidationException {
        AppacitiveObject appacitiveObject = new AppacitiveObject("object");
        final AppacitiveQuery query = new AppacitiveQuery();
        query.pageNumber = 2;
        query.pageSize = 15;
        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                AppacitiveObject.findInBackground("object", query, null, new Callback<PagedList<AppacitiveObject>>() {
                    @Override
                    public void success(PagedList<AppacitiveObject> result) {
                        assert result.pagingInfo.pageSize == 15;
                        assert result.pagingInfo.pageNumber == 2;
                        somethingHappened.set(true);
                    }

                    @Override
                    public void failure(PagedList<AppacitiveObject> result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }
        });
        await().atMost(Duration.TEN_SECONDS).untilTrue(somethingHappened);
    }

    @Test
    public void findObjectsWithPropertyFilterTest() throws ValidationException {
        AppacitiveObject appacitiveObject = new AppacitiveObject("object");
        appacitiveObject.setStringProperty("stringfield", "hello world123");
        appacitiveObject.setIntProperty("intfield", 2005);
        appacitiveObject.setBoolProperty("boolfield", false);
        appacitiveObject.setStringProperty("geofield", "11.11, 22.22");
        final Date date = new Date();
        final String nowAsISODate = convertDateToString(date);
        final String nowAsISOTime = convertTimeToString(date);
        final String nowAsISODateTime = convertDateTimeToString(date);
        appacitiveObject.setStringProperty("datefield", nowAsISODate);
        appacitiveObject.setStringProperty("timefield", nowAsISOTime);
        appacitiveObject.setStringProperty("datetimefield", nowAsISODateTime);

        final AppacitiveQuery query = new AppacitiveQuery();
        final Query q1 = new PropertyFilter("stringfield").isEqualTo("hello world123");
        final Query q2 = new PropertyFilter("intfield").isEqualTo(2005);
        final Query q3 = new PropertyFilter("boolfield").isEqualTo(false);
        final Query q4 = new PropertyFilter("datefield").isEqualTo(nowAsISODate);
        final Query q5 = new PropertyFilter("timefield").isEqualTo(nowAsISOTime);
        final Query q6 = new PropertyFilter("datetimefield").isEqualTo(nowAsISODateTime);
        double[] geo = new double[2];
        geo[0] = 11.11d;
        geo[1] = 22.23d;
        final Query q7 = new GeoFilter("geofield").withinCircle(geo, 10, DistanceMetric.mi);

        query.filter = BooleanOperator.and(new ArrayList<Query>() {{
            add(q1);
            add(q2);
            add(q3);
            add(q4);
            add(q5);
            add(q6);
            add(q7);
        }});
        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                AppacitiveObject.findInBackground("object", query, null, new Callback<PagedList<AppacitiveObject>>() {
                    @Override
                    public void success(PagedList<AppacitiveObject> result) {
                        assert result.results.size() > 0;
                        somethingHappened.set(true);
                    }

                    @Override
                    public void failure(PagedList<AppacitiveObject> result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().atMost(Duration.TEN_SECONDS).untilTrue(somethingHappened);
    }

    @Test
    public void findObjectsWithAttributeFilterTest() throws ValidationException {
        AppacitiveObject appacitiveObject = new AppacitiveObject("object");
        appacitiveObject.setAttribute("a1", "v1");
        appacitiveObject.setAttribute("a2", "v2");
        appacitiveObject.setAttribute("a3", "v3");
        appacitiveObject.setAttribute("a4", "appacitive");
        final AppacitiveQuery query = new AppacitiveQuery();
        final AttributeFilter a1 = new AttributeFilter("a1").isEqualTo("v1");
        final AttributeFilter a2 = new AttributeFilter("a2").endsWith("2");
        final AttributeFilter a3 = new AttributeFilter("a3").startsWith("v");
        final AttributeFilter a4 = new AttributeFilter("a4").like("acit");
        query.filter = BooleanOperator.and(new ArrayList<Query>() {{
            add(a1);
            add(a2);
            add(a3);
            add(a4);
        }});
        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                AppacitiveObject.findInBackground("object", query, null, new Callback<PagedList<AppacitiveObject>>() {
                    @Override
                    public void success(PagedList<AppacitiveObject> result) {
                        Assert.assertTrue(result.results.size() > 0);
                        somethingHappened.set(true);
                    }

                    @Override
                    public void failure(PagedList<AppacitiveObject> result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().atMost(Duration.TEN_SECONDS).untilTrue(somethingHappened);
    }

    @Test
    public void findObjectsWithTagsFilterTest() throws ValidationException {
        AppacitiveObject appacitiveObject = new AppacitiveObject("object");
        List<String> tags = new ArrayList<String>() {{
            add("tag1");
            add("tag2");
            add("tag3");
            add("tag4");
            add("tag5");
        }};
        appacitiveObject.addTags(tags);
        final AppacitiveQuery query = new AppacitiveQuery();
        final TagFilter t1 = new TagFilter().matchAll(new ArrayList<String>() {{
            add("tag1");
            add("tag2");
        }});

        final TagFilter t2 = new TagFilter().matchOneOrMore(new ArrayList<String>() {{
            add("tag4");
            add("tag6");
            add("tag7");
        }});
        query.filter = BooleanOperator.and(new ArrayList<Query>() {{
            add(t1);
            add(t2);
        }});

        appacitiveObject.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                AppacitiveObject.findInBackground("object", query, null, new Callback<PagedList<AppacitiveObject>>() {
                    @Override
                    public void success(PagedList<AppacitiveObject> result) {
                        assert result.results.size() > 0;
                        for (AppacitiveObject obj : result.results) {
                            assert obj.tagExists("tag1") == true;
                            assert obj.tagExists("tag2") == true;
                            assert obj.tagExists("tag4") == true || obj.tagExists("tag6") == true || obj.tagExists("tag7") == true;
                        }
                        somethingHappened.set(true);
                    }

                    @Override
                    public void failure(PagedList<AppacitiveObject> result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().atMost(Duration.TEN_SECONDS).untilTrue(somethingHappened);
    }

    @Test
    public void getConnectedObjectsTest() throws ValidationException {
        AppacitiveUser user = new AppacitiveUser();
        user.setFirstName(getRandomString());
        user.setUsername(getRandomString());
        user.setPassword(getRandomString());
        user.setEmail(getRandomString().concat("@gmail.com"));

        AppacitiveDevice device = new AppacitiveDevice();
        device.setDeviceType("ios");
        device.setDeviceToken(getRandomString());

        new AppacitiveConnection("my_device").fromNewDevice("device", device).toNewUser("user", user).createInBackground(new Callback<AppacitiveConnection>() {
            @Override
            public void success(AppacitiveConnection result) {
                AppacitiveObject.getConnectedObjectsInBackground("my_device", "user", result.endpointB.objectId, null, null, new Callback<ConnectedObjectsResponse>() {
                    @Override
                    public void success(ConnectedObjectsResponse result) {
                        assert result.results.size() > 0;
                        assert result.results.get(0).object != null;
                        assert result.results.get(0).connection != null;
                        assert result.results.get(0).object.getType().equals("device");
                        somethingHappened.set(true);
                    }

                    @Override
                    public void failure(ConnectedObjectsResponse result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }
        });
        await().atMost(Duration.TEN_MINUTES).untilTrue(somethingHappened);

    }

    @Test
    public void setMultiValuedItemsTest() throws ValidationException {
        AppacitiveObject object = new AppacitiveObject("object");
        List<String> stringItems = new ArrayList<String>() {{
            add("a");
            add(null);
            add("b");
            add(null);
        }};
        List<Integer> integerItems = new ArrayList<Integer>() {{
            add(100);
            add(null);
            add(200);
            add(null);
        }};
        List<Double> decimalItems = new ArrayList<Double>() {{
            add(11.12345);
            add(null);
            add(22.09876);
            add(null);
        }};
        object.setPropertyAsMultiValued("multi_string", stringItems);
        object.setPropertyAsMultiValued("multi_integer", integerItems);
        object.setPropertyAsMultiValued("multi_decimal", decimalItems);

        assert object.getPropertyAsMultiValuedDouble("multi_decimal").size() == 4;
        assert object.getPropertyAsMultiValuedInt("multi_integer").size() == 4;
        assert object.getPropertyAsMultiValuedString("multi_string").size() == 4;

        object.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                assert result.getId() > 0;
                List<String> returnStringItems = result.getPropertyAsMultiValuedString("multi_string");
                List<Integer> returnIntegerItems = result.getPropertyAsMultiValuedInt("multi_integer");
                List<Double> returnDoubleItems = result.getPropertyAsMultiValuedDouble("multi_decimal");
                assert returnStringItems.size() == 2;
                assert returnDoubleItems.size() == 2;
                assert returnIntegerItems.size() == 2;
                somethingHappened.set(true);
            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().atMost(Duration.TEN_SECONDS).untilTrue(somethingHappened);

    }

    @Test
    public void addMultiValuedItemsTest() throws ValidationException {
        AppacitiveObject object = new AppacitiveObject("object");
        final List<String> stringItemsWithNulls = new ArrayList<String>() {{
            add("a");
            add(null);
            add("b");
            add(null);
        }};
        final List<Integer> integerItemsWithNulls = new ArrayList<Integer>() {{
            add(100);
            add(null);
            add(200);
            add(null);
        }};
        final List<Double> decimalItemsWithNulls = new ArrayList<Double>() {{
            add(11.12345);
            add(null);
            add(22.09876);
            add(null);
        }};

        object.setPropertyAsMultiValued("multi_string", stringItemsWithNulls);
        object.setPropertyAsMultiValued("multi_integer", integerItemsWithNulls);
        object.setPropertyAsMultiValued("multi_decimal", decimalItemsWithNulls);

        object.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                assert result.getId() > 0;
                result.addItemsToMultiValuedProperty("multi_string", stringItemsWithNulls);
                result.addItemsToMultiValuedProperty("multi_integer", integerItemsWithNulls);
                result.addItemsToMultiValuedProperty("multi_decimal", decimalItemsWithNulls);

                assert result.getPropertyAsMultiValuedDouble("multi_decimal").size() == 6;
                assert result.getPropertyAsMultiValuedInt("multi_integer").size() == 6;
                assert result.getPropertyAsMultiValuedString("multi_string").size() == 6;

                result.updateInBackground(false, new Callback<AppacitiveObject>() {
                    @Override
                    public void success(AppacitiveObject result1) {
                        assert result1.getPropertyAsMultiValuedDouble("multi_decimal").size() == 4;
                        assert result1.getPropertyAsMultiValuedInt("multi_integer").size() == 4;
                        assert result1.getPropertyAsMultiValuedString("multi_string").size() == 4;
                        somethingHappened.set(true);
                    }

                    @Override
                    public void failure(AppacitiveObject result, Exception e) {
                        Assert.fail(e.getMessage());
                    }
                });
            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                Assert.fail(e.getMessage());
            }
        });
        await().atMost(Duration.TEN_SECONDS).untilTrue(somethingHappened);
    }

}

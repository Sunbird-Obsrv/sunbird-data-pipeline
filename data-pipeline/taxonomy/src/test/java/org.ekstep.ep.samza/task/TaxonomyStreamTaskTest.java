package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.api.TaxonomyApi;
import org.ekstep.ep.samza.system.Event;
import org.ekstep.ep.samza.system.Taxonomy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TaxonomyStreamTaskTest {
    private final String SUCCESS_TOPIC = "unique_events";
    private final String FAILED_TOPIC = "failed_taxonomy_events";
    KeyValueStore taxonomyStore;
    private MessageCollector collector;
    private Config configMock;
    private TaskContext contextMock;
    private IncomingMessageEnvelope envelope;
    private TaskCoordinator coordinator;

    @Before
    public void setMock() {
        taxonomyStore = Mockito.mock(KeyValueStore.class);
        collector = Mockito.mock(MessageCollector.class);
        configMock = Mockito.mock(Config.class);
        contextMock = Mockito.mock(TaskContext.class);
        envelope = Mockito.mock(IncomingMessageEnvelope.class);
        coordinator = Mockito.mock(TaskCoordinator.class);

        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(contextMock.getStore("taxonomy")).toReturn(taxonomyStore);

    }

    @Test
    public void ShouldCreateTaxonomyMapIfTaxonomyStoreIsNullAndShouldAddToEvent() throws Exception{
        Event event = Mockito.mock(Event.class);
        Map<String, Object> map = createMap();

        stub(event.getMap()).toReturn(map);
        stub(event.getCid()).toReturn("LT1");

        TaxonomyApi taxonomyApi = Mockito.mock(TaxonomyApi.class);
        Map<String, Object> library = (Map<String,Object>) createTaxonomyLibrary();
        stub(taxonomyApi.getTaxonomyLibrary()).toReturn(library);

        stub(taxonomyStore.get(event.getCid())).toReturn(null);

        Map<String, Object> taxonomyData = (Map<String,Object>) getTaxonomyData();
        Taxonomy taxonomy = Mockito.mock(Taxonomy.class);

        stub(taxonomy.getTaxonomyData("LT1")).toReturn((Map<String,Object>) taxonomyData);

        TaxonomyStreamTask taxonomyStreamTask = new TaxonomyStreamTask(taxonomyStore);

        taxonomyStreamTask.init(configMock, contextMock);
        Map<String,Object> taxonomyStoreData = (Map<String,Object>) getTaxonomyStoreData();
        stub(taxonomyStore.get(event.getCid())).toReturn(taxonomyStoreData);
        taxonomyStreamTask.processEvent(event, collector);

        ArgumentCaptor<OutgoingMessageEnvelope> outgoingMessageEnvelope = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        verify(collector,times(1)).send(outgoingMessageEnvelope.capture());
        verify(event,times(1)).addTaxonomyData(Matchers.<Map<String, Object>>any());

        SystemStream systemStream = outgoingMessageEnvelope.getValue().getSystemStream();
        assertEquals("unique_events", systemStream.getStream());
    }

    @Test
    public void ShouldCreateTaxonomyMapIfTaxonomyStoreHasValueAndShouldAddToEvent() throws Exception{


        Event event = Mockito.mock(Event.class);
        Map<String, Object> map = createMap();


        stub(event.getMap()).toReturn(map);
        stub(event.getCid()).toReturn("LT1");

        stub(envelope.getMessage()).toReturn(map);


        Map<String,Object> taxonomyStoreData = (Map<String,Object>) getTaxonomyStoreData();
        stub(taxonomyStore.get(event.getCid())).toReturn(taxonomyStoreData);

        Map<String, Object> taxonomyData = (Map<String,Object>) getTaxonomyData();
        Taxonomy taxonomy = Mockito.mock(Taxonomy.class);

        stub(taxonomy.getTaxonomyData("LT1")).toReturn((Map<String,Object>) taxonomyData);

        TaxonomyStreamTask taxonomyStreamTask = new TaxonomyStreamTask(taxonomyStore);

        taxonomyStreamTask.init(configMock, contextMock);
        taxonomyStreamTask.processEvent(event, collector);

        ArgumentCaptor<OutgoingMessageEnvelope> outgoingMessageEnvelope = ArgumentCaptor.forClass(OutgoingMessageEnvelope.class);

        verify(collector,times(1)).send(outgoingMessageEnvelope.capture());

        verify(event,times(1)).addTaxonomyData(Matchers.<Map<String, Object>>any());

        SystemStream systemStream = outgoingMessageEnvelope.getValue().getSystemStream();
        assertEquals("unique_events", systemStream.getStream());
    }


    @Test
    public void ShouldCheckNormalFlow() throws Exception{
        Map<String, Object> event = createMap();
        stub(envelope.getMessage()).toReturn(event);

        TaxonomyStreamTask taxonomyStreamTask = new TaxonomyStreamTask(taxonomyStore);
        taxonomyStreamTask.init(configMock, contextMock);

        taxonomyStreamTask.process(envelope, collector, coordinator);
        verify(collector, times(1)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldIgnoreInvalidMessages(){

        IncomingMessageEnvelope envelope = mock(IncomingMessageEnvelope.class);
        when(envelope.getMessage()).thenReturn(" ");

        TaxonomyStreamTask taxonomyStreamTask = new TaxonomyStreamTask(taxonomyStore);

        taxonomyStreamTask.init(configMock, contextMock);
        taxonomyStreamTask.process(envelope, collector, coordinator);

        verify(taxonomyStore, times(0)).get(anyString());
        verify(collector, times(0)).send(any(OutgoingMessageEnvelope.class));

    }

    private Event createEvent() {

        Event event = mock(Event.class);
        Map<String, Object> map = createMap();
        when(event.getMap()).thenReturn(map);
        return event;
    }

    private Map<String, Object> createTaxonomyLibrary(){
        Map<String, Object> jsonObject = new Gson().fromJson("{\n" +
                "    \"id\": \"ekstep.lp.taxonomy.hierarchy\",\n" +
                "    \"ver\": \"1.0\",\n" +
                "    \"ts\": \"2015-07-29T12:07:36Z+05:30\",\n" +
                "    \"params\": {\n" +
                "        \"resmsgid\": \"c48c4983-0c2c-4e06-9a9a-78b570c024dc\",\n" +
                "        \"msgid\": null,\n" +
                "        \"err\": null,\n" +
                "        \"status\": \"successful\",\n" +
                "        \"errmsg\": null\n" +
                "    },\n" +
                "    \"result\": {\n" +
                "        \"taxonomy_hierarchy\": {\n" +
                "            \"identifier\": \"literacy_v2\",\n" +
                "            \"objectType\": \"Taxonomy\",\n" +
                "            \"type\": \"li\",\n" +
                "            \"metadata\": {\n" +
                "                \"description\": \"Literacy\",\n" +
                "                \"name\": \"Literacy V2\",\n" +
                "                \"code\": \"LITERACY\"\n" +
                "            },\n" +
                "            \"children\": [\n" +
                "                {\n" +
                "                    \"identifier\": \"LD5\",\n" +
                "                    \"objectType\": \"Concept\",\n" +
                "                    \"type\": \"LD\",\n" +
                "                    \"metadata\": {\n" +
                "                        \"description\": \"Reading Comprehension\",\n" +
                "                        \"name\": \"Reading Comprehension\",\n" +
                "                        \"code\": \"LD5\"\n" +
                "                    },\n" +
                "                    \"children\": [\n" +
                "                        {\n" +
                "                            \"identifier\": \"LO9\",\n" +
                "                            \"objectType\": \"Concept\",\n" +
                "                            \"type\": \"LO\",\n" +
                "                            \"metadata\": {\n" +
                "                                \"description\": \"Sentence Comprehension\",\n" +
                "                                \"name\": \"Sentence Comprehension\",\n" +
                "                                \"code\": \"LO9\"\n" +
                "                            },\n" +
                "                            \"children\": [\n" +
                "                                {\n" +
                "                                    \"identifier\": \"LT11\",\n" +
                "                                    \"objectType\": \"Concept\",\n" +
                "                                    \"type\": \"LT\",\n" +
                "                                    \"metadata\": {\n" +
                "                                        \"description\": \"Read and choose Picture (read the sentence and related questions and choose a picture answer)\",\n" +
                "                                        \"name\": \"Read and choose Picture\",\n" +
                "                                        \"code\": \"LT11\"\n" +
                "                                    },\n" +
                "                                    \"children\": null\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"identifier\": \"LT12\",\n" +
                "                                    \"objectType\": \"Concept\",\n" +
                "                                    \"type\": \"LT\",\n" +
                "                                    \"metadata\": {\n" +
                "                                        \"description\": \"Passage Reading (fill in the blanks in sentences)\",\n" +
                "                                        \"name\": \"Passage Reading (fill in the blanks)\",\n" +
                "                                        \"code\": \"LT12\"\n" +
                "                                    },\n" +
                "                                    \"children\": null\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"identifier\": \"LT10\",\n" +
                "                                    \"objectType\": \"Concept\",\n" +
                "                                    \"type\": \"LT\",\n" +
                "                                    \"metadata\": {\n" +
                "                                        \"description\": \"Read and choose word (read the sentence and related questions and choose a word answer)\",\n" +
                "                                        \"name\": \"Read and choose word\",\n" +
                "                                        \"code\": \"LT10\"\n" +
                "                                    },\n" +
                "                                    \"children\": null\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"identifier\": \"LD4\",\n" +
                "                    \"objectType\": \"Concept\",\n" +
                "                    \"type\": \"LD\",\n" +
                "                    \"metadata\": {\n" +
                "                        \"description\": \" Decoding & Fluency\",\n" +
                "                        \"name\": \"Decoding & Fluency\",\n" +
                "                        \"code\": \"LD4\"\n" +
                "                    },\n" +
                "                    \"children\": [\n" +
                "                        {\n" +
                "                            \"identifier\": \"LO8\",\n" +
                "                            \"objectType\": \"Concept\",\n" +
                "                            \"type\": \"LO\",\n" +
                "                            \"metadata\": {\n" +
                "                                \"description\": \"Decoding for Spelling\",\n" +
                "                                \"name\": \"Decoding for Spelling\",\n" +
                "                                \"code\": \"LO8\"\n" +
                "                            },\n" +
                "                            \"children\": [\n" +
                "                                {\n" +
                "                                    \"identifier\": \"LT9\",\n" +
                "                                    \"objectType\": \"Concept\",\n" +
                "                                    \"type\": \"LT\",\n" +
                "                                    \"metadata\": {\n" +
                "                                        \"description\": \"Word completion (fill in missing akshara from four options to make a meaningful word)\",\n" +
                "                                        \"name\": \"Word completion\",\n" +
                "                                        \"code\": \"LT9\"\n" +
                "                                    },\n" +
                "                                    \"children\": null\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"identifier\": \"LO7\",\n" +
                "                            \"objectType\": \"Concept\",\n" +
                "                            \"type\": \"LO\",\n" +
                "                            \"metadata\": {\n" +
                "                                \"description\": \"Decoding for Reading\",\n" +
                "                                \"name\": \"Decoding for Reading\",\n" +
                "                                \"code\": \"LO7\"\n" +
                "                            },\n" +
                "                            \"children\": [\n" +
                "                                {\n" +
                "                                    \"identifier\": \"LT7\",\n" +
                "                                    \"objectType\": \"Concept\",\n" +
                "                                    \"type\": \"LT\",\n" +
                "                                    \"metadata\": {\n" +
                "                                        \"description\": \"Word picture matching (Choose the right word to match the picture shown)\",\n" +
                "                                        \"name\": \"Word picture matching\",\n" +
                "                                        \"code\": \"LT7\"\n" +
                "                                    },\n" +
                "                                    \"children\": null\n" +
                "                                },\n" +
                "                                {\n" +
                "                                    \"identifier\": \"LT8\",\n" +
                "                                    \"objectType\": \"Concept\",\n" +
                "                                    \"type\": \"LT\",\n" +
                "                                    \"metadata\": {\n" +
                "                                        \"description\": \"Pick the correct word (find answer to a question about a just-heard sentence from four word options)\",\n" +
                "                                        \"name\": \"Pick the correct word\",\n" +
                "                                        \"code\": \"LT8\"\n" +
                "                                    },\n" +
                "                                    \"children\": null\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"identifier\": \"LO6\",\n" +
                "                            \"objectType\": \"Concept\",\n" +
                "                            \"type\": \"LO\",\n" +
                "                            \"metadata\": {\n" +
                "                                \"description\": \"Decoding for Spelling\",\n" +
                "                                \"name\": \"Decoding for Spelling\",\n" +
                "                                \"code\": \"LO6\"\n" +
                "                            },\n" +
                "                            \"children\": [\n" +
                "                                {\n" +
                "                                    \"identifier\": \"LT6\",\n" +
                "                                    \"objectType\": \"Concept\",\n" +
                "                                    \"type\": \"LT\",\n" +
                "                                    \"metadata\": {\n" +
                "                                        \"description\": \"Teacher Teacher! (decide if spelling is right or wrong after listening to word)\",\n" +
                "                                        \"name\": \"Teacher Teacher!\",\n" +
                "                                        \"code\": \"LT6\"\n" +
                "                                    },\n" +
                "                                    \"children\": null\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"identifier\": \"LD3\",\n" +
                "                    \"objectType\": \"Concept\",\n" +
                "                    \"type\": \"LD\",\n" +
                "                    \"metadata\": {\n" +
                "                        \"description\": \"Akshara Knowledge\",\n" +
                "                        \"name\": \"Akshara Knowledge\",\n" +
                "                        \"code\": \"LD3\"\n" +
                "                    },\n" +
                "                    \"children\": [\n" +
                "                        {\n" +
                "                            \"identifier\": \"LO5\",\n" +
                "                            \"objectType\": \"Concept\",\n" +
                "                            \"type\": \"LO\",\n" +
                "                            \"metadata\": {\n" +
                "                                \"description\": \"Sound-to-symbol Mapping\",\n" +
                "                                \"name\": \"Sound-to-symbol Mapping\",\n" +
                "                                \"code\": \"LO5\"\n" +
                "                            },\n" +
                "                            \"children\": [\n" +
                "                                {\n" +
                "                                    \"identifier\": \"LT5\",\n" +
                "                                    \"objectType\": \"Concept\",\n" +
                "                                    \"type\": \"LT\",\n" +
                "                                    \"metadata\": {\n" +
                "                                        \"description\": \"Akshara Sound (find akshara from four options to match just heard akshara)\",\n" +
                "                                        \"name\": \"Akshara Sound\",\n" +
                "                                        \"code\": \"LT5\"\n" +
                "                                    },\n" +
                "                                    \"children\": null\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"identifier\": \"LD2\",\n" +
                "                    \"objectType\": \"Concept\",\n" +
                "                    \"type\": \"LD\",\n" +
                "                    \"metadata\": {\n" +
                "                        \"description\": \"Listening Comprehension\",\n" +
                "                        \"name\": \"Listening Comprehension\",\n" +
                "                        \"code\": \"LD2\"\n" +
                "                    },\n" +
                "                    \"children\": [\n" +
                "                        {\n" +
                "                            \"identifier\": \"LO4\",\n" +
                "                            \"objectType\": \"Concept\",\n" +
                "                            \"type\": \"LO\",\n" +
                "                            \"metadata\": {\n" +
                "                                \"description\": \"Grammaticality Judgement/Syntax\",\n" +
                "                                \"name\": \"Grammaticality Judgement/Syntax\",\n" +
                "                                \"code\": \"LO4\"\n" +
                "                            },\n" +
                "                            \"children\": [\n" +
                "                                {\n" +
                "                                    \"identifier\": \"LT4\",\n" +
                "                                    \"objectType\": \"Concept\",\n" +
                "                                    \"type\": \"LT\",\n" +
                "                                    \"metadata\": {\n" +
                "                                        \"description\": \"Is this right? (judge whether just-heard sentence is grammatically correct)\",\n" +
                "                                        \"name\": \"Is this right?\",\n" +
                "                                        \"code\": \"LT4\"\n" +
                "                                    },\n" +
                "                                    \"children\": null\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"identifier\": \"LO3\",\n" +
                "                            \"objectType\": \"Concept\",\n" +
                "                            \"type\": \"LO\",\n" +
                "                            \"metadata\": {\n" +
                "                                \"description\": \"Sentence Comprehension\",\n" +
                "                                \"name\": \"Sentence Comprehension\",\n" +
                "                                \"code\": \"LO3\"\n" +
                "                            },\n" +
                "                            \"children\": [\n" +
                "                                {\n" +
                "                                    \"identifier\": \"LT3\",\n" +
                "                                    \"objectType\": \"Concept\",\n" +
                "                                    \"type\": \"LT\",\n" +
                "                                    \"metadata\": {\n" +
                "                                        \"description\": \"Listen and choose picture (find picture from four options to match just heard sentence)\",\n" +
                "                                        \"name\": \"Listen and choose picture\",\n" +
                "                                        \"code\": \"LT3\"\n" +
                "                                    },\n" +
                "                                    \"children\": null\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"identifier\": \"LD1\",\n" +
                "                    \"objectType\": \"Concept\",\n" +
                "                    \"type\": \"LD\",\n" +
                "                    \"metadata\": {\n" +
                "                        \"description\": \"Vocabulary\",\n" +
                "                        \"name\": \"Vocabulary\",\n" +
                "                        \"code\": \"LD1\"\n" +
                "                    },\n" +
                "                    \"children\": [\n" +
                "                        {\n" +
                "                            \"identifier\": \"LO2\",\n" +
                "                            \"objectType\": \"Concept\",\n" +
                "                            \"type\": \"LO\",\n" +
                "                            \"metadata\": {\n" +
                "                                \"description\": \"Lexical Judgement\",\n" +
                "                                \"name\": \"Lexical Judgement\",\n" +
                "                                \"code\": \"LO2\"\n" +
                "                            },\n" +
                "                            \"children\": [\n" +
                "                                {\n" +
                "                                    \"identifier\": \"LT2\",\n" +
                "                                    \"objectType\": \"Concept\",\n" +
                "                                    \"type\": \"LT\",\n" +
                "                                    \"metadata\": {\n" +
                "                                        \"description\": \"Pick the correct Picture (choose spoken word to match picture with a spoken nonword as disractor)\",\n" +
                "                                        \"name\": \"Pick the correct Picture\",\n" +
                "                                        \"code\": \"LT2\"\n" +
                "                                    },\n" +
                "                                    \"children\": null\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"identifier\": \"LO1\",\n" +
                "                            \"objectType\": \"Concept\",\n" +
                "                            \"type\": \"LO\",\n" +
                "                            \"metadata\": {\n" +
                "                                \"description\": \"Receptive Vocabulary\",\n" +
                "                                \"name\": \"Receptive Vocabulary\",\n" +
                "                                \"code\": \"LO1\"\n" +
                "                            },\n" +
                "                            \"children\": [\n" +
                "                                {\n" +
                "                                    \"identifier\": \"LT1\",\n" +
                "                                    \"objectType\": \"Concept\",\n" +
                "                                    \"type\": \"LT\",\n" +
                "                                    \"metadata\": {\n" +
                "                                        \"description\": \"Chili Pili (find picture from four options to match just heard word)\",\n" +
                "                                        \"name\": \"Chili Pili\",\n" +
                "                                        \"code\": \"LT1\"\n" +
                "                                    },\n" +
                "                                    \"children\": null\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}\n",Map.class);
        return jsonObject;
    }

    private Map<String, Object> createMap(){
        Map<String, Object> jsonObject = new Gson().fromJson("{\n" +
                "     \"eid\": \"ME_USER_GAME_LEVEL\",\n" +
                "     \"ts\": \"2015-07-24T12:07:35+05:30\",\n" +
                "     \"ver\": \"1.0\",\n" +
                "     \"uid\": \"3ee97c77-3587-4394-aff6-32b12576fcf6\",\n" +
                "     \"gdata\": { \n" +
                "          \"id\":\"org.eks.lit_screener\",\n" +
                "          \"ver\":\"1.0\"\n" +
                "     },\n" +
                "     \"cid\": \"LT1\",\n" +
                "     \"ctype\": \"LT\",\n" +
                "     \"pdata\": {\n" +
                "          \"id\":\"ASSESMENT_PIPELINE\",\n" +
                "          \"mod\":\"LiteraryScreenerAssessment\",\n" +
                "          \"ver\":\"1.0\"\n" +
                "     },\n" +
                "     \"edata\": {\n" +
                "          \"eks\": {\n" +
                "              \"current_level\":\"Primary 5\",\n" +
                "              \"score\":\"15\"\n" +
                "          }\n" +
                "     }\n" +
                "}",Map.class);
        return jsonObject;
    }

    private Map<String,Object> getTaxonomyData(){
        Map<String, Object> taxonomyData = new Gson().fromJson("{\"LT1\":{\"id\":\"LT1\", \"name\":\"Read and choose Picture\", \"parent\":\"LO9\"}, \"LO9\": {\"id\":\"LO9\", \"name\":\"Sentence Comprehension\", \"parent\":\"LD5\"}, \"LD5\":{\"id\":\"LD5\", \"name\":\"Reading Comprehension\", \"parent\":null}}",Map.class);
        return taxonomyData;
    }

    private Map<String,Object> getTaxonomyStoreData(){
        Map<String,Object> taxonomyStoreData = new Gson().fromJson("{\n" +
                "    \"LT6\": {\n" +
                "        \"id\": \"LT6\",\n" +
                "        \"name\": \"Teacher Teacher!\",\n" +
                "        \"parent\": \"LO6\"\n" +
                "    },\n" +
                "    \"LT7\": {\n" +
                "        \"id\": \"LT7\",\n" +
                "        \"name\": \"Word picture matching\",\n" +
                "        \"parent\": \"LO7\"\n" +
                "    },\n" +
                "    \"LT4\": {\n" +
                "        \"id\": \"LT4\",\n" +
                "        \"name\": \"Is this right?\",\n" +
                "        \"parent\": \"LO4\"\n" +
                "    },\n" +
                "    \"LT5\": {\n" +
                "        \"id\": \"LT5\",\n" +
                "        \"name\": \"Akshara Sound\",\n" +
                "        \"parent\": \"LO5\"\n" +
                "    },\n" +
                "    \"LT2\": {\n" +
                "        \"id\": \"LT2\",\n" +
                "        \"name\": \"Pick the correct Picture\",\n" +
                "        \"parent\": \"LO2\"\n" +
                "    },\n" +
                "    \"LT3\": {\n" +
                "        \"id\": \"LT3\",\n" +
                "        \"name\": \"Listen and choose picture\",\n" +
                "        \"parent\": \"LO3\"\n" +
                "    },\n" +
                "    \"LT1\": {\n" +
                "        \"id\": \"LT1\",\n" +
                "        \"name\": \"Chili Pili\",\n" +
                "        \"parent\": \"LO1\"\n" +
                "    },\n" +
                "    \"LT9\": {\n" +
                "        \"id\": \"LT9\",\n" +
                "        \"name\": \"Word completion\",\n" +
                "        \"parent\": \"LO8\"\n" +
                "    },\n" +
                "    \"LO2\": {\n" +
                "        \"id\": \"LO2\",\n" +
                "        \"name\": \"Lexical Judgement\",\n" +
                "        \"parent\": \"LD1\"\n" +
                "    },\n" +
                "    \"LO1\": {\n" +
                "        \"id\": \"LO1\",\n" +
                "        \"name\": \"Receptive Vocabulary\",\n" +
                "        \"parent\": \"LD1\"\n" +
                "    },\n" +
                "    \"LO10\": {\n" +
                "        \"id\": \"LO10\",\n" +
                "        \"name\": \"Passage Comprehension\",\n" +
                "        \"parent\": \"LD5\"\n" +
                "    },\n" +
                "    \"LT11\": {\n" +
                "        \"id\": \"LT11\",\n" +
                "        \"name\": \"Read and choose Picture\",\n" +
                "        \"parent\": \"LO9\"\n" +
                "    },\n" +
                "    \"LT13\": {\n" +
                "        \"id\": \"LT13\",\n" +
                "        \"name\": \"Passage Reading (match the words)\",\n" +
                "        \"parent\": \"LO10\"\n" +
                "    },\n" +
                "    \"LD3\": {\n" +
                "        \"id\": \"LD3\",\n" +
                "        \"name\": \"Akshara Knowledge\"\n" +
                "    },\n" +
                "    \"LO7\": {\n" +
                "        \"id\": \"LO7\",\n" +
                "        \"name\": \"Decoding for Reading\",\n" +
                "        \"parent\": \"LD4\"\n" +
                "    },\n" +
                "    \"LD2\": {\n" +
                "        \"id\": \"LD2\",\n" +
                "        \"name\": \"Listening Comprehension\"\n" +
                "    },\n" +
                "    \"LO8\": {\n" +
                "        \"id\": \"LO8\",\n" +
                "        \"name\": \"Decoding for Spelling\",\n" +
                "        \"parent\": \"LD4\"\n" +
                "    },\n" +
                "    \"LD1\": {\n" +
                "        \"id\": \"LD1\",\n" +
                "        \"name\": \"Vocabulary\"\n" +
                "    },\n" +
                "    \"LO9\": {\n" +
                "        \"id\": \"LO9\",\n" +
                "        \"name\": \"Sentence Comprehension\",\n" +
                "        \"parent\": \"LD5\"\n" +
                "    },\n" +
                "    \"LO3\": {\n" +
                "        \"id\": \"LO3\",\n" +
                "        \"name\": \"Sentence Comprehension\",\n" +
                "        \"parent\": \"LD2\"\n" +
                "    },\n" +
                "    \"LO4\": {\n" +
                "        \"id\": \"LO4\",\n" +
                "        \"name\": \"Grammaticality Judgement/Syntax\",\n" +
                "        \"parent\": \"LD2\"\n" +
                "    },\n" +
                "    \"LO5\": {\n" +
                "        \"id\": \"LO5\",\n" +
                "        \"name\": \"Sound-to-symbol Mapping\",\n" +
                "        \"parent\": \"LD3\"\n" +
                "    },\n" +
                "    \"LD5\": {\n" +
                "        \"id\": \"LD5\",\n" +
                "        \"name\": \"Reading Comprehension\"\n" +
                "    },\n" +
                "    \"LO6\": {\n" +
                "        \"id\": \"LO6\",\n" +
                "        \"name\": \"Decoding for Spelling\",\n" +
                "        \"parent\": \"LD4\"\n" +
                "    },\n" +
                "    \"LD4\": {\n" +
                "        \"id\": \"LD4\",\n" +
                "        \"name\": \"Decoding & Fluency\"\n" +
                "    }\n" +
                "}",Map.class);
        return taxonomyStoreData;
    }


}
